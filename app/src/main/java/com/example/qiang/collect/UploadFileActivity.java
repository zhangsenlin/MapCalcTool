package com.example.qiang.collect;
/**
 * 上传界面
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.FileListAdapter;
import util.SharedPreferenceUtil;

public class UploadFileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String FILE_LIST = "file_list";
    private ListView mListView ;
    private ImageView mBackBtn ;
    private TextView mEnsureBtn ;
    private  TextView mDelete;
    private List<File> mData ;
    private boolean[] mChecked ;
    private FileListAdapter mAdapter ;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getSupportActionBar().hide();
        setContentView(R.layout.activity_upload_file);

        init() ;
        initData() ;
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.upload_file_list) ;
        mBackBtn = (ImageView) findViewById(R.id.upload_activity_back_btn) ;
        mEnsureBtn = (TextView) findViewById(R.id.upload_file_ensure_btn) ;
        mDelete =(TextView) findViewById(R.id.upload_file_delete_btn);
//        mListView.setOnItemClickListener(this);
        mEnsureBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }


    private void initData() {
        mData = (List<File>) getIntent().getSerializableExtra(FILE_LIST);
        if(mData == null){
            mData = new ArrayList<>();
        }

        mChecked = new boolean[mData.size()] ;
        for(int i = 0; i < mChecked.length; ++i){
            mChecked[i] = false ;
        }
        initAdapter() ;
    }

    private void initAdapter() {
        mAdapter = new FileListAdapter(this, mData, mChecked) ;
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload_activity_back_btn:
                setResult(RESULT_CANCELED);
                finish();
                break ;
            case R.id.upload_file_ensure_btn:
                List<String> list = new ArrayList<>() ;
                for(int i = 0; i < mChecked.length; ++i){
                    if(mChecked[i] == true){
                        list.add(mData.get(i).getAbsolutePath());
                    }
                }
                //文件上传
                new EmailUtil().sendContentWithMulti(new EmailUtil.OnSendEmailListener() {
                    //这两个回调方法中关闭动画
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UploadFileActivity.this, "发送成功", Toast.LENGTH_SHORT).show() ;
                        DialogUtils.closeDialog(mDialog);
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(UploadFileActivity.this, "发送失败，请重试", Toast.LENGTH_SHORT).show() ;
                        DialogUtils.closeDialog(mDialog);
                    }
                }, list, SharedPreferenceUtil.readSharedPreferenceReceiver(UploadFileActivity.this));
                //添加动画
                mDialog = DialogUtils.createLoadingDialog(UploadFileActivity.this, "发送中...");

                break ;


            case R.id.upload_file_delete_btn:
                        AlertDialog.Builder builder = new AlertDialog.Builder(UploadFileActivity.this);
                        builder.setMessage("确定要删除所选项吗？");
                        builder.setTitle("提示");
                        builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which){
                                File dir = new File(Environment.getExternalStorageDirectory().getPath()
                                        + File.separator + "gps") ;
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
                                Toast.makeText(getBaseContext(),"删除成功！",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                        builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which){

                            }
                        });
                        builder.create().show();
                break ;
        }
    }
}
