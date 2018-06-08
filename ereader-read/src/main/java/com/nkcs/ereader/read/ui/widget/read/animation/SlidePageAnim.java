package com.nkcs.ereader.read.ui.widget.read.animation;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

/**
 * @author faunleaf
 * @date 2018/3/31
 */

public class SlidePageAnim extends BaseHorizontalPageAnim {

    private Rect mLeftSrcRect;
    private Rect mLeftDestRect;
    private Rect mRightSrcRect;
    private Rect mRightDestRect;
    private GradientDrawable mBackShadowDrawableRL;
    private GradientDrawable mBackShadowDrawableLR;

    public SlidePageAnim(int w, int h, View view, OnPageChangeListener listener) {
        super(w, h, view, listener);
        mLeftSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mLeftDestRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mRightSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mRightDestRect = new Rect(0, 0, mViewWidth, mViewHeight);

        int[] backShadowColors = new int[] { 0x66000000, 0x00000000 };
        mBackShadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, backShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mBackShadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, backShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
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
                mRightSrcRect.right = mScreenWidth - distance;
                mRightDestRect.left = distance;

                canvas.drawBitmap(mNextBitmap, 0, 0, null);
                canvas.drawBitmap(mCurBitmap, mRightSrcRect, mRightDestRect, null);
                mBackShadowDrawableRL.setBounds(mRightDestRect.left - 20, 0, mRightDestRect.left, mScreenHeight);
                mBackShadowDrawableRL.draw(canvas);
                break;
            case NEXT:
                mLeftSrcRect.left = distance;
                mLeftDestRect.right = mScreenWidth - distance;

                canvas.drawBitmap(mNextBitmap, 0, 0, null);
                canvas.drawBitmap(mCurBitmap, mLeftSrcRect, mLeftDestRect, null);
                mBackShadowDrawableLR.setBounds(mLeftDestRect.right, 0, mLeftDestRect.right + 20, mScreenHeight);
                mBackShadowDrawableLR.draw(canvas);
                break;
            default:
                break;
        }
    }
}
