package com.example.shenyunsuizhou;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;

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
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;

public class Launch extends Activity {

	private Timer timer = new Timer();
	private TimerTask task;
	private FinalDb db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		MobclickAgent.updateOnlineConfig(Launch.this);
		
		db = FinalDb.create(this);
		
		count();//查询是否为第一次登录
		
		timeStart();
		
		
		
		
	}

	
	private void count()
	{
		new Thread()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String condition ="infoID='" + "1" + "'";//搜索条件
				List<Count> list = db.findAllByWhere(Count.class, condition);
				
				if (list.size() == 0) {
					try {
						Test_Bean data = DataManeger.getTestData("http://119.36.193.148/suizhou/api/userc");
					} catch (Y_Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Count user = new Count();
					user.setInfoID("1");
					db.save(user);
					
				}
				
			}
			
		}.start();
	}
	
	
	private void timeStart()
	{
		task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				timer.cancel();
				Intent intent = new Intent(Launch.this,HomeActivity.class);
				intent.putExtra("home", "yes");
				startActivity(intent);
				finish();
				
			}
		};
		
		timer.schedule(task, 1000,1000);
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}
