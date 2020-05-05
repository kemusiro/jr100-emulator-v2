/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device.gamepad;

public abstract class ButtonKeyProcessor extends KeyProcessor {

    public ButtonKeyProcessor(Gamepad gamepad, ComponentStatus[] mapper) {
        super(gamepad, mapper);
    }

    /**
     * ボタン状態を変更する。
     *
     * @param state 現在の状態(ボタンを押したイベントの場合はGamepad.BUTTON_RELEASED)
     * @param action ボタン番号
     */
    @Override
    public void execute(int state, int action) {
        if (state == Gamepad.BUTTON_RELEASED) {
            press(mapper[action]);
        }
        else {
            release(mapper[action]);
        }
    }

}
