/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public abstract class Instruction {
    public static final int MODE_IMMEDIATE = 0;
    public static final int MODE_DIRECT = 1;
    public static final int MODE_INDEXED = 2;
    public static final int MODE_EXTENDED = 3;
    public static final int MODE_IMPLIED = 4;
    public static final int MODE_RELATIVE = 5;

    protected int address = 0;
    protected int mode = 0;
    protected int operand = 0;
    protected String mnemonic = null;
    protected Label label = null;
    protected boolean isBranchTarget = false;

    protected Instruction(int address) {
        this.address = address;
        this.mode = MODE_IMPLIED;
        this.operand = 0;
    }
    protected Instruction(int address, int mode, int operand) {
        this.address = address;
        this.mode = mode;
        this.operand = operand;
    }

    public int getAddress() {
        return address;
    }

    public int getMode() {
        return mode;
    }

    public int getOperand() {
        return operand;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    abstract public String getOperandString();

    abstract public boolean isBranch();

    public boolean isBranchTarget() {
        return isBranchTarget;
    }

    public void setBranchTarget() {
        isBranchTarget = true;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public int getLength() {
        switch (mode) {
        case MODE_IMMEDIATE:
            return 2;
        case MODE_DIRECT:
            return 2;
        case MODE_INDEXED:
            return 2;
        case MODE_EXTENDED:
            return 3;
        case MODE_IMPLIED:
            return 1;
        case MODE_RELATIVE:
            return 2;
        default:
            return 1;
        }
    }
}
