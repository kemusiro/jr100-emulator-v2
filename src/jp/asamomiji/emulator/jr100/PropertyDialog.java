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
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import jp.asamomiji.emulator.Application;
import jp.asamomiji.emulator.Computer;

/*
 * このダイアログからJR-100エミュレータの動作に関する各種プロパティを設定する。
 */
public class PropertyDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private Computer computer;
    private JPanel jContentPane = null;
    private JPanel jPanel = null;
    private JPanel jPanel1 = null;
    private JButton BT_OK = null;
    private JButton BT_cancel = null;
    private JButton BT_apply = null;
    private JTextField TF_datafolder = null;
    private JPanel jPanel2 = null;
    private JButton BT_referdatafolder = null;
    private JCheckBox CB_autopoweron = null;
    private JPanel jPanel4 = null;
    private JCheckBox CB_validateextendedram = null;
    private JRadioButton RB_defaultmap = null;
    private JRadioButton RB_mapfile = null;
    private JTextField TF_mapfile = null;
    private JLabel L_colormap = null;
    private JButton BT_refermapfile = null;
    private JLabel L_datafolder = null;
    private JCheckBox CB_used3d = null;
    private JPanel P_snapshot = null;
    private JLabel L_snapshot = null;
    private JTextField TF_snapshot = null;
    private JButton BT_refersnapshot = null;
    private JLabel L_extension = null;
    private JTextField TF_extension = null;

    /**
     * @param owner
     */
    public PropertyDialog(Computer computer, Frame owner) {
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
        this.setSize(536, 359);
        this.setTitle("プロパティ");
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
            jContentPane.add(getJPanel(), null);
            jContentPane.add(getJPanel1(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.Y_AXIS));
            jPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            jPanel.add(getJPanel2(), null);
            jPanel.add(getP_snapshot(), null);
            jPanel.add(getJPanel4(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new JPanel();
            jPanel1.setLayout(new BoxLayout(getJPanel1(), BoxLayout.X_AXIS));
            jPanel1.add(Box.createHorizontalGlue());
            jPanel1.add(getBT_OK(), null);
            jPanel1.add(getBT_cancel(), null);
            jPanel1.add(getBT_apply(), null);
        }
        return jPanel1;
    }

    private void applyAll() {
        Application.setPropertyOfDataFolder(getTF_datafolder().getText());
        Application.setPropertyOfSnapshotFolder(getTF_snapshot().getText());
        Application.setPropertyOfSnapshotExtension(getTF_extension().getText());
        if (getRB_defaultmap().isSelected()) {
            computer.getHardware().getDisplay().setDisplayColorMap(JR100Display.COLORMAP_DEFAULT, null);
        }
        else {
            computer.getHardware().getDisplay().setDisplayColorMap(JR100Display.COLORMAP_FILE, getTF_mapfile().getText());
        }
        Computer.setPropertyOfAutomaticPowerOn(getCB_autopoweron().isSelected());
        JR100.setPropertyOfUseExtendedRam(getCB_validateextendedram().isSelected());
        JR100Application.setPropertyOfD3dUsed(CB_used3d.isSelected());
    }

    /**
     * This method initializes BT_OK
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_OK() {
        if (BT_OK == null) {
            BT_OK = new JButton();
            BT_OK.setText("OK");
            BT_OK.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    applyAll();
                    PropertyDialog.this.dispose();
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
                    PropertyDialog.this.dispose();
                }
            });
        }
        return BT_cancel;
    }

    /**
     * This method initializes BT_apply
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_apply() {
        if (BT_apply == null) {
            BT_apply = new JButton();
            BT_apply.setText("適用");
            BT_apply.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    applyAll();
                }
            });
        }
        return BT_apply;
    }

    /**
     * This method initializes TF_datafolder
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_datafolder() {
        if (TF_datafolder == null) {
            TF_datafolder = new JTextField();
            TF_datafolder.setText(Application.getPropertyOfDataFolder());
        }
        return TF_datafolder;
    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel2() {
        if (jPanel2 == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridwidth = 4;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridy = 4;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridwidth = 4;
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.gridy = 3;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 0;
            L_datafolder = new JLabel();
            L_datafolder.setText("データフォルダ");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 3;
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridx = 2;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridy = 2;
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridy = 1;
            gridBagConstraints13.anchor = GridBagConstraints.WEST;
            gridBagConstraints13.gridx = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.gridy = 1;
            L_colormap = new JLabel();
            L_colormap.setText("カラーマップ");
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 3;
            gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints1.anchor = GridBagConstraints.CENTER;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.weightx = 1.0;
            jPanel2 = new JPanel();
            jPanel2.setLayout(new GridBagLayout());
            jPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "\u8d77\u52d5", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanel2.add(L_datafolder, gridBagConstraints5);
            jPanel2.add(getTF_datafolder(), gridBagConstraints);
            jPanel2.add(getBT_referdatafolder(), gridBagConstraints1);
            jPanel2.add(L_colormap, gridBagConstraints12);
            jPanel2.add(getRB_defaultmap(), gridBagConstraints13);
            jPanel2.add(getRB_mapfile(), gridBagConstraints21);
            jPanel2.add(getTF_mapfile(), gridBagConstraints3);
            jPanel2.add(getBT_refermapfile(), gridBagConstraints4);
            jPanel2.add(getCB_autopoweron(), gridBagConstraints6);
            jPanel2.add(getCB_used3d(), gridBagConstraints2);
            ButtonGroup bg = new ButtonGroup();
            bg.add(getRB_defaultmap());
            bg.add(getRB_mapfile());
        }
        return jPanel2;
    }

    /**
     * This method initializes BT_referdatafolder
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_referdatafolder() {
        if (BT_referdatafolder == null) {
            BT_referdatafolder = new JButton();
            BT_referdatafolder.setText("参照...");
            BT_referdatafolder.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser fc = new JFileChooser(Application.getPropertyOfDataFolder());
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int selected = fc.showOpenDialog(PropertyDialog.this);
                    if (selected == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        TF_datafolder.setText(file.getAbsolutePath());
                    }
                }
            });
        }
        return BT_referdatafolder;
    }

    /**
     * This method initializes CB_autopoweron
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCB_autopoweron() {
        if (CB_autopoweron == null) {
            CB_autopoweron = new JCheckBox();
            CB_autopoweron.setText("起動時に電源をオンにする。");
            CB_autopoweron.setSelected(Computer.getPropertyOfAutomaticPowerOn());
        }
        return CB_autopoweron;
    }

    /**
     * This method initializes jPanel4
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel4() {
        if (jPanel4 == null) {
            jPanel4 = new JPanel();
            jPanel4.setLayout(new BoxLayout(getJPanel4(), BoxLayout.X_AXIS));
            jPanel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "\u62e1\u5f35", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanel4.add(getCB_validateextendedram(), null);
            jPanel4.add(Box.createHorizontalGlue());
        }
        return jPanel4;
    }

    /**
     * This method initializes CB_validateextendedram
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCB_validateextendedram() {
        if (CB_validateextendedram == null) {
            CB_validateextendedram = new JCheckBox();
            CB_validateextendedram.setText("拡張RAMを有効にする(次回電源オン時に有効)。");
            CB_validateextendedram.setSelected(JR100.getPropertyOfUseExtendedRam());
        }
        return CB_validateextendedram;
    }

    /**
     * This method initializes RB_defaultmap
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRB_defaultmap() {
        if (RB_defaultmap == null) {
            RB_defaultmap = new JRadioButton();
            RB_defaultmap.setText("デフォルト");
            RB_defaultmap.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (RB_defaultmap.isSelected()) {
                        getTF_mapfile().setEnabled(false);
                        getBT_refermapfile().setEnabled(false);
                    }
                }
            });
            if (computer.getHardware().getDisplay().getPropertyOfDisplayColorMap() == JR100Display.COLORMAP_DEFAULT) {
                RB_defaultmap.setSelected(true);
            }
        }
        return RB_defaultmap;
    }

    /**
     * This method initializes RB_mapfile
     *
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getRB_mapfile() {
        if (RB_mapfile == null) {
            RB_mapfile = new JRadioButton();
            RB_mapfile.setText("ファイルから");
            RB_mapfile.setSelected(true);
            RB_mapfile.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (RB_mapfile.isSelected()) {
                        getTF_mapfile().setEnabled(true);
                        getBT_refermapfile().setEnabled(true);
                    }
                }
            });
        }
        return RB_mapfile;
    }

    /**
     * This method initializes TF_mapfile
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_mapfile() {
        if (TF_mapfile == null) {
            TF_mapfile = new JTextField();
            TF_mapfile.setText(computer.getHardware().getDisplay().getPropertyOfDisplayColorMapFile());
        }
        return TF_mapfile;
    }

    /**
     * This method initializes BT_refermapfile
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_refermapfile() {
        if (BT_refermapfile == null) {
            BT_refermapfile = new JButton();
            BT_refermapfile.setText("参照...");
            BT_refermapfile.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser fc = new JFileChooser(Application.getPropertyOfDataFolder());
                    int selected = fc.showOpenDialog(PropertyDialog.this);
                    if (selected == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        TF_mapfile.setText(file.getAbsolutePath());
                    }
                }
            });
        }
        return BT_refermapfile;
    }

    /**
     * This method initializes CB_used3d
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCB_used3d() {
        if (CB_used3d == null) {
            CB_used3d = new JCheckBox();
            CB_used3d.setText("Direct3Dパイプラインを有効にする(次回エミュレータ起動時から有効)。");
            CB_used3d.setSelected(JR100Application.getPropertyOfD3dUsed());
        }
        return CB_used3d;
    }

    /**
     * This method initializes P_snapshot
     *
     * @return javax.swing.JPanel
     */
    private JPanel getP_snapshot() {
        if (P_snapshot == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.anchor = GridBagConstraints.WEST;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints10.gridy = 1;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.gridy = 1;
            L_extension = new JLabel();
            L_extension.setText("拡張子");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 2;
            gridBagConstraints8.gridy = 0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.gridx = 1;
            L_snapshot = new JLabel();
            L_snapshot.setText("スナップショットフォルダ");
            P_snapshot = new JPanel();
            P_snapshot.setLayout(new GridBagLayout());
            P_snapshot.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "\u30b9\u30ca\u30c3\u30d7\u30b7\u30e7\u30c3\u30c8", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            P_snapshot.add(L_snapshot, gridBagConstraints14);
            P_snapshot.add(getTF_snapshot(), gridBagConstraints7);
            P_snapshot.add(getBT_refersnapshot(), gridBagConstraints8);
            P_snapshot.add(L_extension, gridBagConstraints9);
            P_snapshot.add(getTF_extension(), gridBagConstraints10);
        }
        return P_snapshot;
    }

    /**
     * This method initializes TF_snapshot
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_snapshot() {
        if (TF_snapshot == null) {
            TF_snapshot = new JTextField();
            TF_snapshot.setText(Application.getPropertyOfSnapshotFolder());
        }
        return TF_snapshot;
    }

    /**
     * This method initializes BT_refersnapshot
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_refersnapshot() {
        if (BT_refersnapshot == null) {
            BT_refersnapshot = new JButton();
            BT_refersnapshot.setText("参照...");
            BT_refersnapshot.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JFileChooser fc = new JFileChooser(Application.getPropertyOfSnapshotFolder());
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int selected = fc.showOpenDialog(PropertyDialog.this);
                    if (selected == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        TF_snapshot.setText(file.getAbsolutePath());
                    }
                }
            });
        }
        return BT_refersnapshot;
    }

    /**
     * This method initializes TF_extension
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_extension() {
        if (TF_extension == null) {
            TF_extension = new JTextField();
            TF_extension.setColumns(8);
            TF_extension.setText(Application.getPropertyOfSnapshotExtension());
        }
        return TF_extension;
    }

}  //  @jve:decl-index=0:visual-constraint="10,-11"
