
package com.example.qiang.collect;
/**
 * 设置界面
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import util.SharedPreferenceUtil;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBackBtn ;
    private Button mSettingBtn ;
    private EditText mLocationTime ;
    private EditText mReceiverName ;

    private boolean isChangeTime = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setting);

        init() ;
        initData() ;
    }

    private void initData() {
        int time = -1 ;
        String receiver ;
        mLocationTime.setText(((time = SharedPreferenceUtil.readSharedPreferenceTime(this)) == -1 ? 5000 : time) + "") ;
        mReceiverName.setText(TextUtils.isEmpty((receiver = SharedPreferenceUtil.readSharedPreferenceReceiver(this))) ? "18538253565@126.com" : receiver) ;
    }


    private void init() {
        mSettingBtn = (Button) findViewById(R.id.setting_btn) ;
        mLocationTime = (EditText) findViewById(R.id.edit_location_time) ;
        mReceiverName = (EditText) findViewById(R.id.edit_receiver) ;
        mBackBtn = (ImageView) findViewById(R.id.setting_activity_back_btn) ;

        mBackBtn.setOnClickListener(this);
        mSettingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_activity_back_btn:
                if(isChangeTime){
                    setResult(RESULT_OK);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish() ;
                break ;
            case R.id.setting_btn:{
                if(mSettingBtn.getText().equals("开始设置")){
                    mSettingBtn.setText("保存");
                    mLocationTime.setEnabled(true);
                    mReceiverName.setEnabled(true);
                }else{
                    SharedPreferenceUtil.saveSetting(this, Integer.parseInt(mLocationTime.getText().toString()),
                            mReceiverName.getText().toString());

                    mSettingBtn.setText("开始设置");
                    mLocationTime.setEnabled(false);
                    mReceiverName.setEnabled(false);
                    isChangeTime = true ;
                }
                break ;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(isChangeTime){
            setResult(RESULT_OK);
        }else{
            setResult(RESULT_CANCELED);
        }
        finish() ;
    }
}
