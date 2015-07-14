package com.tencentqqdraw.utils;

import android.graphics.PointF;

public class GeometryUtil {

	/**
	 * @param p0
	 * @param p1
	 * @return
	 * 获得两点之间的距离
	 */
	public static float getDistanceBetween2Points(PointF p0,PointF p1){
		float distance=(float) Math.sqrt(Math.pow(p0.y-p1.y, 2)+Math.pow(p0.x-p1.x, 2));
		return distance;
	}
	
	
	
	/**
	 * @param p1
	 * @param p2
	 * @return
	 * 获得两点的中点
	 */
	public static PointF getMiddlePoint(PointF p1,PointF p2){
		
		return new PointF((p1.x+p2.x)/2.0f,(p1.y+p2.y)/2.0f);
	}

/**
 * @param p1
 * @param p2
 * @param percent
 * @return
 * 根据百分比获取两点之间的某个点坐标
 */
public static PointF getPointByPercent(PointF p1,PointF p2,float percent){
	return new PointF(evaluate(percent, p1.x, p2.x),evaluate(percent, p1.y, p2.y));
	
}	




/**
 * @param fraction
 * @param startValue
 * @param endValue
 * @return
 * 估值器
 */
public static Float evaluate(float fraction, Number startValue, Number endValue) {
	float startFloat = startValue.floatValue();
	return startFloat + fraction * (endValue.floatValue() - startFloat);
}


/**
 * 获取   通过制定圆心的直线与圆的交点
 * @param pMiddle
 * @param radius
 * @param lineK 目标角度的正切值
 * @return
 */
public static PointF[]getIntersectionPoints(PointF pMiddle,float radius,Double lineK){
	//数组一定要初始化其大小
	PointF[] points=new PointF[2];
	
	float radian,xOffset=0,yOffset=0;
	if (lineK!=null) {
		radian=(float) Math.atan(lineK);
		xOffset=(float) (Math.sin(radian)*radius);
		yOffset=(float) (Math.cos(radian)*radius);
	}else {
		xOffset=radius;
		yOffset=0;
		
	}
	points[0]=new PointF(pMiddle.x+xOffset,pMiddle.y-yOffset);
	points[1]=new PointF(pMiddle.x-xOffset,pMiddle.y+yOffset);
	
	
	
	return points;
	
}
}
