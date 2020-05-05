/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import java.util.TreeSet;
import java.util.Comparator;

@SuppressWarnings("serial")
public class EventQueue extends TreeSet<EmulatorEvent> {
    public EventQueue() {
        super(new Comparator<EmulatorEvent>() {
            public int compare(EmulatorEvent e1, EmulatorEvent e2) {
                long c1 = e1.getClock();
                long c2 = e2.getClock();
                if (c1 < c2) {
                    return -1;
                }
                else if (c1 > c2){
                    return 1;
                }
                else {
                    return 0;
                }
            }
        });
    }
}
