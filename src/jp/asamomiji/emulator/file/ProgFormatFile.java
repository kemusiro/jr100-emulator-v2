/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.file;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jp.asamomiji.emulator.AddressRegion;
import jp.asamomiji.emulator.MemorySystem;
import jp.asamomiji.emulator.Program;
import jp.asamomiji.emulator.jr100.JR100;

/*
 * このクラスはPROG形式のファイルを読み書きするための処理を定義する。
 */
public class ProgFormatFile extends DataFile {
    public final static int PROG_MAX_PROGRAM_NAME_LENGTH = 256;
    public final static int PROG_MAX_PROGRAM_LENGTH = 65536;
    public final static int PROG_MAX_COMMENT_LENGTH = 1024;
    public final static int PROG_MAX_BINARY_SECTIONS = 256;

    private final static int MIN_VERSION = 1;
    private final static int MAX_VERSION = 2;

    private final static int MAX_BUFFER_SIZE = PROG_MAX_PROGRAM_LENGTH;

    private final static int MAGIC_NUMBER = 0x474f5250;
    private final static int SECTION_PNAM = 0x4d414e50;
    private final static int SECTION_PBAS = 0x53414250;
    private final static int SECTION_PBIN = 0x4e494250;
    private final static int SECTION_CMNT = 0x544e4d43;

    public ProgFormatFile(File file) {
        super(file);
    }

    private void checkMagic(InputStream in, byte[] buffer)
            throws InvalidFormatException, EOFException, IOException {
        int buflen;

        buflen = in.read(buffer, 0, 4);
        if (buflen == -1) {
            throw new EOFException();
        }
        else if (buflen < 4) {
            throw new InvalidFormatException("ファイルフォーマットが違います。");
        }
        int value = convertLEToInteger(buffer, 0, 4);
        if (value != MAGIC_NUMBER) {
            throw new InvalidFormatException("ファイルフォーマットが違います。");
        }
    }

    private void writeMagic(OutputStream out) throws IOException {
        out.write(MAGIC_NUMBER & 0xff);
        out.write((MAGIC_NUMBER & 0xff00) >> 8);
        out.write((MAGIC_NUMBER & 0xff0000) >> 16);
        out.write((MAGIC_NUMBER & 0xff000000) >> 24);
    }

    private int readInteger(InputStream in, byte[] buffer, int min, int max, String attr)
            throws InvalidFormatException, EOFException, IOException {
        int buflen;

        buflen = in.read(buffer, 0, 4);
        if (buflen == -1) {
            throw new EOFException();
        }
        else if (buflen < 4) {
            throw new InvalidFormatException(attr + "の長さを読み取れませんでした。(" + buflen + ")");
        }
        int value = convertLEToInteger(buffer, 0, 4);
        if (value < min) {
            throw new InvalidFormatException(attr + "の長さ(" + value + ")が最小値(" + max + ")より小さいです。");
        }
        if (value > max) {
            throw new InvalidFormatException(attr + "の長さ(" + value + ")が最大値(" + max + ")を超えています。");
        }
        return value;
    }

    private void writeInteger(OutputStream out, int value) throws IOException {
        out.write(value & 0xff);
        out.write((value & 0xff00) >> 8);
        out.write((value & 0xff0000) >> 16);
        out.write((value & 0xff000000) >> 24);
    }

    private byte[] readBytes(InputStream in, byte[] buffer, int length, String attr)
            throws InvalidFormatException, EOFException, IOException {
        int buflen;

        buflen = in.read(buffer, 0, length);
        if (buflen == -1) {
            throw new EOFException();
        }
        else if (buflen < length) {
            throw new InvalidFormatException(attr + "を読み込めませんでした。(" + buflen + ")");
        }
        return buffer;
    }

    private void writeBytes(OutputStream out, byte[] data) throws IOException {
        for (int i = 0; i < data.length; i++) {
            out.write(data[i]);
        }
    }

    private String readUTF8String(InputStream in, byte[] buffer, int maxlen, String attr)
            throws InvalidFormatException, EOFException, IOException {
        int buflen;

        buflen = in.read(buffer, 0, 4);
        if (buflen == -1) {
            throw new EOFException();
        }
        else if (buflen < 4) {
            throw new InvalidFormatException(attr + "の長さを読み取れませんでした。(" + buflen + ")");
        }
        int len = convertLEToInteger(buffer, 0, 4);
        if (len > maxlen) {
            throw new InvalidFormatException(attr + "の長さが長すぎます。(" + len + ")");
        }
        buflen = in.read(buffer, 0, len);
        if (buflen != len) {
            throw new InvalidFormatException(attr + "を読み取れませんでした。(" + buflen + ")");
        }

        return new String(buffer, 0, len, "UTF-8");
    }

    private void writeUTF8String(OutputStream out, String str) throws IOException {
        if (str != null && !str.equals("")) {
            byte[] bytes = str.getBytes("UTF-8");
            writeInteger(out, bytes.length);
            writeBytes(out, bytes);
        }
        else {
            writeInteger(out, 0);
        }
    }

    /**
     * リトルエンディアンで格納されているバイト配列をJavaのint型に変換する。
     *
     * @param b int型に変換するバイト配列
     * @param off 変換する先頭バイトのインデックス
     * @param len 変換するバイト数
     * @return バイト配列をリトルエンディアンとみなしたときの整数値
     * @throws IndexOutOfBoundsException off引数とlen引数の和がb配列の境界の外側を差す場合
     */
    private int convertLEToInteger(byte[] b, int off, int len) {
        if (off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 4) {
            return (b[off] & 0xff)
                    + ((b[off + 1] & 0xff) << 8)
                    + ((b[off + 2] & 0xff) << 16)
                    + ((b[off + 3] & 0xff) << 24);
        }
        else if (len == 0) {
            return 0;
        }
        else if (len == 1) {
            return b[off];
        }
        else if (len == 2) {
            return (b[off] & 0xff)
                    + ((b[off + 1] & 0xff) << 8);
        }
        else if (len == 3) {
            return (b[off] & 0xff)
                    + ((b[off + 1] & 0xff) << 8)
                    + ((b[off + 2] & 0xff) << 16);
        }
        else {
            return (b[off] & 0xff)
                    + ((b[off + 1] & 0xff) << 8)
                    + ((b[off + 2] & 0xff) << 16)
                    + ((b[off + 3] & 0xff) << 24);
        }
    }

    @Override
    public Program load_jr100(MemorySystem ms) {
        Program p = new Program(ms);
        byte[] buffer = new byte[4];
        try (FileInputStream in = new FileInputStream(file)) {
            checkMagic(in, buffer);
            int version = readInteger(in, buffer, MIN_VERSION, MAX_VERSION, "フォーマットバージョン");
            if (version == 1) {
                load1(in, p);
            }
            else if (version == 2) {
                load2(in, p);
            }
            else {
                throw new AssertionError("invalid version");
            }
        }
        catch (FileNotFoundException e) {
            setErrorStatus(STATUS_FILE_NOT_FOUND, e.getMessage());
            return p;
        }
        catch (InvalidFormatException e) {
            setErrorStatus(STATUS_INVALID_FORMAT, e.getMessage());
            return p;
        }
        catch (IOException e) {
            setErrorStatus(STATUS_IO_ERROR, e.getMessage());
            return p;
        }
        setErrorStatus(STATUS_SUCCESS, null);
        return p;
    }

    /**
     * PROGファイルフォーマットのバージョン2のファイルを読み込む。
     * 入力ストリームはこのメソッドの呼び出し元でcloseすること。
     *
     * @param in データを読み込むための入力ストリーム
     * @param p 読み込んだプログラムの情報を格納するProgramオブジェクト
     */
    private void load1(InputStream in, Program p) throws InvalidFormatException, IOException {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        MemorySystem ms = p.getMemorySystem();

        try {
            p.setName(readUTF8String(in, buffer, PROG_MAX_PROGRAM_NAME_LENGTH, "プログラム名"));
            int start_address = readInteger(in, buffer, 0, PROG_MAX_PROGRAM_LENGTH, "開始アドレス");
            int length = readInteger(in, buffer, 0, PROG_MAX_PROGRAM_LENGTH, "プログラム長");
            if (start_address + length > PROG_MAX_PROGRAM_LENGTH) {
                throw new InvalidFormatException("終了アドレス(" + (start_address + length) + ")が大きすぎます。");
            }
            int flag = readInteger(in, buffer, Integer.MIN_VALUE, Integer.MAX_VALUE, "フラグ");
            if (flag == 0) {
                p.setBasicArea(true);
            }
            else {
                p.addAddressRegion(start_address, start_address + length - 1);
                p.setBasicArea(false);
            }
            readBytes(in, buffer, length, "プログラム");
            int end_address = start_address + length - 1;
            for (int i = start_address; i <= end_address; i++) {
                ms.store8(i, buffer[i - start_address]);
            }
            if (flag == 0) {
                ms.store8(end_address + 1, (byte)0xdf);
                ms.store8(end_address + 2, (byte)0xdf);
                ms.store8(end_address + 3, (byte)0xdf);
                ms.store8(0x0006, (byte)((end_address >> 8) & 0xff));
                ms.store8(0x0007, (byte)(end_address & 0xff));
                ms.store8(0x0008, (byte)(((end_address + 1) >> 8) & 0xff));
                ms.store8(0x0009, (byte)((end_address + 1) & 0xff));
                ms.store8(0x000a, (byte)(((end_address + 2) >> 8) & 0xff));
                ms.store8(0x000b, (byte)((end_address + 2) & 0xff));
                ms.store8(0x000c, (byte)(((end_address + 3) >> 8) & 0xff));
                ms.store8(0x000d, (byte)((end_address + 3) & 0xff));
                p.setBasicArea(true);
            }
        }
        catch (EOFException e) {
            throw new InvalidFormatException("ファイルが不完全です。");
        }
    }

    /**
     * PROGファイルフォーマットのバージョン2のファイルを読み込む。
     * 入力ストリームはこのメソッドの呼び出し元でcloseすること。
     *
     * @param in データを読み込むための入力ストリーム
     * @param p 読み込んだプログラムの情報を格納するProgramオブジェクト
     */
    private void load2(InputStream in, Program p) throws InvalidFormatException, IOException {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        MemorySystem ms = p.getMemorySystem();
        int section_id;
        int section_length;
        int count_pnam = 0;
        int count_cmnt = 0;
        int count_pbas = 0;
        int count_pbin = 0;
        while (true) {
            try {
                section_id = readInteger(in, buffer, Integer.MIN_VALUE, Integer.MAX_VALUE, "セクション名");
            }
            catch (EOFException e) {
                break;
            }
            section_length = readInteger(in, buffer, 0, Integer.MAX_VALUE, "セクション長");

            if (section_id == SECTION_PNAM) {
                if (count_pnam == 1) {
                    in.skip(section_length);
                    continue;
                }
                count_pnam++;
                String name = readUTF8String(in, buffer, PROG_MAX_PROGRAM_NAME_LENGTH, "プログラム名");
                p.setName(name);

                if (section_length != name.getBytes("UTF-8").length + 4) {
                    throw new InvalidFormatException("セクションPNAMの長さが不正です。(" + section_length + ")");
                }
            }
            else if (section_id == SECTION_PBAS) {
                if (count_pbas == 1) {
                    in.skip(section_length);
                    continue;
                }
                count_pbas++;
                int start_address = JR100.ADDRESS_START_OF_BASIC_PROGRAM;
                int program_length = readInteger(in, buffer, 0, PROG_MAX_PROGRAM_LENGTH, "BASICプログラム長");
                if (start_address + program_length > PROG_MAX_PROGRAM_LENGTH) {
                    throw new InvalidFormatException("終了アドレス(" + (start_address + program_length) + ")が大きすぎます。");
                }
                readBytes(in, buffer, program_length, "BASICプログラム");
                int end_address = start_address + program_length - 1;
                for (int addr = start_address; addr <= end_address; addr++) {
                    ms.store8(addr, buffer[addr - start_address]);
                }
                ms.store8(end_address + 1, (byte)0xdf);
                ms.store8(end_address + 2, (byte)0xdf);
                ms.store8(end_address + 3, (byte)0xdf);
                ms.store8(0x0006, (byte)((end_address >> 8) & 0xff));
                ms.store8(0x0007, (byte)(end_address & 0xff));
                ms.store8(0x0008, (byte)(((end_address + 1) >> 8) & 0xff));
                ms.store8(0x0009, (byte)((end_address + 1) & 0xff));
                ms.store8(0x000a, (byte)(((end_address + 2) >> 8) & 0xff));
                ms.store8(0x000b, (byte)((end_address + 2) & 0xff));
                ms.store8(0x000c, (byte)(((end_address + 3) >> 8) & 0xff));
                ms.store8(0x000d, (byte)((end_address + 3) & 0xff));
                p.setBasicArea(true);

                if (section_length != program_length + 4) {
                    throw new InvalidFormatException("セクションPBASの長さが不正です。(" + section_length + ")");
                }
            }
            else if (section_id == SECTION_PBIN) {
                if (count_pbin == PROG_MAX_BINARY_SECTIONS) {
                    in.skip(section_length);
                    continue;
                }
                count_pbin++;
                int start_addr = readInteger(in, buffer, 0, PROG_MAX_PROGRAM_LENGTH, "開始アドレス");
                int data_length = readInteger(in, buffer, 0, PROG_MAX_PROGRAM_LENGTH, "データ長");
                if (start_addr + data_length > PROG_MAX_PROGRAM_LENGTH) {
                    throw new InvalidFormatException("終了アドレス(" + (start_addr + data_length) + ")が大きすぎます。");
                }
                readBytes(in, buffer, data_length, "マシン語データ");

                for (int addr = start_addr; addr < start_addr + data_length; addr++) {
                    ms.store8(addr, buffer[addr - start_addr]);
                }
                String comment = readUTF8String(in, buffer, PROG_MAX_COMMENT_LENGTH, "アドレス範囲のコメント");
                p.addAddressRegion(start_addr, start_addr + data_length - 1, comment);

                if (section_length != data_length + 4 + comment.getBytes("UTF-8").length + 4) {
                    throw new InvalidFormatException("セクションPBINの長さが不正です。(" + section_length + ")");
                }
            }
            else if (section_id == SECTION_CMNT) {
                if (count_cmnt == 1) {
                    in.skip(section_length);
                    continue;
                }
                count_cmnt++;
                String comment = readUTF8String(in, buffer, PROG_MAX_COMMENT_LENGTH, "コメント");
                p.setComment(comment);

                if (section_length != comment.getBytes().length + 4) {
                    throw new InvalidFormatException("セクションCMNTの長さが不正です。(" + section_length + ")");
                }
            }
            else {
                in.skip(section_length);
            }
        }
    }

    @Override
    public void save_jr100(Program p, int version) {
        if (p == null) {
            return ;
        }
        try (FileOutputStream out = new FileOutputStream(file)) {
            writeMagic(out);
            writeInteger(out, version);
            if (version == 1) {
                save1(out, p);
            }
            else if (version == 2) {
                save2(out, p);
            }
            else {
                return ;
            }
        }
        catch (FileNotFoundException e) {
            setErrorStatus(STATUS_FILE_NOT_FOUND, e.getMessage());
            return;
        }
        catch (InvalidFormatException e) {
            setErrorStatus(STATUS_INVALID_FORMAT, e.getMessage());
            return;
        }
        catch (IOException e) {
            setErrorStatus(STATUS_IO_ERROR, e.getMessage());
            return;
        }
        setErrorStatus(STATUS_SUCCESS, null);
    }

    private void save1(OutputStream out, Program p) throws InvalidFormatException, IOException {
        MemorySystem ms = p.getMemorySystem();
        ArrayList<AddressRegion> r = p.getAllAddressRegions();
        if ((p.hasBasicArea() && r.size() != 0) || (!p.hasBasicArea() && r.size() != 1)) {
            throw new InvalidFormatException(
                    "このプログラムはバージョン1形式では保存できません。BASIC領域かバイナリ領域のいずれか1つのみ格納できます。");
        }
        writeUTF8String(out, p.getName());
        int start_addr;
        int end_addr;
        int flag;
        if (p.hasBasicArea()) {
            start_addr = JR100.ADDRESS_START_OF_BASIC_PROGRAM;
            end_addr = ms.load16(JR100.WORKAREA_END_OF_BASIC_PROGRAM);
            flag = 0;
        }
        else {
            start_addr = r.get(0).getStartAddress();
            end_addr = r.get(0).getEndAddress();
            flag = 1;

        }
        writeInteger(out, start_addr);
        writeInteger(out, end_addr - start_addr + 1);
        writeInteger(out, flag);
        byte[] data = new byte[end_addr - start_addr + 1];
        for (int addr = start_addr; addr <= end_addr; addr++) {
            data[addr - start_addr] = ms.load8(addr);
        }
        writeBytes(out, data);
    }

    private void save2(OutputStream out, Program p) throws InvalidFormatException, IOException {
        MemorySystem ms = p.getMemorySystem();
        ArrayList<AddressRegion> regions = p.getAllAddressRegions();

        String name = p.getName();
        if (name != null && !name.equals("")) {
            writeInteger(out, SECTION_PNAM);
            writeInteger(out, 4 + name.getBytes("UTF-8").length);
            writeUTF8String(out, name);
        }

        String comment = p.getComment();
        if (comment != null && !comment.equals("")) {
            byte[] bytes = comment.getBytes("UTF-8");
            writeInteger(out, SECTION_CMNT);
            writeInteger(out, 4 + bytes.length);
            writeInteger(out, bytes.length);
            writeBytes(out, bytes);
        }

        if (p.hasBasicArea()) {
            int start_addr;
            int end_addr;

            start_addr = JR100.ADDRESS_START_OF_BASIC_PROGRAM;
            end_addr = ms.load16(JR100.WORKAREA_END_OF_BASIC_PROGRAM);

            writeInteger(out, SECTION_PBAS);
            writeInteger(out, 4 + (end_addr - start_addr + 1));
            writeInteger(out, end_addr - start_addr + 1);
            byte[] data = new byte[end_addr - start_addr + 1];
            for (int addr = start_addr; addr <= end_addr; addr++) {
                data[addr - start_addr] = ms.load8(addr);
            }
            writeBytes(out, data);
        }

        int count = 0;
        for (AddressRegion r : regions) {
            int start_addr = r.getStartAddress();
            int end_addr = r.getEndAddress();
            String region_comment = r.getComment();

            writeInteger(out, SECTION_PBIN);
            writeInteger(out, 4 + (end_addr - start_addr + 1) + 4 + region_comment.getBytes("UTF-8").length);
            writeInteger(out, start_addr);
            writeInteger(out, end_addr - start_addr + 1);
            byte[] data = new byte[end_addr - start_addr + 1];
            for (int addr = start_addr; addr <= end_addr; addr++) {
                data[addr - start_addr] = ms.load8(addr);
            }
            writeBytes(out, data);
            writeUTF8String(out, region_comment);
            count++;
            if (count == PROG_MAX_BINARY_SECTIONS) {
                break;
            }
        }
    }
}
