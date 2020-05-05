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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * エミュレータについてダイアログ
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {
    private Frame owner = null;
    private JPanel jContentPane = null;
    private JLabel jLabel = null;
    private JButton jButton = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    /**
     * This is the default constructor
     */
    public AboutDialog(Frame frame) {
        super(frame, true);
        this.owner = frame;
        initialize();
    }
    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        Point p = owner.getLocationOnScreen();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        this.setResizable(false);
        this.setTitle("JR100エミュレータについて");
        this.setSize(300, 142);

        int x = p.x + 10;
        int y = p.y + 10;

        if (x + this.getSize().width > d.width) {
            x = d.width - this.getSize().width;
        }
        if (y + this.getSize().height > d.height) {
            y = d.height - this.getSize().height;
        }
        this.setLocation(x, y);

        this.setContentPane(getJContentPane());
    }
    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if(jContentPane == null) {
            jLabel2 = new JLabel();
            jLabel1 = new JLabel();
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            jLabel = new JLabel();
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new GridBagLayout());
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weighty = 0.2D;
            jLabel.setText("JR100Emulator " + JR100Application.REVISION_STRING);
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 3;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.gridy = 1;
            gridBagConstraints11.weighty = 0.2D;
            jLabel1.setText(JR100Application.COPYRIGHT_STRING);
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.gridy = 2;
            gridBagConstraints21.weighty = 0.2D;
            jLabel2.setText("(kemusiro@asamomiji.jp)");
            jContentPane.add(jLabel, gridBagConstraints1);
            jContentPane.add(getJButton(), gridBagConstraints2);
            jContentPane.add(jLabel1, gridBagConstraints11);
            jContentPane.add(jLabel2, gridBagConstraints21);
        }
        return jContentPane;
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
                    dispose();
                }
            });
        }
        return jButton;
    }
}
