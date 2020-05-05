/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import java.awt.event.KeyEvent;

import jp.asamomiji.emulator.AbstractKeyboard;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.StateSet;

/*
 * JR-100のキーボード処理を行う。
 */
public final class JR100Keyboard extends AbstractKeyboard {
    private int keyMatrix[] = new int[9];

    public JR100Keyboard(Computer computer) {
        super(computer);
    }

    public int[] getKeyMatrix() {
        return keyMatrix;
    }

    @Override
    public void keyPressed(int keycode) {
        super.keyPressed(keycode);
        switch (keycode) {
        case KeyEvent.VK_MINUS:
            keyMatrix[8] |= 0x10;
            break;
        case KeyEvent.VK_ENTER:
            keyMatrix[8] |= 0x08;
            break;
        case KeyEvent.VK_COLON:
            keyMatrix[8] |= 0x04;
            break;
        case KeyEvent.VK_SPACE:
            keyMatrix[8] |= 0x02;
            break;
        case KeyEvent.VK_PERIOD:
            keyMatrix[8] |= 0x01;
            break;
        case KeyEvent.VK_COMMA:
            keyMatrix[7] |= 0x10;
            break;
        case KeyEvent.VK_M:
            keyMatrix[7] |= 0x08;
            break;
        case KeyEvent.VK_N:
            keyMatrix[7] |= 0x04;
            break;
        case KeyEvent.VK_B:
            keyMatrix[7] |= 0x02;
            break;
        case KeyEvent.VK_V:
            keyMatrix[7] |= 0x01;
            break;
        case KeyEvent.VK_SEMICOLON:
            keyMatrix[6] |= 0x10;
            break;
        case KeyEvent.VK_L:
            keyMatrix[6] |= 0x08;
            break;
        case KeyEvent.VK_K:
            keyMatrix[6] |= 0x04;
            break;
        case KeyEvent.VK_J:
            keyMatrix[6] |= 0x02;
            break;
        case KeyEvent.VK_H:
            keyMatrix[6] |= 0x01;
            break;
        case KeyEvent.VK_P:
            keyMatrix[5] |= 0x10;
            break;
        case KeyEvent.VK_O:
            keyMatrix[5] |= 0x08;
            break;
        case KeyEvent.VK_I:
            keyMatrix[5] |= 0x04;
            break;
        case KeyEvent.VK_U:
            keyMatrix[5] |= 0x02;
            break;
        case KeyEvent.VK_Y:
            keyMatrix[5] |= 0x01;
            break;
        case KeyEvent.VK_0:
            keyMatrix[4] |= 0x10;
            break;
        case KeyEvent.VK_9:
            keyMatrix[4] |= 0x08;
            break;
        case KeyEvent.VK_8:
            keyMatrix[4] |= 0x04;
            break;
        case KeyEvent.VK_7:
            keyMatrix[4] |= 0x02;
            break;
        case KeyEvent.VK_6:
            keyMatrix[4] |= 0x01;
            break;
        case KeyEvent.VK_C:
            keyMatrix[0] |= 0x10;
            break;
        case KeyEvent.VK_X:
            keyMatrix[0] |= 0x08;
            break;
        case KeyEvent.VK_Z:
            keyMatrix[0] |= 0x04;
            break;
        case KeyEvent.VK_SHIFT:
            keyMatrix[0] |= 0x02;
            break;
        case KeyEvent.VK_CONTROL:
            keyMatrix[0] |= 0x01;
            break;
        case KeyEvent.VK_G:
            keyMatrix[1] |= 0x10;
            break;
        case KeyEvent.VK_F:
            keyMatrix[1] |= 0x08;
            break;
        case KeyEvent.VK_D:
            keyMatrix[1] |= 0x04;
            break;
        case KeyEvent.VK_S:
            keyMatrix[1] |= 0x02;
            break;
        case KeyEvent.VK_A:
            keyMatrix[1] |= 0x01;
            break;
        case KeyEvent.VK_T:
            keyMatrix[2] |= 0x10;
            break;
        case KeyEvent.VK_R:
            keyMatrix[2] |= 0x08;
            break;
        case KeyEvent.VK_E:
            keyMatrix[2] |= 0x04;
            break;
        case KeyEvent.VK_W:
            keyMatrix[2] |= 0x02;
            break;
        case KeyEvent.VK_Q:
            keyMatrix[2] |= 0x01;
            break;
        case KeyEvent.VK_5:
            keyMatrix[3] |= 0x10;
            break;
        case KeyEvent.VK_4:
            keyMatrix[3] |= 0x08;
            break;
        case KeyEvent.VK_3:
            keyMatrix[3] |= 0x04;
            break;
        case KeyEvent.VK_2:
            keyMatrix[3] |= 0x02;
            break;
        case KeyEvent.VK_1:
            keyMatrix[3] |= 0x01;
            break;
        default:
            // do nothing
            break;
        }
    }

    @Override
    public void keyReleased(int keycode) {
        super.keyReleased(keycode);
        switch (keycode) {
        case KeyEvent.VK_MINUS:
            keyMatrix[8] &= 0xef;
            break;
        case KeyEvent.VK_ENTER:
            keyMatrix[8] &= 0xf7;
            break;
        case KeyEvent.VK_COLON:
            keyMatrix[8] &= 0xfb;
            break;
        case KeyEvent.VK_SPACE:
            keyMatrix[8] &= 0xfd;
            break;
        case KeyEvent.VK_PERIOD:
            keyMatrix[8] &= 0xfe;
            break;
        case KeyEvent.VK_COMMA:
            keyMatrix[7] &= 0xef;
            break;
        case KeyEvent.VK_M:
            keyMatrix[7] &= 0xf7;
            break;
        case KeyEvent.VK_N:
            keyMatrix[7] &= 0xfb;
            break;
        case KeyEvent.VK_B:
            keyMatrix[7] &= 0xfd;
            break;
        case KeyEvent.VK_V:
            keyMatrix[7] &= 0xfe;
            break;
        case KeyEvent.VK_SEMICOLON:
            keyMatrix[6] &= 0xef;
            break;
        case KeyEvent.VK_L:
            keyMatrix[6] &= 0xf7;
            break;
        case KeyEvent.VK_K:
            keyMatrix[6] &= 0xfb;
            break;
        case KeyEvent.VK_J:
            keyMatrix[6] &= 0xfd;
            break;
        case KeyEvent.VK_H:
            keyMatrix[6] &= 0xfe;
            break;
        case KeyEvent.VK_P:
            keyMatrix[5] &= 0xef;
            break;
        case KeyEvent.VK_O:
            keyMatrix[5] &= 0xf7;
            break;
        case KeyEvent.VK_I:
            keyMatrix[5] &= 0xfb;
            break;
        case KeyEvent.VK_U:
            keyMatrix[5] &= 0xfd;
            break;
        case KeyEvent.VK_Y:
            keyMatrix[5] &= 0xfe;
            break;
        case KeyEvent.VK_0:
            keyMatrix[4] &= 0xef;
            break;
        case KeyEvent.VK_9:
            keyMatrix[4] &= 0xf7;
            break;
        case KeyEvent.VK_8:
            keyMatrix[4] &= 0xfb;
            break;
        case KeyEvent.VK_7:
            keyMatrix[4] &= 0xfd;
            break;
        case KeyEvent.VK_6:
            keyMatrix[4] &= 0xfe;
            break;
        case KeyEvent.VK_C:
            keyMatrix[0] &= 0xef;
            break;
        case KeyEvent.VK_X:
            keyMatrix[0] &= 0xf7;
            break;
        case KeyEvent.VK_Z:
            keyMatrix[0] &= 0xfb;
            break;
        case KeyEvent.VK_SHIFT:
            keyMatrix[0] &= 0xfd;
            break;
        case KeyEvent.VK_CONTROL:
            keyMatrix[0] &= 0xfe;
            break;
        case KeyEvent.VK_G:
            keyMatrix[1] &= 0xef;
            break;
        case KeyEvent.VK_F:
            keyMatrix[1] &= 0xf7;
            break;
        case KeyEvent.VK_D:
            keyMatrix[1] &= 0xfb;
            break;
        case KeyEvent.VK_S:
            keyMatrix[1] &= 0xfd;
            break;
        case KeyEvent.VK_A:
            keyMatrix[1] &= 0xfe;
            break;
        case KeyEvent.VK_T:
            keyMatrix[2] &= 0xef;
            break;
        case KeyEvent.VK_R:
            keyMatrix[2] &= 0xf7;
            break;
        case KeyEvent.VK_E:
            keyMatrix[2] &= 0xfb;
            break;
        case KeyEvent.VK_W:
            keyMatrix[2] &= 0xfd;
            break;
        case KeyEvent.VK_Q:
            keyMatrix[2] &= 0xfe;
            break;
        case KeyEvent.VK_5:
            keyMatrix[3] &= 0xef;
            break;
        case KeyEvent.VK_4:
            keyMatrix[3] &= 0xf7;
            break;
        case KeyEvent.VK_3:
            keyMatrix[3] &= 0xfb;
            break;
        case KeyEvent.VK_2:
            keyMatrix[3] &= 0xfd;
            break;
        case KeyEvent.VK_1:
            keyMatrix[3] &= 0xfe;
            break;
        default:
            // do nothing
            break;
        }
    }

    @Override
    public void saveState(StateSet ss) {
        super.saveState(ss);
        ss.set("JR100Keyboard.keyMatrix", keyMatrix);
    }

    @Override
    public void loadState(StateSet ss) {
        super.loadState(ss);
        keyMatrix = (int[])ss.get("JR100Keyboard.keyMatrix");
    }

    @Override
    public void execute() {
    }
}
