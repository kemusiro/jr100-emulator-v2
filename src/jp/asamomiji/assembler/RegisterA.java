/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class RegisterA extends Register {
    private static RegisterA instance = null;

    private RegisterA() {
        super("A");
    }

    public static synchronized RegisterA getInstance() {
        if (instance == null) {
            instance = new RegisterA();
        }
        return instance;
    }
}
