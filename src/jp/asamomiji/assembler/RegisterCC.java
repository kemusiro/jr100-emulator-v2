/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.assembler;

public class RegisterCC extends Register {
    private static RegisterCC instance = null;

    private RegisterCC() {
        super("C");
    }

    public static synchronized RegisterCC getInstance() {
        if (instance == null) {
            instance = new RegisterCC();
        }
        return instance;
    }
}
