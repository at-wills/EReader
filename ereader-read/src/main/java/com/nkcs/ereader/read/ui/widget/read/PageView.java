package com.nkcs.ereader.read.ui.widget.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.ui.widget.read.animation.BaseHorizontalPageAnim;
import com.nkcs.ereader.read.ui.widget.read.animation.BasePageAnimation;
import com.nkcs.ereader.read.ui.widget.read.animation.SimulationPageAnim;
import com.nkcs.ereader.read.ui.widget.read.animation.SlidePageAnim;
import com.nkcs.ereader.read.ui.widget.read.animation.TranslationPageAnim;

/**
 * @author faunleaf
 * @date 2018/4/17
 */

public class PageView extends View {

    protected boolean isPrepare;

    protected int mViewWidth = 0;
    protected int mViewHeight = 0;

    protected BasePageAnimation mPageAnim;
    protected BasePageAnimation.OnPageChangeListener mPageAnimListener = new BasePageAnimation.OnPageChangeListener() {

        @Override
        public boolean hasPrev() {
            return PageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            return PageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            PageView.this.pageCancel();
        }
    };

    protected PageMode mPageMode = PageMode.SIMULATION;
    protected OnPageTouchListener mOnPageTouchListener;

    private float mStartX;
    private float mStartY;
    private boolean isMove = false;
    private RectF mCenterRect = null;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public enum PageMode {
        SLIDE, SIMULATION, TRANSLATION
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
            case SLIDE:
                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case SIMULATION:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case TRANSLATION:
                mPageAnim = new TranslationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            default:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
        }

        drawCurPage(false);
    }

    public void setOnPageTouchListener(OnPageTouchListener listener) {
        mOnPageTouchListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isPrepare = true;
        mViewWidth = w;
        mViewHeight = h;

        prepareDisplay(w, h);
    }

    protected void prepareDisplay(int w, int h) {
        setPageMode(mPageMode);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        mPageAnim.scrollAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPageAnim.draw(canvas);
    }

    protected void drawNextPage() {
        if (!isPrepare) {
            return;
        }
        if (mPageAnim instanceof BaseHorizontalPageAnim) {
            ((BaseHorizontalPageAnim) mPageAnim).changePage();
        }
        drawPage(mPageAnim.getNextBitmap(), false);
    }

    protected void drawCurPage(boolean isUpdate) {
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

    protected void drawPage(Bitmap bitmap, boolean isUpdate) {
        Canvas canvas = new Canvas(mPageAnim.getBgBitmap());
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.read_bg_1));
        canvas.drawText("Content", 100, 100, new TextPaint());
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                isMove = false;
                mPageAnim.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                final int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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
                        if (mOnPageTouchListener != null) {
                            mOnPageTouchListener.onCenterTouch();
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

    private boolean hasPrevPage() {
        mOnPageTouchListener.onPrevPage();
        return prev();
    }

    protected boolean prev() {
        drawNextPage();
        return true;
    }

    private boolean hasNextPage() {
        mOnPageTouchListener.onNextPage();
        return next();
    }

    protected boolean next() {
        drawNextPage();
        return true;
    }

    protected void pageCancel() {}

    public interface OnPageTouchListener {

        void onCenterTouch();

        void onPrevPage();

        void onNextPage();
    }
}
