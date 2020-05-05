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
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import jp.asamomiji.assembler.Disassembler;
import jp.asamomiji.emulator.Computer;

public class DisassembleDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private Computer computer = null;
    private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="10,10"
    private JScrollPane SP_main = null;
    private JTextArea TA_main = null;
    private JPanel P_option = null;
    private JLabel L_address = null;
    private JTextField TF_start = null;
    private JTextField TF_end = null;
    private JButton BT_execute = null;

    /**
     * @param owner
     */
    public DisassembleDialog(Computer computer, Frame owner) {
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
        this.setSize(350, 457);
        this.setTitle("逆アセンブル");
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
            jContentPane.setSize(new Dimension(265, 272));
            jContentPane.add(getSP_main(), null);
            jContentPane.add(getP_option(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes SP_main
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getSP_main() {
        if (SP_main == null) {
            SP_main = new JScrollPane();
            SP_main.setViewportView(getTA_main());
        }
        return SP_main;
    }

    /**
     * This method initializes TA_main
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getTA_main() {
        if (TA_main == null) {
            TA_main = new JTextArea();
            TA_main.setFont(new Font("Monospaced",  Font.PLAIN, 12));
            TA_main.setEditable(false);
        }
        return TA_main;
    }

    /**
     * This method initializes P_option
     *
     * @return javax.swing.JPanel
     */
    private JPanel getP_option() {
        if (P_option == null) {
            L_address = new JLabel();
            L_address.setText("アドレス");
            P_option = new JPanel();
            P_option.setLayout(new BoxLayout(getP_option(), BoxLayout.X_AXIS));
            P_option.add(L_address, null);
            P_option.add(getTF_start(), null);
            P_option.add(getTF_end(), null);
            P_option.add(getBT_execute(), null);
        }
        return P_option;
    }

    /**
     * This method initializes TF_start
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_start() {
        if (TF_start == null) {
            TF_start = new JTextField();
            TF_start.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        }
        return TF_start;
    }

    /**
     * This method initializes TF_end
     *
     * @return javax.swing.JTextField
     */
    private JTextField getTF_end() {
        if (TF_end == null) {
            TF_end = new JTextField();
            TF_end.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        }
        return TF_end;
    }

    /**
     * This method initializes BT_execute
     *
     * @return javax.swing.JButton
     */
    private JButton getBT_execute() {
        if (BT_execute == null) {
            BT_execute = new JButton();
            BT_execute.setText("実行");
            BT_execute.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int start = Integer.parseInt(TF_start.getText(), 16);
                    int end = Integer.parseInt(TF_end.getText(), 16);
                    Disassembler da = new Disassembler(computer.getHardware().getMemory(), start, end);
                    da.execute();
                    TA_main.setText(da.print());
                    TA_main.setCaretPosition(0);
                }
            });
        }
        return BT_execute;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
