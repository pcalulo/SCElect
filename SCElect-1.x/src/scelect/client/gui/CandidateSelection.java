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

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author lugkhast
 */
public class CandidateSelection extends JPanel {

    /**
     * The radio buttons on the GUI are instances of this class.
     */
    private class CustomRadioButton extends JRadioButton {

        String candidateName;
        String party;
        String desc;
        JLabel label;
        JRadioButton[] otherButtons;

        public CustomRadioButton(String candidateName, String party,
                String desc, JLabel label, JRadioButton[] otherButtons) {
            super("<HTML><B>" + candidateName + "</B>, " + party + "</HTML>");
            setAlignmentX(RIGHT_ALIGNMENT);
            this.candidateName = candidateName;
            this.party = party;
            this.desc = desc;
            this.label = label;
            this.otherButtons = otherButtons;
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    onClick();
                }
            });
        }

        void onClick() {
            label.setText(
                    "<HTML><H3>" + 
                        candidateName + ", " + party + "</H3>" + desc +
                    "</HTML>");

            for (JRadioButton btn : otherButtons) {
                btn.setSelected(false);
            }
            setSelected(true);
            

            CandidateSelection parent = (CandidateSelection) this.getParent();
            parent.selectedCandidate = candidateName;
            System.out.println("New selected candidate: " + parent.selectedCandidate);
        }
    } // End of CustomRadioButton code

    // Code for CandidateSelection itself
    String[] descriptions;
    JRadioButton[] buttons;
    JLabel descLabel;
    
    private String selectedCandidate;
    // private static CandidateSelection[] instances;
    private static ArrayList<CandidateSelection> instances;
    private static int instanceCount = 0;

    public CandidateSelection(String candidatePos, String[] candidates,
            String[] parties, String[] descs) {
        if (instances == null) {
            System.out.println("Instances ArrayList created");
            instances = new ArrayList<CandidateSelection>();
        }
        setBorder(BorderFactory.createTitledBorder(candidatePos));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);
        // setMaximumSize(new Dimension(750, Integer.MAX_VALUE));

        descriptions = descs;
        buttons = new JRadioButton[descriptions.length];
        descLabel = new JLabel();

        for (int i = 0; i < candidates.length; i++) {
            CustomRadioButton radioBtn = new CustomRadioButton(
                    candidates[i],
                    parties[i],
                    descs[i],
                    descLabel,
                    buttons);
            buttons[i] = radioBtn;
            add(radioBtn);
        }

        JPanel labelHolder = new JPanel();
        labelHolder.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelHolder.setAlignmentX(RIGHT_ALIGNMENT);
        descLabel.setMaximumSize(new Dimension(750, Integer.MAX_VALUE));
        descLabel.setAlignmentX(LEFT_ALIGNMENT);
        labelHolder.add(descLabel);
        labelHolder.setMaximumSize(new Dimension(760, Integer.MAX_VALUE));
        add(labelHolder);
        // instances[instanceCount++] = this;
        instances.add(this);
    }

    /**
     * Checks whether the user has selected a candidate for all officer
     * positions
     *
     * @return <code>true</code> if the user has selected a candidate for all
     * positions; <code>false</code> otherwise.
     */
    public static boolean allPositionsFilledIn() {
        for (CandidateSelection instance : instances) {
            if (instance.getSelectedCandidate() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the names of the candidates that the user has picked. Their index
     * in the array matches the index of the position they are in as listed on
     * the ballot page.
     *
     * @return A String array containing the selected candidates' names
     */
    public static String[] getSelectedCandidates() {
        String[] candidates = new String[instances.size()];
        for (int i = 0; i < instances.size(); i++) {
            CandidateSelection cs = instances.get(i);
            System.out.println(cs.getSelectedCandidate());
            candidates[i] = cs.getSelectedCandidate();
        }
        return candidates;
    }

    /**
     * Gets the candidate that the user has selected for the position
     * represented by the instance of this class that this method was called
     * from.
     *
     * @return The name of the currently selected candidate; <code>null</code>
     * if no candidate is selected.
     */
    public String getSelectedCandidate() {
        return selectedCandidate;
    }

    /**
     * Checks whether the user has picked a candidate from this instance of
     * <code>CandidateSelection</code>
     *
     * @return <code>true</code> if the user has selected one;
     * <code>false</code> otherwise.
     */
    public boolean hasSelectedCandidate() {
        for (JRadioButton button : buttons) {
            if (button.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resets this instance of <code>CandidateSelection</code> to its initial
     * (no candidate selected) state.
     */
    public void reset() {
        for (JRadioButton radioButton : buttons) {
            radioButton.setSelected(false);
        }
        descLabel.setText(null);
        selectedCandidate = null;
    }

    /**
     * Resets all instances of <code>CandidateSelection</code> to their initial
     * (no candidate selected) state.
     */
    public static void resetAll() {
        for (CandidateSelection cs : instances) {
            cs.reset();
        }
    }
}

