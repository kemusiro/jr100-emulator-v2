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
 * このインターフェースはアドレスによりアクセス可能な要素に対して実行可能な
 * メソッドを定義する。
 */
public interface Addressable {
    public int getStartAddress();
    public int getEndAddress();
    public byte load8(int address);
    public void store8(int address, byte value);
    public short load16(int address);
    public void store16(int address, short value);
}
