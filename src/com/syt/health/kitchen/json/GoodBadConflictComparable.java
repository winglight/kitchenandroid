package com.syt.health.kitchen.json;

import java.util.List;

public interface GoodBadConflictComparable {
	
	public String getName();

	public boolean isKeWith(Course course);
	public boolean isKeWith(Food food);
	
	public List<String> getGoodDesc(List<String> health);
	public List<String> getBadDesc(List<String> health);
	public List<String> getConflictDesc(List<GoodBadConflictComparable> list);
}
