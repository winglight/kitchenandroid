package com.syt.health.kitchen.json;

/**
 * 同步资源信息
 * 
 * @author tom
 *
 */
public class SyncInfo {
	private String url;

	public SyncInfo() {
		super();
	}

	public SyncInfo(String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "SyncInfo [url=" + url + "]";
	}
}
