/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device;

import jp.asamomiji.emulator.Addressable;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.Device;
import jp.asamomiji.emulator.StateSavable;
import jp.asamomiji.emulator.StateSet;

/**
 * R6522をエミュレートするクラス
 *
 * - メモリマップして使うことを想定し、{@link Addressable}インターフェースを実装
 * - デバイスとして使用するため、{@link Device}インターフェースを実装
 * - 状態を持つデバイスのため、{@link StateSavable}インターフェースを実装し、中断状態からの復元を可能にする。
 */
public class R6522 implements Addressable, Device, StateSavable {
    protected Computer computer;

    private int startAddr;
    private int endAddr;

    // レジスタ群
    // T1CL, T1CH, T1LL, T1LH, T2CL, T2CHはそれぞれ16bitのtimer1, latch1, timer2で内部は表現する。
    protected byte IFR;    // bit7がIRQの状態を表わす。
    protected byte IER;
    protected byte PCR;
    protected byte ACR;
    protected byte IRA;
    protected byte ORA;
    protected byte DDRA;
    protected byte IRB;
    protected byte ORB;
    protected byte DDRB;
    protected byte SR;

    public final static int VIA_REG_IORB	= 0x00;
    public final static int VIA_REG_IORA	= 0x01;
    public final static int VIA_REG_DDRB	= 0x02;
    public final static int VIA_REG_DDRA	= 0x03;
    public final static int VIA_REG_T1CL	= 0x04;
    public final static int VIA_REG_T1CH	= 0x05;
    public final static int VIA_REG_T1LL	= 0x06;
    public final static int VIA_REG_T1LH	= 0x07;
    public final static int VIA_REG_T2CL	= 0x08;
    public final static int VIA_REG_T2CH	= 0x09;
    public final static int VIA_REG_SR		= 0x0a;
    public final static int VIA_REG_ACR		= 0x0b;
    public final static int VIA_REG_PCR		= 0x0c;
    public final static int VIA_REG_IFR		= 0x0d;
    public final static int VIA_REG_IER		= 0x0e;
    public final static int VIA_REG_IORANH	= 0x0f;

    // 制御線
    protected byte portA;	// 8bitポート
    protected byte portB;	// 8bitポート
    protected int CA1in;
    protected int CA2in;
    protected int CA2out;
    protected int CA2timer;
    protected int CB1in;
    protected int CB1out;
    protected int CB2in;
    protected int CB2out;

    // 割り込みフラグ
    protected final static int IFR_BIT_CA2 = 0x01;
    protected final static int IFR_BIT_CA1 = 0x02;
    protected final static int IFR_BIT_SR  = 0x04;
    protected final static int IFR_BIT_CB2 = 0x08;
    protected final static int IFR_BIT_CB1 = 0x10;
    protected final static int IFR_BIT_T2  = 0x20;
    protected final static int IFR_BIT_T1  = 0x40;
    protected final static int IFR_BIT_IRQ = 0x80;

    private int previousPB6;

    protected short latch1 = 0;
    protected short latch2 = 0;
    protected short timer1 = 0;
    protected short timer2 = 0;

    protected boolean shiftTick = false;
    protected int shiftCounter = 0; // シフトを8回カウントするための内部レジスタ
    protected boolean shiftStarted = false;

    protected boolean timer1Initialized = false;
    protected boolean timer1Enable = false;
    protected boolean timer2Initialized = false;
    protected boolean timer2Enable = false;
    protected boolean timer2LowByteTimeout = false;

    protected long currentClock = 0;

    public R6522(Computer computer, int start) {
        this.computer = computer;
        startAddr = start;
        endAddr = start + 0xf;
    }

    public int getStartAddress() {
        return startAddr;
    }

    public int getEndAddress() {
        return endAddr;
    }

    /**
     * IRQの状態が変化したときの割り込み処理を実行する。
     * 実際の処理は{@link R6522#handlerIRQ(int)}の中で記述する。
     *
     */
    private void processIRQ() {
        if ((IER & IFR & 0x7f) != 0) {
            if ((IFR & 0x80) == 0) {
                IFR |= 0x80;
                handlerIRQ(1);
            }

        }
        else {
            if ((IFR & 0x80) != 0) {
                IFR &= ~0x80;
                handlerIRQ(0);
            }
        }
    }

    /**
     * 割り込みフラグをセットする。
     *
     * @param value セットしたいフラグ集合
     */
    private void setInterrupt(int value) {
        if ((IFR & value) == 0) {
            IFR |= value;
            processIRQ();
        }
    }

    /**
     * 割り込みフラグをクリアする。
     *
     * @param value クリアしたいフラグ集合
     */
    private void clearInterrupt(int value) {
        if ((IFR & value) != 0) {
            IFR &= ~value;
            processIRQ();
        }
    }

    private boolean isSetInInterrupts(int value) {
        return (IFR & value) != 0;
    }

    @SuppressWarnings("unused")
    private boolean isClearedInInterrupts(int value) {
        return (IFR & value) == 0;
    }

    /**
     * 割り込み発生時の処理を記述する。
     * 6522のIRQは負論理だが、引数で指定する状態は正論理とする。
     * (statusが0ならば"clear"、1ならば"assert"を意味する)
     *
     * @param state 割り込み線の状態 (0または1)
     */
    public void handlerIRQ(int state) {
    }

    /**
     * portAの指定のビットに状態をセットする。
     * portAが出力に設定されているビット(DDRAの対応するビットが1)の場合は何もしない。
     * 入力ラッチが無効の場合はportAへの状態のセットと同時にIRAにも反映する。
     *
     * @param bit 状態をセットするMSBでのビット位置(0から7、最上位がbit7)
     * @param state セットする状態 (0または1)
     *
     */
    public void setPortA(int bit, int state) {
        int mask = 1 << bit;
        if ((DDRA & mask) != 0) {
            return ;
        }
        if (state == 1) {
            portA |= mask;
        }
        else {
            portA &= ~mask;
        }
        if ((ACR & 0x01) == 0) {
            IRA = portA;
        }
    }

    /**
     * portAに値をセットする。
     * portAが出力に設定されているビット(DDRAの対応するビットが1)はportAの値を変更しない。
     * 入力ラッチが無効の場合はportAへの状態のセットと同時にIRAにも反映する。
     *
     * @param value セットする値
     */
    public void setPortA(byte value) {
        portA = (byte)((portA & DDRA) | (value & ~DDRA));
        if ((ACR & 0x01) == 0) {
            IRA = portA;
        }
    }

    /**
     * portAの入力状態を取得する。
     * 取得される値はピンの入出力の方向により以下となる。
     * <ul>
     *   <li>入力用のピンはIRAの値
     *   <li>出力用のピンは現時点でのportAの状態(Highなら1、Lowなら0)
     * </ul>
     * また
     *
     * @return portAの値 (0から255)
     */
    protected byte inputPortA() {
        return (byte)((IRA & ~DDRA) | (portA & DDRA));
    }

    /**
     * portAの指定したビットの入力状態を取得する。
     * 取得される値はピンの入出力の方向により以下となる。
     * <ul>
     *   <li>入力用のピンはIRAの値
     *   <li>出力用のピンは現時点でのportAの状態(Highなら1、Lowなら0)
     * </ul>
     * また
     *
     * @bit ビット位置
     * @return portBの値 (0から255)
     */
    protected int inputPortA(int bit) {
        return (inputPortA() & (1 << bit)) >> bit;
    }

    /**
     * portAに現時点の内部状態を出力する。
     *
     */
    protected void outputPortA() {
        handlerPortA(ORA);
    }

    /**
     * portAの出力状態が変化したときの処理を記述する。
     *
     * @param state portAの値
     */
    public void handlerPortA(byte state) {
    }

    /**
     * CA1制御線に状態を設定する。
     *
     * @param state 設定する状態(0または1)
     */
    public void setCA1(int state) {
        if (CA1in != state) {
            CA1in = state;
            if ((CA1in == 1) && ((PCR & 0x01) == 0x01) ||	// 立ち上がりエッジ
                (CA1in == 0) && ((PCR & 0x01) == 0x00)) {	// 立ち下がりエッジ
                if ((ACR & 0x01) == 0x01) {	// portA latch enabled
                    IRA = inputPortA();
                }
                setInterrupt(IFR_BIT_CA1);
                if (CA2out == 0 && (PCR & 0x0e) == 0x08) { // "data taken" handshake mode
                    CA2out = 1;
                    handlerCA2(CA2out);
                }
            }
        }
    }

    /**
     * CA2制御線に状態を設定する。
     *
     * @param state 設定する状態(0または1)
     */
    public void setCA2(int state) {
        if (CA2in != state) {
            CA2in = state;
            if ((PCR & 0x08) == 0x00) {
                if ((CA2in == 1) && ((PCR & 0x0c) == 0x04) ||	// 立ち上がりエッジ
                    (CA2in == 0) && ((PCR & 0x0c) == 0x00)) {	// 立ち下がりエッジ
                    setInterrupt(IFR_BIT_CA2);
                }
            }
        }
    }

    /**
     * CA2制御線の状態が変化したときのハンドラを記述する。
     *
     * 以下を前提とする。
     * <ul>
     *   <li>ハンドラは制御線が出力モードの時に呼び出される。
     * </ul>
     *
     * @param status 変化後のCA2線の状態 (0または1)
     */
    protected void handlerCA2(int status) {
    }

    /**
     * portBの指定のビットに状態をセットする。
     * portBが出力に設定されているビット(DDRBの対応するビットが1)の場合は何もしない。
     * 入力ラッチが無効の場合はportBへの状態のセットと同時にIRBにも反映する。
     *
     * @param bit 状態をセットするMSBでのビット位置(0から7、最上位がbit7)
     * @param state セットする状態 (0または1)
     *
     */
    public void setPortB(int bit, int state) {
        int mask = 1 << bit;
        if ((DDRB & mask) != 0) {
            return ;
        }
        if (state == 1) {
            portB |= mask;
        }
        else {
            portB &= ~mask;
        }
        if ((ACR & 0x02) == 0) {
            IRB = portB;
        }
    }

    /**
     * portBに値をセットする。
     * portBが出力に設定されているビット(DDRBの対応するビットが1)はportBの値を変更しない。
     * 入力ラッチが無効の場合はportBへの状態のセットと同時にIRBにも反映する。
     *
     * @param value セットする値
     */
    public void setPortB(byte value) {
        portB = (byte)((portB & DDRB) | (value & ~DDRB));
        if ((ACR & 0x02) == 0) {
            IRB = portB;
        }
    }

    /**
     * portBの指定のビットに状態を反転する。
     * 指定したビットが出力に設定されている(DDRBの対応するビットが1)必要がある。
     * 入力ラッチが無効の場合はportBへの状態のセットと同時にIRBにも反映する。
     *
     * @param bit 状態を反転するMSBでのビット位置(0から7、最上位がbit7)
     *
     */
    protected void invertPortB(int bit) {
        int mask = 1 << bit;
        if ((DDRB & mask) != 0) {
            return ;
        }
        int state = (portB & mask) == 0 ? 0 : 1;
        if (state == 0) {
            portB |= mask;
        }
        else {
            portB &= ~mask;
        }
        if ((ACR & 0x02) == 0) {
            IRB = portB;
        }
    }

    /**
     * portBの入力状態を取得する。
     * 取得される値はピンの入出力の方向により以下となる。
     * <ul>
     *   <li>入力用のピンはIRBの値
     *   <li>出力用のピンはORBの値
     * </ul>
     * また
     *
     * @return portBの値 (0から255)
     */
    protected byte inputPortB() {
        return (byte)((IRB & ~DDRB) | (ORB & DDRB));
    }

    /**
     * portBの指定したビットの入力状態を取得する。
     * 取得される値はピンの入出力の方向により以下となる。
     * <ul>
     *   <li>入力用のピンはIRBの値
     *   <li>出力用のピンはORBの値
     * </ul>
     * また
     *
     * @bit ビット位置
     * @return portBの値 (0から255)
     */
    protected int inputPortB(int bit) {
        return (inputPortB() & (1 << bit)) >> bit;
    }

    /**
     * portBに内部状態を出力する。
     *
     */
    protected void outputPortB() {
        handlerPortB(ORB);
    }

    /**
     * portBの出力状態が変化したときの処理を記述する。
     *
     * @param state portBの値
     */
    public void handlerPortB(int state) {
    }

    /**
     * CB1制御線に状態を設定する。
     *
     * @param state 設定する状態(0または1)
     */
    public void setCB1(int state) {
        if (CB1in != state) {
            CB1in = state;
            if ((CB1in == 1) && ((PCR & 0x10) == 0x10) ||	// 立ち上がりエッジ
                (CB1in == 0) && ((PCR & 0x10) == 0x00)) {	// 立ち下がりエッジ
                if ((ACR & 0x02) == 0x02) {	// portB latch enabled
                    IRB = inputPortB();
                }
                if (shiftStarted && (ACR & 0x1c) == 0x0c) { // SR Mode3: CB1制御の下でシフトインする。
                    processShiftIn();
                }
                if (shiftStarted && (ACR & 0x1c) == 0x1c) {	// SR Mode7: CB1制御の下でシフトアウトする。
                    processShiftOut();
                }
                setInterrupt(IFR_BIT_CB1);
                if (CB2out == 0 && ((PCR & 0xc0) == 0x80)) { // "data taken" handshake mode
                    CB2out = 1;
                    handlerCB2(CB2out);
                }
            }
        }
    }

    /**
     * CB2制御線に状態を設定する。
     *
     * @param state 設定する状態(0または1)
     */
    public void setCB2(int state) {
        if (CB2in != state) {
            CB2in = state;
            if ((PCR & 0x80) == 0x00) {
                if ((CB2in == 1) && ((PCR & 0xc0) == 0x40) ||	// 立ち上がりエッジ
                    (CB2in == 0) && ((PCR & 0xc0) == 0x00)) {	// 立ち下がりエッジ
                    setInterrupt(IFR_BIT_CB2);
                }
            }
        }
    }

    /**
     * CB1線の状態が変化したときのハンドラを記述する。
     *
     * @param status 変化後のCB1線の状態 (0または1)
     */
    public void handlerCB1(int status) {
    }

    /**
     * CB2線の状態が変化したときのハンドラを記述する。
     *
     * @param status 変化後のCB2線の状態 (0または1)
     */
    public void handlerCB2(int status) {
    }

    /*
     * シフトレジスタ
     */

    /**
     * シフトイン処理を初期化する。
     */
    private void initializeShiftIn() {
        shiftTick = false;
        shiftCounter = 0;
        if (isSetInInterrupts(IFR_BIT_SR)) {
            clearInterrupt(IFR_BIT_SR);
            processShiftIn();
        }
        shiftStarted = true;
    }

    /**
     * シフトアウト処理を初期化する。
     */
    private void initializeShiftOut() {
        shiftTick = false;
        shiftCounter = 0;
        if (isSetInInterrupts(IFR_BIT_SR)) {
            clearInterrupt(IFR_BIT_SR);
            processShiftOut();
        }
        shiftStarted = true;
    }

    /**
     * シフトイン処理を実行する。
     * 外部デバイスへのシフトの通知と実際のシフトインを交互に実行する。
     *
     */
    private void processShiftIn() {
        if (shiftTick) {
            // do shift-in
            CB1out = 1;
            handlerCB1(CB1out);
            SR = (byte)(((SR << 1) | (CB2in & 0x01)) & 0xff);
            shiftCounter = (shiftCounter + 1) % 8;
            if (shiftCounter == 0) {
                setInterrupt(IFR_BIT_SR);
                shiftStarted = false;
            }
        }
        else {
            // notify shift-in
            // TODO: PCRで設定するエッジの向きは無視する。 (要確認)
            CB1out = 0;
            handlerCB1(CB1out);
        }
        shiftTick = !shiftTick;
    }

    /**
     * シフトアウト処理を実行する。
     * 外部デバイスへのシフトの通知と実際のシフトアウトを交互に実行する。
     *
     */
    private void processShiftOut() {
        if (shiftTick) {
            // do shift-out
            CB1out = 1;
            handlerCB1(CB1out);
            CB2out = (SR & 0x80) >> 7;
            handlerCB2(CB2out);
            SR = (byte)(((SR << 1) | (CB2out & 0x01)) & 0xff);
            if ((ACR & 0x1c) != 0x10) { // shift out free running at T2 rateではない場合
                shiftCounter = (shiftCounter + 1) % 8;
                if (shiftCounter == 0) {
                    setInterrupt(IFR_BIT_SR);
                    shiftStarted = false;
                }
            }
        }
        else {
            // notify shift-out
            // TODO: PCRで設定するエッジの向きは無視する。 (要確認)
            CB1out = 0;
            handlerCB1(CB1out);
        }
        shiftTick = !shiftTick;
    }

    public byte load8(int address) {
        int delay = 0;
        byte result = 0;
        execute(computer.getClockCount() - 1 + delay);
        switch (address - startAddr) {
        case VIA_REG_IORB:
            if ((ACR & 0x02) == 0) {  // portB latching disabled
                result = (byte)(inputPortB() & 0xff);
            }
            else {
                result = (byte)(IRB & 0xff);
            }
            clearInterrupt(IFR_BIT_CB1 | ((PCR & 0xa0) == 0x20 ? 0x00 : IFR_BIT_CB2));
            break;
        case VIA_REG_IORA:
            result = ((ACR & 0x01) == 0) ? inputPortA() : IRA;
            clearInterrupt(IFR_BIT_CA1 | ((PCR & 0x0a) == 0x02 ? 0x00 : IFR_BIT_CA2));
            // 自動ハンドシェークモードの処理
            // CA2が0の場合の処理は仕様書中に記述がないため実装はしていない。
            if ((CA2out == 1) && (((PCR & 0x0e) == 0x0a) || ((PCR & 0x0e) == 0x08))) {
                CA2out = 0;
                handlerCA2(CA2out);
                if ((PCR & 0x0e) == 0x08) {
                    // パルスモードの場合、１クロック後にCA2ラインを1に戻す。
                    CA2timer = 1;
                }
            }
            break;
        case VIA_REG_DDRB:     // DDRB
            result = DDRB;
            break;
        case VIA_REG_DDRA:     // DDRA
            result = DDRA;
            break;
        case VIA_REG_T1CL:     // T1CL
            clearInterrupt(IFR_BIT_T1);
            result = (byte)(timer1 & 0xff);
            break;
        case VIA_REG_T1CH:     // T1CH
            result = (byte)((timer1 & 0xff00) >> 8);
            break;
        case VIA_REG_T1LL:     // T1LL
            result = (byte)(latch1 & 0xff);
            break;
        case VIA_REG_T1LH:     // T1LH
            result = (byte)((latch1 & 0xff00) >> 8);
            break;
        case VIA_REG_T2CL:     // T2CL
            clearInterrupt(IFR_BIT_T2);
            result = (byte)(timer2 & 0xff);
            break;
        case VIA_REG_T2CH:     // T2CH
            result = (byte)((timer2 & 0xff00) >> 8);
            break;
        case VIA_REG_SR:	// SR
            switch (ACR & 0x1c) {
            case 0x00:
                // TODO: 後で実装する。
                break;
            case 0x04:
            case 0x08:
            case 0x0c:
                initializeShiftIn();
                break;
            case 0x10:
            case 0x14:
            case 0x18:
            case 0x1c:
                initializeShiftOut();
                break;
            default:
                throw new AssertionError("invalid sr mode " + (ACR & 0x1c));
            }
            result = SR;
            break;
        case VIA_REG_ACR:	// ACR
            result = ACR;
            break;
        case VIA_REG_PCR:	// PCR
            result = PCR;
            break;
        case VIA_REG_IFR:	// IFR
            result = IFR;
            break;
        case VIA_REG_IER:	// IER
            result = (byte)(IER | 0x80);
            break;
        case VIA_REG_IORANH:
            result = ((ACR & 0x01) == 0) ? inputPortA() : IRA;
            break;
        default:
            throw new AssertionError("invalid register" + address);
        }
        execute(computer.getClockCount() + delay);
        return result;
    }

    public short load16(int address) {
        throw new RuntimeException("not supported");
    }

    public void store16(int address, short value) {
        throw new RuntimeException("not supported");
    }

    protected void storeORB_option() {
    }

    protected void storeIORA_option() {
    }

    protected void storeDDRB_option() {
    }

    protected void storeDDRA_option() {
    }

    protected void storeT1CL_option() {
    }

    protected void storeT1CH_option() {
    }

    protected void storeT1LL_option() {
    }

    protected void storeT1LH_option() {
    }

    protected void storeT2CL_option() {
    }

    protected void storeT2CH_option() {
    }

    protected void storeSR_option() {
    }

    protected void storeACR_option() {
    }

    protected void storePCR_option() {
    }

    protected void storeIFR_option() {
    }

    protected void storeIER_option() {
    }

    protected void storeIORA_NOHS_option() {
    }

    public void store8(int address, byte value) {
        int delay = 0;
        execute(computer.getClockCount() - 1 + delay);
        switch (address - startAddr) {
        case VIA_REG_IORB:
            ORB = value;
            outputPortB();
            clearInterrupt(IFR_BIT_CB1 | ((PCR & 0xa0) == 0x20 ? 0x00 : IFR_BIT_CB2));
            if ((CB2out == 1) && ((PCR & 0xc0) == 0x80)) {
                CB2out = 0;
                handlerCB2(CB2out);
            }
            storeORB_option();
            break;
        case VIA_REG_IORA:
            ORA = value;
            if (DDRA != 0x00) {
                outputPortA();
            }
            clearInterrupt(IFR_BIT_CA1 | ((PCR & 0x0a) == 0x02 ? 0x00 : IFR_BIT_CA2));
            if (CA2out == 1 && (((PCR & 0x0e) == 0x0a) || (PCR & 0x0c) == 0x08)) {
                CA2out = 0;
                handlerCA2(CA2out);
            }
            if ((PCR & 0x0e) == 0x0a) {
                CA2timer = 1;
            }
            storeIORA_option();
            break;
        case VIA_REG_DDRB:
            DDRB = value;
            storeDDRB_option();
            break;
        case VIA_REG_DDRA:
            DDRA = value;
            storeDDRA_option();
            break;
        case VIA_REG_T1CL:
            latch1 &= 0xff00;
            latch1 |= value & 0xff;
            storeT1CL_option();
            break;
        case VIA_REG_T1CH:
            latch1 &= 0x00ff;
            latch1 |= (value << 8) & 0xff00;
            timer1 = latch1;
            timer1Initialized = true;
            timer1Enable = true;
            setPortB(7, 0); // PB7をLowにセット
            storeT1CH_option();
            break;
        case VIA_REG_T1LL:
            latch1 &= 0xff00;
            latch1 |= value & 0xff;
            storeT1LL_option();
            break;
        case VIA_REG_T1LH:
            latch1 &= 0x00ff;
            latch1 |= (value << 8) & 0xff00;
            storeT1LH_option();
            break;
        case VIA_REG_T2CL:
            latch2 &= 0xff00;
            latch2 |= value & 0xff;
            storeT2CL_option();
            break;
        case VIA_REG_T2CH:
            latch2 &= 0x00ff;
            latch2 |= (value << 8) & 0xff00;
            timer2 = latch2;
            clearInterrupt(IFR_BIT_T2);
            timer2Initialized = true;
            timer2Enable = true;
            storeT2CH_option();
            break;
        case VIA_REG_SR:
            switch (ACR & 0x1c) {
            case 0x04:
            case 0x08:
            case 0x0c:
                initializeShiftIn();
                break;
            case 0x10:
            case 0x14:
            case 0x18:
            case 0x1c:
                initializeShiftOut();
                break;
            default:
                throw new AssertionError("invalid sr mode " + address);
            }
            SR = value;
            storeSR_option();
            break;
        case VIA_REG_ACR:
            ACR = value;
            storeACR_option();
            break;
        case VIA_REG_PCR:
            PCR = value;
            storePCR_option();
            break;
        case VIA_REG_IFR:
            if ((value & 0x80) == 0x80) {
                value = 0x7f;
            }
            clearInterrupt(value);
            storeIFR_option();
            break;
        case VIA_REG_IER:
            IER = value;
            storeIER_option();
            break;
        case VIA_REG_IORANH:
            ORA = value;
            if (DDRA != 0x00) {
                outputPortA();
            }
            storeIORA_NOHS_option();
            break;
        default:
            throw new AssertionError("invalid register" + address);
        }
        execute(computer.getClockCount() + delay);
    }

    protected void timer1TimeoutMode0_option() {
    }

    protected void timer1TimeoutMode1_option() {
    }

    protected void timer1TimeoutMode2_option() {
    }

    protected void timer1TimeoutMode3_option() {
    }

    private void execute(long clock) {
        while (currentClock <= clock) {
            // 制御線のパルスモード処理
            if (CA2timer >= 0) {
                CA2timer--;
                if (CA2timer < 0) {
                    CA2out = 1;
                    handlerCA2(CA2out);
                }
            }

            // タイマ1
            // 実機ではΦ2の立ち下がりでデクリメントされるが、MPUからの読み出しはデクリメント前の値が読み出される。
            // T1CHへの書き込み時にPB7をLowにする(反転させるのではない)。その後タイムアウトでHighにする。
            if (timer1Initialized) {
                timer1Initialized = false;
            }
            else if (timer1 >= 0) {
                timer1--;
            }
            else {
                if (timer1Enable) {
                    setInterrupt(IFR_BIT_T1);
                    switch (ACR & 0xc0) {
                    case 0x00: // Timed interrupt each timeOffset T1 is loaded. PB7 Disabled
                        timer1Enable = false;
                        timer1TimeoutMode0_option();
                        break;
                    case 0x40: // Continuous interrupt.
                        invertPortB(7);
                        timer1TimeoutMode1_option();
                        break;
                    case 0x80: // Timed interrupt each timeOffset T1 is loaded. One-shot output
                        timer1Enable = false;
                        setPortB(7, 1);
                        timer1TimeoutMode2_option();
                        break;
                    case 0xc0: // Continuous interrupt. Square wave output
                        invertPortB(7);
                        timer1TimeoutMode3_option();
                        break;
                    default:
                        throw new AssertionError("invalid t1mode: " + (ACR & 0xc0));
                    }
                }
                timer1 = latch1;
                storeT1CH_option();
            }

            // タイマ2

            // Timer2用のPB6の処理
            int currentPB6 = inputPortB() & 0x40;
            boolean PB6negative = (previousPB6 != 0 && currentPB6 == 0);
            previousPB6 = currentPB6;

            if (timer2 >= 0) {
                switch (ACR & 0x20) {
                case 0x00:  // Timed interrupt mode
                    if (timer2Initialized) {
                        timer2Initialized = false;
                    }
                    else {
                        timer2--;
                    }
                    break;
                case 0x20:  // pulse count mode
                    // PB6の立ち下がりでカウントダウンする。
                    if (PB6negative) {
                        timer2--;
                    }
                    break;
                default:
                    throw new AssertionError("invalid t2mode: " + (ACR & 0x20));
                }
            }
            else {
                if (timer2Enable) {
                    setInterrupt(IFR_BIT_T2);
                    timer2Enable = false;
                }
                if (shiftStarted && (timer2 & 0xff) == 0xff) {  // 下位バイトがタイムアウトした場合
                    if ((ACR & 0x1c) == 0x04) { // SR mode 1
                        processShiftIn();
                    }
                    else if ((ACR & 0x1c) == 0x10 || (ACR & 0x1c) == 0x14) {    // sr mode 4 or 5
                        processShiftOut();
                    }
                }
                timer2 = latch2;
            }

            // シフトレジスタ
            // mode2とmode6以外はTimer2処理またはCB1処理の中で処理を記述する。
            if ((ACR & 0x1c) == 0x08) { // mode 2
                processShiftIn();
            }
            else if ((ACR & 0x1c) == 0x18) {    // mode 6
                processShiftOut();
            }

            currentClock++;
        }
    }

    public void reset() {
        portA = 0;
        portB = 0;
        CA1in = 0;
        CA2in = 0;
        CA2out = 0;
        CB1in = 0;
        CB1out = 0;
        CB2in = 0;
        CB2out = 0;
        CA2timer = -1;

        latch1 = 0;
        latch2 = 0;
        timer1 = 0;
        timer2 = 0;

        timer1Enable = false;
        timer2Enable = false;
        timer2LowByteTimeout = false;

        previousPB6 = 0;

        shiftTick = false;
        shiftStarted = false;
        shiftCounter = 0;

        currentClock = 0;
    }

    public void execute() {
        execute(computer.getClockCount());
    }

    public void saveState(StateSet ss) {
        ss.set("R6522.startAddr", startAddr);
        ss.set("R6522.endAddr", endAddr);
        ss.set("R6522.IRB", IRB);
        ss.set("R6522.ORB", ORB);
        ss.set("R6522.IRA", IRA);
        ss.set("R6522.ORA", ORA);
        ss.set("R6522.DDRB", DDRB);
        ss.set("R6522.DDRA", DDRA);
        ss.set("R6522.SR", SR);
        ss.set("R6522.ACR", ACR);
        ss.set("R6522.PCR", PCR);
        ss.set("R6522.IFR", IFR);
        ss.set("R6522.IER", IER);
        ss.set("R6522.portA", portA);
        ss.set("R6522.portB", portB);
        ss.set("R6522.CA1in", CA1in);
        ss.set("R6522.CA2in", CA2in);
        ss.set("R6522.CA2out", CA2out);
        ss.set("R6522.CA2timer", CA2timer);
        ss.set("R6522.CB1in", CB1in);
        ss.set("R6522.CB1out", CB1out);
        ss.set("R6522.CB2in", CB2in);
        ss.set("R6522.CB2out", CB2out);
        ss.set("R6522.previousPB6", previousPB6);
        ss.set("R6522.latch1", latch1);
        ss.set("R6522.latch2", latch2);
        ss.set("R6522.timer1", timer1);
        ss.set("R6522.timer2", timer2);
        ss.set("R6522.shiftTick", shiftTick);
        ss.set("R6522.shiftStarted", shiftStarted);
        ss.set("R6522.shiftCounter", shiftCounter);
        ss.set("R6522.timer1Initialized", timer1Initialized);
        ss.set("R6522.timer1Enable", timer1Enable);
        ss.set("R6522.timer2Initialized", timer2Initialized);
        ss.set("R6522.timer2Enable", timer2Enable);
        ss.set("R6522.timer2LowByteTimeout", timer2LowByteTimeout);
        ss.set("R6522.currentClock", currentClock);
    }

    public void loadState(StateSet ss) {
        startAddr = (Integer)ss.get("R6522.startAddr");
        endAddr = (Integer)ss.get("R6522.endAddr");
        IRB = (Byte)ss.get("R6522.IRB");
        ORB = (Byte)ss.get("R6522.ORB");
        IRA = (Byte)ss.get("R6522.IRA");
        ORA = (Byte)ss.get("R6522.ORA");
        DDRB = (Byte)ss.get("R6522.DDRB");
        DDRA = (Byte)ss.get("R6522.DDRA");
        SR = (Byte)ss.get("R6522.SR");
        ACR = (Byte)ss.get("R6522.ACR");
        PCR = (Byte)ss.get("R6522.PCR");
        IFR = (Byte)ss.get("R6522.IFR");
        IER = (Byte)ss.get("R6522.IER");
        portA = (Byte)ss.get("R6522.portA");
        portB = (Byte)ss.get("R6522.portB");
        CA1in = (Integer)ss.get("R6522.CA1in");
        CA2in = (Integer)ss.get("R6522.CA2in");
        CA2out = (Integer)ss.get("R6522.CA2out");
        CA2timer = (Integer)ss.get("R6522.CA2timer");
        CB1in = (Integer)ss.get("R6522.CB1in");
        CB1out = (Integer)ss.get("R6522.CB1out");
        CB2in = (Integer)ss.get("R6522.CB2in");
        CB2out = (Integer)ss.get("R6522.CB2out");
        previousPB6 = (Integer)ss.get("R6522.previousPB6");
        latch1 = (Short)ss.get("R6522.latch1");
        latch2 = (Short)ss.get("R6522.latch2");
        timer1 = (Short)ss.get("R6522.timer1");
        timer2 = (Short)ss.get("R6522.timer2");
        shiftTick = (Boolean)ss.get("R6522.shiftTick");
        shiftStarted = (Boolean)ss.get("R6522.shiftStarted");
        shiftCounter = (Integer)ss.get("R6522.shiftCounter");
        timer1Initialized = (Boolean)ss.get("R6522.timer1Initialized");
        timer1Enable = (Boolean)ss.get("R6522.timer1Enable");
        timer2Initialized = (Boolean)ss.get("R6522.timer2Initialized");
        timer2Enable = (Boolean)ss.get("R6522.timer2Enable");
        timer2LowByteTimeout = (Boolean)ss.get("R6522.timer2LowByteTimeout");
        currentClock = (Long)ss.get("R6522.currentClock");
    }
}
