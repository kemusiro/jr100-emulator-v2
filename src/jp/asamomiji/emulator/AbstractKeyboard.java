/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import net.java.games.input.Controller;

public abstract class AbstractKeyboard implements Device, StateSavable {
    protected Computer computer;
    protected Controller controller = null;

    public AbstractKeyboard(Computer computer) {
        this.computer = computer;
    }

    public void connectController(Controller controller) {
        this.controller = controller;
    }

    public void disconnectController() {
        this.controller = null;
    }

    public void keyPressed(int keycode) {
    }

    public void keyReleased(int keycode) {
    }

    public void execute() {
    }

    public void reset() {
    }

    public void loadState(StateSet ss) {
    }

    public void saveState(StateSet ss) {
    }
}
