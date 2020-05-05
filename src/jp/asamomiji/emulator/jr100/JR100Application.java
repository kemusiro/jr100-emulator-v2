/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator.jr100;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import jp.asamomiji.emulator.Application;
import jp.asamomiji.emulator.Computer;

/*
 * JR-100エミュレータのGUIを提供する。
 */
@SuppressWarnings("serial")
public final class JR100Application extends Application {
    public final static String REVISION_STRING = "2.4.0";
    public final static String COPYRIGHT_STRING = "Copyright (C) 2005-2017 Kenichi Miyata";
    public final static String DEFAULT_ROM_FILE_NAME = "jr100rom.prg";
    public final static String LOGFILE_NAME = "jr100v2.log";
    public final static String PROPERTIES_FILENAME = "jr100v2.ini";  //  @jve:decl-index=0:
    public final static String PROPERTY_USE_D3D = "system.use_d3d";

    private static String romfilename;

    private JMenuBar MB_menubar = null;
    private JMenu M_file = null;
    private JMenuItem MI_open = null;
    private JMenuItem MI_save = null;
    private JMenuItem MI_exit = null;
    private JMenu M_tool = null;
    private JMenuItem MI_disassemble = null;
    private JCheckBoxMenuItem MI_pause = null;
    private JMenuItem MI_reset = null;
    private JMenuItem MI_power = null;
    private JRadioButtonMenuItem MI_powerOff = null;
    private JRadioButtonMenuItem MI_powerOn = null;
    private JMenu M_snapshot = null;
    private JMenuItem MI_takeSnapshot = null;
    private JMenuItem MI_revertSnapshot = null;
    private JMenu M_configuration = null;
    private JMenuItem MI_cpu = null;
    private JMenu M_display = null;
    private JMenu M_displayColor = null;
    private JRadioButtonMenuItem MI_monochrome = null;
    private JRadioButtonMenuItem MI_green = null;
    private JRadioButtonMenuItem MI_orange = null;
    private JRadioButtonMenuItem MI_color8 = null;
    private JMenu M_displayScaling = null;
    private JRadioButtonMenuItem MI_x1 = null;
    private JRadioButtonMenuItem MI_x2 = null;
    private JMenuItem MI_sound = null;
    private JMenuItem MI_inputDevice = null;
    private JMenuItem MI_property = null;
    private JMenuItem MI_saveConfiguration = null;
    private JMenu M_help = null;
    private JMenuItem MI_helpTOC = null;
    private JMenuItem MI_about = null;
    private JPanel P_statusBar = null;
    private JButton BT_powerOff = null;
    private JButton BT_pause = null;
    private JButton BT_resume = null;
    private JButton BT_takeSnapshot = null;
    private JButton BT_revertSnapshot = null;

    public static boolean getPropertyOfD3dUsed() {
        String s = Application.getProperties().getProperty(PROPERTY_USE_D3D);
        if (s == null) {
            s = System.getProperty("sun.java2d.d3d");
        }
        if (s == null) {
            return false;
        }
        else {
            return Boolean.parseBoolean(s);
        }
    }

    public static void setPropertyOfD3dUsed(boolean value) {
        Application.getProperties().setProperty(PROPERTY_USE_D3D, Boolean.toString(value));
    }

    public JR100Application() {
    }

    public static void start() {
        try {
            // 初期化
            app = new JR100Application();
            app.setComputer(new JR100(app, romfilename));

            app.createGui(
                "JR-100エミュレータ",
                Toolkit.getDefaultToolkit().getImage(app.getClass().getResource("/jr100emuicon.png")));

            new Thread(app.getComputer()).start();
        }
        catch (Throwable e) {
            getLogger().log(Level.SEVERE, "実行時エラー", e);
            System.exit(0);
        }
    }

    @Override
    public JMenuBar createMenuBar() {
        if (MB_menubar == null) {
            MB_menubar = new JMenuBar();
            MB_menubar.add(getFileMenu());
            MB_menubar.add(getToolMenu());
            MB_menubar.add(getConfigurationMenu());
            MB_menubar.add(getHelpMenu());
        }
        return MB_menubar;
    }

    private JMenu getFileMenu() {
        if (M_file == null) {
            M_file = new JMenu("ファイル");
            M_file.add(getOpenMenuItem());
            M_file.add(getSaveMenuItem());
            M_file.add(getExitMenuItem());
        }
        return M_file;
    }

    private JMenuItem getOpenMenuItem() {
        if (MI_open == null) {
            MI_open = new JMenuItem("開く");
            MI_open.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    FileOpenHooker f = new FileOpenHooker(getComputer(), JR100Application.this);
                    f.openDialog();
                }
            });
        }
        return MI_open;
    }

    private JMenuItem getSaveMenuItem() {
        if (MI_save == null) {
            MI_save = new JMenuItem("保存");
            MI_save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    FileSaveDialog d = new FileSaveDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                    // FileExportDialog d = new FileExportDialog(getComputer(), JR100Application.this);
                    // d.setVisible(true);
                }
            });
        }
        return MI_save ;
    }

    private JMenuItem getExitMenuItem() {
        if (MI_exit == null) {
            MI_exit = new JMenuItem("終了");
            MI_exit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
        return MI_exit;
    }

    private JMenu getToolMenu() {
        if (M_tool == null) {
            M_tool = new JMenu("ツール");
            M_tool.add(getDisassembleMenuItem());
            M_tool.addSeparator();
            M_tool.add(getPauseMenuItem());
            M_tool.add(getResetMenuItem());
            M_tool.add(getPowerMenu());
            M_tool.add(getSnapshotMenu());
        }
        return M_tool;
    }

    private JMenuItem getDisassembleMenuItem() {
        if (MI_disassemble == null) {
            MI_disassemble = new JMenuItem("逆アセンブル");
            MI_disassemble.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DisassembleDialog d = new DisassembleDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_disassemble;
    }

    private void changePowerMenuComponents(int new_status) {
        if (new_status == Computer.STATUS_PAUSED) {
            MI_powerOn.setSelected(true);
            MI_powerOff.setSelected(false);
            MI_pause.setSelected(true);
            MI_pause.setEnabled(true);
            MI_reset.setEnabled(true);
            BT_powerOff.setEnabled(true);
            BT_pause.setEnabled(false);
            BT_resume.setEnabled(true);
        }
        else if (new_status == Computer.STATUS_STOPPED) {
            MI_powerOn.setSelected(false);
            MI_powerOff.setSelected(true);
            MI_pause.setSelected(false);
            MI_pause.setEnabled(false);
            MI_reset.setEnabled(false);
            BT_powerOff.setEnabled(false);
            BT_pause.setEnabled(false);
            BT_resume.setEnabled(true);
        }
        else if (new_status == Computer.STATUS_RUNNING) {
            MI_powerOn.setSelected(true);
            MI_powerOff.setSelected(false);
            MI_pause.setSelected(false);
            MI_pause.setEnabled(true);
            MI_reset.setEnabled(true);
            BT_powerOff.setEnabled(true);
            BT_pause.setEnabled(true);
            BT_resume.setEnabled(false);
        }
    }

    private JCheckBoxMenuItem getPauseMenuItem() {
        if (MI_pause == null) {
            MI_pause = new JCheckBoxMenuItem("一時停止");
            MI_pause.setSelected(getComputer().getRunningStatus() == Computer.STATUS_PAUSED);
            MI_pause.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (MI_pause.isSelected()) {
                        getComputer().pause();
                        changePowerMenuComponents(Computer.STATUS_PAUSED);
                    }
                    else {
                        getComputer().resume();
                        changePowerMenuComponents(Computer.STATUS_RUNNING);
                    }
                }
            });
        }
        return MI_pause;
    }

    private JMenuItem getResetMenuItem() {
        if (MI_reset == null) {
            MI_reset = new JMenuItem("リセット");
            MI_reset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().reset();
                    changePowerMenuComponents(Computer.STATUS_RUNNING);
                }
            });
        }
        return MI_reset;
    }

    private JMenuItem getPowerMenu() {
        if (MI_power == null) {
            MI_power = (JMenuItem)new JMenu("電源");
            ((JMenu)MI_power).add(getPowerOnMenuItem());
            ((JMenu)MI_power).add(getPowerOffMenuItem());
            ButtonGroup g = new ButtonGroup();
            g.add(MI_powerOn);
            g.add(MI_powerOff);
            if (getComputer().getRunningStatus() == Computer.STATUS_RUNNING ||
                    getComputer().getRunningStatus() == Computer.STATUS_PAUSED) {
                MI_powerOn.setSelected(true);
            }
            else {
                MI_powerOff.setSelected(true);
            }
        }
        return MI_power;
    }

    private JRadioButtonMenuItem getPowerOnMenuItem() {
        if (MI_powerOn == null) {
            MI_powerOn = new JRadioButtonMenuItem("On");
            MI_powerOn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getComputer().getRunningStatus() == Computer.STATUS_STOPPED) {
                        synchronized(getComputer()) {
                            getComputer().notify();
                        }
                        changePowerMenuComponents(Computer.STATUS_RUNNING);
                    }
                }
            });
        }
        return MI_powerOn;
    }

    private JRadioButtonMenuItem getPowerOffMenuItem() {
        if (MI_powerOff == null) {
            MI_powerOff = new JRadioButtonMenuItem("Off");
            MI_powerOff.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getComputer().getRunningStatus() != Computer.STATUS_STOPPED) {
                        getComputer().powerOff();
                    }
                    changePowerMenuComponents(Computer.STATUS_STOPPED);
                }
            });
        }
        return MI_powerOff ;
    }

    private JMenu getSnapshotMenu() {
        if (M_snapshot == null) {
            M_snapshot = new JMenu("スナップショット");
            M_snapshot.add(getTakeSnapshotMenuItem());
            M_snapshot.add(getRevertSnapshotMenuItem());
        }
        return M_snapshot;
    }

    private JMenuItem getTakeSnapshotMenuItem() {
        if (MI_takeSnapshot == null) {
            MI_takeSnapshot = new JMenuItem("採取");
            MI_takeSnapshot.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    TakeSnapshotDialog d = new TakeSnapshotDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_takeSnapshot;
    }

    private JMenuItem getRevertSnapshotMenuItem() {
        if (MI_revertSnapshot == null) {
            MI_revertSnapshot = new JMenuItem("復元");
            MI_revertSnapshot.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    RevertSnapshotDialog d = new RevertSnapshotDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_revertSnapshot;
    }

    private JMenu getConfigurationMenu() {
        if (M_configuration == null) {
            M_configuration = new JMenu("設定");
            M_configuration.add(getCPUMenuItem());
            M_configuration.add(getDisplayMenu());
            M_configuration.add(getSoundMenuItem());
            M_configuration.add(setInputDeviceMenuItem());
            M_configuration.add(getPropertyMenuItem());
            M_configuration.add(getSaveConfigurationMenuItem());
        }
        return M_configuration;
    }

    private JMenuItem getCPUMenuItem() {
        if (MI_cpu == null) {
            MI_cpu = new JMenuItem("CPU...");
            MI_cpu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    CpuConfigurationDialog d = new CpuConfigurationDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_cpu;
    }

    private JMenu getDisplayMenu() {
        if (M_display == null) {
            M_display = new JMenu("画面");
            M_display.add(getDisplayColorMenu());
            M_display.add(getDisplayScaling());
        }
        return M_display;
    }

    private JMenu getDisplayColorMenu() {
        if (M_displayColor == null) {
            M_displayColor = new JMenu("ディスプレイ");
            M_displayColor.add(getMonochromeMenuItem());
            M_displayColor.add(getGreenMenuItem());
            M_displayColor.add(getOrangeMenuItem());
            M_displayColor.add(getColor8MenuItem());
            ButtonGroup g = new ButtonGroup();
            g.add(getMonochromeMenuItem());
            g.add(getGreenMenuItem());
            g.add(getOrangeMenuItem());
            g.add(getColor8MenuItem());
            switch (getComputer().getHardware().getDisplay().getPropertyOfDisplayType()) {
            case JR100Display.DISPLAYTYPE_MONOCHROME:
                getMonochromeMenuItem().setSelected(true);
                break;
            case JR100Display.DISPLAYTYPE_GREEN:
                getGreenMenuItem().setSelected(true);
                break;
            case JR100Display.DISPLAYTYPE_AMBER:
                getOrangeMenuItem().setSelected(true);
                break;
            case JR100Display.DISPLAYTYPE_COLOR:
                getColor8MenuItem().setSelected(true);
                break;
            default:
                break;
            }
        }
        return M_displayColor;
    }

    private JRadioButtonMenuItem getMonochromeMenuItem() {
        if (MI_monochrome == null) {
            MI_monochrome = new JRadioButtonMenuItem("白黒");
            MI_monochrome.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().setPropertyOfDisplayType(JR100Display.DISPLAYTYPE_MONOCHROME);
                }
            });
        }
        return MI_monochrome;
    }

    private JRadioButtonMenuItem getGreenMenuItem() {
        if (MI_green == null) {
            MI_green = new JRadioButtonMenuItem("グリーン");
            MI_green.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().setPropertyOfDisplayType(JR100Display.DISPLAYTYPE_GREEN);
                }
            });
        }
        return MI_green;
    }

    private JRadioButtonMenuItem getOrangeMenuItem() {
        if (MI_orange == null) {
            MI_orange = new JRadioButtonMenuItem("アンバー");
            MI_orange.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().setPropertyOfDisplayType(JR100Display.DISPLAYTYPE_AMBER);
                }
            });
        }
        return MI_orange;
    }

    private JRadioButtonMenuItem getColor8MenuItem() {
        if (MI_color8 == null) {
            MI_color8 = new JRadioButtonMenuItem("カラー");
            MI_color8.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().setPropertyOfDisplayType(JR100Display.DISPLAYTYPE_COLOR);
                }
            });
        }
        return MI_color8;
    }

    private JMenu getDisplayScaling() {
        if (M_displayScaling == null) {
            M_displayScaling = new JMenu("拡大率");
            M_displayScaling.add(getX1MenuItem());
            M_displayScaling.add(getX2MenuItem());
            switch (getComputer().getHardware().getDisplay().getPropertyOfDisplayScaling()) {
            case 1:
                getX1MenuItem().setSelected(true);
                break;
            case 2:
                getX2MenuItem().setSelected(true);
                break;
            default:
                break;
            }
            ButtonGroup g = new ButtonGroup();
            g.add(getX1MenuItem());
            g.add(getX2MenuItem());
        }
        return M_displayScaling;
    }

    private JRadioButtonMenuItem getX1MenuItem() {
        if (MI_x1 == null) {
            MI_x1 = new JRadioButtonMenuItem("x1");
            MI_x1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().setPropertyOfDisplayScaling(1);
                }
            });
        }
        return MI_x1;
    }

    private JRadioButtonMenuItem getX2MenuItem() {
        if (MI_x2 == null) {
            MI_x2 = new JRadioButtonMenuItem("x2");
            MI_x2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().setPropertyOfDisplayScaling(2);
                }
            });
        }
        return MI_x2;
    }

    private JMenuItem getSoundMenuItem() {
        if (MI_sound == null) {
            MI_sound = new JMenuItem("音...");
            MI_sound.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    VolumeDialog d = new VolumeDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_sound;
    }

    private JMenuItem setInputDeviceMenuItem() {
        if (MI_inputDevice == null) {
            MI_inputDevice = new JMenuItem("入力デバイス...");
            MI_inputDevice.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    InputDeviceConfigurationDialog d = new InputDeviceConfigurationDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_inputDevice;
    }

    private JMenuItem getPropertyMenuItem() {
        if (MI_property == null) {
            MI_property = new JMenuItem("プロパティ...");
            MI_property.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PropertyDialog d = new PropertyDialog(getComputer(), JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_property;
    }

    private JMenuItem getSaveConfigurationMenuItem() {
        if (MI_saveConfiguration == null) {
            MI_saveConfiguration = new JMenuItem("設定の保存");
            MI_saveConfiguration.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveProperties(PROPERTIES_FILENAME);
                }
            });
        }
        return MI_saveConfiguration;
    }

    private JMenu getHelpMenu() {
        if (M_help == null) {
            M_help = new JMenu("ヘルプ");
            M_help.add(getHelpTOC());
            M_help.add(getAboutMenuItem());
        }
        return M_help;
    }

    private JMenuItem getHelpTOC() {
        if (MI_helpTOC == null) {
            MI_helpTOC = new JMenuItem("ヘルプ目次");

            HelpSet helpset = null;
            HelpBroker broker = null;
            ClassLoader cl = JR100Application.class.getClassLoader();
            try {
                URL url = HelpSet.findHelpSet(cl, "ug.hs");
                helpset = new HelpSet(null, url);
            }
            catch (Throwable e) {
                getLogger().log(Level.SEVERE, "ヘルプセットのオープンに失敗しました。", e);
                return MI_helpTOC;
            }
            CSH.setHelpIDString(MI_helpTOC, "top");
            broker = helpset.createHelpBroker();
            MI_helpTOC.addActionListener(new CSH.DisplayHelpFromSource(broker));
        }
        return MI_helpTOC;
    }

    private JMenuItem getAboutMenuItem() {
        if (MI_about == null) {
            MI_about = new JMenuItem("JR-100エミュレータについて");
            MI_about.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AboutDialog d = new AboutDialog(JR100Application.this);
                    d.setVisible(true);
                }
            });
        }
        return MI_about;
    }

    @Override
    public JPanel createStatusBar() {
        if (P_statusBar == null) {
            P_statusBar = new JPanel();
            P_statusBar.setLayout(new BoxLayout(P_statusBar, BoxLayout.LINE_AXIS));
            P_statusBar.add(createPowerOffButton());
            P_statusBar.add(createPauseButton());
            P_statusBar.add(createResumeButton());
            P_statusBar.add(Box.createRigidArea(new Dimension(16, 16)));
            P_statusBar.add(createTakeSnapshotButton());
            P_statusBar.add(createRevertSnapshotButton());
            P_statusBar.add(Box.createHorizontalGlue());
        }
        return P_statusBar;
    }

    private JButton createPowerOffButton() {
        if (BT_powerOff == null) {
            BT_powerOff = new JButton();
            BT_powerOff.setPreferredSize(new Dimension(16, 16));
            BT_powerOff.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(app.getClass().getResource("/power.png"))));
            BT_powerOff.setToolTipText("電源OFF");
            if (getComputer().getRunningStatus() == Computer.STATUS_RUNNING ||
                    getComputer().getRunningStatus() == Computer.STATUS_PAUSED) {
                BT_powerOff.setEnabled(true);
            }
            else {
                BT_powerOff.setEnabled(false);
            }
            BT_powerOff.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getComputer().getRunningStatus() != Computer.STATUS_STOPPED) {
                        getComputer().powerOff();
                    }
                    changePowerMenuComponents(Computer.STATUS_STOPPED);
                }
            });
        }
        return BT_powerOff;
    }

    private JButton createPauseButton() {
        if (BT_pause == null) {
            BT_pause = new JButton();
            BT_pause.setPreferredSize(new Dimension(16, 16));
            BT_pause.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(app.getClass().getResource("/pause.png"))));
            BT_pause.setToolTipText("一時停止");
            BT_pause.setEnabled(true);
            BT_pause.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().requestFocusInWindow();
                    getComputer().pause();
                    changePowerMenuComponents(Computer.STATUS_PAUSED);
                }
            });
        }
        return BT_pause;
    }

    private JButton createResumeButton() {
        if (BT_resume == null) {
            BT_resume = new JButton();
            BT_resume.setPreferredSize(new Dimension(16, 16));
            BT_resume.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(app.getClass().getResource("/resume.png"))));
            BT_resume.setToolTipText("再開/電源ON");
            if (getComputer().getRunningStatus() == Computer.STATUS_RUNNING ||
                    getComputer().getRunningStatus() == Computer.STATUS_PAUSED) {
                BT_resume.setEnabled(false);
            }
            else {
                BT_resume.setEnabled(true);
            }
            BT_resume.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().requestFocusInWindow();
                    if (getComputer().getRunningStatus() == Computer.STATUS_STOPPED) {
                        synchronized(getComputer()) {
                            getComputer().notify();
                        }
                    }
                    else if (getComputer().getRunningStatus() == Computer.STATUS_PAUSED) {
                        getComputer().resume();
                    }
                    changePowerMenuComponents(Computer.STATUS_RUNNING);
                }
            });
        }
        return BT_resume;
    }

    private JButton createTakeSnapshotButton() {
        if (BT_takeSnapshot == null) {
            BT_takeSnapshot = new JButton();
            BT_takeSnapshot.setPreferredSize(new Dimension(16, 16));
            BT_takeSnapshot.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(app.getClass().getResource("/take_snapshot.png"))));
            BT_takeSnapshot.setToolTipText("スナップショット採取");
            BT_takeSnapshot.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().requestFocusInWindow();
                    try {
                        getComputer().pause();
                        changePowerMenuComponents(Computer.STATUS_PAUSED);
                        saveState();
                    }
                    catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                            return ;
                    }
                    catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                        return ;
                    }
                    finally {
                        getComputer().resume();
                        changePowerMenuComponents(Computer.STATUS_RUNNING);
                    }
                }
            });
        }
        return BT_takeSnapshot;
    }

    private JButton createRevertSnapshotButton() {
        if (BT_revertSnapshot == null) {
            BT_revertSnapshot = new JButton();
            BT_revertSnapshot.setPreferredSize(new Dimension(16, 16));
            BT_revertSnapshot.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(app.getClass().getResource("/revert_snapshot.png"))));
            BT_revertSnapshot.setToolTipText("スナップショット復元");
            BT_revertSnapshot.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getComputer().getHardware().getDisplay().requestFocusInWindow();
                    try {
                        getComputer().pause();
                        changePowerMenuComponents(Computer.STATUS_PAUSED);
                        loadState();
                    }
                    catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                            return ;
                    }
                    catch (IOException ex) {
                        JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                        return ;
                    }
                    finally {
                        getComputer().resume();
                        changePowerMenuComponents(Computer.STATUS_RUNNING);
                    }
                }
            });
        }
        return BT_revertSnapshot;
    }

    public static void main(String[] args) {
        loadProperties(PROPERTIES_FILENAME);
        initializeLogger("JR-100 Emulator Logger", LOGFILE_NAME);

        // システムプロパティの設定
        System.setProperty("sun.java2d.d3d", Boolean.toString(getPropertyOfD3dUsed()));

        if (args.length == 0) {
            romfilename = DEFAULT_ROM_FILE_NAME;
        }
        else {
            romfilename = args[0];
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                start();
            }
        });
    }
}
