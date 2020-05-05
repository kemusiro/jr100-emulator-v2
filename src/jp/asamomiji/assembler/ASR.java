/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class ASR extends NonBranchInstruction {
    public ASR(int address, int mode, int operand) {
        super(address, mode, operand);
        if (mode == MODE_IMMEDIATE || mode == MODE_DIRECT) {
            throw new IllegalArgumentException();
        }
        mnemonic = "ASR";
    }
}
