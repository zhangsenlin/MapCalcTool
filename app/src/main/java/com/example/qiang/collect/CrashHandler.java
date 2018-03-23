package com.example.qiang.collect;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/27/027.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance;

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 核心方法，当程序crash 会回调此方法， Throwable中存放这错误日志
     */
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {

        String  sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dirFile = new File(sdCardRoot + File.separator + "Collect_errorlog" +File.separator);

        dirFile.mkdirs();

        File file = new File(sdCardRoot + File.separator + "Collect_errorlog" + File.separator + "errorlog.txt");
        Log.v("createFileOnSDCard", sdCardRoot + File.separator + "Collect_errorlog" + File.separator + "errorlog.txt");
        try {
            file.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            FileWriter fw = new FileWriter(file, true);
            // 错误信息
            // 这里还可以加上当前的系统版本，机型型号 等等信息
            StackTraceElement[] stackTrace = arg1.getStackTrace();
            fw.write(arg1.getMessage() + "\n");
            for (int i = 0; i < stackTrace.length; i++) {
                fw.write("file:" + stackTrace[i].getFileName() + " class:"
                        + stackTrace[i].getClassName() + " method:"
                        + stackTrace[i].getMethodName() + " line:"
                        + stackTrace[i].getLineNumber() + "\n");

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                fw.write(str);
            }
            fw.write("===================================================================");
            fw.write("\n\n\n\n");
            fw.close();
            // 上传错误信息到服务器
            // uploadToServer();
        } catch (IOException e) {
        }
        arg1.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

