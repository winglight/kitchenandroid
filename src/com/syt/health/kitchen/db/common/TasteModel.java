package com.syt.health.kitchen.db.common;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "taste")
public class TasteModel {

	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";

	@DatabaseField(id=true, index = true, columnName = FIELD_ID)
	private int id = -1;
	@DatabaseField(columnName = FIELD_NAME)
	private String name;

	public TasteModel() {

	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
