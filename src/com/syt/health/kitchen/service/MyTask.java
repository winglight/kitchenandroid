package com.syt.health.kitchen.service;

import com.syt.health.kitchen.utils.AsyncTask;

public abstract class MyTask<Params, Result> extends AsyncTask<Params, Integer, Result>{

	private TaskCallBack<Params, Result> callback;
	
	public MyTask(TaskCallBack<Params, Result> callback){
		this.callback = callback;
	}
	
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		callback.postExecute(result);  
	}	
}
