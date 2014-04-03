package com.syt.health.kitchen.json;

import java.util.Arrays;
import java.util.List;

/**
 * 登录后返回的结果
 * 
 * @author tom
 *
 */
public class LoginToken {
	/**
	 * 登录成功后返回的身份令牌。用于后续访问其他功能接口
	 */
	private String tokenid;
	
	/**
	 * 该用户无权访问的功能模块ID。
	 */
	private List<Integer> forbidmodids;
	
	
	public LoginToken() {
		super();
	}

	public LoginToken(String tokenid, List<Integer> forbidmodids) {
		super();
		this.tokenid = tokenid;
		this.forbidmodids = forbidmodids;
	}

	public String getTokenid() {
		return tokenid;
	}

	public void setTokenid(String tokenid) {
		this.tokenid = tokenid;
	}


	public List<Integer> getForbidmodids() {
		return forbidmodids;
	}

	public void setForbidmodids(List<Integer> forbidmodids) {
		this.forbidmodids = forbidmodids;
	}

	@Override
	public String toString() {
		return "LoginToken [tokenid=" + tokenid + ", forbidmodids="
				+ forbidmodids + "]";
	}
}
