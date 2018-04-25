package com.nkcs.ereader.home.ui.fragment;

import android.widget.TextView;

import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.home.R;

/**
 * Created by 王利通 on 2018/4/24.
 */

public class ManageFragment extends BaseFragment {
    TextView selectAllText;
    TextView pinTopText;
    TextView deleteText;
    TextView shareText;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_manage;
    }

    @Override
    protected void onInitView() {
        selectAllText = findViewById(R.id.select_all);
        pinTopText = findViewById(R.id.pin_top);
        deleteText = findViewById(R.id.delete);
        shareText = findViewById(R.id.share);
    }

}
