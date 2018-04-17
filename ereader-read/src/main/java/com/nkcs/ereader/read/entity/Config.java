package com.nkcs.ereader.read.entity;

import com.nkcs.ereader.read.ui.widget.PageStyle;
import com.nkcs.ereader.read.ui.widget.ReadView;

/**
 * @author faunleaf
 * @date 2018/4/6
 */

public class Config {

    private int brightness;

    private boolean systemBrightness;

    private boolean nightMode;

    private int textSize;

    private PageStyle pageStyle;

    private ReadView.PageMode pageMode;


    public Config(int brightness, boolean systemBrightness, boolean nightMode, int textSize,
                  PageStyle pageStyle, ReadView.PageMode pageMode) {
        this.brightness = brightness;
        this.systemBrightness = systemBrightness;
        this.nightMode = nightMode;
        this.textSize = textSize;
        this.pageStyle = pageStyle;
        this.pageMode = pageMode;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isSystemBrightness() {
        return systemBrightness;
    }

    public void setSystemBrightness(boolean systemBrightness) {
        this.systemBrightness = systemBrightness;
    }

    public boolean isNightMode() {
        return nightMode;
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public PageStyle getPageStyle() {
        return pageStyle;
    }

    public void setPageStyle(PageStyle pageStyle) {
        this.pageStyle = pageStyle;
    }

    public ReadView.PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(ReadView.PageMode pageMode) {
        this.pageMode = pageMode;
    }
}
