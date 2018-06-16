package com.nkcs.ereader.read.ui.widget.read.animation;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

/**
 * @author faunleaf
 * @date 2018/3/31
 */

public class TranslationPageAnim extends BaseHorizontalPageAnim {

    private Rect mLeftSrcRect;
    private Rect mLeftDestRect;
    private Rect mRightSrcRect;
    private Rect mRightDestRect;

    public TranslationPageAnim(int w, int h, View view, OnPageChangeListener listener) {
        super(w, h, view, listener);
        mLeftSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mLeftDestRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mRightSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mRightDestRect = new Rect(0, 0, mViewWidth, mViewHeight);
    }

    @Override
    public void startAnim() {
        super.startAnim();
        int distance = calcDistance();
        int dx = 0;
        switch (mDirection) {
            case PREV:
                if (isCancel) {
                    dx = -distance;
                } else {
                    dx = mScreenWidth - distance;
                }
                break;
            case NEXT:
                if (isCancel) {
                    dx = distance;
                } else {
                    dx = -(mScreenWidth - distance);
                }
                break;
            default:
                break;
        }

        int duration = (300 * Math.abs(dx)) / mScreenWidth;
        mScroller.startScroll((int) mTouchX, 0, dx, 0, duration);
    }

    @Override
    protected void drawStatic(Canvas canvas) {
        if (isCancel) {
            canvas.drawBitmap(mCurBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mNextBitmap, 0, 0, null);
        }
    }

    @Override
    protected void drawMove(Canvas canvas) {
        int distance = calcDistance();
        switch (mDirection) {
            case PREV:
                mLeftSrcRect.left = mScreenWidth - distance;
                mLeftDestRect.right = distance;
                mRightSrcRect.right = mScreenWidth - distance;
                mRightDestRect.left = distance;

                canvas.drawBitmap(mCurBitmap, mRightSrcRect, mRightDestRect, null);
                canvas.drawBitmap(mNextBitmap, mLeftSrcRect, mLeftDestRect, null);
                break;
            case NEXT:
                mLeftSrcRect.left = distance;
                mLeftDestRect.right = mScreenWidth - distance;
                mRightSrcRect.right = distance;
                mRightDestRect.left = mScreenWidth - distance;

                canvas.drawBitmap(mNextBitmap, mRightSrcRect, mRightDestRect, null);
                canvas.drawBitmap(mCurBitmap, mLeftSrcRect, mLeftDestRect, null);
                break;
            default:
                break;
        }
    }
}
