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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import jp.asamomiji.emulator.AddressRegion;
import jp.asamomiji.emulator.MemorySystem;
import jp.asamomiji.emulator.Program;
import jp.asamomiji.emulator.jr100.JR100;

/**
 * このクラスは、バイナリデータ格納用のテキストファイルを読み書きするための処理を定義する。
 * フォーマットは以下。
 * addr <SPC> value1 <SPC> value2 <SPC> ... <SPC> valuen [: checksum]
 * '#'以降は行末までコメントとみなす。
 * すべての値は16進数であるとみなす。'0x'などの前値記号は不要である。
 */
public class BinaryTextFormatFile extends TextFormatFile {
    public BinaryTextFormatFile(File file) {
        super(file);
    }

    enum LineState {
        HEAD,
        ADDR,
        VALUE,
        CHECKSUM
    };

    @Override
    public Program load_jr100(MemorySystem ms) {
        Program p = new Program(ms);
        p.setName("");
        p.setBasicArea(false);

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            String prev_line;
            int addr = 0;
            boolean file_head = true;
            int start_addr = 0;
            StringTokenizer tokens;
            LineState state;
            int checksum;

            state = LineState.HEAD;
            prev_line = null;
            while ((line = r.readLine()) != null) {
                if (state != LineState.HEAD && state != LineState.VALUE) {
                    setErrorStatus(STATUS_INVALID_FORMAT, "不正な行です。" + prev_line);
                    return p;
                }
                tokens = new StringTokenizer(line);
                if (tokens.countTokens() == 0) {
                    continue;
                }
                state = LineState.ADDR;
                checksum = 0;
                while (tokens.hasMoreTokens()) {
                    String t = tokens.nextToken();
                    byte value;
                    try {
                        switch (state) {
                        case ADDR:
                            if (t.equals("#")) {
                                while (tokens.hasMoreTokens()) {
                                    tokens.nextToken();
                                }
                                state = LineState.HEAD;
                            }
                            else if (t.equals(":")) {
                                setErrorStatus(STATUS_NO_ADDRESS, "アドレスがありません。");
                                return p;
                            }
                            else {
                                if (file_head) {
                                    addr = (Integer.parseInt(t, 16) & 0xffff);
                                    start_addr = addr;
                                    file_head = false;
                                }
                                else {
                                    int prev_addr = addr - 1;
                                    addr = (Integer.parseInt(t, 16) & 0xffff);
                                    if (addr - prev_addr > 1) {
                                        p.addAddressRegion(start_addr, prev_addr);
                                        start_addr = addr;
                                    }
                                }
                                state = LineState.VALUE;
                            }
                            break;
                        case VALUE:
                            if (t.equals("#")) {
                                while (tokens.hasMoreTokens()) {
                                    ;
                                }
                                state = LineState.HEAD;
                            }
                            else if (t.equals(":")) {
                                state = LineState.CHECKSUM;
                            }
                            else {
                                value = (byte)(Integer.parseInt(t, 16) & 0xff);
                                ms.store8(addr, value);
                                checksum += (value & 0xff);
                                addr = ((addr + 1) & 0xffff);
                            }
                            break;
                        case CHECKSUM:
                            if (t.equals("#")) {
                                setErrorStatus(STATUS_INVALID_FORMAT, "チェックサムがありません。");
                                return p;
                            }
                            else if (t.equals(":")) {
                                setErrorStatus(STATUS_INVALID_FORMAT, "不正な文字があります。");
                                return p;
                            }
                            else {
                                if ((checksum & 0xff) != (Integer.parseInt(t, 16) & 0xff)) {
                                    String s = String.format("チェックサムエラー(%02X)\n", (checksum & 0xff));
                                    setErrorStatus(STATUS_CHECK_SUM_ERROR, s + line);
                                    return p;
                                }
                                checksum = 0;
                                state = LineState.HEAD;
                            }
                            break;
                        default:
                            throw new AssertionError("invalid state");
                        }
                    }
                    catch (NumberFormatException e) {
                        setErrorStatus(STATUS_INVALID_FORMAT, "数値が不正です。: " + t);
                        return p;
                    }
                }
                prev_line = line;
            }
            if (state != LineState.HEAD && state != LineState.VALUE) {
                setErrorStatus(STATUS_INVALID_FORMAT, "不正な行です。\n" + prev_line);
                return p;
            }
            if (!file_head && addr > start_addr) {
                p.addAddressRegion(start_addr, addr - 1);
            }
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

    private void dumpRegion(PrintStream s, MemorySystem ms, int start, int end) {
        int sum = 0;
        if (start % 16 != 0) {
            s.printf("%04X ", start);
        }
        for (int a = start; a <= end; a++) {
            if (a % 16 == 0) {
                s.printf("%04X ", a);
            }
            int value = (ms.load8(a) & 0xff);
            s.printf("%02X ", value);
            sum += value;
            if (a % 16 == 15) {
                s.printf(": %02X", (sum & 0xff));
                s.println();
                sum = 0;
            }
        }
        if (end % 16 != 15) {
            s.printf(": %02X", (sum & 0xff));
            s.println();
        }
        s.printf("\n");
    }

    @Override
    public void save_jr100(Program p, int version) {
        MemorySystem ms = p.getMemorySystem();
        ArrayList<AddressRegion> regions = p.getAllAddressRegions();
        try (PrintStream s = new PrintStream(file)) {
            if (p.hasBasicArea()) {
                dumpRegion(s, ms, JR100.ADDRESS_START_OF_BASIC_PROGRAM, ms.load16(JR100.WORKAREA_END_OF_BASIC_PROGRAM));
            }
            for (AddressRegion r : regions) {
                dumpRegion(s, ms, r.getStartAddress(), r.getEndAddress());
            }
        }
        catch (FileNotFoundException e) {
            setErrorStatus(STATUS_FILE_NOT_FOUND, e.getMessage());
            return;
        }
        setErrorStatus(STATUS_SUCCESS, null);
    }
}
