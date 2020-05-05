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
 * キーが話されたことを示すイベント
 */
public class KeyReleasedEvent extends EmulatorEvent {
    private int keycode;

    public KeyReleasedEvent(long clock, int keycode) {
        super(clock);
        this.keycode = keycode;
    }

    @Override
    public void dispatch(Computer computer) {
        computer.getHardware().getKeyboard().keyReleased(keycode);
    }
}
