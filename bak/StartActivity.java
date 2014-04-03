package com.syt.health.kitchen;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.syt.health.kitchen.db.CommonDBOpenHelper;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

public class StartActivity extends Activity {

    private static final String LOGTAG = "StartActivity";

	@Override
    public void onCreate(Bundle savedInstanceState) {
		//init db file
        initDBFile();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        new Handler().postDelayed(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(StartActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1000);
        
        
    }
    
    private void initDBFile() {
		File dbfile = getDatabasePath(CommonDBOpenHelper.DATABASE_NAME);
		// judge if there exists the db file
		if (!dbfile.exists()) {
			// no db file then copy init db file from assets
			InputStream in = null;
			try {
				in = getAssets().open(CommonDBOpenHelper.DATABASE_NAME);
				if (in != null) {
					dbfile.getParentFile().mkdirs();
					FileOutputStream writer =  new FileOutputStream(dbfile);
					IOUtils.write(IOUtils.toByteArray(in), writer);
				}
			} catch (Exception e) {
				Log.e(LOGTAG , "copy init db exception:" + e.getMessage());
			}
		}

	}
}
