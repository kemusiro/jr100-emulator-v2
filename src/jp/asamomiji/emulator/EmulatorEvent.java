/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

public abstract class EmulatorEvent {
    protected long clock;

    public EmulatorEvent(long clock) {
        this.clock = clock;
    }

    public long getClock() {
        return clock;
    }

    public void decreaseClock(long interval) {
        clock -= interval;
        if (clock < 0) {
            clock = 0;
        }
    }

    public abstract void dispatch(Computer computer);
}
