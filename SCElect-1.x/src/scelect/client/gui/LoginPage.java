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
 * This class contains the implementation of the login page.
 *
 * @author lugkhast
 */
public class LoginPage extends JPanel {

    public LoginPage() {
        this.parent = BallotWindow.instance;
        initComponents();
    }

    /**
     * This method is called from the constructor and initializes the login
     * page.
     */
    void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        loginHeader = new JLabel("Student Council Elections");
        loginHeader.setFont(
                new Font(loginHeader.getFont().getName(), Font.PLAIN, 24));
        loginHeader.setHorizontalAlignment(SwingConstants.CENTER);
        loginHeader.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // loginHeader.setBorder(BorderFactory.createLineBorder(Color.red));
        loginHeader.setAlignmentX(CENTER_ALIGNMENT);
        add(loginHeader);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        sep.setMinimumSize(new Dimension(300, 4));
        add(sep);

        loginWrapper = new JPanel();
        loginWrapper.setLayout(new BoxLayout(loginWrapper, BoxLayout.PAGE_AXIS));
        add(loginWrapper);

        loginWrapper.add(Box.createVerticalStrut(4));

        // Name box
        loginBoxesPanel = new JPanel();
        loginBoxesPanel.setMaximumSize(new Dimension(500, 32));
        loginBoxesPanel.setLayout(new BoxLayout(loginBoxesPanel, BoxLayout.PAGE_AXIS));
        loginBoxesPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        loginNameBox = new JTextField();
        // loginNameBox.setPreferredSize(new Dimension(487, 25));
        // loginNameBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        Font curFont = loginNameBox.getFont();
        loginNameBox.setFont(new Font(curFont.getName(), Font.ITALIC, curFont.getSize()));
        loginNameBox.setForeground(Color.gray);
        loginNameBox.setText(nameBoxIdleText);
        loginNameBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loginNameBox_OnEnterPressed();
            }
        });
        loginNameBox.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                loginNameBox_OnFocusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                loginNameBox_OnFocusLost(e);
            }
        });
        loginNameBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                loseFocus(e);
            }
        });

        // Password box
        loginPwBox = new JPasswordField();
        loginPwBox.setMaximumSize(new Dimension(500, 32));
        loginPwBox.setForeground(Color.lightGray);
        loginPwBox.setText(pwBoxIdleText);
        loginPwBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                logIn();
            }
        });
        loginPwBox.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                loginPwBox_OnFocusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                loginPwBox_OnFocusLost(e);
            }
        });
        loginPwBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                loseFocus(e);
            }
        });

        loginBoxesPanel.add(loginNameBox);
        loginBoxesPanel.add(Box.createVerticalStrut(3));
        loginBoxesPanel.add(loginPwBox);

        loginWrapper.add(loginBoxesPanel);

        JPanel actionButtons = new JPanel();
        loginLoginButton = new JButton("Log In");
        loginLoginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                logIn();
            }
        });


        loginHelpButton = new JButton("Help");
        loginHelpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                giveLoginHelp();
            }
        });

        actionButtons.add(loginLoginButton);
        actionButtons.add(loginHelpButton);
        actionButtons.setMaximumSize(new Dimension(500, 25));
        loginWrapper.add(actionButtons);

        loginProgBar = new JProgressBar();
        loginProgBar.setIndeterminate(true);
        loginProgBar.setMaximumSize(new Dimension(300, 12));
        loginProgBar.setStringPainted(true);
        loginProgBar.setString("Logging in...");
        loginProgBar.setVisible(false);
        loginWrapper.add(loginProgBar);

        loginWrapper.add(Box.createVerticalStrut(4));

        JSeparator regSep = new JSeparator(JSeparator.HORIZONTAL);
        regSep.setMaximumSize(new Dimension(500, 4));
        loginWrapper.add(regSep);

        loginWrapper.add(Box.createVerticalStrut(4));

        JLabel regText = new JLabel("Not registered yet?");

        regText.setAlignmentX(CENTER_ALIGNMENT);
        loginWrapper.add(regText);
        loginRegisterButton = new JButton("Register");
        loginRegisterButton.setAlignmentX(CENTER_ALIGNMENT);
        loginRegisterButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loginRegisterButton_OnClick();
            }
        });
        loginWrapper.add(loginRegisterButton);

        loginWrapper.add(Box.createVerticalGlue());
    }
    String nameBoxIdleText = "Enter your name";
    String pwBoxIdleText = "Enter your password";

    /**
     * This method is called whenever the login window's name box gains focus.
     * @param e
     */
    void loginNameBox_OnFocusGained(FocusEvent e) {
        Font curFont = loginNameBox.getFont();
        if (loginNameBox.getText().equals(nameBoxIdleText)) {
            loginNameBox.setFont(new Font(curFont.getName(), Font.PLAIN, curFont.getSize()));
            loginNameBox.setForeground(Color.black);
            loginNameBox.setText("");
        }
    }

    /**
     * This method is called whenever the login window's name box loses focus.
     * @param e
     */
    void loginNameBox_OnFocusLost(FocusEvent e) {
        Font curFont = loginNameBox.getFont();
        if (loginNameBox.getText().equals("")) {
            loginNameBox.setFont(new Font(curFont.getName(), Font.ITALIC, curFont.getSize()));
            loginNameBox.setForeground(Color.gray);
            loginNameBox.setText(nameBoxIdleText);
        }
    }

    /**
     * This method is called whenever the login window's password box gains
     * focus.
     * @param e
     */
    void loginPwBox_OnFocusGained(FocusEvent e) {
        Font curFont = loginPwBox.getFont();
        if ((new String(loginPwBox.getPassword()).equals(pwBoxIdleText))) {
            loginPwBox.setFont(new Font(curFont.getName(), Font.PLAIN,
                    curFont.getSize()));
            loginPwBox.setForeground(Color.black);
            loginPwBox.setText("");
        }
    }

    /**
     * This method is called whenever the login window's password box loses
     * focus.
     * @param e
     */
    void loginPwBox_OnFocusLost(FocusEvent e) {
        if ((new String(loginPwBox.getPassword()).equals(""))) {
            loginPwBox.setForeground(Color.gray);
            loginPwBox.setText(pwBoxIdleText);
        }
    }

    /**
     * This method removes focus from any component of the login window.
     * @param e
     */
    void loseFocus(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.requestFocusInWindow();
        }
    }

    /**
     * This method is the login window's name editbox's event handler. It gives
     * focus to the password box.
     */
    void loginNameBox_OnEnterPressed() {
        loginPwBox.requestFocusInWindow();
    }

    void loginRegisterButton_OnClick() {
        new FinalizeVoterDialog(BallotWindow.instance, true).setVisible(true);
    }

    /**
     * This method shows a help dialog containing instructions on how to log in.
     */
    void giveLoginHelp() {
        JOptionPane.showMessageDialog(this,
                "<HTML>" +
                "To log in, enter your username in the upper text box and " +
                "your password in the lower text box." +
                "</HTML>",
                BallotWindow.wndTitle,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void lockLoginUI() {
        loginProgBar.setVisible(true);
        loginRegisterButton.setEnabled(false);
        loginLoginButton.setEnabled(false);
        loginNameBox.setEditable(false);
        loginPwBox.setEditable(false);
    }

    /**
     * This method unlocks the login page to allow logins.
     */
    public void unlockLoginUI() {
        loginProgBar.setVisible(false);
        loginRegisterButton.setEnabled(true);
        loginLoginButton.setEnabled(true);
        loginNameBox.setEditable(true);
        loginPwBox.setEditable(true);
    }

    public void clearLoginUI() {
        loginNameBox.setEditable(true);
        Font curFont = loginNameBox.getFont();
        loginNameBox.setFont(new Font(curFont.getName(), Font.ITALIC,
                curFont.getSize()));
        loginNameBox.setForeground(Color.gray);
        loginNameBox.setText(nameBoxIdleText);
        loginPwBox.setEditable(true);
        loginPwBox.setForeground(Color.gray);
        loginPwBox.setText(pwBoxIdleText);
    }

    /**
     * This method begins the login process. If the name and/or password input
     * boxes have not yet been filled in, the user is told to do so.
     *
     * @see LoginWorker
     */
    void logIn() {
        String name = loginNameBox.getText();
        char[] password = loginPwBox.getPassword();

        if ((!nameBoxIdleText.equals(name)) && (!pwBoxIdleText.equals(new String(password)))) {
            lockLoginUI();
            LoginWorker worker = new LoginWorker(name, password);
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this,
                    "The name and/or password fields are empty. Please enter " +
                    "the required information and try again.",
                    BallotWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * This method handles the result of the login process, giving an
     * appropriate graphical result to the user.
     *
     * @param result The result given by the server
     */
    public void handleLoginResult(String result) {
        unlockLoginUI();
        if ((result == null) || result.equals("SERVER_DOWN")) {
            /* No SCElect Server instance is on the machine that the client is
             * configured to connect to. */
            JOptionPane.showMessageDialog(this,
                    "The server was not found at host \"" +
                    SCENetSettings.getServerHostname() +
                    "\". Please check this program's network settings,\nyour " +
                    "network connection, and whether the server is running.",
                    BallotWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);

        } else if (result.equals("SCELECT_ALREADY_LOGGED_IN")) {
            /* Someone has logged in to the server using the same info given to
             * this client. */
            JOptionPane.showMessageDialog(this,
                    "It appears that you are already logged in from " +
                    "somewhere else.",
                    BallotWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
            return;
        } else if (result.equals("SCELECT_ALREADY_VOTED")) {
            JOptionPane.showMessageDialog(this,
                    "You have already voted.",
                    BallotWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        } else if (result.equals("SCELECT_LOGININFO_ACCEPTED")) {
            /* Success! Ballot creation code is in LoginWorker. */
            parent.showPane("ballot");
            clearLoginUI();
        } else {
            /* Bad login info was given */
            JOptionPane.showMessageDialog(this,
                    "Your username and/or password did not match " +
                    "anything in the server's database.",
                    BallotWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    BallotWindow parent;
    // UI widgets for the login card
    JPanel loginParent;
    JPanel loginWrapper;
    JLabel loginHeader;
    JPanel loginBoxesPanel;
    JTextField loginNameBox;
    JPasswordField loginPwBox;
    public JProgressBar loginProgBar;
    JButton loginHelpButton;
    JButton loginLoginButton;
    JButton loginRegisterButton;
}
