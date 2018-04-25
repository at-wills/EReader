package com.nkcs.ereader.home.ui.fragment;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.utils.WindowTool;

/**
 * Created by 王利通 on 2018/4/22.
 */

public class HomeFragment extends BaseFragment
        implements NavigationView.OnNavigationItemSelectedListener {
    private WindowTool windowTool;
    private DrawerLayout drawer;
    private boolean doubleBackToExitFirst = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onInitView() {
        windowTool = new WindowTool(this);
        windowTool.setNoLimitsWindow();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getHoldingActivity(), drawer, null, R.string.drawer_open, R.string.drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.slide_bar_btn).setOnClickListener(view -> drawer.openDrawer(Gravity.START));

        ImageButton quick = findViewById(R.id.quick_read_btn);
        quick.setOnClickListener(view -> {
            ManageFragment manageFragment = new ManageFragment();
            getHoldingActivity().addFragment(manageFragment);
        });

        ImageButton menuBtn = findViewById(R.id.menu_btn);
        ImageButton searchBtn = findViewById(R.id.search_btn);
        menuBtn.setOnClickListener(view -> showPopupMenu(view, searchBtn));
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
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            return true;
        });
        popupMenu.setOnDismissListener(menu -> {
        });
    }

    /*
    * hide popupmenu item according to configure database */
    private void setPopupMenuItemHide(Menu menu) {
        //todo
        menu.findItem(R.id.sort_item_by_name).setVisible(false);
        menu.findItem(R.id.list_show_books).setVisible(false);
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
        } else {
            if (doubleBackToExitFirst) {
                return false;
            }
            doubleBackToExitFirst = true;
            Toast.makeText(getHoldingActivity(), "再次按下返回键退出应用程序", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitFirst = false, 2000);
        }
        return true;
    }
}
