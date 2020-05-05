/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeManager {
    private ArrayList<Event> eventList;

    class Event implements Comparable<Event> {
        protected long timeOffset;
        protected EventCommand command;
        public Event(long timeOffset, EventCommand command) {
            this.timeOffset = timeOffset;
            this.command = command;
        }
        @Override
        public int compareTo(Event e) {
            return Long.compare(this.timeOffset, e.timeOffset);
        }
        @Override
        public String toString() {
            return String.format("timeOffset = %d %s", timeOffset, command.toString());
        }
    }

    interface EventCommand {
        public void execute();
    }

    private static <T extends Comparable<? super T>> void insert(List<T> list, T element) {
        int index = Collections.binarySearch(list, element);
        if (index < 0) {
            index = -index - 1;
        }
        if (list.size() <= index) {
            list.add(element);
        }
        else {
            list.add(index, element);
        }
    }

    public void addEvent(Event event) {
        synchronized(eventList) {
            insert(eventList, event);
        }
    }

    public void addEvent(long timeOffset, EventCommand command) {
        addEvent(new Event(timeOffset, command));
    }

    public TimeManager() {
        eventList = new ArrayList<Event>();
    }

    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeManager.this.dispatcher();
                        }
                        catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                },
                0, 100, TimeUnit.MICROSECONDS);
    }

    private void dispatcher() {
        long time_offset = System.nanoTime();
        synchronized(eventList) {
            Iterator<Event> itr = eventList.iterator();
            while (itr.hasNext()) {
                Event event = itr.next();
                if (event.timeOffset < time_offset) {
                    event.command.execute();
                    itr.remove();
                }
            }
        }
    }
}
