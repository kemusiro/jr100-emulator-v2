/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */

package jp.asamomiji.emulator.jr100;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import jp.asamomiji.emulator.ROM;

/*
 * このクラスはROM領域を定義する。
 */
public final class BasicRom extends ROM {
    private final static String PROG_FILE_ID = "PROG";

    public BasicRom(String fname, int start, int length) {
        super(start, length);
        readROM(fname);
    }

    public int getFontAddress() {
        return 0xe000;
    }

    private int readByLittleEndian(InputStream in) throws IOException {
        int value1 = in.read();
        int value2 = in.read();
        int value3 = in.read();
        int value4 = in.read();

        return value1 + (value2 << 8) + (value3 << 16) + (value4 << 24);
    }

    private boolean checkFile(FileInputStream in) throws IOException {
        byte[] id = new byte[4];

        in.read(id);
        if (!new String(id).equals(PROG_FILE_ID)) {
            return false;
        }
        // 現在のところファイルのバージョンはチェックしない。
        in.skip(4);
        return true;
    }

    public void readROM(String filename) {
        try {
            FileInputStream in = new FileInputStream(filename);
            if (!checkFile(in)) {
                throw new IOException("invalid file format");
            }
            int nlen = readByLittleEndian(in);
            byte[] pname_array = new byte[nlen];
            in.read(pname_array);

            int startAddress = readByLittleEndian(in);
            int length = readByLittleEndian(in);
            if (startAddress < 0 || startAddress > 65535) {
                throw new IOException("invalid address: " + startAddress);
            }
            if (length < 0 || startAddress + length > 65536) {
                throw new IOException("invalid length: " + length);
            }

            readByLittleEndian(in);

            for (int i = 0; i < length; i++) {
                int v = in.read();
                data[i] = (byte)(v & 0xff);
            }
            in.close();
        }
        catch (IOException e) {
            return ;
        }
    }
}
