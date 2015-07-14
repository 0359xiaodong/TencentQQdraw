package com.tencentqqdraw;

import com.tencentqqdraw.reminder.GooView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.PorterDuff.Mode;
import android.view.Menu;
import android.view.Window;
import android.view.View.MeasureSpec;

public class GooActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(new GooView(this));
		setContentView(R.layout.dragpoint_main);
	
	}



}
