/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;


/*
 * デバイスを表すインターフェース
 */
public interface Device {
    public void reset();
    public void execute();
}
