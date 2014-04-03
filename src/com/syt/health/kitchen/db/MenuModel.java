package com.syt.health.kitchen.db;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.utils.Des3Util;

@DatabaseTable(tableName = "MENU")
public class MenuModel implements Serializable {

	public static final String TAG = "MenuModel";
	
	public static final String FIELD_OID = "OID";
	public static final String FIELD_JSON = "JSON";
	public final static String FIELD_DATE ="DATE";
	public final static String FIELD_USER ="USER";
	public final static String FIELD_SMART_PARAMS = "SMART_PARAMS";

	@DatabaseField(generatedId = true)
	private long id = -1;
	@DatabaseField(index = true, columnName = FIELD_OID)
	private String oid;//UUID of the server
	@DatabaseField(index = true, columnName = FIELD_JSON)
	private String json;
	@DatabaseField(columnName = FIELD_DATE)
	private String date;
	@DatabaseField(canBeNull = false, foreignAutoRefresh = false, foreign = true, columnName = FIELD_USER)
	private UserModel user;
	
	@DatabaseField(columnName = FIELD_SMART_PARAMS)
	private String smartParams;
	
	private String realMenu; //decrypted data
	
	private ObjectMapper mapper = new ObjectMapper();  
	
	public MenuModel(){
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getJson() {
		//decrypt data
		if(json != null && realMenu == null){
			realMenu = Des3Util.decode(json);
		}
		return realMenu;
	}

	public void setJson(String json) {
		if(json != null){
			realMenu = json;
			this.json = Des3Util.encode(json);
		}
	}
	
	public void setObjSmartParams(GenerateCondition smcp) {
		if (smcp != null)
			try {
				this.smartParams = mapper.writeValueAsString(smcp);
			} catch (JsonProcessingException e) {
				Log.e(TAG, e.getMessage());
			}
	}

	public GenerateCondition getObjSmartParams() {
		if (this.smartParams != null) {
			try {
				return mapper.readValue(this.smartParams, GenerateCondition.class);
			} catch (JsonParseException e) {
				Log.e(TAG, e.getMessage());
			} catch (JsonMappingException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			
		}
			return null;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public String getSmartParams() {
		return smartParams;
	}

	public void setSmartParams(String smartParams) {
		this.smartParams = smartParams;
	}
	
}
