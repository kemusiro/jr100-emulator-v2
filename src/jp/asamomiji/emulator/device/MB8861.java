/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device;

import jp.asamomiji.emulator.CPU;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.MemorySystem;
import jp.asamomiji.emulator.StateSet;

/*
 * MB8861を表すクラス
 */
public final class MB8861 extends CPU {
    public final static short VECTOR_IRQ = (short) 0xfff8;
    public final static short VECTOR_SWI = (short) 0xfffa;
    public final static short VECTOR_NMI = (short) 0xfffc;
    public final static short VECTOR_RESTART = (short) 0xfffe;

    public final static byte OP_ABA_IMP = (byte) 0x1b;
    public final static byte OP_ADDA_IMM = (byte) 0x8b;
    public final static byte OP_ADDA_DIR = (byte) 0x9b;
    public final static byte OP_ADDA_IND = (byte) 0xab;
    public final static byte OP_ADDA_EXT = (byte) 0xbb;
    public final static byte OP_ADDB_IMM = (byte) 0xcb;
    public final static byte OP_ADDB_DIR = (byte) 0xdb;
    public final static byte OP_ADDB_IND = (byte) 0xeb;
    public final static byte OP_ADDB_EXT = (byte) 0xfb;
    public final static byte OP_ADCA_IMM = (byte) 0x89;
    public final static byte OP_ADCA_DIR = (byte) 0x99;
    public final static byte OP_ADCA_IND = (byte) 0xa9;
    public final static byte OP_ADCA_EXT = (byte) 0xb9;
    public final static byte OP_ADCB_IMM = (byte) 0xc9;
    public final static byte OP_ADCB_DIR = (byte) 0xd9;
    public final static byte OP_ADCB_IND = (byte) 0xe9;
    public final static byte OP_ADCB_EXT = (byte) 0xf9;
    public final static byte OP_ANDA_IMM = (byte) 0x84;
    public final static byte OP_ANDA_DIR = (byte) 0x94;
    public final static byte OP_ANDA_IND = (byte) 0xa4;
    public final static byte OP_ANDA_EXT = (byte) 0xb4;
    public final static byte OP_ANDB_IMM = (byte) 0xc4;
    public final static byte OP_ANDB_DIR = (byte) 0xd4;
    public final static byte OP_ANDB_IND = (byte) 0xe4;
    public final static byte OP_ANDB_EXT = (byte) 0xf4;
    public final static byte OP_ASLA_IMP = (byte) 0x48;
    public final static byte OP_ASLB_IMP = (byte) 0x58;
    public final static byte OP_ASRA_IMP = (byte) 0x47;
    public final static byte OP_ASRB_IMP = (byte) 0x57;
    public final static byte OP_BITA_IMM = (byte) 0x85;
    public final static byte OP_BITA_DIR = (byte) 0x95;
    public final static byte OP_BITA_IND = (byte) 0xa5;
    public final static byte OP_BITA_EXT = (byte) 0xb5;
    public final static byte OP_BITB_IMM = (byte) 0xc5;
    public final static byte OP_BITB_DIR = (byte) 0xd5;
    public final static byte OP_BITB_IND = (byte) 0xe5;
    public final static byte OP_BITB_EXT = (byte) 0xf5;
    public final static byte OP_CBA_IMP = (byte) 0x11;
    public final static byte OP_CLRA_IMP = (byte) 0x4f;
    public final static byte OP_CLRB_IMP = (byte) 0x5f;
    public final static byte OP_CMPA_IMM = (byte) 0x81;
    public final static byte OP_CMPA_DIR = (byte) 0x91;
    public final static byte OP_CMPA_IND = (byte) 0xa1;
    public final static byte OP_CMPA_EXT = (byte) 0xb1;
    public final static byte OP_CMPB_IMM = (byte) 0xc1;
    public final static byte OP_CMPB_DIR = (byte) 0xd1;
    public final static byte OP_CMPB_IND = (byte) 0xe1;
    public final static byte OP_CMPB_EXT = (byte) 0xf1;
    public final static byte OP_COMA_IMP = (byte) 0x43;
    public final static byte OP_COMB_IMP = (byte) 0x53;
    public final static byte OP_DAA_IMP = (byte) 0x19;
    public final static byte OP_DECA_IMP = (byte) 0x4a;
    public final static byte OP_DECB_IMP = (byte) 0x5a;
    public final static byte OP_EORA_IMM = (byte) 0x88;
    public final static byte OP_EORA_DIR = (byte) 0x98;
    public final static byte OP_EORA_IND = (byte) 0xa8;
    public final static byte OP_EORA_EXT = (byte) 0xb8;
    public final static byte OP_EORB_IMM = (byte) 0xc8;
    public final static byte OP_EORB_DIR = (byte) 0xd8;
    public final static byte OP_EORB_IND = (byte) 0xe8;
    public final static byte OP_EORB_EXT = (byte) 0xf8;
    public final static byte OP_INCA_IMP = (byte) 0x4c;
    public final static byte OP_INCB_IMP = (byte) 0x5c;
    public final static byte OP_LDAA_IMM = (byte) 0x86;
    public final static byte OP_LDAA_DIR = (byte) 0x96;
    public final static byte OP_LDAA_IND = (byte) 0xa6;
    public final static byte OP_LDAA_EXT = (byte) 0xb6;
    public final static byte OP_LDAB_IMM = (byte) 0xc6;
    public final static byte OP_LDAB_DIR = (byte) 0xd6;
    public final static byte OP_LDAB_IND = (byte) 0xe6;
    public final static byte OP_LDAB_EXT = (byte) 0xf6;
    public final static byte OP_LSRA_IMP = (byte) 0x44;
    public final static byte OP_LSRB_IMP = (byte) 0x54;
    public final static byte OP_NEGA_IMP = (byte) 0x40;
    public final static byte OP_NEGB_IMP = (byte) 0x50;
    public final static byte OP_ORAA_IMM = (byte) 0x8a;
    public final static byte OP_ORAA_DIR = (byte) 0x9a;
    public final static byte OP_ORAA_IND = (byte) 0xaa;
    public final static byte OP_ORAA_EXT = (byte) 0xba;
    public final static byte OP_ORAB_IMM = (byte) 0xca;
    public final static byte OP_ORAB_DIR = (byte) 0xda;
    public final static byte OP_ORAB_IND = (byte) 0xea;
    public final static byte OP_ORAB_EXT = (byte) 0xfa;
    public final static byte OP_PSHA_IMP = (byte) 0x36;
    public final static byte OP_PSHB_IMP = (byte) 0x37;
    public final static byte OP_PULA_IMP = (byte) 0x32;
    public final static byte OP_PULB_IMP = (byte) 0x33;
    public final static byte OP_ROLA_IMP = (byte) 0x49;
    public final static byte OP_ROLB_IMP = (byte) 0x59;
    public final static byte OP_RORA_IMP = (byte) 0x46;
    public final static byte OP_RORB_IMP = (byte) 0x56;
    public final static byte OP_STAA_DIR = (byte) 0x97;
    public final static byte OP_STAA_IND = (byte) 0xa7;
    public final static byte OP_STAA_EXT = (byte) 0xb7;
    public final static byte OP_STAB_DIR = (byte) 0xd7;
    public final static byte OP_STAB_IND = (byte) 0xe7;
    public final static byte OP_STAB_EXT = (byte) 0xf7;
    public final static byte OP_SBA_IMP = (byte) 0x10;
    public final static byte OP_SUBA_IMM = (byte) 0x80;
    public final static byte OP_SUBA_DIR = (byte) 0x90;
    public final static byte OP_SUBA_IND = (byte) 0xa0;
    public final static byte OP_SUBA_EXT = (byte) 0xb0;
    public final static byte OP_SUBB_IMM = (byte) 0xc0;
    public final static byte OP_SUBB_DIR = (byte) 0xd0;
    public final static byte OP_SUBB_IND = (byte) 0xe0;
    public final static byte OP_SUBB_EXT = (byte) 0xf0;
    public final static byte OP_SBCA_IMM = (byte) 0x82;
    public final static byte OP_SBCA_DIR = (byte) 0x92;
    public final static byte OP_SBCA_IND = (byte) 0xa2;
    public final static byte OP_SBCA_EXT = (byte) 0xb2;
    public final static byte OP_SBCB_IMM = (byte) 0xc2;
    public final static byte OP_SBCB_DIR = (byte) 0xd2;
    public final static byte OP_SBCB_IND = (byte) 0xe2;
    public final static byte OP_SBCB_EXT = (byte) 0xf2;
    public final static byte OP_TAB_IMP = (byte) 0x16;
    public final static byte OP_TBA_IMP = (byte) 0x17;
    public final static byte OP_TSTA_IMP = (byte) 0x4d;
    public final static byte OP_TSTB_IMP = (byte) 0x5d;
    public final static byte OP_CPX_IMM = (byte) 0x8c;
    public final static byte OP_CPX_DIR = (byte) 0x9c;
    public final static byte OP_CPX_IND = (byte) 0xac;
    public final static byte OP_CPX_EXT = (byte) 0xbc;
    public final static byte OP_DEX_IMP = (byte) 0x09;
    public final static byte OP_DES_IMP = (byte) 0x34;
    public final static byte OP_INX_IMP = (byte) 0x08;
    public final static byte OP_INS_IMP = (byte) 0x31;
    public final static byte OP_LDX_IMM = (byte) 0xce;
    public final static byte OP_LDX_DIR = (byte) 0xde;
    public final static byte OP_LDX_IND = (byte) 0xee;
    public final static byte OP_LDX_EXT = (byte) 0xfe;
    public final static byte OP_LDS_IMM = (byte) 0x8e;
    public final static byte OP_LDS_DIR = (byte) 0x9e;
    public final static byte OP_LDS_IND = (byte) 0xae;
    public final static byte OP_LDS_EXT = (byte) 0xbe;
    public final static byte OP_STX_DIR = (byte) 0xdf;
    public final static byte OP_STX_IND = (byte) 0xef;
    public final static byte OP_STX_EXT = (byte) 0xff;
    public final static byte OP_STS_DIR = (byte) 0x9f;
    public final static byte OP_STS_IND = (byte) 0xaf;
    public final static byte OP_STS_EXT = (byte) 0xbf;
    public final static byte OP_TXS_IMP = (byte) 0x35;
    public final static byte OP_TSX_IMP = (byte) 0x30;
    public final static byte OP_ASL_IND = (byte) 0x68;
    public final static byte OP_ASL_EXT = (byte) 0x78;
    public final static byte OP_ASR_IND = (byte) 0x67;
    public final static byte OP_ASR_EXT = (byte) 0x77;
    public final static byte OP_CLR_IND = (byte) 0x6f;
    public final static byte OP_CLR_EXT = (byte) 0x7f;
    public final static byte OP_COM_IND = (byte) 0x63;
    public final static byte OP_COM_EXT = (byte) 0x73;
    public final static byte OP_DEC_IND = (byte) 0x6a;
    public final static byte OP_DEC_EXT = (byte) 0x7a;
    public final static byte OP_INC_IND = (byte) 0x6c;
    public final static byte OP_INC_EXT = (byte) 0x7c;
    public final static byte OP_LSR_IND = (byte) 0x64;
    public final static byte OP_LSR_EXT = (byte) 0x74;
    public final static byte OP_NEG_IND = (byte) 0x60;
    public final static byte OP_NEG_EXT = (byte) 0x70;
    public final static byte OP_ROL_IND = (byte) 0x69;
    public final static byte OP_ROL_EXT = (byte) 0x79;
    public final static byte OP_ROR_IND = (byte) 0x66;
    public final static byte OP_ROR_EXT = (byte) 0x76;
    public final static byte OP_TST_IND = (byte) 0x6d;
    public final static byte OP_TST_EXT = (byte) 0x7d;
    public final static byte OP_BRA_REL = (byte) 0x20;
    public final static byte OP_BCC_REL = (byte) 0x24;
    public final static byte OP_BCS_REL = (byte) 0x25;
    public final static byte OP_BEQ_REL = (byte) 0x27;
    public final static byte OP_BGE_REL = (byte) 0x2c;
    public final static byte OP_BGT_REL = (byte) 0x2e;
    public final static byte OP_BHI_REL = (byte) 0x22;
    public final static byte OP_BLE_REL = (byte) 0x2f;
    public final static byte OP_BLS_REL = (byte) 0x23;
    public final static byte OP_BLT_REL = (byte) 0x2d;
    public final static byte OP_BMI_REL = (byte) 0x2b;
    public final static byte OP_BNE_REL = (byte) 0x26;
    public final static byte OP_BVC_REL = (byte) 0x28;
    public final static byte OP_BVS_REL = (byte) 0x29;
    public final static byte OP_BPL_REL = (byte) 0x2a;
    public final static byte OP_BSR_REL = (byte) 0x8d;
    public final static byte OP_JMP_IND = (byte) 0x6e;
    public final static byte OP_JMP_EXT = (byte) 0x7e;
    public final static byte OP_JSR_IND = (byte) 0xad;
    public final static byte OP_JSR_EXT = (byte) 0xbd;
    public final static byte OP_NOP_IMP = (byte) 0x01;
    public final static byte OP_RTI_IMP = (byte) 0x3b;
    public final static byte OP_RTS_IMP = (byte) 0x39;
    public final static byte OP_SWI_IMP = (byte) 0x3f;
    public final static byte OP_WAI_IMP = (byte) 0x3e;
    public final static byte OP_CLC_IMP = (byte) 0x0c;
    public final static byte OP_CLI_IMP = (byte) 0x0e;
    public final static byte OP_CLV_IMP = (byte) 0x0a;
    public final static byte OP_SEC_IMP = (byte) 0x0d;
    public final static byte OP_SEI_IMP = (byte) 0x0f;
    public final static byte OP_SEV_IMP = (byte) 0x0b;
    public final static byte OP_TAP_IMP = (byte) 0x06;
    public final static byte OP_TPA_IMP = (byte) 0x07;

    // 以下MB8861の拡張命令
    public final static byte OP_NIM_IND = (byte) 0x71;
    public final static byte OP_OIM_IND = (byte) 0x72;
    public final static byte OP_XIM_IND = (byte) 0x75;
    public final static byte OP_TMM_IND = (byte) 0x7b;
    public final static byte OP_ADX_IMM = (byte) 0xec;
    public final static byte OP_ADX_EXT = (byte) 0xfc;

    public byte A;
    public byte B;
    public short IX;
    public short SP;
    public short PC;
    public boolean CH = false;
    public boolean CI = false;
    public boolean CN = false;
    public boolean CZ = false;
    public boolean CV = false;
    public boolean CC = false;

    private boolean resetStatus = false;
    private boolean nmiStatus = false;
    private boolean irqStatus = false;
    private boolean haltStatus = false;
    private boolean haltProcessed = false;
    private boolean fetchWai = false;

    private MemorySystem m;

    public MB8861(Computer computer) {
        super(computer);
        m = computer.getHardware().getMemory();
    }

    @Override
    public void reset() {
        resetStatus = true;
    }

    @Override
    public void halt() {
        haltStatus = true;
    }

    @Override
    public void nmi() {
        nmiStatus = true;
    }

    @Override
    public void irq() {
        irqStatus = true;
    }

    private byte load8_dir(byte address) {
        return m.load8(address & 0xff);
    }

    private byte load8_ext(short address) {
        return m.load8(address & 0xffff);
    }

    private byte load8_ind(byte offset) {
        return m.load8((IX & 0xffff) + (offset & 0xff));
    }

    private void store8_ind(byte offset, byte value) {
        m.store8((IX & 0xffff) + (offset & 0xff), value);
    }

    private void store8_ext(short address, byte value) {
        m.store8(address & 0xffff, value);
    }

    private short load16_dir(byte address) {
        return m.load16(address & 0xff);
    }

    private short load16_ext(short address) {
        return m.load16(address & 0xffff);
    }

    private short load16_ind(byte offset) {
        return m.load16((IX & 0xffff) + (offset & 0xff));
    }

    private void store16_ext(short address, short value) {
        m.store16(address & 0xffff, value);
    }

    private byte add(byte x, byte y) {
        int t = (x & 0xff) + (y & 0xff);
        byte tt = (byte) t;
        CH = ((x & 0x0f) + (y & 0x0f)) > 0x0f;
        CN = tt < 0;
        CZ = tt == 0;
        CV = (x > 0) & (y > 0) & CN || (x < 0) & (y < 0) & !CN;
        CC = (t & 0x100) != 0;
        return tt;
    }

    private short add16(short x, short y) {
        int t = (x & 0xffff) + (y & 0xffff);
        short tt = (short) t;
        // この関数(ADX命令に対応)のフラグ変化は予想による。
        // CH = ???;
        CN = tt < 0;
        CZ = tt == 0;
        CV = (x > 0) & (y > 0) & CN || (x < 0) & (y < 0) & !CN;
        CC = (t & 0x10000) != 0;
        return tt;
    }

    private byte adc(byte x, byte y) {
        int t = (x & 0xff) + (y & 0xff) + (CC ? 1 : 0);
        byte tt = (byte) t;
        CH = ((x & 0x0f) + (y & 0x0f)) > 0x0f;
        CN = tt < 0;
        CZ = tt == 0;
        CV = (x > 0) & (y > 0) & CN || (x < 0) & (y < 0) & !CN;
        CC = (t & 0x100) != 0;
        return tt;
    }

    private byte and(byte x, byte y) {
        int t = x & y;
        CN = (t & 0x80) != 0;
        CZ = t == 0;
        CV = false;
        return (byte) (t & 0xff);
    }

    public byte nim(byte x, byte y) {
        int t = x & y;
        CZ = t == 0;
        CN = !CZ;
        CV = false;
        return (byte) (t & 0xff);
    }

    private byte asl(byte x) {
        int t = ((x & 0xff) << 1);
        CN = (byte) t < 0;
        CZ = (byte) t == 0;
        CC = t > 0xff;
        CV = CN != CC;
        return (byte) (t & 0xff);
    }

    private byte asr(byte x) {
        int t = ((x & 0xff) >> 1) | (x & 0x80);
        CN = (byte) t < 0;
        CZ = t == 0;
        CC = (x & 0x01) != 0;
        CV = CN != CC;
        return (byte) (t & 0xff);
    }

    private void bit(byte x, byte y) {
        int t = x & y;
        CN = (byte) t < 0;
        CZ = t == 0;
        CV = false;
    }

    private void tmm(byte x, byte y) {
        if (x == 0 | y == 0) {
            CN = false;
            CZ = true;
            CV = false;
        }
        else if (y == (byte) 0xff) {
            CN = false;
            CZ = false;
            CV = true;
        }
        else {
            CN = true;
            CZ = false;
            CV = false;
        }
    }

    private void cmp(byte x, byte y) {
        int t = (x & 0xff) - (y & 0xff);
        byte tt = (byte) (t);
        CN = tt < 0;
        CZ = tt == 0;
        CV = (x > 0) & (y < 0) & CN || (x < 0) & (y > 0) & !CN;
        CC = (t & 0x100) != 0;
    }

    private byte clr() {
        CN = false;
        CZ = true;
        CV = false;
        CC = false;
        return 0x00;
    }

    private byte com(byte x) {
        byte t = (byte) (0xff - (x & 0xff));
        CN = t < 0;
        CZ = t == 0;
        CV = false;
        CC = true;
        return t;
    }

    private void daa() {
        int t = A & 0xff;

        if ((t & 0x0f) >= 0x0a || CH) {
            t += 0x06;
        }
        if ((t & 0xf0) >= 0xa0) {
            t += 0x60;
        }
        byte tt = (byte) t;

        CN = tt < 0;
        CZ = tt == 0;
        CV = (A > 0) & CN || (A < 0) & !CN;
        CC = ((A & 0xf0) >= 0xa0) || CC;
        A = tt;
    }

    private byte dec(byte x) {
        int t = (x - 1) & 0xff;
        CN = (byte) t < 0;
        CZ = t == 0;
        CV = x == (byte) 0x80;
        return (byte) t;
    }

    private byte eor(byte x, byte y) {
        int t = (x & 0xff) ^ (y & 0xff);
        CN = (byte) t < 0;
        CZ = t == 0;
        CV = false;
        return (byte) t;
    }

    public byte xim(byte x, byte y) {
        int t = x ^ y;
        CZ = t == 0;
        CN = !CZ;
        return (byte) (t & 0xff);
    }

    private byte inc(byte x) {
        int t = (x + 1) & 0xff;
        CN = (byte) t < 0;
        CZ = t == 0;
        CV = x == (byte) 0x7f;
        return (byte) t;
    }

    private byte lda(byte x) {
        CN = x < 0;
        CZ = x == 0;
        CV = false;
        return x;
    }

    private byte lsr(byte x) {
        int t = (x & 0xff) >> 1;
        CN = false;
        CZ = t == 0;
        CC = (x & 0x1) != 0;
        CV = CN != CC;
        return (byte) t;
    }

    private byte neg(byte x) {
        int t = 0 - (x & 0xff);
        CN = (byte) t < 0;
        CZ = t == 0;
        CV = (byte) t == 0x80;
        CC = (byte) t == 0x00;
        return (byte) t;
    }

    private byte ora(byte x, byte y) {
        int t = x | y;
        CN = (byte) t < 0;
        CZ = t == 0;
        CV = false;
        return (byte) t;
    }

    public byte oim(byte x, byte y) {
        int t = x | y;
        CZ = t == 0;
        CN = !CZ;
        CV = false;
        return (byte) (t & 0xff);
    }

    private void psh(byte x) {
        store8_ext(SP, x);
        SP--;
    }

    private byte pul() {
        SP++;
        return load8_ext(SP);
    }

    private byte rol(byte x) {
        int t = (x & 0xff) << 1;
        t |= CC ? 0x01 : 0;
        CN = (byte) t < 0;
        CZ = t == 0;
        CC = t > 0xff;
        CV = CN != CC;
        return (byte) t;
    }

    private byte ror(byte x) {
        int t = (x & 0xff) >> 1;
        t |= CC ? 0x80 : 0;
        CN = (byte) t < 0;
        CZ = (byte) t == 0;
        CC = (x & 0x01) != 0;
        CV = CN != CC;
        return (byte) t;
    }

    private void sta(short a, byte x) {
        CN = x < 0;
        CZ = x == 0;
        CV = false;
        store8_ext(a, x);
    }

    private byte sub(byte x, byte y) {
        int t = (x & 0xff) - (y & 0xff);
        byte tt = (byte) t;
        CN = tt < 0;
        CZ = tt == 0;
        CV = (x > 0) & (y < 0) & CN || (x < 0) & (y > 0) & !CN;
        CC = (t & 0x100) != 0;
        return tt;
    }

    private byte sbc(byte x, byte y) {
        int t = (x & 0xff) - (y & 0xff) - (CC ? 0x01 : 0x00);
        byte tt = (byte) t;
        CN = tt < 0;
        CZ = tt == 0;
        CV = (x > 0) & (y < 0) & CN || (x < 0) & (y > 0) & !CN;
        CC = (t & 0x100) != 0;
        return tt;
    }

    private void tab() {
        B = A;
        CN = B < 0;
        CZ = B == 0;
        CV = false;
    }

    private void tba() {
        A = B;
        CN = A < 0;
        CZ = A == 0;
        CV = false;
    }

    private void tst(byte x) {
        CN = x < 0;
        CZ = x == 0;
        CV = false;
        CC = false;
    }

    private void cpx(short x) {
        int t = (IX & 0xffff) - (x & 0xffff);
        CN = (short) t < 0;
        CZ = (short) t == 0;
        CV = (IX > 0) & (x < 0) & CN || (IX < 0) & (x > 0) & !CN;
    }

    private void dex() {
        IX--;
        CZ = IX == 0;
    }

    private void des() {
        SP--;
    }

    private void inx() {
        IX++;
        CZ = IX == 0;
    }

    private void ins() {
        SP++;
    }

    private void ldx(short a) {
        IX = a;
        CN = IX < 0;
        CZ = IX == 0;
        CV = false;
    }

    private void lds(short a) {
        SP = a;
        CN = (short) SP < 0;
        CZ = (short) SP == 0;
        CV = false;
    }

    private void stx(short a) {
        store16_ext(a, IX);
        CN = IX < 0;
        CZ = IX == 0;
        CV = false;
    }

    private void sts(short a) {
        store16_ext(a, SP);
        CN = IX < 0;
        CZ = IX == 0;
        CV = false;
    }

    private void txs() {
        SP = (short) (IX - 1);
    }

    private void tsx() {
        IX = (short) (SP + 1);
    }

    private void branch(byte offset, boolean condition) {
        if (condition) {
            PC = (short) (PC + offset);
        }
    }

    private void bsr(byte offset) {
        SP = (short) (SP - 2);
        store16_ext((short) (SP + 1), PC);
        PC = (short) (PC + offset);
    }

    private void jump(short a) {
        PC = a;
    }

    private void jsr(short a) {
        SP = (short) (SP - 2);
        store16_ext((short) (SP + 1), PC);
        PC = a;
    }

    private void nop() {
    }

    private void pushAllRegisters() {
        int ccr = 0xc0;
        if (CH) {
            ccr |= 0x20;
        }
        if (CI) {
            ccr |= 0x10;
        }
        if (CN) {
            ccr |= 0x08;
        }
        if (CZ) {
            ccr |= 0x04;
        }
        if (CV) {
            ccr |= 0x02;
        }
        if (CC) {
            ccr |= 0x01;
        }
        store16_ext((short) (SP - 1), PC);
        store16_ext((short) (SP - 3), IX);
        store8_ext((short) (SP - 4), A);
        store8_ext((short) (SP - 5), B);
        store8_ext((short) (SP - 6), (byte) ccr);
        SP = (short) (SP - 7);
    }

    private void popAllRegisters() {
        SP = (short) (SP + 7);
        int ccr = (int) load8_ext((short) (SP - 6));
        CH = (ccr & 0x20) != 0;
        CI = (ccr & 0x10) != 0;
        CN = (ccr & 0x08) != 0;
        CZ = (ccr & 0x04) != 0;
        CV = (ccr & 0x02) != 0;
        CC = (ccr & 0x01) != 0;
        B = load8_ext((short) (SP - 5));
        A = load8_ext((short) (SP - 4));
        IX = load16_ext((short) (SP - 3));
        PC = load16_ext((short) (SP - 1));
    }

    private void rti() {
        popAllRegisters();
    }

    private void rts() {
        SP = (short) (SP + 2);
        PC = load16_ext((short) (SP - 1));
    }

    private void swi() {
        PC = (short) (PC + 1);
        pushAllRegisters();
        CI = true;
        PC = load16_ext(VECTOR_SWI);
    }

    private void wai() {
        fetchWai = true;
    }

    private void clc() {
        CC = false;
    }

    private void cli() {
        CI = false;
    }

    private void clv() {
        CV = false;
    }

    private void sec() {
        CC = true;
    }

    private void sei() {
        CI = true;
    }

    private void sev() {
        CV = true;
    }

    private void tap() {
        CH = (A & 0x20) != 0;
        CI = (A & 0x10) != 0;
        CN = (A & 0x08) != 0;
        CZ = (A & 0x04) != 0;
        CV = (A & 0x02) != 0;
        CC = (A & 0x01) != 0;
    }

    private void tpa() {
        A = (byte) 0xc0;
        if (CH) {
            A |= 0x20;
        }
        if (CI) {
            A |= 0x10;
        }
        if (CN) {
            A |= 0x08;
        }
        if (CZ) {
            A |= 0x04;
        }
        if (CV) {
            A |= 0x02;
        }
        if (CC) {
            A |= 0x01;
        }
    }

    private byte fetchOp() {
        return load8_ext(PC++);
    }

    private byte fetchOperand1() {
        return load8_ext(PC++);
    }

    private short fetchOperand2() {
        int op1 = fetchOperand1() & 0xff;
        int op2 = fetchOperand1() & 0xff;
        return (short) ((op1 << 8) + op2);
    }

    @Override
    public long execute(long clocks) {
        long initial_clock;

        initial_clock = computer.clockCount;
        while (computer.clockCount < initial_clock + clocks) {
            byte offset;
            byte value;
            short addr;
            if (resetStatus) {
                resetStatus = false;
                fetchWai = false;
                PC = load16_ext(VECTOR_RESTART);
                computer.clockCount = 0;
                return 0;
            }
            if (haltStatus) {
                haltProcessed = true;
                continue;
            }
            if (haltProcessed) {
                haltProcessed = false;
            }
            else {
                if (fetchWai) {
                    if (nmiStatus) {
                        nmiStatus = false;
                        fetchWai = false;
                        pushAllRegisters();
                        PC = m.load16(VECTOR_NMI);
                        computer.clockCount += 12;
                    }
                    if (irqStatus && !CI) {
                        irqStatus = false;
                        fetchWai = false;
                        pushAllRegisters();
                        PC = m.load16(VECTOR_IRQ);
                        computer.clockCount += 12;
                    }
                    computer.clockCount++;
                    continue;
                }
                else {
                    if (nmiStatus) {
                        nmiStatus = false;
                        pushAllRegisters();
                        PC = m.load16(VECTOR_NMI);
                        computer.clockCount += 12;
                        continue;
                    }
                    if (irqStatus && !CI) {
                        irqStatus = false;
                        pushAllRegisters();
                        PC = m.load16(VECTOR_IRQ);
                        computer.clockCount += 12;
                        continue;
                    }
                }
            }

            switch (fetchOp()) {
            case OP_ABA_IMP:
                A = add(A, B);
                computer.clockCount += 2;
                break;
            case OP_ADDA_IMM:
                A = add(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ADDA_DIR:
                A = add(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ADDA_IND:
                A = add(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ADDA_EXT:
                A = add(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_ADDB_IMM:
                B = add(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ADDB_DIR:
                B = add(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ADDB_IND:
                B = add(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ADDB_EXT:
                B = add(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_ADCA_IMM:
                A = adc(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ADCA_DIR:
                A = adc(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ADCA_IND:
                A = adc(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ADCA_EXT:
                A = adc(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_ADCB_IMM:
                B = adc(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ADCB_DIR:
                B = adc(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ADCB_IND:
                B = adc(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ADCB_EXT:
                B = adc(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_ANDA_IMM:
                A = and(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ANDA_DIR:
                A = and(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ANDA_IND:
                A = and(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ANDA_EXT:
                A = and(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_ANDB_IMM:
                B = and(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ANDB_DIR:
                B = and(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ANDB_IND:
                B = and(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ANDB_EXT:
                B = and(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_ASLA_IMP:
                A = asl(A);
                computer.clockCount += 2;
                break;
            case OP_ASLB_IMP:
                B = asl(B);
                computer.clockCount += 2;
                break;
            case OP_ASRA_IMP:
                A = asr(A);
                computer.clockCount += 2;
                break;
            case OP_ASRB_IMP:
                B = asr(B);
                computer.clockCount += 2;
                break;
            case OP_BITA_IMM:
                bit(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_BITA_DIR:
                bit(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_BITA_IND:
                bit(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_BITA_EXT:
                bit(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_BITB_IMM:
                bit(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_BITB_DIR:
                bit(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_BITB_IND:
                bit(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_BITB_EXT:
                bit(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_CBA_IMP:
                cmp(A, B);
                computer.clockCount += 2;
                break;
            case OP_CLRA_IMP:
                A = clr();
                computer.clockCount += 2;
                break;
            case OP_CLRB_IMP:
                B = clr();
                computer.clockCount += 2;
                break;
            case OP_CMPA_IMM:
                cmp(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_CMPA_DIR:
                cmp(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_CMPA_IND:
                cmp(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_CMPA_EXT:
                cmp(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_CMPB_IMM:
                cmp(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_CMPB_DIR:
                cmp(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_CMPB_IND:
                cmp(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_CMPB_EXT:
                cmp(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_COMA_IMP:
                A = com(A);
                computer.clockCount += 2;
                break;
            case OP_COMB_IMP:
                B = com(B);
                computer.clockCount += 2;
                break;
            case OP_DAA_IMP:
                daa();
                computer.clockCount += 2;
                break;
            case OP_DECA_IMP:
                A = dec(A);
                computer.clockCount += 2;
                break;
            case OP_DECB_IMP:
                B = dec(B);
                computer.clockCount += 2;
                break;
            case OP_EORA_IMM:
                A = eor(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_EORA_DIR:
                A = eor(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_EORA_IND:
                A = eor(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_EORA_EXT:
                A = eor(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_EORB_IMM:
                B = eor(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_EORB_DIR:
                B = eor(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_EORB_IND:
                B = eor(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_EORB_EXT:
                B = eor(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_INCA_IMP:
                A = inc(A);
                computer.clockCount += 2;
                break;
            case OP_INCB_IMP:
                B = inc(B);
                computer.clockCount += 2;
                break;
            case OP_LDAA_IMM:
                A = lda(fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_LDAA_DIR:
                A = lda(load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_LDAA_IND:
                A = lda(load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_LDAA_EXT:
                A = lda(load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_LDAB_IMM:
                B = lda(fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_LDAB_DIR:
                B = lda(load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_LDAB_IND:
                B = lda(load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_LDAB_EXT:
                B = lda(load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_LSRA_IMP:
                A = lsr(A);
                computer.clockCount += 2;
                break;
            case OP_LSRB_IMP:
                B = lsr(B);
                computer.clockCount += 2;
                break;
            case OP_NEGA_IMP:
                A = neg(A);
                computer.clockCount += 2;
                break;
            case OP_NEGB_IMP:
                B = neg(B);
                computer.clockCount += 2;
                break;
            case OP_ORAA_IMM:
                A = ora(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ORAA_DIR:
                A = ora(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ORAA_IND:
                A = ora(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ORAA_EXT:
                A = ora(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_ORAB_IMM:
                B = ora(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_ORAB_DIR:
                B = ora(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_ORAB_IND:
                B = ora(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_ORAB_EXT:
                B = add(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_PSHA_IMP:
                psh(A);
                computer.clockCount += 4;
                break;
            case OP_PSHB_IMP:
                psh(B);
                computer.clockCount += 4;
                break;
            case OP_PULA_IMP:
                A = pul();
                computer.clockCount += 4;
                break;
            case OP_PULB_IMP:
                B = pul();
                computer.clockCount += 4;
                break;
            case OP_ROLA_IMP:
                A = rol(A);
                computer.clockCount += 2;
                break;
            case OP_ROLB_IMP:
                B = rol(B);
                computer.clockCount += 2;
                break;
            case OP_RORA_IMP:
                A = ror(A);
                computer.clockCount += 2;
                break;
            case OP_RORB_IMP:
                B = ror(B);
                computer.clockCount += 2;
                break;
            case OP_STAA_DIR:
                sta((short) (fetchOperand1() & 0xff), A);
                computer.clockCount += 4;
                break;
            case OP_STAA_IND:
                sta((short) ((IX & 0xffff) + (fetchOperand1() & 0xff)), A);
                computer.clockCount += 6;
                break;
            case OP_STAA_EXT:
                sta(fetchOperand2(), A);
                computer.clockCount += 5;
                break;
            case OP_STAB_DIR:
                sta((short) (fetchOperand1() & 0xff), B);
                computer.clockCount += 4;
                break;
            case OP_STAB_IND:
                sta((short) ((IX & 0xffff) + (fetchOperand1() & 0xff)), B);
                computer.clockCount += 6;
                break;
            case OP_STAB_EXT:
                sta(fetchOperand2(), B);
                computer.clockCount += 5;
                break;
            case OP_SBA_IMP:
                A = sub(A, B);
                computer.clockCount += 2;
                break;
            case OP_SUBA_IMM:
                A = sub(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_SUBA_DIR:
                A = sub(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_SUBA_IND:
                A = sub(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_SUBA_EXT:
                A = sub(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_SUBB_IMM:
                B = sub(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_SUBB_DIR:
                B = sub(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_SUBB_IND:
                B = sub(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_SUBB_EXT:
                B = sub(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_SBCA_IMM:
                A = sbc(A, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_SBCA_DIR:
                A = sbc(A, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_SBCA_IND:
                A = sbc(A, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_SBCA_EXT:
                A = sbc(A, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_SBCB_IMM:
                B = sbc(B, fetchOperand1());
                computer.clockCount += 2;
                break;
            case OP_SBCB_DIR:
                B = sbc(B, load8_dir(fetchOperand1()));
                computer.clockCount += 3;
                break;
            case OP_SBCB_IND:
                B = sbc(B, load8_ind(fetchOperand1()));
                computer.clockCount += 5;
                break;
            case OP_SBCB_EXT:
                B = sbc(B, load8_ext(fetchOperand2()));
                computer.clockCount += 4;
                break;
            case OP_TAB_IMP:
                tab();
                computer.clockCount += 2;
                break;
            case OP_TBA_IMP:
                tba();
                computer.clockCount += 2;
                break;
            case OP_TSTA_IMP:
                tst(A);
                computer.clockCount += 2;
                break;
            case OP_TSTB_IMP:
                tst(B);
                computer.clockCount += 2;
                break;
            case OP_CPX_IMM:
                cpx(fetchOperand2());
                computer.clockCount += 3;
                break;
            case OP_CPX_DIR:
                cpx(load16_dir(fetchOperand1()));
                computer.clockCount += 4;
                break;
            case OP_CPX_IND:
                cpx(load16_ind(fetchOperand1()));
                computer.clockCount += 6;
                break;
            case OP_CPX_EXT:
                cpx(load16_ext(fetchOperand2()));
                computer.clockCount += 5;
                break;
            case OP_DEX_IMP:
                dex();
                computer.clockCount += 4;
                break;
            case OP_DES_IMP:
                des();
                computer.clockCount += 4;
                break;
            case OP_INX_IMP:
                inx();
                computer.clockCount += 4;
                break;
            case OP_INS_IMP:
                ins();
                computer.clockCount += 4;
                break;
            case OP_LDX_IMM:
                ldx(fetchOperand2());
                computer.clockCount += 3;
                break;
            case OP_LDX_DIR:
                ldx(load16_dir(fetchOperand1()));
                computer.clockCount += 4;
                break;
            case OP_LDX_IND:
                ldx(load16_ind(fetchOperand1()));
                computer.clockCount += 6;
                break;
            case OP_LDX_EXT:
                ldx(load16_ext(fetchOperand2()));
                computer.clockCount += 5;
                break;
            case OP_LDS_IMM:
                lds(fetchOperand2());
                computer.clockCount += 3;
                break;
            case OP_LDS_DIR:
                lds(load16_dir(fetchOperand1()));
                computer.clockCount += 4;
                break;
            case OP_LDS_IND:
                lds(load16_ind(fetchOperand1()));
                computer.clockCount += 6;
                break;
            case OP_LDS_EXT:
                lds(load16_ext(fetchOperand2()));
                computer.clockCount += 5;
                break;
            case OP_STX_DIR:
                stx((short) (fetchOperand1() & 0xff));
                computer.clockCount += 5;
                break;
            case OP_STX_IND:
                stx((short) ((IX & 0xffff) + (fetchOperand1() & 0xff)));
                computer.clockCount += 7;
                break;
            case OP_STX_EXT:
                stx(fetchOperand2());
                computer.clockCount += 6;
                break;
            case OP_STS_DIR:
                sts((short) (fetchOperand1() & 0xff));
                computer.clockCount += 5;
                break;
            case OP_STS_IND:
                sts((short) ((IX & 0xffff) + (fetchOperand1() & 0xff)));
                computer.clockCount += 7;
                break;
            case OP_STS_EXT:
                sts(fetchOperand2());
                computer.clockCount += 6;
                break;
            case OP_TXS_IMP:
                txs();
                computer.clockCount += 4;
                break;
            case OP_TSX_IMP:
                tsx();
                computer.clockCount += 4;
                break;
            case OP_ASL_IND:
                offset = fetchOperand1();
                store8_ind(offset, asl(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_ASL_EXT:
                addr = fetchOperand2();
                store8_ext(addr, asl(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_ASR_IND:
                offset = fetchOperand1();
                store8_ind(offset, asr(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_ASR_EXT:
                addr = fetchOperand2();
                store8_ext(addr, asr(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_CLR_IND:
                store8_ind(fetchOperand1(), clr());
                computer.clockCount += 7;
                break;
            case OP_CLR_EXT:
                store8_ext(fetchOperand2(), clr());
                computer.clockCount += 6;
                break;
            case OP_COM_IND:
                offset = fetchOperand1();
                store8_ind(offset, com(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_COM_EXT:
                addr = fetchOperand2();
                store8_ext(addr, com(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_DEC_IND:
                offset = fetchOperand1();
                store8_ind(offset, dec(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_DEC_EXT:
                addr = fetchOperand2();
                store8_ext(addr, dec(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_INC_IND:
                offset = fetchOperand1();
                store8_ind(offset, inc(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_INC_EXT:
                addr = fetchOperand2();
                store8_ext(addr, inc(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_LSR_IND:
                offset = fetchOperand1();
                store8_ind(offset, lsr(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_LSR_EXT:
                addr = fetchOperand2();
                store8_ext(addr, lsr(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_NEG_IND:
                offset = fetchOperand1();
                store8_ind(offset, neg(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_NEG_EXT:
                addr = fetchOperand2();
                store8_ext(addr, neg(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_ROL_IND:
                offset = fetchOperand1();
                store8_ind(offset, rol(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_ROL_EXT:
                addr = fetchOperand2();
                store8_ext(addr, rol(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_ROR_IND:
                offset = fetchOperand1();
                store8_ind(offset, ror(load8_ind(offset)));
                computer.clockCount += 7;
                break;
            case OP_ROR_EXT:
                addr = fetchOperand2();
                store8_ext(addr, ror(load8_ext(addr)));
                computer.clockCount += 6;
                break;
            case OP_TST_IND:
                tst(load8_ind(fetchOperand1()));
                computer.clockCount += 7;
                break;
            case OP_TST_EXT:
                tst(load8_ext(fetchOperand2()));
                computer.clockCount += 6;
                break;
            case OP_BRA_REL:
                branch(fetchOperand1(), true);
                computer.clockCount += 4;
                break;
            case OP_BCC_REL:
                branch(fetchOperand1(), !CC);
                computer.clockCount += 4;
                break;
            case OP_BCS_REL:
                branch(fetchOperand1(), CC);
                computer.clockCount += 4;
                break;
            case OP_BEQ_REL:
                branch(fetchOperand1(), CZ);
                computer.clockCount += 4;
                break;
            case OP_BGE_REL:
                branch(fetchOperand1(), !(CN ^ CV));
                computer.clockCount += 4;
                break;
            case OP_BGT_REL:
                branch(fetchOperand1(), !(CZ | (CN ^ CV)));
                computer.clockCount += 4;
                break;
            case OP_BHI_REL:
                branch(fetchOperand1(), !(CC | CZ));
                computer.clockCount += 4;
                break;
            case OP_BLE_REL:
                branch(fetchOperand1(), CZ | (CN ^ CV));
                computer.clockCount += 4;
                break;
            case OP_BLS_REL:
                branch(fetchOperand1(), CC | CZ);
                computer.clockCount += 4;
                break;
            case OP_BLT_REL:
                branch(fetchOperand1(), CN ^ CV);
                computer.clockCount += 4;
                break;
            case OP_BMI_REL:
                branch(fetchOperand1(), CN);
                computer.clockCount += 4;
                break;
            case OP_BNE_REL:
                branch(fetchOperand1(), !CZ);
                computer.clockCount += 4;
                break;
            case OP_BVC_REL:
                branch(fetchOperand1(), !CV);
                computer.clockCount += 4;
                break;
            case OP_BVS_REL:
                branch(fetchOperand1(), CV);
                computer.clockCount += 4;
                break;
            case OP_BPL_REL:
                branch(fetchOperand1(), !CN);
                computer.clockCount += 4;
                break;
            case OP_BSR_REL:
                bsr(fetchOperand1());
                computer.clockCount += 8;
                break;
            case OP_JMP_IND:
                jump((short) ((IX & 0xffff) + (fetchOperand1() & 0xff)));
                computer.clockCount += 4;
                break;
            case OP_JMP_EXT:
                jump(fetchOperand2());
                computer.clockCount += 3;
                break;
            case OP_JSR_IND:
                jsr((short) ((IX & 0xffff) + (fetchOperand1() & 0xff)));
                computer.clockCount += 8;
                break;
            case OP_JSR_EXT:
                jsr(fetchOperand2());
                computer.clockCount += 9;
                break;
            case OP_NOP_IMP:
                computer.clockCount += 2;
                break;
            case OP_RTI_IMP:
                rti();
                computer.clockCount += 10;
                break;
            case OP_RTS_IMP:
                rts();
                computer.clockCount += 5;
                break;
            case OP_SWI_IMP:
                swi();
                computer.clockCount += 12;
                break;
            case OP_WAI_IMP:
                wai();
                computer.clockCount += 9;
                break;
            case OP_CLC_IMP:
                clc();
                computer.clockCount += 2;
                break;
            case OP_CLI_IMP:
                cli();
                computer.clockCount += 2;
                break;
            case OP_CLV_IMP:
                clv();
                computer.clockCount += 2;
                break;
            case OP_SEC_IMP:
                sec();
                computer.clockCount += 2;
                break;
            case OP_SEI_IMP:
                sei();
                computer.clockCount += 2;
                break;
            case OP_SEV_IMP:
                sev();
                computer.clockCount += 2;
                break;
            case OP_TAP_IMP:
                tap();
                computer.clockCount += 2;
                break;
            case OP_TPA_IMP:
                tpa();
                computer.clockCount += 2;
                break;
            // 以下MB8861の拡張命令
            case OP_NIM_IND:
                value = fetchOperand1();
                offset = fetchOperand1();
                store8_ind(offset, nim(value, load8_ind(offset)));
                computer.clockCount += 8;
                break;
            case OP_OIM_IND:
                value = fetchOperand1();
                offset = fetchOperand1();
                store8_ind(offset, oim(value, load8_ind(offset)));
                computer.clockCount += 8;
                break;
            case OP_XIM_IND:
                value = fetchOperand1();
                offset = fetchOperand1();
                store8_ind(offset, xim(value, load8_ind(offset)));
                computer.clockCount += 8;
                break;
            case OP_TMM_IND:
                tmm(fetchOperand1(), load8_ind(fetchOperand1()));
                computer.clockCount += 7;
                break;
            case OP_ADX_IMM:
                IX = add16(IX, (short) (fetchOperand1() & 0xff));
                computer.clockCount += 3;
                break;
            case OP_ADX_EXT:
                IX = add16(IX, load16_ext(fetchOperand2()));
                computer.clockCount += 7;
                break;
            default:
                nop();
                computer.clockCount += 1;
                break;
            }
        }
        return computer.clockCount - (initial_clock + clocks);
    }
    //
    // public void dumpRegister() {
    //     System.out.printf("computer.clockCount:%d PC:%04x A:%02x B:%02x IX:%04x SP:%04X H:%b I:%b N:%b Z:%b V:%b C:%b\n", computer.clockCount, (short) PC, A, B, IX, SP, CH, CI, CN, CZ, CV, CC);
    // }

    public void saveState(StateSet ss) {
        ss.set("MB8861.A", A);
        ss.set("MB8861.B", B);
        ss.set("MB8861.IX", IX);
        ss.set("MB8861.SP", SP);
        ss.set("MB8861.PC", PC);
        ss.set("MB8861.CH", CH);
        ss.set("MB8861.CI", CI);
        ss.set("MB8861.CN", CN);
        ss.set("MB8861.CZ", CZ);
        ss.set("MB8861.CV", CV);
        ss.set("MB8861.CC", CC);
        ss.set("MB8861.resetStatus", resetStatus);
        ss.set("MB8861.nmiStatus", nmiStatus);
        ss.set("MB8861.irqStatus", irqStatus);
        ss.set("MB8861.haltStatus", haltStatus);
        ss.set("MB8861.haltProcessed", haltProcessed);
        ss.set("MB8861.fetchWai", fetchWai);
    }

    public void loadState(StateSet ss) {
        A = (Byte)ss.get("MB8861.A");
        B = (Byte)ss.get("MB8861.B");
        IX = (Short)ss.get("MB8861.IX");
        SP = (Short)ss.get("MB8861.SP");
        PC = (Short)ss.get("MB8861.PC");
        CH = (Boolean)ss.get("MB8861.CH");
        CI = (Boolean)ss.get("MB8861.CI");
        CN = (Boolean)ss.get("MB8861.CN");
        CZ = (Boolean)ss.get("MB8861.CZ");
        CV = (Boolean)ss.get("MB8861.CV");
        CC = (Boolean)ss.get("MB8861.CC");
        resetStatus = (Boolean)ss.get("MB8861.resetStatus");
        nmiStatus = (Boolean)ss.get("MB8861.nmiStatus");
        irqStatus = (Boolean)ss.get("MB8861.irqStatus");
        haltStatus = (Boolean)ss.get("MB8861.haltStatus");
        haltProcessed = (Boolean)ss.get("MB8861.haltProcessed");
        fetchWai = (Boolean)ss.get("MB8861.fetchWai");
    }
}
