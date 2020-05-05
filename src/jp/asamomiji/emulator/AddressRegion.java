/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

public class AddressRegion implements Cloneable {
    private int start;
    private int end;
    private String comment;

    public AddressRegion(int start, int end, String comment) {
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

    public AddressRegion(int start, int end) {
        this(start, end, "");
    }

    public int getStartAddress() {
        return start;
    }

    public void setStartAddress(int start) {
        this.start = start;
    }

    public int getEndAddress() {
        return end;
    }

    public void setEndAddress(int end) {
        this.end = end;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLength() {
        return end - start + 1;
    }

    @Override
    public AddressRegion clone() {
        AddressRegion r;
        try {
            r = (AddressRegion)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError("AddressRegionのクローンに失敗した。");
        }
        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(String.format("0x%04X", start));
        sb.append(",");
        sb.append(String.format("0x%04X", end));
        sb.append(",");
        sb.append(comment);
        sb.append(")");
        return sb.toString();
    }

}
