package com.syt.health.kitchen.json;

/**
 * 返回结果的消息头
 * 
 * @author tom
 *
 */
public class ReturnHead {
	/**
	 * 结果码。0是成功，其他表示失败
	 */
	private int ret;
	
	/**
	 * 结果描述。
	 */
	private String msg;
	
	public ReturnHead(){
		super();
	}
	
	public ReturnHead(int ret, String msg) {
		super();
		this.ret = ret;
		this.msg = msg;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return "ReturnHead [ret=" + ret + ", msg=" + msg + "]";
	}
		
}
