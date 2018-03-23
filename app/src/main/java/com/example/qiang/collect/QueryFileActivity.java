package com.example.qiang.collect;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.FileListAdapter;

public class QueryFileActivity extends AppCompatActivity implements  View.OnClickListener {

    public static final String FILE_LIST = "file_list";
    private ListView mListView ;
    private ImageView mBackBtn ;
    private TextView mEnsureBtn ;
    private TextView mDelete;
    private List<File> mData ;
    private boolean[] mChecked ;
    private FileListAdapter mAdapter ;

    private boolean isJump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getSupportActionBar().hide();
        setContentView(R.layout.activity_query_file);

        init() ;
        initData() ;
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.file_list) ;
        mBackBtn = (ImageView) findViewById(R.id.img_back_btn) ;
        mEnsureBtn = (TextView) findViewById(R.id.ensure_btn) ;
        mDelete = (TextView)findViewById(R.id.delete_btn);
//        mListView.setOnItemClickListener(this);
        mEnsureBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }


    private void initData() {
        try {
            mData = (List<File>) getIntent().getSerializableExtra(FILE_LIST);
            if (mData == null) {
                mData = new ArrayList<>();
            }

            mChecked = new boolean[mData.size()];
            for (int i = 0; i < mChecked.length; ++i) {
                mChecked[i] = false;
            }
            initAdapter();
        }catch(Exception e){
            Toast.makeText(this, "1, initData出现异常", Toast.LENGTH_SHORT).show() ;
            finish();
        }
    }

    private void initAdapter() {
        try{
            mAdapter = new FileListAdapter(this, mData, mChecked) ;
            mListView.setAdapter(mAdapter);
        }catch (Exception e){
            Toast.makeText(this, "2, initAdapter出现异常", Toast.LENGTH_SHORT).show() ;
        }

    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if(isJump){
//            Intent intent = new Intent() ;
//            intent.putExtra(MainActivity.FILE_PATH, mData.get(position).getAbsolutePath()) ;
//            setResult(RESULT_OK, intent);
//            finish();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back_btn:
                try{
                    setResult(RESULT_CANCELED);
                    isJump = true;
                    finish();
                }catch (Exception e){
                    Toast.makeText(this, "3, R.id.img_back_btn出现异常", Toast.LENGTH_SHORT).show() ;
                }

                break ;
            case R.id.ensure_btn:

                try{
                    List<String> list = new ArrayList<>() ;
                    for(int i = 0; i < mChecked.length; ++i){
                        if(mChecked[i] == true){
                            list.add(mData.get(i).getAbsolutePath()) ;
                        }
                    }

                    Intent intent = new Intent() ;
                    intent.putStringArrayListExtra(MainActivity.FILE_PATH, (ArrayList<String>) list) ;
                    setResult(RESULT_OK, intent);
                    Toast.makeText(getBaseContext(),"显示",Toast.LENGTH_LONG).show();
                    isJump = true;
                    finish();
                }catch (Exception e){
                    Toast.makeText(this, "4, R.id.ensure_btn出现异常", Toast.LENGTH_SHORT).show() ;
                }

                break ;


            case R.id.delete_btn:

                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(QueryFileActivity.this);
                    builder.setMessage("确定要删除所选项吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                            File dir = new File(Environment.getExternalStorageDirectory().getPath()
                                    + File.separator + "collect") ;
                            File[] files = null ;
                            if(dir.exists()){
                                files = dir.listFiles();
                            }else{
                                files = new File[0] ;
                            }
                            for(int i = 0; i < mChecked.length; ++i){
                                if(mChecked[i] == true){
                                    //listdel.add(mData.get(i).getAbsolutePath()) ;
                                    files[i].delete();
                                }
                            }
                            isJump = false;
                            Toast.makeText(getBaseContext(),"删除成功！",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                    builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                            isJump = false;
                        }
                    });
                    builder.create().show();
                }catch (Exception e){
                    Toast.makeText(this, "5, R.id.delete_btn出现异常", Toast.LENGTH_SHORT).show() ;
                }

                break ;
        }
    }
}
