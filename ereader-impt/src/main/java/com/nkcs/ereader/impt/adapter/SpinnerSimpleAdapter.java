package com.nkcs.ereader.impt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nkcs.ereader.impt.R;

/**
 * Created by futanwei on 2018/6/10.
 */

public class SpinnerSimpleAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] mStringArray;
    private Drawable mDropDown;

    public SpinnerSimpleAdapter(Context context, String[] array) {
        super(context, android.R.layout.simple_spinner_item, array);
        mContext = context;
        mStringArray = array;
        mDropDown = mContext.getResources().getDrawable(R.mipmap.drop_icon_down);
        mDropDown.setBounds(10, 0, 48, 48);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //修改Spinner展开后的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextColor(Color.BLACK);

        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 修改Spinner选择后结果的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextSize(18f);
        tv.setTextColor(Color.WHITE);
        tv.setCompoundDrawables(null, null, mDropDown, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tv.getLayoutParams());
        lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        tv.setLayoutParams(lp);
        return convertView;
    }
}
