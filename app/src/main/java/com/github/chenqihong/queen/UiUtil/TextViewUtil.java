package com.github.chenqihong.queen.UiUtil;

import android.util.Log;

import org.json.JSONObject;

/**
 * TextView点击动作搜集
 * Encapsulate the TextView clicked data;
 */
public class TextViewUtil {
	private final static String TAG = "TextViewUtil";

	/**
	 * TextView点击动作收集
	 * Encapsulation
	 *
	 * @param target 标志
	 * @param title	标题
	 * @param tag	标记
	 * @param activityName	activity名字
	 * @return
	 */
	public static JSONObject textViewInfoGenerated(String target, String title, String tag, String activityName){
		long time = System.currentTimeMillis();
		JSONObject object = null;
		try{
			object = new JSONObject();
			object.put("tm", time);
			if(null != title){
				object.put("ti", title);
			}

			if(null != tag){
				object.put("tg",tag);
			}

			if(null != activityName){
				activityName = activityName.substring(activityName.lastIndexOf(".")+1, activityName.indexOf("@"));
				object.put("pi", activityName);
			}

			if(null != target){
				String[] splittedStr = target.split("app:id/");
				if(null != splittedStr){
					int length = 0;
					String tarStr = null;
					if(2 == splittedStr.length){
						length = splittedStr[1].length();
						tarStr = splittedStr[1].substring(0, length-1);
					}else{
						splittedStr = target.split("android:id/");
						if(2 == splittedStr.length){
							length = splittedStr[1].length();
							tarStr = splittedStr[1].substring(0, length-1);
						}else {
							tarStr = target;
						}
					}

					if(null != tarStr){
						object.put("ta", tarStr);
					}
				}
			}

			object.put("ac", "BC");
		}catch(Exception e){
			Log.e(TAG, "ButtonUtil:unknown error");
		}

		return object;
	}

}
