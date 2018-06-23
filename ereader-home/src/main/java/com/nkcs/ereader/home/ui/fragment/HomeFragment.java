package com.nkcs.ereader.home.ui.fragment;

import android.Manifest;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.contract.HomeContract;
import com.nkcs.ereader.home.ui.adapter.BookAdapter;
import com.nkcs.ereader.home.ui.utils.SharedPreferenceManager;
import com.nkcs.ereader.home.ui.utils.WindowTool;

import java.io.File;
import java.util.List;

/**
 * Created by 王利通 on 2018/4/22.
 */

public class HomeFragment extends BaseFragment
        implements NavigationView.OnNavigationItemSelectedListener, HomeContract.IView {
    private DrawerLayout drawer;
    private boolean doubleBackToExitFirst = false;
    private LinearLayout bottomBtns;
    private RelativeLayout editLayout;
    private RelativeLayout originLayout;
    private ImageButton quickReadBtn;
    private ImageButton originLayoutSearchBtn;
    private AppCompatEditText searchEdit;
    private RelativeLayout searchLayout;
    private ModeSwitcher modeSwitcher = new ModeSwitcher();
    private BookAdapter adapter;
    private ImageButton menuBtn;
    public SharedPreferenceManager sharedPreferenceManager;
    private HomeContract.IPresenter presenter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onInitView() {
        sharedPreferenceManager = new SharedPreferenceManager(this.getContext());


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

        // 设置全屏/noLimit
        adjustFullWindow();
    }

    public void showMenu() {
        menuBtn.performClick();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_books);

        GridLayoutManager layoutManager = new GridLayoutManager(getHoldingActivity(), BookAdapter.GRID_COLUMNS_PER_ROW);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BookAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void adjustFullWindow() {
        // 根据用户设置决定是否全屏
        WindowTool windowTool = new WindowTool(this);
        if (sharedPreferenceManager.getSetting(SharedPreferenceManager.WINDOW_NO_LIMIT)) {
            windowTool.setNoLimitsWindow();
        } else {
            windowTool.setFullWindow();
        }
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
                adapter.sort();
            } else if (id == R.id.sort_item_by_time) {
                sharedPreferenceManager.setSetting(
                        SharedPreferenceManager.HOME_BOOK_SORT_BY_TIME, true
                );
                adapter.sort();
//            } else if (id == R.id.list_show_books) {
//                Toast.makeText(this.getContext(), "别点了，没做", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.cloud_bookshelf) {
                ARouter.getInstance().build(RouterConstant.CLOUD_PAGE).navigation();
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

    /**
     * hide popupmenu item according to configure database
     */
    private void setPopupMenuItemHide(Menu menu) {
        // 根据之前的设置，设置弹出菜单的项目
        if (sharedPreferenceManager.getSetting(SharedPreferenceManager.HOME_BOOK_SHOW_TRADITION)) {
            menu.findItem(R.id.tradition_show_books).setVisible(false);
        }/* else {
            menu.findItem(R.id.list_show_books).setVisible(false);
        }*/
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
            adapter.endSearch();
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

    @Override
    public void onStart() {
        super.onStart();
        presenter.getBooks();
    }

    @Override
    public void onGetBooks(List<Book> books) {
        adapter.addData(books);
    }

    @Override
    public void setPresenter(HomeContract.IPresenter presenter) {
        this.presenter = presenter;
    }

    class ModeSwitcher {
        Boolean isEditing = false;
        Boolean isSearching = false;

        void switchEdit(Boolean isEditing) {
            this.isEditing = isEditing;
            // 切换 recyclerView 的编辑状态
            adapter.switchEdit(isEditing);
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
                searchEdit.setText("");
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
        searchLayout = findViewById(R.id.search_layout);
        TextView editLayoutCancelBtn = findViewById(R.id.cancel_edit_btn);
        editLayoutCancelBtn.setOnClickListener(e -> {
            modeSwitcher.switchEdit(false);
        });
        originLayoutSearchBtn = findViewById(R.id.search_btn);
        originLayoutSearchBtn.setOnClickListener(e -> {
            modeSwitcher.switchSearch(true);
        });
        searchEdit = findViewById(R.id.search_edit);
        ImageButton searchLayoutCancelBtn = findViewById(R.id.search_cancel_btn);
        searchLayoutCancelBtn.setOnClickListener(e -> {
            adapter.endSearch();
            modeSwitcher.switchSearch(false);
        });
        quickReadBtn.setOnClickListener(view -> {
            Book book = null;
            for (Book b : adapter.getItems()) {
                if (b.getCreated() != null
                        && (book == null || b.getCreated().after(book.getCreated()))) {
                    book = b;
                }
            }
            if (book != null) {
                goReadPage(book);
            }
        });
        findViewById(R.id.select_all).setOnClickListener(e -> {
            if (!adapter.allSelected()) {
                adapter.selectAllOrUnselectAll(true);
            } else {
                adapter.selectAllOrUnselectAll(false);
            }
        });
        ((AppCompatEditText) findViewById(R.id.search_edit)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                adapter.search(str);
            }
        });
        findViewById(R.id.pin_top_btn).setOnClickListener(e -> {
            adapter.pinBooksTopOrCancel();
        });
        findViewById(R.id.delete).setOnClickListener(e -> {
            callWithPermissions("delete");
        });
        findViewById(R.id.share).setOnClickListener(e -> {
            callWithPermissions("share");
        });
        findViewById(R.id.detail).setOnClickListener(e -> {
            adapter.detail();
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.full_screen);
        Switch aSwitch = (Switch) item.getActionView().findViewById(R.id.switchForActionBar);
        aSwitch.setChecked(!sharedPreferenceManager.getSetting(SharedPreferenceManager.WINDOW_NO_LIMIT));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferenceManager spm = sharedPreferenceManager;
                spm.setSetting(SharedPreferenceManager.WINDOW_NO_LIMIT, !isChecked);
                adjustFullWindow();
            }
        });
    }

    @NeedsPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE},
            permissionsText = {"读写手机存储"})
    public void delete() {
        adapter.delete();
    }

    @NeedsPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE},
            permissionsText = {"读写手机存储"})
    public void share() {
        adapter.share();
    }

    public void goImportPage() {
        ARouter.getInstance().build(RouterConstant.IMPORT_PAGE).navigation();
    }

    public void goReadPage(Book book) {
        ARouter.getInstance().build(RouterConstant.READ_PAGE)
                .withLong("bookId", book.getId())
                .navigation();
    }

    public void deleteBook(List<Book> bookList, boolean deleteFile) {
        if (deleteFile) {
            for (Book book : bookList) {
                if (book.getPath() == null) {
                    Log.e("delete modal sure",
                            "错误，无路径文件 " + book.toString());
                    continue;
                }
                File file = new File(book.getPath());
                if (file.exists()) {
                    if (!file.delete()) {
                        Log.e("delete modal sure", "删除文件失败：" + file.getAbsolutePath());
                    }
                } else {
                    Log.e("delete modal sure",
                            "文件不存在 " + book.toString());
                }
            }
        }
        presenter.deleteBooks(bookList);
    }

    public void switchBookEditMode(boolean isEditing) {
        modeSwitcher.switchEdit(isEditing);
    }
}
