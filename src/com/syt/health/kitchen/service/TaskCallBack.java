package com.syt.health.kitchen.service;

public interface TaskCallBack<P,T> {

	public P getParameters();
	public void postExecute(T result);
}
