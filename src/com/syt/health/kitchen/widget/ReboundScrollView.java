package com.syt.health.kitchen.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class ReboundScrollView extends ScrollView {

	public ReboundScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	private View inner;//子view
	private float y;//点击时y坐标
	private Rect normal = new Rect();//矩形 判断是否要、需要动画
	private boolean isCount = false;
	/***  * 排除出第一次移动计算，因为第一次无法得知y坐标， 在MotionEvent.ACTION_DOWN中获取不到，
	 *   因为此时是MyScrollView的touch事件传递到到了LIstView的孩子item上面.所以从第二次计算开始. 
	 *  然而我们也要进行初始化，就是第一次移动的时候让滑动距离归0. 之后记录准确了就正常执行.  
	 *    */
	@Override
	protected void onFinishInflate() {
		if(getChildCount() > 0){
			inner = getChildAt(0);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(inner !=null){
			commOnTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}
	/**
	 * 触摸事件
	 * @param ev
	 */
	private void commOnTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			if(isNeedAnimation()){
				animation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			final float pressY = y;
			float nowY = ev.getY();
			int deltaY = (int)(pressY-nowY); //滑动的距离
			scrollBy(0, deltaY);	
			y = nowY;
			// 当滚动到最上或者最下时就不会再滚动，这时移动布局  
			if(isNeedMove()){
				if(normal.isEmpty()){
					normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
				}
				inner.layout(inner.getLeft(), inner.getTop()-deltaY/2, inner.getRight(), inner.getBottom()-deltaY/2);
			}
			isCount = false;
			break;
			default:
				break;
		}
	}
	/**
	 * 回缩动画
	 */
	private void animation(){
		//开启移动动画
		TranslateAnimation animation = new TranslateAnimation(0, 0, inner.getTop(), normal.top);
		animation.setDuration(200);
		inner.setAnimation(animation);
		//设置回到正常的布局位置
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);
		normal.setEmpty();
	}
	private boolean isNeedAnimation(){
		return !normal.isEmpty();
	}
	/**
	 * 是否需要移动布局 
	 * inner.getMeasuredHeight()获取控件的总高度
	 * getHeight()获取屏幕的高度
	 * @return
	 */
	private boolean isNeedMove(){
		int offset = inner.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		//0是顶部，offset是顶部
		if(scrollY == 0 || scrollY == offset){
			return true;
		}else{
			return false;
		}
	}
}
