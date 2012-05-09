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

/*
 * FinalizeVoterDialog.java
 *
 * Created on Jul 5, 2009, 10:58:16 AM
 */
package scelect.client.gui;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import scelect.client.core.*;

/**
 *
 * @author Calulo
 */
public class FinalizeVoterDialog extends javax.swing.JDialog {

    DefaultListModel listModel;
    boolean isLocked = false;
    private String instructions = "Select your name, then click the button on the right.";

    /** Creates new form FinalizeVoterDialog */
    public FinalizeVoterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        listModel = new DefaultListModel();
        initComponents();
        centerDialog();
    }

    /**
     * <code>centerDialog</code> centers this dialog over its parent.
     */
    void centerDialog() {
        Frame parent = (Frame) getParent();
        Point loc = parent.getLocation();
        int xLoc = (loc.x + (parent.getWidth() / 2)) - (getWidth() / 2);
        int yLoc = (loc.y + (parent.getHeight() / 2)) - (getHeight() / 2);
        setLocation(xLoc, yLoc);
    }

    void onTextChanged() {
        String password = new String(pwFieldFinal.getPassword());
        if (!password.equals("")) {
            addPwButton.setEnabled(true);
        } else {
            addPwButton.setEnabled(false);
        }
    }

    public void updateMatches(List<String> list) {
        listModel.removeAllElements();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            listModel.addElement(list.get(i));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        nameMatchesList = new javax.swing.JList();
        pwFieldFinal = new javax.swing.JPasswordField();
        addPwButton = new javax.swing.JButton();
        lockButton = new javax.swing.JButton();
        helpText = new javax.swing.JLabel();
        helpText.setText(instructions);
        jSeparator2 = new javax.swing.JSeparator();
        pwFieldInitial = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Voter Registration");
        setMinimumSize(new java.awt.Dimension(444, 245));
        setResizable(false);

        jLabel2.setText("Name:");

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        nameMatchesList.setModel(listModel);
        nameMatchesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(nameMatchesList);

        pwFieldFinal.setEditable(false);
        pwFieldFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pwFieldFinalKeyReleased(evt);
            }
        });

        addPwButton.setText("Add Password");
        addPwButton.setEnabled(false);
        addPwButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPwButtonActionPerformed(evt);
            }
        });

        lockButton.setText("This is my name");
        lockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockButtonActionPerformed(evt);
            }
        });

        helpText.setText("Select your name, then click the button on the right.");

        pwFieldInitial.setEditable(false);

        jLabel1.setText("Password:");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Retype:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addPwButton)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwFieldFinal, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                            .addComponent(pwFieldInitial, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(helpText, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lockButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lockButton)
                    .addComponent(helpText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwFieldInitial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(pwFieldFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addPwButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pwFieldFinalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pwFieldFinalKeyReleased
        onTextChanged();
    }//GEN-LAST:event_pwFieldFinalKeyReleased

    private void addPwButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPwButtonActionPerformed
        String selectedName = (String) nameMatchesList.getSelectedValue();

        // Is a name selected?
        if (selectedName == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a name first.",
                    "Voter Registration",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        String pwInitial = new String(pwFieldFinal.getPassword());
        String pwFinal = new String(pwFieldInitial.getPassword());

        // Are both password fields filled in?
        if (pwInitial.equals("") || pwFinal.equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your password in both password fields.",
                    "Voter Registration",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Do the passwords in both fields match?
        if (!pwInitial.equals(pwFinal)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the same password in both password fields.",
                    "Voter Registration",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        /* TODO: Work on serverside manual verification so we don't have to use
        two dialogs to slow down mischievous people... */
        int result = JOptionPane.showConfirmDialog(this,
                "<HTML>" +
                "Please make sure that you selected the right name. You " +
                "picked: " +
                "<H1>" + selectedName + "</H1>" +
                "Do you wish to continue?" +
                "</HTML>",
                "Voter Registration",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) { // User clicked "Yes"
            SwingWorker worker = new FinalizeVoterWorker(this,
                    (String) nameMatchesList.getSelectedValue(),
                    pwFieldFinal.getPassword());
            worker.execute();
            dispose();
        }
    }//GEN-LAST:event_addPwButtonActionPerformed

    /**
     * This method toggles the state of the name selection lock.
     * @param evt
     */
    private void lockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockButtonActionPerformed
        if (!isLocked) {
            isLocked = true;
            String selectedName = (String) nameMatchesList.getSelectedValue();
            if (selectedName == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a name.",
                        "Voter Registration",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            helpText.setText("<HTML><B>" + selectedName + "</B></HTML>");
            lockSearchUI();
        } else {
            isLocked = false;
            unlockSearchUI();
        }
    }//GEN-LAST:event_lockButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        search();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        search();
    }//GEN-LAST:event_nameFieldActionPerformed

    /**
     * Creates a SwingWorker that will query the server for names that match
     * the text in the name field.
     */
    private void search() {
        SwingWorker worker = new FinalizeVoterWorker(this, nameField.getText());
        worker.execute();
    }

    /**
     * Locks the search UI, forcing the user's attention onto the password
     * fields.
     */
    private void lockSearchUI() {
        nameField.setEditable(false);
        searchButton.setEnabled(false);
        nameMatchesList.setEnabled(false);
        addPwButton.setEnabled(true);
        pwFieldInitial.setEditable(true);
        pwFieldFinal.setEditable(true);
        lockButton.setText("This is not my name");
        pwFieldInitial.requestFocusInWindow();
    }

    private void unlockSearchUI() {
        helpText.setText(instructions);
        nameField.setEditable(true);
        searchButton.setEnabled(true);
        nameMatchesList.setEnabled(true);
        addPwButton.setEnabled(false);
        pwFieldInitial.setEditable(false);
        pwFieldFinal.setEditable(false);
        lockButton.setText("This is my name");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Should anything be done here?
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                FinalizeVoterDialog dialog =
                        new FinalizeVoterDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPwButton;
    private javax.swing.JLabel helpText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton lockButton;
    private javax.swing.JTextField nameField;
    private javax.swing.JList nameMatchesList;
    private javax.swing.JPasswordField pwFieldFinal;
    private javax.swing.JPasswordField pwFieldInitial;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables
}
