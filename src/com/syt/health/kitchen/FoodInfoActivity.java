package com.syt.health.kitchen;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.syt.health.kitchen.fragment.BuyingListFragment;
import com.syt.health.kitchen.fragment.FoodInfoFragment;
import com.syt.health.kitchen.json.Food;

public class FoodInfoActivity extends BaseActivity {

	public static final String FOODS_KEY = "foods";
	public static final String FOOD_KEY = "food";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		ArrayList<Food> foods = (ArrayList<Food>) intent
				.getSerializableExtra(FOODS_KEY);
		if (foods != null) {
			int index = intent.getIntExtra("index", 0);
			addFragment(FoodInfoFragment.newInstance(foods, index),
					FoodInfoFragment.TAG, android.R.id.content);
		} else {
			Food food = (Food) intent.getSerializableExtra(FOOD_KEY);
			addFragment(FoodInfoFragment.newInstance(food),
					FoodInfoFragment.TAG, android.R.id.content);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}