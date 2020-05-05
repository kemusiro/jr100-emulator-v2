/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device.gamepad;

import java.awt.Color;

import javax.swing.AbstractButton;

public class ButtonDialogKeyProcessor extends ButtonKeyProcessor {
    private AbstractButton[] buttons;

    public ButtonDialogKeyProcessor(Gamepad gamepad, ComponentStatus[] mapper, AbstractButton[] buttons) {
        super(gamepad, mapper);
        this.buttons = buttons;
    }

    @Override
    public void press(ComponentStatus status) {
        if (status.getStatus() == ComponentStatus.STATUS_UNUSED) {
            return ;
        }
        int index = status.getIndex();
        if (index < buttons.length && buttons[index] != null) {
            buttons[index].setBackground(Color.ORANGE);
        }
    }

    @Override
    public void release(ComponentStatus status) {
        if (status.getStatus() == ComponentStatus.STATUS_UNUSED) {
            return ;
        }
        int index = status.getIndex();
        if (index < buttons.length && buttons[index] != null) {
            buttons[index].setBackground(Color.LIGHT_GRAY);
        }
    }
}
