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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;

import jp.asamomiji.emulator.AbstractDisplay;
import jp.asamomiji.emulator.Application;
import jp.asamomiji.emulator.Computer;
import jp.asamomiji.emulator.MemorySystem;
import jp.asamomiji.emulator.StateSet;

@SuppressWarnings("serial")
public class JR100Display extends AbstractDisplay {
    public final static int FONT_NORMAL = 0;
    public final static int FONT_USER_DEFINED = 1;

    public final static int WIDTH_CHARS = 32;
    public final static int HEIGHT_CHARS = 24;
    public final static int PPC = 8;    // Pixel Per Char

    private int userDefinedRam;
    private int videoRam;
    private int characterRom;
    private BufferedImage[][] fonts = new BufferedImage[2][256];
    private BufferedImage[] currentFont;

    private int[][] defaultColorMap = new int[][] {
        {
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
            0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000,
        },
        {
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
            0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff, 0xffffff,
        }
    };
    private int[][] colorMap = new int[2][256];
    private MemorySystem memory;

    public JR100Display(Computer computer) {
        super(computer, WIDTH_CHARS, HEIGHT_CHARS, PPC, PPC);
        this.memory = computer.getHardware().getMemory();
        this.scaling = getPropertyOfDisplayScaling();
        userDefinedRam = memory.getMemory(UserDefinedCharacterRam.class).getStartAddress();
        videoRam = memory.getMemory(VideoRam.class).getStartAddress();
        characterRom = ((BasicRom)memory.getMemory(BasicRom.class)).getFontAddress();
        initializeColors();
        initializeFonts();
        currentFont = fonts[0];
        setPreferredSize(getPreferredSize());
        setFocusable(true);
    }

    @Override
    protected boolean readColorMapFile(String file) {
        if (file == null) {
            Application.getLogger().log(Level.SEVERE, "ファイル 名が指定されていません。");
            return false;
        }
        try {
            int index = 0;
            String line;
            BufferedReader r = new BufferedReader(new FileReader(new File(file)));
            while ((line = r.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    int value;
                    try {
                        value = Integer.parseInt(s, 16);
                    }
                    catch (NumberFormatException ee) {
                        Application.getLogger().log(Level.WARNING, "不正な数値(" + s + ")がありました。0xff0000(赤)として扱います。", ee);
                        value = 0xff0000;
                    }
                    colorMap[index / 256][index % 256] = value;
                    index++;
                    if (index == 2 * 256) {
                        r.close();
                        return true;
                    }
                }
            }
            r.close();
            return true;
        }
        catch (FileNotFoundException e) {
            Application.getLogger().log(Level.SEVERE, "ファイル " + file + " が見つかりません。", e);
            return false;
        }
        catch (IOException e) {
            Application.getLogger().log(Level.SEVERE, "ファイル " + file + " の読み込みに失敗しました。", e);
            return false;
        }
    }

    @Override
    protected void applyDisplayColor() {
        double rgb[][] = new double[2][3];
        switch (getPropertyOfDisplayType()) {
        case DISPLAYTYPE_MONOCHROME:
            rgb[0][0] = 31;
            rgb[0][1] = 31;
            rgb[0][2] = 31;
            rgb[1][0] = 255;
            rgb[1][1] = 255;
            rgb[1][2] = 255;
            break;
        case DISPLAYTYPE_GREEN:
            rgb[0][0] = 0;
            rgb[0][1] = 31;
            rgb[0][2] = 0;
            rgb[1][0] = 0;
            rgb[1][1] = 223;
            rgb[1][2] = 31;
            break;
        case DISPLAYTYPE_AMBER:
            rgb[0][0] = 31;
            rgb[0][1] = 0;
            rgb[0][2] = 0;
            rgb[1][0] = 255;
            rgb[1][1] = 127;
            rgb[1][2] = 0;
            break;
        default:
            rgb[0][0] = 31;
            rgb[0][1] = 31;
            rgb[0][2] = 31;
            rgb[1][0] = 255;
            rgb[1][1] = 255;
            rgb[1][2] = 255;
            break;
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 256; j++) {
                if (getPropertyOfDisplayType() != DISPLAYTYPE_COLOR) {
                    double r0 = (double)((colorMap[i][j] & 0xff0000) >> 16);
                    double g0 = (double)((colorMap[i][j] & 0x00ff00) >> 8);
                    double b0 = (double)( colorMap[i][j] & 0x0000ff);
                    double y = 0.299 * r0 + 0.587 * g0 + 0.114 * b0;
                    int r = (int)(y * rgb[1][0] / 255 + rgb[0][0]);
                    int g = (int)(y * rgb[1][1] / 255 + rgb[0][1]);
                    int b = (int)(y * rgb[1][2] / 255 + rgb[0][2]);
                    if (r > 255) {
                        r = 255;
                    }
                    if (g > 255) {
                        g = 255;
                    }
                    if (b > 255) {
                        b = 255;
                    }
                    colorMap[i][j] = (r << 16) + (g << 8) + b;
                }
            }
        }
    }

    @Override
    protected void initializeColors() {
        int maptype = getPropertyOfDisplayColorMap();
        if (maptype == COLORMAP_DEFAULT) {
            for  (int i = 0; i < 2; i++) {
                System.arraycopy(defaultColorMap[i], 0, colorMap[i], 0, defaultColorMap[i].length);
            }
        }
        else if (maptype == COLORMAP_FILE){
            if (!readColorMapFile(getPropertyOfDisplayColorMapFile())) {
                Application.getLogger().log(Level.WARNING, "カラーマップファイルの読み込みに失敗したためデフォルトカラーマップを適用します。");
                for  (int i = 0; i < 2; i++) {
                    System.arraycopy(defaultColorMap[i], 0, colorMap[i], 0, defaultColorMap[i].length);
                }
            }
        }
        applyDisplayColor();
    }

    @Override
    protected void initializeFonts() {
        int[] bitmasks = new int[] {0xff0000, 0x00ff00, 0x0000ff};
        DirectColorModel cm = new DirectColorModel(24, bitmasks[0], bitmasks[1], bitmasks[2]);
        for (int i = 0; i < fonts.length; i++) {
            for (int j = 0; j < fonts[i].length; j++) {
                fonts[i][j] =
                    new BufferedImage(
                        cm,
                        Raster.createWritableRaster(
                            new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, PPC, PPC, bitmasks),
                            new Point()),
                        false,
                        null);
            }
        }

        for (int code = 0; code < 128; code++) {
            DataBuffer db0a = fonts[FONT_NORMAL][code].getRaster().getDataBuffer();
            DataBuffer db0b = fonts[FONT_NORMAL][code + 128].getRaster().getDataBuffer();
            DataBuffer db1a = fonts[FONT_USER_DEFINED][code].getRaster().getDataBuffer();
            for (int line = 0; line < PPC; line++) {
                int value = memory.load8(characterRom + code * 8 + line);
                for (int bit = 0; bit < PPC; bit++) {
                    int index = line * PPC + (PPC - bit - 1);

                    // CMODE0時の通常文字 (0x00-0x7f)
                    db0a.setElem(index, colorMap[value & 0x01][code]);

                    // CMODE1時の通常文字 (0x00-0x7f)
                    db1a.setElem(index, colorMap[value & 0x01][code]);

                    // CMODE0時の反転文字 (0x80-0xff)
                    db0b.setElem(index, colorMap[1 - (value & 0x01)][code + 128]);

                    value >>= 1;
                }
            }
        }

        // ユーザ定義文字生成
        for (int code = 128; code < 256; code++) {
            DataBuffer db1b = fonts[FONT_USER_DEFINED][code].getRaster().getDataBuffer();
            for (int line = 0; line < PPC; line++) {
                int value = memory.load8(userDefinedRam + (code - 128) * PPC + line);
                for (int bit = 0; bit < PPC; bit++) {
                    // CMODE1時の反転文字 (0x80-0xff)
                    db1b.setElem(line * PPC + (PPC - bit - 1), colorMap[value & 0x01][code]);
                    value >>= 1;
                }
            }
        }
//        try {
//            ImageIO.write(fonts, "png", new File("fonts.png"));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void updateFont(int code, int line, int value) {
        DataBuffer db1b = fonts[FONT_USER_DEFINED][code + 128].getRaster().getDataBuffer();
        for (int bit = 0; bit < PPC; bit++) {
            db1b.setElem(line * PPC + PPC - bit - 1, colorMap[value & 0x01][code + 128]);
            value >>= 1;
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(width * scaling, height * scaling);
    }

    public void setCurrentFont(int plane) {
        currentFont = fonts[plane];
    }

    @Override
    protected void renderDisplay(Graphics g) {
        for (int x = 0; x < cx; x++) {
            for (int y = 0; y < cy; y++) {
                int value = memory.load8(videoRam + x + y * cx) & 0xff;
                g.drawImage(
                    currentFont[value],
                    x * px * scaling,
                    y * py * scaling,
                    (x + 1) * px * scaling,
                    (y + 1) * py * scaling,
                    0,
                    0,
                    px,
                    py,
                    null);
            }
        }
    }

    public BufferedImage[][] getFonts() {
        return fonts;
    }

    @Override
    public void saveState(StateSet ss) {
        super.saveState(ss);
        int index;
        if (currentFont == fonts[0]) {
            index = 0;
        }
        else {
            index = 1;
        }
        ss.set("JR100Display.currentFont", index);
    }

    @Override
    public void loadState(StateSet ss) {
        super.loadState(ss);
        initializeFonts();
        int index;
        index = (Integer)ss.get("JR100Display.currentFont");
        currentFont = fonts[index];
    }
}
