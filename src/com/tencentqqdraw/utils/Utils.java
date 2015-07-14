package com.tencentqqdraw.utils;

import java.lang.reflect.TypeVariable;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

public class Utils {
public static Toast mToast;
public static void showToast(Context mContext,String msg){
	if (mToast==null) {
		mToast=Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
	}
	mToast.setText(msg);
	mToast.show();
}
	
/**
 * @param dip
 * @param context
 * @return
 * dp转成px
 */
public static float dip2Dimension(float dip,Context context){
	DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);	
	
}
/**
 * @param dip
 * @param context
 * @param complexUnit
 * @return
 * dp转成px
 */
public static float toDimension(float dip,Context context,int complexUnit){
	DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
	return TypedValue.applyDimension(complexUnit, dip, displayMetrics);
	
}

/**
 * @param v
 * @return
 *获得状态栏高度
 *这里的状态栏是指显示信号，wifi，电量那一栏
 */
public static int getStatusBarHeight(View v){
	if (v==null) {
		return 0;
	}
	Rect frame=new Rect();
	v.getWindowVisibleDisplayFrame(frame);
	return frame.top;
	
}



}
