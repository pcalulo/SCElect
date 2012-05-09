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
import java.sql.SQLException;

import java.util.Hashtable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import scelect.server.core.*;
/**
 *
 * @author lugkhast
 */
public class PositionsPage extends SetupPage {

    private static PositionsPage instance;
    private Hashtable[] candidates;

    public PositionsPage() {
        initComponents();
        instance = this;
        if (ServerCore.hasConfigured()) {
            updatePosList();
        }
    }

    void initComponents() {
        topLabel = new JLabel(topText);
        topLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(topLabel);
        add(Box.createVerticalStrut(5));

        add(Box.createVerticalStrut(5));

        container = new JPanel();

        GridLayout layout = new GridLayout(2, 1);
        container.setLayout(layout);

        // Create officers panel
        officersPanel = new JPanel();
        officersPanel.setBorder(BorderFactory.createTitledBorder("Officer Positions"));
        officersPanel.setLayout(new BoxLayout(
                officersPanel, BoxLayout.LINE_AXIS));

        posModel = new DefaultListModel();
        posList = new JList(posModel);
        posList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        posList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                updateCandidateList();
            }
        });
        posScroller = new JScrollPane(posList);
        posScroller.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, Integer.MAX_VALUE));
        officersPanel.add(posScroller);

        officersPanel.add(Box.createHorizontalStrut(5));

        posButtonPanel = new JPanel();
        posButtonPanel.setLayout(new BoxLayout(
                posButtonPanel, BoxLayout.PAGE_AXIS));

        Dimension minBtnSize = new Dimension(110, 25);

        posAddBtn = new JButton("Add");
        posAddBtn.setMaximumSize(minBtnSize);
        posAddBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addPosition();
            }
        });
        posButtonPanel.add(posAddBtn);

        posEditBtn = new JButton("Edit");
        posEditBtn.setMaximumSize(minBtnSize);
        posEditBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editPosition();
            }
        });
        posButtonPanel.add(posEditBtn);

        posRemoveBtn = new JButton("Remove");
        posRemoveBtn.setMaximumSize(minBtnSize);
        posRemoveBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removePosition();
            }
        });
        posButtonPanel.add(posRemoveBtn);

        posButtonPanel.add(Box.createVerticalGlue());

        posMoveUpBtn = new JButton("Move Up");
        posMoveUpBtn.setMaximumSize(minBtnSize);
        posMoveUpBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                movePositionUp();
            }
        });
        posButtonPanel.add(posMoveUpBtn);

        posMoveDownBtn = new JButton("Move Down");
        posMoveDownBtn.setMaximumSize(minBtnSize);
        posMoveDownBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                movePositionDown();
            }
        });
        posButtonPanel.add(posMoveDownBtn);

        officersPanel.add(posButtonPanel);
        container.add(officersPanel);


        // Create candidates panel
        candidatesPanel = new JPanel();
        candidatesPanel.setBorder(BorderFactory.createTitledBorder(
                "Candidates (create and/or select a position above)"));
        candidatesPanel.setLayout(
                new BoxLayout(candidatesPanel, BoxLayout.LINE_AXIS));

        candModel = new DefaultListModel();
        candList = new JList(candModel);
        candList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candScroller = new JScrollPane(candList);
        candScroller.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, Integer.MAX_VALUE));

        candidatesPanel.add(candScroller);
        candidatesPanel.add(Box.createHorizontalStrut(5));

        candButtonPanel = new JPanel();
        candButtonPanel.setLayout(new BoxLayout(
                candButtonPanel, BoxLayout.PAGE_AXIS));

        candAddBtn = new JButton("Add");
        candAddBtn.setMaximumSize(minBtnSize);
        candAddBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addCandidate();
            }
        });
        candButtonPanel.add(candAddBtn);

        candEditBtn = new JButton("Edit");
        candEditBtn.setMaximumSize(minBtnSize);
        candEditBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editCandidate();
            }
        });
        candButtonPanel.add(candEditBtn);

        candRemoveBtn = new JButton("Remove");
        candRemoveBtn.setMaximumSize(minBtnSize);
        candRemoveBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeCandidate();
            }
        });
        candButtonPanel.add(candRemoveBtn);

        candButtonPanel.add(Box.createVerticalGlue());

        candidatesPanel.add(candButtonPanel);

        container.add(candidatesPanel);
        add(container);
    }

    String getSelectedPos() {
        return (String) posList.getSelectedValue();
    }

    String getSelectedCand() {
        // The candList displays both name and party.
        // We could use regex, but this one is pretty self-explanatory.
        int index = candList.getSelectedIndex();
        if (index == -1) {
            return null;
        }
        return (String) candidates[index].get("NAME");
    }

    void updatePosList() {
        try {
            String[] positions = SCDB.GetPositions();
            posModel.removeAllElements();
            for (String position : positions) {
                posModel.addElement(position);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Setup was unable to refresh the positions list.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    void updateCandidateList() {
        if (getSelectedPos() == null) {
            return;
        }
        try {
            candidates = SCDB.GetCandidatesInPosition(getSelectedPos());
            candModel.clear();
            for (Hashtable candData : candidates) {
                String candString = candData.get("NAME") + ", " +
                        candData.get("PARTY");
                candModel.addElement(candString);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Setup was unable to refresh the candidate list for the " +
                    "selected position.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    void addCandidate() {
        if (getSelectedPos() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and/or select an officer position first.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        CandidateRegDlg dialog = new CandidateRegDlg(SetupWindow.getInstance(),
                true, getSelectedPos());
        dialog.setVisible(true);
        updateCandidateList();
    }

    void editCandidate() {
        if (getSelectedPos() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and/or select an officer position first.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (getSelectedCand() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and/or select a candidate first.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Hashtable candTbl = candidates[candList.getSelectedIndex()];
        String name = (String) candTbl.get("NAME");
        String party = (String) candTbl.get("PARTY");
        String desc = (String) candTbl.get("DESC");

        CandidateRegDlg dialog = new CandidateRegDlg(SetupWindow.getInstance(),
                true, name, party, desc);
        dialog.setVisible(true);
        updateCandidateList();
    }

    void removeCandidate() {
        if (getSelectedPos() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and/or select an officer position first.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (getSelectedCand() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and/or select a candidate first.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove that candidate?",
                SetupWindow.wndTitle,
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                SCDB.RemoveCandidate(getSelectedCand());
                updateCandidateList();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Setup was unable to delete that candidate.",
                        SetupWindow.wndTitle,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* POSITIONS RELATED METHODS */
    void addPosition() {
        String newPos = JOptionPane.showInputDialog(
                this,
                "Enter the name of the officer position that you would like " +
                "to add.",
                "Add Officer Position", JOptionPane.QUESTION_MESSAGE);

        if (newPos == null) {
            return;
        }

        try {
            SCDB.AddPosition(newPos);
            posModel.addElement(newPos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Setup was unable to add that position. There may already " +
                    "be an officer position with that name.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    void editPosition() {
        String oldName = getSelectedPos();
        if (oldName == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and/or select an officer position first.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String newName = (String) JOptionPane.showInputDialog(
                this,
                "Please enter a new name for the position \"" + oldName + "\".",
                "Edit Position",
                JOptionPane.QUESTION_MESSAGE,
                null, null,
                oldName);
        if (newName == null) {
            return;
        }

        try {
            SCDB.EditPosition(oldName, newName);
            int selected = posList.getSelectedIndex();
            posModel.removeElementAt(selected);
            posModel.add(selected, newName);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Setup was unable to perform the requested name change.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void removePosition() {
        String oldName = getSelectedPos();
        if (oldName == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and/or select an officer position first.",
                    SetupWindow.wndTitle,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you would like to delete that officer position?",
                SetupWindow.wndTitle,
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            String pos = (String) posList.getSelectedValue();
            try {
                SCDB.RemovePosition(pos);
                posModel.removeElement(pos);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Setup was unable to delete the selected officer position.",
                        SetupWindow.wndTitle,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void movePositionUp() {
        int selectedIndex = posList.getSelectedIndex();
        if (selectedIndex == 0) {
            // We can't move it up if it's already at the top
            System.out.println("At the top!");
            return;
        }
        try {
            SCDB.SwapPositions(selectedIndex, selectedIndex - 1);
            String selectedPosition = (String) posList.getSelectedValue();
            posModel.removeElement(selectedPosition);
            posModel.insertElementAt(selectedPosition, selectedIndex - 1);
            posList.setSelectedIndex(selectedIndex - 1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Setup was unable to reorder the positions.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    void movePositionDown() {
        int selectedIndex = posList.getSelectedIndex();
        if (selectedIndex == posModel.getSize() - 1) {
            // We can't move it up if it's already at the bottom
            System.out.println("At the bottom!");
            return;
        }
        try {
            SCDB.SwapPositions(selectedIndex, selectedIndex + 1);
            String selectedPosition = (String) posList.getSelectedValue();
            posModel.removeElement(selectedPosition);
            posModel.insertElementAt(selectedPosition, selectedIndex + 1);
            posList.setSelectedIndex(selectedIndex + 1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Setup was unable to reorder the positions.",
                    SetupWindow.wndTitle,
                    JOptionPane.ERROR_MESSAGE);
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
    JPanel container;
    JPanel officersPanel;
    JPanel candidatesPanel;
    JScrollPane posScroller;
    JList posList;
    DefaultListModel posModel;
    JPanel posButtonPanel;
    public JButton posAddBtn;
    public JButton posEditBtn;
    public JButton posRemoveBtn;
    public JButton posMoveUpBtn;
    public JButton posMoveDownBtn;
    JScrollPane candScroller;
    JList candList;
    DefaultListModel candModel;
    JPanel candButtonPanel;
    public JButton candAddBtn;
    public JButton candEditBtn;
    public JButton candRemoveBtn;

    public JLabel topLabel;
    String topText =
            "<HTML>" +
            "<H2>Register your Positions and Candidates</H2>" +
            "Now that a database is up and running, we can add data for " +
            "officer positions and candidates. Take note that you will no " +
            "longer be able to add or remove officer positions or candidates " +
            " after setup is finished."+
            "</HTML>";
}
