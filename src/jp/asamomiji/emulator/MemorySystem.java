/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public final class MemorySystem {
    private Addressable[] instance;
    private HashMap<Class<? extends Addressable>, Addressable> map =
        new HashMap<Class<? extends Addressable>, Addressable>();

    private boolean debug = false;

    public MemorySystem() {
    }

    public void allocateSpace(int capacity) {
        if (capacity < 0 || capacity > 65536) {
            throw new RuntimeException("容量(" + capacity + ")が大きすぎます。");
        }
        instance = new Addressable[capacity];
        UnmappedMemory um = new UnmappedMemory(0, capacity);
        Arrays.fill(instance, um);
    }

    public void registMemory(Addressable m) {
        Arrays.fill(instance, m.getStartAddress(), m.getEndAddress() + 1, m);
        map.put(m.getClass(), m);
    }

    public Addressable getMemory(Class<? extends Addressable> clazz) {
        return map.get(clazz);
    }

    public Collection<Addressable> getMemories() {
        return map.values();
    }

    public int getStartAddress(Class<? extends Addressable> c) {
        return map.get(c).getStartAddress();
    }

    public int getEndAddress(Class<? extends Addressable> c) {
        return map.get(c).getEndAddress();
    }

    public byte load8(int address) {
        address &= 0xffff;
        byte v = instance[address].load8(address);
        if (debug) {
            System.out.printf("load8: addr=%04x val=%02x\n", address, v);
        }
        return v;
    }

    public void store8(int address, byte value) {
        address &= 0xffff;
        if (debug) {
            System.out.printf("store8: addr=%04x val=%02x\n", address, value & 0xff);
        }
        instance[address].store8(address, value);
    }

    public short load16(int address) {
        int a1 = address & 0xffff;
        int a2 = (address + 1) & 0xffff;
        short v = (short)(((instance[a1].load8(a1) & 0xff) << 8) + (instance[a2].load8(a2) & 0xff));
        if (debug) {
            System.out.printf("load16: addr=%04x val=%04x\n", a1, v & 0xffff);
        }
        return v;
    }

    public void store16(int address, short value) {
        int a1 = address & 0xffff;
        int a2 = (address + 1) & 0xffff;
        if (debug) {
            System.out.printf("store16: addr=%04x val=%04x\n", a1, value & 0xffff);
        }
        instance[a1].store8(a1, (byte)((value & 0xff00) >> 8));
        instance[a2].store8(a2, (byte)(value & 0xff));
    }
}
