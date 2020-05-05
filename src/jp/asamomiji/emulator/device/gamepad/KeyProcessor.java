/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */

package jp.asamomiji.emulator.device.gamepad;

public abstract class KeyProcessor {
    protected Gamepad gamepad;
    /*
     * エミュレータ内部のイベントIDをJavaキーコードに変換するための対応表
     */
    protected ComponentStatus[] mapper;

    public KeyProcessor(Gamepad gamepad, ComponentStatus[] mapper) {
        this.gamepad = gamepad;
        this.mapper = mapper;
    }

    public abstract void execute(int state, int action);

    public abstract void press(ComponentStatus status);

    public abstract void release(ComponentStatus status);
}
