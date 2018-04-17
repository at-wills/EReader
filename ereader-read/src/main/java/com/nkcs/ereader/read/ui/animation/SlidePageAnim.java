package com.nkcs.ereader.read.ui.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * @author faunleaf
 * @date 2018/3/31
 */

public class SlidePageAnim extends BaseHorizontalPageAnim {

    public SlidePageAnim(int w, int h, View view, OnPageChangeListener listener) {
        super(w, h, view, listener);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (isCancel) {
            mNextBitmap = mCurBitmap.copy(Bitmap.Config.RGB_565, true);
            canvas.drawBitmap(mCurBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mNextBitmap, 0, 0, null);
        }
    }

    @Override
    public void drawMove(Canvas canvas) {

    }
}
