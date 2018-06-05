package com.nkcs.ereader.impt.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.impt.R;
import com.nkcs.ereader.impt.adapter.FileAdapter;
import com.nkcs.ereader.impt.adapter.TitleAdapter;
import com.nkcs.ereader.impt.adapter.base.RecyclerViewAdapter;
import com.nkcs.ereader.impt.bean.FileBean;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.nkcs.ereader.impt.bean.FileType;
import com.nkcs.ereader.impt.bean.TitlePath;
import com.nkcs.ereader.impt.util.FileUtil;


public class ImptFragment extends BaseFragment {
    private RecyclerView title_recycler_view ;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private FileAdapter txtAdapter;
    private FileAdapter tmpAdapter;
    private List<FileBean> beanList = new ArrayList<>();
    private List<FileBean> searchTxtList = new ArrayList<>();
    private List<File>  forsearchList = new ArrayList<>();
    private List<FileBean> tmpList = new ArrayList<>();
    private File rootFile ;
    private LinearLayout empty_rel ;
    private int PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 100 ;
    private String rootPath ;
    private TitleAdapter titleAdapter ;
    public  TextView numofchecked;
    private Button btnChooseAll;
    private Button btnAddFile;
    private Spinner spinnerforimpt;
    private ProgressDialog mProgressDialog;
    private Activity currActivity;
    private String data;
    private ImageButton btnSearch;
    private ImageButton btnList;
    private TextView sortTime;
    private TextView sortName;
    private TextView sortSize;


    private EditText searchEdit;
    private ImageButton btnBack;
    private int status = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_impt;
    }

    private void saveBookList(){
        List<FileBean> files;
        if(status==1){
            files = tmpAdapter.getCheckFiles();
        }
        else if(data.equals("智能浏览")) {
            files = txtAdapter.getCheckFiles();
        }
        else {
            files = fileAdapter.getCheckFiles();
        }
        if (files.size() > 0) {
            for (FileBean file : files) {
                String name = file.getName();
                /*

                  这里是接口！！！！！！！！！！！！！！！！！！！！！
                 */
                numofchecked.setText(name);
            }
        }
    }

    @Override
    protected void onInitView() {
        currActivity = this.getActivity();
        title_recycler_view = (RecyclerView) findViewById(R.id.title_recycler_view);
        title_recycler_view.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL , false ));
        titleAdapter = new TitleAdapter( this.getActivity() , new ArrayList<TitlePath>() ) ;
        title_recycler_view.setAdapter( titleAdapter );
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fileAdapter = new FileAdapter(this.getActivity(), beanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(fileAdapter);
        txtAdapter = new FileAdapter(this.getActivity(),searchTxtList);
        tmpAdapter = new FileAdapter(this.getActivity(),tmpList);

        empty_rel = (LinearLayout) findViewById( R.id.empty_rel );
        numofchecked = (TextView)findViewById(R.id.numofchecked);
        btnChooseAll = (Button)findViewById(R.id.btnChooseAll);
        btnAddFile = (Button)findViewById(R.id.btnAddFile);
        spinnerforimpt = (Spinner)findViewById(R.id.spinnerforimpt) ;
        btnSearch = (ImageButton)findViewById(R.id.btnSearch);
        btnList = (ImageButton)findViewById(R.id.btnList);
        sortTime = (TextView)findViewById(R.id.sortTime);
        sortName = (TextView)findViewById(R.id.sortName);
        sortSize = (TextView)findViewById(R.id.sortSize);
        searchEdit = (EditText) findViewById(R.id.searchEdit);
        btnBack = (ImageButton)findViewById(R.id.btnBack);
        spinnerforimpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //当选中某一个数据项时触发该方法
            /*
             * parent接收的是被选择的数据项所属的 Spinner对象，
             * view参数接收的是显示被选择的数据项的TextView对象
             * position接收的是被选择的数据项在适配器中的位置
             * id被选择的数据项的行号
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                data = (String)spinnerforimpt.getItemAtPosition(position);//从spinner中获取被选择的数据
                if(data.equals("智能浏览")){
                    title_recycler_view.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(txtAdapter);
                    fileAdapter.cancel();
                    tmpAdapter.cancel();
                    btnSearch.setVisibility(View.VISIBLE);
                    btnChooseAll.setText("全选");
                    numofchecked.setText("已选（0）");
                    //backforTxt(rootFile);
                }
                else if(data.equals("手机目录")){
                    title_recycler_view.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(fileAdapter);
                    txtAdapter.cancel();
                    tmpAdapter.cancel();
                    btnSearch.setVisibility(View.GONE);
                    btnChooseAll.setText("全选");
                    numofchecked.setText("已选（0）");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        numofchecked.setText("已选（0）");
        btnChooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnChooseAll.getText().equals("全选")) {
                    btnChooseAll.setText("取消");
                    if(status == 1){
                        tmpAdapter.checkAll();
                    }
                    else if (data.equals("智能浏览")) {
                        txtAdapter.checkAll();
                    } else {
                        fileAdapter.checkAll();
                    }
                }
                else if(btnChooseAll.getText().equals("取消")){
                    btnChooseAll.setText("全选");
                    if(status == 1){
                        tmpAdapter.cancel();
                    }
                    else if (data.equals("智能浏览")) {
                        txtAdapter.cancel();
                    } else {
                        fileAdapter.cancel();
                    }
                }
            }
        });

        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBookList();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerforimpt.setVisibility(View.GONE);
                btnList.setVisibility(View.GONE);
                searchEdit.setVisibility(View.VISIBLE);
                status = 1;
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTime.setVisibility(View.VISIBLE);
                sortName.setVisibility(View.VISIBLE);
                sortSize.setVisibility(View.VISIBLE);
            }
        });

        sortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTime.setVisibility(View.GONE);
                sortName.setVisibility(View.GONE);
                sortSize.setVisibility(View.GONE);
                if (status == 1) {
                    tmpAdapter.sort(tmpAdapter.SORT_TIME);
                } else if (data.equals("智能浏览")) {
                    txtAdapter.sort(txtAdapter.SORT_TIME);
                } else if (data.equals("手机目录")) {
                    fileAdapter.sort(fileAdapter.SORT_TIME);
                }
            }
        });

        sortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTime.setVisibility(View.GONE);
                sortName.setVisibility(View.GONE);
                sortSize.setVisibility(View.GONE);
                if (status == 1) {
                    tmpAdapter.sort(tmpAdapter.SORT_NAME);
                } else if (data.equals("智能浏览")) {
                    txtAdapter.sort(txtAdapter.SORT_NAME);
                } else if (data.equals("手机目录")) {
                    fileAdapter.sort(fileAdapter.SORT_NAME);
                }
            }
        });

        sortSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTime.setVisibility(View.GONE);
                sortName.setVisibility(View.GONE);
                sortSize.setVisibility(View.GONE);
                if (status == 1) {
                    tmpAdapter.sort(tmpAdapter.SORT_SIZE);
                } else if (data.equals("智能浏览")) {
                    txtAdapter.sort(txtAdapter.SORT_SIZE);
                } else if (data.equals("手机目录")) {
                    fileAdapter.sort(fileAdapter.SORT_SIZE);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status == 1) {
                    spinnerforimpt.setVisibility(View.VISIBLE);
                    btnList.setVisibility(View.VISIBLE);
                    searchEdit.setVisibility(View.GONE);
                    status = 0;
                    recyclerView.setAdapter(txtAdapter);
                    fileAdapter.cancel();
                    tmpAdapter.cancel();
                    btnChooseAll.setText("全选");
                    numofchecked.setText("已选（0）");
                }
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //过滤操作
                if (data.equals("智能浏览")) {
                    filter(s.toString());

                } else {
                    filter(s.toString());
                }
                if (s.length() > 0) {
                    //searchClear.setVisibility(View.VISIBLE);
                } else {
                    //searchClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


        });


        //状态监听数目
        fileAdapter.setCheckedChangeListener(new FileAdapter.CheckedChangeListener() {

            @Override
            public void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked) {
                numofchecked.setText("已选（"+Integer.toString(fileAdapter.getCheckNum())+"）");
            }
        });
        txtAdapter.setCheckedChangeListener(new FileAdapter.CheckedChangeListener() {

            @Override
            public void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked) {
                numofchecked.setText("已选（"+Integer.toString(txtAdapter.getCheckNum())+"）");
            }
        });

        tmpAdapter.setCheckedChangeListener(new FileAdapter.CheckedChangeListener() {
            @Override
            public void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked) {
                numofchecked.setText("已选（"+Integer.toString(tmpAdapter.getCheckNum())+"）");
            }
        });

        fileAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                if ( viewHolder instanceof FileAdapter.FileHolder){
                    FileBean file = beanList.get(position);
                    FileType fileType = file.getFileType() ;
                    if ( fileType == FileType.directory) {
                        getFile(file.getPath());
                        refreshTitleState( file.getName() , file.getPath() );
                    }else if ( fileType == FileType.apk ){
                        //安装app
                        // FileUtil.openAppIntent( MainActivity.this , new File( file.getPath() ) );
                    }else if ( fileType == FileType.image ){
                        //FileUtil.openImageIntent( MainActivity.this , new File( file.getPath() ));
                    }else if ( fileType == FileType.txt ){
                        // FileUtil.openTextIntent( MainActivity.this , new File( file.getPath() ) );
                    }else if ( fileType == FileType.music ){
                        //FileUtil.openMusicIntent( MainActivity.this ,  new File( file.getPath() ) );
                    }else if ( fileType == FileType.video ){
                        //FileUtil.openVideoIntent( MainActivity.this ,  new File( file.getPath() ) );
                    }else {
                        //FileUtil.openApplicationIntent( MainActivity.this , new File( file.getPath() ) );
                    }
                }
            }
        });

        fileAdapter.setOnItemLongClickListener(new RecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                if ( viewHolder instanceof  FileAdapter.FileHolder ){
                    FileBean fileBean = (FileBean) fileAdapter.getItem( position );
                    FileType fileType = fileBean.getFileType() ;
                    if ( fileType != null && fileType != FileType.directory ){
                        //FileUtil.sendFile( MainActivity.this , new File( fileBean.getPath() ) );
                    }
                }
                return false;
            }
        });

        titleAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
                TitlePath titlePath = (TitlePath) titleAdapter.getItem( position );
                getFile( titlePath.getPath() );
                int count = titleAdapter.getItemCount() ;
                int removeCount = count - position - 1 ;
                for ( int i = 0 ; i < removeCount ; i++ ){
                    titleAdapter.removeLast();
                }
                numofchecked.setText("已选（0）");
            }
        });

        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        refreshTitleState( "内部存储设备" , rootPath );

        // 先判断是否有权限。
        getFile(rootPath);
    }

    public void getFile(String path ) {
        rootFile = new File( path + File.separator  )  ;
        new MyTask(rootFile).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , "") ;
        Mythread mythread = new Mythread();
        mythread.start();
    }

    public class Mythread extends Thread {
        @Override
        public void run() {
            backforTxt(rootFile);
        }
        public void startThread(){
            Mythread mythread = new Mythread();
            mythread.start();
        }
    }

    public void filter(String charString) {
        tmpList.clear();
        if (charString.isEmpty()) {
            recyclerView.setAdapter(txtAdapter);
            fileAdapter.cancel();
            tmpAdapter.cancel();
            btnChooseAll.setText("全选");
            numofchecked.setText("已选（0）");
        } else {
            for (File f : forsearchList) {
                if(f.getName().indexOf(charString)!=-1) {
                    FileBean fileBean = new FileBean();
                    fileBean.setName(f.getName());
                    fileBean.setTime(f.lastModified());
                    fileBean.setPath(f.getAbsolutePath());
                    fileBean.setFileType(FileUtil.getFileType(f));
                    fileBean.setChildCount(FileUtil.getFileChildCount(f));
                    fileBean.setSize(f.length());
                    fileBean.setHolderType( 0 );
                    tmpList.add(fileBean);
                }
            }
            recyclerView.setAdapter(tmpAdapter);
            fileAdapter.cancel();
            txtAdapter.cancel();
            btnChooseAll.setText("全选");
            numofchecked.setText("已选（0）");
        }
    }

    private void backforTxt(File file) {
        File[] filesArray = file.listFiles();

        if (filesArray != null) {
            List<File> fileList = new ArrayList<>();
            Collections.addAll(fileList, filesArray);  //把数组转化成list
            Collections.sort(fileList, FileUtil.comparator);  //按照名字排序
            for (File f : fileList) {
                if (f.isHidden()) continue;
                FileBean fileBean = new FileBean();
                fileBean.setName(f.getName());
                fileBean.setTime(f.lastModified());
                fileBean.setPath(f.getAbsolutePath());
                fileBean.setFileType(FileUtil.getFileType(f));
                fileBean.setChildCount(FileUtil.getFileChildCount(f));
                fileBean.setSize(f.length());
                if (fileBean.getFileType() == FileType.directory) {
                    backforTxt(f);

                }
                else{
                    fileBean.setHolderType( 0 );
                }
                if (fileBean.getFileType() == FileType.txt) {
                    forsearchList.add(f);
                    searchTxtList.add(fileBean);
                }
            }
        }
    }

    class MyTask extends AsyncTask {
        File file;

        MyTask(File file) {
            this.file = file;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            List<FileBean> fileBeenList = new ArrayList<>();

            if ( file.isDirectory() ) {
                File[] filesArray = file.listFiles();
                if ( filesArray != null ){
                    List<File> fileList = new ArrayList<>() ;
                    Collections.addAll( fileList , filesArray ) ;  //把数组转化成list
                    Collections.sort( fileList , FileUtil.comparator );  //按照名字排序

                    for (File f : fileList ) {
                        if (f.isHidden()) continue;

                        FileBean fileBean = new FileBean();
                        fileBean.setName(f.getName());
                        fileBean.setTime(f.lastModified());
                        fileBean.setPath(f.getAbsolutePath());
                        fileBean.setFileType( FileUtil.getFileType( f ));
                        fileBean.setChildCount( FileUtil.getFileChildCount( f ));
                        fileBean.setSize( f.length() );
                        if(fileBean.getFileType() == FileType.directory)
                        {
                            fileBean.setHolderType(-1);
                        }
                        else{
                            fileBean.setHolderType( 0 );
                        }
                        fileBeenList.add(fileBean);
                    }
                }
            }

            beanList = fileBeenList;
            return fileBeenList;
        }

        @Override
        protected void onPostExecute(Object o) {
            if ( beanList.size() > 0  ){
                empty_rel.setVisibility( View.GONE );
            }else {
                empty_rel.setVisibility( View.VISIBLE );
            }
            fileAdapter.refresh(beanList);
        }
    }

    void refreshTitleState( String title , String path ){
        TitlePath filePath = new TitlePath() ;
        filePath.setNameState( title + " > " );
        filePath.setPath( path );
        titleAdapter.addItem( filePath );
        title_recycler_view.smoothScrollToPosition( titleAdapter.getItemCount());
    }

    public void cancleSelect() {
        List<TitlePath> titlePathList = (List<TitlePath>) titleAdapter.getAdapterData();
        if ( titlePathList.size() == 1 ){
            this.getActivity().finish();
        }else {
            titleAdapter.removeItem( titlePathList.size() - 1 );
            getFile( titlePathList.get(titlePathList.size() - 1 ).getPath() );
        }
    }
}
