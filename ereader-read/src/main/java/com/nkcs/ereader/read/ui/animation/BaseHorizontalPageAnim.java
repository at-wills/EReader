package com.nkcs.ereader.read.ui.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author faunleaf
 * @date 2018/4/5
 */

public abstract class BaseHorizontalPageAnim extends BasePageAnimation {

    protected Bitmap mCurBitmap;
    protected Bitmap mNextBitmap;

    protected boolean isCancel = false;

    public BaseHorizontalPageAnim(int w, int h, View view, OnPageChangeListener listener) {
        this(w, h, 0, 0, view, listener);
    }

    public BaseHorizontalPageAnim(int w, int h, int marginWidth, int marginHeight,
                                  View view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);
        mCurBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
        mNextBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            default:
                break;
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isRunning) {
            drawMove(canvas);
        } else {
            if (isCancel) {
                mNextBitmap = mCurBitmap.copy(Bitmap.Config.RGB_565, true);
            }
            drawStatic(canvas);
        }
    }

    @Override
    public Bitmap getBgBitmap() {
        return mNextBitmap;
    }

    @Override
    public Bitmap getNextBitmap() {
        return mNextBitmap;
    }

    /**
     * 转换页面，在显示下一章的时候，必须首先调用此方法
     */
    public void changePage() {
        Bitmap bitmap = mCurBitmap;
        mCurBitmap = mNextBitmap;
        mNextBitmap = bitmap;
    }

    public abstract void drawStatic(Canvas canvas);

    public abstract void drawMove(Canvas canvas);
}
