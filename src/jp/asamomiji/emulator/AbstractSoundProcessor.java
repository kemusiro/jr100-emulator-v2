/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import jp.asamomiji.emulator.TimeManager.EventCommand;

public abstract class AbstractSoundProcessor extends Thread implements Device, StateSavable {
    public final static String PROPERTY_SOUND_VOLUME = "sound.volume";
    public final static byte DEFAULT_VOLUME = 30;

    private final static int SAMPLING_BIT_LENGTH = 8;
    private final static double COEFF = 19.36708871; // 3 / (2 - log10(70))

    protected Computer computer;
    protected volatile double frequency;
    protected double samplingRate;
    protected int volume;

    private double amplitude;
    private SourceDataLine line;
    private byte[] buffer = null;
    private double status = 0;

    class ChangeFrequencyCommand implements EventCommand {
        private double frequency;

        public ChangeFrequencyCommand(double frequency) {
            this.frequency = frequency;
        }

        @Override
        public void execute() {
            changeFrequency(frequency);
        }

        @Override
        public String toString() {
            return String.format("change frequency to %d", (int)frequency);
        }
    }

    public AbstractSoundProcessor(Computer computer, double sampling_rate) {
        this.computer = computer;
        this.samplingRate = sampling_rate;
        this.buffer = new byte[(int)(sampling_rate)];
        setVolume(getPropertyOfVolume());
        AudioFormat format =
            new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                (float)sampling_rate,
                SAMPLING_BIT_LENGTH,
                1,
                1,
                (float)sampling_rate,
                true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try {
            line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(format, 1000);
        }
        catch (LineUnavailableException e) {
            throw new RuntimeException("fail to initialize sound I/O");
        }
        line.start();
    }

    public int getPropertyOfVolume() {
        String m = Application.getProperties().getProperty(PROPERTY_SOUND_VOLUME);
        if (m == null) {
            return DEFAULT_VOLUME;
        }
        else {
            int value;
            try {
                value = Integer.parseInt(m);
            }
            catch (NumberFormatException e) {
                value = DEFAULT_VOLUME;
            }
            return value;
        }
    }

    public void setPropertyOfVolume(int volume) {
        setVolume(volume);
        Application.getProperties().setProperty(PROPERTY_SOUND_VOLUME, Integer.toString(volume));
    }

    private void setVolume(int volume) {
        this.volume = volume;
        this.amplitude = calculateAmplitude(volume) * (Math.pow(2, SAMPLING_BIT_LENGTH - 1) - 1);
    }

    public void setLineOn() {
        status = 1;
    }

    public void setLineOff() {
        status = 0;
    }

    private double calculateAmplitude(int volume) {
        if (volume == 0) {
            return 0;
        }
        else {
            double dB = COEFF * (Math.log10(volume) - 2.0);
            double amplitude = Math.pow(10.0, Math.log10(2.0) / 3 * dB) * 0.8;
            return amplitude;
        }
    }

    public void setFrequency(long time, double frequency) {
        computer.getTimeManager().addEvent(time, new ChangeFrequencyCommand(frequency));
    }

    public abstract void changeFrequency(double frequency);

    public abstract float tick();

    @Override
    public void reset() {
        Thread.State state = this.getState();
        if (state == State.NEW || state == State.TERMINATED) {
            this.start();
        }
    }

    @Override
    public void execute() {
        // 別スレッドにてスケジュール実行されるため、このメソッドでの処理は不要。
    }

    @Override
    public void run() {
        while (true) {
            int len = 20;
            for (int t = 0; t < len; t++) {
                buffer[t] = (byte)(status * amplitude * tick());
            }
            line.write(buffer, 0, len);
        }
    }
}
