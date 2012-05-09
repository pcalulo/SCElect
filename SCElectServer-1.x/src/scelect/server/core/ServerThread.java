/*
 * SCElect Server: Provides services to SCElect clients on the network
 * Copyright (C) 2008-2009 Lawrence Patrick C. Calulo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scelect.server.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * This class handles the server's connections to all clients that are connected
 * to it.
 *
 * @author lugkhast
 */
public class ServerThread extends Thread {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String user;
    private char[] password;
    boolean loginSuccessful;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            String inputLine;
            user = "UNKNOWN";

            // Before anything else, ask for a username and password.
            loginSuccessful = handleLoginData();

            // Only enter the main loop if the login succeeds.
            // Otherwise, go straight to cleanup.
            if (loginSuccessful) {
                while (true) {
                    inputLine = (String) in.readObject();
                    System.out.println(inputLine);

                    if (inputLine.equals("SCELECT_BALLOTDATA_TRANSFER_START")) {
                        handleBallotData();
                        continue;
                    }

                    if (inputLine.equals("SCELECT_LOGOUT")) {
                        break;
                    }

                } // Loop ends here
                ServerCore.onUserDisconnect(user);
            }

            // Clean up
            releaseResources();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    boolean handleLoginData() {
        try {
            user = (String) in.readObject();
            password = (char[]) in.readObject();

            if (ServerCore.isUserConnected(user)) {
                out.writeObject("SCELECT_ALREADY_LOGGED_IN");
                return false;
            }

            boolean alreadyVoted = SCDB.HasVoted(user);
            boolean isValid = SCDB.IsLoginValid(user, password);
            if (isValid && !alreadyVoted) {
                ServerCore.onUserConnect(user);

                // Send required data to the client
                out.writeObject("SCELECT_LOGININFO_ACCEPTED");
                out.writeObject("2009"); // School year
                SCDB.SendCandidatesData(out);

                return true;
            } else if (!isValid) {
                out.writeObject("SCELECT_LOGININFO_REJECTED");
                return false;
            } else {
                out.writeObject("SCELECT_ALREADY_VOTED");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    void handleBallotData() {
        try {
            int numPositions = SCDB.GetNumOfficerPositions();

            // Client waits for a response from us - anything can be used here
            out.writeObject("Hello, World!");

            String[] ballotData = (String[]) in.readObject();
            Boolean isBallotDataValid = true;
            if (numPositions == ballotData.length) {
                for (int i = 0; i < numPositions; i++) {
                    System.out.println(ballotData[i]);
                    if (ballotData[i] == null) {
                        isBallotDataValid = false;
                    }
                }
            }

            if (isBallotDataValid) {
                out.writeObject("SCELECT_BALLOTDATA_VALID");
            } else {
                out.writeObject("SCELECT_BALLOTDATA_INVALID");
            }

            try {
                SCDB.RecordVotes(ballotData, user);
            } catch (SQLException ex) {
                System.err.println("Vote data failed to get recorded.");
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    void releaseResources() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
