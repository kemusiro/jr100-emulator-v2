/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import jp.asamomiji.emulator.RAM;
import jp.asamomiji.emulator.StateSavable;
import jp.asamomiji.emulator.StateSet;

/*
 * このクラスはユーザ定義文字のメモリ領域を定義する。
 */
public class UserDefinedCharacterRam extends RAM implements StateSavable {
    private JR100Display display;

    public UserDefinedCharacterRam(int start, int length) {
        super(start, length);
    }

    public void setDisplay(JR100Display display) {
        this.display = display;
    }

    @Override
    public void store8(int address, byte value) {
        data[address - start] = value;
        display.updateFont((address - start) / 8, (address - start) % 8, value);
    }

    @Override
    public void store16(int address, short value) {
        data[address - start] = (byte)(value >> 8);
        display.updateFont((address - start) / 8, (address - start) % 8, value >> 8);
        data[address - getStartAddress() + 1] = (byte)(value & 0xff);
        display.updateFont((address - start) / 8, (address - start) % 8, value & 0xff);
    }

    public void saveState(StateSet ss) {
        ss.set("UserDefinedCharacterRam.start_addr", start);
        ss.set("UserDefinedCharacterRam.length", length);
        ss.set("UserDefinedCharacterRam.data", data);
    }

    public void loadState(StateSet ss) {
        start = (Integer)ss.get("UserDefinedCharacterRam.start_addr");
        length = (Integer)ss.get("UserDefinedCharacterRam.length");
        data = (byte[])ss.get("UserDefinedCharacterRam.data");
    }
}
