package com.nkcs.ereader.read.ui.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import com.nkcs.ereader.base.utils.LogUtils;

/**
 * @author faunleaf
 * @date 2018/3/31
 */

public class SlidePageAnim extends BaseHorizontalPageAnim {

    private Rect mSrcRect;
    private Rect mDestRect;
    private Rect mNextSrcRect;
    private Rect mNextDestRect;

    public SlidePageAnim(int w, int h, View view, OnPageChangeListener listener) {
        super(w, h, view, listener);
        mSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mDestRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mNextSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mNextDestRect = new Rect(0, 0, mViewWidth, mViewHeight);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (isCancel) {
            canvas.drawBitmap(mCurBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mNextBitmap, 0, 0, null);
        }
    }

    @Override
    public void drawMove(Canvas canvas) {
        int distance;
        switch (mDirection) {
            case PREV:
                distance = (int) (mTouchX - mStartX);
                if (distance < 0) {
                    distance = 0;
                    mStartX = mTouchX;
                }
                mSrcRect.left = mScreenWidth - distance;
                mDestRect.right = distance;
                mNextSrcRect.right = mScreenWidth - distance;
                mNextDestRect.left = distance;

                canvas.drawBitmap(mCurBitmap, mNextSrcRect, mNextDestRect, null);
                canvas.drawBitmap(mNextBitmap, mSrcRect, mDestRect, null);
                break;
            case NEXT:
                distance = (int) (mScreenWidth - mStartX + mTouchX);
                if (distance > mScreenWidth) {
                    distance = mScreenWidth;
                }
                mSrcRect.left = mScreenWidth - distance;
                mDestRect.right = distance;
                mNextSrcRect.right = mScreenWidth - distance;
                mNextDestRect.left = distance;

                canvas.drawBitmap(mNextBitmap, mNextSrcRect, mNextDestRect, null);
                canvas.drawBitmap(mCurBitmap, mSrcRect, mDestRect, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void startAnim() {
        super.startAnim();
        int destX = 0;
        switch (mDirection) {
            case PREV:
                if (isCancel) {
                    destX = (int) (-Math.abs(mTouchX - mStartX));
                } else {
                    destX = (int) (mScreenWidth - (mTouchX - mStartX));
                }
                break;
            case NEXT:
                if (isCancel) {
                    int distance = (int) (mScreenWidth + (mTouchX - mStartX));
                    if (distance > mScreenWidth) {
                        distance = mScreenWidth;
                    }
                    destX = mScreenWidth - distance;
                } else {
                    destX = (int) -(mScreenWidth + (mTouchX - mStartX));
                }
                break;
            default:
                break;
        }

        int duration = (400 * Math.abs(destX)) / mScreenWidth;
        mScroller.startScroll((int) mTouchX, 0, destX, 0, duration);
    }
}
