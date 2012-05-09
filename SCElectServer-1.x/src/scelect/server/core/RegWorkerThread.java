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
import java.util.List;

/**
 * This class handles voter registration itself.
 *
 * @author lugkhast
 */
public class RegWorkerThread extends Thread {

    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public RegWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // What does the client want?
            String request = (String) in.readObject();

            if (request.equals("SEARCH")) {
                // Go pester the DB about it.
                String search = (String) in.readObject();
                // System.out.println(search);
                try {
                    List<String> results = SCDB.SearchForVoter(search);
                    out.writeObject(results);
                } catch (SQLException ex) {
                    out.writeObject(null);
                }
            } else if (request.equals("SUBMIT")) {
                // Register the voter
                String name = (String) in.readObject();
                System.out.println(name);
                char[] password = (char[]) in.readObject();
                try {
                    SCDB.FinalizeVoter(name, password);
                    out.writeBoolean(true);
                } catch (SQLException ex) {
                    out.writeBoolean(false);
                }
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
