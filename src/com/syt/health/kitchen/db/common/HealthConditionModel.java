package com.syt.health.kitchen.db.common;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "healthcond")
public class HealthConditionModel {

	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_CLASSIFY = "classifyid";

	@DatabaseField(id=true, index = true, columnName = FIELD_ID)
	private int id = -1;
	@DatabaseField(columnName = FIELD_NAME)
	private String name;
	@DatabaseField(columnName = FIELD_CLASSIFY)
	private int classify;

	public HealthConditionModel() {

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

	public int getClassify() {
		return classify;
	}

	public void setClassify(int classify) {
		this.classify = classify;
	}

	public String toString(){
		return name;
	}
}
