package com.example.shenyunsuizhou;

import java.util.Timer;
import java.util.TimerTask;

import com.example.shenyunsuizhou.ListViewActivity.backOnClick;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Develop extends Activity {

	private Button backButton;
	private TextView tabTextView;
	private String barString = null;
	private TextView contentTextView1;
	private TextView contentTextView2;
	private TextView contentTextView3;
	private TextView contentTextView4;
	private TextView contentTextView5;
	private String textString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.develop);
		
		barString = (String)getIntent().getExtras().getString("tabText");//接收导航条名
		textString = (String)getIntent().getExtras().getString("text");//接收导航条名

		backButton = (Button)findViewById(R.id.Develop_Button_back);
		tabTextView = (TextView)findViewById(R.id.Develop_tabBarText);
		contentTextView1 = (TextView)findViewById(R.id.Develop_Text_content1);
		contentTextView2 = (TextView)findViewById(R.id.Develop_Text_content2);
		contentTextView3 = (TextView)findViewById(R.id.Develop_Text_content3);
		contentTextView4 = (TextView)findViewById(R.id.Develop_Text_content4);
		contentTextView5 = (TextView)findViewById(R.id.Develop_Text_content5);

		if (textString.equals("神韵随州简介")) {
			contentTextView2.setVisibility(View.VISIBLE);
			contentTextView3.setVisibility(View.VISIBLE);
			contentTextView4.setVisibility(View.VISIBLE);
			contentTextView5.setVisibility(View.VISIBLE);
		}
		else {
			contentTextView1.setText(textString);
		}
		tabTextView.setText(barString);
		
		backButton.setOnClickListener(new backOnClick());

	}

	class backOnClick implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onBackPressed();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
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
