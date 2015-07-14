package com.tencentqqdraw.reminder;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.tencentqqdraw.utils.GeometryUtil;
import com.tencentqqdraw.utils.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
/**
	 * 自定义粘性控件
	 * 
	 * @param context
	 */
public class GooView extends View {
	
	private Paint mPaint;
	public onStateChangeListener mStateChangeListener;

	public onStateChangeListener getStateChangeListener() {
		return mStateChangeListener;
	}

	public void setStateChangeListener(
			onStateChangeListener mStateChangeListener) {
		this.mStateChangeListener = mStateChangeListener;
	}

	public interface onStateChangeListener {

		/**
		 * 清除时进行的回调
		 */
		void onDisappear(PointF mDragCenter);

		/**
		 * 当恢复时进行回调
		 */
		void onReset(boolean isOutOfRange);

	}

	public GooView(Context context) {
		this(context, null);

	}

	public GooView(Context context, AttributeSet attrs) {
		this(context, attrs, 0); // TODO Auto-generated constructor stub
	}

	public GooView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.RED);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.WHITE);
		// 设置居中
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setTextSize(18);
	}

	PointF[] mStickPoints = new PointF[] { new PointF(250f, 250f),
			new PointF(250f, 350f)

	};
	PointF[] mDragPoints = new PointF[] { new PointF(50f, 250f),
			new PointF(50f, 350f)

	};

	// 初始化拖拽圆
	PointF mDragCenter = new PointF(150f, 150f);
	float mDragRadius = 16f;
	PointF mControlPoint = new PointF(0,0);
	// 初始化固定圆
	PointF mStickCenter = new PointF(150f, 150f);
	float mStickRadius = 12f;

	private int mStatusBarHeight;

	@Override
	protected void onDraw(Canvas canvas) {
		// 调用restore,回复到save之前的位置（之上）
	
		canvas.save();
		
		
		
		
		// 移动画布，调整高度
		canvas.translate(0, -mStatusBarHeight);
		mPaint.setStyle(Style.STROKE);
		canvas.drawCircle(mStickCenter.x, mStickCenter.y, mFarthest, mPaint);
		mPaint.setStyle(Style.FILL);
		// 计算坐标
		// 如果没有消失
		if (!isDisappear) {
			// 如果没有超出范围

			if (!isOutOfRange) {
				// 1.根据两元圆心间距，计算固定圆半径
				float distance = GeometryUtil.getDistanceBetween2Points(
						mDragCenter, mStickCenter);
				float tempRadius = getRadiusByDistance(distance);

				// 2.计算四个附着点坐标
				float offsetY = mStickCenter.y - mDragCenter.y;
				float offsetX = mStickCenter.x - mDragCenter.x;
				Double lineK = null;// 正切值
				if (offsetX != 0) {
					lineK = (double) (offsetY / offsetX);
				}
				mDragPoints = GeometryUtil.getIntersectionPoints(mDragCenter,
						mDragRadius, lineK);
				mStickPoints = GeometryUtil.getIntersectionPoints(mStickCenter,
						tempRadius, lineK);

				// 3.计算控制点坐标
				mControlPoint = GeometryUtil.getPointByPercent(mDragCenter,
						mStickCenter, 0.618f);

				// 画连接部分
				Path path = new Path();
				// 从哪个点开始
				path.moveTo(mStickPoints[0].x, mStickPoints[0].y);
				// 前两个点是控制点，后两个点是终点
				path.quadTo(mControlPoint.x, mControlPoint.y, mDragPoints[0].x,
						mDragPoints[0].y);
				// 画线
				path.lineTo(mDragPoints[1].x, mDragPoints[1].y);
				path.quadTo(mControlPoint.x, mControlPoint.y,
						mStickPoints[1].x, mStickPoints[1].y);
				path.close();
				canvas.drawPath(path, mPaint);

				// 画固定圆
				canvas.drawCircle(mStickCenter.x, mStickCenter.y, tempRadius,
						mPaint);
			}

			// 画拖拽圆
			canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, mPaint);

			canvas.drawText("7", mDragCenter.x, mDragCenter.y + mDragRadius
					/ 2.0f, mTextPaint);
		}
		canvas.restore();
	}

	/**
	 * 根据两圆间距，计算固定圆的半径
	 * 
	 * @param distance
	 * @return
	 */
	private float mFarthest = 80f;
	private Paint mTextPaint;
	// 是否超出最大范围
	private boolean isOutOfRange = false;
	private boolean isDisappear = false;

	private float getRadiusByDistance(float distance) {

		distance = Math.min(distance, mFarthest);
		float percent = distance / mFarthest;

		// 从原始半径缩放到40%

		return GeometryUtil
				.evaluate(percent, mStickRadius, mStickRadius * 0.4f);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (MotionEventCompat.getActionMasked(event)) {
		case MotionEvent.ACTION_DOWN:
			isOutOfRange = false;
			isDisappear = false;
			// 整个屏幕的xy
			float x = event.getRawX();
			float y = event.getRawY();
			updateDragCenter(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			float rawX = event.getRawX();
			float rawY = event.getRawY();
			updateDragCenter(rawX, rawY);
			float distanceBetween2Points = GeometryUtil
					.getDistanceBetween2Points(mDragCenter, mStickCenter);
			// 当拖拽超出范围,断开
			if (distanceBetween2Points > mFarthest) {
				isOutOfRange = true;
				invalidate();
			}

			break;
		case MotionEvent.ACTION_UP:

			if (!isOutOfRange) {
				// 当拖拽没有超出范围，松手，弹

				onViewReset();
			} else {
				float distance = GeometryUtil.getDistanceBetween2Points(
						mStickCenter, mDragCenter);

				if (distance < mFarthest) {
						isDisappear = false;
					
					// 当拖拽超出范围，放回去，松手,恢复
					updateDragCenter(mStickCenter.x, mStickCenter.y);
					
					if (mStateChangeListener!=null) {
						mStateChangeListener.onReset(isOutOfRange);
					}
				} else {

					// 当拖拽超出范围，没放回去，松手,清除
					isDisappear = true;
					invalidate();
					if (mStateChangeListener!=null) {
						mStateChangeListener.onDisappear(mDragCenter);
					}
				}

			}
			break;
		default:
			break;
		}

		return true;
	}

	private void onViewReset() {
		/**
		 * 值动画执行原理: 它有三个值，开始，结束（这两个都是float）,时间间隔.例如0~1(从1走到0,500毫秒内走完)如下
		 * 在0~1（100%）的过程中，再添加updatelistener，作出相应的响应回调事件
		 */
		ValueAnimator mAnim = ValueAnimator.ofFloat(1.0f);
		final PointF startP = new PointF(mDragCenter.x, mDragCenter.y);
		final PointF endP = new PointF(mStickCenter.x, mStickCenter.y);
		mAnim.setDuration(500);

		mAnim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				float Fraction = valueAnimator.getAnimatedFraction();
				PointF pointByPercent = GeometryUtil.getPointByPercent(
						startP, endP, Fraction);
				updateDragCenter(pointByPercent.x, pointByPercent.y);

			}
		});
		mAnim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (mStateChangeListener != null) {
					mStateChangeListener.onReset(isOutOfRange);
				}
				
			}
			
		});
		
		
		
		// tense:强度，越大，弹的范围越大
		mAnim.setInterpolator(new OvershootInterpolator(4.0f));

		mAnim.start();
	}

	/**
	 * 更新拖拽圆圆心坐标，同时界面的重绘
	 * 
	 * @param x
	 * @param y
	 */
	public void updateDragCenter(float x, float y) {
		mDragCenter.set(x, y);
		invalidate();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mStatusBarHeight = Utils.getStatusBarHeight(this);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int measuredHeight = measureHeight(heightMeasureSpec);

		int measuredWidth = measureWidth(widthMeasureSpec);

		setMeasuredDimension(measuredWidth, measuredHeight);
	
	}
	private int measureHeight(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.

		int result = 500;
		if (specMode == MeasureSpec.AT_MOST){

		// Calculate the ideal size of your
		// control within this maximum size.
		// If your control fills the available
		// space return the outer bound.

		result = specSize;
		}
		else if (specMode == MeasureSpec.EXACTLY){

		// If your control can fit within these bounds return that value.
		result = specSize;
		}

		return result;
		}
	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.
		int result = 100;
		if (specMode == MeasureSpec.AT_MOST){
		// Calculate the ideal size of your control
		// within this maximum size.
		// If your control fills the available space
		// return the outer bound.
		result = specSize;
		}

		else if (specMode == MeasureSpec.EXACTLY){
		// If your control can fit within these bounds return that value.

		result = specSize;
		}

		return result;
		}
}
