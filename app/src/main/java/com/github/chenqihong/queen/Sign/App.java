package com.github.chenqihong.queen.Sign;

import android.app.Application;
import android.content.Context;

import com.github.chenqihong.queen.Device.Device;

import java.util.List;

/**
 * Created by yixianglin on 2017/7/6.
 */

public class App {
    private Context mContext;
    private static App mInstance;
    public static App getInstance(Application application){
        if(null == mInstance) {
            mInstance = new App(application);
        }

        return mInstance;
    }
    private App(Context context){
        mContext=context;
    }
    public  List<AppInfo> getAppInfos(Context context){
        return ApplicationInfoUtil.getAppInfos(context);
    }
}
