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

import java.io.*;
import java.net.*;
import java.util.List;
import javax.swing.*;
import scelect.client.gui.FinalizeVoterDialog;

/**
 *
 * @author lugkhast
 */
public class FinalizeVoterWorker extends SwingWorker<Boolean, Void> {

    ObjectOutputStream out;
    ObjectInputStream in;
    Socket socket;
    FinalizeVoterDialog dialog;

    private String workType;

    private String query;
    private List<String> result;

    private String name;
    private char[] password;

    /**
     * When given a string, FinalizeVoterWorker gets a list of matching names
     * from the server.
     * @param query The name (or part of a name) to look for
     */
    public FinalizeVoterWorker(FinalizeVoterDialog dlg, String query) {
        workType = "SEARCH";
        this.query = query;
        dialog = dlg;
    }

    /**
     * When given a String and a char[], FinalizeVoterWorker finishes the
     * specified voter's registration by giving him/her the specified password.
     * 
     * @param name Voter to add a password to
     * @param password The password to use
     */
    public FinalizeVoterWorker(FinalizeVoterDialog dlg, String name, char[] password) {
        workType = "SUBMIT";
        this.name = name;
        this.password = password;
        dialog = dlg;
    }

    

    @Override
    protected Boolean doInBackground() {
        try {
            socket = new Socket(SCENetSettings.getServerHostname(), 8088);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(workType);

            if (workType.equals("SEARCH")) {
                out.writeObject(query);
                result = (List<String>) in.readObject();
                return true;
            } else {
                out.writeObject(name);
                out.writeObject(password);
                boolean success = in.readBoolean();
                return success;
            }
        } catch (IOException ex) {
            return false;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    @Override
    protected void done() {
        boolean success = false;
        try {
            success = get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (success) {
            if (workType.equals("SEARCH")) {
                dialog.updateMatches(result);
            } else {
                
            }
        }
    }
}
