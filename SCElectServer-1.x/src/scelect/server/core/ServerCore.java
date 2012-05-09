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

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import scelect.server.*;
import scelect.server.gui.*;
import scelect.server.setup.SetupWindow;

/**
 * This class handles the startup and core functions of SCElect Server.
 *
 * @author lugkhast
 */
public class ServerCore {

    private static StatusWindow mainWindow = null;
    private static List<String> connectedUsers = new ArrayList<String>();

    /**
     * This method should be called whenever a user connects to add that user
     * to the server's list of connected users. Doing so puts the user's name
     * on the server UI and prevents that username from being used to log in
     * from anywhere else.
     *
     * @param name The username to add
     */
    public static void onUserConnect(String name) {
        connectedUsers.add(name);
        mainWindow.addOnlineUser(name);
    }

    /**
     * This method removes the specified username from the server's UI and
     * internal list of connected users, allowing that username to be used
     * to connect again, unless they have already voted.
     *
     * @param name The username to remove
     */
    public static void onUserDisconnect(String name) {
        connectedUsers.remove(name);
        mainWindow.removeOnlineUser(name);
    }

    public static void updateWindow() {
        mainWindow.updateProgressCount();
    }

    /**
     * Checks whether the specified user is connected.
     *
     * @param name
     * @return <code>true</code> if the user is connected, <code>false</code>
     * otherwise.
     */
    public static boolean isUserConnected(String name) {
        return connectedUsers.contains(name);
    }

    public static boolean hasConfigured() {
        String fSep = File.separator;
        String homeStr;
        if (System.getenv("USERPROFILE") != null) {
            // We're running on Windows!
            // C:\Documents and Settings\*username*\.scelect\   (Windows XP)
            // C:\Users\*username*\.scelect\ (Windows Vista, Windows 7)
            homeStr = System.getenv("HomeDrive") + System.getenv("HomePath") + fSep + ".scelect" + fSep;
        } else if (System.getenv("HOME") != null) {
            // Unix-like!
            // /home/*username*/.scelect/
            homeStr = System.getenv("HOME") + fSep + ".scelect" + fSep;
        } else {
            // What the hell are we running on?
            // /.scelect/
            homeStr = fSep + ".scelect" + fSep; // Let's save our stuff at the root of the drive...
        }
        File file = new File(homeStr + "configured");
        return file.exists();
    }

    public static void requestExit() {
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!hasConfigured()) {
            new SetupWindow().setVisible(true);
            return;
        }

        // Ask for database login
        DBLoginDialog dlg = new DBLoginDialog(null, true);
        dlg.setVisible(true);

        // Initialize networking
        ServerSocket serverSocket = null;
        boolean listening = true;
        int port = 8086;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("SCElectServer: failed to listen in port " + port);
            System.err.println("Another application may already be using it.");
            // e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "SCElectServer failed to listen in port " + port + ". Another program may already be\n" +
                    "listening in the port that SCElectServer uses, possibly another instance of\n" +
                    "SCElectServer. Check if another instance of SCElectServer is already running.\n\n" +
                    "If another instance is running, there is no need to start another one. If\n" +
                    "no other instance of SCElectServer is running, try closing other programs that\n" +
                    "require a network connection and try again.",
                    "SCElect Server",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        // Start the registration thread
        new RegMasterThread().start();

        System.out.println();
        System.out.println("=======================================================");
        System.out.println("Student Council Elections Server v1.1.0");
        System.out.println("Server started! Accepting connections on port " + port);
        System.out.println("=======================================================");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainWindow = new StatusWindow();
                mainWindow.updateProgressCount();
                mainWindow.setVisible(true);
            }
        });
        // Create and show the UI

        // Start listening for incoming connections
        while (listening) {
            new ServerThread(serverSocket.accept()).start();
        }

        serverSocket.close();
    }
}
