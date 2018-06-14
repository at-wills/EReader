package com.nkcs.ereader.home.ui.fragment;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.adaptor.BookAdapter;
import com.nkcs.ereader.home.ui.utils.SharedPreferenceManager;
import com.nkcs.ereader.home.ui.utils.WindowTool;

/**
 * Created by 王利通 on 2018/4/22.
 */

public class HomeFragment extends BaseFragment
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private boolean doubleBackToExitFirst = false;
    private LinearLayout bottomBtns;
    private RelativeLayout editLayout;
    private RelativeLayout originLayout;
    private TextView editLayoutCancelBtn;
    private ImageButton quickReadBtn;
    private ImageButton originLayoutSearchBtn;
    private ImageButton searchLayoutCancelBtn;
    private AppCompatEditText searchEdit;
    private RelativeLayout searchLayout;
    ModeSwitcher modeSwitcher = new ModeSwitcher();
    BookAdapter adapter;
    GridLayoutManager layoutManager;
    ImageButton menuBtn;
    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onInitView() {
        sharedPreferenceManager = new SharedPreferenceManager(this.getContext());

        // 根据用户设置决定是否全屏
        WindowTool windowTool = new WindowTool(this);
        if (sharedPreferenceManager.getSetting(SharedPreferenceManager.WINDOW_NO_LIMIT)) {
            windowTool.setNoLimitsWindow();
        } else {
            windowTool.setFullWindow();
        }

        // 初始化 recyclerView
        initRecyclerView();

        // 抽屉侧边栏
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getHoldingActivity(),
                drawer, null, R.string.drawer_open, R.string.drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        findViewById(R.id.slide_bar_btn).setOnClickListener(view -> drawer.openDrawer(Gravity.START));

        // 初始化各组件
        findViews();

        // 初始化菜单
        menuBtn = findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(view -> showPopupMenu(view, originLayoutSearchBtn));
    }

    public void showMenu() {
        menuBtn.performClick();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recycler_books);

        layoutManager = new GridLayoutManager(getHoldingActivity(), BookAdapter.GRID_COLUMNS);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BookAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter.addData();
    }

    private void showPopupMenu(View view, View standByView) {
        // the view is which the popup menu relays on
        PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(getHoldingActivity(), view, Gravity.END);
        } else {
            popupMenu = new PopupMenu(getHoldingActivity(), standByView);
        }
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        setPopupMenuItemHide(popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.import_item) {
                goImportPage();
            } else if (id == R.id.sort_item_by_name) {
                sharedPreferenceManager.setSetting(
                        SharedPreferenceManager.HOME_BOOK_SORT_BY_TIME, false);
            } else if (id == R.id.sort_item_by_time) {
                sharedPreferenceManager.setSetting(
                        SharedPreferenceManager.HOME_BOOK_SORT_BY_TIME, true
                );
            } else if (id == R.id.list_show_books) {
                sharedPreferenceManager.setSetting(
                        SharedPreferenceManager.HOME_BOOK_SHOW_TRADITION, false
                );
            } else if (id == R.id.tradition_show_books) {
                sharedPreferenceManager.setSetting(
                        SharedPreferenceManager.HOME_BOOK_SHOW_TRADITION, true);
            } else if (id == R.id.manage_item) {
                modeSwitcher.switchEdit(true);
            }
            return true;
        });
        popupMenu.show();
    }

    /*
     * hide popupmenu item according to configure database */
    private void setPopupMenuItemHide(Menu menu) {
        // 根据之前的设置，设置弹出菜单的项目
        if (sharedPreferenceManager.getSetting(SharedPreferenceManager.HOME_BOOK_SHOW_TRADITION)) {
            menu.findItem(R.id.tradition_show_books).setVisible(false);
        } else {
            menu.findItem(R.id.list_show_books).setVisible(false);
        }
        if (sharedPreferenceManager.getSetting(SharedPreferenceManager.HOME_BOOK_SORT_BY_TIME)) {
            menu.findItem(R.id.sort_item_by_time).setVisible(false);
        } else {
            menu.findItem(R.id.sort_item_by_name).setVisible(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (modeSwitcher.isEditing) {
            modeSwitcher.switchEdit(false);
            return true;
        } else if (modeSwitcher.isSearching) {
            modeSwitcher.switchSearch(false);
        } else {
            if (doubleBackToExitFirst) {
                return false;
            }
            doubleBackToExitFirst = true;
            Toast.makeText(getHoldingActivity(),
                    "再次按下返回键退出应用程序", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitFirst = false, 2000);
        }
        return true;
    }

    class ModeSwitcher {
        Boolean isEditing = false;
        Boolean isSearching = false;

        void switchEdit(Boolean isEditing) {
            this.isEditing = isEditing;
            if (isEditing) {
                hideView(quickReadBtn).showView(editLayout)
                        .showView(bottomBtns).hideView(originLayout);
            } else {
                showView(originLayout).hideView(editLayout)
                        .hideView(bottomBtns).showView(quickReadBtn);
            }
        }

        void switchSearch(Boolean isSearching) {
            this.isSearching = isSearching;

            if (isSearching) {
                showView(searchLayout).hideView(originLayout);
            } else {
                showView(originLayout).hideView(searchLayout);
            }

            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            // 开关输入法
            if (isSearching) {
                searchEdit.requestFocus();
                if (imm != null) {
                    imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);
                }
            } else {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                }
            }
        }

        private ModeSwitcher showView(View view) {
            view.setVisibility(View.VISIBLE);
            return this;
        }

        private ModeSwitcher hideView(View view) {
            view.setVisibility(View.INVISIBLE);
            return this;
        }
    }

    void findViews() {
        bottomBtns = findViewById(R.id.bottom_btns);
        editLayout = findViewById(R.id.edit_layout);
        originLayout = findViewById(R.id.origin_layout);
        quickReadBtn = findViewById(R.id.quick_read_btn);
        editLayoutCancelBtn = findViewById(R.id.cancel_edit_btn);
        searchLayout = findViewById(R.id.search_layout);
        editLayoutCancelBtn = findViewById(R.id.cancel_edit_btn);
        editLayoutCancelBtn.setOnClickListener(e -> modeSwitcher.switchEdit(false));
        originLayoutSearchBtn = findViewById(R.id.search_btn);
        originLayoutSearchBtn.setOnClickListener(e -> modeSwitcher.switchSearch(true));
        searchEdit = findViewById(R.id.search_edit);
        searchLayoutCancelBtn = findViewById(R.id.search_cancel_btn);
        searchLayoutCancelBtn.setOnClickListener(e -> {
            searchEdit.setText("");
            modeSwitcher.switchSearch(false);
        });
        quickReadBtn.setOnClickListener(view -> {
            Toast.makeText(this.getContext(), "打开最近的书", Toast.LENGTH_SHORT).show();
        });
    }

    public void goImportPage() {
        Toast.makeText(this.getContext(), "跳转到导入页面", Toast.LENGTH_SHORT).show();
    }

    public void turnOnBookEditMode() {
        modeSwitcher.switchEdit(true);
    }
}
