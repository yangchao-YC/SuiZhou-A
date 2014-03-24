package com.example.shenyunsuizhou;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.example.shenyunsuizhou.json.Normal;
import com.umeng.analytics.MobclickAgent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Base64;

/**
 * Json参数总结
 * title  			标题
 * note   			判断此列表是否为滑动列表 值为yes时
 * modified_time	时间
 * haveimg			值为yes为有图列表反之则无图
 * cnparams			有图列表与滑动列表显示图片
 * description		内容简介
 * zcategory		获取当前列表等级，如为no则为最下级列表，点击跳转至浏览页面
 * zcategoryurl		获取下级列表的申请数据地址
 * introtext		网页详情显示内容
 * 
 */

public class WebActivity extends Activity implements android.view.View.OnClickListener{

	private WebView webView;
	private Button backButton;
	private TextView tabTextView;
	private ImageView refreshBackButton;
	private ImageView backButtonImageView;
	private ImageView nextButtonImageView;
	private Handler webHandler = new Handler();

	private String barString = null;
	private String urlString = null;
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		barString = (String)getIntent().getExtras().getString("tabText");//接收导航条名
		urlString = (String)getIntent().getExtras().getString("url");//接收网页地址
		Log.e("---", urlString);
		
		webView = (WebView)findViewById(R.id.web_WebView);
		backButton = (Button)findViewById(R.id.web_Button_back);
		tabTextView = (TextView)findViewById(R.id.web_tabbarText);
		refreshBackButton = (ImageView)findViewById(R.id.web_Button_bottom_Refresh);
		backButtonImageView = (ImageView)findViewById(R.id.web_Button_bottom_Back);
		nextButtonImageView = (ImageView)findViewById(R.id.web_Button_bottom_Next);

		tabTextView.setText(barString);
		
		backButton.setOnClickListener(this);
		refreshBackButton.setOnClickListener(this);
		backButtonImageView.setOnClickListener(this);
		nextButtonImageView.setOnClickListener(this);
		
		web();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);
		
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});
	
		/*
		
		WebSettings webSettings = webView.getSettings();       
        webSettings.setJavaScriptEnabled(true);       
        webView.addJavascriptInterface(new Object() {       
            public void clickOnAndroid( String strings) {   
            	barString = strings;
                webHandler.post(new Runnable() {       
                    public void run() {       
                    	Log.e("9999------999", barString); 
                    	Play("http://www.w3school.com.cn/i/movie.mp4");
   
                    }       
                });       
            }       
        }, "demo"); 
      
		webView.loadUrl("file:///android_asset/articles.html");
		*/
	}

	private void Play(String url) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);  
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);  
		Intent mediaIntent = new Intent(Intent.ACTION_VIEW);  
		mediaIntent.setDataAndType(Uri.parse(url), mimeType);  
		startActivity(mediaIntent); 
	}
	
	private void web() {
		webView.loadUrl(urlString);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.web_Button_back:
			onBackPressed();
			break;
		case R.id.web_Button_bottom_Back:
			webView.goBack();
			break;
		case R.id.web_Button_bottom_Next:
			webView.goForward();
			break;
		case R.id.web_Button_bottom_Refresh:
			web();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	

}
