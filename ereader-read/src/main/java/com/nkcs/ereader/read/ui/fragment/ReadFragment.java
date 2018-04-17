package com.nkcs.ereader.read.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.BrightnessUtils;
import com.nkcs.ereader.base.utils.ScreenUtils;
import com.nkcs.ereader.base.utils.StringUtils;
import com.nkcs.ereader.base.utils.SystemBarUtils;
import com.nkcs.ereader.base.utils.ToastUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.contract.ReadContract;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.ui.adapter.CatalogueAdapter;
import com.nkcs.ereader.read.ui.widget.ReadBrightnessDialog;
import com.nkcs.ereader.read.ui.widget.ReadView;

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

    private TextView mTvCatalogue;
    private TextView mTvBrightness;
    private TextView mTvMode;
    private TextView mTvSetting;

    private ReadBrightnessDialog mBrightnessDialog;

    private TextView mTvTitle;
    private RecyclerView mRvCatalogue;
    private CatalogueAdapter mCatalogueAdapter;

    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;

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
        mRvPage.setTouchListener(new ReadView.TouchListener() {

            @Override
            public void center() {
                toggleMenu(true);
            }

            @Override
            public void prePage() {

            }

            @Override
            public void nextPage() {

            }

            @Override
            public void cancel() {

            }
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

        // 目录
        mTvCatalogue = findViewById(R.id.read_tv_catalogue);
        mTvCatalogue.setOnClickListener(v -> {
            toggleMenu(true);
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

        // 亮度
        mBrightnessDialog = new ReadBrightnessDialog(getHoldingActivity());
        mBrightnessDialog.setOnBrightnessChangeListener((brightness, systemBrightness) -> {
            mPresenter.changeBrightness(brightness, systemBrightness);
        });
        mBrightnessDialog.setOnDismissListener((dialog) -> {
            hideSystemBar();
        });

        mTvTitle = findViewById(R.id.read_tv_title);
        mRvCatalogue = findViewById(R.id.read_rv_catalogue);
        mRvCatalogue.setLayoutManager(new LinearLayoutManager(getHoldingActivity()));
        mCatalogueAdapter = new CatalogueAdapter();
        mRvCatalogue.setAdapter(mCatalogueAdapter);

        hideSystemBar();
    }

    @Override
    protected void onLoadData() {
        mPresenter.getBook(bookId);
        mPresenter.getReadConfig();
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
    public void onGetReadConfig(Config config) {
        mBrightnessDialog.setBrightness(config.getBrightness());
        mBrightnessDialog.setSystemBrightness(config.isSystemBrightness());
        onChangeBrightness(config);

        // night_mode需在page_style之后
        toggleNightMode(config.isNightMode());
        mRvPage.setNightMode(config.isNightMode());

        mRvPage.setTextSize(config.getTextSize());
        mRvPage.setPageStyle(config.getPageStyle());
        mRvPage.setPageMode(config.getPageMode());
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

    private void showSystemBar() {
        SystemBarUtils.showUnStableStatusBar(getHoldingActivity());
    }

    private void hideSystemBar() {
        SystemBarUtils.hideStableStatusBar(getHoldingActivity());
    }
}
