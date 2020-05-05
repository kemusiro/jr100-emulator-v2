/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import jp.asamomiji.emulator.device.gamepad.Gamepad;

public class AbstractHardware implements StateSavable {
    private MemorySystem memory = null;
    private AbstractDisplay display = null;
    private AbstractSoundProcessor soundProcessor = null;
    private AbstractKeyboard keyboard = null;
    private Gamepad gamepad = null;
    private Application application = null;

    public AbstractHardware() {
        memory = new MemorySystem();
    }

    public MemorySystem getMemory() {
        return memory;
    }

    public void setDisplay(AbstractDisplay display) {
        this.display = display;
    }

    public AbstractDisplay getDisplay() {
        return display;
    }

    public AbstractSoundProcessor getSoundProcessor() {
        return soundProcessor;
    }

    public void setSoundProcessor(AbstractSoundProcessor sound) {
        this.soundProcessor = sound;
    }

    public AbstractKeyboard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(AbstractKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    public Gamepad getGamepad() {
        return gamepad;
    }

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void saveState(StateSet ss) {
        for (Addressable m : memory.getMemories()) {
            if (m instanceof StateSavable) {
                ((StateSavable)m).saveState(ss);
            }
        }
        display.saveState(ss);
        soundProcessor.saveState(ss);
        keyboard.saveState(ss);
    }

    public void loadState(StateSet ss) {
        for (Addressable m : memory.getMemories()) {
            if (m instanceof StateSavable) {
                ((StateSavable)m).loadState(ss);
            }
        }
        display.loadState(ss);
        soundProcessor.loadState(ss);
        keyboard.loadState(ss);
    }
}
