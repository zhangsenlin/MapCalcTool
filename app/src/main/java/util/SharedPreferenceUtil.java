package util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Hero on 2017/1/18.
 */

public class SharedPreferenceUtil {
    public static void saveSetting(Context context, int time, String receiver){
        SharedPreferences.Editor editor = context.getSharedPreferences("setting", MODE_PRIVATE).edit() ;
        editor.putInt("time", time) ;
        editor.putString("receiver", receiver) ;
        editor.commit() ;
    }


    public static int readSharedPreferenceTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("setting", MODE_PRIVATE) ;
        return preferences.getInt("time", 2000);
    }

    public static String readSharedPreferenceReceiver(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("setting", MODE_PRIVATE) ;
        return preferences.getString("receiver", "");
    }
}
