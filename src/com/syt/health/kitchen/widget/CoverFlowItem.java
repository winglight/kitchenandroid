package com.syt.health.kitchen.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author Michael
 * Intention：For coverflowview to display every item
 * Description： generate every imageview by image and title
 */
public class CoverFlowItem extends ImageView {

	int mNumber;
	int mOriginalImageHeight;
	int mBitmapWidth = 0;
	int mBitmapHeight = 0;
	float mScaleX = 1;
	float mScaleY = 1;

	public CoverFlowItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CoverFlowItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CoverFlowItem(Context context) {
		super(context);
	}

	public void setScaleX(float x) {
		mScaleX = x;
	}
	
	public void setScaleY(float y) {
		mScaleY = y;
	}
	public void setNumber(int n) {
		mNumber = n;
	}

	public int getCoverWidth() {
		return (int)(mBitmapWidth * mScaleX);
	}

	public int getCoverHeight() {
		return (int)(mBitmapHeight * mScaleY);
	}

	public int getNumber() {
		return mNumber;
	}

	public int getOriginalCoverHeight() {
		return (int)(mOriginalImageHeight * mScaleY);
	}

	public void setImageBitmap(Bitmap bitmap, int originalImageHeight,
			float reflectionFraction) {
		mOriginalImageHeight = originalImageHeight;
		mBitmapWidth = bitmap.getWidth();
		mBitmapHeight = bitmap.getHeight();
		setLayoutParams(new ViewGroup.LayoutParams((int)(mBitmapWidth * mScaleX), (int)(mBitmapHeight * mScaleY)));
		setImageBitmap(bitmap);
	}

	public static Bitmap createReflectedBitmap(Bitmap b, String title, float reflectionFraction,
			int dropShadowRadius) {
		if (0 == reflectionFraction && 0 == dropShadowRadius)
			return b;

		Bitmap result;
		int padding = dropShadowRadius;

		// Create the result bitmap, in which we'll print the
		// original bitmap and its reflection
		result = Bitmap.createBitmap(b.getWidth() + padding * 2, 2 * padding
				+ (int) (b.getHeight() * (1 + reflectionFraction)) + 1,
				Config.ARGB_8888);

		// We'll work in a canvas
		Canvas canvas = new Canvas(result);

		// Add a drop shadow
//		Paint dropShadow = new Paint();
//		dropShadow.setShadowLayer(padding, 0, 0, 0xFF000000);
//		canvas.drawRect(padding, padding, b.getWidth() + padding, result
//				.getHeight()
//				- padding, dropShadow);
		
		// draw the original image
		canvas.drawBitmap(b, padding, padding, new Paint(Paint.ANTI_ALIAS_FLAG));
		
		// draw the reflection
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setAlpha(100);
//		Matrix matrix = new Matrix();
//		matrix.preScale(1, -1);
//		canvas.setMatrix(matrix);
//		int reflectionHeight = Math.round(reflectionFraction * b.getHeight());
//		canvas.drawBitmap(b, new Rect(0, b.getHeight() - reflectionHeight, b
//				.getWidth(), b.getHeight()), new Rect(padding,
//				-reflectionHeight - padding - b.getHeight() , padding
//						+ b.getWidth(), -padding - b.getHeight() - 1), paint);
//		canvas.setMatrix(new Matrix());
		
		// draw the gradient
//				LinearGradient shader = new LinearGradient(0, b.getHeight(), 0, result
//						.getHeight(), Color.TRANSPARENT, Color.WHITE, TileMode.REPEAT);
//				Paint paint = new Paint();  
//				paint.setShader(shader);
//				paint.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
//				canvas.drawRect(padding, padding + b.getHeight(), padding
//						+ b.getWidth(), padding + b.getHeight()
//						* (1 + reflectionFraction), paint);
		LinearGradient shader = new LinearGradient(0, b.getHeight(), 0, result
				.getHeight(), Color.argb(125, 221, 221, 221), Color.argb(250, 221, 221, 221), TileMode.CLAMP);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setShader(shader);
//		paint.setXfermode(new PorterDuffXfermode(Mode.DARKEN));
//		canvas.drawRect(padding, padding + b.getHeight(), padding
//				+ b.getWidth(), padding + b.getHeight()
//				* (1 + reflectionFraction) + 15, paint);
		
		return result;  
	}
}
