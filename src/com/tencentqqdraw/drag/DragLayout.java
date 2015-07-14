package com.tencentqqdraw.drag;

import com.nineoldandroids.view.ViewHelper;
import com.tencentqqdraw.MainActivity;

import android.R.color;
import android.R.integer;
import android.content.Context;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Color;
import android.provider.OpenableColumns;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author Administrator 自定义拖拽控件
 */
public class DragLayout extends FrameLayout {
	private ViewDragHelper mDragHelper;
	private ViewGroup mLeftContent;
	// 由helper判断是否需要拦截处理
	private ViewGroup mMainContent;
	private int mWidth;
	private int mDragRange;
	private int mHeight;
	private int deltaXforLeft;

	public static enum Status {
		Open, Close, Draging
	}

	public static interface onDragStateChangeListener {
		void onOpen();

		void onClose();

		void onDraging(float percent);
	}

	private Status mStatus = Status.Close;
	private onDragStateChangeListener mOnDragStateChangeListener;

	public DragLayout(Context context) {
		this(context, null);

	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mDragHelper = ViewDragHelper.create(this, 1.0f, mCallBack);

	}

	ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {
		private float leftMove;

		// 决定了是否要拖拽当前的child,如果是true，随便拖哪个都行
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}

		/**
		 * 设置拖拽的横向范围（用来决定横向动画执行时间）
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return mDragRange;
		}

		// 决定了view将要放置的位置(在这里进行位置的修正)
		// 返回值觉得了它要到的位置
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// oldleft=dx=left
			if (child == mMainContent) {
				left = fixLeft(left);
			}

			return left;
		}

		// 控制主面板的范围
		private int fixLeft(int left) {
			if (left < 0) {
				return 0;

			} else if (left > mDragRange) {
				return mDragRange;
			}
			return left;
		}

		// 决定了当view位置被改变时，要做的其他事情（伴随动画）
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			int mMainLeft = mMainContent.getLeft();

			if (changedView != mMainContent) {
				mMainLeft = mMainLeft + dx;
			}
			// 修复并判断主面板的范围
			mMainLeft = fixLeft(mMainLeft);
			if (changedView == mLeftContent) {

				mLeftContent.layout(0, 0, mWidth, mHeight);
				mMainContent.layout(mMainLeft, 0, mMainLeft + mWidth, mHeight);

			}
			dispatchDragEvent(mMainLeft);
			// 支持低版本的操作，手动更新,或者是有些时候左边会留一点空隙
			invalidate();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			Log.d("Tag",
					"ismainContent"
							+ String.valueOf(releasedChild == mMainContent));
			Log.d("Tag", "xvel" + String.valueOf(xvel));
			Log.d("Tag", "deltaXforLeft" + deltaXforLeft);

			if (releasedChild == mMainContent) {
				if (xvel > 400) {
					Open();
				} else if (xvel == 0
						&& releasedChild.getLeft() >= mDragRange * 0.5f) {
					Open();
				} else {
					close();
				}
			}
			if (releasedChild == mLeftContent) {
				if (xvel > 0 || (xvel < 0 && deltaXforLeft < mDragRange * 0.2f)) {
					Open();

				} else if (xvel == 0) {
					Open();
				} else {
					close();
				}
			}

		}

	};

	public void close() {
		// releaseChild.layout(0, 0, mWidth, mHeight);
		mDragHelper.smoothSlideViewTo(mMainContent, 0, 0);
		ViewCompat.postInvalidateOnAnimation(DragLayout.this);
	}

	public void Open() {
		// releaseChild.layout(mDragRange, 0, mDragRange+mWidth, mHeight);
		mDragHelper.smoothSlideViewTo(mMainContent, mDragRange, 0);
		ViewCompat.postInvalidateOnAnimation(DragLayout.this);

	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	}

	protected void dispatchDragEvent(int mMainLeft) {

		// 只要有一个是float值，除出来的值就是float (0~100%)
		float percent = mMainLeft * 1.0f / mDragRange;

		animViews(percent);
		if (mOnDragStateChangeListener!=null) {
			mOnDragStateChangeListener.onDraging(percent);
		}
		Status lastStatus = mStatus;
		mStatus = updateStatus(percent);
		if (mStatus != lastStatus&&mOnDragStateChangeListener!=null) {
			if (mStatus == Status.Close) {
				mOnDragStateChangeListener.onClose();
			}else if (mStatus == Status.Open) {
				mOnDragStateChangeListener.onOpen();
			}
		}
	}

	private Status updateStatus(float percent) {
		if (percent == 0) {
			mStatus = Status.Close;
		} else if (percent == 1) {
			mStatus = Status.Open;
		} else {
			mStatus = Status.Draging;
		}
		return mStatus;
	}

	private void animViews(float percent) {
		//ViewHelper.setTranslationX(mMainContent,
		//		evaluate(percent, 0, mWidth*0.2f));
		// 1.主面板：缩放动画,100%~80%,图标为1-percent是0~1
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
		// 2.1左面板：缩放动画,缩放是以自己为中心的吧- -
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		// 2.2左面板：平移动画
		// -mWidth/2.0f----0
		ViewHelper.setTranslationX(mLeftContent,
				evaluate(percent, -mWidth / 2.0f, 0));
		// 2.3左面板：透明度变化
		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.0f, 1.0f));
		// 3.背景：亮度变化
		getBackground().setColorFilter(
				evaluateColor(percent, Color.BLACK, Color.TRANSPARENT),
				android.graphics.PorterDuff.Mode.SRC_OVER);
	}

	private int evaluateColor(float fraction, int startValue, int endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));

	}

	// FloatEvaluator估值器,简单的意思就是:开始+过程的多少%
	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int childCount = getChildCount();
		if (childCount < 2) {
			throw new IllegalStateException("you need 2child at least");
		}
		if (!(getChildAt(0) instanceof ViewGroup)
				|| !(getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalStateException("your children must be viewgroup");
		}

		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);

	}

	// onmeasure方法之后调用，刚开始是0，测量完就是maincontent之后就有值了，所以会调用
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = mMainContent.getMeasuredWidth();
		mHeight = mMainContent.getMeasuredHeight();
		//mDragRange = (int) (mWidth * 0.4f+0.5f);
		mDragRange = (int) (mWidth * 0.7f);

		// 初始化左面板的大小！赞！

		ViewHelper.setScaleX(mLeftContent, 0.5f);
		ViewHelper.setScaleY(mLeftContent, 0.5f);
		ViewHelper.setAlpha(mLeftContent, 0.0f);
		ViewHelper.setTranslationX(mLeftContent, -mWidth / 2.0f);
		getBackground().setColorFilter(Color.BLACK,
				android.graphics.PorterDuff.Mode.SRC_OVER);

	}

	private int x;
	private int upx;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		try {

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				x = (int) event.getX();

				break;

			case MotionEvent.ACTION_UP:
				upx = (int) event.getX();
				deltaXforLeft = Math.abs(upx - x);
				break;
			default:
				break;
			}

			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	public onDragStateChangeListener getOnDragStateChangeListener() {
		return mOnDragStateChangeListener;
	}

	public void setOnDragStateChangeListener(
			onDragStateChangeListener mOnDragStateChangeListener) {
		this.mOnDragStateChangeListener = mOnDragStateChangeListener;
	}

	public Status getStatus() {
		return mStatus;
	}

	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	};
}
