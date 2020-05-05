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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jp.asamomiji.emulator.Computer;

/*
 * 音に関する設定のためのダイアログを表示する。
 */
@SuppressWarnings("serial")
public class VolumeDialog extends JDialog {
    private Computer computer;
    private JPanel jContentPane = null;
    private JSlider jSlider = null;
    private JPanel jPanel = null;
    private JButton jButton = null;
    private JPanel jPanel1 = null;
    private JButton jButton1 = null;
    private JButton jButton2 = null;
    private JLabel L_value = null;
    private JTextField TF_value = null;

    /**
     * @param owner
     */
    public VolumeDialog(Computer computer, Frame owner) {
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
        this.setSize(422, 136);
        this.setPreferredSize(new Dimension(318, 117));
        this.setTitle("ボリューム設定");
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
            jContentPane.add(getJPanel1(), null);
            jContentPane.add(getJPanel(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes jSlider
     *
     * @return javax.swing.JSlider
     */
    private JSlider getJSlider() {
        if (jSlider == null) {
            jSlider = new JSlider();
            jSlider.setMajorTickSpacing(10);
            jSlider.setMinorTickSpacing(5);
            jSlider.setPaintLabels(true);
            jSlider.setPaintTicks(true);
            jSlider.setValue((int)(computer.getHardware().getSoundProcessor().getPropertyOfVolume() & 0xff));
            jSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    TF_value.setText(Integer.toString(jSlider.getValue()));
                }
            });
        }
        return jSlider;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.X_AXIS));
            jPanel.add(Box.createHorizontalGlue());
            jPanel.add(getJButton(), null);
            jPanel.add(getJButton1(), null);
            jPanel.add(getJButton2(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setText("OK");
            jButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    computer.getHardware().getSoundProcessor().setPropertyOfVolume((byte)jSlider.getValue());
                    VolumeDialog.this.dispose();
                }
            });
        }
        return jButton;
    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.NONE;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 0.0D;
            gridBagConstraints2.gridx = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.insets = new Insets(0, 10, 0, 5);
            gridBagConstraints1.gridy = 0;
            L_value = new JLabel();
            L_value.setText("値");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.setBorder(BorderFactory.createTitledBorder(null, "\u97f3\u306e\u5927\u304d\u3055", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanel1.add(getJSlider(), gridBagConstraints);
            jPanel1.add(L_value, gridBagConstraints1);
            jPanel1.add(getTF_value(), gridBagConstraints2);
        }
        return jPanel1;
    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setText("キャンセル");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    VolumeDialog.this.dispose();
                }
            });
        }
        return jButton1;
    }

    /**
     * This method initializes jButton2
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton2() {
        if (jButton2 == null) {
            jButton2 = new JButton();
            jButton2.setText("適用");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    computer.getHardware().getSoundProcessor().setPropertyOfVolume((byte)jSlider.getValue());
                }
            });
        }
        return jButton2;
    }

    private int getTextFieldValue() {
        int value = jSlider.getValue();
        try {
            value = Integer.parseInt(TF_value.getText());
            if (value < 0) {
                value = 0;
            }
            else if (value > 100) {
                value = 100;
            }
        }
        catch (NumberFormatException ex) {
        }
        return value;
    }

    /**
     * This method initializes TF_value
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_value() {
        if (TF_value == null) {
            TF_value = new JTextField(4);
            TF_value.setText(Integer.toString(computer.getHardware().getSoundProcessor().getPropertyOfVolume()));
            TF_value.setHorizontalAlignment(JTextField.RIGHT);
            TF_value.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    jSlider.setValue(getTextFieldValue());
                }
            });
            TF_value.addFocusListener(new java.awt.event.FocusListener() {
                public void focusLost(java.awt.event.FocusEvent e) {
                    jSlider.setValue(getTextFieldValue());
                }
                public void focusGained(java.awt.event.FocusEvent e) {
                }
            });
        }
        return TF_value;
    }

}  //  @jve:decl-index=0:visual-constraint="12,13"
