package com.example.shenyunsuizhou;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;

import com.example.shenyunsuizhou.json.DataManeger;
import com.example.shenyunsuizhou.json.Normal;
import com.example.shenyunsuizhou.json.Test_Bean;
import com.example.shenyunsuizhou.json.Test_Model;
import com.example.shenyunsuizhou.json.Y_Exception;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class Launch extends Activity {

	private Timer timer = new Timer();
	private TimerTask task;
	private FinalDb db;
	
	//private String urlString ="http://121.199.29.181/demo/joomla/suizhou/index.php?option=com_content&view=category&layout=blog&id=1&statez=1";
	private String urlString ="http://119.36.193.147/index.php?option=com_content&view=category&layout=blog&id=1&statez=1";
	private ArrayList<HashMap<String, String>> dateMap =  new ArrayList<HashMap<String, String>>();	
	//private ProgressDialog progressDialog; //刷新数据时的框
	Normal normal; //连网的判断
	private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_suizhou/"; 
	 
	private String[] filename; //图片名
	//private Bitmap[] mBitmap;	 
	private String[] id;
	private String[] title;
	private String[] zcategoryurl;
	private String[] stype;
	private String[] metakey;
	 
	public  ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>() ;
	public  HashMap<String, Object> listInfo = new HashMap<String, Object>();
	TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		MobclickAgent.updateOnlineConfig(Launch.this);
		
		db = FinalDb.create(this);      
        tm  = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE); 
      
       
        String userId = tm.getDeviceId().replaceAll("\\s*", ""); 
        String model = android.os.Build.MODEL.replaceAll("\\s*", ""); 
        count(userId,model);//查询是否为第一次登录
		
        normal = new Normal(this);
  
        urlString = urlString+"&userid="+userId+"&model="+model;
		
		if (normal.note_Intent()) {
			//progressDialog = ProgressDialog.show(Launch.this, "", "正在刷新...", true, false);		
			downloadInfo();
				 
		}
		else {
			Toast.makeText(getApplicationContext(), "请链接网络", Toast.LENGTH_SHORT).show();				
		}	
		
	
						
		
	}

	
	private void downloadInfo() {
		// TODO Auto-generated method stub
		
		new Thread(){
			@Override
			public void run() {				
				try {												
					Test_Bean data = DataManeger.getTestData(urlString);
					ArrayList<Test_Model> datalist = data.getData();
					for (Test_Model test_Model : datalist) {							
						  HashMap<String, String> map=new HashMap<String, String>();					
						  map.put("id", (test_Model.getId()==null? "": test_Model.getId()));					
						  map.put("title", (test_Model.getTitle()==null? "": test_Model.getTitle()));				
						  map.put("cnparams", (test_Model.getCnparams()==null? "": test_Model.getCnparams()));					
						  map.put("zcategoryurl",(test_Model.getZcategoryurl()==null? "": test_Model.getZcategoryurl()));
						  map.put("stype",(test_Model.getStype()==null? "": test_Model.getStype()));
						  map.put("metakey",(test_Model.getMetakey()==null? "": test_Model.getMetakey()));
						  dateMap.add(map);							  						
			    	}	
				handler.sendEmptyMessage(0);					
				} catch (Y_Exception e) {
					// TODO: handle exception
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
			switch (msg.what) {
			case 0:		
				new Thread(connectNet).start();  
				break;
			case 1:	
				//progressDialog.dismiss();
				handler.sendEmptyMessage(2);			
				break;	
			case 2:
				timeStart();			
				break;
			default:
				break;
			}
		}	
	};
	
	 /* 
     * 连接网络 
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问 
     */  
    private Runnable connectNet = new Runnable(){  
        @Override  
        public void run() {  
            try {  
              
            	filename = new String[dateMap.size()];
            	//mBitmap = new Bitmap[dateMap.size()];           	
            	id = new String[dateMap.size()];
            	title = new String[dateMap.size()];
            	zcategoryurl = new String[dateMap.size()];  
            	stype = new String[dateMap.size()];
            	metakey = new String[dateMap.size()];
            	for (int i = 0; i < dateMap.size(); i++) {
					filename[i] = i+".jpg";
					id[i] = dateMap.get(i).get("id");
					title[i] = dateMap.get(i).get("title");
					zcategoryurl[i] = dateMap.get(i).get("zcategoryurl");
					stype[i] = dateMap.get(i).get("stype");
					metakey[i] = dateMap.get(i).get("metakey");
					byte[] data = getImage(dateMap.get(i).get("cnparams")); 
					 if(data!=null){  
						  bitmaps.add(BitmapFactory.decodeByteArray(data, 0, data.length));
		                  //  mBitmap[i] = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
		                }
				}  
            	
            	listInfo.put("id", id);
            	listInfo.put("title", title);
            	listInfo.put("zcategoryurl", zcategoryurl);
            	listInfo.put("stype", stype);
                listInfo.put("metakey", metakey);
                // 发送消息
            	handler.sendEmptyMessage(1);
                //Log.d(TAG, "set image ...");  
            } catch (Exception e) {  
              //  Toast.makeText(Launch.this,"无法链接网络！", 1).show();  
                e.printStackTrace();  
            }  
  
        }  
  
    };  
	
    /** 
     * Get image from newwork 
     * @param path The path of image 
     * @return byte[] 
     * @throws Exception 
     */  
    public byte[] getImage(String path) throws Exception{  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setConnectTimeout(5 * 1000);  
        conn.setRequestMethod("GET");  
        InputStream inStream = conn.getInputStream();  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
            return readStream(inStream);  
        }  
        return null;  
    }  
    
    /** 
     * Get data from stream 
     * @param inStream 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    }  
    
	private void count(final String userId, final String model)
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
						//tm.getDeviceId()
						Test_Bean data = DataManeger.getTestData("http://119.36.193.147/index.php?option=com_content&view=category&layout=blog&sbid="+userId+"&sbxh="+model+"&statez=4");
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
				//intent.putExtra("home", "yes");
				//intent.putExtra("id", id);
				//intent.putExtra("title", title);
				//intent.putExtra("stype", stype);
				//intent.putExtra("zcategoryurl", zcategoryurl);
			    HomeActivity.bitmaps = bitmaps;
			    HomeActivity.listInfo = listInfo;
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
