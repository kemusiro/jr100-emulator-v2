/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import jp.asamomiji.emulator.MemorySystem;
import jp.asamomiji.emulator.Program;
import jp.asamomiji.emulator.jr100.JR100;
import jp.asamomiji.emulator.jr100.MainRam;

/*
 * このクラスは、BASICテキストファイルを読み書きするための処理を定義する。
 */
public class BasicTextFormatFile extends TextFormatFile {
    public BasicTextFormatFile(File file) {
        super(file);
    }

    private int getLineNumber(StringBuffer buf) {
        skipWhiteSpace(buf);
        if (buf.length() == 0) {
            return -1;
        }
        int index = 0;
        while (Character.isDigit(buf.charAt(index))) {
            index++;
        }
        if (index == 0) {
            return -1;
        }
        else {
            int number;
            try {
                number = Integer.parseInt(buf.substring(0, index));
            }
            catch (NumberFormatException e) {
                return -1;
            }
            buf.delete(0, index);
            return number;
        }
    }

    private void canonicalizeLine(StringBuffer buf) {
        int index;
        index = 0;
        if (buf.length() == 0) {
            return ;
        }
        while (index < buf.length() && Character.isWhitespace(buf.charAt(index))) {
            index++;
        }
        if (index > 0) {
            buf.delete(0, index);
        }
        index = buf.length() - 1;
        while (index >= 0 && Character.isWhitespace(buf.charAt(index))) {
            index--;
        }
        if ((buf.length() - 1) - index > 0) {
            buf.delete(index + 1, buf.length());
        }
        for (int i = 0; i < buf.length(); i++) {
            buf.setCharAt(i, Character.toUpperCase(buf.charAt(i)));
        }
    }

    private void skipWhiteSpace(StringBuffer buf) {
        int index = 0;
        while (index < buf.length() && Character.isWhitespace(buf.charAt(index))) {
            index++;
        }
        if (index > 0) {
            buf.delete(0, index);
        }
    }

    @Override
    public Program load_jr100(MemorySystem ms) {
        Program p = new Program(ms);
        p.setName("");
        p.setBasicArea(true);
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            int line_length;
            StringBuffer buf = new StringBuffer();
            int addr = JR100.ADDRESS_START_OF_BASIC_PROGRAM;
            // int end_addr = ms.getEndAddress(MainRam.class);
            int end_addr = 0x7fff;

            while ((line = r.readLine()) != null) {
                buf.replace(0, buf.length(), line);
                canonicalizeLine(buf);
                if (buf.length() == 0) {
                    continue;
                }
                line_length = 0;
                int line_number = getLineNumber(buf);
                if (line_number == -1) {
                    setErrorStatus(STATUS_INVALID_FORMAT, "行番号がありません。");
                    return p;
                }
                else if (line_number < 1 || line_number > 32767) {
                    setErrorStatus(STATUS_INVALID_FORMAT, "行番号(" + line_number + ")が不正です。");
                    return p;
                }
                skipWhiteSpace(buf);
                ms.store16(addr, (short)(line_number & 0xffff));
                line_length += 2;
                addr += 2;
                for (int i = 0; i < buf.length(); i++) {
                    if (addr > end_addr) {
                        setErrorStatus(STATUS_MEMORY_FULL, "メモリがいっぱいです。");
                        return p;
                    }
                    byte data = (byte)(buf.charAt(i) & 0xff);
                    if (buf.charAt(i) == '\\') {
                        if (i + 2 < buf.length()) {
                            try {
                                data = (byte)(Integer.parseInt(buf.substring(i + 1, i + 3), 16) & 0xff);
                                i += 2;
                            }
                            catch (NumberFormatException e) {
                                setErrorStatus(STATUS_INVALID_FORMAT, "不正なエスケープ文字(\\" + buf.substring(i+1, i+3) + ")があります。\n" + line);
                                return p;
                            }
                        }
                        else {
                            setErrorStatus(STATUS_INVALID_FORMAT, "行末に達しました。\n" + line);
                            return p;
                        }
                    }
                    ms.store8(addr++, data);
                    line_length++;
                }
                if (line_length > 72) {
                    setErrorStatus(STATUS_INVALID_FORMAT, "行の長さが長すぎます。\n" + line);
                    return p;
                }
                ms.store8(addr++, (byte)0x00);
            }
            if (addr + 3 > end_addr) {
                setErrorStatus(STATUS_MEMORY_FULL, "メモリがいっぱいです。");
                return p;
            }
            int end = addr + 1;
            ms.store8(addr++, (byte)0xdf);
            ms.store8(addr++, (byte)0xdf);  // BASICプログラムの末尾
            ms.store8(addr++, (byte)0xdf);
            ms.store8(0x0006, (byte)((end >> 8) & 0xff));
            ms.store8(0x0007, (byte)(end & 0xff));
            ms.store8(0x0008, (byte)(((end + 1) >> 8) & 0xff));
            ms.store8(0x0009, (byte)((end + 1) & 0xff));
            ms.store8(0x000a, (byte)(((end + 2) >> 8) & 0xff));
            ms.store8(0x000b, (byte)((end + 2) & 0xff));
            ms.store8(0x000c, (byte)(((end + 3) >> 8) & 0xff));
            ms.store8(0x000d, (byte)((end + 3) & 0xff));
        }
        catch (FileNotFoundException e) {
            setErrorStatus(STATUS_FILE_NOT_FOUND, e.getMessage());
            return p;
        }
        catch (IOException e) {
            setErrorStatus(STATUS_IO_ERROR, e.getMessage());
            return p;
        }
        setErrorStatus(STATUS_SUCCESS, null);
        return p;
    }

    @Override
    public void save_jr100(Program p, int version) {
        MemorySystem ms = p.getMemorySystem();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            int addr = JR100.ADDRESS_START_OF_BASIC_PROGRAM;
            int end_addr = ms.getMemory(MainRam.class).getEndAddress();
            int line_number = (ms.load16(addr) & 0xffff);
            while (addr < end_addr && line_number != 0xdfdf) {
                w.write(Integer.toString(line_number));
                w.write(' ');
                addr += 2;
                int value;
                while ((value = (ms.load8(addr) & 0xff)) != 0x00) {
                    if (value >= 0x80) {
                        w.write('\\');
                        String hex = Integer.toHexString(value).toUpperCase();
                        w.write(hex, hex.length() -2, 2);
                    }
                    else {
                        w.write((char)value);
                    }
                    addr++;
                }
                w.write(System.getProperty("line.separator"));
                addr++;
                line_number = (ms.load16(addr) & 0xffff);
            }
        }
        catch (FileNotFoundException e) {
            setErrorStatus(STATUS_FILE_NOT_FOUND, e.getMessage());
            return;
        }
        catch (IOException e) {
            setErrorStatus(STATUS_IO_ERROR, e.getMessage());
            return;
        }
        setErrorStatus(STATUS_SUCCESS, null);
    }
}
