/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device.gamepad;

import jp.asamomiji.emulator.AbstractKeyboard;

public class ButtonKeyboardKeyProcessor extends ButtonKeyProcessor {
    private AbstractKeyboard keyboard;

    public ButtonKeyboardKeyProcessor(Gamepad gamepad, ComponentStatus[] mapper, AbstractKeyboard keyboard) {
        super(gamepad, mapper);
        this.keyboard = keyboard;
    }

    @Override
    public void press(ComponentStatus status) {
        keyboard.keyPressed(status.getKeyCode());
    }

    @Override
    public void release(ComponentStatus status) {
        keyboard.keyReleased(status.getKeyCode());
    }
}
