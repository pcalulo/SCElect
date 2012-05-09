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
import java.net.Socket;
import javax.swing.JOptionPane;

import javax.swing.SwingWorker;
import scelect.client.gui.*;

/**
 *
 * @author lugkhast
 */
public class SubmitBallotWorker extends SwingWorker<Boolean, Void> {

    Socket socket;
    String[] ballotData;
    ObjectOutputStream out;
    ObjectInputStream in;

    public SubmitBallotWorker(String[] ballotData) {
        out = LoginWorker.out;
        in = LoginWorker.in;
        this.ballotData = ballotData;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        out.writeObject("SCELECT_BALLOTDATA_TRANSFER_START");
        in.readObject();
        
        out.writeObject(ballotData);

        String response = (String) in.readObject();
        if (response.equals("SCELECT_BALLOTDATA_VALID")) {
            LoginWorker.logout();
            return true;
        }
        return false;
    }

    @Override
    public void done() {
        boolean successful = false;
        try {
            successful = get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (successful) {
            JOptionPane.showMessageDialog(null,
                    "Your ballot has been processed. You may leave now.",
                    BallotWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "A problem was encountered while processing your ballot. " +
                    "Please try again.",
                    BallotWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
        }

        BallotWindow mainWnd = BallotWindow.instance;
        mainWnd.showPane("login");
    }
}
