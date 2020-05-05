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

public class ComponentStatus {
    public final static int STATUS_NOT_IMPLEMENTED = 0;
    public final static int STATUS_UNUSED = 1;
    public final static int STATUS_UNASSIGNED = 2;
    public final static int STATUS_ASSIGNED = 3;

    private int index;
    private int status;
    private int keycode;
    /**
     * コントローラコンポーネントの状態オブジェクトを構築する。
     *
     * @param index このコンポーネントの識別番号
     * @param keycode コンポーネントのイベント発生時に生成するキーコード
     */
    public ComponentStatus(int index, int keycode) {
        this.index = index;
        this.keycode = keycode;
        if (keycode == -1) {
            this.status = STATUS_UNUSED;
        }
        else if (keycode == KeyEvent.VK_UNDEFINED) {
            this.status = STATUS_UNASSIGNED;
        }
        else {
            this.status = STATUS_ASSIGNED;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getKeyCode() {
        return keycode;
    }

    public void setKeyCode(int keycode) {
        this.keycode = keycode;
        if (keycode == -1) {
            this.status = STATUS_UNUSED;
        }
        else if (keycode == KeyEvent.VK_UNDEFINED) {
            this.status = STATUS_UNASSIGNED;
        }
        else {
            this.status = STATUS_ASSIGNED;
        }
    }

    public String getKeyText() {
        if (status == STATUS_UNUSED) {
            return "未使用";
        }
        else if (status == STATUS_UNASSIGNED) {
            return "未割当";
        }
        else if (status == STATUS_NOT_IMPLEMENTED) {
            return "未実装";
        }
        else {
            return KeyEvent.getKeyText(keycode);
        }
    }
}
