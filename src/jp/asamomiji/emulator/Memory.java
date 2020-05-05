/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

/**
 * このクラスはアドレス範囲を持ち各アドレスにデータを保存することのできる
 * 汎用的なメモリを実装する。
 * メモリアドレスに対して特別な意味を持たせる場合は、このクラスを継承して
 * load,store命令をオーバーライドする。
 *
 */
public class Memory implements Addressable {
    protected byte[] data = null;
    protected int start;
    protected int length;

    public Memory(int start, int length) {
        this.start = start;
        this.length = length;
        data = new byte[length];
    }
    public int getStartAddress() {
        return start;
    }

    public int getEndAddress() {
        return start + length - 1;
    }

    public byte load8(int address) {
        return data[address - start];
    }

    public short load16(int address) {
        byte val1 = data[address - start];
        byte val2 = data[address - start + 1];
        return (short)((val1 << 8) + val2);
    }

    public void store8(int address, byte value) {
        data[address - start] = value;
    }

    public void store16(int address, short value) {
        data[address - start] = (byte)(value >> 8);
        data[address - start + 1] = (byte)(value & 0xff);
    }
}
