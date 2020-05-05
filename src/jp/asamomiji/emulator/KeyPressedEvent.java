/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;


/*
 * キーが押されたことを示すイベント
 */
public class KeyPressedEvent extends EmulatorEvent {
    private int keycode;

    public KeyPressedEvent(long clock, int keycode) {
        super(clock);
        this.keycode = keycode;
    }

    @Override
    public void dispatch(Computer computer) {
        computer.getHardware().getKeyboard().keyPressed(keycode);
    }
}
