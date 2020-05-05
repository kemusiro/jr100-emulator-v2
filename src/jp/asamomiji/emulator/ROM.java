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
 * この抽象クラスは読み出し専用のメモリを表す。
 */
public abstract class ROM extends Memory {
    public ROM(int start, int length) {
        super(start, length);
    }

    public void store16(int address, short value) {
    }

    public void store8(int address, byte value) {
    }
}
