/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device.gamepad;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import jp.asamomiji.emulator.Application;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.Device;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class Gamepad extends Thread implements Device {
    public final static String PROPERTY_GAMEPAD_USE = "jr100.input.gamepad.use";
    public final static String PROPERTY_GAMEPAD_NAME = "jr100.input.gamepad.name";
    public final static String PROPERTY_GAMEPAD_MAPPING_MODE = "jr100.input.gamepad.mapping_mode";
    public final static String PROPERTY_GAMEPAD_AXIS_USED = "jr100.input.gamepad.axis_used";
    /**
     * 軸ボタンそれぞれに対してマップするキーコードを列挙する。
     * 列挙する順番は、左上、上、右上、左、右、左下、下、右下、の順とする。
     * キーコードはKeyEventで定義される低数値とする。
     * ボタンが未使用であることを示す場合は-1を記述する。
     */
    public final static String PROPERTY_GAMEPAD_AXIS_MAPPING = "jr100.input.gamepad.axis_mapping";
    public final static String PROPERTY_GAMEPAD_BUTTON_MAPPING = "jr100.input.gamepad.button_mapping";

    public final static int GAMEPAD_MAX_NUMBER_OF_BUTTONS = 32;

    public final static int MODE_UNDEFINED = -1;
    public final static int MODE_KEYBOARD = 0;
    public final static int MODE_NATIVE = 1;

    public final static int AXIS_UNDEFINED = -1;
    public final static int AXIS_XY = 0;
    public final static int AXIS_RXRY = 1;
    public final static int AXIS_ZRZ = 2;
    public final static int AXIS_POV = 3;

    public final static int GAMEPAD_NUMBER_OF_DIRECTIONS = 8;
    public final static int DIR_UP_LEFT = 0;
    public final static int DIR_UP = 1;
    public final static int DIR_UP_RIGHT = 2;
    public final static int DIR_LEFT = 3;
    public final static int DIR_RIGHT = 4;
    public final static int DIR_DOWN_LEFT = 5;
    public final static int DIR_DOWN = 6;
    public final static int DIR_DOWN_RIGHT = 7;
    public final static int DIR_NEUTRAL = 8;
    public final static int DIR_ERROR = 9;

    public final static int BUTTON_PRESSED = 100;
    public final static int BUTTON_RELEASED = 101;

    public final static int GAMEPAD_NUMBER_OF_ACTIONS = 10;
    public final static int ACT_LEFT = 0;
    public final static int ACT_X_NEUTRAL = 1;
    public final static int ACT_RIGHT = 2;
    public final static int ACT_UP = 3;
    public final static int ACT_Y_NEUTRAL = 4;
    public final static int ACT_DOWN = 5;
    public final static int ACT_UP_LEFT = 6;
    public final static int ACT_UP_RIGHT = 7;
    public final static int ACT_DOWN_LEFT = 8;
    public final static int ACT_DOWN_RIGHT = 9;
    public final static int ACT_ORIGIN = 10;
    public final static int ACT_ERROR = 11;

    private final static int STATUS_STOPPED = 0;
    private final static int STATUS_SUSPENDED = 1;
    private final static int STATUS_RUNNING = 2;

    private final static float THRESHOLD = 0.5f;

    @SuppressWarnings("unused")
    private Computer computer;
    private Controller controller = null;
    private volatile boolean suspendRequested = false;
    private int status = STATUS_STOPPED;
    private int direction = DIR_NEUTRAL;
    private int selectedAxisType;

    private KeyProcessor axisKeyProcessor = null;
    private KeyProcessor buttonKeyProcessor = null;

    // $CC02 ジョイスティック入力レジスタ bit7,6:不定, bit5:0, bit4Switch, bit3:Down, bit2:Up, bit1:Left, bit0:Right
    // private boolean nativeGamepadMode = false;

    private int currentState = Gamepad.DIR_NEUTRAL;

    // 短縮表現用
    private final static int S_UL = DIR_UP_LEFT;
    private final static int S_UP = DIR_UP;
    private final static int S_UR = DIR_UP_RIGHT;
    private final static int S_LE = DIR_LEFT;
    private final static int S_RI = DIR_RIGHT;
    private final static int S_DL = DIR_DOWN_LEFT;
    private final static int S_DO = DIR_DOWN;
    private final static int S_DR = DIR_DOWN_RIGHT;
    private final static int S_NE = DIR_NEUTRAL;
    private final static int S_ER = DIR_ERROR;

    /*
     * 行: 現在の状態
     * 列: 発生したアクション
     *
     * stateTransitionTable[S][A]: 現在の状態がSで発生したアクションがAのときに次に遷移する状態を意味する。
     */
    private static int[][] stateTransitionTable;

    private static int[][] stateTransitionTable84 = {
            { S_ER, S_UP, S_ER, S_ER, S_LE, S_ER, S_ER, S_ER, S_ER, S_ER, S_ER }, // S_UL
            { S_UL, S_ER, S_UR, S_ER, S_NE, S_ER, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_UP
            { S_ER, S_UP, S_ER, S_ER, S_RI, S_ER, S_ER, S_ER, S_ER, S_ER, S_ER }, // S_UR
            { S_ER, S_NE, S_ER, S_UL, S_ER, S_DL, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_LE
            { S_ER, S_NE, S_ER, S_UR, S_ER, S_DR, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_RI
            { S_ER, S_DO, S_ER, S_ER, S_LE, S_ER, S_ER, S_ER, S_ER, S_ER, S_ER }, // S_DL
            { S_DL, S_ER, S_DR, S_ER, S_NE, S_ER, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_DO
            { S_ER, S_DO, S_ER, S_ER, S_RI, S_ER, S_ER, S_ER, S_ER, S_ER, S_ER }, // S_DR
            { S_LE, S_ER, S_RI, S_UP, S_ER, S_DO, S_ER, S_ER, S_ER, S_ER, S_NE }  // S_NE
    };

    private static int[][] stateTransitionTableForPov = {
            { S_LE, S_UP, S_ER, S_UP, S_LE, S_ER, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_UL
            { S_UL, S_ER, S_UR, S_ER, S_NE, S_ER, S_UL, S_UR, S_ER, S_ER, S_NE }, // S_UP
            { S_ER, S_UP, S_RI, S_UP, S_RI, S_ER, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_UR
            { S_ER, S_NE, S_ER, S_UL, S_ER, S_DL, S_UL, S_ER, S_DL, S_ER, S_NE }, // S_LE
            { S_ER, S_NE, S_ER, S_UR, S_ER, S_DR, S_ER, S_UR, S_ER, S_DR, S_NE }, // S_RI
            { S_LE, S_DO, S_ER, S_ER, S_LE, S_DO, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_DL
            { S_DL, S_ER, S_DR, S_ER, S_NE, S_ER, S_ER, S_ER, S_DL, S_DR, S_NE }, // S_DO
            { S_ER, S_DO, S_RI, S_ER, S_RI, S_DO, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_DR
            { S_LE, S_ER, S_RI, S_UP, S_ER, S_DO, S_ER, S_ER, S_ER, S_ER, S_NE }, // S_NE
    };

    private HashMap<Identifier, Integer> buttonNumber = new HashMap<Identifier, Integer>() {
        {
            put(Component.Identifier.Button._0, 0);
            put(Component.Identifier.Button._1, 1);
            put(Component.Identifier.Button._2, 2);
            put(Component.Identifier.Button._3, 3);
            put(Component.Identifier.Button._4, 4);
            put(Component.Identifier.Button._5, 5);
            put(Component.Identifier.Button._6, 6);
            put(Component.Identifier.Button._7, 7);
            put(Component.Identifier.Button._8, 8);
            put(Component.Identifier.Button._9, 9);
            put(Component.Identifier.Button._10, 10);
            put(Component.Identifier.Button._11, 11);
            put(Component.Identifier.Button._12, 12);
            put(Component.Identifier.Button._13, 13);
            put(Component.Identifier.Button._14, 14);
            put(Component.Identifier.Button._15, 15);
            put(Component.Identifier.Button._16, 16);
            put(Component.Identifier.Button._17, 17);
            put(Component.Identifier.Button._18, 18);
            put(Component.Identifier.Button._19, 19);
            put(Component.Identifier.Button._20, 20);
            put(Component.Identifier.Button._21, 21);
            put(Component.Identifier.Button._22, 22);
            put(Component.Identifier.Button._23, 23);
            put(Component.Identifier.Button._24, 24);
            put(Component.Identifier.Button._25, 25);
            put(Component.Identifier.Button._26, 26);
            put(Component.Identifier.Button._27, 27);
            put(Component.Identifier.Button._28, 28);
            put(Component.Identifier.Button._29, 29);
            put(Component.Identifier.Button._30, 30);
            put(Component.Identifier.Button._31, 31);
        }
    };

    public Gamepad(Computer computer) {
        super("gamepad manager");
        this.computer = computer;
    }

    public void setAxisKeyProcessor(KeyProcessor akp) {
        axisKeyProcessor = akp;
    }

    public KeyProcessor getAxisKeyProcessor() {
        return axisKeyProcessor;
    }

    public void setButtonKeyProcessor(KeyProcessor kp) {
        buttonKeyProcessor = kp;
    }

    public KeyProcessor getButtonKeyProcessor() {
        return buttonKeyProcessor;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public void dumpTable() {
        for (int i = 0; i < 9; i++) {
            System.out.print(i + " : ");
            for(int j = 0; j < 11; j++) {
                System.out.print(stateTransitionTable[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void setAxisType(int type) {
        selectedAxisType = type;
        switch (type) {
        case AXIS_XY:
        case AXIS_RXRY:
        case AXIS_ZRZ:
            stateTransitionTable = stateTransitionTable84;
            break;
        case AXIS_POV:
            stateTransitionTable = stateTransitionTableForPov;
            break;
        default:
            stateTransitionTable = stateTransitionTable84;
            break;
        }
        // dumpTable();
    }

    public int getAxisType() {
        return selectedAxisType;
    }

    public int getDirection() {
        return direction;
    }

    public static void setPropertyOfGamepadUse(boolean value) {
        Application.getProperties().setProperty(PROPERTY_GAMEPAD_USE, Boolean.toString(value));
    }

    public static boolean getPropertyOfGamepadUse() {
        String m = Application.getProperties().getProperty(PROPERTY_GAMEPAD_USE);
        if (m == null) {
            return false;
        }
        else {
            return Boolean.parseBoolean(m);
        }
    }

    public static void setPropertyOfGamepadName(String value) {
        Application.getProperties().setProperty(PROPERTY_GAMEPAD_NAME, value);
    }

    public static String getPropertyOfGamepadName() {
        String m = Application.getProperties().getProperty(PROPERTY_GAMEPAD_NAME);
        if (m == null) {
            return "";
        }
        else {
            return m;
        }
    }

    public static void setPropertyOfGamepadMappingMode(int mode) {
        String mode_string;
        switch (mode) {
        case MODE_KEYBOARD:
            mode_string = "keyboard";
            break;
        case MODE_NATIVE:
            mode_string = "native";
            break;
        default:
            mode_string = null;
            break;
        }
        if (mode_string != null) {
            Application.getProperties().setProperty(PROPERTY_GAMEPAD_MAPPING_MODE, mode_string);
        }
    }

    public static int getPropertyOfGamepadMappingMode() {
        String m = Application.getProperties().getProperty(PROPERTY_GAMEPAD_MAPPING_MODE);
        if (m == null) {
            return MODE_KEYBOARD;
        }
        else if (m.equals("keyboard")) {
            return MODE_KEYBOARD;
        }
        else if (m.equals("native")) {
            return MODE_NATIVE;
        }
        else {
            return MODE_UNDEFINED;
        }
    }

    public static void setPropertyOfGamepadAxisUsed(int mode) {
        String mode_string;
        switch (mode) {
        case AXIS_XY:
            mode_string = "xy";
            break;
        case AXIS_RXRY:
            mode_string = "rxry";
            break;
        case AXIS_ZRZ:
            mode_string = "zrz";
            break;
        case AXIS_POV:
            mode_string = "pov";
            break;
        default:
            mode_string = null;
            break;
        }
        if (mode_string != null) {
            Application.getProperties().setProperty(PROPERTY_GAMEPAD_AXIS_USED, mode_string);
        }
    }

    public static int getPropertyOfGamepadAxisUsed() {
        String m = Application.getProperties().getProperty(PROPERTY_GAMEPAD_AXIS_USED);
        if (m == null) {
            return AXIS_XY;
        }
        else if (m.equals("xy")) {
            return AXIS_XY;
        }
        else if (m.equals("rxry")) {
            return AXIS_RXRY;
        }
        else if (m.equals("zrz")) {
            return AXIS_ZRZ;
        }
        else if (m.equals("pov")) {
            return AXIS_POV;
        }
        else {
            return AXIS_UNDEFINED;
        }
    }

    public static void setPropertyOfGamepadAxisMapping(ComponentStatus[] mapping) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mapping.length; i++) {
            sb.append(Integer.toString(mapping[i].getKeyCode()));
            sb.append(" ");
        }
        Application.getProperties().setProperty(PROPERTY_GAMEPAD_AXIS_MAPPING, sb.toString());
    }

    public static ComponentStatus[] getPropertyOfGamepadAxisMapping() {
        String m = Application.getProperties().getProperty(PROPERTY_GAMEPAD_AXIS_MAPPING);
        ComponentStatus[] mapping = new ComponentStatus[GAMEPAD_NUMBER_OF_DIRECTIONS];
        if (m == null) {
            for (int i = 0; i < mapping.length; i++) {
                mapping[i] = new ComponentStatus(i, KeyEvent.VK_UNDEFINED);
            }
        }
        else {
            String[] splitted = m.split("\\s+", 0);
            for (int i = 0; i < Math.min(mapping.length, splitted.length); i++) {
                mapping[i] = new ComponentStatus(i, Integer.parseInt(splitted[i]));
            }
            for (int i = Math.min(mapping.length, splitted.length); i < GAMEPAD_NUMBER_OF_DIRECTIONS; i++) {
                mapping[i] = new ComponentStatus(i, -1);
            }
        }
        return mapping;
    }

    public static void setPropertyOfGamepadButtonMapping(ComponentStatus[] mapping) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mapping.length; i++) {
            sb.append(Integer.toString(mapping[i].getKeyCode()));
            sb.append(" ");
        }
        Application.getProperties().setProperty(PROPERTY_GAMEPAD_BUTTON_MAPPING, sb.toString());
    }

    public static ComponentStatus[] getPropertyOfGamepadButtonMapping() {
        String m = Application.getProperties().getProperty(PROPERTY_GAMEPAD_BUTTON_MAPPING);
        ComponentStatus[] mapping = new ComponentStatus[GAMEPAD_MAX_NUMBER_OF_BUTTONS];
        if (m == null) {
            for (int i = 0; i < mapping.length; i++) {
                mapping[i] = new ComponentStatus(i, KeyEvent.VK_UNDEFINED);
            }
        }
        else {
            String[] splitted = m.split("\\s+", 0);
            for (int i = 0; i < Math.min(mapping.length, splitted.length); i++) {
                mapping[i] = new ComponentStatus(i, Integer.parseInt(splitted[i]));
            }
            for (int i =Math.min(mapping.length, splitted.length); i < GAMEPAD_NUMBER_OF_DIRECTIONS; i++) {
                mapping[i] = new ComponentStatus(i, -1);
            }
        }
        return mapping;
    }

    /**
     * ゲームパッドの状態に応じてイベントを発生させる。
     *
     * @return suspend要求を受信して停止する場合はtrue、コントローラのポーリングが失敗した場合はfalseを返す。
     */
    private boolean loop() {
        // System.out.println("gpm loop start");
        while (!suspendRequested) {
            if (!controller.poll()) {
                // System.out.println("gpm polling failed and break");
                return false;
            }
            EventQueue queue = controller.getEventQueue();
            Event event = new Event();
            while (queue.getNextEvent(event)) {
                Component c = event.getComponent();
                Identifier id = c.getIdentifier();
                float v = c.getPollData();
                // System.out.println("selected axis = " + selectedAxisType + "   " + id.toString() + " " + v);
                if ((selectedAxisType == AXIS_XY && c.getIdentifier() == Component.Identifier.Axis.X) ||
                    (selectedAxisType == AXIS_RXRY && c.getIdentifier() == Component.Identifier.Axis.RX) ||
                    (selectedAxisType == AXIS_ZRZ && c.getIdentifier() == Component.Identifier.Axis.Z)) {
                    if (v < -THRESHOLD) {
                        gamepadDirectionChanged(currentState, ACT_LEFT);
                    }
                    else if (v > THRESHOLD) {
                        gamepadDirectionChanged(currentState, ACT_RIGHT);
                    }
                    else {
                        gamepadDirectionChanged(currentState, ACT_X_NEUTRAL);
                    }
                }
                else if ((selectedAxisType == AXIS_XY && c.getIdentifier() == Component.Identifier.Axis.Y) ||
                        (selectedAxisType == AXIS_RXRY && c.getIdentifier() == Component.Identifier.Axis.RY) ||
                        (selectedAxisType == AXIS_ZRZ && c.getIdentifier() == Component.Identifier.Axis.RZ)) {
                    if (v < -THRESHOLD) {
                        gamepadDirectionChanged(currentState, ACT_UP);
                    }
                    else if (v > THRESHOLD) {
                        gamepadDirectionChanged(currentState, ACT_DOWN);
                    }
                    else {
                        gamepadDirectionChanged(currentState, ACT_Y_NEUTRAL);
                    }
                }
                else if (selectedAxisType == AXIS_POV) {
                    if (v < 0.0625) {
                        gamepadDirectionChanged(currentState, ACT_ORIGIN);
                    }
                    else if (v < 0.1875) {
                        gamepadDirectionChanged(currentState, ACT_UP_LEFT);
                    }
                    else if (v < 0.3125) {
                        gamepadDirectionChanged(currentState, ACT_UP);
                    }
                    else if (v < 0.4375) {
                        gamepadDirectionChanged(currentState, ACT_UP_RIGHT);
                    }
                    else if (v < 0.5625) {
                        gamepadDirectionChanged(currentState, ACT_RIGHT);
                    }
                    else if (v < 0.6875) {
                        gamepadDirectionChanged(currentState, ACT_DOWN_RIGHT);
                    }
                    else if (v < 0.8125) {
                        gamepadDirectionChanged(currentState, ACT_DOWN);
                    }
                    else if (v < 0.9375) {
                        gamepadDirectionChanged(currentState, ACT_DOWN_LEFT);
                    }
                    else {
                        gamepadDirectionChanged(currentState, ACT_LEFT);
                    }
                }
                else if (id instanceof Component.Identifier.Button) {
                    if (v > THRESHOLD) {
                        gamepadButtonPressed(buttonNumber.get(id));
                    }
                    else {
                        gamepadButtonReleased(buttonNumber.get(id));
                    }
                }
            }
            try {
                Thread.sleep(25);
            }
            catch (InterruptedException e) {
                break;
            }
        }
        // System.out.println("loop end");
        suspendRequested = false;
        return true;
    }

    public void requestSuspend() {
        suspendRequested = true;
    }

    public boolean waitForSuspended(long period, int count) {
        if (status == STATUS_RUNNING) {
            // System.out.println("status is RUNNING");
            while (suspendRequested && count >= 0) {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                }
                count--;
            }
            return !suspendRequested;
        }
        else if (status == STATUS_STOPPED) {
            // System.out.println("status is STOPPED");
            return false;
        }
        else {
            // System.out.println("status is SUSPENDED");
            return true;
        }
    }

    public boolean restart(Controller controller) {
        // System.out.println("gpm requested restart");
        if (status == STATUS_STOPPED) {
            return false;
        }
        // System.out.println("request gpm to suspend");
        if (status == STATUS_RUNNING) {
            requestSuspend();
            if (!waitForSuspended(100, 100)) {
                // タイムアウト
                // System.out.println("request timeout...");
                return false;
            }
        }
        // System.out.println("gpm has been suspended");
        setController(controller);
        // axisMapping = getPropertyOfGamepadAxisMapping();
        synchronized(this) {
            // System.out.println("notify to gpm");
            notify();
        }
        return true;
    }

    public ArrayList<Controller> scanControllers() {
        Controller[] candidates = ControllerEnvironment.getDefaultEnvironment().getControllers();
        ArrayList<Controller> found = new ArrayList<>();
        for(int i = 0; i < candidates.length; i++){
            Controller controller = candidates[i];

            if (controller.getType() == Controller.Type.STICK
                    || controller.getType() == Controller.Type.GAMEPAD) {
                found.add(controller);
            }
        }
        return found;
    }

    public Controller searchForController() {
        ArrayList<Controller> controllers = scanControllers();
        controller = null;
        for (Controller c : controllers) {
            if (c.getName().equals(getPropertyOfGamepadName())) {
                controller = c;
            }
        }
        return controller;
    }

    @Override
    public void run() {
        try {
            synchronized(this) {
                while (true) {
                    while (!getPropertyOfGamepadUse() || controller == null) {
                        status = STATUS_SUSPENDED;
                        try {
                            wait();
                        }
                        catch (InterruptedException e) {
                        }
                    }
                    status = STATUS_RUNNING;
                    if (loop()) {
                        status = STATUS_SUSPENDED;
                        try {
                            wait();
                        }
                        catch (InterruptedException e) {
                        }
                    }
                    else {
                        status = STATUS_SUSPENDED;
                    }
                }
            }
        }
        catch (Throwable e) {
            Application.getLogger().log(Level.SEVERE, "実行時エラー", e);
            return ;
        }
    }

    static int s;
    static int a;

    public void gamepadDirectionChanged(int state, int action) {
        if (axisKeyProcessor != null) {
            axisKeyProcessor.execute(state, action);
            currentState = stateTransitionTable[state][action];
            if (currentState == DIR_ERROR) {
                // アナログスティックの場合、閾値を超えないイベントが発生する場合がある。
                // この場合は状態遷移させない。
                currentState = state;
            }
            /*
            {
                if (state != s || action != a) {
                    System.out.println("gamepadDirectionChanged(" + state + ", " + action + ") -> " + currentState);
                    s = state;
                    a = action;

                }
            }
            */
        }
    }

    public void gamepadButtonPressed(int number) {
        if (buttonKeyProcessor != null) {
            buttonKeyProcessor.execute(BUTTON_RELEASED, number);
        }
    }

    public void gamepadButtonReleased(int number) {
        if (buttonKeyProcessor != null) {
            buttonKeyProcessor.execute(BUTTON_PRESSED, number);
        }
    }


    @Override
    public void reset() {
    }

    @Override
    public void execute() {
    }
}
