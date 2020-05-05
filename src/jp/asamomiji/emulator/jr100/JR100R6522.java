/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */

package jp.asamomiji.emulator.jr100;

import jp.asamomiji.emulator.AbstractSoundProcessor;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.device.R6522;

/*
 * JR100向けにカスタマイズしたR6522クラス
 */
public final class JR100R6522 extends R6522 {
    private double prevFrequency = 0;

    public JR100R6522(Computer computer, int start) {
        super(computer, start);
    }

    private void jumperPB7andPB6() {
        setPortB(6, inputPortB(7));
    }

    @Override
    protected void storeORB_option() {
        JR100Display d = (JR100Display)computer.getHardware().getDisplay();
        if ((inputPortB() & 0x20) == 0x20) {
            d.setCurrentFont(JR100Display.FONT_USER_DEFINED);
        }
        else {
            d.setCurrentFont(JR100Display.FONT_NORMAL);
        }
        jumperPB7andPB6();
    }

    @Override
    protected void storeIORA_option() {
        int[] m = ((JR100Keyboard)computer.getHardware().getKeyboard()).getKeyMatrix();
        byte value = inputPortB();
        value &= 0xe0;
        value |= (~m[ORA & 0x0f]) & 0x1f;
        setPortB(value);
    }

    @Override
    protected void storeT1CH_option() {
        AbstractSoundProcessor sp = computer.getHardware().getSoundProcessor();
        if ((ACR & 0xc0) == 0xc0) {
            double frequency = (double)(894886.25 / (timer1 + 2) / 2);
            if (frequency == prevFrequency) {
                sp.setLineOn();
                return ;
            }
            prevFrequency = frequency;
            sp.setFrequency(
                    currentClock * 1000000000 / JR100.getPropertyOfCpuClockFrequency() + computer.getBaseTime(),
                    frequency);
            sp.setLineOn();
        }
        else {
            sp.setLineOff();
        }
    }

    @Override
    protected void timer1TimeoutMode0_option() {
        computer.getHardware().getSoundProcessor().setLineOff();
    }

    @Override
    protected void timer1TimeoutMode2_option() {
        jumperPB7andPB6();
    }

    @Override
    protected void timer1TimeoutMode3_option() {
        jumperPB7andPB6();
    }
}
