package com.syt.health.kitchen.db.common;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sys")
public class SysModel {

	public static final String FIELD_ID = "productid";
	public static final String FIELD_VERSION = "resversion";

	@DatabaseField(id=true, index = true, columnName = FIELD_ID)
	private int id = -1;
	@DatabaseField(columnName = FIELD_VERSION)
	private int version;

	public SysModel() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


}
