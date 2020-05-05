/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device;

import jp.asamomiji.emulator.AbstractSoundProcessor;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.StateSet;

/*
 * BEEP音を発生するサウンドプロセッサを定義する。
 */
public class Beep extends AbstractSoundProcessor {
    private final static int MAX_RANK = 30;
    private final static int TABLE_LENGTH = 8192;
    private double time;
    private volatile double delta;
    private static volatile float[][] table2 = new float[MAX_RANK + 1][TABLE_LENGTH];
    private volatile float[] t = table2[0];

    public Beep(Computer computer, double sampling_rate) {
        super(computer, sampling_rate);

        for (int rank = 1; rank <= MAX_RANK; rank++) {
            for (int i = 0; i < TABLE_LENGTH; i++) {
                table2[rank][i] = value(rank, (double)i / TABLE_LENGTH);
            }
        }
        for (int i = 0; i < TABLE_LENGTH; i++) {
            table2[0][i] = 0.0f;
        }
        time = 0;
    }

    private float value(int n, double phase) {
        double x = 2 * Math.PI * phase;
        double temp = 0;
        for (int k = 1; k <= n; k++) {
            temp += Math.sin((2 * k - 1) * x) / (2 * k - 1);
        }
        return (float)(4 * temp / Math.PI);
    }

    public void changeFrequency(double frequency) {
        int n = (int) Math.floor((samplingRate / (2 * frequency) + 1) / 2);
        if (n >= MAX_RANK) {
            n = MAX_RANK;
        }
        synchronized(this) {
            Beep.this.frequency = frequency;
            t = table2[n];
            delta = TABLE_LENGTH * frequency / samplingRate;
        }
    }

    @Override
    public synchronized float tick() {
        while ((int)time >= TABLE_LENGTH) {
            time -= TABLE_LENGTH;
        }
        int index = (int)time;
        time += delta;
        return t[index];
    }

    @Override
    public void saveState(StateSet ss) {
        ss.set("AbstractSoundProcessor.frequency", frequency);
        ss.set("AbstractSoundProcessor.beep.time", time);
        ss.set("AbstractSoundProcessor.beep.delta", delta);
        ss.set("AbstractSoundProcessor.beep.current_table", t);
    }

    @Override
    public void loadState(StateSet ss) {
        frequency = (Double)ss.get("AbstractSoundProcessor.frequency");
        time = (Double)ss.get("AbstractSoundProcessor.beep.time");
        delta = (Double)ss.get("AbstractSoundProcessor.beep.delta");
        t = (float[])ss.get("AbstractSoundProcessor.beep.current_table");
    }
}
