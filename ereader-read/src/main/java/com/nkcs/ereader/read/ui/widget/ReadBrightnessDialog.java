package com.nkcs.ereader.read.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.base.ui.widget.BaseDialog;
import com.nkcs.ereader.read.R;

/**
 * @author faunleaf
 * @date 2018/4
 */

public class ReadBrightnessDialog extends BaseDialog {

    private int mBrightness;
    private boolean mSystemBrightness;

    private ImageView mIvBrightnessMinus;
    private SeekBar mSbBrightness;
    private ImageView mIvBrightnessPlus;
    private Switch mSwitchSystemBrightness;

    private OnBrightnessChangeListener mOnBrightnessChangeListener;

    public ReadBrightnessDialog(@NonNull Context context) {
        super(context, R.style.Read_BottomDialog);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_read_brightness;
    }

    @Override
    protected void onInitView() {
        mIvBrightnessMinus = findViewById(R.id.read_iv_brightness_minus);
        mIvBrightnessMinus.setOnClickListener(v -> {
            int progress = mSbBrightness.getProgress();
            if (progress > 0) {
                mSbBrightness.setProgress(progress - 1);
            }
        });
        mSbBrightness = findViewById(R.id.read_sb_brightness);
        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mSwitchSystemBrightness.isChecked()) {
                    mSwitchSystemBrightness.setChecked(false);
                }
                if (mOnBrightnessChangeListener != null) {
                    mOnBrightnessChangeListener.onBrightnessChanged(mSbBrightness.getProgress(), mSwitchSystemBrightness.isChecked());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mIvBrightnessPlus = findViewById(R.id.read_iv_brightness_plus);
        mIvBrightnessPlus.setOnClickListener(v -> {
            int progress = mSbBrightness.getProgress();
            if (progress < mSbBrightness.getMax()) {
                mSbBrightness.setProgress(progress + 1);
            }
        });
        mSwitchSystemBrightness = findViewById(R.id.read_switch_system_brightness);
        mSwitchSystemBrightness.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mOnBrightnessChangeListener != null) {
                mOnBrightnessChangeListener.onBrightnessChanged(mSbBrightness.getProgress(), mSwitchSystemBrightness.isChecked());
            }
        });
    }

    @Override
    protected void onLoadData() {
        mSbBrightness.setProgress(mBrightness);
        mSwitchSystemBrightness.setChecked(mSystemBrightness);
    }

    public void setBrightness(int brightness) {
        mBrightness = brightness;
    }

    public void setSystemBrightness(boolean systemBrightness) {
        mSystemBrightness = systemBrightness;
    }

    public void setOnBrightnessChangeListener(OnBrightnessChangeListener l) {
        mOnBrightnessChangeListener = l;
    }

    public interface OnBrightnessChangeListener {

        void onBrightnessChanged(int brightness, boolean systemBrightness);
    }
}
