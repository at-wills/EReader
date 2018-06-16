package com.nkcs.ereader.impt.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.nkcs.ereader.impt.R;
import com.nkcs.ereader.impt.ui.adapter.base.RecyclerViewAdapter;
import com.nkcs.ereader.impt.ui.adapter.base.RecyclerViewHolder;
import com.nkcs.ereader.impt.entity.TitlePath;

public class TitleHolder extends RecyclerViewHolder<TitleHolder> {

    TextView textView ;

    public TitleHolder(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.title_Name );
    }

    @Override
    public void onBindViewHolder(TitleHolder lineHolder, RecyclerViewAdapter adapter, int position) {
        TitlePath titlePath = (TitlePath) adapter.getItem( position );
        lineHolder.textView.setText( titlePath.getNameState() );
    }
}
