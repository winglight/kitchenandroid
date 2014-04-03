package com.syt.health.kitchen.db.common;


import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "healthcondclassify")
public class HealthCondClassifyModel {

	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";

	@DatabaseField(id=true, index = true, columnName = FIELD_ID)
	private int id = -1;
	@DatabaseField(columnName = FIELD_NAME)
	private String name;
	private List<HealthConditionModel> subList;

	public HealthCondClassifyModel() {

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

	public List<HealthConditionModel> getSubList() {
		return subList;
	}

	public void setSubList(List<HealthConditionModel> subList) {
		this.subList = subList;
	}
	
	public String toString(){
		return name;
	}

}
