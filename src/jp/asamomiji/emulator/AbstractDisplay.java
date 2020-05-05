/**
 * JR-100 Emulator Version 2
 *
 * Copyright (c) 2006-2020 Kenichi Miyata
 *
 * This software is released under the the MIT license
 * http://opensource.org/licenses/mit-license.php
 */
package jp.asamomiji.emulator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public abstract class AbstractDisplay extends JComponent implements Device, StateSavable {
    public final static int DISPLAYTYPE_MONOCHROME = 0;
    public final static int DISPLAYTYPE_GREEN = 1;
    public final static int DISPLAYTYPE_AMBER = 2;
    public final static int DISPLAYTYPE_COLOR = 3;
    public final static int COLORMAP_DEFAULT = 0;
    public final static int COLORMAP_FILE = 1;

    public final static String PROPERTY_DISPLAY_COLOR = "display.color";    // for compatibility
    public final static String PROPERTY_DISPLAY_COLORMAP = "display.colormap";
    public final static String PROPERTY_DISPLAY_TYPE = "display.type";
    public final static String PROPERTY_DISPLAY_SCALING = "display.scaling";

    protected Computer computer;
    protected int cx;
    protected int cy;
    protected int px;
    protected int py;
    protected int width;
    protected int height;
    protected int scaling = 1;

    private class Refresher implements Runnable {
        public void run() {
            paintImmediately(0, 0, width * scaling, height * scaling);
        }
    }
    private Refresher refresher = new Refresher();

    public AbstractDisplay(Computer computer, int cx, int cy, int px, int py) {
        this.computer = computer;
        this.cx = cx;
        this.cy = cy;
        this.px = px;
        this.py = py;
        this.width = cx * px;
        this.height = cy * py;
    }

    public void setPropertyOfDisplayType(int type) {
        String s;
        switch (type) {
        case DISPLAYTYPE_MONOCHROME:
            s = "monochrome";
            break;
        case DISPLAYTYPE_GREEN:
            s = "green";
            break;
        case DISPLAYTYPE_AMBER:
            s = "amber";
            break;
        case DISPLAYTYPE_COLOR:
            s = "color";
            break;
        default:
            s = "monochrome";
            break;
        }
        Application.getProperties().setProperty(PROPERTY_DISPLAY_TYPE, s);
        initializeColors();
        initializeFonts();
    }

    public int getPropertyOfDisplayType() {
        Properties p = Application.getProperties();
        String m = p.getProperty(PROPERTY_DISPLAY_TYPE);
        String m_compat = p.getProperty(PROPERTY_DISPLAY_COLOR);

        // 新しいプロパティ文字列に変換する。
        if (m_compat != null) {
            p.setProperty(PROPERTY_DISPLAY_TYPE, m_compat);
            p.remove(PROPERTY_DISPLAY_COLOR);
            m = m_compat;
        }
        if (m == null) {
            return DISPLAYTYPE_MONOCHROME;
        }
        else {
            if (m.equals("monochrome")) {
                return DISPLAYTYPE_MONOCHROME;
            }
            else if (m.equals("green")) {
                return DISPLAYTYPE_GREEN;
            }
            else if (m.equals("amber") || m.equals("orange")) {
                return DISPLAYTYPE_AMBER;
            }
            else if (m.equals("color")){
                return DISPLAYTYPE_COLOR;
            }
            else {
                return DISPLAYTYPE_MONOCHROME;
            }
        }
    }

    public void setDisplayColorMap(int kind, String file) {
        if (kind == COLORMAP_DEFAULT) {
            Application.getProperties().setProperty(PROPERTY_DISPLAY_COLORMAP, "default");
        }
        else if (kind == COLORMAP_FILE) {
            if (file == null || file.length() == 0) {
                throw new IllegalArgumentException("ファイルが指定されていないかファイル名長がゼロです。");
            }
            readColorMapFile(file);
            Application.getProperties().setProperty(PROPERTY_DISPLAY_COLORMAP, "file " + file);
        }
        applyDisplayColor();
        initializeFonts();
    }

    public int getPropertyOfDisplayColorMap() {
        String m = Application.getProperties().getProperty(PROPERTY_DISPLAY_COLORMAP, "default");
        if (m.matches("^default\\s.*$")) {
            return COLORMAP_DEFAULT;
        }
        else if (m.matches("^file\\s.*$")) {
            return COLORMAP_FILE;
        }
        else {
            return COLORMAP_DEFAULT;
        }
    }

    public String getPropertyOfDisplayColorMapFile() {
        String m = Application.getProperties().getProperty(PROPERTY_DISPLAY_COLORMAP);
        if (m == null) {
            return null;
        }
        else {
            if (m.matches("^default\\s.*$")) {
                return null;
            }
            else if (m.matches("^file\\s.*$")) {
                return m.replaceFirst("^file\\s*", "");
            }
            else {
                return null;
            }
        }
    }

    public int getPropertyOfDisplayScaling() {
        String m = Application.getProperties().getProperty(PROPERTY_DISPLAY_SCALING, "1");
        int value;
        try {
            value = Integer.parseInt(m);
        }
        catch (NumberFormatException e) {
            value = 1;
        }
        return value;
    }

    public void setPropertyOfDisplayScaling(int scaling) {
        this.scaling = scaling;
        computer.getApplication().pack();
        Application.getProperties().setProperty(PROPERTY_DISPLAY_SCALING, Integer.toString(scaling));
    }

    protected abstract boolean readColorMapFile(String filename);

    protected abstract void applyDisplayColor();

    protected abstract void initializeColors();

    protected abstract void initializeFonts();

    protected abstract void renderDisplay(Graphics g);

    public void refresh() {
        SwingUtilities.invokeLater(refresher);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.gray);
        g.fillRect(0, 0, width * scaling, height * scaling);
        if (computer.getRunningStatus() == Computer.STATUS_RUNNING || computer.getRunningStatus() == Computer.STATUS_PAUSED) {
            renderDisplay(g);
        }
    }

    public void execute() {
    }

    public void reset() {
    }

    public void loadState(StateSet ss) {
    }

    public void saveState(StateSet ss) {
    }
}
