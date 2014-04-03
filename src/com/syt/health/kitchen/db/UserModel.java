package com.syt.health.kitchen.db;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.LoginToken;
import com.syt.health.kitchen.json.Return;

@DatabaseTable(tableName = "USER")
public class UserModel implements Serializable {

	public static final String TAG = "UserModel";
	
	public static final String FIELD_OID = "OID";
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_EMAIL = "EMAIL";
	public static final String FIELD_PHONE_NUMBER = "PHONE_NUMBER";
	public final static String FIELD_PASSWORD = "PASSWORD";
	public final static String FIELD_SID = "DEVICE_ID";
	public final static String FIELD_AVAILABLE_LIST = "AVAILABLE";
	public final static String FIELD_MENU = "MENUS";
	public final static String FIELD_SMART_PARAMS = "SMART_PARAMS";
	public final static String FIELD_OUT_PARAMS = "OUT_PARAMS";
	public final static String FIELD_FOOD_LIST = "FOOD_LIST";
	public final static String FIELD_FOOD_LIST_DESC = "FOOD_LIST_DESC";
	public final static String FIELD_OUTSIDE_HISTORY = "OUTSIDE_HISTORY";

	@DatabaseField(generatedId = true)
	private long id = -1;
	@DatabaseField(index = true, columnName = FIELD_OID)
	private String oid;// UUID of the server
	@DatabaseField(index = true, columnName = FIELD_NAME)
	private String name;
	@DatabaseField(index = true, columnName = FIELD_EMAIL)
	private String email;
	@DatabaseField(index = true, columnName = FIELD_PHONE_NUMBER)
	private String phoneNumber;
	@DatabaseField(columnName = FIELD_PASSWORD)
	private String password;
	@DatabaseField(columnName = FIELD_SID)
	private String sid;
	@DatabaseField(columnName = FIELD_SMART_PARAMS)
	private String smartParams;
//	@DatabaseField(columnName = FIELD_OUT_PARAMS)
//	private String outParams;
	@DatabaseField(columnName = FIELD_FOOD_LIST)
	private String foodList;
	@DatabaseField(columnName = FIELD_FOOD_LIST_DESC)
	private String foodListDesc;
//	@DatabaseField(columnName = FIELD_OUTSIDE_HISTORY)
//	private String outsideHistoryList;
	@DatabaseField(columnName = FIELD_AVAILABLE_LIST)
	private String forbids;
	// Non-DB field
	@ForeignCollectionField(eager = false)
	private Collection<MenuModel> menus;

	private ObjectMapper mapper = new ObjectMapper();

	public UserModel() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getFoodListDesc() {
		return foodListDesc;
	}

	public void setFoodListDesc(String foodListDesc) {
		this.foodListDesc = foodListDesc;
	}

	public List<Integer> getForbidList() {
		if (this.forbids != null) {
			String[] list = this.forbids.split(",");
			ArrayList<Integer> intList = new ArrayList<Integer>();
			for (String str : list) {
				intList.add(Integer.valueOf(str));
			}
			return intList;
		}
		return null;
	}

	public void setForbidList(List<Integer> forbidList) {
		if(forbidList != null && forbidList.size() > 0){
			this.forbids = "";
			for(int i : forbidList){
				this.forbids += i + ",";
			}
			if(this.forbids.endsWith(",")){
				this.forbids = this.forbids.substring(0, this.forbids.length()-1);
			}
		}
		this.forbids = null;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}


	public Collection<MenuModel> getMenus() {
		return menus;
	}

	public void setMenus(Collection<MenuModel> menus) {
		this.menus = menus;
	}

	public String getSmartParams() {
		return smartParams;
	}

	public void setSmartParams(String params) {
		this.smartParams = params;
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
			return new GenerateCondition();
	}
	
//	public void setObjOutParams(GenerateCondition smcp) {
//		if (smcp != null)
//			this.smartParams = gson.toJson(smcp, GenerateCondition.class);
//	}
//
//	public GenerateCondition getObjOutParams() {
//		if (this.smartParams != null) {
//			return gson.fromJson(this.smartParams, GenerateCondition.class);
//		} else {
//			return null;
//		}
//	}
	
	public void setObjFoodList(List<CourseFood> list) {
		if (list != null){
			try {
				this.foodList = mapper.writeValueAsString(list);
			} catch (JsonProcessingException e) {
				Log.e(TAG, e.getMessage());
			}
		}
			
	}

	public List<CourseFood> getObjFoodList() {
		if (this.foodList != null) {
			try {
				return mapper.readValue(this.foodList, new TypeReference<List<CourseFood>>() { });
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
	/**
	public void setObjOutsideHistoryList(List<RestaurantModel> list) {
		if (list != null){
			java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<RestaurantModel>>() {
			}.getType();
			
			this.outsideHistoryList = gson.toJson(list, type);
		}
			
	}

	public List<RestaurantModel> getObjOutsideHistoryList() {
		if (this.outsideHistoryList != null) {
			java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<RestaurantModel>>() {
			}.getType();
			return gson.fromJson(this.outsideHistoryList, type);
		} else {
			return null;
		}
	}**/

}
