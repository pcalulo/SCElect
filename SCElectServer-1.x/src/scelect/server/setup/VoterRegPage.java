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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import scelect.server.core.*;

/**
 *
 * @author lugkhast
 */
public class VoterRegPage extends SetupPage {

    public VoterRegPage() {
        initComponents();
        if (ServerCore.hasConfigured()) {
            updateList();
        }
    }

    void initComponents() {
        topLabel = new JLabel(topText);
        topLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(topLabel);
        add(Box.createVerticalStrut(5));

        voterModel = new DefaultListModel();
        voterList = new JList(voterModel);
        voterList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                String selectedValue = (String) voterList.getSelectedValue();
                if (selectedValue != null) {
                    nameField.setText(selectedValue);
                }
            }
        });

        scroller = new JScrollPane(voterList);
        scroller.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        add(scroller);

        ctrls = new JPanel();
        ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.LINE_AXIS));
        nameField = new JTextField();
        nameField.setMinimumSize(new Dimension(300, 20));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        nameField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addVoter();
            }
        });
        nameField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    SetupWindow.getInstance().requestFocusInWindow();
                }
            }
        });
        ctrls.add(nameField);

        ctrls.add(Box.createHorizontalStrut(5));

        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addVoter();
            }
        });
        ctrls.add(addButton);

        editButton = new JButton("Update");
        editButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editVoter();
            }
        });
        ctrls.add(editButton);

        removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeVoter();
            }
        });
        ctrls.add(removeButton);

        importButton = new JButton("Import");
        importButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                importVoters();
            }
        });
        ctrls.add(importButton);

        ctrls.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(ctrls);
    }

    /**
     * This method updates the list of voters.
     */
    void updateList() {
        try {
            voterModel.clear();
            String voters[] = SCDB.GetAllVoters();
            for (int i = 0; i < voters.length; i++) {
                voterModel.addElement(voters[i]);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Setup failed to update the voter list.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    void addVoter() {
        String name = nameField.getText();
        if (name.equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a name.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            SCDB.RegisterVoter(name);
            updateList();
            nameField.setText("");
            nameField.requestFocusInWindow();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Setup was unable to register that voter.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    void editVoter() {
        String oldName = (String) voterList.getSelectedValue();
        String newName = nameField.getText();

        if (oldName == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a voter.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (newName.equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a name.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            SCDB.EditVoter(oldName, newName, null);
            updateList();
            nameField.setText("");
            nameField.requestFocusInWindow();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Setup was unable to modify that voter's information.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    void removeVoter() {
        String name = (String) voterList.getSelectedValue();
        if (name == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a voter.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this voter?",
                SetupWindow.wndTitle,
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                SCDB.RemoveVoter(name);
                updateList();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Setup was unable to remove that voter.",
                        SetupWindow.wndTitle,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    void importVoters() {
        fileChooser = new JFileChooser();
        int retVal = fileChooser.showOpenDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                SCDB.ImportVotersFromFile(file);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "SCElect Server has failed to register at least some " +
                        "of your voters to the database.",
                        "Voter Registration",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "The file was not found.",
                        "Voter Registration",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            updateList();
        }
    }

    @Override
    public boolean canProceed() {
        return true;
    }

    @Override
    public boolean canReturn() {
        return true;
    }
    DefaultListModel voterModel;
    JList voterList;
    JScrollPane scroller;
    JPanel ctrls;
    JButton addButton;
    JButton editButton;
    JButton removeButton;
    JButton importButton;
    JTextField nameField;

    JFileChooser fileChooser;

    public JLabel topLabel;
    String topText =
            "<HTML>" +
            "<H2>Voter Registration</H2>" +
            "This is the final step in setting up your server. Enter the " +
            "names of all of your voters. They themselves will be the ones " +
            "to create a password." +
            "</HTML>";
}
