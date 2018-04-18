package com.nkcs.ereader.read.ui.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.nkcs.ereader.base.utils.LogUtils;

/**
 * @author faunleaf
 * @date 2018/4/5
 */

public abstract class BaseHorizontalPageAnim extends BasePageAnimation {

    protected Bitmap mCurBitmap;
    protected Bitmap mNextBitmap;

    protected boolean isCancel = false;

    protected float mTouchX;
    protected float mTouchY;

    protected float mStartX;
    protected float mStartY;

    private float mMoveX;
    private float mMoveY;
    private boolean isMove = false;
    /**
     * 是否翻阅下一页。true表示翻到下一页，false表示上一页
     */
    private boolean toNext = false;
    /**
     * 是否没下一页或者上一页
     */
    private boolean noNext = false;

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
    public void scrollAnim() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            mTouchX = x;
            mTouchY = y;

            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) {
                isRunning = false;
            }
            mView.postInvalidate();
        }
    }

    @Override
    public void abortAnim() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            isRunning = false;
            mTouchX = mScroller.getFinalX();
            mTouchY = mScroller.getFinalY();
            mView.postInvalidate();
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        mTouchX = event.getX();
        mTouchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMoveX = 0;
                mMoveY = 0;
                isMove = false;
                toNext = false;
                noNext = false;
                isCancel = false;

                mStartX = mTouchX;
                mStartY = mTouchY;
                isRunning = false;
                abortAnim();
                break;
            case MotionEvent.ACTION_MOVE:
                final int touchSlop = ViewConfiguration.get(mView.getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - mTouchX) > touchSlop
                            || Math.abs(mStartY - mTouchY) > touchSlop;
                }
                if (isMove) {
                    // 初始滑动方向代表翻页方向
                    if (mMoveX == 0 && mMoveY == 0) {
                        toNext = mTouchX - mStartX <= 0;
                        if (!nextPage()) {
                            return;
                        }
                    } else {
                        if (toNext) {
                            isCancel = mTouchX - mMoveX > 0;
                        } else {
                            isCancel = mTouchX - mMoveX < 0;
                        }
                    }
                }

                mMoveX = mTouchX;
                mMoveY = mTouchY;
                isRunning = true;
                mView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    toNext = mTouchX > mScreenWidth / 2;
                    if (!nextPage()) {
                        return;
                    }
                }

                if (isCancel) {
                    mOnPageChangeListener.pageCancel();
                }
                if (!noNext) {
                    startAnim();
                    mView.invalidate();
                }
                break;
            default:
                break;
        }
    }

    private boolean nextPage() {
        if (toNext) {
            setDirection(Direction.NEXT);
            noNext = !mOnPageChangeListener.hasNext();
        } else {
            setDirection(Direction.PREV);
            noNext = !mOnPageChangeListener.hasPrev();
        }
        return !noNext;
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
