/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class BVS extends BranchInstruction {
    public BVS(int address, int operand) {
        super(address, Instruction.MODE_RELATIVE, operand);
        mnemonic = "BVS";
    }
}
