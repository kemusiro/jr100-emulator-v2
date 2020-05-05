/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class RegisterB extends Register {
    private static RegisterB instance = null;

    private RegisterB() {
        super("B");
    }

    public static synchronized RegisterB getInstance() {
        if (instance == null) {
            instance = new RegisterB();
        }
        return instance;
    }
}
