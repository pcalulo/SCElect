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
package scelect.server.gui;

import javax.swing.*;
import java.awt.*;

import java.sql.SQLException;
import scelect.server.core.*;

/**
 *
 * @author lugkhast
 */
public class ResultsDialog extends JDialog {

    public ResultsDialog(JFrame parent) {
        super(parent, true);
        setTitle("SCElect Server - Election Results");

        initComponents();
    }

    void initComponents() {
        setMinimumSize(new Dimension(640, 480));
        setLocationByPlatform(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        JScrollPane scroller = new JScrollPane(container);
        scroller.getVerticalScrollBar().setUnitIncrement(16);

        String text = "<HTML><H1>Election Results</H1>";

        try {
            ResultsStruct results = SCDB.GetResults();
            String[] positions = results.getPositions();
            String[][] candidates = results.getCandidates();
            int[][] votes = results.getVoteData();
            for (int posIndex = 0; posIndex < positions.length; posIndex++) {
                text += "<H2>" + positions[posIndex] + "</H2>";
                String[] candsForPos = candidates[posIndex];
                int[] votesForPos = votes[posIndex];
                for (int candIndex = 0; candIndex < candsForPos.length;
                    candIndex++) {
                        text += "<B>" + candsForPos[candIndex] + "</B>" +
                                " with <B>" + votesForPos[candIndex] +
                                "</B> votes <BR>";
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // TODO: Create a way to write results out to a file
        text += "</HTML>";

        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        container.add(label);
        add(scroller);

        setResizable(true);
        centerDialog();
    }

    void centerDialog() {
        Frame parent = (Frame) getParent();
        Point loc = parent.getLocation();
        int xLoc = (loc.x + (parent.getWidth() / 2)) - (getWidth() / 2);
        int yLoc = (loc.y + (parent.getHeight() / 2)) - (getHeight() / 2);
        setLocation(xLoc, yLoc);
    }
}
