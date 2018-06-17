package com.nkcs.ereader.impt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkcs.ereader.impt.R;
import com.nkcs.ereader.impt.ui.adapter.base.RecyclerViewAdapter;
import com.nkcs.ereader.impt.ui.adapter.base.RecyclerViewHolder;
import com.nkcs.ereader.impt.entity.FileBean;
import com.nkcs.ereader.impt.entity.FileType;
import com.nkcs.ereader.impt.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FileAdapter extends RecyclerViewAdapter {

    public class FileHolder extends RecyclerViewHolder<FileHolder> {
        ImageView fileIcon;
        TextView fileName;
        TextView fileChildCount;
        TextView fileSize;
        CheckBox checkBox;

        public FileHolder(View view) {
            super(view);
            fileIcon = (ImageView) view.findViewById(R.id.fileIcon);
            fileName = (TextView) view.findViewById(R.id.fileName);
            fileChildCount = (TextView) view.findViewById(R.id.fileChildCount);
            fileSize = (TextView) view.findViewById(R.id.fileSize);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        }

        @Override
        public void onBindViewHolder(final FileHolder fileHolder, RecyclerViewAdapter adapter, int position) {
            FileBean fileBean = (FileBean) adapter.getItem(position);
            fileHolder.fileName.setText(fileBean.getName());

            if (fileBean.getFileType() != FileType.directory) {
                fileHolder.checkBox.setVisibility(View.VISIBLE);
                fileHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(map.keySet().contains(position)) {
                            map.put(position, b);
                        }
                        if (mCheckedChangeListener != null) {
                            mCheckedChangeListener.onCheckedChanged(position, compoundButton, b);
                        }
                    }
                });

                if (map.get(position) == null) {
                    map.put(position, false);
                }

                fileHolder.checkBox.setChecked(map.get(position));
            }

            FileType fileType = fileBean.getFileType();
            if (fileType == FileType.directory) {
                fileHolder.fileChildCount.setVisibility(View.VISIBLE);
                fileHolder.fileChildCount.setText(fileBean.getChildCount() + "项");
                fileHolder.fileSize.setVisibility(View.GONE);

            } else {
                fileHolder.fileChildCount.setVisibility(View.GONE);
                fileHolder.fileSize.setVisibility(View.VISIBLE);
                fileHolder.fileSize.setText(FileUtils.sizeToChange(fileBean.getSize()));
            }

            //设置图标
            if (fileType == FileType.directory) {
                fileHolder.fileIcon.setImageResource(R.mipmap.file_icon_dir);
//            } else if (fileType == FileType.music) {
//                fileHolder.fileIcon.setImageResource(R.mipmap.file_icon_music);
//            } else if (fileType == FileType.video) {
//                fileHolder.fileIcon.setImageResource(R.mipmap.file_icon_video);
            } else if (fileType == FileType.txt) {
                fileHolder.fileIcon.setImageResource(R.mipmap.file_icon_txt);
//            } else if (fileType == FileType.zip) {
//                fileHolder.fileIcon.setImageResource(R.mipmap.file_icon_zip);
//            } else if (fileType == FileType.image) {
//                Glide.with(fileHolder.itemView.getContext()).load(new File(fileBean.getPath())).into(fileHolder.fileIcon);
//            } else if (fileType == FileType.apk) {
//                fileHolder.fileIcon.setImageResource(R.mipmap.file_icon_apk);
            } else {
                fileHolder.fileIcon.setImageResource(R.mipmap.file_icon_other);
            }
        }
    }


    private Context context;
    private List<FileBean> list;
    private List<FileBean> alist;
    private LayoutInflater mLayoutInflater;
    private Map<Integer, Boolean> map = new ConcurrentHashMap<>();
    private CheckedChangeListener mCheckedChangeListener;

    public static final int SORT_TIME = 102;
    public static final int SORT_NAME = 103;
    public static final int SORT_SIZE = 100;
    public static final int SORT_RECOVER = -1;
    protected List<FileBean> sortCache;

    public class sNameComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            FileBean f1 = (FileBean) o1;
            FileBean f2 = (FileBean) o2;
            if ((f1.getFileType() == FileType.directory && f2.getFileType() == FileType.directory)
                    || (f1.getFileType() != FileType.directory && f2.getFileType() != FileType.directory)) {
                if (f1.getName() != null && f2.getName() != null) {
                    return (f1.getName().compareTo(f2.getName()));
                } else {
                    return 0;
                }
            } else if (f1.getFileType() == FileType.directory) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public class sSizeComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            FileBean f1 = (FileBean) o1;
            FileBean f2 = (FileBean) o2;
            if ((f1.getFileType() == FileType.directory && f2.getFileType() == FileType.directory)
                    || (f1.getFileType() != FileType.directory && f2.getFileType() != FileType.directory)) {
                if (f2.getSize() == f1.getSize()) {
                    return 0;
                } else if (f2.getSize() > f1.getSize()) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (f1.getFileType() == FileType.directory) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public class sTimeComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            FileBean f1 = (FileBean) o1;
            FileBean f2 = (FileBean) o2;
            if ((f1.getFileType() == FileType.directory && f2.getFileType() == FileType.directory)
                    || (f1.getFileType() != FileType.directory && f2.getFileType() != FileType.directory)) {
                if (f2.getTime() == f1.getTime()) {
                    return 0;
                } else if (f2.getTime() > f1.getTime()) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (f1.getFileType() == FileType.directory) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public @interface SortType {

    }

    public void sort(@SortType int type) {
        if (sortCache == null) {
            sortCache = new ArrayList<>();
            sortCache.addAll(list);
        }
        if (type == SORT_RECOVER) {
            list.addAll(sortCache);
            notifyDataSetChanged();
            return;
        }
        if (sortCache.size() != list.size()) {
            sortCache.clear();
            sortCache.addAll(list);
        }

        switch (type) {
            case SORT_TIME:
                Collections.sort(list, new sTimeComparator());
                break;
            case SORT_NAME:
                Collections.sort(list, new sNameComparator());
                break;
            case SORT_SIZE:
                Collections.sort(list, new sSizeComparator());
                break;
            default://SORT_LAST_FOLLOW_TIME 默认此序
                Collections.sort(list, new sNameComparator());
                break;
        }

        //for (int i = 0; i < mAdapterListData.size(); i++) {
        //    System.out.println(mAdapterListData.get(i).getCreateTime() + "    :  " + sortCache.get(i).getCreateTime());
        //}
        notifyDataSetChanged();
    }

    public FileAdapter(Context context, List<FileBean> list) {
        this.context = context;
        this.list = list;
        initMap();
        mLayoutInflater = LayoutInflater.from(context);

    }

    private void initMap() {
        if (this.list != null) {
            for (int i = 0; i < this.list.size(); i++) {
                if(list.get(i).getFileType() != FileType.directory && !map.keySet().contains(i)) {
                    map.put(i, false);
                }
            }
        }
    }

    //全选
    public void checkAll() {

        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            map.put((Integer) entry.getKey(), true);
        }

        notifyDataSetChanged();
    }

    //取消
    public void cancel() {

        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            map.put((Integer) entry.getKey(), false);
        }
        notifyDataSetChanged();
    }

    //选择的数目
    public int getCheckNum() {
        int num = 0;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if ((Boolean) entry.getValue()) {
                num++;
            }
        }
        return num;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == -1) {
            view = mLayoutInflater.inflate(R.layout.list_item_dist, parent, false);
            return new FileHolder(view);
        } else if (viewType == 0) {
            view = mLayoutInflater.inflate(R.layout.list_item_file, parent, false);
            return new FileHolder(view);
        } else {
            view = mLayoutInflater.inflate(R.layout.list_item_line, parent, false);
            return new LineHolder(view);
        }
    }

    @Override
    public void onBindViewHolders(final RecyclerView.ViewHolder holder,
                                  final int position) {
        if (holder instanceof FileHolder) {
            FileHolder fileHolder = (FileHolder) holder;
            fileHolder.onBindViewHolder(fileHolder, this, position);

        } else if (holder instanceof LineHolder) {
            LineHolder lineHolder = (LineHolder) holder;
            lineHolder.onBindViewHolder(lineHolder, this, position);
        }
    }

    @Override
    public Object getAdapterData() {
        return list;
    }

    public List<FileBean> getCheckFiles() {
        List<FileBean> files = new ArrayList<>();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if ((Boolean) entry.getValue()) {
                FileBean file = getItem((Integer) entry.getKey());
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public FileBean getItem(int positon) {
        return list.get(positon);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getHolderType();
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public void refresh(List<FileBean> list) {
        cancel();
        this.list = list;
        initMap();
        notifyDataSetChanged();
    }

    public void addToList(FileBean e){
        this.list.add(e);

        initMap();
    }

    public void tmprefresh(List<FileBean> list) {
        cancel();
        alist = this.list;
        this.list = list;
        notifyDataSetChanged();
        this.list = alist;
    }


    public void setCheckedChangeListener(CheckedChangeListener checkedChangeListener) {
        mCheckedChangeListener = checkedChangeListener;
    }

    public interface CheckedChangeListener {
        void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked);
    }
}
