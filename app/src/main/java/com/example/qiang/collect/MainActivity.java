package com.example.qiang.collect;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import util.SharedPreferenceUtil;

public class MainActivity extends Activity implements LocationSource, AMapLocationListener, View.OnClickListener {

    private TextView tishi;

    private Dialog mDialog;

    private static final int REQUEST_FILE_PATH = 0;
    public static final String FILE_PATH = "file_path";
    private static final int REQUEST_CHANGED_TIME = 1;



    public MapView mapView;
    public AMap aMap;
    public OnLocationChangedListener listener;
    public AMapLocationClient aMapLocationClient;
    public AMapLocationClientOption option;
    private LatLng oldLatLng;
    private boolean isFirstLatlng;

    private TextView start, pause, end, upLoad;
    private TextView setting;
    private TextView display;
    private List<LatLng> list = new ArrayList<>();//LatLng高德地图处理的过后的类
    private List<GPSLocation> gpsList = new ArrayList<>();
    //TODO 新建gpsDataList，用于计算面积
    //新建gpsDataList，用于计算面积
    private List<GPSLocation> gpsDataList = new ArrayList<>();
    //设置起点
    private GPSLocation beginLocation;

    private File file;
    //    private FileOutputStream fileOutputStream;
    private boolean isStart = false;
    private boolean isOnce = false;
    private List<String> mFilePath;
    private String mLocationName;
    private int time;

    private LocationManager locationManager;

    boolean isD = true;
    //多次取经纬度来减小误差
    LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {}
        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {}
        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        //获取多次GPS定位的坐标值，求平均值来减小误差
        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                if (isOnce == false) {return;}
                GPSLocation gpsLocation = new GPSLocation();
                //获取多次GPS定位的坐标值来减小误差
                gpsLocation.setLa(location.getLatitude());
                gpsLocation.setLo(location.getLongitude());
                gpsList.add(gpsLocation);
                gpsDataList.add(gpsLocation);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_main);


        //加入CrashHandler来获取崩溃信息
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());


        isFirstLatlng = true;
        mapView = (MapView) findViewById(R.id.map);
        setting = (TextView) findViewById(R.id.setting);
        start = (TextView) findViewById(R.id.start);
        pause = (TextView) findViewById(R.id.pause);
        end = (TextView) findViewById(R.id.end);
        display = (TextView) findViewById(R.id.display);
        //     display= (TextView) findViewById(R.id.display);
        upLoad = (TextView) findViewById(R.id.upload);



        setting.setOnClickListener(this);
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        end.setOnClickListener(this);
        display.setOnClickListener(this);
        upLoad.setOnClickListener(this);

        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        //保持小蓝点在地图中间
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        //显示定位自己按钮
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        //指北针
        aMap.getUiSettings().setCompassEnabled(true);
        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);

        getGPSLocation();

    }

    private void getGPSLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 0, locationListener);
            //gps已打开
        } else {
            Toast.makeText(this, "请打开GPS服务,并重启软件", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpMap(LatLng oldLatLng, LatLng newLatlng) {
        aMap.addPolyline((new PolylineOptions()).add(oldLatLng, newLatlng).geodesic(true).color(Color.GREEN));
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        listener = onLocationChangedListener;
        if (aMapLocationClient == null) {
            aMapLocationClient = new AMapLocationClient(this.getApplicationContext());
            option = new AMapLocationClientOption();

            //获取一次定位结果：
            option.setOnceLocation(false);

            time = SharedPreferenceUtil.readSharedPreferenceTime(MainActivity.this);

            if (SharedPreferenceUtil.readSharedPreferenceTime(MainActivity.this) == -1) {
                option.setInterval(5000);
            } else {
                option.setInterval(time);
            }

            option.setHttpTimeOut(30000);
            option.setWifiActiveScan(false);
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            aMapLocationClient.setLocationOption(option);
            aMapLocationClient.setLocationListener(this);
            aMapLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        listener = null;
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        aMapLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (listener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                listener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                if (isOnce == false) {
                    return;
                }

                LatLng newLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (isFirstLatlng) {
                    oldLatLng = newLatlng;
                    Toast.makeText(this, "开始运动", Toast.LENGTH_SHORT).show();
                    list.add(oldLatLng);
                    isFirstLatlng = false;
                }
                if (oldLatLng != newLatlng) {
                    //计算两点距离，大于10 写文件
                    float distance = AMapUtils.calculateLineDistance(oldLatLng, newLatlng);
                    if (distance > 10.00) {
                        //地图标注
                        setUpMap(oldLatLng, newLatlng);
                        list.add(newLatlng);
                        oldLatLng = newLatlng;
                    }
                }
            } else {
                String errText = "定位失败" + aMapLocation.getErrorCode() + ": "
                        + aMapLocation.getErrorInfo();
                Toast.makeText(MainActivity.this, "" + errText,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

//显示对话框
    public void showDialog(){
        final EditText editText;

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.layout_editdialog, null);
        tishi = (TextView) view.findViewById(R.id.tishi);
        editText = (EditText)view.findViewById(R.id.input);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //文件名
                        mLocationName = editText.getText().toString() + "_" + currentTime();
                        if(TextUtils.isEmpty(mLocationName)){
                            Toast.makeText(MainActivity.this, "请输入村庄的名字", Toast.LENGTH_SHORT).show();
                        }else{
                            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                Toast.makeText(MainActivity.this,"SDCard没有找到",Toast.LENGTH_SHORT).show();
                            }else{
                                isStart=true;
                                isOnce = true ;
                                start.setText("开始");
                                start.setEnabled(false);
                                isFirstLatlng = true ;
                                list.clear();
                                gpsList.clear();
                                aMap.clear();//清除历史轨迹
                            }

                        }

                    }
                }).setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();

        final Button positiveButton = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub、
                tishi.setText("");
                if(editText.getText().toString().contains("_")){
                    positiveButton.setEnabled(false);
                    tishi.setText("输入村庄名字不能包含下划线‘_’");
                }else{
                    positiveButton.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                tishi.setText("");
                if(editText.getText().toString().contains("_")){
                    positiveButton.setEnabled(false);
                    tishi.setText("输入村庄名字不能包含下划线‘_’");
                }else{
                    positiveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                tishi.setText("");
                if(editText.getText().toString().contains("_")){
                    positiveButton.setEnabled(false);
                    tishi.setText("输入村庄名字不能包含下划线‘_’");
                }else{
                    positiveButton.setEnabled(true);
                }

            }
        });

        /*final AlertDialog dialog=new AlertDialog.Builder(this).setTitle("请输入村庄的名字").setIcon(R.mipmap.ic_launcher)
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    //文件名
                    mLocationName = editText.getText().toString() + "_" + currentTime();
                    if(TextUtils.isEmpty(mLocationName)){
                        Toast.makeText(MainActivity.this, "请输入村庄的名字", Toast.LENGTH_SHORT).show();
                    }else{
                        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            Toast.makeText(MainActivity.this,"SDCard没有找到",Toast.LENGTH_SHORT).show();
                        }else{
                            isStart=true;
                            isOnce = true ;
                            start.setText("开始");
                            start.setEnabled(false);
                            isFirstLatlng = true ;
                            list.clear();
                            gpsList.clear();
                            aMap.clear();//清除历史轨迹
                        }

                    }
                }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        }).setCancelable(false).create();
        dialog.show();*/
    }

    private String currentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss") ;
        return format.format(new Date());
    }

    //申请权限
    public void permission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }else{
            showDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showDialog();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.setting:{
                Intent intent = new Intent(this, SettingActivity.class) ;
                startActivityForResult(intent,REQUEST_CHANGED_TIME);
                break ;
            }
            case R.id.start:
                if(isStart){
                    //开始后，点击暂存，再次点击该按钮执行
                    start.setText("开始");
                    pause.setEnabled(true);
                    start.setEnabled(false);
                    isOnce = true ;



                    //获取起点位置
                    for (GPSLocation gpsLocation : gpsList) {
                        beginLocation = gpsLocation;
                        if(beginLocation!=null)
                            break;
                    }
                }else{
                    //首次点击
                    permission();
                }
                break;
            case R.id.pause:
                if(isStart){
                    start.setText("继续");
                    start.setEnabled(true);
                    pause.setEnabled(false);

                    isOnce = false ;
//                    aMapLocationClient.stopLocation();

//                        FileInputStream fileInputStream = new FileInputStream(file);
//                        int length = fileInputStream.available();
//                        byte[] bytes = new byte[length];
//                        fileInputStream.read(bytes);
//                        Toast.makeText(MainActivity.this,"  "+ bytes.toString()+"   ",Toast.LENGTH_SHORT).show() ;
                }else{
                    Toast.makeText(MainActivity.this,"你还没有开始",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.end:
                if(isStart){
                    start.setEnabled(true) ;
                    pause.setEnabled(true) ;
                    end.setEnabled(true) ;
//                    aMapLocationClient.stopLocation();
                    isStart = false ;
                    isOnce = false ;

                    //获取最后地点
                    //gpsDataList.add(beginLocation);

                    //添加计算面积时的动画
                    mDialog = DialogUtils.createLoadingDialog(MainActivity.this, "计算中...");

                    writeLocation();
                    writeGPS();
                }else{
                    Toast.makeText(MainActivity.this,"你还没有开始",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.display:{
                //显示文件列表
                File dir = new File(Environment.getExternalStorageDirectory().getPath()
                        + File.separator + "collect") ;
                File[] files = null ;
                if(dir.exists()){
                    files = dir.listFiles();
                }else{
                    files = new File[0] ;
                }

                //跳转到新的activity
                Intent intent = new Intent(this, QueryFileActivity.class) ;
                intent.putExtra(QueryFileActivity.FILE_LIST, (Serializable) Arrays.asList(files)) ;


//                startActivity(intent);
                startActivityForResult(intent, REQUEST_FILE_PATH);


                aMapLocationClient.stopLocation();
                break;
            }

            case R.id.upload: {
                start.setEnabled(true);
                end.setEnabled(true);
                pause.setEnabled(true);

                File dir = new File(Environment.getExternalStorageDirectory().getPath()
                        + File.separator + "gps");
                File[] files = null ;
                if(dir.exists()){
                    files = dir.listFiles();
                }else{
                    files = new File[0] ;
                }

                //跳转到新的activity
                Intent intent = new Intent(this, UploadFileActivity.class);
                intent.putExtra(UploadFileActivity.FILE_LIST, (Serializable) Arrays.asList(files));
                startActivity(intent);

                //发送邮件
//                new EmailUtil().sendContent(new EmailUtil.OnSendEmailListener() {
//                    //这两个回调方法中关闭动画
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show() ;
//                        mUploadPath = "" ;
//                        DialogUtils.closeDialog(mDialog);
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Toast.makeText(MainActivity.this, "发送失败，请重试", Toast.LENGTH_SHORT).show() ;
//                        DialogUtils.closeDialog(mDialog);
//                    }
//                }, mUploadPath) ;
//                //添加动画
//                mDialog = DialogUtils.createLoadingDialog(MainActivity.this, "发送中...");
                // mHandler.sendEmptyMessageDelayed(1, 20000);
                break;
            }
        }
    }

    public void writeLocation(){
        File fileDir = new File (Environment.getExternalStorageDirectory().getPath() + File.separator + "collect") ;
        if(fileDir.exists() == false){
            fileDir.mkdirs() ;
        }

        file=new File(Environment.getExternalStorageDirectory(), "collect" + File.separator + mLocationName +".txt");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = null ;
            try {
                fileOutputStream = new FileOutputStream(file);

                if(list!=null && list.size() > 0){
                    for (LatLng latLng : list) {
                        //解决偏差问题
/*                    DealDeviation dealdata = new DealDeviation();
                    dealdata.transform(latLng.latitude,latLng.longitude);*/
                        fileOutputStream.write((latLng.latitude + " " + latLng.longitude).getBytes());
                        //解决经纬度偏差问题
                        //fileOutputStream.write((latLng.latitude + " " + latLng.longitude).getBytes());
                        fileOutputStream.write("\r\n".getBytes());//写入一个换行
                    }
                    fileOutputStream.write((list.get(0).latitude + " " + list.get(0).longitude).getBytes());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"文件写入失败",Toast.LENGTH_SHORT).show();
            } finally {
                if(fileOutputStream != null){
                    fileOutputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"文件创建失败",Toast.LENGTH_SHORT).show();
        }
    }
    public void writeGPS(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                File fileDir = new File (Environment.getExternalStorageDirectory().getPath() + File.separator + "gps") ;
                if(fileDir.exists() == false){
                    fileDir.mkdirs() ;
                }

                file=new File(Environment.getExternalStorageDirectory(), "gps" + File.separator + mLocationName +".txt");
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = null ;
                    try {
                        fileOutputStream=new FileOutputStream(file);
                        List<Double> averageData = new ArrayList<>();
                        //调用计算方法，返回面积值
                        //ClaLong c = new ClaLong();
                        //double Area = c.Claute(gpsList)/1000000.0;

                        if(gpsList!=null && gpsList.size() > 0){

                            for (GPSLocation gpsLocation : gpsList) {
                                fileOutputStream.write((gpsLocation.getLa() + " " + gpsLocation.getLo()).getBytes());
                                fileOutputStream.write("\r\n".getBytes());//写入一个换行
                            }
                            //调用计算方法，返回面积值
                            LiuchunTest liu = new LiuchunTest();
                            double Area1 = liu.Claute(gpsList);

                            MainActivity.renameFile(Environment.getExternalStorageDirectory().getPath() + File.separator + "gps",
                                    mLocationName +".txt",mLocationName+"_" +Area1+".txt");
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage("您尚未活动");
                            builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

                                public void onClick(DialogInterface dialog,int which){
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this,"请重新运动",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if(fileOutputStream != null){
                            fileOutputStream.close();
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mDialog.dismiss();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"文件创建失败",Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
        t.start();
    }

    //回显时 接收来自返回数据activity的数据（回显哪一项）
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_FILE_PATH :
                if(resultCode != this.RESULT_OK){
                    break ;
                }
                if(data == null){
                    Toast.makeText(this, "文件获取异常，请稍后重试", Toast.LENGTH_SHORT).show() ;
                    break ;
                }

                mFilePath = data.getStringArrayListExtra(FILE_PATH) ;
                Toast.makeText(this, mFilePath.size() + "", Toast.LENGTH_SHORT).show() ;

                if(mFilePath.size() > 0){
                    aMap.clear();
                    getFileData() ;
                }
                break ;

            case REQUEST_CHANGED_TIME:{
                if(resultCode != RESULT_OK){
                    break ;
                }

                //重新设置定位间隔
                option.setInterval((time = SharedPreferenceUtil.readSharedPreferenceTime(this))
                        == -1 ?  5000 : time) ;
                aMapLocationClient.setLocationOption(option);
                aMapLocationClient.startLocation();
                break ;
            }
        }
    }

    LatLng centerLatLng;
    int cou=0;
    //从文件中获取经纬度的数据
    private void getFileData() {
        for (String path: mFilePath) {
            File file = new File(path) ;
            if(file.exists() == false) {
                continue;
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
                StringBuilder sb = new StringBuilder() ;
                String str = "" ;
                while((str = reader.readLine()) != null){
                    if(str!=" "){
                        sb.append(str) ;
                        sb.append(" ") ;
                    }
                }
                //对每一个图形进行求取圆心操作
                String[] datastr = sb.toString().split(" ") ;

                //判断文件内容是否为空值
                if(sb.toString().contains("1") || sb.toString().contains("3") || sb.toString().contains("2")
                        || sb.toString().contains("4")|| sb.toString().contains("5")|| sb.toString().contains("6")
                        || sb.toString().contains("7")|| sb.toString().contains("8")|| sb.toString().contains("9")){
                    CenterLaLo center = new CenterLaLo(datastr);
                    centerLatLng = center.getCenter();
                    //传入字符串的经纬度和绘制的图形个数
                    showPath(sb.toString(),mFilePath.size()) ;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isD = true;
    }


    //回显时 再次绘制覆盖物
    private void showPath(String path,int count) {
        boolean once = true ;

        //设置中心点
        double sumLa=0.0,sumLo=0.0;
        double centerLo,centerLa;

        centerLa = centerLatLng.latitude;
        centerLo = centerLatLng.longitude;
        LatLng marker1 = new LatLng(centerLa, centerLo);
        //aMap.setPointToCenter(centerLa,centerLo);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));


        LatLng oldPoint = null ;
        LatLng newPoint = null ;
        String[] data = path.split(" ") ;

        //判断文件写入是否为空值
            for(int i = 0; i < data.length; i = i + 2){
                if(once){
                    newPoint = new LatLng(Double.parseDouble(data[i]), Double.parseDouble(data[i + 1])) ;
                    once = false ;
                }else{
                    oldPoint = newPoint ;
                    newPoint = new LatLng(Double.parseDouble(data[i]), Double.parseDouble(data[i + 1])) ;
                    setUpMap(oldPoint, newPoint);
                }
            }


    }

    /** *//**文件重命名
     * @param path 文件目录
     * @param oldname  原来的文件名
     * @param newname 新文件名
     */
    public static void renameFile(String path,String oldname,String newname){
        if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile=new File(path+"/"+oldname);
            File newfile=new File(path+"/"+newname);
            if(!oldfile.exists()){
                return;//重命名文件不存在
            }
            if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
            {}
            else{
                oldfile.renameTo(newfile);
            }
        }else{

        }
    }
}
