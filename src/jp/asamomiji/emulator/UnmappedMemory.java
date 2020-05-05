/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

public final class UnmappedMemory implements Addressable {
    // private static final long serialVersionUID = 6272322731939091623L;

    private int start;
    private int length;

    public UnmappedMemory(int start, int length) {
        this.start = start;
        this.length = length;
    }
    public int getStartAddress() {
        return start;
    }

    public int getEndAddress() {
        return start + length - 1;
    }

    public short load16(int address) {
        if (address == 0xd000) {
            return (short)0xaa00;
        }
        else {
            return 0;
        }
    }

    public byte load8(int address) {
        if (address == 0xd000) {
            return (byte)0xaa;
        }
        else {
            return 0;
        }
    }

    public void store16(int address, short value) {
        // do nothing
    }

    public void store8(int address, byte value) {
        // do nothing
    }
}
