/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public abstract class Register {
    private String name;

    protected Register(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
