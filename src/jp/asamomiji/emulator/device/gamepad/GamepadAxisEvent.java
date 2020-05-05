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

public class GamepadAxisEvent extends GamepadEvent {
    private int state;
    private int action;

    public GamepadAxisEvent(Controller controller, int state, int act) {
        super(controller);
        this.state = state;
        this.action = act;
    }

    public int getCurrentState() {
        return state;
    }

    public int getAction() {
        return action;
    }

}
