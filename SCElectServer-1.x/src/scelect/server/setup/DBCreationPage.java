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
package scelect.server.setup;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import scelect.server.core.*;

/**
 *
 * @author Lugkhast
 */
public class DBCreationPage extends SetupPage {

    public DBCreationPage() {
        initComponents();
    }

    private void initComponents() {
        JLabel topLabel = new JLabel(UI_UpperText);
        add(topLabel);
        add(Box.createVerticalStrut(10));

        dbInfoUserPanel = new JPanel();
        dbInfoUserPanel.setLayout(new BoxLayout(dbInfoUserPanel, BoxLayout.LINE_AXIS));
        dbInfoUserPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        dbInfoUserPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dbInfoUserPanel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel nameLabel = new JLabel("Username:");
        nameLabel.setMinimumSize(new Dimension(75, 25));
        dbInfoUserPanel.add(Box.createHorizontalStrut(25));
        dbInfoUserPanel.add(nameLabel);
        dbInfoUserPanel.add(Box.createHorizontalStrut(5));
        dbUserField = new JTextField();
        dbUserField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                onTextChanged();
            }
        });
        dbInfoUserPanel.add(dbUserField);
        dbInfoUserPanel.add(Box.createHorizontalStrut(25));
        add(dbInfoUserPanel);

        dbInfoPwPanel = new JPanel();
        dbInfoPwPanel.setLayout(new BoxLayout(dbInfoPwPanel, BoxLayout.LINE_AXIS));
        dbInfoPwPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        dbInfoPwPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        dbInfoPwPanel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel pwLabel = new JLabel("Password:");
        pwLabel.setMinimumSize(new Dimension(75, 25));
        dbInfoPwPanel.add(Box.createHorizontalStrut(25));
        dbInfoPwPanel.add(pwLabel);
        dbInfoPwPanel.add(Box.createHorizontalStrut(5));
        dbPwField = new JPasswordField();
        dbPwField.setMinimumSize(new Dimension(300, 20));
        dbPwField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                onTextChanged();
            }
        });
        dbInfoPwPanel.add(dbPwField);
        dbInfoPwPanel.add(Box.createHorizontalStrut(25));
        add(dbInfoPwPanel);

        JPanel createDBPanel = new JPanel();
        createButton = new JButton("Create Database");
        createButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onCreateButtonClicked();
            }
        });
        createButton.setEnabled(false);
        createDBPanel.setAlignmentX(LEFT_ALIGNMENT);
        createDBPanel.add(createButton);
        progBar = new JProgressBar();
        progBar.setIndeterminate(true);
        progBar.setVisible(false);
        // progBar.setMinimumSize(new Dimension(400, 20));
        progBar.setStringPainted(true);
        progBar.setString("Creating database...");
        createDBPanel.add(progBar);
        add(createDBPanel);
    }

    @Override
    public boolean canProceed() {
        return hasCreatedDB;
    }

    @Override
    public boolean canReturn() {
        return true;
    }

    void onTextChanged() {
        // Allow the user to click the Create Database button only when the user
        // and password fields are filled in.
        String user = dbUserField.getText();
        String password = new String(dbPwField.getPassword());
        if (!(user.equals("")) && (!password.equals(""))) {
            createButton.setEnabled(true);
        } else {
            createButton.setEnabled(false);
        }
    }

    void onCreateButtonClicked() {
        createButton.setVisible(false);
        progBar.setVisible(true);
        dbUserField.setEnabled(false);
        dbPwField.setEnabled(false);
        SwingWorker worker = createDBWorker();
        worker.execute();
    }

    private SwingWorker createDBWorker() {
        return new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() {
                try {
                    int result = SCDB.CreateDB(dbUserField.getText(),
                            new String(dbPwField.getPassword()));
                    if (result == SCDB.DBCONN_SUCCESS) {
                        return true;
                    } else if (result == SCDB.SQL_FAIL) {
                        // This shouldn't happen unless the table creation SQL
                        // code is being worked on. Of course, keep the message
                        // sane so we don't end up on The Daily WTF.
                        JOptionPane.showMessageDialog(SetupWindow.getInstance(),
                                "The table creation SQL code caused an " +
                                "exception to be thrown. Database creation " +
                                "may not have been completely successful.",
                                "SCElect Server Setup",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    return false;
                } catch (Exception e) {
                    // Usually caused by not having Derby running.
                    return false;
                }
            }

            @Override
            protected void done() {
                boolean successful;
                try {
                    successful = get();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            SetupWindow.getInstance(),
                            "A problem was encountered while finishing up " +
                            "database creation. The database may have not " +
                            "been created. Please delete the " +
                            "<B>scelect-db</B> folder (if it exists) and " +
                            "try again.",
                            "SCElect Server Setup",
                            JOptionPane.ERROR_MESSAGE);
                    resetAll();
                    return;
                }
                if (successful) {
                    SetupWindow.allowNext(true);
                    SetupWindow.allowBack(false);
                    progBar.setString("Done!");
                    progBar.setIndeterminate(false);
                    progBar.setMinimum(0);
                    progBar.setMaximum(10);
                    progBar.setValue(10);
                    hasCreatedDB = true;
                } else {
                    resetAll();
                    JOptionPane.showMessageDialog(SetupWindow.getInstance(),
                            "Setup failed to connect to Apache Derby. Please " +
                            "check if it is running.",
                            "SCElect Server Setup",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    void resetAll() {
        dbUserField.setEnabled(true);
        dbPwField.setEnabled(true);
        progBar.setVisible(false);
        createButton.setVisible(true);
    }
    JPanel dbInfoUserPanel;
    JTextField dbUserField;
    JPanel dbInfoPwPanel;
    JPasswordField dbPwField;
    JButton createButton;
    JProgressBar progBar;
    String UI_UpperText =
            "<HTML>" +
            "<H2>Database Creation</H2>" +
            "Now that you have set up Apache Derby, we can now create a " +
            "database. First, however, start Derby so SCElect Server Setup " +
            "can connect to it. Once again, see <B>readme.html</B> for " +
            "more information." +
            "<BR><BR>" +
            "For security purposes, it is required that " +
            "you password protect your database to prevent unauthorized " +
            "access. You should remember your username and password as you " +
            "will need to enter this information into SCElect Server every " +
            "time it is started up for it to be authorized to use the " +
            "database." +
            "<BR><BR>" +
            "Take note that database files will be stored in a folder named " +
            "<B>scelect-db</B>, in the directory from which Derby was " +
            "started. In other words, this is the working directory of your " +
            "terminal when you started Derby." +
            "</HTML>";

    // A flag to indicate whether a DB has been created
    boolean hasCreatedDB = false;
}
