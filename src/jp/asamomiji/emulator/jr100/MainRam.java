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
 * このクラスは主記憶領域を定義する。
 * 主記憶領域は読み書きが自由な領域である。
 */
public final class MainRam extends RAM implements StateSavable {
    public MainRam(int start, int length) {
        super(start, length);
    }

    public void saveState(StateSet ss) {
        ss.set("MainRam.start_addr", start);
        ss.set("MainRam.length", length);
        ss.set("MainRam.data", data);
    }

    public void loadState(StateSet ss) {
        start = (Integer)ss.get("MainRam.start_addr");
        length = (Integer)ss.get("MainRam.length");
        data = (byte[])ss.get("MainRam.data");
    }
}
