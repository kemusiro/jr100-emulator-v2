/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import java.awt.Frame;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import jp.asamomiji.emulator.AddressRegion;
import jp.asamomiji.emulator.Application;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.Program;
import jp.asamomiji.emulator.file.BasicTextFormatFile;
import jp.asamomiji.emulator.file.BinaryTextFormatFile;
import jp.asamomiji.emulator.file.DataFile;
import jp.asamomiji.emulator.file.ProgFormatFile;

public class FileOpenHooker {
    private Computer computer;
    private Frame owner;
    private JFileChooser chooser;

    public FileOpenHooker(Computer computer, Frame owner) {
        this.computer = computer;
        this.owner = owner;
        chooser = new JFileChooser(Application.getCurrentDirectory());
        chooser.setMultiSelectionEnabled(true);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("標準プログラムファイル (*.prg, *.txt, *.bas, *.dat)", "prg", "txt", "bas", "dat"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PROG形式(バイナリ)ファイル (*.prg)", "prg"));
        chooser.setApproveButtonText("開く(複数選択可)");
    }

    public void openDialog() {
        int selected = chooser.showOpenDialog(owner);
        Application.setCurrentDirectory(chooser.getCurrentDirectory().getAbsolutePath());
        DataFile df;
        File[] files = chooser.getSelectedFiles();
        File f = null;
        Program merged_program = new Program(computer.getHardware().getMemory());
        StringBuilder sb = new StringBuilder();
        if (selected == JFileChooser.APPROVE_OPTION) {
            for (int i = 0; i < files.length; i++) {
                f = files[i];

                if (DataFile.isProgFile(f)) {
                    df = new ProgFormatFile(f);
                }
                else if (DataFile.isBasicTextFile(f)) {
                    df = new BasicTextFormatFile(f);
                }
                else {
                    df = new BinaryTextFormatFile(f);
                }
                Program p = df.load_jr100(computer.getHardware().getMemory());
                if (df.getErrorStatus() != DataFile.STATUS_SUCCESS) {
                    JOptionPane.showMessageDialog(
                            chooser,
                            df.getErrorMessage(),
                            "エラー",
                            JOptionPane.ERROR_MESSAGE);
                    return ;
                }
                if (merged_program.getName().equals("") && !p.getName().equals("")) {
                    merged_program.setName(p.getName());
                }

                if (!p.getComment().equals("")) {
                    sb.delete(0, sb.length());
                    sb.append(merged_program.getComment());
                    if (!merged_program.getComment().equals("")) {
                        sb.append("\n");
                    }
                    sb.append(p.getComment());
                    merged_program.setComment(sb.toString());
                }

                if (merged_program.hasBasicArea() && p.hasBasicArea()) {
                    JOptionPane.showMessageDialog(
                            chooser,
                            "BASICプログラム領域が重複しています。",
                            "エラー",
                            JOptionPane.ERROR_MESSAGE);
                    return ;
                }
                else if (!merged_program.hasBasicArea() && p.hasBasicArea()) {
                    merged_program.setBasicArea(true);
                }

                for (AddressRegion r : p.getAllAddressRegions()) {
                    if (merged_program.getAllAddressRegions().size() == ProgFormatFile.PROG_MAX_BINARY_SECTIONS) {
                        break;
                    }
                    merged_program.addAddressRegion(r.clone());
                }

            }
            if (files.length == 1) {
                merged_program.setFile(files[0]);
            }
            computer.setProgram(merged_program);
        }
        else if (selected == JFileChooser.CANCEL_OPTION) {

        }
        else {
            Application.getLogger().log(Level.SEVERE, "FileChooserでエラーが発生しました。");
        }
    }
}
