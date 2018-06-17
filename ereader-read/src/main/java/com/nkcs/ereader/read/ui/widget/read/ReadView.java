package com.nkcs.ereader.read.ui.widget.read;

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

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.subscriber.CommonSubscriber;
import com.nkcs.ereader.base.utils.FileUtils;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.RxUtils;
import com.nkcs.ereader.base.utils.ScreenUtils;
import com.nkcs.ereader.base.utils.StringUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.ui.widget.read.formatter.BookFormatter;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author faunleaf
 * @date 2018/3/27
 */

public class ReadView extends PageView {

    private final static int STATUS_LOADING = 1;
    private final static int STATUS_FINISH = 2;
    private final static int STATUS_ERROR = 3;
    private int mStatus = STATUS_LOADING;

    private int mMarginWidth = ScreenUtils.dpToPx(18);
    private int mMarginHeight = ScreenUtils.dpToPx(40);
    private int mVisibleWidth;
    private int mVisibleHeight;

    private Book mBook;
    private OnBookChangeListener mOnBookChangeListener;

    private int mCurChapter;
    private int mLastChapter;
    private ContentPage mCurPage;
    private ContentPage mCancelPage;
    private List<ContentPage> mPrevPageList;
    private List<ContentPage> mCurPageList;
    private List<ContentPage> mNextPageList;
    private Disposable mPreLoadDisp;

    private boolean mNightMode;
    private int mTextSize;
    private int mTitleSize;
    private PageStyle mPageStyle = PageStyle.BG_1;
    private int mBgColor;
    private int mTextColor;
    private double mTextInterval = 0.5;

    private Paint mTipPaint;
    private Paint mBgPaint;
    private Paint mTitlePaint;
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

        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);

        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
    }

    public void setBook(Book book, OnBookChangeListener listener) {
        if (book == null || listener == null) {
            // TODO: 显示错误信息
            return;
        }
        mBook = book;
        mOnBookChangeListener = listener;

        if (mBook.getHasFormat()) {
            int chapter = mBook.getLastReadChapter() != null ? mBook.getLastReadChapter() : 0;
            skipToChapter(chapter);
        } else {
            runInBackground(() -> BookFormatter.getFormatter(mBook).getChapterList(),
                    (chapterList) -> listener.onFormatChapters(mBook, chapterList));
        }
    }

    public void skipToChapter(int pos) {
        mCurChapter = pos;
        mLastChapter = mCurChapter;
        mPrevPageList = null;
        mCurPageList = null;

        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        mNextPageList = null;

        mStatus = STATUS_LOADING;
        loadChapter(pos, true);
    }

    private void loadChapter(int pos, boolean render) {
        Chapter chapter = mBook.getChapterList().get(pos);
        runInBackground(() -> BookFormatter.getFormatter(mBook).loadChapter(chapter), (reader) -> {
            mCurPageList = loadPages(chapter, reader);
            mCurPage = mCurPageList.get(0);
            mStatus = STATUS_FINISH;

            if (render) {
                drawCurPage(false);
            }
            chapterChangeCallback();
        });
    }

    private List<ContentPage> loadPages(Chapter chapter, BufferedReader reader) {
        List<ContentPage> pages = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        int height = mVisibleHeight;
        int titleLinesCount = 0;
        try {
            boolean showTitle = true;
            String paragraph = chapter.getTitle();
            Paint paint = mTitlePaint;
            while (showTitle || (paragraph = reader.readLine()) != null) {
                if (showTitle) {
                    height -= computeTextInterval();
                } else {
                    paragraph = StringUtils.fullToHalf(paragraph).trim();
                    if ("".equals(paragraph)) {
                        continue;
                    }
                    paragraph = StringUtils.halfToFull("  " + paragraph + "\n");
                }

                int wordCount;
                String subStr;
                while (paragraph.length() > 0) {
                    height -= paint.getTextSize();
                    if (height <= 0) {
                        ContentPage page = new ContentPage();
                        page.setPosition(pages.size());
                        page.setChapter(chapter);
                        page.setLines(new ArrayList<>(lines));
                        page.setTitleLinesCount(titleLinesCount);
                        pages.add(page);

                        lines.clear();
                        height = mVisibleHeight;
                        titleLinesCount = 0;
                        continue;
                    }

                    wordCount = paint.breakText(paragraph,
                            true, mVisibleWidth, null);
                    subStr = paragraph.substring(0, wordCount);
                    if (!"\n".equals(subStr)) {
                        lines.add(subStr);
                        height -= computeTextInterval();
                        if (showTitle) {
                            titleLinesCount += 1;
                        }
                    }
                    paragraph = paragraph.substring(wordCount);
                }

                if (lines.size() != 0) {
                    height = height - computeTextPara() + computeTextInterval();
                }
                if (showTitle) {
                    showTitle = false;
                    paint = mTextPaint;
                }
            }

            if (lines.size() != 0) {
                ContentPage page = new ContentPage();
                page.setPosition(pages.size());
                page.setChapter(chapter);
                page.setLines(new ArrayList<>(lines));
                page.setTitleLinesCount(titleLinesCount);
                pages.add(page);
                lines.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(reader);
        }
        return pages;
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
        mTextSize = textSize;
        mTitleSize = (int)(mTextSize * 1.2);

        mTitlePaint.setTextSize(mTitleSize);
        mTextPaint.setTextSize(mTextSize);
        // 取消缓存
        mPrevPageList = null;
        mNextPageList = null;

        // 如果当前已经显示数据
        if (mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            int pos = mCurPage.getPosition();
            loadChapter(mCurChapter, false);

            // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
            if (pos >= mCurPageList.size()) {
                pos -= 1;
            }
            mCurPage = mCurPageList.get(pos);
        }

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
        mTitlePaint.setColor(mTextColor);
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
        if (mPageAnim != null && !mPageAnim.isRunning()) {
            drawCurPage(true);
        }
    }

    /**
     * 更新时间
     */
    public void updateTime() {
        if (mPageAnim != null && !mPageAnim.isRunning()) {
            drawCurPage(true);
        }
    }

    /**
     * 获取 Book
     */
    public Book getBook() {
        return mBook;
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
            canvas.drawRect(0, mViewHeight - 50, mViewWidth / 2, mViewHeight, mBgPaint);
        }

        drawTopArea(canvas);
        drawBottomArea(canvas);
    }

    private void drawTopArea(Canvas canvas) {
        int tipMarginHeight = ScreenUtils.dpToPx(8);
        // 章节
        float y = tipMarginHeight - mTipPaint.getFontMetrics().top;
        String chapter = (mCurPage != null ? mCurPage.getChapter().getTitle() : "");
        canvas.drawText(chapter, mMarginWidth, y, mTipPaint);

        // 标题
        String title = (mBook != null ? mBook.getTitle() : "");
        canvas.drawText(title, mViewWidth - mMarginWidth - mTipPaint.measureText(title), y, mTipPaint);
    }

    private void drawBottomArea(Canvas canvas) {
        int tipMarginHeight = ScreenUtils.dpToPx(8);

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
        String time = StringUtils.dateConvert(System.currentTimeMillis(), "HH:mm");
        float x = polarLeft + polarWidth + ScreenUtils.dpToPx(6);
        canvas.drawText(time, x, y, mTipPaint);

        // 章节
        String chapter = (mCurPage != null ? mCurPage.getChapter().getSequence() + 1 : 1) + "/"
                + (mBook != null ? mBook.getChapterList().size() : 1) + "章";
        canvas.drawText(chapter, (mViewWidth - mTipPaint.measureText(chapter)) / 2, y, mTipPaint);

        // 进度
        String progress = "本章第" + (mCurPage != null ? mCurPage.getPosition() + 1 : 1)
                + "/" + (mCurPageList != null ? mCurPageList.size() : 1);
        canvas.drawText(progress, mViewWidth - mMarginWidth - mTipPaint.measureText(progress), y, mTipPaint);
    }

    private void drawContent(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
//        if (mPageMode == PageMode.SCROLL) {
//            canvas.drawColor(mBgColor);
//        }

        if (mStatus != STATUS_FINISH) {
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "正在拼命加载中...";
                    break;
                case STATUS_ERROR:
                    tip = "加载出错";
                    break;
                default:
                    break;
            }
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTextPaint.measureText(tip);
            float pivotX = (mViewWidth - textWidth) / 2;
            float pivotY = (mViewHeight - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTextPaint);
        } else {
            int top;
//            if (mPageMode == PageMode.SCROLL) {
//                top = -mTextPaint.getFontMetrics().top;
//            } else {
            top = mMarginHeight - (int) mTextPaint.getFontMetrics().top;
//            }

            float interval = computeTextInterval() + mTextPaint.getTextSize();
            float para = computeTextPara() + mTextPaint.getTextSize();
            float titleInterval = computeTextInterval() + mTitlePaint.getTextSize();
            float titlePara = computeTextPara() + mTitlePaint.getTextSize();
            String str;

            for (int i = 0; i < mCurPage.getTitleLinesCount(); ++i) {
                str = mCurPage.getLines().get(i);
                if (i == 0) {
                    top += computeTextInterval();
                }

                canvas.drawText(str, mMarginWidth, top, mTitlePaint);
                if (i == mCurPage.getTitleLinesCount() - 1) {
                    top += titlePara;
                } else {
                    top += titleInterval;
                }
            }

            for (int i = mCurPage.getTitleLinesCount(); i < mCurPage.getLines().size(); ++i) {
                str = mCurPage.getLines().get(i);
                canvas.drawText(str, mMarginWidth, top, mTextPaint);
                if (str.endsWith("\n")) {
                    top += para;
                } else {
                    top += interval;
                }
            }
        }
    }

    private int computeTextInterval() {
        return (int)(mTextSize * mTextInterval);
    }

    private int computeTextPara() {
        return (int)(mTextSize * mTextInterval * 2);
    }

    @Override
    protected boolean prev() {
        mLastChapter = mCurChapter;
        if (!canTurnPage()) {
            return false;
        }

        ContentPage prevPage = getPrevPage();
        if (prevPage != null) {
            mCancelPage = mCurPage;
            mCurPage = prevPage;
            drawNextPage();
            return true;
        }

        if (hasPrevChapter()) {
            mCancelPage = mCurPage;
            if (parsePrevChapter()) {
                mCurPage = mCurPageList.get(mCurPageList.size() - 1);
            }
            drawNextPage();
            return true;
        }

        return false;
    }

    private ContentPage getPrevPage() {
        int pos = mCurPage.getPosition() - 1;
        if (pos < 0) {
            return null;
        }
        return mCurPageList.get(pos);
    }

    private boolean hasPrevChapter() {
        return mCurChapter - 1 >= 0;
    }

    private boolean parsePrevChapter() {
        int prevChapter = mCurChapter - 1;
        mLastChapter = mCurChapter;
        mCurChapter = prevChapter;

        mNextPageList = mCurPageList;
        if (mPrevPageList != null) {
            mCurPageList = mPrevPageList;
            mPrevPageList = null;

            chapterChangeCallback();
        } else {
            loadChapter(prevChapter, false);
        }
        return mCurPageList != null;
    }

    @Override
    protected boolean next() {
        mLastChapter = mCurChapter;
        if (!canTurnPage()) {
            return false;
        }

        ContentPage nextPage = getNextPage();
        if (nextPage != null) {
            mCancelPage = mCurPage;
            mCurPage = nextPage;
            drawNextPage();
            return true;
        }

        if (hasNextChapter()) {
            mCancelPage = mCurPage;
            if (parseNextChapter()) {
                mCurPage = mCurPageList.get(0);
            }
            drawNextPage();
            return true;
        }

        return false;
    }

    private ContentPage getNextPage() {
        int pos = mCurPage.getPosition() + 1;
        if (pos >= mCurPageList.size()) {
            return null;
        }
        return mCurPageList.get(pos);
    }

    private boolean hasNextChapter() {
        return mCurChapter + 1 < mBook.getChapterList().size();
    }

    private boolean parseNextChapter() {
        int nextChapter = mCurChapter + 1;
        mLastChapter = mCurChapter;
        mCurChapter = nextChapter;

        mPrevPageList = mCurPageList;
        if (mNextPageList != null) {
            mCurPageList = mNextPageList;
            mNextPageList = null;

            chapterChangeCallback();
        } else {
            loadChapter(nextChapter, false);
        }
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null;
    }

    private boolean canTurnPage() {
        return mStatus == STATUS_FINISH;
    }

    @Override
    protected void pageCancel() {
        if (mCurPage.getPosition() == 0 && mCurChapter > mLastChapter) {
            if (mPrevPageList != null) {
                cancelNextChapter();
            } else if (parsePrevChapter()) {
                mCurPage = mCurPageList.get(mCurPageList.size() - 1);
            }
        } else if (mCurPage.getPosition() == mCurPageList.size() - 1 && mCurChapter < mLastChapter) {
            if (mNextPageList != null) {
                cancelPrevChapter();
            } else if (parseNextChapter()) {
                mCurPage = mCurPageList.get(0);
            }
        } else {
            mCurPage = mCancelPage;
        }
    }

    private void cancelNextChapter() {
        int temp = mLastChapter;
        mLastChapter = mCurChapter;
        mCurChapter = temp;

        mNextPageList = mCurPageList;
        mCurPageList = mPrevPageList;
        mPrevPageList = null;

        chapterChangeCallback();

        mCurPage = mCurPageList.get(mCurPageList.size() - 1);
        mCancelPage = null;
    }

    private void cancelPrevChapter() {
        int temp = mLastChapter;
        mLastChapter = mCurChapter;
        mCurChapter = temp;

        mPrevPageList = mCurPageList;
        mCurPageList = mNextPageList;
        mNextPageList = null;

        chapterChangeCallback();

        mCurPage = mCurPageList.get(0);
        mCancelPage = null;
    }

    private void chapterChangeCallback() {
        if (mOnBookChangeListener != null) {
            mOnBookChangeListener.onChapterChange(mBook, mBook.getChapterList().get(mCurChapter));
        }
    }

    private void preLoadNextChapter() {
        if (!hasNextChapter()) {
            return;
        }

        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        // 调用异步进行预加载加载
        Chapter nextChapter = mBook.getChapterList().get(mCurChapter + 1);
        RxUtils.toObservable(() -> BookFormatter.getFormatter(mBook).loadChapter(nextChapter)).subscribe(new CommonSubscriber<BufferedReader>() {

            @Override
            public void onSubscribe(Disposable d) {
                mPreLoadDisp = d;
            }

            @Override
            public void onSuccess(BufferedReader reader) {
                mNextPageList = loadPages(nextChapter, reader);
            }

            @Override
            public void onFailure(Throwable e) {}

            @Override
            public void onComplete() {
                mPreLoadDisp = null;
            }
        });
    }

    private <T> void runInBackground(Callable<T> callable, Consumer<T> onSuccess) {
        RxUtils.toObservable(callable).subscribe(new CommonSubscriber<T>() {

            @Override
            public void onSuccess(T t) {
                if (onSuccess != null) {
                    try {
                        onSuccess.accept(t);
                    } catch (Exception e) {
                        mStatus = STATUS_ERROR;
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable e) {
                // TODO: 显示错误信息
                mStatus = STATUS_ERROR;
            }
        });
    }

    public interface OnBookChangeListener {

        void onFormatChapters(Book book, List<Chapter> chapterList);

        void onChapterChange(Book book, Chapter chapter);
    }

}
