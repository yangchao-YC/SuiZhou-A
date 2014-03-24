package com.example.shenyunsuizhou;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.example.shenyunsuizhou.json.DataManeger;
import com.example.shenyunsuizhou.json.Test_Bean;
import com.example.shenyunsuizhou.json.Test_Model;
import com.example.shenyunsuizhou.json.Y_Exception;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

public class HomeActivity extends Activity implements OnTouchListener,OnGestureListener{
	/**
	 * Json参数总结
	 * title  			标题
	 * note   			判断当前列表是否为滑动列表 值为yes时
	 * azhuadong		判断下级页面是否为滑动列表 值为yes为滑动
	 * modified_time	时间
	 * introtext		网页详情显示内容/或存放网页地址
	 * haveimg			值为yes为有图列表反之则无图
	 * cnparams			有图列表与滑动列表显示图片
	 * description		内容简介
	 * zcategory		获取当前列表等级，如为no则为最下级列表，点击跳转至浏览页面
	 * zcategoryurl		获取下级列表的申请数据地址

	 * 直接跳转至网页页面为：联通营业厅，公积金帐号	索引号为 14，15号
	 * 跳一级列表然后直接跳转至网页页面为：主持人微博，企业风采（3个），索引号为7，8，9，12
	 * 跳转至开发页面为：社保账户，医保帐号，评选活动，关于本应用页面（显示本应用介绍） 索引号为17，18，21，11
	 * 
	 * 此处设计不合理，过于复杂与容易出错，建议后期更改为直接获取服务器数据，添加到gridView列表中
	 */
	
	GestureDetector mGestureDetector;  
	private static final int FLING_MIN_DISTANCE = 200;  
	private static final int FLING_MIN_VELOCITY = 0;
	ArrayList<HashMap<String, Object>> arrayList;

	private String vercode;
	private static String packgeName;
	private ProgressDialog progressDialog;
	private String packsString =null;
	private String upadteURL =null;
	private String urlString ="http://119.36.193.148/suizhou/api/categories/155?op=all";//更新地址

	private int[] images= {//存放各个模块图标
			R.drawable.home1,R.drawable.home2,R.drawable.home3,R.drawable.home4,
			R.drawable.home5,R.drawable.home6,R.drawable.home7,R.drawable.home8,
			R.drawable.home9,R.drawable.home10,R.drawable.home11,R.drawable.home12,
			R.drawable.home13,R.drawable.home14,R.drawable.home15,R.drawable.home16,
			R.drawable.home17,R.drawable.home18,R.drawable.home19,R.drawable.home20,
			R.drawable.home21,R.drawable.home22
	};
	private String[] nameStrings = {//存放各个模块名称
			"随州介绍","旅游资源","日版电子报","随州新闻",
			"政务公开","清廉随州","专题视频","主持人微博",
			"企业风采—特汽","企业风采—工业","节目直播","关于本应用",
			"企业风采—农业","新闻视频","联通手机业务","联通营业厅",
			"公积金帐号","社保账户","医保帐号","招聘信息",
			"楚天都市报","评选活动"
	};
	private String[] urlStrings = {//存放各个模块数据获取地址
			"http://119.36.193.148/suizhou/api/categories/86?op=all",
			"http://119.36.193.148/suizhou/api/categories/95?op=all",
			"http://119.36.193.148/suizhou/api/categories/107?op=all",
			"http://119.36.193.148/suizhou/api/categories/112?op=all",
			"http://119.36.193.148/suizhou/api/categories/114?op=all",
			"http://119.36.193.148/suizhou/api/categories/127?op=all",
			"http://119.36.193.148/suizhou/api/categories/134?op=all",
			"http://119.36.193.148/suizhou/api/articles/?catid=135&op=all",
			"http://119.36.193.148/suizhou/api/articles/?catid=136&op=all",
			"http://119.36.193.148/suizhou/api/articles/?catid=137&op=all",
			"http://119.36.193.148/suizhou/api/articles/?catid=138&op=all",
			//"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=86&statez=1",//随州介绍
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=95&statez=1",//旅游资源
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=107&statez=1",//日版电子报
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=112&statez=1",	//随州新闻
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=114&statez=1",	//政务公开	
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=127&statez=1",//清廉随州
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=134&statez=1",//专题视频
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=135&statez=2",//主持人微博
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=136&statez=2",	//企业风采-特汽
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=137&statez=2",//企业风采-工业
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=138&statez=2",//节目直播
			"-",//关于本应用
			"http://119.36.193.148/suizhou/api/articles/?catid=139&op=all",
			"http://119.36.193.148/suizhou/api/articles/?catid=140&op=all",
			"http://119.36.193.148/suizhou/api/articles/?catid=141&op=all",
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=139&statez=2",//企业风采-农业
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=140&statez=2",	//新闻视频
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=141&statez=2",//联通手机业务
			"-",//联通营业厅
			"-",//公积金帐号
			"-",//社保账户
			"-",//医保帐号	
			"http://119.36.193.148/suizhou/api/articles/?catid=142&op=all",
			"http://119.36.193.148/suizhou/api/categories/143?op=all",
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=142&statez=2",//招聘信息
//			"http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=143&statez=1",//楚天都市报
			"-"//评选活动

	};

	
	private String [] urlCount = {
			"86","95","107","112","114",
			"127","134","135","136","137",
			"138","-","139","140","141",
			"-","-","-","-","142",
			"143","-"	
	};
	
	private GridView gridView;	//当前页面才用gridView布局
	private Button siteButton;	//跳转至设置页面
	private RadioButton oneRadioButton;//下方记录当前页面翻页图片，此为第一张图，以下2个为另外2个翻页图片
	private RadioButton twoRadioButton;
	private RadioButton threeRadioButton;
	private String homeString = "no";
	
	private int mark = 0;	//记录当前页数，默认值为1，则进入程序后显示第一页，一共3页
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		 mGestureDetector = new GestureDetector(this); 
		siteButton  =(Button)findViewById(R.id.Home_Button_site);
	    gridView = (GridView) findViewById(R.id.Home_GridView);
	    homeString = getIntent().getExtras().getString("home");
	    
	    
	    oneRadioButton = (RadioButton)findViewById(R.id.Home_top_image1);
	    twoRadioButton = (RadioButton)findViewById(R.id.Home_top_image2);
	    threeRadioButton = (RadioButton)findViewById(R.id.Home_top_image3);

	    gridView.setOnTouchListener(this);
	   // gridView.setLongClickable(true);
	    gridView.setOnItemClickListener(new GridOnClick());
	    Grid();
	    Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()  && homeString.equals("yes")) {// 判断是否有网络连接
			packge();
		}
	    
	    oneRadioButton.setChecked(true);
	    /**
	     * 设置按钮监听器，跳转至设置页面
	     */
	    siteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this,SiteActivity.class);
				startActivity(intent);
				
			}
		});
	}
	/**
	 * 获取GridView数据
	 * count为九宫格页面设置基数，布局文件内设置每行最多为3个，此参数控制每页最多显示9个图标
	 * 
	 */
	private void Grid() {
		
		int count =9;
		//判断当前为第几页，根据页数计算当前页该显示多少模块图标，显示哪些模块图标
		if ((images.length - ((mark +1)*9))<0) {
			Log.v("--93-", ""+mark);
			count = images.length;
		}
		else {
			Log.v("--97-", ""+mark);
			count = (mark +1) *9;
		}
		arrayList = new ArrayList<HashMap<String, Object>>();
		//根据当前的页数填充显示信息
		for (int i = (mark*9); i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", images[i]);
			map.put("ItemText", nameStrings[i]);
			arrayList.add(map);
		}
		
		//判断当前页面设置下面页面图片该选中哪个
		if (mark == 0) {
			oneRadioButton.setChecked(true);
		}
		if (mark == 1) {
			twoRadioButton.setChecked(true);
		}
		if (mark == 2) {
			threeRadioButton.setChecked(true);
		}
		SimpleAdapter adapter = new SimpleAdapter(HomeActivity.this, arrayList,
				R.layout.home_gridview, new String[] { "ItemImage",
						"ItemText" }, new int[] {
						R.id.Home_Grid_Image, R.id.Home_Grid_Text_Name });
		gridView.setAdapter(adapter);
		
		
	}
	/**
	 * 根据原始程序分析每个模块的显示信息
	 * 直接跳转至网页页面为：联通营业厅，公积金帐号	索引号为 14，15号
	 * 跳一级列表然后直接跳转至网页页面为：主持人微博，企业风采（3个），索引号为7，8，9，12
	 * 跳转至开发页面为：社保账户，医保帐号，评选活动，关于本应用页面（显示本应用介绍） 索引号为17，18，21，11
	 */
	class GridOnClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent;
			
			 
			 arg2 = arg2 + (mark *9);
			 Log.v("点击项", ""+arg2);
			if (arg2==15  || arg2 == 16) {
				if (arg2 ==15) {
					intent = new Intent(HomeActivity.this,WebActivity.class);
					intent.putExtra("tabText", nameStrings[arg2]);
					intent.putExtra("url", "http://mob.10010.com/");
					startActivity(intent);
				}
				if (arg2 == 16) {
					intent = new Intent(HomeActivity.this,WebActivity.class);
					intent.putExtra("tabText", nameStrings[arg2]);
					intent.putExtra("url", "http://www.suizhougjj.cn/");
					startActivity(intent);
				}
			}
			else if (arg2 ==7 || arg2 ==8 || arg2 ==9 ||arg2 ==10 || arg2 ==12) 
			{
				count(arg2);
				webPush(arg2);
				
			}
			else if (arg2 ==17 || arg2 ==18 || arg2 ==21 || arg2 ==11) 
			{
				developPush(arg2);
			}
			else {
				count(arg2);
				Push(arg2);
			}
			 
			
		}
		
	}
	
	private void count(final int key)
	{
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String countURL= "http://119.36.193.148/suizhou/api/categoryc/"+urlCount[key];
					
					Test_Bean data = DataManeger.getTestData(countURL);
				} catch (Y_Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
	}
	
	/**
	 * 参数web，判定下级页面是否为直接跳转至网页yes为是,no为不是
	 * arg2参数为判定用户点击的是哪项
	 * @param arg2
	 */
	private void Push(int arg2) {
		Log.e("---", "push---"+arg2+"----"+nameStrings[arg2]);
		Intent intent;
		intent = new Intent(HomeActivity.this,ListViewActivity.class);
		intent.putExtra("tabText", nameStrings[arg2]);
		intent.putExtra("url",urlStrings[arg2]);
		intent.putExtra("web", "no");
		intent.putExtra("arg2", String.valueOf(arg2));
		startActivity(intent);
	}
	/**
	 * 跳转至网页显示页面前的列表显示，传递值“web”为判定下级列表是网页显示页面，在此列表获取下级网页显示页面需要显示的信息
	 * arg2参数为判定用户点击的是哪项
	 * @param arg2
	 */
	private void webPush(int arg2) {
		Log.e("---", "webPush---"+arg2+"----"+nameStrings[arg2]);
		Intent intent;
		intent = new Intent(HomeActivity.this,ListViewActivity.class);
		intent.putExtra("tabText", nameStrings[arg2]);
		intent.putExtra("url",urlStrings[arg2]);
		intent.putExtra("web", "yes");
		intent.putExtra("arg2", String.valueOf(arg2));
		startActivity(intent);
	}
	/**
	 * 跳转至提示页面，并且传递显示的内容
	 * arg2参数为判定用户点击的是哪项
	 * @param arg2
	 */
	private void developPush(int arg2) {
		Log.e("---", "developPush---"+arg2+"----"+nameStrings[arg2]);
		Intent intent;
		intent = new Intent(HomeActivity.this,Develop.class);
		intent.putExtra("tabText", nameStrings[arg2]);
		if (arg2 == 11) {
			intent.putExtra("text", "神韵随州简介");

		}
		else {
			intent.putExtra("text", " 正在开发中...");
		}
		

		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

	/**
	 * 以下为继承OnTouchListener,OnGestureListener的回调方法
	 * 控制翻页，向左/右滑动时对mark进行赋值，判断当前页数，然后清空当前的gridView信息，重新获取赋值
	 */
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
        return mGestureDetector.onTouchEvent(event); 
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		
		if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE   
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
            // Fling left   
			if (images.length - ((mark +1)*9)>0) {
        		Log.v("---", "我加1了");
				mark++;
				Log.v("---", ""+mark);
				arrayList.clear();
				Grid();
			}
        } else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE   
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
            // Fling right   
        	
        	
        	if (mark != 0) {
				mark--;
				arrayList.clear();
				Grid();
			}
        }  
		return false;
	}

	
	private void packge() {
		packgeName = this.getPackageName();
		vercode = HomeActivity.getVerCode(this);//版本号
		Log.v("----", "--"+vercode);
		packgeThread();
		
		
	}
	
	public static String getVerCode(Context context) {
		String verCode = "-1";
		try {
			verCode = context.getPackageManager().getPackageInfo(packgeName, 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("log", e.getMessage());
		}
		return verCode;
	}
	private void packgeThread()
	{
		//progressDialog = ProgressDialog.show(HomeActivity.this, "", "正在检查更新", true, false);
		//progressDialog.setCancelable(true);//设置是否可以使用返回键取消
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Test_Bean data = DataManeger.getTestData(urlString);
					ArrayList<Test_Model> datalist = data.getData();
					for (Test_Model test_Model : datalist) {

						
						packsString = String.valueOf(test_Model.getTitle());
						upadteURL = String.valueOf(test_Model.getDescription());
						break;	
					}
					handler.sendEmptyMessage(1);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
		//	progressDialog.dismiss();
			Log.v("---",String.valueOf(msg.what));
			String verString = String.valueOf(vercode);
			if (packsString.equals(verString)) {
			}
			else {
				UpdateManager updateManager = new UpdateManager(HomeActivity.this);
				updateManager.updateMsg = "当前最新版本为:"+packsString;
			    updateManager.apkUrl = upadteURL;
				updateManager.checkUpdateInfo();
				
			}
		}
		
	};
	
	
	
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
	
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
