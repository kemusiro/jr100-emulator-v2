/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/*
 * 実行中のプログラムの属性を定義する。
 */
public class Program {
    private MemorySystem ms = null;
    private String name = "";
    private String comment = "";
    private boolean basicArea = false;
    private ArrayList<AddressRegion> regions = new ArrayList<>();
    private File file = null;

    public Program(MemorySystem ms) {
        this.ms = ms;
    }

    public MemorySystem getMemorySystem() {
        return ms;
    }

    /**
     * プログラム名を設定する。
     * nullを指定した場合は""(空文字)が設定される。
     *
     * @param name プログラム名
     */
    public void setName(String name) {
        this.name = Objects.toString(name, "");
    }

    /**
     * プログラム名を取得する。
     * このメソッドの呼び出しはnullになることはない。プログラム名が無い場合は長さ0の文字列を返す。
     *
     * @return プログラム名
     */
    public String getName() {
        return name;
    }

    public void setBasicArea(boolean value) {
        this.basicArea = value;
    }

    public boolean hasBasicArea() {
        return basicArea;
    }

    public void addAddressRegion(int start, int end) {
        regions.add(new AddressRegion(start, end));
    }

    public void addAddressRegion(int start, int end, String comment) {
        regions.add(new AddressRegion(start, end, comment));
    }

    public void addAddressRegion(AddressRegion a) {
        regions.add(a);
    }

    /**
     * プログラムに対するコメントを設定する。
     * nullを指定した場合は""(空文字)が設定される。
     *
     * @param name プログラム名
     */
    public void setComment(String comment) {
        this.comment = Objects.toString(comment, "");
    }

    /**
     * プログラムのコメントを取得する。
     * このメソッドの呼び出しはnullになることはない。
     *
     * @return プログラム名
     */
    public String getComment() {
        return comment;
    }

    public ArrayList<AddressRegion> getAllAddressRegions() {
        return regions;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append("(");
        sb.append(getComment());
        sb.append(") ");
        sb.append("hasBasic=");
        sb.append(basicArea);
        sb.append(", file=");
        sb.append(file.getAbsolutePath());
        sb.append(", ");
        sb.append(regions.toString());
        return sb.toString();
    }

}
