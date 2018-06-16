package com.nkcs.ereader.read.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nkcs.ereader.base.ui.widget.BaseDialog;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.ui.widget.read.PageStyle;
import com.nkcs.ereader.read.ui.widget.read.PageView;

/**
 * @author faunleaf
 * @date 2018/4
 */

public class ReadSettingDialog extends BaseDialog {

    private static final int MAX_TEXT_SIZE = 70;

    private int mTextSize;
    private PageView.PageMode mPageMode;
    private PageStyle mPageStyle;

    private TextView mTvTextSizeMinus;
    private TextView mTvTextSize;
    private TextView mTvTextSizePlus;

    private RadioGroup mRgPageMode;
    private RadioGroup mRgPageStyle;

    private OnSettingChangeListener mOnSettingChangeListener;

    public ReadSettingDialog(@NonNull Context context) {
        super(context, R.style.Read_BottomDialog);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_read_setting;
    }

    @Override
    protected void onInitView() {
        mTvTextSizeMinus = findViewById(R.id.read_tv_text_size_minus);
        mTvTextSizeMinus.setOnClickListener(v -> {
            int textSize = Integer.parseInt(mTvTextSize.getText().toString());
            if (textSize > 0) {
                textSize -= 1;
                mTvTextSize.setText(String.valueOf(textSize));
                textSizeChanged(textSize);
            }
        });
        mTvTextSize = findViewById(R.id.read_tv_text_size);
        mTvTextSizePlus = findViewById(R.id.read_tv_text_size_plus);
        mTvTextSizePlus.setOnClickListener(v -> {
            int textSize = Integer.parseInt(mTvTextSize.getText().toString());
            if (textSize < MAX_TEXT_SIZE) {
                textSize += 1;
                mTvTextSize.setText(String.valueOf(textSize));
                textSizeChanged(textSize);
            }
        });

        mRgPageMode = findViewById(R.id.read_rg_page_mode);
        mRgPageMode.setOnCheckedChangeListener((group, checkedId) -> {
            PageView.PageMode pageMode;
            if (checkedId == R.id.read_rb_slide) {
                pageMode = PageView.PageMode.SLIDE;
            } else if (checkedId == R.id.read_rb_simulation) {
                pageMode = PageView.PageMode.SIMULATION;
            } else if (checkedId == R.id.read_rb_translation) {
                pageMode = PageView.PageMode.TRANSLATION;
            } else {
                pageMode = PageView.PageMode.SIMULATION;
            }
            pageModeChanged(pageMode);
        });
        mRgPageStyle = findViewById(R.id.read_rg_page_style);
        mRgPageStyle.setOnCheckedChangeListener((group, checkedId) -> {
            PageStyle pageStyle;
            if (checkedId == R.id.read_rb_page_style_1) {
                pageStyle = PageStyle.BG_1;
            } else if (checkedId == R.id.read_rb_page_style_2) {
                pageStyle = PageStyle.BG_2;
            } else if (checkedId == R.id.read_rb_page_style_3) {
                pageStyle = PageStyle.BG_3;
            } else if (checkedId == R.id.read_rb_page_style_4) {
                pageStyle = PageStyle.BG_4;
            } else if (checkedId == R.id.read_rb_page_style_5) {
                pageStyle = PageStyle.BG_5;
            } else {
                pageStyle = PageStyle.BG_1;
            }
            // check 图标
            Drawable drawableUnchecked = ContextCompat.getDrawable(getContext(), R.drawable.ic_read_unchecked);
            ((RadioButton) findViewById(R.id.read_rb_page_style_1))
                    .setCompoundDrawablesWithIntrinsicBounds(drawableUnchecked, null, null, null);
            ((RadioButton) findViewById(R.id.read_rb_page_style_2))
                    .setCompoundDrawablesWithIntrinsicBounds(drawableUnchecked, null, null, null);
            ((RadioButton) findViewById(R.id.read_rb_page_style_3))
                    .setCompoundDrawablesWithIntrinsicBounds(drawableUnchecked, null, null, null);
            ((RadioButton) findViewById(R.id.read_rb_page_style_4))
                    .setCompoundDrawablesWithIntrinsicBounds(drawableUnchecked, null, null, null);
            ((RadioButton) findViewById(R.id.read_rb_page_style_5))
                    .setCompoundDrawablesWithIntrinsicBounds(drawableUnchecked, null, null, null);
            Drawable drawableChecked = ContextCompat.getDrawable(getContext(), R.drawable.ic_read_checked);
            ((RadioButton) findViewById(checkedId))
                    .setCompoundDrawablesWithIntrinsicBounds(drawableChecked, null, null, null);
            pageStyleChanged(pageStyle);
        });
    }

    @Override
    protected void onLoadData() {
        mTvTextSize.setText(String.valueOf(mTextSize));
        switch (mPageMode) {
            case SLIDE:
                mRgPageMode.check(R.id.read_rb_slide);
                break;
            case SIMULATION:
                mRgPageMode.check(R.id.read_rb_simulation);
                break;
            case TRANSLATION:
                mRgPageMode.check(R.id.read_rb_translation);
                break;
            default:
                break;
        }
        switch (mPageStyle) {
            case BG_1:
                mRgPageStyle.check(R.id.read_rb_page_style_1);
                break;
            case BG_2:
                mRgPageStyle.check(R.id.read_rb_page_style_2);
                break;
            case BG_3:
                mRgPageStyle.check(R.id.read_rb_page_style_3);
                break;
            case BG_4:
                mRgPageStyle.check(R.id.read_rb_page_style_4);
                break;
            case BG_5:
                mRgPageStyle.check(R.id.read_rb_page_style_5);
                break;
            default:
                break;
        }
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public void setPageMode(PageView.PageMode pageMode) {
        mPageMode = pageMode;
    }

    public void setPageStyle(PageStyle pageStyle) {
        mPageStyle = pageStyle;
    }

    public void setOnSettingChangeListener(OnSettingChangeListener l) {
        mOnSettingChangeListener = l;
    }

    private void textSizeChanged(int textSize) {
        mTextSize = textSize;
        if (mOnSettingChangeListener != null) {
            mOnSettingChangeListener.onTextSizeChanged(textSize);
        }
    }

    private void pageModeChanged(PageView.PageMode pageMode) {
        mPageMode = pageMode;
        if (mOnSettingChangeListener != null) {
            mOnSettingChangeListener.onPageModeChanged(pageMode);
        }
    }

    private void pageStyleChanged(PageStyle pageStyle) {
        mPageStyle = pageStyle;
        if (mOnSettingChangeListener != null) {
            mOnSettingChangeListener.onPageStyleChanged(pageStyle);
        }
    }

    public interface OnSettingChangeListener {

        void onTextSizeChanged(int textSize);

        void onPageModeChanged(PageView.PageMode pageMode);

        void onPageStyleChanged(PageStyle pageStyle);
    }
}
