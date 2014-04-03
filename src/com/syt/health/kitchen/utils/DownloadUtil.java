package com.syt.health.kitchen.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.os.Handler;
import android.util.Log;

public class DownloadUtil {

	protected static final String LOGTAG = "DownloadUtil";


	public DownloadUtil() {
	}

	public void runSaveUrl(final List<String> postUrl, String path,
			final Handler handler) {

		if (!path.endsWith("/")) {
			path += "/";
		}

		for (final String purl : postUrl) {
			 String fileName = path + purl.substring(purl.lastIndexOf("/")+1);

			final File tmp = new File(fileName);
			if (tmp.exists()) {
				continue;
			}
//			saveHtml(purl, fileName);
			Log.d(LOGTAG, "finished download: "
					+ purl);
		}
	}
	
//	public String saveHtml(String purl, String path){
//		try {
//
//			URL url = new URL(purl);
//		     HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//		     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//		     
//		     String html = IOUtils.toString(in);
//		     Source source = new Source(html);
//		     
//		     List<Element> list = source.getAllElements(HTMLElementName.IMG);
//		     for(Element ele : list){
//		    	 String imgUrl = ele.getAttributeValue("src");
//		    	 String iPath = path.substring(0, path.lastIndexOf("/")+1) + imgUrl.substring(imgUrl.lastIndexOf("/")+1);
//		    	 html = html.replace(imgUrl, imgUrl.substring(imgUrl.lastIndexOf("/")+1));
//		    	//TODO: temporarily add prefix
//		    	 if(imgUrl.startsWith("/")){
//		    		 imgUrl = "http://42.96.139.238:8080" + imgUrl;
//		    	 }
//		    	 saveImg(imgUrl, iPath);
//		     }
//		     FileOutputStream fos = new FileOutputStream(new File(path));
//
//		     IOUtils.write(html, fos);
//		     
//		     urlConnection.disconnect();
//		     in.close();
//		     fos.close();
//		     
//		     return path;
//
//		} catch (OutOfMemoryError e) {
//			Log.e(LOGTAG, "Out of memory error :(");
//		} catch (Exception e) {
//			Log.e(LOGTAG, "download exception url:"
//					+ purl);
//		}
//		
//		return null;
//	}
	
	private void saveImg(String purl, String path){
		try {

			URL url = new URL(purl);
		     HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

		     InputStream in = new BufferedInputStream(urlConnection.getInputStream());

		     
		     FileOutputStream fos = new FileOutputStream(new File(path));

		     IOUtils.write(IOUtils.toByteArray(in), fos);
		     
		     urlConnection.disconnect();
		     in.close();
		     fos.close();
		     Log.d(LOGTAG, "finished download image: "
						+ purl);
		} catch (Exception e) {
			Log.e(LOGTAG, "saveImg exception url:"
					+ purl);

		} 
	}


}
