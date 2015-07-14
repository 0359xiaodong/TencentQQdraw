package com.tencentqqdraw.drag;

import com.tencentqqdraw.drag.DragLayout.Status;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MyRelativeLayout extends RelativeLayout {
	private DragLayout mDragLayout;

	public MyRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DragLayout getDragLayout() {
		return mDragLayout;
	}

	public void setDragLayout(DragLayout mDragLayout) {
		this.mDragLayout = mDragLayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (mDragLayout.getStatus() == Status.Close) {
			return super.onInterceptTouchEvent(ev);
		} else {
			return true;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mDragLayout.getStatus() == Status.Close) {
			return super.onTouchEvent(event);
		} else {
//			if (MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_UP) {
//				//mDragLayout.Open();
//			}
			return true;
		}
		
	}
}
