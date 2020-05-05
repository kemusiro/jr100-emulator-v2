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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Hashtable;

public class StateSet {
    public final static String SIGNATURE = "SPST";
    public final static int VERSION = 1;

    private String name;
    private Date createdDate;
    private String comment;
    private Hashtable<String, Object> valueTable = new Hashtable<String, Object>();

    public StateSet(String name, Date date, String comment) {
        this.name = name;
        this.createdDate = date;
        this.comment = comment;
    }

    public StateSet() {
        this(null, null, null);
    }

    public void set(String key, Object value) {
        valueTable.put(key, value);
    }

    public Object get(String key, Object default_value) {
        Object value = valueTable.get(key);
        return value == null ? default_value : value;
    }

    public Object get(String key) {
        return get(key, null);
    }

    public void saveState(Computer computer) {
        computer.saveState(this);
    }

    public void loadState(Computer computer) {
        computer.loadState(this);
    }

    public void writeFile(String file_name) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(file_name));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(SIGNATURE);
        oos.writeInt(VERSION);
        oos.writeObject(name);
        oos.writeObject(createdDate);
        oos.writeObject(comment);
        oos.writeObject(valueTable);
        oos.close();
    }

    @SuppressWarnings("unchecked")
    public void readFile(String file_name) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(new File(file_name));
        ObjectInputStream ois = new ObjectInputStream(fis);
        try {
            if (!((String)ois.readObject()).equals(SIGNATURE)) {
            	ois.close();
                throw new IOException("スナップショットのフォーマットが不正です。");
            }
            if (ois.readInt() != VERSION) {
            	ois.close();
                throw new IOException("スナップショットのバージョンが不正です。");
            }
            this.name = (String)ois.readObject();
            this.createdDate = (Date)ois.readObject();
            this.comment = (String)ois.readObject();
            this.valueTable = (Hashtable<String, Object>)ois.readObject();
        }
        catch (ClassNotFoundException e) {
        }
        ois.close();
    }
}
