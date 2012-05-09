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

import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.sql.SQLException;
import javax.swing.border.TitledBorder;
import scelect.server.core.*;

/**
 * The <code>StatusWindow</code> provides information on which users are
 * currently connected and how many users have voted.
 *
 * @author lugkhast
 */
public class StatusWindow extends JFrame {

    public StatusWindow() {
        initComponents();
    }

    void initComponents() {
        setTitle("SCElect Server Election Status");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new Dimension(600, 400));

        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        
        connUsersPanel = new JPanel();
        connUsersPanel.setBorder(
                new TitledBorder("Currently Connected Voters"));
        connUsersPanel.setLayout(new BoxLayout(connUsersPanel,
                BoxLayout.PAGE_AXIS));
        add(connUsersPanel);

        listModel = new DefaultListModel();
        connUsers = new JList(listModel);
        JScrollPane connUsersScroller = new JScrollPane();
        connUsersScroller.setViewportView(connUsers);
        connUsersPanel.add(connUsersScroller);


        progressPanel = new JPanel();
        progressPanel.setBorder(new TitledBorder("Election Summary"));
        progressPanel.setLayout(new BoxLayout(progressPanel,
                BoxLayout.PAGE_AXIS));
        progressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        add(progressPanel);

        JLabel hostnameLabel = new JLabel();
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            String ipAddr = addr.getHostAddress();
            String text = String.format(
                    "<HTML><CENTER>Running on machine <B>%s</B> " +
                    "(IP Address: <B>%s</B>)</CENTER></HTML>",
                    hostname, ipAddr);
            hostnameLabel.setText(text);
        } catch (UnknownHostException ex) {
            hostnameLabel.setText(
                    "Hostname and IP address could not be determined");
        }
        hostnameLabel.setAlignmentX(CENTER_ALIGNMENT);
        hostnameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progressPanel.add(hostnameLabel);

        progressBar = new JProgressBar();
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(75);
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressPanel.add(progressBar);

        progressDetails = new JLabel();
        progressDetails.setText("<HTML><B>1337/1337</B> (1337%)</HTML>");
        progressDetails.setAlignmentX(CENTER_ALIGNMENT);
        progressDetails.setHorizontalAlignment(SwingConstants.CENTER);
        progressPanel.add(progressDetails);


        // Menu bar code
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        
        exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ServerCore.requestExit();
            }
        });
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);


        JMenu electionMenu = new JMenu("Election");
        electionMenu.setMnemonic('c');
        resultsItem = new JMenuItem("Results", KeyEvent.VK_T);
        resultsItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showResults();
            }
        });
        electionMenu.add(resultsItem);
        JMenuItem openConfigItem = new JMenuItem("Edit Election Settings",
                KeyEvent.VK_E);
        openConfigItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openElectionConfig();
            }
        });
        electionMenu.add(openConfigItem);
        menuBar.add(electionMenu);
        JMenuItem getNotYetVotedList = new JMenuItem(
                "Voters who have not yet voted");
        getNotYetVotedList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getNotYetVoted();
            }
        });
        electionMenu.add(getNotYetVotedList);

        setJMenuBar(menuBar);

        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
    }

    public void openElectionConfig() {
        new EditElectionWindow(this).setVisible(true);
        updateProgressCount();
    }

    public void getNotYetVoted() {
        JDialog results = new JDialog(this, true);
        results.setTitle("Voters who have not yet voted");
        results.setMinimumSize(new Dimension(640, 480));
        results.setResizable(false);
        results.setLocationByPlatform(true);

        JLabel label = new JLabel();
        JScrollPane scroller = new JScrollPane(label);
        results.add(scroller);

        List<String> notYetVotedList = null;
        try {
            notYetVotedList = SCDB.GetVotersNotYetVoted();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "SCElect Server has failed to get the list of voters who " +
                    "have not yet voted.",
                    "SCElect Server",
                    JOptionPane.ERROR_MESSAGE);
        }

        String text = 
                "<HTML>" +
                "<H3>Following are the voters who have not yet voted:</H3>";

        for (String name : notYetVotedList) {
            text += name + "<BR>";
        }

        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        label.setText(text);

        Point loc = this.getLocation();
        int xLoc = (loc.x + (this.getWidth() / 2)) - (results.getWidth() / 2);
        int yLoc = (loc.y + (this.getHeight() / 2)) - (results.getHeight() / 2);
        results.setLocation(xLoc, yLoc);

        results.setVisible(true);
    }

    public void addOnlineUser(String name) {
        listModel.addElement(name);
    }

    public void removeOnlineUser(String name) {
        listModel.removeElement(name);
    }

    public void updateProgressCount() {
        try {
            int voted = SCDB.GetNumVotersVoted();
            int voters = SCDB.GetNumVoters();
            double pct =  ((double) voted / (double) voters) * 100;
            int unregistered = SCDB.GetNumVotersUnregistered();
            if (voters > 0) {
                String text = "<HTML><B>%d/%d</B> (<B>%.2f%%</B>) voted | " +
                        "<B>%d</B> voters unregistered, <B>%d</B> " +
                        "registered</HTML>";
                progressDetails.setText(
                        String.format(text, voted, voters, pct, unregistered,
                        voters - unregistered));
            } else {
                progressDetails.setText("<B>No registered voters</B>");
            }

            progressBar.setMinimum(0);
            progressBar.setMaximum(voters);
            progressBar.setValue(voted);
        } catch (SQLException ex) {
            ex.printStackTrace();
            progressDetails.setText(
                    "<HTML><B>Election Progress Unknown</B></HTML>");
        }
    }

    public void showResults() {
        new ResultsDialog(this).setVisible(true);
    }

    public static void main(String[] args) {
        // /*
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Should anything even be done here?
        }
        // */

        new StatusWindow().setVisible(true);
    }
    JPanel connUsersPanel;
    JList connUsers;
    JPanel progressPanel;
    JProgressBar progressBar;
    JLabel progressDetails;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem resultsItem;
    JMenuItem exitItem;
    DefaultListModel listModel;
}
