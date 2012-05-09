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

/**
 * This class stores information regarding which machine the server is hosted
 * on.
 *
 * @author lugkhast
 */
public class SCENetSettings {

    private static int port;
    private static String serverHostname;
    private static String fSep = File.separator;
    private static String fileName = "NetData.scdf";
    private static File dataFile;
    private static File dir;
    private static String homeStr;

    public static void init() {
        if (System.getenv("HomePath") != null) {
            // We're running on Windows!
            // C:\Documents and Settings\*username*\.scelect\   (Windows XP)
            homeStr = System.getenv("HomeDrive") + System.getenv("HomePath") + fSep + ".scelect" + fSep;
        } else if (System.getenv("HOME") != null) {
            // /home/*username*/.scelect/
            homeStr = System.getenv("HOME") + fSep + ".scelect" + fSep;
        } else {
            // What the hell are we running on?
            // /.scelect/
            homeStr = fSep + ".scelect" + fSep; // Let's save our stuff at the root of the drive...
        }

        // Create our directories at the root of the drive where SCElect is being run from
        dataFile = new File(homeStr + fileName);
        dir = new File(homeStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dataFile.exists()) {
            serverHostname = "localhost";
            port = 8086;
            ObjectOutputStream fout;
            try {
                fout = new ObjectOutputStream(new BufferedOutputStream(
                        new FileOutputStream(dataFile)));
                fout.writeObject(serverHostname);
                fout.writeInt(port);
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ObjectInputStream fin = new ObjectInputStream(
                        new BufferedInputStream(new FileInputStream(dataFile)));
                serverHostname = (String) fin.readObject();
                port = fin.readInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getServerHostname() {
        return serverHostname;
    }

    public static int getPort() {
        return port;
    }

    /**
     * Sets the machine that the client will connect to, then saves it to disk.
     *
     * @param newHostname The hostname of the machine to connect to
     */
    public static void setServerHostname(String newHostname) {
        serverHostname = newHostname;
        saveSettings();
    }

    /**
     * Sets the port that the client will contact the server on, then saves it
     * to disk.
     *
     * @param newPort The port to use
     */
    public static void setPort(int newPort) {
        port = newPort;
        saveSettings();
    }

    /**
     * Saves the current settings to disk.
     */
    public static void saveSettings() {
        try {
            // dataFile = path + fileName;
            ObjectOutputStream fout;
            fout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)));
            fout.writeObject(serverHostname);
            fout.writeInt(port);
            fout.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
        System.out.println("Server: " + SCENetSettings.getServerHostname());
        System.out.println("Port: " + SCENetSettings.getPort());
    }
}
