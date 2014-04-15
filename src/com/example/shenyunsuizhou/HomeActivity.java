package com.example.shenyunsuizhou;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.example.shenyunsuizhou.json.DataManeger;
import com.example.shenyunsuizhou.json.Test_Bean;
import com.example.shenyunsuizhou.json.Test_Model;
import com.example.shenyunsuizhou.json.Y_Exception;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

public class HomeActivity extends Activity{
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
	 * stype            1.list(列表) 2.weblink（直接跳转到网页）3.views（正在开发中）4.aboutus（关于）
      
	 * 直接跳转至网页页面为：联通营业厅，公积金帐号	索引号为 14，15号
	 * 跳一级列表然后直接跳转至网页页面为：主持人微博，企业风采（3个），索引号为7，8，9，12
	 * 跳转至开发页面为：社保账户，医保帐号，评选活动，关于本应用页面（显示本应用介绍） 索引号为17，18，21，11
	 * 
	 * 此处设计不合理，过于复杂与容易出错，建议后期更改为直接获取服务器数据，添加到gridView列表中
	 */
	
	GestureDetector mGestureDetector;  
	private static final int FLING_MIN_DISTANCE = 200;  
	private static final int FLING_MIN_VELOCITY = 0;
	//ArrayList<HashMap<String, Object>> arrayList;

	private String vercode;
	private static String packgeName;
	private ProgressDialog progressDialog;

	//private String urlString ="http://119.36.193.148/suizhou/api/categories/155?op=all";//更新地址
	

	
	//private GridView gridView;	//当前页面才用gridView布局
	private Button siteButton;	//跳转至设置页面
	private RadioButton oneRadioButton;//下方记录当前页面翻页图片，此为第一张图，以下2个为另外2个翻页图片
	private RadioButton twoRadioButton;
	private RadioButton threeRadioButton;
	private String homeString = "no";
	
	private int mark = 0;	//记录当前页数，默认值为1，则进入程序后显示第一页，一共3页
	
	private RadioGroup radioGroup;

	private LinearLayout scollLinearLayout = null;
	private LinearLayout mContainer = null;
	
	ArrayList<HashMap<String, Object>> arrayList;	
	private MyGridView[] gridView;
	private int pageCount = 0; //总共有几个页面	
	private String[] id;
	private String[] title;
	private String[] zcategoryurl;
	private String[] stype;
	private String[] metakey;
	//private Bitmap[] mBitmap;
	public static ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>() ;
	
	public static HashMap<String, Object> listInfo = new HashMap<String, Object>();
	
	private  String Internet = null;
	
	private MyScrollView scollview;
	private int downX = 0;
	//private String[] filename; //图片名
	//private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_suizhou/"; 
	//
	RadioButton tempButton[];
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		 //mGestureDetector = new GestureDetector(this); 
		siteButton  =(Button)findViewById(R.id.Home_Button_site);
	    //gridView = (GridView) findViewById(R.id.Home_GridView);
	   // homeString = getIntent().getExtras().getString("home");	    	    
	  /*  oneRadioButton = (RadioButton)findViewById(R.id.Home_top_image1);
	    twoRadioButton = (RadioButton)findViewById(R.id.Home_top_image2);
	    threeRadioButton = (RadioButton)findViewById(R.id.Home_top_image3);
	    */
		Internet = getIntent().getExtras().getString("Internet");
		
	    radioGroup = (RadioGroup)findViewById(R.id.group_button);
	    
	    scollLinearLayout =  (LinearLayout) findViewById(R.id.linealayout_scrollview);
      //  mContainer = (LinearLayout)findViewById(R.id.container);
	  	LayoutParams params = new LayoutParams(getWinWidth(), getWinHeight());
	    
	  	mContainer = new LinearLayout(this);
	  	mContainer.setLayoutParams(params);
	  	mContainer.setOrientation(0);
	  	
	  	scollview = new MyScrollView(this);
	    scollview.setLayoutParams(params);
	    scollview.setFillViewport(true);
	    scollview.addView(mContainer);
	    scollLinearLayout.addView(scollview);
	    
	
		id = (String[]) listInfo.get("id");
		title = (String[]) listInfo.get("title");
		zcategoryurl = (String[]) listInfo.get("zcategoryurl");
		stype = (String[]) listInfo.get("stype");
		metakey =  (String[]) listInfo.get("metakey");
	    
	    //id =  getIntent().getExtras().getStringArray("id");
		//title =  getIntent().getExtras().getStringArray("title");
		//zcategoryurl =  getIntent().getExtras().getStringArray("zcategoryurl");
		//stype = getIntent().getExtras().getStringArray("stype");
	
		
		int Number = id.length;		
		int quotient = Number/9; //商
        int remainder = Number%9; //余数
        
        if(remainder>0){
        	pageCount = quotient+1;
        }else {
        	pageCount = quotient;
		}
		
        gridView = new MyGridView[pageCount];
        
        tempButton = new RadioButton[pageCount];
        for (int i = 0; i < pageCount; i++) {
        	tempButton[i] = new RadioButton(this);
		    tempButton[i].setWidth(18);
		    tempButton[i].setHeight(18);   	
		    tempButton[i].setButtonDrawable(android.R.color.transparent);
		    tempButton[i].setBackgroundResource(R.drawable.radio);	 
		    radioGroup.addView(tempButton[i], LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  		
	}

        tempButton[0].setChecked(true);
     
      
        
        
        for (int i = 0; i < pageCount; i++) {
        	//i表示当前为第几页
        
        	gridView[i] = new MyGridView(this);
        	gridView[i].setNumColumns(3);  
        	gridView[i].setGravity(Gravity.CENTER);
        	gridView[i].setLayoutParams(params);
        	gridView[i].setTag(i);
        	Grid(i);
            mContainer.addView(gridView[i]);
            //gridView[i].setLongClickable(true);
            gridView[i].setSelector(new ColorDrawable(Color.TRANSPARENT));
            gridView[i].setClickable(true);
            gridView[i].setOnItemClickListener(new GridOnClick(i));
            gridView[i].setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					
					
					if( event.getAction()==MotionEvent.ACTION_DOWN)
				     downX = (int)event.getX();
			
					return false;
				}
			});
		}
		
      
    
      
	  //  	Log.v("-----247------", visibility+"ttt");
        //gridView.setOnTouchListener(this);
	   // gridView.setLongClickable(true);
	 /*   gridView.setOnItemClickListener(new GridOnClick());
	    Grid();
	    Normal normal = new Normal(this);// 判断是否有网络连接
		if (normal.note_Intent()  && homeString.equals("yes")) {// 判断是否有网络连接
			packge();
		}
	    */
	  //  oneRadioButton.setChecked(true);
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
	
	
	private void Grid( int pageNum) {
		
		int count =9;
		//判断当前为第几页，根据页数计算当前页该显示多少模块图标，显示哪些模块图标
		if ((id.length - ((pageNum +1)*9))<0) {
			count = id.length;
		}
		else {
			count = (pageNum +1) *9;
		}
		
	
				
		arrayList = new ArrayList<HashMap<String, Object>>();
		for (int i = pageNum*9; i < count; i++) {
			if (Internet.equals("YES")) {
				HashMap<String, Object> map = new HashMap<String, Object>();				
				map.put("ItemImage",bitmaps.get(i));
				map.put("ItemText", title[i]);
				arrayList.add(map);
			}
			else {
				Bitmap bmBitmap = null;
				HashMap<String, Object> map = new HashMap<String, Object>();				
					
			    File file = new File(Launch.ALBUM_PATH+id[i]+".png");
				if(file.exists())   { 
				  map.put("ItemImage",getBitMap(Launch.ALBUM_PATH+id[i]+".png"));
				  }
				map.put("ItemText", title[i]);
				arrayList.add(map);
				System.gc();
			}
			
			//}
			
		}
	
        SimpleAdapter adapter = new SimpleAdapter(HomeActivity.this, arrayList,R.layout.home_gridview, 
		new String[] { "ItemImage","ItemText" }, new int[] {R.id.Home_Grid_Image, R.id.Home_Grid_Text_Name });
		
        adapter.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// TODO Auto-generated method stub
				  if( (view instanceof ImageView) & (data instanceof Bitmap) ) {  
		                ImageView iv = (ImageView) view;  
		                Bitmap bm = (Bitmap) data;  
		                iv.setImageBitmap(bm);  
		                return true;  
		                }  
				return false;
			}
		});
      
        gridView[pageNum].setAdapter(adapter);	
        
	}

	
	/**
	 * 根据原始程序分析每个模块的显示信息
	 * 直接跳转至网页页面为：联通营业厅，公积金帐号	
	 * 跳一级列表然后直接跳转至网页页面为：主持人微博，企业风采（3个），
	 * 跳转至开发页面为：社保账户，医保帐号，评选活动，关于本应用页面（显示本应用介绍） 
	 */
	class GridOnClick implements OnItemClickListener{
        
		int page = 0;
		public GridOnClick(int i) {
			// TODO Auto-generated constructor stub
			this.page = i;
		}

		
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
		
			 arg2 = arg2+page*9;

			 Log.v("点击项", ""+arg2);
			 Log.v("-----424-----", stype[arg2]);
			 Log.v("-----425-----", id[arg2]);
			 
			 if (stype[arg2].equals("unfinished") || stype[arg2].equals("aboutus")) {
				 developPush(arg2,stype[arg2]);
			 }
			 else if (stype[arg2].equals("weblink")) {				
				 webInter(arg2);	
			 }
			 else if (stype[arg2].equals("list")) {
				 Push(arg2);
			 }
			 else if (stype[arg2].equals("weblist")) {
				 webPush(arg2);
			}
		}
		
	}
	
	
	private void webInter(int arg2){
		Intent intent;
		intent = new Intent(HomeActivity.this,WebActivity.class);
		intent.putExtra("tabText", title[arg2]);
		intent.putExtra("url", metakey[arg2]);
		Log.v("-----485-----", metakey[arg2]);
		Log.v("-----485-----", zcategoryurl[arg2]);
		
		startActivity(intent);
	}
	
	
	
	/**
	 * 参数web，判定下级页面是否为直接跳转至网页yes为是,no为不是
	 * arg2参数为判定用户点击的是哪项
	 * @param arg2
	 */
	private void Push(int arg2) {
		//Log.e("---", "push---"+arg2+"----"+nameStrings[arg2]);
		Intent intent;
		intent = new Intent(HomeActivity.this,ListViewActivity.class);
		intent.putExtra("tabText", title[arg2]);
		intent.putExtra("url",zcategoryurl[arg2]);
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
		
		Intent intent;
		intent = new Intent(HomeActivity.this,ListViewActivity.class);
		intent.putExtra("tabText", title[arg2]);
		intent.putExtra("url",zcategoryurl[arg2]);
		intent.putExtra("web", "yes");
		intent.putExtra("arg2", String.valueOf(arg2));
		startActivity(intent);
	}
	/**
	 * 跳转至提示页面，并且传递显示的内容
	 * arg2参数为判定用户点击的是哪项
	 * @param arg2
	 */
	private void developPush(int arg2, String key) {
		Intent intent;
		intent = new Intent(HomeActivity.this,Develop.class);
		intent.putExtra("tabText", title[arg2]);
		if (key.equals("unfinished")) {
		
			intent.putExtra("text", " 正在开发中...");
		}
		else {
			intent.putExtra("text", "神韵随州简介");
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
	
/*	@Override
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
	}*/

	/**
	    * 加载本地图片
	    * @param url
	    * @return
	    */
	    public static Bitmap getBitMap(String url) {
	         try {
	        	 
	        	// int inSampleSize = getinSampleSize(String url);
	        	 
	        	 BitmapFactory.Options options = new BitmapFactory.Options();
	        	// options.inJustDecodeBounds = false;
	        	// options.inSampleSize = 2;
	        	 Bitmap bitmap = BitmapFactory.decodeFile(url, options);   
	        	 return bitmap;

	           } catch (Exception e) {
				// TODO: handle exception
	        	   Log.v("----328----", e.getMessage());
	        	   return null;
			}
	    }

	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
		//	progressDialog.dismiss();
			
		}
		
	};
	
	private int getWinWidth(){
		DisplayMetrics dm = new DisplayMetrics();
		//获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	private int getWinHeight(){
		DisplayMetrics dm = new DisplayMetrics();
		//获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	//定义scollview
	public class MyScrollView extends HorizontalScrollView {
		private int subChildCount = 0;
		private ViewGroup firstChild = null;
		
		private int currentPage = 0;
		private ArrayList<Integer> pointList = new ArrayList<Integer>();
		
		public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			init();
		}


		public MyScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}

		public MyScrollView(Context context) {
			super(context);
			init();
		}
		private void init() {
			setHorizontalScrollBarEnabled(false);
		}
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			receiveChildInfo();
		}
		public void receiveChildInfo() {
			
			firstChild = (ViewGroup) getChildAt(0);
			if(firstChild != null){
				subChildCount = firstChild.getChildCount();
				
				for(int i = 0;i < subChildCount;i++){
					if(((View)firstChild.getChildAt(i)).getWidth() > 0){
						pointList.add(((View)firstChild.getChildAt(i)).getLeft());
					}
				}
			}

		}
		
		
		/*
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			// TODO Auto-generated method stub
			
	
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) ev.getX();		
				break;
			case MotionEvent.ACTION_MOVE:{
				
					
			}break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:{
				
				 Log.v("-----702----", downX+"");
			      Log.v("-----703----", ev.getX()+"");
				
					 if( Math.abs((ev.getX() - downX)) > getWidth() / 4){
							if(ev.getX() - downX > 0){
								smoothScrollToPrePage();
							}else{
								smoothScrollToNextPage();
							}
						}else{			
							smoothScrollToCurrent();
						}
			}
			}
			
			return super.dispatchTouchEvent(ev);
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			// TODO Auto-generated method stub
			 if (ev.getAction() == MotionEvent.ACTION_DOWN)
		        {
		            return false;
		        }
		        if (ev.getAction() == MotionEvent.ACTION_MOVE)
		        {
		            return true;
		        }
		        return super.onInterceptTouchEvent(ev);
		}*/
		

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) ev.getX();		
				break;
			case MotionEvent.ACTION_MOVE:{
				
			}break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:{
		      Log.v("-----702----", downX+"");
		      Log.v("-----703----", ev.getX()+"");
		    
				if( Math.abs((ev.getX() - downX)) > getWidth() / 4){
					if(ev.getX() - downX > 0){
						smoothScrollToPrePage();
					}else{
						smoothScrollToNextPage();
					}
				}else{			
					smoothScrollToCurrent();
				}
				return true;
				//return false;
			}
			}
			return super.onTouchEvent(ev);
		}
	
		
	


		private void pageChoose(int i) {
			// TODO Auto-generated method stub
			 tempButton[i].setChecked(true);
		}


		private void smoothScrollToCurrent() {
			smoothScrollTo(pointList.get(currentPage), 0);
			pageChoose(currentPage);		
		}

		private void smoothScrollToNextPage() {
			if(currentPage < subChildCount - 1){
				currentPage++;
				smoothScrollTo(pointList.get(currentPage), 0);		
				
				pageChoose(currentPage);
			}
		}

		private void smoothScrollToPrePage() {
			if(currentPage > 0){			
				currentPage--;
				smoothScrollTo(pointList.get(currentPage), 0);
				pageChoose(currentPage);
			}
		}
		/**
		 * ��һҳ
		 */
		public void nextPage(){
			smoothScrollToNextPage();
		}
		/**
		 * ��һҳ
		 */
		public void prePage(){
			smoothScrollToPrePage();
		}
		/**
		 * ��ת��ָ����ҳ��
		 * @param page
		 * @return
		 */
		public boolean gotoPage(int page){
			if(page > 0 && page < subChildCount - 1){
				smoothScrollTo(pointList.get(page), 0);
				currentPage = page;
				return true;
			}
			return false;
		}
	}


	
	/*@Override
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
	}*/

}
