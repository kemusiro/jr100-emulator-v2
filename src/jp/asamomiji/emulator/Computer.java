/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import jp.asamomiji.emulator.device.gamepad.AxisKeyboardKeyProcessor;
import jp.asamomiji.emulator.device.gamepad.ButtonKeyboardKeyProcessor;
import jp.asamomiji.emulator.device.gamepad.ComponentStatus;
import jp.asamomiji.emulator.device.gamepad.Gamepad;
import net.java.games.input.Controller;

/*
 * この抽象クラスはエミュレートするコンピュータシステムのルートとなるクラスである。
 * エミュレートする対象は、すべてこのクラスのオブジェクトからたどることができる。
 */
public abstract class Computer implements StateSavable, Runnable {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_PAUSED = 1;
    public static final int STATUS_STOPPED = 2;

    public final static String PROPERTY_AUTOMATIC_POWERON = "system.automatic_power_on";

    /**
     * エミュレータが起動してからのクロック数
     */
    public long clockCount = 0;

    private EventQueue eventQueue = new EventQueue();
    private double refreshRate;
    private CPU cpu;
    private Vector<Device> devices = new Vector<Device>();
    private Application application;
    private AbstractHardware hardware;
    private long intervalClocks;
    private int runningStatus;
    private Program program;
    private TimeManager timeManager;
    private long baseTime;

    public Computer(Application application, double refresh_rate) {
        this.application = application;
        this.refreshRate = refresh_rate;
        this.hardware = new AbstractHardware();
        clockCount = 0;
    }

    public static boolean getPropertyOfAutomaticPowerOn() {
        return Boolean.parseBoolean(
            Application.getProperties().getProperty(PROPERTY_AUTOMATIC_POWERON, "true"));
    }

    public static void setPropertyOfAutomaticPowerOn(boolean value) {
        Application.getProperties().setProperty(PROPERTY_AUTOMATIC_POWERON, Boolean.toString(value));
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public long getBaseTime() {
        return baseTime;
    }

    public CPU getCPU() {
        return cpu;
    }

    public void setCPU(CPU cpu) {
        this.cpu = cpu;
    }

    public AbstractHardware getHardware() {
        return hardware;
    }

    public void setHardware(AbstractHardware hardware) {
        this.hardware = hardware;
    }

    public Vector<Device> getDevices() {
        return devices;
    }

    public void setDevices(Vector<Device> devices) {
        this.devices = devices;
    }

    public Application getApplication() {
        return application;
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    public long getClockCount() {
        return clockCount;
    }

    public abstract int getClockFrequency();

    public abstract void setClockFrequency(int f);

    public int getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(int status) {
        this.runningStatus = status;
    }

    public void setProgram(Program p) {
        this.program = p;
    }

    public Program getProgram() {
        return program;
    }

    public void saveState(StateSet ss) {
        ss.set("computer.clockCount", clockCount);
        cpu.saveState(ss);
        hardware.saveState(ss);
    }

    public void loadState(StateSet ss) {
        this.clockCount = (Long)ss.get("computer.clockCount");
        cpu.loadState(ss);
        hardware.loadState(ss);
    }

    public void reset() {
        if (getRunningStatus() == STATUS_RUNNING || getRunningStatus() == STATUS_PAUSED) {
            setRunningStatus(STATUS_RUNNING);
            eventQueue.add(new ResetEvent(0));
        }
    }

    /**
     * コンピュータを一時停止する。
     * 一時停止イベントは同時には1つしか受け付けない。
     */
    public void pause() {
        if (getRunningStatus() == STATUS_RUNNING) {
            Iterator<EmulatorEvent> i = eventQueue.iterator();
            while (i.hasNext()) {
                if (i.next() instanceof PauseEvent) {
                    return ;
                }
            }
            eventQueue.add(new PauseEvent(0));
        }
    }

    public void resume() {
        if (getRunningStatus() == STATUS_PAUSED) {
            eventQueue.add(new ResumeEvent(0));
        }
    }

    public void powerOn() {
        this.intervalClocks = (long)(refreshRate * getClockFrequency());
        setRunningStatus(STATUS_RUNNING);
        reset();
        start();
    }

    public void powerOff() {
        if (getRunningStatus() == STATUS_RUNNING || getRunningStatus() == STATUS_PAUSED) {
            eventQueue.add(new PowerOffEvent(0));
        }
    }

    private long executeIfPossible(long clocks) {
        if (clocks <= 0 || getRunningStatus() != STATUS_RUNNING) {
            return 0;
        }
        long a = cpu.execute(clocks);
        for (Device d : devices) {
            d.execute();
        }
        return a;
    }

    private boolean hasConsumableEvent(long end_clock) {
        if (eventQueue.isEmpty()) {
            return false;
        }
        long c = eventQueue.first().getClock();
        if (getRunningStatus() == STATUS_PAUSED) {
            return c == 0;
        }
        else {
            return c <= end_clock;
        }
    }

    public void start() {
        double emulator_time = 0;
        double real_time = 0;
        long clock_adjustment = 0;

        baseTime = System.nanoTime();
        while (getRunningStatus() != STATUS_STOPPED) {
            long end_clock = clockCount + intervalClocks - clock_adjustment;

            long start = System.nanoTime();

            while (hasConsumableEvent(end_clock)) {
                EmulatorEvent event = eventQueue.first();
                eventQueue.remove(event);
                executeIfPossible(event.getClock() - clockCount);
                event.dispatch(this);
            }
            clock_adjustment = executeIfPossible(end_clock - clockCount);
            hardware.getDisplay().refresh();

            long end = System.nanoTime();
            long wait = (long)(refreshRate * 1E9) - (end - start);
            wait += (long)(emulator_time - real_time);
            if (wait > 0) {
                try {
                    Thread.sleep(wait / 1000000, (int)(wait % 1000000));
                }
                catch (InterruptedException e) {
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            real_time += System.nanoTime() - start;
            emulator_time += refreshRate * 1E9;
        }
        return ;
    }

    private void gamepadStart() {
        Gamepad g = getHardware().getGamepad();
        ComponentStatus[] amap = Gamepad.getPropertyOfGamepadAxisMapping();
        ComponentStatus[] bmap = Gamepad.getPropertyOfGamepadButtonMapping();
        g.setAxisType(Gamepad.getPropertyOfGamepadAxisUsed());
        g.setAxisKeyProcessor(new AxisKeyboardKeyProcessor(g, amap, getHardware().getKeyboard()));
        g.setButtonKeyProcessor(new ButtonKeyboardKeyProcessor(g, bmap, getHardware().getKeyboard()));
        Controller c = g.searchForController();
        if (c != null) {
            g.setController(c);
        }
        g.start();
    }

    public void run() {
        try {
            timeManager = new TimeManager();
            timeManager.start();
            gamepadStart();
            if (!getPropertyOfAutomaticPowerOn()) {
                synchronized(application.getComputer()) {
                    try {
                        application.getComputer().wait();
                    }
                    catch (InterruptedException e) {
                        return ;
                    }
                }
            }
            while (true) {
                application.getComputer().powerOn();
                application.getComputer().getHardware().getDisplay().repaint();
                synchronized(application.getComputer()) {
                    try {
                        application.getComputer().wait();
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }
        catch (Throwable e) {
            Application.getLogger().log(Level.SEVERE, "実行時エラー", e);
            e.printStackTrace();
            return ;
        }
    }
}
