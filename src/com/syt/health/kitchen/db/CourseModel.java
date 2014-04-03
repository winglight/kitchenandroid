package com.syt.health.kitchen.db;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "COURSE")
public class CourseModel {

	public static final String FIELD_OID = "OID";
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_UNIT = "UNIT";
	public static final String FIELD_CALORIES = "CALORIES";
	public static final String FIELD_EFFECTIVITY = "EFFECTIVITY";
	public static final String FIELD_INCOMPATIBLE = "INCOMPATIBLE";
	public static final String FIELD_FOOD_ITEMS = "FOOD_ITEMS";
	public static final String FIELD_COOK_METHODS = "COOK_METHODS";
	public static final String FIELD_TOTAL_COOK_TIME = "TOTAL_COOK_TIME";
	public static final String FIELD_COOK_PRACTICE = "COOK_PRACTICE";

	@DatabaseField(generatedId = true)
	private long id = -1; 
	@DatabaseField(index = true, columnName = FIELD_OID)
	private String oid;//UUID of the server
	@ForeignCollectionField(eager = false, columnName = FIELD_FOOD_ITEMS)
	private List<CourseFoodItemModel> foodItems;
	@ForeignCollectionField(eager = false, columnName = FIELD_COOK_PRACTICE)
	private List<CookStepModel> cookSteps;
	@DatabaseField(columnName = FIELD_NAME)
	private String name;
	@DatabaseField(index = true, columnName = FIELD_UNIT)
	private String unit;
	@DatabaseField(columnName = FIELD_CALORIES)
	private int calories;
	@DatabaseField(index = true, columnName = FIELD_EFFECTIVITY)
	private String[] effectivity;
	@DatabaseField(columnName = FIELD_INCOMPATIBLE)
	private String[] incompatible;
	@DatabaseField(index = true, columnName = FIELD_COOK_METHODS)
	private String[] cookMethods;

	public CourseModel() {

	}
}
