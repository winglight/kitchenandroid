package com.syt.health.kitchen.json;

public class User {
	public static final String COLL = "user";
	public static final String EMAIL_FIELD = "email";
	public static final String MOBILE_FIELD = "mobile";
	public static final String PASSWD_FIELD = "passwd";
	
	private String id;		// 帐号ID
	private String email;	// 帐号email
	private String mobile;	// 手机号码
	private String passwd;	// 密码
	
	public User() {
		super();
	}

	public User(String id, String email, String mobile, String passwd) {
		super();
		this.id = id;
		this.email = email;
		this.mobile = mobile;
		this.passwd = passwd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", mobile=" + mobile
				+ ", passwd=" + passwd + "]";
	}
}
