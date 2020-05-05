/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.file;

import java.io.File;

/*
 * このクラスは、テキスト形式のファイルを読み書きするための処理を定義する。
 */
public abstract class TextFormatFile extends DataFile {

    protected TextFormatFile(File file) {
        super(file);
    }

}
