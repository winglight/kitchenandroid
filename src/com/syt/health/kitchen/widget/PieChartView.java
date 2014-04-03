package com.syt.health.kitchen.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syt.health.kitchen.StartupActivity;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.utils.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class PieChartView extends View {
	private Menu menu;
	private float left;
	private float right;
	private float top;
	private float bottom;
	private List<String> types;
	private int[][] colors = {{217,215,215},{240,233,195},{196,251,189},{201,230,240},{219,183,207},{240,194,194}};
	private List<Map<String, Integer>> maps = new ArrayList<Map<String,Integer>>();
	public PieChartView(Context context,Menu menu,List<String> types) {
		super(context);
		this.menu = menu;
		this.types = types;
		left = 100;
		top = 100;
		right = 450;
		bottom = 450;   
	}
	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < maps.size(); i++) {
			
			int start_angle = 0;
			Paint p = new Paint();
			p.setColor(Color.rgb(colors[i][0],colors[i][1],colors[i][2]));
			RectF rectF = new RectF(left, top, right, bottom);
			canvas.drawArc(rectF,start_angle,maps.get(i).get(types.get(i))/360, true, p);
			start_angle = start_angle+maps.get(i).get(types.get(i))/360;
			p.setAntiAlias(true);
		}
		
//		Paint p = new Paint();
//		p.setAntiAlias(true);
//		p.setColor(Color.GRAY);
//		RectF rectF = new RectF(left, top, right, bottom);
//		canvas.drawArc(rectF,0,angle, true, p);
//		
//		Paint p1 = new Paint();
//		p1.setAntiAlias(true);
//		p1.setColor(Color.BLUE);
//		RectF rectF1 = new RectF(left, top, right, bottom);
//		canvas.drawArc(rectF1, angle,(360-angle), true, p1);
//		
//		RectF rectF2 = new RectF(left+right, top, left+right-100,top+50);
//		canvas.drawRect(rectF2, p);
//		Paint text_p = new Paint();
//		text_p.setAntiAlias(true);
//		text_p.setColor(Color.GRAY);
//		text_p.setTextSize(30);
//		canvas.drawText("主食", left+right+10, top+30, text_p);
//		
//		RectF rectF3 = new RectF(left+right, top+70, left+right-100,top+120);
//		canvas.drawRect(rectF3, p1);
//		Paint text_p1 = new Paint();
//		text_p1.setAntiAlias(true);
//		text_p1.setColor(Color.BLUE);
//		text_p1.setTextSize(30);
//		canvas.drawText("荤", left+right+10, top+95, text_p1);
		super.onDraw(canvas);
	}
	private void calculateDeal(){
		List<Course> courses = new ArrayList<Course>();
		courses.addAll((Utils.convertMealCourse(menu.getBreakfast().getItems())));
		courses.addAll((Utils.convertMealCourse(menu.getLunch().getItems())));
		courses.addAll((Utils.convertMealCourse(menu.getDinner().getItems())));
		for (Course course : courses) {
			for (int i = 0; i < types.size(); i++) {
				int param = 0;
				if(course.getCoursecond().equals(types.get(i))){
					param = param+course.getCalories();
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put(types.get(i), param);
					maps.add(map);
				}
			}
		}
	}
	private List<Course> getCourse(MealCourse[]mealCourse){
		List<Course> courses = new ArrayList<Course>();
		for (int i = 0; i < mealCourse.length; i++) {
			courses.add(mealCourse[i].getCourse());
		}
		return courses;
	}
}
