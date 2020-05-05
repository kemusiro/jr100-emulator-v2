/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class BranchInstruction extends Instruction {
    private Instruction target = null;

    public BranchInstruction(int address) {
        super(address);
    }

    public BranchInstruction(int address, int mode, int operand) {
        super(address, mode, operand);
    }

    public Instruction getTarget() {
        return target;
    }

    public void setTarget(Instruction target) {
        this.target = target;
    }

    @Override
    public boolean isBranch() {
        return true;
    }

    @Override
    public String getOperandString() {
        if (target != null && target.getLabel() != null) {
            return target.getLabel().toString();
        }
        switch (mode) {
        case MODE_RELATIVE:
            return String.format("PC+0x%02X", (byte)operand);
        case MODE_INDEXED:
            return String.format("X+0x%02X", (byte)operand);
        case MODE_EXTENDED:
            return String.format("0x%04X", (short)operand);
        default:
            return "???";
        }
    }

    public int getTargetAddress() {
        switch (mode) {
        case MODE_IMMEDIATE:
            return operand;
        case MODE_EXTENDED:
            return operand;
        case MODE_RELATIVE:
            return address + (byte)operand + 2;
        default:
            return -1;
        }
    }
}
