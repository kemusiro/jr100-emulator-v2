/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;


/*
 * エミュレータアプリケーションのGUIとエミュレータ共通処理を提供する抽象クラス
 * このクラスが提供する機能は以下である。
 * <ul>
 * <li>プロパティのロード・セーブ処理
 * <li>ログの生成処理
 * <li>実行状態の保存・回復処理の呼び出し
 * <li>
 * </ul>
 * また以下のメソッドはこのクラスを継承したクラスで実装する。
 * <ul>
 * <li>メニューバーの作成
 * <li>ステータスバーの作成
 * <li>mainメソッド
 * </ul>
 * 個々のエミュレータ実行画面はこのクラスを継承し、いくつかのメソッドを実装する必要がある。
 *
 * @author Kenichi Miyata
 *
 */
@SuppressWarnings("serial")
public abstract class Application extends JFrame {
    public final static String PROPERTY_DATA_FOLDER = "system.data_folder";
    public final static String PROPERTY_SNAPSHOT_FOLDER = "system.snapshot_folder";
    public final static String PROPERTY_SNAPSHOT_EXTENSION = "system.snapshot_extension";
    public final static String PROPERTY_LOG_LEVEL = "system.log_level";

    private final static String DEFAULT_SNAPSHOT_EXTENSION = "snapshot";

    private static Properties properties = new Properties();  //  @jve:decl-index=0:
    private static String currentDirectory = null;
    private static Logger logger;
    private Computer computer = null;
    protected static Application app;

    public Application() {
    }

    public static Properties getProperties() {
        return properties;
    }

    public static boolean loadProperties(String file_name) {
        File f = new File(file_name);
        try {
            if (f.exists()) {
                // ファイルが存在する場合
                FileInputStream in = new FileInputStream(f);
                Application.getProperties().load(in);
                in.close();
            }
        }
        catch (IOException e) {
            getLogger().log(Level.SEVERE, "設定ファイル " + file_name + " の入出力に失敗しました。");
            return false;
        }
        return true;
    }

    public boolean saveProperties(String file_name) {
        File f = new File(file_name);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            getProperties().store(out, null);
            out.close();
        }
        catch (IOException ex) {
            getLogger().log(Level.SEVERE, "設定ファイル " + file_name + " の入出力に失敗しました。");
            return false;
        }
        return true;
    }


    public static String getPropertyOfDataFolder() {
        return Application.getProperties().getProperty(
            PROPERTY_DATA_FOLDER,
            System.getProperty("user.home"));
    }

    public static void setPropertyOfDataFolder(String path) {
        Application.getProperties().setProperty(PROPERTY_DATA_FOLDER, path);
        currentDirectory = path;
    }

    public static String getPropertyOfSnapshotFolder() {
        return Application.getProperties().getProperty(
            PROPERTY_SNAPSHOT_FOLDER,
            getPropertyOfDataFolder());
    }

    public static void setPropertyOfSnapshotFolder(String path) {
        Application.getProperties().setProperty(PROPERTY_SNAPSHOT_FOLDER, path);
    }

    public static String getPropertyOfSnapshotExtension() {
        return Application.getProperties().getProperty(
            PROPERTY_SNAPSHOT_EXTENSION,
            DEFAULT_SNAPSHOT_EXTENSION);
    }

    public static void setPropertyOfSnapshotExtension(String extension) {
        Application.getProperties().setProperty(PROPERTY_SNAPSHOT_EXTENSION, extension);
    }

    public static Level getLogLevel() {
        String m = Application.getProperties().getProperty(PROPERTY_LOG_LEVEL);
        if (m == null) {
            return Level.OFF;
        }
        else {
            Level level;
            try {
                level = Level.parse(Application.getProperties().getProperty(PROPERTY_LOG_LEVEL));
            }
            catch (IllegalArgumentException e) {
                level = Level.OFF;
            }
            return level;
        }
    }

    public static void setLogLevel(Level level) {
        Application.getProperties().setProperty(PROPERTY_LOG_LEVEL, level.toString());
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getCurrentDirectory() {
        if (currentDirectory == null) {
            currentDirectory = getPropertyOfDataFolder();
        }
        return currentDirectory;
    }

    public static void setCurrentDirectory(String path) {
        currentDirectory = path;
    }

    public static void initializeLogger(String log_name, String file_name) {
        try {
            logger = Logger.getLogger(log_name);
            logger.setLevel(getLogLevel());
            if (getLogLevel().equals(Level.OFF)) {
                logger.addHandler(
                    new StreamHandler(
                        // /dev/nullへの書き込み相当のクラスを実装する。
                        new OutputStream() {
                            @Override
                            public void write(int b) {
                            }
                            @Override
                            public void write(byte[] b) {
                            }
                            @Override
                            public void write(byte[] b, int off, int len) {
                            }
                        },
                        new SimpleFormatter()));
            }
            else {
                FileHandler fh = new FileHandler(file_name);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            }
        }
        catch (IOException e) {
            System.out.println("ログファイルの初期化に失敗しました。");
            return ;
        }
    }

    public void setComputer(Computer computer) {
        this.computer = computer;
    }

    public Computer getComputer() {
        return computer;
    }

    public abstract JMenuBar createMenuBar();

    public JComponent createMainPanel() {
        getComputer().getHardware().getDisplay().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                getComputer().getHardware().getKeyboard().keyPressed(e.getKeyCode());
            }
            public void keyReleased(KeyEvent e) {
                getComputer().getHardware().getKeyboard().keyReleased(e.getKeyCode());
            }
        });
        getComputer().getHardware().getDisplay().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                getComputer().getHardware().getDisplay().requestFocusInWindow();
            }
        });
        return getComputer().getHardware().getDisplay();
    }

    public abstract JPanel createStatusBar();

    public void createGui(String title, Image icon) {
        app.setTitle(title);
        app.setIconImage(icon);
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        app.setJMenuBar(app.createMenuBar());
        app.getContentPane().add(app.createMainPanel(), BorderLayout.CENTER);
        app.getContentPane().add(app.createStatusBar(), BorderLayout.SOUTH);

        app.getRootPane().setDoubleBuffered(true);

        app.pack();
        app.setVisible(true);
    }

    private void waitForStatus(int status) {
        try {
            int count = 0;
            while (computer.getRunningStatus() != status) {
                if (count > 10) {
                    throw new RuntimeException("エミュレータの状態が一時停止になりませんでした。");
                }
                Thread.sleep(1000);
                count++;
            }
        }
        catch (InterruptedException e) {
        }
    }

    /**
     * エミュレータの実行状態をファイルに保存する。
     * このメソッドを呼び出す前に、エミュレータの一時停止(Computer.pause())を実行しておかなければならない。
     * ただし一時停止待ちの処理はこのメソッド内で行うため、呼び出し元で待つ必要はない。
     *
     * @param set_name
     * @param file_name
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveState(String snapshot_name, String comment) throws FileNotFoundException, IOException {
        StringBuffer path = new StringBuffer();
        Date date = new Date();
        path.append(Application.getPropertyOfSnapshotFolder());
        path.append("\\");
        path.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date));
        path.append(".");
        path.append(getPropertyOfSnapshotExtension());

        waitForStatus(Computer.STATUS_PAUSED);
        StateSet s = new StateSet(snapshot_name, date, comment);
        s.saveState(computer);
        s.writeFile(path.toString());
    }

    public void saveState() throws FileNotFoundException, IOException {
        saveState("quick snapshot", "This is a quick snapshot");
    }

    public Vector<Hashtable<String, Object>> getSnapshotProperties() {
        File d = new File(Application.getPropertyOfSnapshotFolder());
        File[] files = d.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                int period_index = name.lastIndexOf('.');
                if (period_index < 0 || period_index > name.length() - 1) {
                    return false;
                }
                return name.substring(period_index + 1).compareToIgnoreCase(getPropertyOfSnapshotExtension()) == 0;
            }
        });
        if (files == null) {
            return null;
        }
        Vector<Hashtable<String, Object>> v = new Vector<Hashtable<String, Object>>();
        for (int i = 0; i < files.length; i++) {
            try {
                FileInputStream fis = new FileInputStream(files[i]);
                ObjectInputStream ois = new ObjectInputStream(fis);
                if (!((String)ois.readObject()).equals(StateSet.SIGNATURE)) {
                    ois.close();
                    throw new IOException("スナップショットのフォーマットが不正です。");
                }
                if (ois.readInt() != StateSet.VERSION) {
                    ois.close();
                    throw new IOException("スナップショットのバージョンが不正です。");
                }
                Hashtable<String, Object> p = new Hashtable<String, Object>();
                p.put("file", files[i]);
                p.put("name", (String)ois.readObject());
                p.put("createdDate", (Date)ois.readObject());
                p.put("comment", (String)ois.readObject());
                v.add(p);
                ois.close();
            }
            catch (FileNotFoundException e) {
                // 例外が発生した場合、このスナップショットは無視する。
            }
            catch (IOException e) {
                // 例外が発生した場合、このスナップショットは無視する。
            }
            catch (ClassNotFoundException e) {
                // 例外が発生した場合、このスナップショットは無視する。
            }
            catch (ClassCastException e) {
                // 例外が発生した場合、このスナップショットは無視する。
            }
        }
        return v;
    }

    public void loadState(String path_name) throws FileNotFoundException, IOException {
        waitForStatus(Computer.STATUS_PAUSED);
        StateSet s = new StateSet();
        s.readFile(path_name);
        s.loadState(computer);
    }

    public void loadState() throws FileNotFoundException, IOException {
        File d = new File(Application.getPropertyOfSnapshotFolder());
        File[] files = d.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                int period_index = name.lastIndexOf('.');
                if (period_index < 0 || period_index > name.length() - 1) {
                    return false;
                }
                return name.substring(period_index + 1).compareToIgnoreCase(getPropertyOfSnapshotExtension()) == 0;
            }
        });
        if (files != null && files.length > 0) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File file1, File file2) {
                    long mtime1 = file1.lastModified();
                    long mtime2 = file2.lastModified();
                    if (mtime1 < mtime2) {
                        return -1;
                    }
                    else if (mtime1 > mtime2) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            });
            loadState(files[files.length - 1].getAbsolutePath());
        }
        else {
            waitForStatus(Computer.STATUS_PAUSED);
        }
    }
}
