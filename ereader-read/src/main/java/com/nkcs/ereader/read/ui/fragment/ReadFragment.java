package com.nkcs.ereader.read.ui.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.BrightnessUtils;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.ScreenUtils;
import com.nkcs.ereader.base.utils.StringUtils;
import com.nkcs.ereader.base.utils.SystemBarUtils;
import com.nkcs.ereader.base.utils.ToastUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.contract.ReadContract;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.entity.RetrievalResult;
import com.nkcs.ereader.read.ui.adapter.CatalogueAdapter;
import com.nkcs.ereader.read.ui.widget.ReadBrightnessDialog;
import com.nkcs.ereader.read.ui.widget.ReadSettingDialog;
import com.nkcs.ereader.read.ui.widget.read.PageStyle;
import com.nkcs.ereader.read.ui.widget.read.PageView;
import com.nkcs.ereader.read.ui.widget.read.ReadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class ReadFragment extends BaseFragment implements ReadContract.IView {

    private Long bookId = 1L;
    private ReadContract.IPresenter mPresenter;

    private DrawerLayout mDlSlide;
    private int mDrawerDirection = Gravity.START;

    private ReadView mRvPage;
    private RelativeLayout mRlMask;
    private RelativeLayout mRlTopMenu;
    private LinearLayout mLlBottomMenu;

    private ImageView mIvBack;
    private ImageView mIvMore;

    private TextView mTvChapterTip;
    private TextView mTvPrevChapter;
    private SeekBar mSbChapterProgress;
    private TextView mTvNextChapter;

    private TextView mTvCatalogue;
    private TextView mTvBrightness;
    private TextView mTvMode;
    private TextView mTvSetting;

    private ReadBrightnessDialog mBrightnessDialog;
    private ReadSettingDialog mSettingDialog;

    private TextView mTvTitle;
    private RecyclerView mRvCatalogue;
    private CatalogueAdapter mCatalogueAdapter;

    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;

    /**
     * 接收电池信息和时间更新的广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mRvPage.updateBattery(level);
            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mRvPage.updateTime();
            }
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_read;
    }

    @Override
    protected void onInitView() {
        // 侧滑栏
        mDlSlide = findViewById(R.id.read_dl_slide);
        mDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDlSlide.setFocusableInTouchMode(false);

        // 阅读区
        mRvPage = findViewById(R.id.read_rv_page);
        mRvPage.setOnPageTouchListener(new ReadView.OnPageTouchListener() {

            @Override
            public void onCenterTouch() {
                toggleMenu(true);
            }

            @Override
            public void onPrevPage() {}

            @Override
            public void onNextPage() {}
        });
        // 蒙版
        mRlMask = findViewById(R.id.read_rl_mask);
        mRlMask.setOnClickListener(v -> {
            toggleMenu(true);
        });
        // 顶部菜单
        mRlTopMenu = findViewById(R.id.read_rl_top_menu);
        if (Build.VERSION.SDK_INT >= 19) {
            mRlTopMenu.setPadding(mRlTopMenu.getPaddingLeft(),
                    mRlTopMenu.getPaddingTop() + ScreenUtils.getStatusBarHeight(),
                    mRlTopMenu.getPaddingRight(), mRlTopMenu.getPaddingBottom());
        }
        // 底部菜单
        mLlBottomMenu = findViewById(R.id.read_ll_bottom_menu);

        // 后退
        mIvBack = findViewById(R.id.read_iv_back);
        mIvBack.setOnClickListener(v -> {
            removeFragment();
        });
        // 更多选项
        mIvMore = findViewById(R.id.read_iv_more);
        mIvMore.setOnClickListener(v -> {
            showPopupWindow(v);
        });

        mTvChapterTip = findViewById(R.id.read_tv_chapter_tip);
        // 上一章
        mTvPrevChapter = findViewById(R.id.read_tv_prev_chapter);
        mTvPrevChapter.setOnClickListener(v -> {
            int prevChapter = mCatalogueAdapter.getCurChapter() - 1;
            if (prevChapter >= 0) {
                mRvPage.skipToChapter(prevChapter);
            }
        });
        // 章节滚动条
        mSbChapterProgress = findViewById(R.id.read_sb_chapter_progress);
        mSbChapterProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mLlBottomMenu.getVisibility() == View.VISIBLE) {
                    String tip = mCatalogueAdapter.getItem(progress).getTitle() + "\n"
                            + (progress + 1) + "/" + mCatalogueAdapter.getItemCount();
                    mTvChapterTip.setText(tip);
                    mTvChapterTip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int pos = mSbChapterProgress.getProgress();
                if (pos != mCatalogueAdapter.getCurChapter()) {
                    mRvPage.skipToChapter(pos);
                }
                mTvChapterTip.setVisibility(View.GONE);
            }
        });
        // 下一章
        mTvNextChapter = findViewById(R.id.read_tv_next_chapter);
        mTvNextChapter.setOnClickListener(v -> {
            int nextChapter = mCatalogueAdapter.getCurChapter() + 1;
            if (nextChapter < mCatalogueAdapter.getItemCount()) {
                mRvPage.skipToChapter(nextChapter);
            }
        });

        // 目录
        mTvCatalogue = findViewById(R.id.read_tv_catalogue);
        mTvCatalogue.setOnClickListener(v -> {
            toggleMenu(true);
            mCatalogueAdapter.scrollToSelected();
            mDlSlide.openDrawer(mDrawerDirection);
        });
        // 亮度
        mTvBrightness = findViewById(R.id.read_tv_brightness);
        mTvBrightness.setOnClickListener(v -> {
            toggleMenu(false);
            toggleBrightnessDialog();
        });
        // 日间/夜间模式
        mTvMode = findViewById(R.id.read_tv_mode);
        mTvMode.setOnClickListener(v -> {
            mPresenter.changeNightMode(!mRvPage.isNightMode());
        });
        // 设置
        mTvSetting = findViewById(R.id.read_tv_setting);
        mTvSetting.setOnClickListener(v -> {
            toggleMenu(false);
            toggleSettingDialog();
        });

        // 亮度
        mBrightnessDialog = new ReadBrightnessDialog(getHoldingActivity());
        mBrightnessDialog.setOnBrightnessChangeListener((brightness, systemBrightness) -> {
            mPresenter.changeBrightness(brightness, systemBrightness);
        });
        mBrightnessDialog.setOnDismissListener((dialog) -> {
            hideSystemBar();
        });
        // 设置
        mSettingDialog = new ReadSettingDialog(getHoldingActivity());
        mSettingDialog.setOnSettingChangeListener(new ReadSettingDialog.OnSettingChangeListener() {
            @Override
            public void onTextSizeChanged(int textSize) {
                mPresenter.changeTextSize(textSize);
            }

            @Override
            public void onPageModeChanged(PageView.PageMode pageMode) {
                mPresenter.changePageMode(pageMode);
            }

            @Override
            public void onPageStyleChanged(PageStyle pageStyle) {
                mPresenter.changePageStyle(pageStyle);
            }
        });
        mSettingDialog.setOnDismissListener((dialog) -> {
            hideSystemBar();
        });

        mTvTitle = findViewById(R.id.read_tv_title);
        mRvCatalogue = findViewById(R.id.read_rv_catalogue);
        mRvCatalogue.setLayoutManager(new LinearLayoutManager(getHoldingActivity()));
        mCatalogueAdapter = new CatalogueAdapter();
        mRvCatalogue.setAdapter(mCatalogueAdapter);
        mCatalogueAdapter.setOnItemClickListener((v, chapter, position) -> {
            mRvPage.skipToChapter(chapter.getSequence());
            onBackPressed();
        });

        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        getHoldingActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onLoadData() {
        mPresenter.getReadConfig();
        callWithPermissions("getBook");
    }

    @NeedsPermission(permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE },
            permissionsText = { "读写手机存储" })
    public void getBook() {
        mPresenter.getBook(bookId);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRlTopMenu.getVisibility() != View.VISIBLE && !mBrightnessDialog.isShowing()
                && !mSettingDialog.isShowing()) {
            hideSystemBar();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getHoldingActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onBackPressed() {
        if (mRlTopMenu.getVisibility() == View.VISIBLE) {
            toggleMenu(true);
            return true;
        } else if (mDlSlide.isDrawerOpen(mDrawerDirection)) {
            mDlSlide.closeDrawer(mDrawerDirection);
            return true;
        }

        return false;
    }

    @Override
    public void setPresenter(ReadContract.IPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onGetBook(Book book) {
        mTvTitle.setText(book.getTitle());
        mCatalogueAdapter.refreshItems(book.getChapterList());
        mRvPage.setBook(book, new ReadView.OnBookChangeListener() {

            @Override
            public void onFormatChapters(Book book, List<Chapter> chapterList) {
                mPresenter.formatChapters(book, chapterList);
            }

            @Override
            public void onChapterChange(Book book, Chapter chapter) {
                mCatalogueAdapter.setCurChapter(chapter.getSequence());
                mSbChapterProgress.setProgress(chapter.getSequence());
                mPresenter.changeLastReadChapter(book, chapter);
                mTvChapterTip.setVisibility(View.GONE);
            }
        });
        mSbChapterProgress.setMax(book.getChapterList().size() - 1);
        mSbChapterProgress.setProgress(book.getLastReadChapter() != null ? book.getLastReadChapter() : 0);
    }

    @Override
    public void onChangeBrightness(Config config) {
        if (config.isSystemBrightness()) {
            BrightnessUtils.setBrightness(getHoldingActivity(), BrightnessUtils.getScreenBrightness(getHoldingActivity()));
        } else {
            BrightnessUtils.setBrightness(getHoldingActivity(), config.getBrightness());
        }
    }

    @Override
    public void onChangeNightMode(Config config) {
        toggleNightMode(config.isNightMode());
        mRvPage.setNightMode(config.isNightMode());
    }

    @Override
    public void onChangeTextSize(Config config) {
        mRvPage.setTextSize(config.getTextSize());
    }

    @Override
    public void onChangePageMode(Config config) {
        mRvPage.setPageMode(config.getPageMode());
    }

    @Override
    public void onChangePageStyle(Config config) {
        mRvPage.setPageStyle(config.getPageStyle());
    }

    @Override
    public void onGetReadConfig(Config config) {
        mBrightnessDialog.setBrightness(config.getBrightness());
        mBrightnessDialog.setSystemBrightness(config.isSystemBrightness());
        onChangeBrightness(config);

        mSettingDialog.setTextSize(config.getTextSize());
        onChangeTextSize(config);
        mSettingDialog.setPageMode(config.getPageMode());
        onChangePageMode(config);
        mSettingDialog.setPageStyle(config.getPageStyle());
        onChangePageStyle(config);

        toggleNightMode(config.isNightMode());
        mRvPage.setNightMode(config.isNightMode());

        mRvPage.setTextSize(config.getTextSize());
        mRvPage.setPageStyle(config.getPageStyle());
        mRvPage.setPageMode(config.getPageMode());
        mRvPage.setPageStyle(config.getPageStyle());
    }

    @Override
    public void showTips(String text) {
        ToastUtils.showText(text);
    }

    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();

        if (mRlTopMenu.getVisibility() == View.VISIBLE) {
            //关闭
            mRlMask.setVisibility(View.GONE);
            mRlTopMenu.startAnimation(mTopOutAnim);
            mLlBottomMenu.startAnimation(mBottomOutAnim);
            mRlTopMenu.setVisibility(View.GONE);
            mLlBottomMenu.setVisibility(View.GONE);
            mTvChapterTip.setVisibility(View.GONE);

            if (hideStatusBar) {
                hideSystemBar();
            }
        } else {
            mRlMask.setVisibility(View.VISIBLE);
            mRlTopMenu.setVisibility(View.VISIBLE);
            mLlBottomMenu.setVisibility(View.VISIBLE);
            mRlTopMenu.startAnimation(mTopInAnim);
            mLlBottomMenu.startAnimation(mBottomInAnim);
            showSystemBar();
        }
    }

    private void initMenuAnim() {
        if (mTopInAnim != null) {
            return;
        }
        mTopInAnim = AnimationUtils.loadAnimation(getHoldingActivity(), R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(getHoldingActivity(), R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(getHoldingActivity(), R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(getHoldingActivity(), R.anim.slide_bottom_out);
    }

    private void toggleBrightnessDialog() {
        if (mBrightnessDialog.isShowing()) {
            mBrightnessDialog.dismiss();
            hideSystemBar();
        } else {
            mBrightnessDialog.show();
            showSystemBar();
        }
    }

    private void toggleNightMode(boolean nightMode) {
        if (!nightMode) {
            mTvMode.setText(StringUtils.getString(R.string.read_menu_night));
            Drawable drawable = ContextCompat.getDrawable(getHoldingActivity(), R.drawable.ic_read_menu_night);
            mTvMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else {
            mTvMode.setText(StringUtils.getString(R.string.read_menu_day));
            Drawable drawable = ContextCompat.getDrawable(getHoldingActivity(), R.drawable.ic_read_menu_day);
            mTvMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    private void toggleSettingDialog() {
        if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            hideSystemBar();
        } else {
            mSettingDialog.show();
            showSystemBar();
        }
    }

    private void showSystemBar() {
        SystemBarUtils.showUnStableStatusBar(getHoldingActivity());
    }

    private void hideSystemBar() {
        SystemBarUtils.hideStableStatusBar(getHoldingActivity());
    }

    private void showPopupWindow(View view) {
        View popupView = getHoldingActivity().getLayoutInflater().inflate(R.layout.popup_read_more, null);
        PopupWindow window = new PopupWindow(popupView, 280, 200);
        window.setBackgroundDrawable(ContextCompat.getDrawable(getHoldingActivity(), R.color.read_menu_bg));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAsDropDown(view, 0, 20);

        TextView tvFulltextRetrieval = popupView.findViewById(R.id.read_tv_fulltext_retrieval);
        tvFulltextRetrieval.setOnClickListener(v -> {
            window.dismiss();
            Book book = mRvPage.getBook();
            if (book == null) {
                return;
            }
            ARouter.getInstance().build(RouterConstant.READ_SEARCH_PAGE)
                    .withLong("bookId", book.getId())
                    .navigation();
        });
        TextView tvShare = popupView.findViewById(R.id.read_tv_share);
        tvShare.setOnClickListener(v -> {
            window.dismiss();
            Book book = mRvPage.getBook();
            if (book == null) {
                return;
            }
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(book.getPath())));
            share.setType("*/*");
            startActivity(Intent.createChooser(share, "分享"));
        });
    }

    // region EventBus 相关

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(RetrievalResult event) {
        mRvPage.skipToChapter(event.getChapter().getSequence());
    }

    // endregion
}
