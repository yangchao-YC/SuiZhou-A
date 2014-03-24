package com.example.shenyunsuizhou;


import java.util.ArrayList;
import java.util.Hashtable;

import com.example.shenyunsuizhou.json.DataManeger;
import com.example.shenyunsuizhou.json.Normal;
import com.example.shenyunsuizhou.json.Test_Bean;
import com.example.shenyunsuizhou.json.Y_Exception;
import com.umeng.analytics.MobclickAgent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
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

public class WebViewActivity extends Activity implements android.view.View.OnClickListener {
	
	ListViewActivity listViewActivity = new ListViewActivity();
	
	private WebView webView;//主体
	private Button backButton;//返回按钮
	private ImageView webBackButton;//布局页面下方向左箭头
	private ImageView webNextButton;//布局页面下方向右箭头
	private TextView tabTextView;
	private ImageView webShareButton;//布局页面下方分享图片
	private ImageView RefreshButton;//布局页面下方刷新图片
	private Handler webHandler = new Handler();
	private String webValue;//存储网页返回的值，一般为视频地址
	public static ArrayList<Hashtable<String, String>> data ;//获取数据，由上级页面传递
	public static int mark;//获取当前点击的是第几项，然后给web页面赋值
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		Log.e("-----", data.toString());
		Log.e("-----", ""+mark);

		webView = (WebView)findViewById(R.id.Web_WebView);
		backButton = (Button)findViewById(R.id.Web_Button_back);
		webBackButton = (ImageView)findViewById(R.id.Web_Button_bottom_Back);
		webNextButton = (ImageView)findViewById(R.id.Web_Button_bottom_Next);
		tabTextView = (TextView)findViewById(R.id.Web_tabbarText);
		webShareButton = (ImageView)findViewById(R.id.Web_Button_bottom_Share);
		RefreshButton = (ImageView)findViewById(R.id.Web_Button_bottom_Refresh);
		
		tabTextView.setText((mark+1) + "/" +data.size());//设置标题mark为数据的索引号，data为所有数据存储数组
		//绑定监听事件
		backButton.setOnClickListener(this);
		webBackButton.setOnClickListener(this);
		webNextButton.setOnClickListener(this);
		webShareButton.setOnClickListener(this);
		RefreshButton.setOnClickListener(this);
		//显示内容

		count(data.get(mark).get(listViewActivity.KEY_ID));
		initWeb(data.get(mark).get(listViewActivity.KEY_TITLE),
				data.get(mark).get(listViewActivity.KEY_INTROTEXT),
				data.get(mark).get(listViewActivity.KEY_TIME));
		

		webView.getSettings().setPluginsEnabled(true);
		/**
		 * 接收网页的值播放视频，本地html文件内设置支持Javascript
		 */
		WebSettings webSettings = webView.getSettings();       
        webSettings.setJavaScriptEnabled(true);       
        
        
        
        
        webView.addJavascriptInterface(new Object() {       
            public void clickOnAndroid( String strings) {   
            	webValue = strings;
                webHandler.post(new Runnable() {       
                    public void run() {       
                    	Log.e("9999------999", webValue);   
                    	Play(webValue);
                    }       
                });       
            }       
        }, "demo"); 
      
	//	webView.loadUrl("file:///android_asset/template.html");	

	}

	
	private void count(final String key)
	{
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String countURL= "http://119.36.193.148/suizhou/api/articlec/"+key;
					
					Test_Bean data = DataManeger.getTestData(countURL);
				} catch (Y_Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
	}
	
	/**
	 * 根据传递的url调用系统播放器进行视频播放
	 * @param url
	 */
	private void Play(String url) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);  
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);  
		Intent mediaIntent = new Intent(Intent.ACTION_VIEW);  
		mediaIntent.setDataAndType(Uri.parse(url), mimeType);  
		startActivity(mediaIntent); 
	}
	/**
	 * 
	 * @param title	标题
	 * @param introtext	内容
	 * @param time	时间
	 * 根据数据调用本地html进行网页显示
	 */
	private void initWeb(String title,String introtext,String time) {
		
		
		
		byte b[] = android.util.Base64.decode(introtext, Base64.DEFAULT);
		introtext = new String(b);
		
		Log.v("-----webView----", introtext);
		
		Normal normal = new Normal(this);
		String summary = normal.getFromAssets("template.html");
		summary = summary.replace(listViewActivity.KEY_TITLE+"String", title);
		summary = summary.replace(listViewActivity.KEY_INTROTEXT+"String", introtext);
		summary = summary.replace(listViewActivity.KEY_TIME+"String", time);
		webView.getSettings().setDefaultTextEncodingName("UTF-8"); 
		//mWebView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.loadDataWithBaseURL("file:///android_asset/",summary, "text/html", "UTF-8", "about:blank");
	
	}
	/**
	 * 向左图片调用方法，点击图片后进行判断此页面是否为第一页，不是则进行mark赋值，根据mark的值获取新页面需显示的数据，然后调用initWeb方法进行页面显示
	 */
	private void backWeb()
	{
		if (mark!=0) {
			if ((mark+1)>1) {
				mark--;
				Log.e("---1-", ""+mark);
				count(data.get(mark).get(listViewActivity.KEY_ID));
				initWeb(data.get(mark).get(listViewActivity.KEY_TITLE),
						data.get(mark).get(listViewActivity.KEY_INTROTEXT),
						data.get(mark).get(listViewActivity.KEY_TIME));
				tabTextView.setText((mark+1) + "/" +data.size());

			}
		}
		
	}
	/**
	 * 向右图片调用方法，点击图片后进行判断此页面是否为最后，不是则进行mark赋值，根据mark的值获取新页面需显示的数据，然后调用initWeb方法进行页面显示
	 */
	private void nextWeb()
	{
		if (data.size() ==(mark +1)) {
			
		}
		else {
		if (data.size()>mark) {
			mark++;
			Log.e("---2-", ""+mark);
			count(data.get(mark).get(listViewActivity.KEY_ID));
			initWeb(data.get(mark).get(listViewActivity.KEY_TITLE),
					data.get(mark).get(listViewActivity.KEY_INTROTEXT),
					data.get(mark).get(listViewActivity.KEY_TIME));
			tabTextView.setText((mark+1) + "/" +data.size());
		}
		
	}
	}
	
	/**
	 * 刷新按钮，再次调用initWeb方法进行数据刷新
	 */
	private void Refresh()
	{
		initWeb(data.get(mark).get(listViewActivity.KEY_TITLE),
				data.get(mark).get(listViewActivity.KEY_INTROTEXT),
				data.get(mark).get(listViewActivity.KEY_TIME));
		tabTextView.setText((mark +1) + "/" +data.size());
	}
	/**
	 * 分享图片触发时间，首先进行base64解码数据，然后调用系统检查当前邮件软件进行邮件的标题与内容赋值进行分享，此时设置为支持html显示
	 */
	private void Share()
	{		
		byte b[] = android.util.Base64.decode(data.get(mark).get(listViewActivity.KEY_INTROTEXT), Base64.DEFAULT);
		String introtext = new String(b);
		
		String titleS= data.get(mark).get(listViewActivity.KEY_TITLE);
		/*
		StringBuffer uriSb = new StringBuffer();
		uriSb.append("mailto:1111@qq.com")
		.append("?subject=").append(titleS)
		.append("&body=").append("body");
		Uri uri = Uri.parse(uriSb.toString());
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {""});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,titleS );
		emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(introtext));
		startActivity(Intent.createChooser(emailIntent, null));
		*/
		String content = Html.fromHtml(introtext).toString();

		Intent intent = new Intent(Intent.ACTION_SEND); //启动分享发送的属性
        intent.setType("text/plain");                                    //分享发送的数据类型
        intent.putExtra(Intent.EXTRA_SUBJECT, titleS);    //分享的主题
        intent.putExtra(Intent.EXTRA_TEXT, content);    //分享的内容
        intent.putExtra(Intent.EXTRA_TITLE, titleS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//这个也许是分享列表的背景吧
        startActivity(Intent.createChooser(intent, "分享"));//目标应用选择对话框的标题
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
		case R.id.Web_Button_back:
			onBackPressed();//返回上一级
			break;
		case R.id.Web_Button_bottom_Back:
			backWeb();
			break;
		case R.id.Web_Button_bottom_Next:
			nextWeb();
			break;
		case R.id.Web_Button_bottom_Refresh:
			Refresh();
			break;
		case R.id.Web_Button_bottom_Share:
			Share();
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
