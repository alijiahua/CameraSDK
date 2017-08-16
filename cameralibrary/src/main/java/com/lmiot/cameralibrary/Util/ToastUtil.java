package com.lmiot.cameralibrary.Util;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * Copyright (c) 2014 惠州市英赛信息科技有限公司
 * 文件名：
 * 文件功能描述：UI帮助类
 * 创建人：helloyifa
 * 创建日期：2014-3-5下午2:19:25
 */
public class ToastUtil {
	private static Toast mToast = null;

	/**
	 * 弹出Toast消息
	 *
	 */
	public static void ToastMessage(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public static void ToastLongMessage(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		if (mToast == null) {
			mToast = Toast.makeText(cont, msg, time);
		} else {
			mToast.setText(msg);
			mToast.setDuration(time);
		}
		mToast.show();
	}
}
