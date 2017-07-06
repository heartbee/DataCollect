package com.github.chenqihong.queen.Sign;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;


public class ApplicationInfoUtil {
    public static List<AppInfo> getAppInfos(Context context){
        PackageManager pm=context.getPackageManager();
        List<PackageInfo> packageInfoList=pm.getInstalledPackages(0);
        List<AppInfo> appInfoList=new ArrayList<AppInfo>();
        for(PackageInfo packageInfo:packageInfoList){
            AppInfo appInfo=new AppInfo();
            String packageName=packageInfo.packageName;
            appInfo.setPackname(packageName);
            String apkpath=packageInfo.applicationInfo.sourceDir;
            appInfo.setApkpath(apkpath);

            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}