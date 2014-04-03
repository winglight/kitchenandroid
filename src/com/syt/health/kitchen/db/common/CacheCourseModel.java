package com.syt.health.kitchen.db.common;


import java.io.IOException;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.utils.Des3Util;

@DatabaseTable(tableName = "cachecourse")
public class CacheCourseModel {

	public static final String TAG = "CacheCourseModel";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_JSON = "json";

	@DatabaseField(id=true, index = true, columnName = FIELD_ID)
	private String id;
	@DatabaseField(columnName = FIELD_JSON)
	private String json;

	private String realCourse; //decrypted data
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public CacheCourseModel() {

	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getJson() {
		//decrypt data
		if(json != null && realCourse == null){
			realCourse = Des3Util.decode(json);
		}
		return realCourse;
	}

	public void setJson(String json) {
		if(json != null){
			realCourse = json;
			this.json = Des3Util.encode(json);
		}
	}
	
	public void setJson(Course course) {
		try {
			String str =  mapper.writeValueAsString(course);
			setJson(str);
		} catch (JsonProcessingException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
