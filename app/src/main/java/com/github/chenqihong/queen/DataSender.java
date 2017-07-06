package com.github.chenqihong.queen;

import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.chenqihong.queen.Base.Md5Utils;
import com.github.chenqihong.queen.Device.Device;
import com.github.chenqihong.queen.Device.TrafficUtil;
import com.github.chenqihong.queen.Emulator.JniAnti;
import com.github.chenqihong.queen.Geolocation.GeoLocationListener;
import com.github.chenqihong.queen.Geolocation.ILocation;
import com.github.chenqihong.queen.Geolocation.LocationFactory;
import com.github.chenqihong.queen.HttpUtil.HttpUtils;
import com.github.chenqihong.queen.Sign.App;
import com.github.chenqihong.queen.Sign.AppInfo;
import com.github.chenqihong.queen.Sign.Sign;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * 数据发送前准备接口，封装环境数据
 * Encapsulate environmental data before sending
 */
public class DataSender {

    public static String RESULT;


    /**
     * 后台Url
     *
     * Url of server;
     */
    private static String sUrl = "";

    /**
     * 定位组件
     *
     * Location instance;
     */
    private ILocation mGeolocation = null;

    /**
     * 环境变量封装HashMap
     *
     * Hash map used to encapsulate environmental data;
     */
    private HashMap<String, Object> mParams;

    /**
     * 行为Json列表
     *
     * User behavior data for json array;
     */
    private JSONArray mActionArray;

    /**
     * cookie域名
     *
     * Domain of cookie;
     */
    private String mDomain;

    /**
     * cookie的session id
     *
     * session id of cookie;
     */
    private String mSessionId;

    /**
     * APP的user id（登录名）
     *
     * user id of APP;
     */
    private String mUserId;

    private Application mContext;

    private double mLongitude;
    private double mLatitude;

    /**
     * 初始化DataSender
     *
     * Initiation of DataSender;
     */
    public DataSender(Application application) {
        mParams = new HashMap<String, Object>();
        mContext = application;
    }

    /**
     * 设置TdId，之后的数据发送前均使用该id
     *
     */
    public void setTdId(List<HttpCookie> receivedCookies) {
        List<HttpCookie> cookies = receivedCookies;
        for (int i = 0; i < cookies.size(); i++) {
            if (mDomain.contains(cookies.get(i).getDomain())) {
                if ("tdid".equals(cookies.get(i).getName())) {
                    mSessionId = cookies.get(i).getValue();
                    break;
                }
            }
        }
    }

    /**
     * 封装sissionid
     */
    public void putSessionId() {
        if (null != mSessionId) {
            mParams.put("td", mSessionId + "");
        }
    }

    public void setUrl(String url){
        sUrl = url;
    }

    /**
     * 封装当前时间
     */
    public void putTime() {
        long time = System.currentTimeMillis();
        mParams.put("qt", time);
    }

    /**
     * 封装userId
     */
    public void putUserId() {
        if (null != mUserId) {
            mParams.put("ud", mUserId);
        }
    }

    /**
     * 设置userId
     *
     * @param userId userId需要从用户输入登录名时获取
     */
    public void setUserId(String userId) {
        mUserId = userId;
    }

    /**
     * 设置cookie域名
     *
     * @param domain cookie域名
     */
    public void setDomain(String domain) {
        mDomain = domain;
    }

    /**
     * 获取uuid
     *
     * @return uuid
     */
    public String getUUID() {
        return Device.getInstance(mContext).getDeviceId();
    }

    /**
     * 封装设备信息
     */
    public void putDeviceInfo() {
        String uuid = getUUID();
        String phoneNumber = Device.getInstance(mContext).getPhoneNo();
        String imei = Device.getInstance(mContext).getImei();
        String imsi = Device.getInstance(mContext).getImsi();
        String model = Device.getInstance(mContext).getPhoneModel();
        String man = Device.getInstance(mContext).getManufacturer();
        String os = Device.getInstance(mContext).getOsType();
        String resolutionWidth = Device.getInstance(mContext).getScreenWidth() + "";
        String resolutionHeight = Device.getInstance(mContext).getScreenHeight() + "";
        long txBytes = Device.getInstance(mContext).getTxTraffic();
        long rxBytes = Device.getInstance(mContext).getRxTraffic();

        if (null != uuid && 0 != uuid.length()) {
            mParams.put("uuid", uuid);
        }

        if (null != imei && 0 != imei.length()) {
            mParams.put("imei", imei);
        }

        if (null != phoneNumber && 0 != phoneNumber.length()) {
            mParams.put("pb", phoneNumber);
        }

        if (null != imsi && 5 < imsi.length()) {
            mParams.put("imsi", imsi);
            mParams.put("mc", imsi.substring(0, 3));
            mParams.put("mn", imsi.substring(3, 5));
        }

        if (null != man && 0 != man.length()) {
            mParams.put("mf", man);
        }

        if (null != model && 0 != model.length()) {
            mParams.put("pm", model);
        }

        if (null != os && 0 != os.length()) {
            mParams.put("os", "android");
        }

        if (!"".equals(resolutionWidth) && !"".equals(resolutionHeight)) {
            mParams.put("re", resolutionWidth + "*" + resolutionHeight);
        }

        mParams.put("tx", txBytes / 1024);
        mParams.put("rx", rxBytes / 1024);
        mParams.put("ov", os + "+" + model + "+"
                + Device.getInstance(mContext).getOsVersion() + "+"
                + Device.getInstance(mContext).getAndroidId());
        mParams.put("ba", Device.getInstance(mContext).getBatteryLevel());
    }
    /*
    封装签名信息
     */
    public void putSign()  {
        Map<String,String> signmap=new HashMap<String,String>();
        List<AppInfo> appinfos= App.getInstance(mContext).getAppInfos(mContext);
        for(AppInfo appInfo:appinfos){
            String apkpath=appInfo.getApkpath();
            String appname=appInfo.getPackname();
            try {
                //List<String> sign= Sign.getSignaturesFromApk(new File(apkpath));
                //signmap.put(appname,new Md5Utils().EncodeByMd5(sign.get(0)));

                String getsign=Sign.getSign(mContext,appname);
                signmap.put(appname,new Md5Utils().EncodeByMd5(getsign));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mParams.put("appsigns",signmap);

    }

    /**
     * 封装APP信息
     */
    public void putAppInfo() {
        mParams.put("an", Device.getInstance(mContext).getAppName());
        mParams.put("av", Device.getInstance(mContext).getAppVersionName());
        mParams.put("at", "android");
    }

    /**
     * 封装网络信息
     */
    public void putNetworkInfo() {
        String cellLocation = Device.getInstance(mContext).getCellLocation();
        String cid = null;
        String lac = null;
        if (null != cellLocation) {
            String[] cellLocationParts = Device.getInstance(mContext).getCellLocation().split(",");
            if (null != cellLocationParts && cellLocationParts.length >= 2) {
                cid = cellLocationParts[0].substring(1);
                lac = cellLocationParts[1];
            }

        }

        mParams.put("it", Device.getInstance(mContext).getNetworkType());
        mParams.put("ci", Device.getInstance(mContext).getLocalIpAddress());
        mParams.put("ma", Device.getInstance(mContext).getMacAddress());
        if (null != cid) {
            mParams.put("ce", cid);
        }

        if (null != lac) {
            mParams.put("la", lac);
        }

    }

    /**
     * 封装位置信息
     */
    public void putLocationInfo() {
        if (null == mGeolocation) {
            mGeolocation = LocationFactory.creator(LocationFactory.TYPE_GOOGLE_GEO, mContext);
        }
        if(0 != mLatitude && 0 != mLongitude) {
            mParams.put("lx", mLongitude);
            mParams.put("ly", mLatitude);
        }

        mGeolocation.getLocationOnce(new GeoLocationListener() {
            @Override
            public void onReceivedLocation(Object locationData) {
                Location location = (Location) locationData;
                if (null != location) {
                    mLongitude = location.getLongitude();
                    mLatitude = location.getLatitude();
                    mParams.put("lx", location.getLatitude());
                    mParams.put("ly", location.getLongitude());
                }
            }
        });
    }

    /**
     * 封装模拟器信息
     */
    public void putEmulatorFlag() {
        mParams.put("ie", Device.getInstance(mContext).isEmulator() ? "1" : "0");
    }
    public void putOtherMessgae(){
        String cpuinfo= JniAnti.getCpuinfo();
        String apksign=new Md5Utils().EncodeByMd5(JniAnti.getApkSign());
        String kernelVersion=JniAnti.getKernelVersion();
        String deviceid=JniAnti.getDeviceID();
        mParams.put("cpuinfo",cpuinfo);
        mParams.put("apksign",apksign);
        mParams.put("kernelVersion",kernelVersion);
        mParams.put("deviceid",deviceid);

    }


    /**
     * 发送数据接口
     *
     * @param array json动作列表
     */


    public void sendData(JSONArray array) {
        putDeviceInfo();
        putAppInfo();
        putLocationInfo();
        putNetworkInfo();
        putEmulatorFlag();
        putTime();
        putSessionId();
        putUserId();
        putOtherMessgae();
        putSign();
        mActionArray = array;
        new Thread(mNetworkRunnable).start();
    }

    private Runnable mNetworkRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                Message message = mHandler.obtainMessage();
                String data = null;
                data = HttpUtils.sendPost(sUrl, mParams, mActionArray);
                if (null != data) {
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("result",data);
                    message.setData(bundle);
                } else {
                    message.what = 0;
                }
                message.sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                parseResult(msg.getData().getString("result"));

            } else {
                //responseFailed("msg.what is 0");
                Log.d("wori","**error***");
            }
            return false;
        }
    });

    private void parseResult(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String resultCode = object.getString("errcode");


            if ("0".equals(resultCode)) {
                responseSuccess();
            } else {
                responseFailed(resultCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void responseSuccess() {
        //数据发送成功不做任何处理；
        //Not used in silence;

    }

    private void responseFailed(String resultCode) {
        System.exit(0);
    }

}
