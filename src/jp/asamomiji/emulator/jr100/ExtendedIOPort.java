/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import jp.asamomiji.emulator.Addressable;
import jp.asamomiji.emulator.Computer;

public class ExtendedIOPort implements Addressable {
    protected Computer computer;

    private int startAddr;
    private int endAddr;

    private byte gamepadStatus = 0;

    public ExtendedIOPort(Computer computer, int start) {
        this.computer = computer;
        startAddr = start;
        endAddr = start + 0x3ff;
    }

    public int getStartAddress() {
        return startAddr;
    }

    public int getEndAddress() {
        return endAddr;
    }

    public byte load8(int address) {
        // System.out.println("load8: " + Integer.toHexString(address) + " : " + gamepadStatus);
        if (address == 0xcc02) {
            return gamepadStatus;
        }
        else {
            return 0;
        }
    }

    public void store8(int address, byte value) {
        // System.out.println("store8: " + Integer.toHexString(address) + " : " + gamepadStatus);
        if (address == 0xcc02) {
            // System.out.println(value);
            gamepadStatus = value;
        }
    }

    public short load16(int address) {
        // System.out.println("load16: " + Integer.toHexString(address) + " : " + gamepadStatus);
        if (address == 0xcc01) {
            return (short)(gamepadStatus & 0x00ff);
        }
        else if (address == 0xcc02) {
            return (short)((gamepadStatus << 8) & 0xff00);
        }
        return 0;
    }

    public void store16(int address, short value) {
    }
}
