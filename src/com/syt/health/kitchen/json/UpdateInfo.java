package com.syt.health.kitchen.json;

import java.util.Arrays;

/**
 * 软件升级信息
 * 
 * @author tom
 *
 */
public class UpdateInfo {
	private String url;				// 下载地址
	private int newversionid;		// 升级的新版本ID
	private String newversion;		// 升级后的新版本号
	private String[] updatepoint;	// 该次升级的主要功能点
	private int isforce;			// 是否必须升级。0，不必须；1，必须
	
	public UpdateInfo() {
		super();
	}

	public UpdateInfo(String url, int newversionid, String newversion,
			String[] updatepoint, int isforce) {
		super();
		this.url = url;
		this.newversionid = newversionid;
		this.newversion = newversion;
		this.updatepoint = updatepoint;
		this.isforce = isforce;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getNewversionid() {
		return newversionid;
	}

	public void setNewversionid(int newversionid) {
		this.newversionid = newversionid;
	}

	public String getNewversion() {
		return newversion;
	}

	public void setNewversion(String newversion) {
		this.newversion = newversion;
	}

	public String[] getUpdatepoint() {
		return updatepoint;
	}

	public void setUpdatepoint(String[] updatepoint) {
		this.updatepoint = updatepoint;
	}

	public int getIsforce() {
		return isforce;
	}

	public void setIsforce(int isforce) {
		this.isforce = isforce;
	}

	@Override
	public String toString() {
		return "UpdateInfo [url=" + url + ", newversionid=" + newversionid
				+ ", newversion=" + newversion + ", updatepoint="
				+ Arrays.toString(updatepoint) + ", isforce=" + isforce + "]";
	}
	
}
