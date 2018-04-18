package com.nkcs.ereader.read.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.nkcs.ereader.base.utils.ScreenUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.ui.animation.BaseHorizontalPageAnim;
import com.nkcs.ereader.read.ui.animation.BasePageAnimation;
import com.nkcs.ereader.read.ui.animation.SlidePageAnim;

/**
 * @author faunleaf
 * @date 2018/3/27
 */

public class ReadView extends PageView {

    private int mMarginWidth = ScreenUtils.dpToPx(18);
    private int mMarginHeight = ScreenUtils.dpToPx(20);

    private int mVisibleWidth;
    private int mVisibleHeight;

    private boolean mNightMode;
    private int mTextSize;
    private PageStyle mPageStyle = PageStyle.BG_1;
    private int mBgColor;
    private int mTextColor;

    private Paint mTipPaint;
    private Paint mBgPaint;
    private TextPaint mTextPaint;

    private int mBatteryLevel;


    public ReadView(Context context) {
        this(context, null);
    }

    public ReadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitView();
    }

    private void onInitView() {
        mTipPaint = new Paint();
        mTipPaint.setColor(ContextCompat.getColor(getContext(), R.color.read_text_light));
        mTipPaint.setTextAlign(Paint.Align.LEFT);
        mTipPaint.setTextSize(ScreenUtils.spToPx(12));
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
    }

    /**
     * 设置夜间模式
     *
     * @param nightMode
     */
    public void setNightMode(boolean nightMode) {
        mNightMode = nightMode;
        if (mNightMode) {
            setPageStyle(PageStyle.NIGHT);
        } else {
            setPageStyle(mPageStyle);
        }
    }

    /**
     * 是否为夜间模式
     */
    public boolean isNightMode() {
        return mNightMode;
    }

    /**
     * 设置文字相关参数
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        mTextPaint.setTextSize(mTextSize);
        // 取消缓存
//        mPrePageList = null;
//        mNextPageList = null;

        // 如果当前已经显示数据
//        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
//            // 重新计算当前页面
//            dealLoadPageList(mCurChapterPos);
//
//            // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
//            if (mCurPage.position >= mCurPageList.size()) {
//                mCurPage.position = mCurPageList.size() - 1;
//            }
//
//            // 重新获取指定页面
//            mCurPage = mCurPageList.get(mCurPage.position);
//        }

        drawCurPage(false);
    }

    /**
     * 设置页面样式
     *
     * @param pageStyle
     */
    public void setPageStyle(PageStyle pageStyle) {
        if (pageStyle != PageStyle.NIGHT) {
            mPageStyle = pageStyle;
        }
        if (mNightMode && pageStyle != PageStyle.NIGHT) {
            return;
        }

        // 设置当前颜色样式
        mTextColor = ContextCompat.getColor(getContext(), pageStyle.getFontColor());
        mBgColor = ContextCompat.getColor(getContext(), pageStyle.getBgColor());

        mTipPaint.setColor(mTextColor);
        mTextPaint.setColor(mTextColor);
        mBgPaint.setColor(mBgColor);

        drawCurPage(false);
    }

    /**
     * 更新电量
     *
     * @param level
     */
    public void updateBattery(int level) {
        mBatteryLevel = level;
        if (!mPageAnim.isRunning()) {
            drawCurPage(true);
        }
    }

    @Override
    protected void prepareDisplay(int w, int h) {
        mVisibleWidth = mViewWidth - mMarginWidth * 2;
        mVisibleHeight = mViewHeight - mMarginHeight * 2;

        setPageMode(mPageMode);
        drawCurPage(false);
    }

    @Override
    protected void drawPage(Bitmap bitmap, boolean isUpdate) {
        drawBackground(mPageAnim.getBgBitmap(), isUpdate);
        if (!isUpdate) {
            drawContent(bitmap);
        }
        invalidate();
    }

    private void drawBackground(Bitmap bitmap, boolean isUpdate) {
        Canvas canvas = new Canvas(bitmap);
        if (!isUpdate) {
            canvas.drawColor(mBgColor);
        } else {

        }

        drawTopArea(canvas);
        drawBottomArea(canvas);
    }

    private void drawTopArea(Canvas canvas) {
        // 章节
        float y = mMarginHeight;
        String chapter = "第一章 监狱";
        canvas.drawText(chapter, mMarginWidth, y, mTipPaint);

        // 标题
        String title = "英雄监狱";
        canvas.drawText(title, mViewWidth - mMarginWidth - mTipPaint.measureText(title), y, mTipPaint);
    }

    private void drawBottomArea(Canvas canvas) {
        int tipMarginHeight = ScreenUtils.dpToPx(5);

        // 电量
        int visibleBottom = mViewHeight - tipMarginHeight;
        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();
        int polarHeight = ScreenUtils.dpToPx(6);
        int polarWidth = ScreenUtils.dpToPx(2);
        int border = 1;
        int innerMargin = 1;

        int outFrameLeft = mMarginWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, outFrameLeft + outFrameWidth, outFrameBottom);
        mTipPaint.setStyle(Paint.Style.STROKE);
        mTipPaint.setStrokeWidth(border);
        canvas.drawRect(outFrame, mTipPaint);

        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);
        mTipPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(innerFrame, mTipPaint);

        int polarLeft = outFrameLeft + outFrameWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, polarLeft + polarWidth,
                polarTop + polarHeight - ScreenUtils.dpToPx(2));
        mTipPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(polar, mTipPaint);

        // 时间
        float y = mViewHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight + 1;
        String time = "18:41";
        float x = polarLeft + polarWidth + ScreenUtils.dpToPx(6);
        canvas.drawText(time, x, y, mTipPaint);

        // 章节
        String chapter = "1/1481章";
        canvas.drawText(chapter, (mViewWidth - mTipPaint.measureText(chapter)) / 2, y, mTipPaint);

        // 进度
        String progress = "本章第1/11";
        canvas.drawText(progress, mViewWidth - mMarginWidth - mTipPaint.measureText(progress), y, mTipPaint);
    }

    private void drawContent(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);


    }

    @Override
    protected boolean hasPrevPage() {
        super.hasPrevPage();
//        return mPageLoader.prev();
        return true;
    }

    @Override
    protected boolean hasNextPage() {
        super.hasNextPage();
//        return mPageLoader.next();
        return true;
    }

    @Override
    protected void pageCancel() {
        super.pageCancel();
//        mPageLoader.pageCancel();
    }
}
