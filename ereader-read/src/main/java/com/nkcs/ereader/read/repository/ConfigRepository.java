package com.nkcs.ereader.read.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.nkcs.ereader.base.BaseApplication;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.RxUtils;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.ui.widget.read.PageStyle;
import com.nkcs.ereader.read.ui.widget.read.PageView;
import com.nkcs.ereader.read.ui.widget.read.ReadView;

import io.reactivex.Observable;

/**
 * @author faunleaf
 * @date 2018/4/6
 */

public class ConfigRepository extends BaseRepository {

    private static final String USER_CONFIG = "user_config";

    private static final String CONFIG_BRIGHTNESS = "brightness";
    private static final String CONFIG_SYSTEM_BRIGHTNESS = "system_brightness";
    private static final String CONFIG_NIGHT_MODE = "night_mode";
    private static final String CONFIG_TEXT_SIZE = "text_size";
    private static final String CONFIG_PAGE_STYLE = "page_style";
    private static final String CONFIG_PAGE_MODE = "page_mode";

    public ConfigRepository(RxLifecycleBinder binder) {
        super(binder);
    }

    public Observable<Config> getAllConfig() {
        return RxUtils
                .toObservable(() -> getAll())
                .compose(defaultRxConfig());
    }

    public Observable<Config> saveBrigtnessConfig(int brightness, boolean systemBrightness) {
        return RxUtils
                .toObservable(() -> {
                    saveConfig(CONFIG_BRIGHTNESS, brightness);
                    saveConfig(CONFIG_SYSTEM_BRIGHTNESS, systemBrightness);
                    return getAll();
                }).compose(defaultRxConfig());
    }

    public Observable<Config> saveNightModeConfig(boolean nightMode) {
        return RxUtils
                .toObservable(() -> {
                    saveConfig(CONFIG_NIGHT_MODE, nightMode);
                    return getAll();
                }).compose(defaultRxConfig());
    }

    public Observable<Config> saveTextSizeConfig(int textSize) {
        return RxUtils
                .toObservable(() -> {
                    saveConfig(CONFIG_TEXT_SIZE, textSize);
                    return getAll();
                }).compose(defaultRxConfig());
    }

    public Observable<Config> savePageModeConfig(PageView.PageMode pageMode) {
        return RxUtils
                .toObservable(() -> {
                    saveConfig(CONFIG_PAGE_MODE, pageMode);
                    return getAll();
                }).compose(defaultRxConfig());
    }

    public Observable<Config> savePageStyleConfig(PageStyle pageStyle) {
        return RxUtils
                .toObservable(() -> {
                    saveConfig(CONFIG_PAGE_STYLE, pageStyle);
                    return getAll();
                }).compose(defaultRxConfig());
    }

    private Config getAll() {
        SharedPreferences sp = BaseApplication.getContext()
                .getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE);
        return new Config(sp.getInt(CONFIG_BRIGHTNESS, 255),
                sp.getBoolean(CONFIG_SYSTEM_BRIGHTNESS, true),
                sp.getBoolean(CONFIG_NIGHT_MODE, false),
                sp.getInt(CONFIG_TEXT_SIZE, 36),
                PageStyle.values()[sp.getInt(CONFIG_PAGE_STYLE, PageStyle.BG_1.ordinal())],
                ReadView.PageMode.values()[sp.getInt(CONFIG_PAGE_MODE, ReadView.PageMode.SIMULATION.ordinal())]);
    }

    private void saveConfig(String configName, int value) {
        SharedPreferences sp = BaseApplication.getContext()
                .getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(configName, value);
        editor.commit();
    }

    private void saveConfig(String configName, boolean value) {
        SharedPreferences sp = BaseApplication.getContext()
                .getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(configName, value);
        editor.commit();
    }

    private void saveConfig(String configName, PageStyle value) {
        SharedPreferences sp = BaseApplication.getContext()
                .getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(configName, value.ordinal());
        editor.commit();
    }

    private void saveConfig(String configName, ReadView.PageMode value) {
        SharedPreferences sp = BaseApplication.getContext()
                .getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(configName, value.ordinal());
        editor.commit();
    }
}
