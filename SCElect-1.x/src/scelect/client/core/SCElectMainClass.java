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

import javax.swing.*;
import scelect.client.gui.*;

/**
 *
 * @author lugkhast
 */
public class SCElectMainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SCENetSettings.init();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                } catch (Exception e) {
                    // Should anything be done here?
                }
                new BallotWindow();
            }
        });
    }
}
