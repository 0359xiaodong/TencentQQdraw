package com.tencentqqdraw;

import java.util.Random;

import com.nineoldandroids.view.ViewHelper;
import com.tencentqqdraw.bean.Cheeses;
import com.tencentqqdraw.drag.DragLayout;
import com.tencentqqdraw.drag.DragLayout.onDragStateChangeListener;
import com.tencentqqdraw.drag.MyRelativeLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView mMainList;
	private ListView mLeftList;
	private DragLayout mDragLayout;
	private View mHeader;
	private MyRelativeLayout mMainContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mMainContent = (MyRelativeLayout) findViewById(R.id.rl_main);
		mMainList = (ListView) findViewById(R.id.lv_main);
		mMainList.setDividerHeight(0);
		mMainList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Cheeses.NAMES));
		mLeftList = (ListView) findViewById(R.id.lv_left);
		mLeftList.setDividerHeight(0);

		mLeftList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Cheeses.CheeseStrings) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView mText = (TextView) view;
				mText.setTextSize(20);
				mText.setTextColor(Color.WHITE);
				return view;
			}
		});
		mHeader = findViewById(R.id.iv_header);
		mDragLayout = (DragLayout) findViewById(R.id.dl);

		mDragLayout
				.setOnDragStateChangeListener(new onDragStateChangeListener() {

					@Override
					public void onOpen() {
						Toast.makeText(getApplicationContext(), "onOpen", 0)
								.show();

						mLeftList.smoothScrollToPosition(new Random()
								.nextInt(26));
					}

					@Override
					public void onDraging(float percent) {
						ViewHelper.setAlpha(mHeader, 1 - percent);
					}

					@Override
					public void onClose() {
						Toast.makeText(getApplicationContext(), "onClose", 0)
								.show();
						// 最后一个参数是距离
						ObjectAnimator mAnim = ObjectAnimator.ofFloat(mHeader,
								"translationX", 7f);
						mAnim.setDuration(200);
						// 转的圈数
						mAnim.setInterpolator(new CycleInterpolator(3.0f));
						mAnim.start();
					}
				});

		mMainContent.setDragLayout(mDragLayout);

		mMainList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivity(new Intent(getApplicationContext(),
						DrActivity.class));

			}
		});
	}

}
