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
import java.net.ServerSocket;

/**
 * This class accepts connections and passes them along to
 * <code>RegWorkerThread</code> to be processed.
 * @author lugkhast
 */
public class RegMasterThread extends Thread {

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8088);
            System.out.println("Registration master thread started");
            while (true) {
                new RegWorkerThread(serverSocket.accept()).start();
            }
        } catch (IOException ex) {
            System.err.println("Registration thread failed to start up - " +
                    "port 8088 in use");
        }
    }
}
