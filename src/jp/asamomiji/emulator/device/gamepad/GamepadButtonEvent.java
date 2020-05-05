/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device.gamepad;

import net.java.games.input.Controller;

public class GamepadButtonEvent extends GamepadEvent {
    public final static int EVENT_PRESSED = 0;
    public final static int EVENT_RELEASED = 1;

    private int kind;
    private int buttonNumber;

    public GamepadButtonEvent(Controller controller, int kind, int number) {
        super(controller);
        this.kind = kind;
        this.buttonNumber = number;
    }

    public int getKind() {
        return kind;
    }

    public int getButtonNumber() {
        return buttonNumber;
    }
}
