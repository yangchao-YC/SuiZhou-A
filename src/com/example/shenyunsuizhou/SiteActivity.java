package com.example.shenyunsuizhou;

import java.util.ArrayList;
import java.util.Hashtable;

import com.example.shenyunsuizhou.json.DataManeger;
import com.example.shenyunsuizhou.json.Test_Bean;
import com.example.shenyunsuizhou.json.Test_Model;
import com.example.shenyunsuizhou.json.Y_Exception;
import com.umeng.analytics.MobclickAgent;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SiteActivity extends Activity implements android.view.View.OnClickListener{

	private Button backButton;
	private TextView tabTextView;

	private TextView boutsLayout;
	private TextView updateLayout;
	private String vercode;
	private static String packgeName;
	
	private String packsString =null;
	private String upadteURL =null;
	private ProgressDialog progressDialog;
	private String urlString ="http://119.36.193.148/suizhou/api/categories/155?op=all";//更新地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site);

		backButton = (Button)findViewById(R.id.Site_Button_back);
		boutsLayout = (TextView)findViewById(R.id.Site_bouts);
		updateLayout = (TextView)findViewById(R.id.Site_update);
		tabTextView = (TextView)findViewById(R.id.Site_tabBarText);
		tabTextView.setText("设置");
		backButton.setOnClickListener(this);
		boutsLayout.setOnClickListener(this);
		updateLayout.setOnClickListener(this);
		
	}

	
	private void packge() {
		packgeName = this.getPackageName();
		vercode = SiteActivity.getVerCode(this);//版本号
		packgeThread();
		
		
	}
	
	private void packgeThread()
	{
		progressDialog = ProgressDialog.show(SiteActivity.this, "", "正在检查更新", true, false);
		progressDialog.setCancelable(true);//设置是否可以使用返回键取消
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
				} catch (Y_Exception e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			String verString = String.valueOf(vercode);
			if (packsString.equals(verString)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SiteActivity.this);
				builder.setTitle("当前版本是最新");
				builder.setPositiveButton("确定", null);
				builder.create().show();
			}
			else {
				UpdateManager updateManager = new UpdateManager(SiteActivity.this);
				updateManager.updateMsg = "当前最新版本为:"+packsString;
			    updateManager.apkUrl = upadteURL;
				updateManager.checkUpdateInfo();
			}
		}
		
	};
	
	public static String getVerCode(Context context) {
		String verCode = "-1";
		try {
			verCode = context.getPackageManager().getPackageInfo(packgeName, 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("log", e.getMessage());
		}
		return verCode;
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
		case R.id.Site_Button_back:
			onBackPressed();
			break;
		case R.id.Site_bouts:
			Intent intent;
			intent = new Intent(SiteActivity.this,Develop.class);
			intent.putExtra("tabText","设置");
			intent.putExtra("text", "神韵随州简介");
			startActivity(intent);
			break;
		case R.id.Site_update:
			Normal normal = new Normal(SiteActivity.this);// 判断是否有网络连接
			if (normal.note_Intent()) {// 判断是否有网络连接
				packge();
			}
			else {
				Toast.makeText(getApplicationContext(), "请连接网络",
						Toast.LENGTH_SHORT).show();
			}
			
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
