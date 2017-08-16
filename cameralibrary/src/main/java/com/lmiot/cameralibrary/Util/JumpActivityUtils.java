package com.lmiot.cameralibrary.Util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.lmiot.cameralibrary.R;

/**
 * Created by ming on 2016/9/1.
 */
public class JumpActivityUtils {




    /**
     * 跳转工具类
     * @param context 上下文
     * @param cls 跳转到的activity
     * @param isFinish 当前页面是否关闭
     * @param inFlag true表示进入动画，false表示退出动画
     */
    public static void JumpToActivity(Activity context, Class<?> cls, boolean isFinish,boolean inFlag) {

        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
        if (isFinish) {
            context.finish();
        }
        if(inFlag){
            context.overridePendingTransition(R.anim.move_left_out_activity, R.anim.move_right_in_activity);
        }
        else{
            context.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_in_activity);

        }
    }
}