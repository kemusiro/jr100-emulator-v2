/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public abstract class NonBranchInstruction extends Instruction {
    public NonBranchInstruction(int address) {
        super(address);
    }

    public NonBranchInstruction(int address, int mode, int operand) {
        super(address, mode, operand);
    }

    @Override
    public String getOperandString() {
        switch (mode) {
        case MODE_IMMEDIATE:
            return String.format("0x%02X", (byte)operand);
        case MODE_DIRECT:
            return String.format("[0x%02X]", (byte)operand);
        case MODE_INDEXED:
            return String.format("[X+0x%02X]", (byte)operand);
        case MODE_EXTENDED:
            return String.format("[0x%04X]", (short)operand);
        case MODE_IMPLIED:
            return "";
        default:
            return "???";
        }
    }

    @Override
    public boolean isBranch() {
        return false;
    }
}
