package com.syt.health.kitchen.db;

import java.io.Serializable;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MEAL")
public class MealModel implements Serializable {

	public static final String FIELD_OID = "OID";
	public static final String FIELD_SELECT_TYPE = "SELECT_TYPE";
	public static final String FIELD_MEAL_TYPE = "MEAL_TYPE";
	public static final String FIELD_MEAL_ITEMS = "MEAL_ITEMS";
	public final static String FIELD_DATE ="DATE";
	public final static String FIELD_MENU_ID ="MENU_ID";

	@DatabaseField(generatedId = true)
	private long id = -1;
	@DatabaseField(index = true, columnName = FIELD_OID)
	private String oid;//UUID of the server
	@DatabaseField(index = true, columnName = FIELD_SELECT_TYPE)
	private String name;
	@DatabaseField(index = true, columnName = FIELD_MEAL_TYPE)
	private String email;
	@ForeignCollectionField(eager = false, columnName = FIELD_MEAL_ITEMS)
	private List<MealItemModel> phoneNumber;
	@DatabaseField(columnName = FIELD_DATE)
	private String date;
	@DatabaseField( columnName = FIELD_MENU_ID)
	private String menuId;

	public MealModel() {

	}
}
