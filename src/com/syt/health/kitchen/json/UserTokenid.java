package com.syt.health.kitchen.json;

/**
 * tokenid和userid的影射。每次登录成功，就为该userid分配一个临时的tokenid
 * 
 * @author tom
 *
 */
public class UserTokenid {
	public static final String COLL = "usertoken";
	public static final String USERID_FIELD = "userid";
	public static final String DATE_FIELD = "milldate";
	
	private String userid;
	private String tokenid;
	private long millDate;	// 用毫秒数表示的最近访问日期
	
	public UserTokenid() {
		super();
	}

	public UserTokenid(String userid, String tokenid, long millDate) {
		super();
		this.userid = userid;
		this.tokenid = tokenid;
		this.millDate = millDate;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getTokenid() {
		return tokenid;
	}

	public void setTokenid(String tokenid) {
		this.tokenid = tokenid;
	}

	public long getMillDate() {
		return millDate;
	}

	public void setMillDate(long millDate) {
		this.millDate = millDate;
	}

	@Override
	public String toString() {
		return "UserTokenid [userid=" + userid + ", tokenid=" + tokenid
				+ ", millDate=" + millDate + "]";
	}
}
