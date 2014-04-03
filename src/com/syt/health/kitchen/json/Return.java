package com.syt.health.kitchen.json;

/**
 * WEBSERVICE返回结果的通用结构定义
 * 
 * @author tom
 *
 */
public class Return<T> {
	/**
	 * 返回结果的消息头
	 */
	private ReturnHead head;
	
	/**
	 * 返回结果的消息体
	 */
	private T data;
	
	private String encryptdata;
	
	public Return(){
		super();
	}
	
	public Return(ReturnHead head, T data) {
		super();
		this.head = head;
		this.data = data;
	}

	public ReturnHead getHead() {
		return head;
	}

	public void setHead(ReturnHead head) {
		this.head = head;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	

	public String getEncryptdata() {
		return encryptdata;
	}

	public void setEncryptdata(String encryptdata) {
		this.encryptdata = encryptdata;
	}

	@Override
	public String toString() {
		return "Return [head=" + head + ", data=" + data + "]";
	}
}
