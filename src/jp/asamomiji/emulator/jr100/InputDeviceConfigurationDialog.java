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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.device.gamepad.AxisDialogKeyProcessor;
import jp.asamomiji.emulator.device.gamepad.AxisKeyboardKeyProcessor;
import jp.asamomiji.emulator.device.gamepad.ButtonDialogKeyProcessor;
import jp.asamomiji.emulator.device.gamepad.ButtonKeyboardKeyProcessor;
import jp.asamomiji.emulator.device.gamepad.ComponentStatus;
import jp.asamomiji.emulator.device.gamepad.Gamepad;
import jp.asamomiji.emulator.device.gamepad.KeyProcessor;
import net.java.games.input.Component;
import net.java.games.input.Controller;

public class InputDeviceConfigurationDialog extends JDialog {
    private Computer computer;
    private Gamepad gamepad;
    private int selected_axis;
    private final JPanel contentPanel = new JPanel();
    private JCheckBox checkBox_use_gamepad;
    private ArrayList<Controller> foundControllers;
    private JComboBox<Controller> comboBox_devices;
    private JRadioButton radioButton_keyboard;
    private JRadioButton radioButton_native;
    private ButtonGroup buttonGroup_mapping_mode = new ButtonGroup();
    private JRadioButton rdbtn_xy;
    private JRadioButton rdbtn_rxry;
    private JRadioButton rdbtn_zrz;
    private JRadioButton rdbtn_pov;
    private ButtonGroup buttonGroup_axes_used = new ButtonGroup();
    private JPanel panel_actualbuttons;

    private AxisDialogKeyProcessor axisProcessor = null;
    private ButtonDialogKeyProcessor buttonProcessor = null;
    private JButton[] button_axes = new JButton[Gamepad.GAMEPAD_NUMBER_OF_DIRECTIONS];
    private JButton[] button_buttons = new JButton[Gamepad.GAMEPAD_MAX_NUMBER_OF_BUTTONS];
    private ComponentStatus[] axisMapping;
    private ComponentStatus[] buttonMapping;
    private boolean saveduseGamepad;
    private KeyProcessor savedAxisKeyProcessor;
    private KeyProcessor savedButtonKeyProcessor;
    private int savedAxisUsed;
    private Controller savedController;

    private HashMap<JButton, ComponentStatus> componentStatusMap = new HashMap<>();
    private JTextField textField_button_number;
    private JTextField textField_button_keycode;
    private final Action actionOk = new SwingActionOk();
    private final Action actionCancel = new SwingActionCancel();
    private JTextField textField_axis_direction;
    private JTextField textField_axis_keycode;
    private final Action actionApply = new SwingActionApply();

    private class RoundButton extends JButton {
        protected Shape base;
        protected Shape shape;
        protected Shape border;
        protected final Color fc = new Color(100, 150, 255, 200);
        protected final Color ac = new Color(230, 230, 230);
        protected final Color rc = Color.ORANGE;
        protected final static int focusstroke = 2;
        public RoundButton() {
            super();
            setFocusPainted(true);
            initShape();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            // d.width = d.height = Math.max(d.width, d.height);
            d.width = d.height = 30;
            return d;
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBackground(new Color(250, 250, 250));
            initShape();
        }

        protected void initShape() {
            if (!getBounds().equals(base)) {
                base = getBounds();
                shape = new Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
                border =new Ellipse2D.Float(
                        focusstroke,
                        focusstroke,
                        getWidth() - 1 - focusstroke * 2,
                        getHeight() - 1 - focusstroke * 2);
            }
        }

        private void paintFocusAndRollover(Graphics2D g2, Color color) {
            g2.setPaint(new GradientPaint(
                    0, 0, color, getWidth() - 1, getHeight() - 1, color.brighter(), true));
            g2.fill(shape);
            g2.setColor(getBackground());
            g2.fill(border);
        }

        @Override
        protected void paintComponent(Graphics g) {
            initShape();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isArmed()) {
                g2.setColor(ac);
                g2.fill(shape);
            } else if (isRolloverEnabled() && getModel().isRollover()) {
                paintFocusAndRollover(g2, rc);
            } else if (hasFocus()) {
                paintFocusAndRollover(g2, fc);
            } else {
                g2.setColor(getBackground());
                g2.fill(shape);
            }
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(getBackground());
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            initShape();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getForeground());
            g2.draw(shape);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            initShape();
            return shape == null ? false : shape.contains(x, y);
        }
    }

    private class KeyAdapterForButton extends KeyAdapter {
        private AbstractButton parent;
        private ComponentStatus status;
        private JTextField keytext;
        KeyAdapterForButton(AbstractButton parent, ComponentStatus status, JTextField keytext) {
            this.parent = parent;
            this.status = status;
            this.keytext = keytext;
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (parent.hasFocus()) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    status.setStatus(ComponentStatus.STATUS_UNASSIGNED);
                    status.setKeyCode(KeyEvent.VK_UNDEFINED);
                    parent.setText("未割当");
                    keytext.setText("未割当");
                }
                else if(e.getKeyCode() != KeyEvent.VK_ALT && status.getStatus() != ComponentStatus.STATUS_UNUSED) {
                    status.setKeyCode(e.getKeyCode());
                    parent.setText(KeyEvent.getKeyText(e.getKeyCode()));
                    keytext.setText(KeyEvent.getKeyText(e.getKeyCode()));
                }

            }
        }
    }

    /**
     * Create the dialog.
     */
    public InputDeviceConfigurationDialog(Computer computer, Frame owner) {
        this.computer = computer;
        gamepad = computer.getHardware().getGamepad();
        savedAxisKeyProcessor = gamepad.getAxisKeyProcessor();
        saveduseGamepad = Gamepad.getPropertyOfGamepadUse();
        savedButtonKeyProcessor = gamepad.getButtonKeyProcessor();
        savedAxisUsed = Gamepad.getPropertyOfGamepadAxisUsed();
        savedController = gamepad.getController();

        setTitle("入力デバイスの設定");
        setBounds(100, 100, 724, 535);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        axisMapping = Gamepad.getPropertyOfGamepadAxisMapping();
        buttonMapping = Gamepad.getPropertyOfGamepadButtonMapping();

        checkBox_use_gamepad = new JCheckBox("ゲームパッドを使用する。");
        checkBox_use_gamepad.setSelected(Gamepad.getPropertyOfGamepadUse());

        JLabel label_device = new JLabel("デバイス");
        JScrollPane scrollPane = new JScrollPane();
        JLabel label_mapping = new JLabel("マッピングモード");
        label_mapping.setVisible(false);

        radioButton_keyboard = new JRadioButton("キーボードマッピング");
        radioButton_keyboard.setVisible(false);

        radioButton_native = new JRadioButton("ネイティブマッピング");
        radioButton_native.setVisible(false);

        buttonGroup_mapping_mode.add(radioButton_keyboard);
        buttonGroup_mapping_mode.add(radioButton_native);
        radioButton_keyboard.setSelected(true);

        JPanel panel_axis = new JPanel();
        panel_axis.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "軸", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panel__button = new JPanel();
        panel__button.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "ボタン", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JTextArea txtrAltbackspace = new JTextArea();
        txtrAltbackspace.setEditable(false);
        txtrAltbackspace.setLineWrap(true);
        txtrAltbackspace.setText("Altを押しながらクリックすると未使用状態と使用状態を選択できます。Backspaceキーを押すとキーが未割り当て状態になります。");
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(txtrAltbackspace, 0, 0, Short.MAX_VALUE)
                        .addComponent(panel_axis, GroupLayout.PREFERRED_SIZE, 330, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                    .addComponent(panel__button, GroupLayout.PREFERRED_SIZE, 338, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(checkBox_use_gamepad)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addComponent(label_device)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 412, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addComponent(label_mapping)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(radioButton_keyboard)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(radioButton_native)))
                    .addContainerGap(244, Short.MAX_VALUE))
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(checkBox_use_gamepad)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_device, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_mapping)
                        .addComponent(radioButton_keyboard)
                        .addComponent(radioButton_native))
                    .addPreferredGap(ComponentPlacement.RELATED, 213, Short.MAX_VALUE)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addComponent(panel_axis, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtrAltbackspace, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
                        .addComponent(panel__button, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );

        panel_actualbuttons = new JPanel();

        JPanel panel_1 = new JPanel();
        GroupLayout gl_panel__button = new GroupLayout(panel__button);
        gl_panel__button.setHorizontalGroup(
            gl_panel__button.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel__button.createSequentialGroup()
                    .addGroup(gl_panel__button.createParallelGroup(Alignment.TRAILING)
                        .addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel_actualbuttons, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 314, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        gl_panel__button.setVerticalGroup(
            gl_panel__button.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel__button.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel_actualbuttons, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                    .addGap(18))
        );
        panel_actualbuttons.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JLabel label_1 = new JLabel("キー割り当て");

        JLabel label_2 = new JLabel("ボタン");

        textField_button_number = new JTextField();
        textField_button_number.setEditable(false);
        textField_button_number.setColumns(10);

        JLabel label_3 = new JLabel("キー");

        textField_button_keycode = new JTextField();
        textField_button_keycode.setEditable(false);
        textField_button_keycode.setColumns(10);
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(
            gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addComponent(label_1)
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(label_2)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textField_button_number, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(label_3)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textField_button_keycode, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(151, Short.MAX_VALUE))
        );
        gl_panel_1.setVerticalGroup(
            gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addComponent(label_1)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_2)
                        .addComponent(textField_button_number, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_3)
                        .addComponent(textField_button_keycode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(26, Short.MAX_VALUE))
        );
        panel_1.setLayout(gl_panel_1);
        panel__button.setLayout(gl_panel__button);

        JPanel panel_stick = new JPanel();
        panel_stick.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "\u4F7F\u3046\u8EF8", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

        JPanel panel_padbuttons = new JPanel();

        JLabel label = new JLabel("ボタン");

        textField_axis_direction = new JTextField();
        textField_axis_direction.setEditable(false);
        textField_axis_direction.setColumns(10);

        JLabel label_4 = new JLabel("キー");

        textField_axis_keycode = new JTextField();
        textField_axis_keycode.setEditable(false);
        textField_axis_keycode.setColumns(10);
        GroupLayout gl_panel_axis = new GroupLayout(panel_axis);
        gl_panel_axis.setHorizontalGroup(
            gl_panel_axis.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_axis.createSequentialGroup()
                    .addComponent(panel_stick, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_axis.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_axis.createSequentialGroup()
                            .addComponent(label)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textField_axis_direction, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(label_4)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textField_axis_keycode, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                        .addComponent(panel_padbuttons, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE))
                    .addGap(26))
        );
        gl_panel_axis.setVerticalGroup(
            gl_panel_axis.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_axis.createSequentialGroup()
                    .addGroup(gl_panel_axis.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel_stick, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                        .addComponent(panel_padbuttons, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_panel_axis.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label)
                        .addComponent(textField_axis_direction, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_4)
                        .addComponent(textField_axis_keycode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );

        panel_padbuttons.setLayout(new GridLayout(0, 3, 0, 0));

        rdbtn_xy = new JRadioButton("x/y");
        rdbtn_xy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected_axis = Gamepad.AXIS_XY;
                gamepad.setAxisType(Gamepad.AXIS_XY);
                axisProcessor.recalcMapping();
            }
        });

        rdbtn_rxry = new JRadioButton("rx/ry");
        rdbtn_rxry.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected_axis = Gamepad.AXIS_RXRY;
                gamepad.setAxisType(Gamepad.AXIS_RXRY);
                axisProcessor.recalcMapping();
            }
        });

        rdbtn_zrz = new JRadioButton("z/rz");
        rdbtn_zrz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected_axis = Gamepad.AXIS_ZRZ;
                gamepad.setAxisType(Gamepad.AXIS_ZRZ);
                axisProcessor.recalcMapping();
            }
        });

        rdbtn_pov = new JRadioButton("POV");
        rdbtn_pov.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selected_axis = Gamepad.AXIS_POV;
                gamepad.setAxisType(Gamepad.AXIS_POV);
                axisProcessor.recalcMapping();
            }
        });

        buttonGroup_axes_used.add(rdbtn_xy);
        buttonGroup_axes_used.add(rdbtn_rxry);
        buttonGroup_axes_used.add(rdbtn_zrz);
        buttonGroup_axes_used.add(rdbtn_pov);
        switch (Gamepad.getPropertyOfGamepadAxisUsed()) {
        case Gamepad.AXIS_XY:
            rdbtn_xy.setSelected(true);
            gamepad.setAxisType(Gamepad.AXIS_XY);
            break;
        case Gamepad.AXIS_RXRY:
            rdbtn_rxry.setSelected(true);
            gamepad.setAxisType(Gamepad.AXIS_RXRY);
            break;
        case Gamepad.AXIS_ZRZ:
            rdbtn_zrz.setSelected(true);
            gamepad.setAxisType(Gamepad.AXIS_ZRZ);
            break;
        case Gamepad.AXIS_POV:
            rdbtn_pov.setSelected(true);
            gamepad.setAxisType(Gamepad.AXIS_POV);
            break;
        default:
            rdbtn_xy.setSelected(true);
            gamepad.setAxisType(Gamepad.AXIS_XY);
            break;
        }

        GroupLayout gl_panel_stick = new GroupLayout(panel_stick);
        gl_panel_stick.setHorizontalGroup(
            gl_panel_stick.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_stick.createSequentialGroup()
                    .addGroup(gl_panel_stick.createParallelGroup(Alignment.LEADING)
                        .addComponent(rdbtn_xy)
                        .addComponent(rdbtn_rxry)
                        .addComponent(rdbtn_zrz)
                        .addComponent(rdbtn_pov))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_panel_stick.setVerticalGroup(
            gl_panel_stick.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_stick.createSequentialGroup()
                    .addComponent(rdbtn_xy)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn_rxry)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn_zrz)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtn_pov)
                    .addContainerGap(15, Short.MAX_VALUE))
        );
        panel_stick.setLayout(gl_panel_stick);
        panel_axis.setLayout(gl_panel_axis);
        {
            comboBox_devices = new JComboBox<Controller>();
            scrollPane.setViewportView(comboBox_devices);
            comboBox_devices.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    updateControllerProperty(foundControllers.get(comboBox_devices.getSelectedIndex()));
                }
            });
        }
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setAction(actionOk);
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setAction(actionCancel);
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }

            JButton applyButton = new JButton("適用");
            applyButton.setAction(actionApply);
            buttonPane.add(applyButton);
        }

        /*
         * ボタンの描画と状態の設定
         */

        for (int i = 0; i < Gamepad.GAMEPAD_NUMBER_OF_DIRECTIONS; i++) {
            if (i ==4) {
                JLabel label_center = new JLabel("〇");
                label_center.setHorizontalAlignment(SwingConstants.CENTER);
                panel_padbuttons.add(label_center);
            }
            button_axes[i] = new JButton();
            button_axes[i].setMargin(new Insets(0, 0, 0, 0));
            if (axisMapping[i].getStatus() == ComponentStatus.STATUS_UNUSED) {
                button_axes[i].setBackground(Color.DARK_GRAY);
            }
            else {
                button_axes[i].setBackground(Color.LIGHT_GRAY);
            }
            button_axes[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AbstractButton button = (JButton)e.getSource();
                    ComponentStatus status = componentStatusMap.get(button);
                    int modifiers = e.getModifiers();
                    switch (status.getIndex()) {
                    case Gamepad.DIR_UP_LEFT:
                        textField_axis_direction.setText("左上");
                        break;
                    case Gamepad.DIR_UP:
                        textField_axis_direction.setText("上");
                        break;
                    case Gamepad.DIR_UP_RIGHT:
                        textField_axis_direction.setText("右上");
                        break;
                    case Gamepad.DIR_LEFT:
                        textField_axis_direction.setText("左");
                        break;
                    case Gamepad.DIR_RIGHT:
                        textField_axis_direction.setText("右");
                        break;
                    case Gamepad.DIR_DOWN_LEFT:
                        textField_axis_direction.setText("左下");
                        break;
                    case Gamepad.DIR_DOWN:
                        textField_axis_direction.setText("下");
                        break;
                    case Gamepad.DIR_DOWN_RIGHT:
                        textField_axis_direction.setText("右下");
                        break;
                    default:
                        break;
                    }
                    textField_axis_keycode.setText(status.getKeyText());
                    if ((modifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK) {
                        if (status.getStatus() != ComponentStatus.STATUS_UNUSED) {
                            status.setStatus(ComponentStatus.STATUS_UNUSED);
                            button.setBackground(Color.DARK_GRAY);
                        }
                        else {
                            status.setStatus(ComponentStatus.STATUS_UNASSIGNED);
                            button.setBackground(Color.LIGHT_GRAY);
                        }
                        axisProcessor.recalcMapping();
                    }
                }
            });
            button_axes[i].addKeyListener(new KeyAdapterForButton(button_axes[i], axisMapping[i], textField_axis_keycode));
            componentStatusMap.put(button_axes[i], axisMapping[i]);
            panel_padbuttons.add(button_axes[i]);
        }

        for (int i = 0; i < Gamepad.GAMEPAD_MAX_NUMBER_OF_BUTTONS; i++) {
            buttonMapping[i].setIndex(i);
            button_buttons[i] = new RoundButton();
            button_buttons[i].setEnabled(false);
            panel_actualbuttons.add(button_buttons[i]);
            button_buttons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AbstractButton button = (JButton)e.getSource();
                    ComponentStatus status = componentStatusMap.get(button);
                    int modifiers = e.getModifiers();
                    textField_button_number.setText(Integer.toString(status.getIndex()));
                    textField_button_keycode.setText(status.getKeyText());
                    if ((modifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK) {
                        if (status.getStatus() != ComponentStatus.STATUS_UNUSED) {
                            status.setStatus(ComponentStatus.STATUS_UNUSED);
                            button.setBackground(Color.DARK_GRAY);
                        }
                        else {
                            status.setStatus(ComponentStatus.STATUS_UNASSIGNED);
                            button.setBackground(Color.LIGHT_GRAY);
                        }
                    }
                }
            });

            button_buttons[i].addKeyListener(new KeyAdapterForButton(button_buttons[i], buttonMapping[i], textField_button_keycode));
            componentStatusMap.put(button_buttons[i], buttonMapping[i]);

        }

        foundControllers = gamepad.scanControllers();
        if (foundControllers.isEmpty()) {
            checkBox_use_gamepad.setEnabled(false);
        }
        else {
            boolean selected = false;
            for (Controller c : foundControllers) {
                comboBox_devices.addItem(c);
                if (c.getName().equals(Gamepad.getPropertyOfGamepadName())) {
                    comboBox_devices.setSelectedItem(c);
                    selected = true;
                }
            }
            if (!selected) {
                comboBox_devices.setSelectedIndex(0);
                selected = true;
            }
            Controller controller = foundControllers.get(comboBox_devices.getSelectedIndex());
            updateControllerProperty(controller);
        }
    }

    private void updateControllerProperty(Controller controller) {
        int button_count = 0;
        boolean has_x = false;
        boolean has_y = false;
        boolean has_rx = false;
        boolean has_ry = false;
        boolean has_z = false;
        boolean has_rz = false;
        boolean has_pov = false;

        Component[] components = controller.getComponents();
        for (int i = 0; i < components.length; i++) {
            // System.out.println(components[i].getIdentifier().getClass().toString() + " " + components[i].getIdentifier().getName() + " " + components[i].getName().toString());
            if (components[i].getIdentifier().getName().equals("x")) {
                has_x = true;
            }
            else if (components[i].getIdentifier().getName().equals("y")) {
                has_y = true;
            }
            else if (components[i].getIdentifier().getName().equals("rx")) {
                has_rx = true;
            }
            else if (components[i].getIdentifier().getName().equals("ry")) {
                has_ry = true;
            }
            else if (components[i].getIdentifier().getName().equals("z")) {
                has_z = true;
            }
            else if (components[i].getIdentifier().getName().equals("rz")) {
                has_rz = true;
            }
            else if (components[i].getIdentifier().getName().equals("rz")) {
                has_rz = true;
            }
            else if (components[i].getIdentifier().getName().equals("pov")) {
                has_pov = true;
            }
            else if (components[i].getIdentifier() instanceof Component.Identifier.Button) {
                button_count++;
            }
        }
        if (has_x && has_y) {
            rdbtn_xy.setEnabled(true);
        }
        else {
            rdbtn_xy.setEnabled(false);
        }
        if (has_rx && has_ry) {
            rdbtn_rxry.setEnabled(true);
        }
        else {
            rdbtn_rxry.setEnabled(false);
        }
        if (has_z && has_rz) {
            rdbtn_zrz.setEnabled(true);
        }
        else {
            rdbtn_zrz.setEnabled(false);
        }
        if (has_pov) {
            rdbtn_pov.setEnabled(true);
        }
        else {
            rdbtn_pov.setEnabled(false);
        }

        for (int i = 0; i < Gamepad.GAMEPAD_NUMBER_OF_DIRECTIONS; i++) {
            button_axes[i].setText(axisMapping[i].getKeyText());
        }
        axisProcessor = new AxisDialogKeyProcessor(gamepad, axisMapping, button_axes);
        gamepad.setAxisKeyProcessor(axisProcessor);


        for (int i = 0; i < Gamepad.GAMEPAD_MAX_NUMBER_OF_BUTTONS; i++) {
            ComponentStatus status = componentStatusMap.get(button_buttons[i]);
            if (i < button_count) {
                button_buttons[i].setEnabled(true);
                button_buttons[i].setVisible(true);
                button_buttons[i].setBackground(Color.LIGHT_GRAY);
            }
            else {
                button_buttons[i].setEnabled(false);
                button_buttons[i].setVisible(false);
                // button_buttons[i].setBackground(Color.DARK_GRAY);
                // status.setStatus(ComponentStatus.STATUS_UNUSED);
            }
            button_buttons[i].setText(status.getKeyText());
        }

        buttonProcessor = new ButtonDialogKeyProcessor(gamepad, buttonMapping, button_buttons);
        gamepad.setButtonKeyProcessor(buttonProcessor);
        gamepad.restart(controller);
    }

    private boolean applyAll() {
        boolean use_gamepad = checkBox_use_gamepad.isSelected();
        if (use_gamepad && foundControllers.size() == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "ゲームパッドが見つかりません。",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Gamepad.setPropertyOfGamepadUse(use_gamepad);
        if (!use_gamepad) {
            gamepad.restart(null);
            return true;
        }
        // ゲームパッドを使うときのみ以降のプロパティをセットする。
        Controller c = foundControllers.get(comboBox_devices.getSelectedIndex());
        Gamepad.setPropertyOfGamepadName(c.getName());

        if (radioButton_keyboard.isSelected()) {
            Gamepad.setPropertyOfGamepadMappingMode(Gamepad.MODE_KEYBOARD);
        }
        else {
            Gamepad.setPropertyOfGamepadMappingMode(Gamepad.MODE_NATIVE);
        }
        Gamepad.setPropertyOfGamepadAxisUsed(selected_axis);
        Gamepad.setPropertyOfGamepadAxisMapping(axisMapping);
        Gamepad.setPropertyOfGamepadButtonMapping(buttonMapping);

        gamepad.setAxisKeyProcessor(new AxisKeyboardKeyProcessor(gamepad, axisMapping, computer.getHardware().getKeyboard()));
        gamepad.setButtonKeyProcessor(new ButtonKeyboardKeyProcessor(gamepad, buttonMapping, computer.getHardware().getKeyboard()));

        gamepad.restart(c);

        return true;
    }
    private class SwingActionOk extends AbstractAction {
        public SwingActionOk() {
            putValue(NAME, "Ok");
            putValue(SHORT_DESCRIPTION, "設定を有効にして閉じる。");
        }
        public void actionPerformed(ActionEvent e) {
            applyAll();
            InputDeviceConfigurationDialog.this.dispose();        }
    }
    private class SwingActionCancel extends AbstractAction {
        public SwingActionCancel() {
            putValue(NAME, "キャンセル");
            putValue(SHORT_DESCRIPTION, "設定を無効にして閉じる。");
        }
        public void actionPerformed(ActionEvent e) {
            Gamepad.setPropertyOfGamepadUse(saveduseGamepad);
            gamepad.setAxisKeyProcessor(savedAxisKeyProcessor);
            gamepad.setButtonKeyProcessor(savedButtonKeyProcessor);
            gamepad.setAxisType(savedAxisUsed);
            gamepad.restart(savedController);
            InputDeviceConfigurationDialog.this.dispose();
        }
    }
    private class SwingActionApply extends AbstractAction {
        public SwingActionApply() {
            putValue(NAME, "適用");
            putValue(SHORT_DESCRIPTION, "設定を有効にする。");
        }
        public void actionPerformed(ActionEvent e) {
            applyAll();
            savedAxisKeyProcessor = gamepad.getAxisKeyProcessor();
            saveduseGamepad = Gamepad.getPropertyOfGamepadUse();
            savedButtonKeyProcessor = gamepad.getButtonKeyProcessor();
            savedAxisUsed = Gamepad.getPropertyOfGamepadAxisUsed();
            savedController = gamepad.getController();

            Controller controller = foundControllers.get(comboBox_devices.getSelectedIndex());
            updateControllerProperty(controller);
        }
    }
}
