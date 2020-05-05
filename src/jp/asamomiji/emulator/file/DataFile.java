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
import java.io.FileInputStream;

import jp.asamomiji.emulator.MemorySystem;
import jp.asamomiji.emulator.Program;

/**
 * この抽象クラスは、エミュレータで読み可能なデータを定義する。
 */
public abstract class DataFile {
    public static final int FORMAT_UNKNOWN = 0;
    public static final int FORMAT_PROG = 1;
    public static final int FORMAT_BASIC_TEXT = 2;
    public static final int FORMAT_BINARY_TEXT = 3;

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_NO_ADDRESS = 1;
    public static final int STATUS_CHECK_SUM_ERROR = 2;
    public static final int STATUS_INVALID_FORMAT = 3;
    public static final int STATUS_FILE_NOT_FOUND = 4;
    public static final int STATUS_IO_ERROR = 5;
    public static final int STATUS_UNEXPECTED_ERROR = 6;
    public static final int STATUS_MEMORY_FULL = 7;

    protected File file = null;
    protected int errorStatus = STATUS_SUCCESS;
    protected String errorMessage = null;

    protected DataFile(File file) {
        this.file = file;
    }

    public abstract Program load_jr100(MemorySystem ms);
    public abstract void save_jr100(Program p, int version);

    public void setErrorStatus(int status, String message) {
        this.errorStatus = status;
        this.errorMessage = message;
    }

    public int getErrorStatus() {
        return errorStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    protected int withMessage(int status, String message) {
        errorMessage = message;
        return status;
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf(".");
        if (index != -1) {
            return filename.substring(index + 1);
        }
        return null;
    }

    private static byte[] readMagic(String filename) {
        byte[] magic = new byte[4];
        try (FileInputStream in = new FileInputStream(filename)) {
            in.read(magic);
        }
        catch (Exception e) {
            return null;
        }
        return magic;
    }

    public static boolean isProgFile(File f) {
        String ext = getExtension(f.getName());
        if (ext != null && ext.equals("prg")) {
            return true;
        }
        else {
            byte[] magic = readMagic(f.getName());
            if (magic == null) {
                return false;
            }
            if (magic[0] == (byte)0x50 &&   /* 'P' */
                    magic[1] == (byte)0x52 &&   /* 'R' */
                    magic[2] == (byte)0x4f &&   /* 'O' */
                    magic[3] == (byte)0x47) {   /* 'G' */
                return true;
            }
        }
        return false;
    }

    public static boolean isBasicTextFile(File f) {
        String ext = getExtension(f.getName());
        if (ext != null && (ext.equals("txt") || ext.equals("bas"))) {
            return true;
        }
        else {
            return false;
        }
    }
}
