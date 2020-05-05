/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import jp.asamomiji.emulator.MemorySystem;

public class Disassembler {
    private Vector<Instruction> instructions = new Vector<Instruction>();
    private byte[] data;
    private int startAddress;
    private int endAddress;

    public Disassembler(byte[] data, int start_address, int end_address) {
        this.data = data;
        this.startAddress = start_address;
        this.endAddress = end_address;
    }

    public Disassembler(MemorySystem m, int start_address, int end_address) {
        this.data = new byte[end_address - start_address + 3];
        this.startAddress = start_address;
        this.endAddress = end_address;
        for (int i = 0; i < data.length; i++) {
            data[i] = m.load8(startAddress + i);
        }
    }

    private int getOperand1(int index) {
        if (index >= data.length) {
            return 0;
        }
        else {
            return data[index];
        }
    }

    private int getOperand2(int index) {
        if (index + 1 >= data.length) {
            return 0;
        }
        else {
            return ((data[index] & 0xff) << 8) + (data[index + 1] & 0xff);
        }
    }

    private void disassemble() {
        Instruction inst;
        int i = 0;
        while (startAddress + i < endAddress) {
            int a = startAddress + i;
            switch ((int)(data[i] & 0xff)) {
            case 0x01:
                inst = new NOP(a);
                break;
            case 0x06:
                inst = new TAP(a);
                break;
            case 0x07:
                inst = new TPA(a);
                break;
            case 0x08:
                inst = new INX(a);
                break;
            case 0x09:
                inst = new DEX(a);
                break;
            case 0x0A:
                inst = new CLV(a);
                break;
            case 0x0B:
                inst = new SEV(a);
                break;
            case 0x0C:
                inst = new CLC(a);
                break;
            case 0x0D:
                inst = new SEC(a);
                break;
            case 0x0E:
                inst = new CLI(a);
                break;
            case 0x0F:
                inst = new SEI(a);
                break;
            case 0x10:
                inst = new SBA(a);
                break;
            case 0x11:
                inst = new CBA(a);
                break;
            case 0x16:
                inst = new TAB(a);
                break;
            case 0x17:
                inst = new TAB(a);
                break;
            case 0x19:
                inst = new DAA(a);
                break;
            case 0x1B:
                inst = new ABA(a);
                break;
            case 0x20:
                inst = new BRA(a, getOperand1(i + 1));
                break;
            case 0x22:
                inst = new BHI(a, getOperand1(i + 1));
                break;
            case 0x23:
                inst = new BLS(a, getOperand1(i + 1));
                break;
            case 0x24:
                inst = new BCC(a, getOperand1(i + 1));
                break;
            case 0x25:
                inst = new BCS(a, getOperand1(i + 1));
                break;
            case 0x26:
                inst = new BNE(a, getOperand1(i + 1));
                break;
            case 0x27:
                inst = new BEQ(a, getOperand1(i + 1));
                break;
            case 0x28:
                inst = new BVC(a, getOperand1(i + 1));
                break;
            case 0x29:
                inst = new BVS(a, getOperand1(i + 1));
                break;
            case 0x2A:
                inst = new BPL(a, getOperand1(i + 1));
                break;
            case 0x2B:
                inst = new BMI(a, getOperand1(i + 1));
                break;
            case 0x2C:
                inst = new BGE(a, getOperand1(i + 1));
                break;
            case 0x2D:
                inst = new BLT(a, getOperand1(i + 1));
                break;
            case 0x2E:
                inst = new BGT(a, getOperand1(i + 1));
                break;
            case 0x2F:
                inst = new BLE(a, getOperand1(i + 1));
                break;
            case 0x30:
                inst = new TSX(a);
                break;
            case 0x31:
                inst = new INS(a);
                break;
            case 0x32:
                inst = new PULA(a);
                break;
            case 0x33:
                inst = new PULB(a);
                break;
            case 0x34:
                inst = new DES(a);
                break;
            case 0x35:
                inst = new TXS(a);
                break;
            case 0x36:
                inst = new PSHA(a);
                break;
            case 0x37:
                inst = new PSHB(a);
                break;
            case 0x39:
                inst = new RTS(a);
                break;
            case 0x3B:
                inst = new RTI(a);
                break;
            case 0x3E:
                inst = new WAI(a);
                break;
            case 0x3F:
                inst = new SWI(a);
                break;
            case 0x40:
                inst = new NEGA(a);
                break;
            case 0x43:
                inst = new COMA(a);
                break;
            case 0x44:
                inst = new LSRA(a);
                break;
            case 0x46:
                inst = new RORA(a);
                break;
            case 0x47:
                inst = new ASRA(a);
                break;
            case 0x48:
                inst = new ASLA(a);
                break;
            case 0x49:
                inst = new ROLA(a);
                break;
            case 0x4A:
                inst = new DECA(a);
                break;
            case 0x4C:
                inst = new INCA(a);
                break;
            case 0x4D:
                inst = new TSTA(a);
                break;
            case 0x4F:
                inst = new CLRA(a);
                break;
            case 0x50:
                inst = new NEGB(a);
                break;
            case 0x53:
                inst = new COMB(a);
                break;
            case 0x54:
                inst = new LSRB(a);
                break;
            case 0x56:
                inst = new RORB(a);
                break;
            case 0x57:
                inst = new ASRB(a);
                break;
            case 0x58:
                inst = new ASLB(a);
                break;
            case 0x59:
                inst = new ROLB(a);
                break;
            case 0x5A:
                inst = new DECB(a);
                break;
            case 0x5C:
                inst = new INCB(a);
                break;
            case 0x5D:
                inst = new TSTB(a);
                break;
            case 0x5F:
                inst = new CLRB(a);
                break;
            case 0x60:
                inst = new NEG(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x63:
                inst = new COM(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x64:
                inst = new LSR(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x66:
                inst = new ROR(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x67:
                inst = new ASR(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x68:
                inst = new ASL(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x69:
                inst = new ROL(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x6A:
                inst = new DEC(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x6C:
                inst = new INC(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x6D:
                inst = new TST(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x6E:
                inst = new JMP(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x6F:
                inst = new CLR(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0x70:
                inst = new NEG(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x71:
                inst = new NIM(a, getOperand2(i + 1));
                break;
            case 0x72:
                inst = new OIM(a, getOperand2(i + 1));
                break;
            case 0x73:
                inst = new COM(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x74:
                inst = new LSR(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x75:
                inst = new XIM(a, getOperand2(i + 1));
                break;
            case 0x76:
                inst = new ROR(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x77:
                inst = new ASR(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x78:
                inst = new ASL(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x79:
                inst = new ROL(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x7A:
                inst = new DEC(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x7B:
                inst = new TMM(a, getOperand2(i + 1));
                break;
            case 0x7C:
                inst = new INC(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x7D:
                inst = new TST(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x7E:
                inst = new JMP(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x7F:
                inst = new CLR(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0x80:
                inst = new SUBA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x81:
                inst = new CMPA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x82:
                inst = new SBCA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x84:
                inst = new ANDA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x85:
                inst = new BITA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x86:
                inst = new LDAA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x88:
                inst = new EORA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x89:
                inst = new ADCA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x8A:
                inst = new ORAA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x8B:
                inst = new ADDA(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0x8C:
                inst = new CPX(a, Instruction.MODE_IMMEDIATE, getOperand2(i + 1));
                break;
            case 0x8D:
                inst = new BSR(a, getOperand1(i + 1));
                break;
            case 0x8E:
                inst = new LDS(a, Instruction.MODE_IMMEDIATE, getOperand2(i + 1));
                break;
            case 0x90:
                inst = new SUBA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x91:
                inst = new CMPA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x92:
                inst = new SBCA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x94:
                inst = new ANDA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x95:
                inst = new BITA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x96:
                inst = new LDAA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x97:
                inst = new STAA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x98:
                inst = new EORA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x99:
                inst = new ADCA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x9A:
                inst = new ORAA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x9B:
                inst = new ADDA(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x9C:
                inst = new CPX(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x9E:
                inst = new LDS(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0x9F:
                inst = new STS(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xA0:
                inst = new SUBA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA1:
                inst = new CMPA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA2:
                inst = new SBCA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA4:
                inst = new ANDA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA5:
                inst = new BITA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA6:
                inst = new LDAA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA7:
                inst = new STAA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA8:
                inst = new EORA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xA9:
                inst = new ADCA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xAA:
                inst = new ORAA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xAB:
                inst = new ADDA(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xAC:
                inst = new CPX(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xAD:
                inst = new JSR(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xAE:
                inst = new LDS(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xAF:
                inst = new STS(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xB0:
                inst = new SUBA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB1:
                inst = new CMPA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB2:
                inst = new SBCA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB4:
                inst = new ANDA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB5:
                inst = new BITA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB6:
                inst = new LDAA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB7:
                inst = new STAA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB8:
                inst = new EORA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xB9:
                inst = new ADCA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xBA:
                inst = new ORAA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xBB:
                inst = new ADDA(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xBC:
                inst = new CPX(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xBD:
                inst = new JSR(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xBE:
                inst = new LDS(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xBF:
                inst = new STS(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xC0:
                inst = new SUBB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xC1:
                inst = new CMPB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xC2:
                inst = new SBCB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xC4:
                inst = new ANDB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xC5:
                inst = new BITB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xC6:
                inst = new LDAB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xC8:
                inst = new EORB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xC9:
                inst = new ADCB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xCA:
                inst = new ORAB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xCB:
                inst = new ADDB(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xCE:
                inst = new LDX(a, Instruction.MODE_IMMEDIATE, getOperand2(i + 1));
                break;
            case 0xD0:
                inst = new SUBB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD1:
                inst = new CMPB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD2:
                inst = new SBCB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD4:
                inst = new ANDB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD5:
                inst = new BITB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD6:
                inst = new LDAB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD7:
                inst = new STAB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD8:
                inst = new EORB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xD9:
                inst = new ADCB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xDA:
                inst = new ORAB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xDB:
                inst = new ADDB(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xDE:
                inst = new LDX(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xDF:
                inst = new STX(a, Instruction.MODE_DIRECT, getOperand1(i + 1));
                break;
            case 0xE0:
                inst = new SUBB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE1:
                inst = new CMPB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE2:
                inst = new SBCB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE4:
                inst = new ANDB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE5:
                inst = new BITB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE6:
                inst = new LDAB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE7:
                inst = new STAB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE8:
                inst = new EORB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xE9:
                inst = new ADCB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xEA:
                inst = new ORAB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xEB:
                inst = new ADDB(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xEC:
                inst = new ADX(a, Instruction.MODE_IMMEDIATE, getOperand1(i + 1));
                break;
            case 0xEE:
                inst = new LDX(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xEF:
                inst = new STX(a, Instruction.MODE_INDEXED, getOperand1(i + 1));
                break;
            case 0xF0:
                inst = new SUBB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF1:
                inst = new CMPB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF2:
                inst = new SBCB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF4:
                inst = new ANDB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF5:
                inst = new BITB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF6:
                inst = new LDAB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF7:
                inst = new STAB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF8:
                inst = new EORB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xF9:
                inst = new ADCB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xFA:
                inst = new ORAB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xFB:
                inst = new ADDB(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xFC:
                inst = new ADX(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xFE:
                inst = new LDX(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            case 0xFF:
                inst = new STX(a, Instruction.MODE_EXTENDED, getOperand2(i + 1));
                break;
            default:
                inst = new UnknownInstruction(a);
                break;
            }
            instructions.add(inst);
            i += inst.getLength();
        }
    }

    private void resolveBranch() {
        int label_number = 1;
        for (Instruction i : instructions) {
            if (i.isBranch()) {
                int target_address;
                BranchInstruction b = (BranchInstruction)i;
                target_address = b.getTargetAddress();
                for (Instruction j : instructions) {
                    if (j.getAddress() == target_address) {
                        if (!j.isBranchTarget()) {
                            j.setBranchTarget();
                            j.setLabel(new Label(label_number++));
                        }
                        b.setTarget(j);
                    }
                }
            }
        }
    }

    private void sortLabel() {
        Vector<Instruction> labels = new Vector<Instruction>();

        for (Instruction i : instructions) {
            if (i.getLabel() != null) {
                labels.add(i);
            }
        }
        Collections.sort(labels, new Comparator<Instruction>() {
            public int compare(Instruction i1, Instruction i2) {
                if (i1.getAddress() < i2.getAddress()) {
                    return -1;
                }
                else if (i1.getAddress() > i2.getAddress()) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        });
        int labelNumber = 1;
        for (Instruction i : labels) {
            i.getLabel().setNumber(labelNumber++);
        }
    }

    public String print() {
        StringBuffer sb = new StringBuffer();
        int index = 0;
        for (Instruction i : instructions) {
            if (i.getLabel() != null) {
                sb.append(i.getLabel().toString() + ":\n");
            }
            sb.append(String.format("    %04x ", i.getAddress()));
            for (int n = 0; n < 3; n++) {
                if (n < i.getLength()) {
                    sb.append(String.format("%02x ", data[index++]));
                }
                else {
                    sb.append("   ");
                }
            }
            sb.append(String.format("   %-5s %s\n", i.getMnemonic(), i.getOperandString()));
        }
        return sb.toString();
    }

    public void execute() {
        disassemble();
        resolveBranch();
        sortLabel();
    }
}
