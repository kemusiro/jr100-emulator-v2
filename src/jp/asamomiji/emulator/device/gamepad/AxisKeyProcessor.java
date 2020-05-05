/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.device.gamepad;

public abstract class AxisKeyProcessor extends KeyProcessor {
    private int[][][] keyTable;

    private static int[][][] keyTableXY8 = new int [][][] {
        {   // 左上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 上
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 下
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 原点
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        }
    };

    private static int[][][] keyTablePOV8 = new int [][][] {
        {   // 左上
            {Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 上
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_UP_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_UP_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左下
            {Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 下
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 原点
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        }
    };

    private static int[][][] keyTableXY4 = new int[][][] {
        {   // 左上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN,Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN,Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN,Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 原点
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        }
    };

    private static int[][][] keyTablePOV4 = new int[][][] {
        {   // 左上
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右上
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_UP, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 左下
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN,Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN,Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 右下
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN,Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_DOWN, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        },
        {   // 原点
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_LEFT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_RIGHT, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_UP, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_DOWN, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR},
            {Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR, Gamepad.DIR_ERROR}
        }
    };

    private int[][][] convertTable4 = {
            {   // 左上
                {Gamepad.DIR_UP_LEFT, Gamepad.ACT_LEFT},
                {Gamepad.DIR_UP_LEFT, Gamepad.ACT_X_NEUTRAL},
                {Gamepad.DIR_UP_LEFT, Gamepad.ACT_UP},
                {Gamepad.DIR_UP_LEFT, Gamepad.ACT_Y_NEUTRAL},
                {Gamepad.DIR_UP_LEFT, Gamepad.ACT_ORIGIN},
                {Gamepad.DIR_UP, Gamepad.ACT_LEFT},
                {Gamepad.DIR_UP, Gamepad.ACT_UP_LEFT},
                {Gamepad.DIR_LEFT, Gamepad.ACT_UP},
                {Gamepad.DIR_LEFT, Gamepad.ACT_UP_LEFT}
            },
            {   // 上

            },
            {   // 右上
                {Gamepad.DIR_UP, Gamepad.ACT_RIGHT},
                {Gamepad.DIR_UP, Gamepad.ACT_UP_RIGHT},
                {Gamepad.DIR_UP_RIGHT, Gamepad.ACT_X_NEUTRAL},
                {Gamepad.DIR_UP_RIGHT, Gamepad.ACT_RIGHT},
                {Gamepad.DIR_UP_RIGHT, Gamepad.ACT_UP},
                {Gamepad.DIR_UP_RIGHT, Gamepad.ACT_Y_NEUTRAL},
                {Gamepad.DIR_UP_RIGHT, Gamepad.ACT_ORIGIN},
                {Gamepad.DIR_RIGHT, Gamepad.ACT_UP},
                {Gamepad.DIR_RIGHT, Gamepad.ACT_UP_RIGHT}
            },
            {   // 左

            },
            {   // 右

            },
            {   // 左下
                {Gamepad.DIR_DOWN_LEFT, Gamepad.ACT_LEFT},
                {Gamepad.DIR_DOWN_LEFT, Gamepad.ACT_X_NEUTRAL},
                {Gamepad.DIR_DOWN_LEFT, Gamepad.ACT_Y_NEUTRAL},
                {Gamepad.DIR_DOWN_LEFT, Gamepad.ACT_DOWN},
                {Gamepad.DIR_DOWN_LEFT, Gamepad.ACT_ORIGIN},
                {Gamepad.DIR_LEFT, Gamepad.ACT_DOWN},
                {Gamepad.DIR_LEFT, Gamepad.ACT_DOWN_LEFT},
                {Gamepad.DIR_DOWN, Gamepad.ACT_LEFT},
                {Gamepad.DIR_DOWN, Gamepad.ACT_DOWN_LEFT}
            },
            {   //下

            },
            {   // 右下
                {Gamepad.DIR_RIGHT, Gamepad.ACT_DOWN},
                {Gamepad.DIR_RIGHT, Gamepad.ACT_DOWN_RIGHT},
                {Gamepad.DIR_DOWN, Gamepad.ACT_RIGHT},
                {Gamepad.DIR_DOWN, Gamepad.ACT_DOWN_RIGHT},
                {Gamepad.DIR_DOWN_RIGHT, Gamepad.ACT_X_NEUTRAL},
                {Gamepad.DIR_DOWN_RIGHT, Gamepad.ACT_RIGHT},
                {Gamepad.DIR_DOWN_RIGHT, Gamepad.ACT_Y_NEUTRAL},
                {Gamepad.DIR_DOWN_RIGHT, Gamepad.ACT_DOWN},
                {Gamepad.DIR_DOWN_RIGHT, Gamepad.ACT_ORIGIN}
            }
    };

    /**
     * 軸のためのキー処理用オブジェクトを作成する。
     *
     * @param mapper 方向とキーコードの対応関係
     * @param adjust 4方向キーを8方向キーのための特別な処理を行う場合はtrue
     * @param pov POVを使う場合にtrueを指定する。
     */
    public AxisKeyProcessor(Gamepad gamepad, ComponentStatus[] mapper) {
        super(gamepad, mapper);
        keyTable = new int[keyTableXY8.length][keyTableXY8[0].length][keyTableXY8[0][0].length];
        recalcMapping();
    }

    @SuppressWarnings("unused")
    private void dumpMapping(int[][][] table) {
        for (int i = 0; i < 9; i++) {
            System.out.println("dir = " + i);
            for (int j = 0; j < 11; j++) {
                System.out.println("  act = " + j + " : " + table[i][j][0] + " " + table[i][j][1] + " " + table[i][j][2] + " " + table[i][j][3]);
            }
        }
    }

    public void recalcMapping() {
        // System.out.println("** recalc mapping **");
        for (int dir = 0; dir < keyTable.length; dir++) {
            for (int st = 0; st < keyTable[0].length; st++) {
                if (gamepad.getAxisType() == Gamepad.AXIS_POV) {
                    keyTable[dir][st] = keyTablePOV8[dir][st];
                }
                else {
                    keyTable[dir][st] = keyTableXY8[dir][st];
                }
            }
        }
        for (int dir = 0; dir < convertTable4.length; dir++) {
            if (mapper[dir].getStatus() == ComponentStatus.STATUS_UNUSED) {
                int[][] table = convertTable4[dir];
                if (table != null) {
                    for (int i = 0; i < table.length; i++) {
                        int[] entry = table[i];
                        if (gamepad.getAxisType() == Gamepad.AXIS_POV) {
                            keyTable[entry[0]][entry[1]] = keyTablePOV4[entry[0]][entry[1]];
                        }
                        else {
                            keyTable[entry[0]][entry[1]] = keyTableXY4[entry[0]][entry[1]];
                        }
                    }
                }
            }
        }
        // dumpMapping(keyTable);
    }

    @Override
    public void execute(int state, int action) {
        int[] entry = keyTable[state][action];
        if (entry[0] != Gamepad.DIR_ERROR) {
            release(mapper[entry[0]]);
            if (entry[1] != Gamepad.DIR_ERROR) {
                release(mapper[entry[1]]);
            }
        }
        if (entry[2] != Gamepad.DIR_ERROR) {
            press(mapper[entry[2]]);
            if (entry[3] != Gamepad.DIR_ERROR) {
                press(mapper[entry[3]]);
            }
        }
    }
}
