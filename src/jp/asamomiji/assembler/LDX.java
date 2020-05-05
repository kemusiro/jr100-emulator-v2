/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class LDX extends NonBranchInstruction {
    public LDX(int address, int mode, int operand) {
        super(address, mode, operand);
        mnemonic = "LDX";
    }

    @Override
    public String getOperandString() {
        switch (mode) {
        case MODE_IMMEDIATE:
            return String.format("0x%04X", operand);
        default:
            return super.getOperandString();
        }
    }

    @Override
    public int getLength() {
        switch (mode) {
        case MODE_IMMEDIATE:
            return 3;
        default:
            return super.getLength();
        }
    }
}
