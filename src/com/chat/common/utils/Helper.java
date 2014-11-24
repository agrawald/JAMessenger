package com.chat.common.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by e7006722 on 24/11/2014.
 */
public class Helper {
    public static boolean isNetworkAvailable(Activity mainActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void makeToast(String msg, int length, Activity mainAct){
        Toast.makeText(mainAct, msg, length).show();
    }
}
