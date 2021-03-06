/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class ADCA extends NonBranchInstruction {
    public ADCA(int address, int mode, int operand) {
        super(address, mode, operand);
        mnemonic = "ADCA";
    }
}
