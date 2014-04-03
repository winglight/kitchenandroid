package com.syt.health.kitchen.db;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MEAL_ITEM")
public class MealItemModel {

	public static final String FIELD_OID = "OID";
	public static final String FIELD_MEAL = "MEAL";
	public static final String FIELD_COURSE = "COURSE";
	public static final String FIELD_QUANTITY = "QUANTITY";
	public static final String FIELD_UNIT = "UNIT";

	@DatabaseField(generatedId = true)
	private long id = -1;
	@DatabaseField(index = true, columnName = FIELD_OID)
	private String oid;//UUID of the server
	@DatabaseField(canBeNull = false, foreignAutoRefresh = false, columnName = FIELD_MEAL)
	private MealModel meal;
	@DatabaseField(canBeNull = false, foreignAutoRefresh = true, columnName = FIELD_COURSE)
	private CourseModel course;
	@DatabaseField(columnName = FIELD_QUANTITY)
	private int quantity;
	@DatabaseField(index = true, columnName = FIELD_UNIT)
	private String unit;

	public MealItemModel() {

	}
}
