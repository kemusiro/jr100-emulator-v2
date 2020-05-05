/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

public class PowerOffEvent extends EmulatorEvent {
    public PowerOffEvent(long clock) {
        super(clock);
    }

    @Override
    public void dispatch(Computer computer) {
        computer.setRunningStatus(Computer.STATUS_STOPPED);
    }
}
