package com.example.shenyunsuizhou;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Normal {
	
	private Context currentContext;	

	public Normal(Context context){
		currentContext = context;
	}
	
	public Normal(){
	}
	
    public boolean note_Intent() {
    	ConnectivityManager con = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkinfo = con.getActiveNetworkInfo();
    	if (networkinfo == null || !networkinfo.isAvailable()) {
//	    	Toast.makeText(context.getApplicationContext(), "请检查网络连接",Toast.LENGTH_LONG).show();
	    	return false;
    	}
//    	boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//	    	.isConnectedOrConnecting();
//	    	if (!wifi) {
//	    	Toast.makeText(context.getApplicationContext(), "建议您使用WIFI以减少流量！",
//	    	Toast.LENGTH_SHORT).show();
//    	}
    	return true;
    }
    
    public String getFromAssets(String fileName){
        String result = "";
        try {
     	   InputStream in = currentContext.getResources().getAssets().open(fileName);    
     	   int length = in.available();    
     	   byte[]  buffer = new byte[length];   
     	   in.read(buffer);
     	   result = EncodingUtils.getString(buffer,"UTF-8");
 	   } catch (Exception e) {
 	       e.printStackTrace();
 	   }
        return result;
    }
    
	public String getWebviewData(String url){
		String webviewcontent = "暂无详细信息";
		Log.v("url", url);
		String datainfo = "";
		datainfo = TrycatchTime(url);
		webviewcontent = datainfo;
		return webviewcontent;
	}
	
	public String TrycatchTime(String url){
		String datainfo = "";
		try {
			datainfo = tryGetData(url,1);
			return datainfo;
		} catch (IOException e) {
			try {
				datainfo = tryGetData(url,2);
				return datainfo;
			} catch (IOException e1) {
				try {
					datainfo = tryGetData(url,3);
					return datainfo;
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return datainfo;
	}
	
	public String tryGetData(String url,int times) throws IOException{
//		Log.v("try times", String.valueOf(times));
		Connection c = Jsoup.connect(url);
		return c.post().toString();
	}
}
