package com.lmiot.cameralibrary.Util;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.lmiot.cameralibrary.R;

/**
 * @Desc:
 * @Author: Jenchar
 * @Time: 2016/11/30 15:17
 */

public class LayoutDialogUtil {
	/**
	 * 创建新版对话框
	 * @param context
	 * @param layout
	 */
	public static Dialog createDailog(Context context,@LayoutRes int layout){
		Dialog dialog = new Dialog(context);

		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

			dialog.setContentView(layout);
			//设置对话框的大小
			Window dialogWindow = dialog.getWindow();
			dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_bg);
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.CENTER);
			lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialogWindow.setAttributes(lp);
			dialog.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return dialog;
	}

	/**
	 * 底部对话框
	 * @param context
	 * @param layout
     * @return
     */
	public static Dialog createBottomDailog(Context context,@LayoutRes int layout){
		Dialog dialog = new Dialog(context,R.style.ActionSheetDialogStyle);

		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

			dialog.setContentView(layout);
			//设置对话框的大小
			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialogWindow.setAttributes(lp);
			dialog.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return dialog;
	}

	/**
	 * 加载对话框
	 * @param context
	 * @param layout
     * @return
     */
	public static Dialog createLoadingDailog(Context context,@LayoutRes int layout){
		Dialog dialog = new Dialog(context,R.style.loadingdialog);

		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

			dialog.setContentView(layout);
			//设置对话框的大小
			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialogWindow.setAttributes(lp);
			dialog.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return dialog;
	}




}
