package com.nkcs.ereader.home.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.utils.WindowTool;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private WindowTool windowTool;
    private DrawerLayout drawer;
    private boolean isFullScreen = false;
    private boolean doubleBackToExitFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        windowTool = new WindowTool(this);
        windowTool.setNoLimitsWindow();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.drawer_open, R.string.drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.slide_bar_btn).setOnClickListener(view -> drawer.openDrawer(Gravity.START));

        ImageButton quick = findViewById(R.id.quick_read_btn);
        quick.setOnClickListener(view -> {
            if (!isFullScreen) {
                windowTool.setFullWindow();
            } else {
                windowTool.setNoLimitsWindow();
            }
            isFullScreen = !isFullScreen;
        });

        ImageButton menuBtn = findViewById(R.id.menu_btn);
        ImageButton searchBtn = findViewById(R.id.search_btn);
        menuBtn.setOnClickListener(view -> showPopupMenu(view, searchBtn));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitFirst) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitFirst = true;
            Toast.makeText(this, "再次按下返回键退出应用程序", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitFirst = false, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void showPopupMenu(View view, View standByView) {
        // the view is which the popup menu relays on
        PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(this, view, Gravity.END);
        } else {
            popupMenu = new PopupMenu(this, standByView);
        }
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            return true;
        });
        popupMenu.setOnDismissListener(menu -> {
        });

    }
}
