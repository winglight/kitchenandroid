package com.syt.health.kitchen.widget;

import com.syt.health.kitchen.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class Banner extends ImageView {

	private final Drawable logo;

	public Banner(Context context) {
		super(context);
		logo = this.getDrawable();
	}

	public Banner(Context context, AttributeSet attrs) {
		super(context, attrs);
		logo = this.getDrawable();
	}

	public Banner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		logo = this.getDrawable();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (logo != null) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = width * logo.getIntrinsicHeight()
					/ logo.getIntrinsicWidth();
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
