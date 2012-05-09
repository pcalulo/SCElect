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

import scelect.client.core.*;

/**
 * This is the main window of the SCElect client. It holds both the login and
 * the actual ballot screens using CardLayout.
 *
 * @author lugkhast
 */
public class BallotWindow extends JFrame {

    public BallotWindow() {
        instance = this;
        initComponents();
    }

    /**
     * This method initializes SCElect's main window.
     */
    public void initComponents() {
        setTitle(wndTitle);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        cardLayout = new CardLayout();

        this.getContentPane().setLayout(cardLayout);
        setMinimumSize(new Dimension(800, 400));
        setLocationByPlatform(true);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu electionMenu = new JMenu("Election");
        JMenuItem registerItem = new JMenuItem("Register Yourself",
                KeyEvent.VK_R);
        registerItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showRegistrationDialog();
            }
        });
        electionMenu.add(registerItem);
        electionMenu.setMnemonic('E');
        electionMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Log Out/Exit");
        exitItem.setMnemonic('X');
        exitItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onWindowClosing();
            }
        });
        electionMenu.add(exitItem);

        menuBar.add(electionMenu);

        JMenu networkMenu = new JMenu("Network");
        networkMenu.setMnemonic('N');
        JMenuItem srvHostnameItem = new JMenuItem("Server Hostname",
                KeyEvent.VK_H);
        srvHostnameItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editHostname();
            }
        });
        networkMenu.add(srvHostnameItem);
        menuBar.add(networkMenu);

        setJMenuBar(menuBar);

        loginPage = new LoginPage();
        add(loginPage, "login");

        cardLayout.show(this.getContentPane(), "login");
        visibleCardName = "login";

        // Maximize the window
        setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
        setVisible(true);
        this.requestFocusInWindow();
    }

    /**
     * This method initializes the ballot panel. Because candidate information
     * can be changed at any time, the ballot panel is recreated every time a
     * successful login occurs.
     *
     * @param positions The officer positions for this election
     * @param candidates The candidates participating in this election
     * @param parties The parties that the candidates belong to
     * @param descs Short descriptions of the candidates
     */
    public void initBallotUI(String[] positions, String[][] candidates,
            String[][] parties, String[][] descs, String name) {
        BallotPage ballotPage = new BallotPage(positions, candidates, parties,
                descs, name);
        add(ballotPage, "ballot");
    }

    /**
     * This method decides whether to close the ballot window and exit (if not
     * logged in) or log the user out (if logged in).
     */
    void onWindowClosing() {
        if (visibleCardName.equals("ballot")) { // If we're logged in, log out.
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out without voting?",
                    wndTitle,
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                LoginWorker.logout();
                cardLayout.show(getContentPane(), "login");
                visibleCardName = "login";
            }
        } else { // If we're not, exit.
            dispose();
            System.exit(0);
        }
    }

    public void showPane(String paneName) {
        cardLayout.show(this.getContentPane(), paneName);
        visibleCardName = paneName;
    }

    public void handleLoginResult(String result) {
        loginPage.handleLoginResult(result);
    }

    public void showRegistrationDialog() {
        new FinalizeVoterDialog(this, true).setVisible(true);
    }

    public void editHostname() {
        String newHost = (String) JOptionPane.showInputDialog(this,
                "<HTML>" +
                "Please enter the hostname of the computer on which SCElect " +
                "Server is running." +
                "<BR>" +
                "Entering an incorrect value <B>will</B> prevent you from " +
                "connecting." +
                "</HTML>",
                wndTitle,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                SCENetSettings.getServerHostname());
        if (newHost != null) {
            SCENetSettings.setServerHostname(newHost);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new BallotWindow().setVisible(true);
            }
        });
    }
    public static BallotWindow instance;
    String visibleCardName;
    /**
     * The title of all SCElect windows
     */
    public static String wndTitle = "SCElect";
    LoginPage loginPage;
    // A uniform size for all borders that will be used
    Insets borderInsets = new Insets(5, 5, 5, 5);
    // Array to hold the candidate selections
    CandidateSelection[] candSelections;
    // The CardLayout object
    CardLayout cardLayout;
}
