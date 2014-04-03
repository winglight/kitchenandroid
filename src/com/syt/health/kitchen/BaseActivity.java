package com.syt.health.kitchen;


import java.io.IOException;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import com.syt.health.kitchen.fragment.RefreshableFragment;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.utils.ImageCache.ImageCacheParams;
import com.syt.health.kitchen.utils.DiskLruCache;
import com.syt.health.kitchen.utils.ImageCache;
import com.syt.health.kitchen.utils.ImageFetcher;
import com.syt.health.kitchen.utils.Utils;
import com.umeng.analytics.MobclickAgent;
   
public class BaseActivity extends FragmentActivity{

	private static final String IMAGE_CACHE_DIR = "images";
	
	//Asynchronously load image to ImageView   
	protected static ImageFetcher mImageFetcher;
	
	//Service instance for RPC or DB
	private ServiceImpl service;
	
	private final Object mClickLock = new Object();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onError(this);     
        
        ImageCacheParams cacheParams = new ImageCacheParams(this, IMAGE_CACHE_DIR);
          
        // Set memory cache to 25% of mem class
        cacheParams.setMemCacheSizePercent(this, 0.25f);

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this);
        mImageFetcher.setLoadingImage(R.drawable.s_logo_shrink);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        
        service = ServiceImpl.getInstance(this);
        
        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
        
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        
        Log.d("BaseActivity", "dpi:"+metrics.density*160);
        Log.d("BaseActivity", "n Brand => "+Build.BRAND);
        Log.d("BaseActivity", "n Device => "+Build.DEVICE);
    }
	
	public long getCacheSize(){
		return Utils.getDirSize(ImageCache.getDiskCacheDir(this, "http").getParentFile());
	}
	
	public void clearCacheDir(){
		try {
			DiskLruCache.deleteContents(ImageCache.getDiskCacheDir(this, IMAGE_CACHE_DIR));
			DiskLruCache.deleteContents(ImageCache.getDiskCacheDir(this, "http"));
		} catch (IOException e) {
			Log.e("BaseActivity", e.getMessage());
		}
//		mImageFetcher.clearCache();
	}
	
	private OnBackStackChangedListener getListener()
    {
        OnBackStackChangedListener result = new OnBackStackChangedListener()
        {
            public void onBackStackChanged() 
            {                   
                FragmentManager manager = getSupportFragmentManager();

                if (manager != null)
                {
                    Fragment currFrag = manager.
                    findFragmentById(R.id.activity_main_left_linear);
                    
                    if(currFrag != null && currFrag instanceof RefreshableFragment && service.getCurrentMenu() != null){
                    	synchronized (mClickLock) {
                    ((RefreshableFragment)currFrag).refreshData(service.getCurrentMenu());
                    mClickLock.notifyAll();
                		}
                    }
                }                   
            }
        };

        return result;
    }
	
	public void addFragment(Fragment f, String tag,int id){
		synchronized (mClickLock) {
            final FragmentTransaction ft = getSupportFragmentManager().
            		beginTransaction();
            if(getSupportFragmentManager().findFragmentByTag(tag) == null){
            	ft.add(id, f, tag).addToBackStack(tag);
            	
            }else{
            	ft.replace(id, f, tag).addToBackStack(tag);
            }
            ft.commit();
            mClickLock.notifyAll();
		}
            
	}
	
	public void clearFragment(){
		synchronized (mClickLock) {
		final FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    fm.popBackStack();
		}
		ft.commit();
		mClickLock.notifyAll();
	}
	}
	
	public void backFragment(Fragment f){
		synchronized (mClickLock) {
		final FragmentTransaction ft = getSupportFragmentManager().
        		beginTransaction();
		ft.remove(f);
		ft.commit();
		mClickLock.notifyAll();
	}
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(this instanceof NoteActivity){
        mImageFetcher.closeCache();
        service.close();
        }
    }
    
    public ServiceImpl getService(){
    	if(service == null){
    		service = ServiceImpl.getInstance(this);
    	}
    	return service;
    }

	public ImageFetcher getmImageFetcher() {
		return mImageFetcher;
	}
	
}
