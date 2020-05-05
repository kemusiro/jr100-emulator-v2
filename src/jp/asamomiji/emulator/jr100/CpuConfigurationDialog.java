/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import jp.asamomiji.emulator.Computer;

public class CpuConfigurationDialog extends JDialog {
    @SuppressWarnings("unused")
    private Computer computer;

    private final JPanel contentPanel = new JPanel();
    private ButtonGroup btnGroup = new ButtonGroup();
    private JTextField txtCustomclock;
    private final Action action = new SwingAction();
    private final Action action_1 = new SwingAction_1();

    /**
     * Create the dialog.
     */
    public CpuConfigurationDialog(Computer computer, Frame owner) {
        this.computer = computer;
        setTitle("CPUの設定");
        setBounds(100, 100, 463, 330);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblcpu = new JLabel("実行中のCPU速度");

        JLabel lblCurrentclock = new JLabel("current_clock");
        int clock = computer.getClockFrequency();
        if (clock < 1000) {
            lblCurrentclock.setText(Integer.toString(computer.getClockFrequency()) + "Hz");
        }
        else if (clock < 1000000) {
            lblCurrentclock.setText(String.format("%.3f", (double)computer.getClockFrequency() / 1000) + "kHz");
        }
        else {
            lblCurrentclock.setText(String.format("%.6f", (double)computer.getClockFrequency() / 1000000) + "MHz");
        }

        JLabel lblCpu = new JLabel("CPU速度");

        JRadioButton rdbtn100khz = new JRadioButton("100kHz");
        rdbtn100khz.setActionCommand("100000");

        JRadioButton rdbtn500khz = new JRadioButton("500kHz");
        rdbtn500khz.setActionCommand("500000");

        JRadioButton rdbtn894khz = new JRadioButton("894kHz (デフォルト)");
        rdbtn894khz.setActionCommand("894000");

        JRadioButton rdbtn1mhz = new JRadioButton("1MHz");
        rdbtn1mhz.setActionCommand("1000000");

        JRadioButton rdbtn2mhz = new JRadioButton("2MHz");
        rdbtn2mhz.setActionCommand("2000000");

        JRadioButton rdbtn5mhz = new JRadioButton("5MHz");
        rdbtn5mhz.setActionCommand("5000000");

        JRadioButton rdbtn10mhz = new JRadioButton("10MHz");
        rdbtn10mhz.setActionCommand("10000000");

        JRadioButton rdbtncustom = new JRadioButton("カスタム...");
        rdbtncustom.setActionCommand("custom");

        btnGroup.add(rdbtn100khz);
        btnGroup.add(rdbtn500khz);
        btnGroup.add(rdbtn894khz);
        btnGroup.add(rdbtn1mhz);
        btnGroup.add(rdbtn2mhz);
        btnGroup.add(rdbtn5mhz);
        btnGroup.add(rdbtn10mhz);
        btnGroup.add(rdbtncustom);
        rdbtn894khz.setSelected(true);

        txtCustomclock = new JTextField();
        txtCustomclock.setText("894000");
        txtCustomclock.setColumns(10);

        JLabel lblHz_1 = new JLabel("Hz");

        JLabel lbljroffon = new JLabel("設定を有効にするためにJR-100の電源をOFFにしてからONにしてください。");

        JLabel lblNewLabel = new JLabel("(100000(100kHz)～10000000(10MHz))");
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addComponent(lblcpu)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lblCurrentclock, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addComponent(lblCpu)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(rdbtn500khz)
                                .addComponent(rdbtn100khz)
                                .addComponent(rdbtn894khz)
                                .addComponent(rdbtn1mhz)
                                .addComponent(rdbtn2mhz)
                                .addComponent(rdbtn5mhz)
                                .addComponent(rdbtn10mhz)
                                .addGroup(gl_contentPanel.createSequentialGroup()
                                    .addComponent(rdbtncustom)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                        .addComponent(lblNewLabel)
                                        .addGroup(gl_contentPanel.createSequentialGroup()
                                            .addComponent(txtCustomclock, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(lblHz_1, GroupLayout.PREFERRED_SIZE, 138, Short.MAX_VALUE))))))
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(lbljroffon)))
                    .addGap(95))
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblcpu)
                        .addComponent(lblCurrentclock))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblCpu)
                        .addComponent(rdbtn100khz))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn500khz)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn894khz)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn1mhz)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn2mhz)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn5mhz)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn10mhz)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(rdbtncustom)
                        .addComponent(txtCustomclock, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblHz_1))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblNewLabel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lbljroffon)
                    .addContainerGap(44, Short.MAX_VALUE))
        );
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton();
                okButton.setAction(action_1);
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton();
                cancelButton.setAction(action);
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
    private class SwingAction extends AbstractAction {
        public SwingAction() {
            putValue(NAME, "キャンセル");
            putValue(SHORT_DESCRIPTION, "設定を無効にして閉じる。");
        }
        public void actionPerformed(ActionEvent e) {
            CpuConfigurationDialog.this.dispose();
        }
    }
    private class SwingAction_1 extends AbstractAction {
        public SwingAction_1() {
            putValue(NAME, "OK");
            putValue(SHORT_DESCRIPTION, "設定を有効にして閉じる。");
        }
        public void actionPerformed(ActionEvent evt) {
            int new_clock;
            String s = btnGroup.getSelection().getActionCommand();
            if (s.equals("custom")) {
                s = txtCustomclock.getText();
            }
            try {
                new_clock = Integer.parseInt(s);
                if (new_clock <= 0 || new_clock > 10000000) {
                    JOptionPane.showMessageDialog(
                            null,
                            "100000(100kHz)～10000000(10MHz)の間の数値を指定してください",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    return ;
                }
            }
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "不正な数値です: " + s,
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                return ;
            }
            JR100.setPropertyOfCpuClockFrequency(new_clock);
            CpuConfigurationDialog.this.dispose();
        }
    }
}
