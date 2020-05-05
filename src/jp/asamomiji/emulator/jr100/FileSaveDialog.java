/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jp.asamomiji.emulator.AddressRegion;
import jp.asamomiji.emulator.Application;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.Program;
import jp.asamomiji.emulator.file.BasicTextFormatFile;
import jp.asamomiji.emulator.file.BinaryTextFormatFile;
import jp.asamomiji.emulator.file.DataFile;
import jp.asamomiji.emulator.file.ProgFormatFile;

public class FileSaveDialog extends JDialog {
    private Computer computer;

    private final JPanel contentPanel = new JPanel();
    private JTextField textField_programName;
    private JTextArea textArea_programComment;
    private JTable table_addressRegions;
    private JTextField textField_filename;
    private JTextField textField_pathname;
    private JCheckBox checkBox_saveBasicArea;
    private JComboBox<FileFormatEntry> comboBox_fileFormat;
    private JLabel label_message = new JLabel();
    private final Action action_cancel = new SwingAction();
    private final Action action_chooseFile = new SwingAction_1();
    private final Action action_ok = new SwingAction_2();

    private final static FileFormatEntry FORMAT_PROG = new FileFormatEntry("prg", "PROG形式(*.prg)");
    private final static FileFormatEntry FORMAT_BASIC = new FileFormatEntry("bas", "BASICテキスト(*.bas)");
    private final static FileFormatEntry FORMAT_DUMP = new FileFormatEntry("dat", "16進ダンプ(*.dat)");
    private FileFormatEntry[] fileFormatList = {FORMAT_PROG, FORMAT_BASIC, FORMAT_DUMP};
    private ComboBoxModel<FileFormatEntry> fileFormatModel = new DefaultComboBoxModel<FileFormatEntry>(fileFormatList);

    static class FileFormatEntry implements Comparable<FileFormatEntry> {
        private String extension;
        private  String description;
        public FileFormatEntry(String e, String d) {
            this.extension = e;
            this.description = d;
        }
        public String getExtension() {
            return extension;
        }
        public String getDescription() {
            return description;
        }
        @Override
        public int compareTo(FileFormatEntry e) {
            return this.extension.compareTo(e.getExtension());
        }
    }

    /**
     * ファイル形式をJComboBoxで表示するためのレンダラー
     *
     */
    class FileFormatRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                setText(((FileFormatEntry)value).getDescription());
            }
            return label;
        }
    }

    /**
     * アドレス範囲を管理するためのテーブルモデル
     *
     */
    class AddressRegionTableModel extends AbstractTableModel {
        private ColumnContext[] column_array = {
                new ColumnContext("開始アドレス", Integer.class, true),
                new ColumnContext("終了アドレス", Integer.class, true),
                new ColumnContext("コメント", String.class, true),
                new ColumnContext("", String.class, true)
        };

        private ArrayList<AddressRegion> regions;

        public AddressRegionTableModel(ArrayList<AddressRegion> regions) {
            this.regions = regions;
        }

        public ArrayList<AddressRegion> getAllAddressRegions() {
            return regions;
        }

        private class ColumnContext {
            public final String columnName;
            public final Class<?> columnClass;
            public final boolean isEditable;
            protected ColumnContext(String columnName, Class<?> columnClass, boolean isEditable) {
                this.columnName = columnName;
                this.columnClass = columnClass;
                this.isEditable = isEditable;
            }
        }

        @Override
        public String getColumnName(int column) {
            return column_array[column].columnName;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column_array[column].columnClass;
        }

        @Override
        public int getColumnCount() {
            return column_array.length;
        }

        @Override
        public int getRowCount() {
            return regions.size();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return column_array[columnIndex].isEditable;
        }

        public void removeRow(int row) {
            regions.remove(row);
            fireTableRowsDeleted(row, row);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            AddressRegion r = regions.get(rowIndex);
            switch (columnIndex) {
            case 0:
                return r.getStartAddress();
            case 1:
                return r.getEndAddress();
            case 2:
                return r.getComment();
            default:
                return "";
            }
        }

        @Override
        public void setValueAt(Object obj, int model_row, int model_column) {
            AddressRegion r = regions.get(model_row);
            if (model_column == 0) {
                r.setStartAddress((Integer)obj);
                fireTableCellUpdated(model_row, model_column);
            }
            else if (model_column == 1) {
                r.setEndAddress((Integer)obj);
                fireTableCellUpdated(model_row, model_column);
            }
            else {
                String value = (String)obj;
                if (value.length() > ProgFormatFile.PROG_MAX_COMMENT_LENGTH) {
                    return ;
                }
                r.setComment(value);
                fireTableCellUpdated(model_row, model_column);
            }
        }

        public void addAddressRegion(int start, int end, String comment) {
            regions.add(new AddressRegion(start, end, comment));
        }

        public void addAddressRegion(AddressRegion r) {
            regions.add(r);
        }
    }

    /**
     * アドレス値を表示するためのレンダラークラス
     *
     */
    class AddressRenderer extends DefaultTableCellRenderer {
        public AddressRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer c =
                    (DefaultTableCellRenderer)super.getTableCellRendererComponent(
                            table, String.format("%04X", value), isSelected, hasFocus, row, column);
            c.setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }

    /**
     * アドレス値を編集するためのエディタークラス
     *
     */
    private class AddressEditor extends DefaultCellEditor {
        private int address;

        public AddressEditor() {
            super(new JTextField());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            ((JComponent)getComponent()).setBorder(BorderFactory.createLineBorder(Color.BLUE));
            JTextField c = (JTextField)super.getTableCellEditorComponent(table, String.format("%04X", value), isSelected, row, column);
            c.setHorizontalAlignment(SwingConstants.CENTER);
            c.setFont(table.getFont());
            return c;
        }

        @Override
        public boolean stopCellEditing() {
            int value = 0;
            String s = (String)super.getCellEditorValue();
            if (s != null && !s.isEmpty()) {
                try {
                    value = Integer.parseInt(s, 16);
                    if (value < 0 || value > 0xffff) {
                        ((JComponent)getComponent()).setBorder(BorderFactory.createLineBorder(Color.RED));
                        label_message.setText("数値が範囲外です。");
                        return false;
                    }
                }
                catch (NumberFormatException e) {
                    label_message.setText("数値のフォーマットが不正です。");
                    ((JComponent)getComponent()).setBorder(BorderFactory.createLineBorder(Color.RED));
                    return false;
                }
            }
            else {
                label_message.setText("数値が空です。");
                ((JComponent)getComponent()).setBorder(BorderFactory.createLineBorder(Color.RED));
                return false;
            }
            this.address = value;
            label_message.setText("");
            return super.stopCellEditing();
        }

        @Override
        public Object getCellEditorValue() {
            return address;
        }
    }

    /**
     * コメントを表示するためのレンダラー
     *
     */
    class CommentRenderer extends JTextArea implements TableCellRenderer {
        private final List<List<Integer>> rowAndCellHeights = new ArrayList<>();
        private final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        public CommentRenderer() {
            super();
            setLineWrap(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            }
            else {
                setBorder(noFocusBorder);

            }
            setText(Objects.toString(value, ""));
            adjustRowHeight(table, row, column);
            return this;
        }

        private void adjustRowHeight(JTable table, int row, int column) {
            setBounds(table.getCellRect(row, column, false));

            int preferredHeight = getPreferredSize().height;
            // 行が追加された場合に新しい行の配列を追加する。
            while (rowAndCellHeights.size() <= row) {
                rowAndCellHeights.add(new ArrayList<>(column));
            }
            // 列が追加された場合に新しい列の高さを仮にゼロと設定する。
            List<Integer> list = rowAndCellHeights.get(row);
            while (list.size() <= column) {
                list.add(0);
            }

            list.set(column, preferredHeight);
            int max = list.stream().max(Integer::compare).get();
            if (table.getRowHeight(row) != max) {
                table.setRowHeight(row, max);
            }
        }
    }

    /**
     * コメントを編集するためのエディター
     *
     */
    class CommentEditor extends JTextArea implements TableCellEditor {
        private static final String KEY_STOP = "Stop-Cell-Editing";
        private static final String KEY_ENTER = "Insert-Enter";
        protected transient ChangeEvent changeEvent;
        private final JScrollPane scroll;

        protected CommentEditor() {
            super();
            scroll = new JScrollPane(this) {
                @Override
                public boolean isValidateRoot() {
                    return true;
                }
            };
            scroll.setBorder(BorderFactory.createEmptyBorder());

            setLineWrap(true);

            KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
            KeyStroke enter_shift = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK);
            getInputMap(JComponent.WHEN_FOCUSED).put(enter, KEY_STOP);
            getInputMap(JComponent.WHEN_FOCUSED).put(enter_shift, KEY_ENTER);
            getActionMap().put(KEY_STOP, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                }
            });
            getActionMap().put(KEY_ENTER,  new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insert("\n", getCaretPosition());

                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return getText();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            setFont(table.getFont());
            setBorder(BorderFactory.createLineBorder(Color.BLUE));
            setText(Objects.toString(value, ""));
            table.setRowHeight(row, Math.max(48, table.getCellRect(row, column, true).height));
            // invokeLaterから呼び出さないと、キーイベントで編集を開始した場合にtext areaが更新されない。
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setCaretPosition(getText().length());
                    requestFocusInWindow();
                }
            });
            return scroll;
        }

        @Override
        public boolean isCellEditable(final EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 2;
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (e instanceof KeyEvent) {
                        KeyEvent ke = (KeyEvent) e;
                        char kc = ke.getKeyChar();
                        // 制御キーではなく文字のキーが押下されたかを判定する。
                        if (Character.isUnicodeIdentifierStart(kc)) {
                            setText(getText() + kc);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject e) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            try {
                if (getText().getBytes("UTF-8").length > ProgFormatFile.PROG_MAX_COMMENT_LENGTH) {
                    label_message.setText("文字列が長すぎます。");
                    setBorder(BorderFactory.createLineBorder(Color.RED));
                    return false;
                }
            }
            catch (UnsupportedEncodingException e) {
                label_message.setText("不正な文字が含まれています。");
                setBorder(BorderFactory.createLineBorder(Color.RED));
                return false;
            }
            label_message.setText("");
            fireEditingStopped();
            return true;
        }

        @Override
        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listenerList.add(CellEditorListener.class, l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listenerList.remove(CellEditorListener.class, l);
        }

        public CellEditorListener[] getCellEditorListeners() {
            return listenerList.getListeners(CellEditorListener.class);
        }

        protected void fireEditingStopped() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    if (Objects.isNull(changeEvent)) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
                }
            }
        }

        protected void fireEditingCanceled() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    if (Objects.isNull(changeEvent)) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
                }
            }
        }
    }

    class DeleteButton extends JButton {
        @Override
        public void updateUI() {
            super.updateUI();
            setBorder(BorderFactory.createEmptyBorder());
            setFocusable(false);
            setRolloverEnabled(false);
            setText("X");
        }
    }

    /**
     * ボタンを表示するためのレンダラー
     *
     */
    class DeleteButtonRenderer extends DeleteButton implements TableCellRenderer {
        private final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        public DeleteButtonRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (hasFocus) {
                setBackground(table.getSelectionBackground());
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            }
            else {
                setBackground(table.getBackground());
                setBorder(noFocusBorder);

            }
            return this;
        }
    }

    /**
     * ボタンのためのエディター
     */
    class DeleteButtonEditor extends DeleteButton implements TableCellEditor {
        private transient ActionListener listener;
        @Override
        public void updateUI() {
            removeActionListener(listener);
            super.updateUI();
            listener = e -> {
                Object o = SwingUtilities.getAncestorOfClass(JTable.class, this);
                if (o instanceof JTable) {
                    Object[] options = {"はい", "いいえ" };
                    int answer = JOptionPane.showOptionDialog(
                            FileSaveDialog.this,
                            "削除しても良いですか?",
                            "削除確認",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options, options[1]);
                    if (answer == JOptionPane.NO_OPTION) {
                        return ;
                    }
                    JTable table = (JTable) o;
                    int row = table.convertRowIndexToModel(table.getEditingRow());
                    fireEditingStopped();
                    AddressRegionTableModel artm = (AddressRegionTableModel)table.getModel();
                    artm.removeRow(row);
                }
            };
            addActionListener(listener);
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return this;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        @Override
        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listenerList.add(CellEditorListener.class, l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listenerList.remove(CellEditorListener.class, l);
        }

        public CellEditorListener[] getCellEditorListeners() {
            return listenerList.getListeners(CellEditorListener.class);
        }

        protected void fireEditingStopped() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    if (Objects.isNull(changeEvent)) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
                }
            }
        }

        protected void fireEditingCanceled() {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    if (Objects.isNull(changeEvent)) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
                }
            }
        }
    }

    /**
     * Create the dialog.
     */
    public FileSaveDialog(Computer computer, Frame owner) {
        super(owner);
        this.computer = computer;
        Program p = computer.getProgram();

        setTitle("保存");
        setBounds(100, 100, 599, 546);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel label_programName = new JLabel("プログラム名");

        textField_programName = new JTextField();
        textField_programName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!checkText(textField_programName.getText(), ProgFormatFile.PROG_MAX_PROGRAM_NAME_LENGTH, "プログラム名")) {
                    textField_programName.requestFocusInWindow();
                }
            }

        });
        textField_programName.setColumns(10);
        if (p != null && p.getName() != null) {
            textField_programName.setText(p.getName());
        }

        JLabel label_programComment = new JLabel("コメント");

        JScrollPane scrollPane_programComment = new JScrollPane();

        JPanel panel_saveArea = new JPanel();
        panel_saveArea.setBorder(new TitledBorder(null, "保存する領域", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel label_filename = new JLabel("ファイル名");

        JLabel label_pathname = new JLabel("パス名");

        textField_filename = new JTextField();
        textField_filename.setColumns(10);

        textField_pathname = new JTextField();
        textField_pathname.setColumns(10);

        if (p != null && p.getFile() != null) {
            textField_filename.setText(p.getFile().getName());
            textField_pathname.setText(p.getFile().getParent());
        }
        else {
            textField_filename.setText("");
            textField_pathname.setText(Application.getCurrentDirectory());
        }

        JButton button_chooseFile = new JButton("ファイル選択");
        button_chooseFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        button_chooseFile.setAction(action_chooseFile);

        JLabel label_fileFormat = new JLabel("ファイル形式");

        comboBox_fileFormat = new JComboBox<FileFormatEntry>(fileFormatModel);
        comboBox_fileFormat.setRenderer(new FileFormatRenderer());
        comboBox_fileFormat.setSelectedItem(FORMAT_PROG);

        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(label_programComment)
                                .addComponent(label_programName))
                            .addGap(36)
                            .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(scrollPane_programComment, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                                .addComponent(textField_programName, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(label_pathname)
                                .addComponent(label_filename)
                                .addComponent(label_fileFormat))
                            .addGap(31)
                            .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(textField_filename, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_contentPanel.createSequentialGroup()
                                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPanel.createSequentialGroup()
                                            .addComponent(comboBox_fileFormat, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGap(52))
                                        .addGroup(gl_contentPanel.createSequentialGroup()
                                            .addComponent(textField_pathname, GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                                            .addPreferredGap(ComponentPlacement.RELATED)))
                                    .addComponent(button_chooseFile))))
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addComponent(panel_saveArea, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(3)))
                    .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(textField_programName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_programName))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(label_programComment)
                        .addComponent(scrollPane_programComment, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel_saveArea, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_filename)
                        .addComponent(textField_filename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_fileFormat)
                        .addComponent(comboBox_fileFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_pathname)
                        .addComponent(textField_pathname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_chooseFile))
                    .addContainerGap())
        );

        checkBox_saveBasicArea = new JCheckBox("BASICプログラム領域");
        if (p == null) {
            checkBox_saveBasicArea.setSelected(true);
        }
        else {
            checkBox_saveBasicArea.setSelected(p.hasBasicArea());
        }

        JButton button_addRegion = new JButton("追加");
        button_addRegion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddressRegionTableModel artm = (AddressRegionTableModel)table_addressRegions.getModel();
                if (artm.getRowCount() >= ProgFormatFile.PROG_MAX_BINARY_SECTIONS) {
                    JOptionPane.showMessageDialog(
                            FileSaveDialog.this,
                            "これ以上アドレス領域を追加できません。",
                            "エラー",
                            JOptionPane.ERROR_MESSAGE);
                    return ;
                }
                artm.addAddressRegion(0, 0, "");
                int new_row = table_addressRegions.convertRowIndexToView(artm.getRowCount() - 1);
                artm.fireTableRowsInserted(0, new_row);
                table_addressRegions.editCellAt(new_row, 0);
                table_addressRegions.setRowSelectionInterval(new_row, new_row);
                table_addressRegions.setColumnSelectionInterval(0, 0);
                AddressEditor editor = (AddressEditor)table_addressRegions.getCellEditor(new_row, 0);
                editor.getComponent().requestFocusInWindow();
            }
        });

        JScrollPane scrollPane_addressRegions = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel label_machineLanguageArea = new JLabel("マシン語領域");
        GroupLayout gl_panel_saveArea = new GroupLayout(panel_saveArea);
        gl_panel_saveArea.setHorizontalGroup(
            gl_panel_saveArea.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel_saveArea.createSequentialGroup()
                    .addGroup(gl_panel_saveArea.createParallelGroup(Alignment.LEADING)
                        .addComponent(checkBox_saveBasicArea)
                        .addGroup(gl_panel_saveArea.createSequentialGroup()
                            .addComponent(label_machineLanguageArea)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(button_addRegion))
                        .addGroup(gl_panel_saveArea.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(label_message, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE))
                        .addGroup(gl_panel_saveArea.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane_addressRegions, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        gl_panel_saveArea.setVerticalGroup(
            gl_panel_saveArea.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_saveArea.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(checkBox_saveBasicArea)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_saveArea.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_machineLanguageArea)
                        .addComponent(button_addRegion))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane_addressRegions, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(label_message)
                    .addContainerGap(35, Short.MAX_VALUE))
        );

        {
            AddressRegionTableModel dm = new AddressRegionTableModel(new ArrayList<AddressRegion>());
            if (p != null) {
                p.getAllAddressRegions().forEach(r -> dm.addAddressRegion(r.clone()));
            }

            table_addressRegions = new JTable(dm) {
                @Override
                public boolean editCellAt(int row, int column, EventObject e) {
                    boolean r = super.editCellAt(row, column, e);
                    if (r) {
                        if (e instanceof KeyEvent) {
                            KeyEvent ke = (KeyEvent) e;
                            char ch = ke.getKeyChar();
                            if (!Character.isISOControl(ch)) { //普通の文字の場合
                                if (super.editorComp instanceof JTextField) {
                                    JTextField tf = (JTextField) super.editorComp;
                                    tf.selectAll(); //既存文字列を全選択
                                }
                            }
                        }
                    }
                    return r;
                }
            };
            table_addressRegions.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            table_addressRegions.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            table_addressRegions.setAutoCreateRowSorter(true);
            table_addressRegions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table_addressRegions.getTableHeader().setReorderingAllowed(false);
            table_addressRegions.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            table_addressRegions.setSurrendersFocusOnKeystroke(true);

            TableColumnModel column_model = (DefaultTableColumnModel)table_addressRegions.getColumnModel();
            AddressRenderer ar = new AddressRenderer();
            CommentRenderer cr = new CommentRenderer();
            DeleteButtonRenderer dr = new DeleteButtonRenderer();
            AddressEditor ae = new AddressEditor();
            CommentEditor ce = new CommentEditor();
            DeleteButtonEditor de = new DeleteButtonEditor();

            TableColumn column_start = column_model.getColumn(0);
            column_start.setPreferredWidth(120);
            column_start.setResizable(false);
            column_start.setCellRenderer(ar);
            column_start.setCellEditor(ae);

            TableColumn column_end = column_model.getColumn(1);
            column_end.setPreferredWidth(120);
            column_end.setResizable(false);
            column_end.setCellRenderer(ar);
            column_end.setCellEditor(ae);

            TableColumn column_comment = column_model.getColumn(2);
            column_comment.setPreferredWidth(500);
            column_comment.setResizable(true);
            column_comment.setCellRenderer(cr);
            column_comment.setCellEditor(ce);

            TableColumn column_button = column_model.getColumn(3);
            column_button.setCellRenderer(dr);
            column_button.setCellEditor(de);
            column_button.setMinWidth(20);
            column_button.setMaxWidth(20);
            // column_button.setResizable(false);
        }

        scrollPane_addressRegions.setViewportView(table_addressRegions);
        panel_saveArea.setLayout(gl_panel_saveArea);

        textArea_programComment = new JTextArea();
        textArea_programComment.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!checkText(textArea_programComment.getText(), ProgFormatFile.PROG_MAX_COMMENT_LENGTH, "プログラムコメント")) {
                    textArea_programComment.requestFocusInWindow();
                }
            }

        });

        if (p != null && p.getComment() != null) {
            textArea_programComment.setText(p.getComment());
        }
        scrollPane_programComment.setViewportView(textArea_programComment);
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("保存");
                okButton.setAction(action_ok);
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("キャンセル");
                cancelButton.setAction(action_cancel);
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    private class SwingAction extends AbstractAction {
        public SwingAction() {
            putValue(NAME, "キャンセル");
            putValue(SHORT_DESCRIPTION, "設定を無効にして閉じる。");
        }
        public void actionPerformed(ActionEvent e) {
            FileSaveDialog.this.dispose();
        }
    }
    private class SwingAction_1 extends AbstractAction {
        public SwingAction_1() {
            putValue(NAME, "保存場所の選択");
            putValue(SHORT_DESCRIPTION, "プログラムを保存するファイルまたはフォルダを選択する。");
        }
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(Application.getCurrentDirectory());
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int selected = chooser.showDialog(FileSaveDialog.this, "選択");
            Application.setCurrentDirectory(chooser.getCurrentDirectory().getAbsolutePath());

            if (selected == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file.isDirectory()) {
                    textField_pathname.setText(file.getAbsolutePath());
                }
                else {
                    textField_filename.setText(file.getName());
                    textField_pathname.setText(file.getParent());
                }
            }
        }
    }

    private boolean checkText(String text, int maxlen, String name) {
        try {
            int len = text.getBytes("UTF-8").length;
            if (len > maxlen) {
                JOptionPane.showMessageDialog(
                        FileSaveDialog.this,
                        name + "の長さ(" + len + "バイト)が最大値(" + maxlen + "バイト)を超えています。",
                        "エラー",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        catch (UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(
                    FileSaveDialog.this,
                    name + "の文字コードが不正です。",
                    "エラー",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean checkParameters() {
        if (textField_filename.getText().equals("")) {
            JOptionPane.showMessageDialog(
                    FileSaveDialog.this,
                    "ファイル名を指定してください。",
                    "エラー",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        AddressRegionTableModel dm = (AddressRegionTableModel)table_addressRegions.getModel();
        if (dm.getRowCount() > 0) {
            for (AddressRegion r : dm.getAllAddressRegions()) {
                if (r.getStartAddress() > r.getEndAddress()) {
                    JOptionPane.showMessageDialog(
                            FileSaveDialog.this,
                            "アドレス範囲が不正です。" + r.toString(),
                            "エラー",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (!checkText(r.getComment(), ProgFormatFile.PROG_MAX_COMMENT_LENGTH, "アドレス範囲のコメント")) {
                    return false;
                }
            }
        }

        return true;
    }

    private class SwingAction_2 extends AbstractAction {
        public SwingAction_2() {
            putValue(NAME, "保存");
            putValue(SHORT_DESCRIPTION, "設定をファイルに保存する。");
        }
        public void actionPerformed(ActionEvent e) {
            if (!checkParameters()) {
                return ;
            }

            // Programオブジェクトの作成
            Program p = computer.getProgram();
            if (p == null) {
                p = new Program(computer.getHardware().getMemory());
            }
            String name = textField_programName.getText();
            if (name != null && !name.equals("")) {
                p.setName(textField_programName.getText());
            }
            String comment = textArea_programComment.getText();
            if (comment != null && !comment.equals("")) {
                p.setComment(comment);
            }
            p.setBasicArea(checkBox_saveBasicArea.isSelected());

            AddressRegionTableModel dm = (AddressRegionTableModel)table_addressRegions.getModel();
            ArrayList<AddressRegion> regions = p.getAllAddressRegions();
            regions.clear();
            dm.getAllAddressRegions().forEach(r -> regions.add(r.clone()));

            /*
             * 保存するファイル名の解決
             * 指定されたファイルの拡張子が、指定された保存形式の標準拡張子(.prg, .bas, .dat)と
             * 一致する場合は、指定されたファイル名をそのまま使用する。
             * 指定されたファイルの拡張子が、指定された保存形式の標準拡張子と一致しない場合は、
             * 指定されたファイル名に標準拡張子を付与する。
             * 指定されたファイルに拡張子が含まれていない場合は、指定された保存形式の標準拡張子を
             * 付加する。
             *
             */
            StringBuilder path_name = new StringBuilder();
            path_name.append(textField_pathname.getText());
            path_name.append(File.separator);
            path_name.append(textField_filename.getText());
            String ext = DataFile.getExtension(textField_filename.getText());
            FileFormatEntry selected_format = (FileFormatEntry)comboBox_fileFormat.getSelectedItem();
            if (ext == null) {
                path_name.append(".");
                path_name.append(selected_format.getExtension());
            }
            else if (!ext.equals(selected_format.getExtension())) {
                path_name.append(".");
                path_name.append(selected_format.getExtension());
            }

            File file = new File(path_name.toString());
            if (file.exists()) {
                int selected =
                    JOptionPane.showConfirmDialog(
                        FileSaveDialog.this,
                        "ファイルが存在します。上書きしてもよいですか?",
                        "確認",
                        JOptionPane.YES_NO_OPTION);
                if (selected == JOptionPane.NO_OPTION) {
                    return ;
                }
                else {
                    Path target_file = Paths.get(path_name.toString());
                    Path backup_file = Paths.get(path_name.toString() + "~");
                    try {
                        Files.move(target_file, backup_file, StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch (IOException exc) {
                        JOptionPane.showMessageDialog(
                                FileSaveDialog.this,
                                exc.getMessage(),
                                "エラー",
                                JOptionPane.ERROR_MESSAGE);
                        return ;
                    }
                }
            }
            DataFile data_file = null;
            if (selected_format.compareTo(FORMAT_PROG) == 0) {
                data_file = new ProgFormatFile(file);
            }
            else if (selected_format.compareTo(FORMAT_BASIC) == 0) {
                data_file = new BasicTextFormatFile(file);
            }
            else if (selected_format.compareTo(FORMAT_DUMP) == 0) {
                data_file = new BinaryTextFormatFile(file);
            }
            else {
                throw new AssertionError("未定義のフォーマットで保存しようとした。");
            }
            data_file.save_jr100(p, 2);
            if (data_file.getErrorStatus() != DataFile.STATUS_SUCCESS) {
                JOptionPane.showMessageDialog(
                        FileSaveDialog.this,
                        data_file.getErrorMessage(),
                        "エラー",
                        JOptionPane.ERROR_MESSAGE);
                return ;
            }
            p.setFile(file);
            computer.setProgram(p);
            FileSaveDialog.this.dispose();
        }
    }
}
