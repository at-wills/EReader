package com.nkcs.ereader.read.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.ScreenUtils;
import com.nkcs.ereader.base.utils.StringUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.ui.animation.BaseHorizontalPageAnim;
import com.nkcs.ereader.read.ui.animation.BasePageAnimation;
import com.nkcs.ereader.read.ui.animation.SlidePageAnim;

/**
 * @author faunleaf
 * @date 2018/3/27
 */

public class ReadView extends View {

    private int mViewWidth = 0;
    private int mViewHeight = 0;

    private int mMarginWidth = ScreenUtils.dpToPx(18);
    private int mMarginHeight = ScreenUtils.dpToPx(20);

    private int mVisibleWidth;
    private int mVisibleHeight;

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;

    private boolean isPrepare;

    private boolean mNightMode;
    private int mTextSize;
    private PageStyle mPageStyle = PageStyle.BG_1;
    private int mBgColor;
    private int mTextColor;

    private PageMode mPageMode;

    private Paint mTipPaint;
    private Paint mBgPaint;
    private TextPaint mTextPaint;

    private int mBatteryLevel;

    /**
     * 唤醒菜单的区域
     */
    private RectF mCenterRect = null;

    private BasePageAnimation mPageAnim;
    private BasePageAnimation.OnPageChangeListener mPageAnimListener = new BasePageAnimation.OnPageChangeListener() {

        @Override
        public boolean hasPrev() {
            return ReadView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            return ReadView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            ReadView.this.pageCancel();
        }
    };

    private TouchListener mTouchListener;

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

    public enum PageMode {
        SIMULATION, SLIDE
    }

    /**
     * 翻页动画
     *
     * @param pageMode
     */
    public void setPageMode(PageMode pageMode) {
        mPageMode = pageMode;
        // 视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) {
            return;
        }

        switch (mPageMode) {
//            case SIMULATION:
//                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
//                break;
            case SLIDE:
                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            default:
                break;
        }
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

    public void setTouchListener(TouchListener listener) {
        mTouchListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                isMove = false;
                mPageAnim.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > touchSlop
                            || Math.abs(mStartY - event.getY()) > touchSlop;
                }
                if (isMove) {
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    // 设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 5, mViewHeight / 3,
                                mViewWidth * 4 / 5, mViewHeight * 2 / 3);
                    }

                    // 是否点击了中间
                    if (mCenterRect.contains(event.getX(), event.getY())) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        return true;
                    }
                }
                mPageAnim.onTouchEvent(event);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isPrepare = true;
        prepareDisplay(w, h);
    }

    private void prepareDisplay(int w, int h) {
        mViewWidth = w;
        mViewHeight = h;

        mVisibleWidth = mViewWidth - mMarginWidth * 2;
        mVisibleHeight = mViewHeight - mMarginHeight * 2;

        setPageMode(PageMode.SLIDE);

        drawCurPage(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPageAnim.draw(canvas);
    }

    private void drawNextPage() {
        if (!isPrepare) {
            return;
        }
        if (mPageAnim instanceof BaseHorizontalPageAnim) {
            ((BaseHorizontalPageAnim) mPageAnim).changePage();
        }
        drawPage(mPageAnim.getNextBitmap(), false);
    }

    private void drawCurPage(boolean isUpdate) {
        if (!isPrepare) {
            return;
        }

//        if (!isUpdate){
//            if (mPageAnim instanceof ScrollPageAnim) {
//                ((ScrollPageAnim) mPageAnim).resetBitmap();
//            }
//        }
        drawPage(mPageAnim.getNextBitmap(), isUpdate);
    }

    private void drawPage(Bitmap bitmap, boolean isUpdate) {
        drawBackground(mPageAnim.getBgBitmap(), isUpdate);
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

    private boolean hasPrevPage() {
//        mTouchListener.prePage();
//        return mPageLoader.prev();
        return true;
    }

    private boolean hasNextPage() {
//        mTouchListener.nextPage();
//        return mPageLoader.next();
        return true;
    }

    private void pageCancel() {
//        mTouchListener.cancel();
//        mPageLoader.pageCancel();
    }

    public interface TouchListener {

        void center();

        void prePage();

        void nextPage();

        void cancel();
    }
}
