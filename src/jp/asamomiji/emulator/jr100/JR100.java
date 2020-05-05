/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import jp.asamomiji.emulator.Application;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.MemorySystem;
import jp.asamomiji.emulator.UnmappedMemory;
import jp.asamomiji.emulator.device.Beep;
import jp.asamomiji.emulator.device.MB8861;
import jp.asamomiji.emulator.device.gamepad.Gamepad;

/*
 * このクラスはJR-100をエミュレートする。
 */
public final class JR100 extends Computer {
    public final static String PROPERTY_EXTENDED_RAM = "jr100.extended_ram";
    public final static String PROPERTY_CPU_CLOCK_FREQUENCY = "jr100.cpu_clock_frequency";

    public final static int ADDRESS_START_OF_BASIC_PROGRAM = 0x0246;
    public final static int WORKAREA_END_OF_BASIC_PROGRAM = 0x06;

    public static final int DEFAULT_CPU_CLOCK = 894 * 1000;
    private static final int MEMORY_CAPACITY = 65536;
    private static final double REFRESH_RATE = 1.0 / 50.0;

    private static int clockFrequency = DEFAULT_CPU_CLOCK;


    public JR100(Application application, String fname) {
        super(application, REFRESH_RATE);

        // メモリ空間の初期化
        MemorySystem m = getHardware().getMemory();
        m.allocateSpace(MEMORY_CAPACITY);
        if (getPropertyOfUseExtendedRam()) {
            m.registMemory(new MainRam(0x0000, 0x8000));
        }
        else {
            m.registMemory(new MainRam(0x0000, 0x4000));
        }
        m.registMemory(new UserDefinedCharacterRam(0xc000, 0x100));
        m.registMemory(new VideoRam(0xc100, 0x300));
        m.registMemory(new ExtendedIOPort(this, 0xcc00));
        m.registMemory(new BasicRom(fname, 0xe000, 0x2000));

        // CPUの設定
        setCPU(new MB8861(this));

        // VIAの設定とメモリ空間へのマッピング
        JR100R6522 via = new JR100R6522(this, 0xc800);
        m.registMemory(via);

        // デバイスの設定
        getHardware().setSoundProcessor(new Beep(this, 44100.0));
        getHardware().setKeyboard(new JR100Keyboard(this));
        getHardware().setGamepad(new Gamepad(this));

        JR100Display display = new JR100Display(this);
        getHardware().setDisplay(display);
        UserDefinedCharacterRam um =
            (UserDefinedCharacterRam)m.getMemory(UserDefinedCharacterRam.class);
        um.setDisplay(display);
        VideoRam vm =
            (VideoRam)m.getMemory(VideoRam.class);
        vm.setDisplay(display);

        getDevices().add(via);
        getDevices().add(getHardware().getKeyboard());
        getDevices().add(display);
        getDevices().add(getHardware().getSoundProcessor());
    }

    public static boolean getPropertyOfUseExtendedRam() {
        String m = Application.getProperties().getProperty(PROPERTY_EXTENDED_RAM);
        if (m == null) {
            return false;
        }
        else {
            return Boolean.parseBoolean(m);
        }
    }

    public static void setPropertyOfUseExtendedRam(boolean value) {
        Application.getProperties().setProperty(PROPERTY_EXTENDED_RAM, Boolean.toString(value));
    }

    public static int getPropertyOfCpuClockFrequency() {
        String m = Application.getProperties().getProperty(PROPERTY_CPU_CLOCK_FREQUENCY);
        if (m == null) {
            return DEFAULT_CPU_CLOCK;
        }
        else {
            return Integer.parseInt(m);
        }
    }

    public static void setPropertyOfCpuClockFrequency(int value) {
        Application.getProperties().setProperty(PROPERTY_CPU_CLOCK_FREQUENCY, Integer.toString(value));
    }

    @Override
    public int getClockFrequency() {
        return clockFrequency;
    }

    @Override
    public void setClockFrequency(int f) {
        clockFrequency = f;
    }

    @Override
    public void powerOn() {
        setClockFrequency(getPropertyOfCpuClockFrequency());

        if (getPropertyOfUseExtendedRam()) {
            getHardware().getMemory().registMemory(new MainRam(0x0000, 0x8000));
        }
        else {
            getHardware().getMemory().registMemory(new MainRam(0x0000, 0x4000));
            getHardware().getMemory().registMemory(new UnmappedMemory(0x4000, 0x4000));
        }
        super.powerOn();
    }
}
