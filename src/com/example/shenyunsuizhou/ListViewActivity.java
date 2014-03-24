package com.example.shenyunsuizhou;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;

import com.example.shenyunsuizhou.XListView.IXListViewListener;
import com.example.shenyunsuizhou.json.DataManeger;
import com.example.shenyunsuizhou.json.Test_Bean;
import com.example.shenyunsuizhou.json.Test_Model;
import com.example.shenyunsuizhou.json.Y_Exception;
import com.umeng.analytics.MobclickAgent;

import adapter.LazyAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * 
 */
/**
 * 
 * @author yangchao
 *继承自定义IXListViewListener，有下拉属性与加载更多功能
 */
public class ListViewActivity extends Activity implements IXListViewListener,OnClickListener,OnPageChangeListener{

	private XListView listView;//下拉刷新列表
	private Button backButton;//返回按钮
	private TextView tabTextView;//设置导航条标题
	private RelativeLayout relativeLayout;//转载图片列表
	private TextView topTextView;
	private Button siteButton;

	//以下KEY参数对照上面Json参数总结
	public static final String KEY_ID = "Id"; 
	public static final String KEY_TITLE = "Title";  
	public static final String KEY_DESCRIPTION = "Description";
	public static final String KEY_NOTE = "Note";
	public static final String KEY_THUMB_URL = "CnParams"; 
	public static final String KEY_TIME = "Modified_Time"; 
	public static final String KEY_INTROTEXT = "Introtext"; 
	public static final String KEY_ZCATEGORYURL = "ZCategoryUrl"; 
	public static final String KEY_AZHUADONG = "Azhuadong"; //判断下级页面是否为滑动列表 值为yes为滑动，，，，《去除使用》
	public static final String KEY_ZCATEGORSTRINGS = "ZcategoryStrings"; //获取当前列表等级，如为no则为最下级列表，点击跳转至浏览页面

	
	LazyAdapter adapter;
	private String barString = null;//接收导航条名
	private String urlString = null;//接收数据地址
	private String webString = null;//接收参数，判定下级页面是否为直接跳转至网页yes为是
	private String arg2String = null;//接收类别
	private Boolean intent = false;
	
	
	private ViewPager viewPager;
	private ViewPageAdapter vpAdapter;
	private ArrayList<View> views;	
	private int iString;
	private ArrayList<Float> iList = new ArrayList<Float>();//滑动列表存储点击范围与时间的数组
	private ImageView[]points;
	private int currentIndex;
	private static  ArrayList<String> pics = new ArrayList<String>();//存储网络图片地址
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>() ;//存储网络图片,在滑动列表显示，最大值为4
	ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	ArrayList<Hashtable<String, String>> blockdata = new ArrayList<Hashtable<String, String>>();//存储获取的网络信息
	private ProgressDialog progressDialog;
	private Boolean progress = false;
	private String timeString = "刚刚";//记录下拉刷新时间
	private Handler mHandler;
	private int Mark =1;//下拉刷新加载倍数设置
	private int MarkCount = 0;//记录当前数据量
	private int imageInt = 0;//根据此参数判断列表该从第几项开始显示
	
	private Timer timer = new Timer();
	private TimerTask task;
	
	
	private FinalDb db = null;//数据库对象
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		barString = (String)getIntent().getExtras().getString("tabText");
		urlString = (String)getIntent().getExtras().getString("url");
		webString = (String)getIntent().getExtras().getString("web");
		arg2String = (String)getIntent().getExtras().getString("arg2");

		listView = (XListView)findViewById(R.id.xListView);
		backButton = (Button)findViewById(R.id.ListView_Button_back);
		tabTextView = (TextView)findViewById(R.id.ListView_tabBarText);
		relativeLayout = (RelativeLayout)findViewById(R.id.List_top_relative);
		topTextView = (TextView)findViewById(R.id.List_top_text);
		
		siteButton  =(Button)findViewById(R.id.ListView_Button_site);
		siteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			Intent intent = new Intent(ListViewActivity.this,SiteActivity.class);
				startActivity(intent);

			}
		});
		
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);
		mHandler = new Handler();
		tabTextView.setText(barString);
		backButton.setOnClickListener(new backOnClick());
		
		db = FinalDb.create(this);//实例化数据对象
		
		Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()) {// 判断是否有网络连接
			progressDialog = ProgressDialog.show(ListViewActivity.this, "", getResources().getString(R.string.xlistview_header_hint_loading), true, false);
			progress = true;
			//	progressDialog.setCancelable(true);//设置是否可以使用返回键取消
			Log.v("测试定时器", "开始");
			task = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.v("测试定时器", "结束");
					timer.cancel();	
					if (progress) {
						progressDialog.dismiss();
						Toast.makeText(getApplicationContext(), "连接超时", Toast.LENGTH_SHORT).show();
					}
					
					
				}
			};
			timer.schedule(task,30000,30000);
			
			thread();
		}
		else {
			Toast.makeText(getApplicationContext(), "无网络数据，开启离线模式",
					Toast.LENGTH_SHORT).show();
			intent = true;
			date();
		}
		
	}
	
	class backOnClick implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onBackPressed();
		}
		
	}
	
	/**
	 * 离线模式调用
	 * 查询数据库，根据infoID<arg2String>判断列表等级，检查当前列表数据与数据库是否吻合，如果不是则删除原始数据，添加新数据，如果是则继续刷新列表
	 */
	private void date()
	{
		String condition ="infoID='" + arg2String+ "'";//搜索条件
		List<User> list = db.findAllByWhere(User.class, condition);
		if ( list != null ) {
			blockdata.clear();
				for (int i = 0; i < list.size(); i++) {
					Hashtable<String, String> hashtable = new Hashtable<String, String>();
					hashtable.put(KEY_ID, (list.get(i).getInfoID()==null? "": list.get(i).getInfoID()));
					hashtable.put(KEY_TITLE, (list.get(i).getTitle()==null? "": list.get(i).getTitle()));
					hashtable.put(KEY_DESCRIPTION, (list.get(i).getDescription()==null? "": list.get(i).getDescription()));
					hashtable.put(KEY_THUMB_URL, (list.get(i).getCnparams()==null? "": list.get(i).getCnparams()));
					hashtable.put(KEY_ZCATEGORYURL, (list.get(i).getZcategoryurl()==null? "": list.get(i).getZcategoryurl()));
					hashtable.put(KEY_ZCATEGORSTRINGS, (list.get(i).getZcategory()==null? "": list.get(i).getZcategory()));
					hashtable.put(KEY_TIME, (list.get(i).getModifiedTime()==null? "": list.get(i).getModifiedTime()));
					hashtable.put(KEY_INTROTEXT, (list.get(i).getIntrotext()==null? "": list.get(i).getIntrotext()));
					hashtable.put(KEY_NOTE, (list.get(i).getNote()==null? "": list.get(i).getNote()));
					blockdata.add(hashtable);
				}
				startHandler.sendEmptyMessage(0);
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(ListViewActivity.this);
			builder.setTitle("当前无缓存数据");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					onBackPressed();
				}
			});
			builder.create().show();
		}
	}
	/**
	 * 有网络模式检查数据
	 * 数据与数据库数据数目不一样，删除数据，添加新数据，
	 */
	private void intentDate()
	{
		String condition ="infoID='" + arg2String+ "'";//搜索条件
		List<User> list = db.findAllByWhere(User.class, condition);
		if ( list != null ) {
			if (list.size() == blockdata.size()) {
				startHandler.sendEmptyMessage(0);
			}
			else {
				deleteDate();
			}
			
		}
	}
	
	
	/**
	 * 新数据与数据库数据数目不一样，删除数据，添加新数据，
	 */
	private void deleteDate()
	{
		startHandler.sendEmptyMessage(0);
		deleteDateThread();
	}
	
	
	private void deleteDateThread()
	{
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String condition ="infoID='" + arg2String+ "'";//搜索条件
				db.deleteByWhere(User.class, condition);
				for (int i = 0; i < blockdata.size(); i++) {
					User user = new User();
					
					user.setInfoID(arg2String);
					user.setTitle(blockdata.get(i).get(KEY_TITLE));
					user.setNote(blockdata.get(i).get(KEY_NOTE));
					user.setIntrotext(blockdata.get(i).get(KEY_INTROTEXT));
					user.setDescription(blockdata.get(i).get(KEY_DESCRIPTION));
					user.setCnparams(blockdata.get(i).get(KEY_THUMB_URL));
					user.setModifiedTime(blockdata.get(i).get(KEY_TIME));
					user.setZcategoryurl(blockdata.get(i).get(KEY_ZCATEGORYURL));
					user.setZcategory(blockdata.get(i).get(KEY_ZCATEGORSTRINGS));
					db.save(user);
				}
			}
			
		}.start();
	}
	/**
	 * 获取数据的的线程,获取数据后存储到blockdata，通知startHandler进行下一步处理
	 */
	private void thread()
	{

		
		new Thread()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
					try {
						Test_Bean data = DataManeger.getTestData(urlString);
						ArrayList<Test_Model> datalist = data.getData();
						for (Test_Model test_Model : datalist) {					
							Hashtable<String, String> hashtable = new Hashtable<String, String>();
							hashtable.put(KEY_ID, test_Model.getId()==null? "": test_Model.getId());
							hashtable.put(KEY_TITLE, test_Model.getTitle()==null? "": test_Model.getTitle());
							hashtable.put(KEY_DESCRIPTION, test_Model.getDescription()==null? "": test_Model.getTitle());
							hashtable.put(KEY_THUMB_URL, test_Model.getCnparams()==null? "": test_Model.getCnparams());
							hashtable.put(KEY_ZCATEGORYURL, test_Model.getZcategoryurl()==null? "": test_Model.getZcategoryurl());
							hashtable.put(KEY_ZCATEGORSTRINGS, test_Model.getZcategory()==null? "": test_Model.getZcategory());
							hashtable.put(KEY_TIME, test_Model.getModifiedTime()==null? "": test_Model.getModifiedTime());
							hashtable.put(KEY_INTROTEXT, test_Model.getIntrotext()==null? "": test_Model.getIntrotext());
							hashtable.put(KEY_NOTE, test_Model.getNote()==null? "": test_Model.getNote());
							blockdata.add(hashtable);
						}
						
						startHandler.sendEmptyMessage(1);
						
					} catch (Y_Exception e) {
						e.printStackTrace();
					}
				
				
			}
			
		}.start();
		
	}
	
	/**
	 * 获得数据后进行判断
	 * KEY_NOTE进行第一次判断，判断此列表是否显示图片滑动，值为yes时为显示，获取前4项的值然后调用imgThread方法
	 * 值为no时调用markItems方法
	 * 根据msg值判断是否进行列表赋值，为0则进行，为1则进入查询数据库方法
	 * 
	 */
	private Handler startHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			if (msg.what == 0) {
				
				for (int i = 0; i < blockdata.size(); i++) {
					Log.e("block--1----", blockdata.get(i).get(KEY_ID));
					Log.e("block--1----", blockdata.get(i).get(KEY_TITLE));
					Log.e("block--2----", blockdata.get(i).get(KEY_DESCRIPTION));
					Log.e("block--3----", blockdata.get(i).get(KEY_THUMB_URL));
					Log.e("block--4----", blockdata.get(i).get(KEY_ZCATEGORYURL));
					Log.e("block--5----", blockdata.get(i).get(KEY_ZCATEGORSTRINGS));
					Log.e("block--6----", blockdata.get(i).get(KEY_TIME));
				}
				if (blockdata.size()!=0) {
					if (blockdata.get(0).get(KEY_NOTE).equals("yes") && blockdata.size()>4) {
						
						
						topTextView.setText(blockdata.get(0).get(KEY_TITLE));
						imageInt = 4;
						for (int j = 0; j < 4; j++) {
							pics.add(blockdata.get(j).get(KEY_THUMB_URL));
						}
						imgThread();
						
					}
					else {
						markItems(imageInt);
					}
					
				}
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(ListViewActivity.this);
					builder.setTitle("当前无数据");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							onBackPressed();
						}
					});
					builder.create().show();
				}
			}
			else {
				progress = false;
				progressDialog.dismiss();
				intentDate();
			}

		}
		
	};
	
	/**
	 * 网络获取需要显示的图片，获取完毕后存储到bitmaps中，然后通知imgHandler
	 */
	private void imgThread()
	{
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < 4; i++) {
					URL imageUrl =null;
					Bitmap bitmap =null;
					try {
						imageUrl = new URL(pics.get(i));
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
		
						HttpURLConnection connection =(HttpURLConnection)imageUrl.openConnection();
						connection.setDoInput(true);
						connection.connect();
						InputStream is=connection.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);
						bitmap = BitmapFactory.decodeStream(bis);
						bis.close();
						is.close();
						bitmaps.add(bitmap);
					} catch (Exception e) {
						// TODO: handle exception
						bitmaps.add(null);
					}
				}
				
				imgHandler.sendEmptyMessage(1);
			}
			
		}.start();
	}
	/**
	 * 首先设置滑动列表的状态，布局文件默认状态为none，此时设置为显示
	 * 然后设置完滑动列表后开始设置下列普通listview列表，调用markItems方法，此时imageInt值为4
	 */
	private Handler imgHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			relativeLayout.setVisibility(View.VISIBLE);
			
			initView();
			initDate();
			markItems(imageInt);
		}
		
	};
	
	/**
	 * 显示listview列表，根据变量webString判断是否进入详情显示页面，如果不是yes则获取数据地址再次进入此activity显示
	 */
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

		
		adapter = new LazyAdapter(ListViewActivity.this, mData);
		listView.setAdapter(adapter);
			
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent;
				Log.e("listView打印网页地址", webString);

				if (webString.equals("yes")) {
					
					Articlecount(blockdata.get(arg2-1).get(KEY_ID));
					
					Log.e("listView打印网页地址", blockdata.get(arg2-1 +imageInt).get(KEY_INTROTEXT));
					intent = new Intent(ListViewActivity.this,WebActivity.class);
					intent.putExtra("tabText", blockdata.get(arg2-1 +imageInt).get(KEY_TITLE));
					String urlString = blockdata.get(arg2-1 +imageInt).get(KEY_INTROTEXT);
					byte b[] = android.util.Base64.decode(urlString, Base64.DEFAULT);
					urlString = new String(b);
					intent.putExtra("url", urlString);//传递网页地址
					startActivity(intent);
				}
				else {

				if (blockdata.get(0).get(KEY_ZCATEGORSTRINGS).equals("no")) {
					WebViewActivity activityw = new WebViewActivity();
					activityw.data =blockdata;
					activityw.mark = arg2-1 +imageInt;
					intent = new Intent(ListViewActivity.this,WebViewActivity.class);
					startActivity(intent);
				}
				else {

					count(blockdata.get(arg2-1).get(KEY_ID));
					intent = new Intent(ListViewActivity.this,ListViewActivity.class);
					intent.putExtra("url",blockdata.get(arg2-1 +imageInt).get(KEY_ZCATEGORYURL));
					intent.putExtra("tabText",blockdata.get(arg2-1 +imageInt).get(KEY_TITLE));
					intent.putExtra("web", "no");
					intent.putExtra("arg2", arg2String +"-" +blockdata.get(arg2-1).get(KEY_ID));
					startActivity(intent);
				}
				
			}
			}
			
		});
		}
		
	};

	private void Articlecount(final String key)
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
	
	
	private void count(final String key)
	{
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String countURL= "http://119.36.193.148/suizhou/api/categoryc/"+key;
					
					Test_Bean data = DataManeger.getTestData(countURL);
				} catch (Y_Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
	}
	
	private void onLoad() {
		listView.stopRefresh();
		listView.stopLoadMore();
		
		listView.setRefreshTime(timeString);
	}

	private void timeS() {
		Calendar calendar = Calendar.getInstance();
		String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(calendar.get(Calendar.MINUTE));
		
		timeString = hour + ":" + minute;
	}

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Normal normal = new Normal(ListViewActivity.this);// 判断是否有网络连接
				if (normal.note_Intent()) {// 判断是否有网络连接
					mData.clear();
					blockdata.clear();
					Mark = 1;
					MarkCount = 0;
					timeS();
					thread();
					onLoad();
				}
				else {
					if (intent) {
						mData.clear();
						blockdata.clear();
						Mark = 1;
						MarkCount = 0;
						timeS();
						date();
						onLoad();
					}
					else {
						onLoad();
						Toast.makeText(getApplicationContext(), "请连接网络",
								Toast.LENGTH_SHORT).show();
					}	
				}
					
	
			}
		}, 2000);
	}

	/**
	 * 加载更多
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Normal normal = new Normal(ListViewActivity.this);// 判断是否有网络连接
				if (normal.note_Intent()) {// 判断是否有网络连接
					geneItems();
					adapter.notifyDataSetChanged();
					onLoad();
				}
				else {
					if (intent) {
						geneItems();
						adapter.notifyDataSetChanged();
						onLoad();
					}
					else {
						Toast.makeText(getApplicationContext(), "请连接网络",
								Toast.LENGTH_SHORT).show();
					}
					
				}
			}
		}, 2000);
	}	
/**
 * 加载更多数据
 * 根据传递的imageInt值进行判断列表从第几项开始显示，如果此前显示了滑动列表，则值为4，如果此前没有显示，则值为0
 * blockdata为所有数据的存储数组，   
 * 获取值完毕后通知handler进行列表显示
 */
	
	private void markItems(int img) {
			if (blockdata.size()>=10) {
				for (int i = img; i < 10+img; i++) {
					HashMap<String, String> itemMap = new HashMap<String, String>();
					itemMap.put(KEY_TITLE, blockdata.get(i).get(KEY_TITLE));
					if (webString.equals("yes") || blockdata.get(0).get(KEY_ZCATEGORSTRINGS).equals("no")) {
						itemMap.put(KEY_TIME,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					else {
						itemMap.put(KEY_DESCRIPTION,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					itemMap.put(KEY_THUMB_URL, blockdata.get(i).get(KEY_THUMB_URL));
					mData.add(itemMap);
				}
				MarkCount =10 +img;
				handler.sendEmptyMessage(0);
			}
			else {
				for (int i = img; i < blockdata.size(); i++) {
					HashMap<String, String> itemMap = new HashMap<String, String>();
					itemMap.put(KEY_TITLE, blockdata.get(i).get(KEY_TITLE));
					if (webString.equals("yes") || blockdata.get(0).get(KEY_ZCATEGORSTRINGS).equals("no")) {
						itemMap.put(KEY_TIME,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					else {
						itemMap.put(KEY_DESCRIPTION,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					itemMap.put(KEY_THUMB_URL, blockdata.get(i).get(KEY_THUMB_URL));
					mData.add(itemMap);
				}
				MarkCount = blockdata.size();
				handler.sendEmptyMessage(0);
			}
	}
	
	
	private void geneItems() {
			
			Mark++;
			if (MarkCount == blockdata.size()) {
				
			}
			else {
				if (blockdata.size() >= (10 *Mark)) {
					for (int i = MarkCount; i < (10 *Mark); i++) {
					HashMap<String, String> itemMap = new HashMap<String, String>();
					itemMap.put(KEY_TITLE, blockdata.get(i).get(KEY_TITLE));
					if (webString.equals("yes") || blockdata.get(0).get(KEY_ZCATEGORSTRINGS).equals("no")) {
						itemMap.put(KEY_TIME,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					else {
						itemMap.put(KEY_DESCRIPTION,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					itemMap.put(KEY_THUMB_URL, blockdata.get(i).get(KEY_THUMB_URL));
					mData.add(itemMap);					
					}
					MarkCount+=10;
				}
				else {
					for (int i = MarkCount; i < blockdata.size(); i++) {
					HashMap<String, String> itemMap = new HashMap<String, String>();
					itemMap.put(KEY_TITLE, blockdata.get(i).get(KEY_TITLE));
					if (webString.equals("yes") || blockdata.get(0).get(KEY_ZCATEGORSTRINGS).equals("no")) {
						itemMap.put(KEY_TIME,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					else {
						itemMap.put(KEY_DESCRIPTION,blockdata.get(i).get(KEY_DESCRIPTION));
					}
					
					itemMap.put(KEY_THUMB_URL, blockdata.get(i).get(KEY_THUMB_URL));
					mData.add(itemMap);
					}
					MarkCount = blockdata.size();
					
					listView.setAdapter(adapter);
					listView.setXListViewListener(ListViewActivity.this);
			}
		}	
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

	
	
	private void initView() {

		views = new ArrayList<View>();
		viewPager = (ViewPager)findViewById(R.id.viewpager);
		vpAdapter = new ViewPageAdapter(views);
		
	}


	
	private void initDate() {
		for (int i = 0; i < 4; i++) {
			//pics.add(blockdata.get(i).get(KEY_THUMB_URL));
		}
		
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				  LinearLayout.LayoutParams.FILL_PARENT);
		
		for (int i = 0; i < 4; i++) {
			ImageView iv=new ImageView(this);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setLayoutParams(mParams);
			iv.setImageBitmap(bitmaps.get(i));
			views.add(iv);
		}
		
		viewPager.setAdapter(vpAdapter);
		viewPager.setOnPageChangeListener(this);
		initPoint();
	}
	
	
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout)findViewById(R.id.ll);
		points =new ImageView[4];
		for (int i = 0; i < 4; i++) {
			points[i] = (ImageView)linearLayout.getChildAt(i);
			points[i].setEnabled(true);
			points[i].setOnClickListener(this);
			points[i].setTag(i);
		}

		currentIndex =0;
		points[currentIndex].setEnabled(false);
	}
	

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		Log.e("---1----", arg0 +"");
		
		if (arg0 ==0) {
			jisu();
		}
	}
	/**
	 * 根据点击的时间与范围进行判断，
	 * 数组iList小于10则代表用户轻点，为跳转页面，大于10则为滑动，根据iString判断用户滑动到哪页
	 */
	private void jisu() {
		if (iList.size()<10) {
			Log.v("----", "我触发了点击");
			iList.clear();
			Log.v("----", "我是第"+(iString+1) +"页");
			
			Intent intent ;
			if (webString.equals("yes")) {
				Log.e("listView打印网页地址", blockdata.get(iString).get(KEY_INTROTEXT));
				intent = new Intent(ListViewActivity.this,WebActivity.class);
				intent.putExtra("tabText", blockdata.get(iString).get(KEY_TITLE));
				intent.putExtra("url", blockdata.get(iString).get(KEY_INTROTEXT));//传递网页地址
				startActivity(intent);
			}
			else {

			if (blockdata.get(0).get(KEY_ZCATEGORSTRINGS).equals("no")) {
				WebViewActivity activityw = new WebViewActivity();
				activityw.data =blockdata;
				activityw.mark = iString;
				intent = new Intent(ListViewActivity.this,WebViewActivity.class);
				startActivity(intent);
			}
			else {
				intent = new Intent(ListViewActivity.this,ListViewActivity.class);
				intent.putExtra("url",blockdata.get(iString).get(KEY_ZCATEGORYURL));
				intent.putExtra("tabText",blockdata.get(iString).get(KEY_TITLE));
				intent.putExtra("web", "no");
				intent.putExtra("arg2", arg2String +"-" +blockdata.get(iString).get(KEY_TITLE));
				startActivity(intent);
			}
			}}
		else {
			iList.clear();
		}
	}
	
	
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		Log.e("---2----", "arg0---"+arg0 +"-arg1---" + arg1 + "-agr2---" + arg2);
		iString = arg0;
		iList.add(arg1);
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		Log.e("---3----", arg0 +"");
		setCurDot(arg0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer)v.getTag();
		setCurView(position);
		setCurDot(position);
	}
	
	
	private void setCurView(int position){  
        if (position < 0 || position >= 4) {  
            return;  
        }  
        viewPager.setCurrentItem(position);  
    }  
 
    /** 
    * 设置当前的小点的位置 
    */  
   private void setCurDot(int positon){  
        if (positon < 0 || positon > 4 - 1 || currentIndex == positon) {  
            return;  
        }  
        topTextView.setText(blockdata.get(positon).get(KEY_TITLE));
        points[positon].setEnabled(false);  
        points[currentIndex].setEnabled(true);  
 
        currentIndex = positon;  
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
