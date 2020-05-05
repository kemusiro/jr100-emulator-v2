/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import jp.asamomiji.emulator.Computer;

@SuppressWarnings("serial")
public class TakeSnapshotDialog extends JDialog {
    private Computer computer = null;
    private JPanel jContentPane = null;
    private JPanel P_main = null;
    private JPanel P_buttons = null;
    private JButton BT_OK = null;
    private JButton BT_cancel = null;
    private JLabel L_name = null;
    private JTextField TF_name = null;
    private JLabel L_comment = null;
    private JTextArea TA_comment = null;
    private JScrollPane SP_comment = null;
    /**
     * @param owner
     */
    public TakeSnapshotDialog(Computer computer, Frame owner) {
        super(owner);
        this.computer = computer;
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(437, 150);
        this.setTitle("スナップショットの採取");
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
            jContentPane.add(getP_main(), null);
            jContentPane.add(getP_buttons(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes P_main
     *
     * @return javax.swing.JPanel
     */
    private JPanel getP_main() {
        if (P_main == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = GridBagConstraints.BOTH;
            gridBagConstraints21.weighty = 1.0;
            gridBagConstraints21.gridx = 1;
            gridBagConstraints21.gridy = 2;
            gridBagConstraints21.weightx = 1.0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints2.gridy = 2;
            L_comment = new JLabel();
            L_comment.setText("コメント");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.NONE;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            L_name = new JLabel();
            L_name.setText("スナップショット名");
            P_main = new JPanel();
            P_main.setLayout(new GridBagLayout());
            P_main.add(L_name, gridBagConstraints);
            P_main.add(getTF_name(), gridBagConstraints1);
            P_main.add(L_comment, gridBagConstraints2);
            P_main.add(getSP_comment(), gridBagConstraints21);
        }
        return P_main;
    }

    /**
     * This method initializes P_buttons
     *
     * @return javax.swing.JPanel
     */
    private JPanel getP_buttons() {
        if (P_buttons == null) {
            P_buttons = new JPanel();
            P_buttons.setLayout(new BoxLayout(getP_buttons(), BoxLayout.X_AXIS));
            P_buttons.add(Box.createHorizontalGlue());
            P_buttons.add(getBT_OK(), null);
            P_buttons.add(getBT_cancel(), null);
        }
        return P_buttons;
    }

    /**
     * This method initializes BT_OK
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_OK() {
        if (BT_OK == null) {
            BT_OK = new JButton();
            BT_OK.setText("採取");
            BT_OK.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (TF_name.getText().equals("")) {
                        JOptionPane.showMessageDialog(
                            null,
                            "スナップショット名が空です。",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                        return ;
                    }
                    try {
                        computer.pause();
                        computer.getApplication().saveState(TF_name.getText(), TA_comment.getText());
                    }
                    catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                            return ;
                    }
                    catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                        return ;
                    }
                    computer.resume();
                    TakeSnapshotDialog.this.dispose();
                }
            });
        }
        return BT_OK;
    }

    /**
     * This method initializes BT_cancel
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_cancel() {
        if (BT_cancel == null) {
            BT_cancel = new JButton();
            BT_cancel.setText("キャンセル");
            BT_cancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    TakeSnapshotDialog.this.dispose();
                }
            });
        }
        return BT_cancel;
    }

    /**
     * This method initializes TF_name
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_name() {
        if (TF_name == null) {
            TF_name = new JTextField();
            TF_name.setColumns(13);
        }
        return TF_name;
    }

    /**
     * This method initializes TA_comment
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getTA_comment() {
        if (TA_comment == null) {
            TA_comment = new JTextArea();
            TA_comment.setBorder(new LineBorder(Color.black));
        }
        return TA_comment;
    }

    /**
     * This method initializes SP_comment
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getSP_comment() {
        if (SP_comment == null) {
            SP_comment = new JScrollPane();
            SP_comment.setViewportView(getTA_comment());
        }
        return SP_comment;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
