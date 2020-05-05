/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

/*
 * このクラスは一般的なCPUの挙動を制御するためのメソッドを定義する。
 * エミュレートする実CPUはこのクラスを継承し、各抽象メソッドを実装しなければならない。
 */
public abstract class CPU implements StateSavable {
    protected Computer computer = null;

    /**
     * CPUオブジェクトを構築する。
     *
     * @param computer このCPUを実行するコンピュータクラスのオブジェクト
     */
    public CPU(Computer computer) {
        this.computer = computer;
    }

    /**
     * CPUをリセットする。
     */
    public abstract void reset();

    /**
     * CPUをホールトする。
     */
    public abstract void halt();

    /**
     * CPUにNMIを入力する。
     */
    public abstract void nmi();

    /**
     * CPUにIRQを入力する。
     */
    public abstract void irq();

    /**
     * CPUを少なくとも指定のクロック数だけ実行する。
     * 命令実行中に指定したクロック数を超過する場合は、その命令の実行は完了させ、
     * 余分に実行したクロック数を返す。
     *
     * @param clocks
     * @return 余分に実行したクロック数
     */
    public abstract long execute(long clocks);
}
