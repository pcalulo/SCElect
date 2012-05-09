/*
 * SCElect: The client for the tool that makes student council elections easier
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
package scelect.client.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import scelect.client.gui.*;

/**
 * This class is used to log the user in, without freezing the UI while the
 * client waits for a response from the server.
 *
 * @author lugkhast
 */
public class LoginWorker extends SwingWorker<String, Void> {

    static Socket socket = null;
    static ObjectOutputStream out = null;
    static ObjectInputStream in;
    private String voterName = "";
    private char[] voterPassword;
    private String positions[];
    private String candidates[][];
    private String parties[][];
    private String descs[][];
    BallotWindow mainWnd;

    public LoginWorker(String voterName, char[] voterPassword) {
        mainWnd = BallotWindow.instance;
        this.voterName = voterName;
        this.voterPassword = voterPassword;
    }

    /**
     * Handles the candidate data from the server and bundles it into arrays
     * that are used for UI creation.
     *
     * @throws java.io.IOException
     */
    void processCandidateData() throws IOException, ClassNotFoundException {
        positions = (String[]) in.readObject();
        candidates = (String[][]) in.readObject();
        parties = (String[][]) in.readObject();
        descs = (String[][]) in.readObject();

        /* System.out.println("Number of positions: " + descs.length);
        for (int i = 0; i < positions.length; i++) {
            System.out.println("Position: " + positions[i]);
            System.out.println("Number of candidates in pos " + i + ": " +
                    candidates[0].length);
            for (int candId = 0; candId < candidates[i].length; candId++) {
                System.out.println("Candidate: " + candidates[i][candId]);
                System.out.println("Party: " + parties[i][candId]);
                System.out.println("Desc: " + descs[i][candId]);
            }
            
        }
         * */
        

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                mainWnd.initBallotUI(positions, candidates, parties, descs, voterName);
            }
        });
    }

    /**
     * Logs the user out.
     */
    public static void logout() {
        try {
            out.writeObject("SCELECT_LOGOUT");
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground() {
        Boolean connected = false;
        String result;

        String hostname = SCENetSettings.getServerHostname();
        int port = SCENetSettings.getPort();
        // mainWnd.loginProgBar.setString("Connecting to host \"" + hostname + "\"...");

        // Connect
        try {
            socket = new Socket(hostname, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
        } catch (Exception ex) {
            return "SERVER_DOWN";
        }

        if (connected) {
            try {
                // Send login info
                out.writeObject(voterName);
                out.writeObject(voterPassword);
                Arrays.fill(voterPassword, '0');
                
                result = (String) in.readObject();

                if (result.equals("SCELECT_LOGININFO_ACCEPTED")) {
                    // Login info is good, let the user in
                    String schoolYear = (String) in.readObject();
                    processCandidateData(); // All the magic happens here
                }
                // Regardless of success, return the result so any further
                // GUI-side processing can be done
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return "SERVER_DOWN";
    }

    @Override
    public void done() {
        String result = null;
        try {
            result = get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mainWnd.handleLoginResult(result);
    }
}
