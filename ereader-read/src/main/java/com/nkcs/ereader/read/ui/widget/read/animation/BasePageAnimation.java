package com.nkcs.ereader.read.ui.widget.read.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

/**
 * @author faunleaf
 * @date 2018/3/31
 */

public abstract class BasePageAnimation {

    protected View mView;
    protected Scroller mScroller;
    protected OnPageChangeListener mOnPageChangeListener;

    protected Direction mDirection = Direction.NEXT;
    protected boolean isRunning = false;

    protected int mScreenWidth;
    protected int mScreenHeight;

    protected int mMarginWidth;
    protected int mMarginHeight;

    protected int mViewWidth;
    protected int mViewHeight;

    protected float mTouchX;
    protected float mTouchY;

    protected float mStartX;
    protected float mStartY;

    public BasePageAnimation(int w, int h, View view, OnPageChangeListener listener){
        this(w, h, 0, 0, view, listener);
    }

    public BasePageAnimation(int w, int h, int marginWidth, int marginHeight, View view,
                             OnPageChangeListener listener){
        mScreenWidth = w;
        mScreenHeight = h;

        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        mViewWidth = mScreenWidth - mMarginWidth * 2;
        mViewHeight = mScreenHeight - mMarginHeight * 2;

        mView = view;
        mOnPageChangeListener = listener;

        mScroller = new Scroller(mView.getContext(), new LinearInterpolator());
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected void setTouchPoint(float x, float y) {
        mTouchX = x;
        mTouchY = y;
    }

    protected void setStartPoint(float x, float y) {
        mStartX = x;
        mStartY = y;
    }

    /**
     * 开启翻页动画
     */
    public void startAnim() {
        if (isRunning) {
            return;
        }
        isRunning = true;
    }

    /**
     * 滚动动画，必须放在computeScroll()方法中执行
     */
    public abstract void scrollAnim();

    /**
     * 取消动画
     */
    public abstract void abortAnim();

    /**
     * 点击事件的处理
     *
     * @param event
     */
    public abstract void onTouchEvent(MotionEvent event);

    /**
     * 绘制图形
     *
     * @param canvas
     */
    public abstract void draw(Canvas canvas);

    /**
     * 获取背景板
     *
     * @return Bitmap
     */
    public abstract Bitmap getBgBitmap();

    /**
     * 获取内容显示版面
     */
    public abstract Bitmap getNextBitmap();

    public enum Direction {
        PREV, NEXT
    }

    public interface OnPageChangeListener {

        boolean hasPrev();

        boolean hasNext();

        void pageCancel();
    }
}
