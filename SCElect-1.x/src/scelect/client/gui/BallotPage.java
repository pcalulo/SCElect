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
package scelect.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import scelect.client.core.SubmitBallotWorker;

/**
 * This class contains the implementation of the actual ballot that the user
 * will interact with during the election.
 *
 * @author lugkhast
 */
public class BallotPage extends JScrollPane {

    String[] positions;
    String[][] candidates;
    String[][] parties;
    String[][] descs;
    String name;

    public BallotPage(String[] positions, String[][] candidates,
            String[][] parties, String[][] descs, String name) {
        this.positions = positions;
        this.candidates = candidates;
        this.parties = parties;
        this.descs = descs;
        this.name = name;

        initComponents();
    }

    /**
     * This method initializes the ballot page.
     */
    void initComponents() {
        // System.out.println("Is event dispatch thread: " + SwingUtilities.isEventDispatchThread());

        // Acts as a background for the actual ballot
        ballotParent = new JPanel();
        this.setViewportView(ballotParent);
        this.getVerticalScrollBar().setUnitIncrement(16);

        // The actual ballot. Holds the CandidateSelection components.
        ballotWrapper = new JPanel();
        ballotWrapper.setLayout(new BoxLayout(ballotWrapper, BoxLayout.Y_AXIS));
        ballotWrapper.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));
        ballotWrapper.setAlignmentX(CENTER_ALIGNMENT);
        ballotWrapper.setBorder(BorderFactory.createEtchedBorder());
        ballotParent.add(ballotWrapper);

        // The label at the top
        ballotHeader = new JLabel();
        ballotHeader.setFont(new Font(ballotHeader.getFont().getName(), Font.PLAIN, 24));
        ballotHeader.setHorizontalAlignment(SwingConstants.CENTER);
        ballotHeader.setText(
                "Student Council Elections");
        ballotHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        ballotWrapper.add(ballotHeader);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        sep.setPreferredSize(new Dimension(770, 4));
        ballotWrapper.add(sep);

        // Create the greeting
        ballotGreeting = new JLabel(
                String.format("<HTML>You are: <B>%s</B></HTML>", name));
        ballotGreeting.setFont(
                new Font(ballotGreeting.getFont().getName(), Font.PLAIN, 20));
        ballotGreeting.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // 5 pixel border on the left
        
        // Aligns it to the left for some odd reason. LEFT_ALIGNMENT aligns it
        // to the right.
        ballotGreeting.setAlignmentX(RIGHT_ALIGNMENT);
        ballotGreeting.setHorizontalAlignment(SwingConstants.LEFT);
        ballotGreeting.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        ballotWrapper.add(ballotGreeting);

        // Generate the ballot
        for (int i = 0; i < positions.length; i++) {
            CandidateSelection cs = new CandidateSelection(positions[i], candidates[i], parties[i], descs[i]);
            ballotWrapper.add(cs);
        }

        bottomPanel = new JPanel();
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                CandidateSelection.resetAll();
            }
        });
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                submitBallot();
            }
        });
        bottomPanel.add(submitButton);
        bottomPanel.add(resetButton);
        ballotWrapper.add(bottomPanel);
    }

    /**
     * This method is called when the "Submit" button in the ballot page is
     * clicked.
     */
    void submitBallot() {
        if (!CandidateSelection.allPositionsFilledIn()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all the officer positions before you " +
                    "submit your ballot.",
                    BallotWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] ballotData = CandidateSelection.getSelectedCandidates();
        for (String nameInArray : ballotData) {
            System.out.println(nameInArray);
        }
        String confirmText =
                "<HTML>" +
                "Are you sure you would like to vote for the " +
                "following candidates?" +
                "<BR><BR>";
        for (int i = 0; i < ballotData.length; i++) {
            String position = positions[i];
            String candidate = ballotData[i];
            confirmText = confirmText + (
                    "<B>" + position + "</B>: " + candidate + "<BR>");
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                confirmText,
                BallotWindow.wndTitle,
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SubmitBallotWorker worker = new SubmitBallotWorker(ballotData);
            worker.execute();
        }
    }
    // UI widgets for the ballot card
    JLabel ballotHeader;
    JPanel ballotParent;
    JPanel ballotWrapper;
    public JLabel ballotGreeting;
    JLabel contentLabel;
    JPanel bottomPanel;
    JButton resetButton;
    JButton submitButton;
}
