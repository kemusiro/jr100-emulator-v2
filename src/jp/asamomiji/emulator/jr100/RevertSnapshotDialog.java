/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import jp.asamomiji.emulator.Computer;

@SuppressWarnings("serial")
public class RevertSnapshotDialog extends JDialog {
    class SnapshotsTableModel extends DefaultTableModel {
        public SnapshotsTableModel(String[] coumn_names, int row_count) {
            super(coumn_names, row_count);
        }

        @Override
        public Class<? extends Object> getColumnClass(int column) {
            switch (column) {
            case 0:
                return JRadioButton.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            default:
                return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private Computer computer = null;
    private SnapshotsTableModel tableModel = null;
    private Vector<Hashtable<String, Object>> snapshots;  //  @jve:decl-index=0:
    private int selectedIndex;
    private JPanel jContentPane = null;
    private JPanel P_main = null;
    private JPanel P_buttons = null;
    private JButton BT_revert = null;
    private JButton BT_cancel = null;
    private JPanel P_snapshots = null;
    private JScrollPane SP_snapshots = null;
    private JTable TBL_snapshots = null;
    private JTextArea TA_comment = null;
    private JScrollPane SP_comment = null;
    /**
     * @param owner
     */
    public RevertSnapshotDialog(Computer computer, Frame owner) {
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
        this.setSize(384, 244);
        this.setTitle("スナップショットの復元");
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
            P_main = new JPanel();
            P_main.setLayout(new BoxLayout(getP_main(), BoxLayout.Y_AXIS));
            P_main.add(getP_snapshots(), null);
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
            P_buttons.add(getBT_revert(), null);
            P_buttons.add(getBT_cancel(), null);
        }
        return P_buttons;
    }

    /**
     * This method initializes BT_revert
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_revert() {
        if (BT_revert == null) {
            BT_revert = new JButton();
            BT_revert.setText("復元");
            BT_revert.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    File f = (File)snapshots.get(selectedIndex).get("file");
                    try {
                        computer.pause();
                        computer.getApplication().loadState(f.getAbsolutePath());
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
                    RevertSnapshotDialog.this.dispose();
                }
            });
        }
        return BT_revert;
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
                    RevertSnapshotDialog.this.dispose();
                }
            });
        }
        return BT_cancel;
    }

    /**
     * This method initializes P_snapshots
     *
     * @return javax.swing.JPanel
     */
    private JPanel getP_snapshots() {
        if (P_snapshots == null) {
            P_snapshots = new JPanel();
            P_snapshots.setLayout(new BoxLayout(getP_snapshots(), BoxLayout.Y_AXIS));
            P_snapshots.add(getSP_snapshots(), null);
            P_snapshots.add(getSP_comment(), null);
        }
        return P_snapshots;
    }

    /**
     * This method initializes SP_snapshots
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getSP_snapshots() {
        if (SP_snapshots == null) {
            SP_snapshots = new JScrollPane();
            SP_snapshots.setViewportView(getTBL_snapshots());
        }
        return SP_snapshots;
    }

    /**
     * This method initializes TBL_snapshots
     *
     * @return javax.swing.JTable
     */
    private JTable getTBL_snapshots() {
        if (TBL_snapshots == null) {
            String[] column_names = {"スナップショット名", "作成日"};
            tableModel = new SnapshotsTableModel(column_names, 0);
            TBL_snapshots = new JTable(tableModel);
            TBL_snapshots.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            TBL_snapshots.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return ;
                    }
                    selectedIndex = TBL_snapshots.getSelectedRow();
                    TA_comment.setText((String)snapshots.get(selectedIndex).get("comment"));
                    TA_comment.setCaretPosition(0);
                }
            });
        }
        snapshots = computer.getApplication().getSnapshotProperties();
        if (snapshots != null) {
            for (Hashtable<String, Object> d : snapshots) {
                tableModel.addRow(new Object[] {d.get("name"), DateFormat.getDateTimeInstance().format((Date)d.get("createdDate"))});
            }
        }
        return TBL_snapshots;
    }

    /**
     * This method initializes TA_comment
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getTA_comment() {
        if (TA_comment == null) {
            TA_comment = new JTextArea();
            // TA_comment.setRows(10);
            TA_comment.setEditable(false);
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
            SP_comment.setPreferredSize(new Dimension(120, 180));
            SP_comment.setViewportView(getTA_comment());
        }
        return SP_comment;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
