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
import scelect.server.setup.*;

/**
 *
 * @author lugkhast
 */
public class EditElectionWindow extends JDialog {

    PositionsPage posPage;
    VoterRegPage voterPage;

    public EditElectionWindow(JFrame parent) {
        super(parent, true);
        initComponents();
    }

    void initComponents() {
        setTitle("Edit Election");
        posPage = new PositionsPage();
        voterPage = new VoterRegPage();

        JTabbedPane pages = new JTabbedPane();

        pages.add("Officer Positions", posPage);
        pages.add("Voters", voterPage);

        add(pages);

        posPage.posAddBtn.setEnabled(false);
        posPage.posRemoveBtn.setEnabled(false);
        posPage.topLabel.setText(posText);

        posPage.candAddBtn.setEnabled(false);
        posPage.candRemoveBtn.setEnabled(false);

        voterPage.topLabel.setText(votersText);

        setMinimumSize(new Dimension(640, 480));
        centerDialog();
    }

    void centerDialog() {
        Frame parent = (Frame) getParent();
        Point loc = parent.getLocation();
        int xLoc = (loc.x + (parent.getWidth() / 2)) - (getWidth() / 2);
        int yLoc = (loc.y + (parent.getHeight() / 2)) - (getHeight() / 2);
        setLocation(xLoc, yLoc);
    }

    String posText = 
            "<HTML>" +
            "<H2>Modify your Officer Positions and Candidates</H2>" +
            "</HTML>";
    String votersText = 
            "<HTML>" +
            "<H2>Voter Registration</H2>" +
            "</HTML>";
}
