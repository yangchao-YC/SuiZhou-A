package adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.shenyunsuizhou.ListViewActivity;
import com.example.shenyunsuizhou.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {

	
	private Activity activity;  
    private ArrayList<HashMap<String, String>> data;  
    private static LayoutInflater inflater=null;  
    public ImageLoader imageLoader; //用来下载图片的类，后面有介绍  
    
    public LazyAdapter(Activity a,ArrayList<HashMap<String, String>> d){
    	activity = a;
    	data = d;
    	inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	imageLoader = new ImageLoader(activity.getApplicationContext());
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi=convertView;
		if (convertView == null) 
			vi = inflater.inflate(R.layout.listview_listview, null);
		
		
		
			TextView title = (TextView)vi.findViewById(R.id.List_listview_Text_Title);
			TextView artist = (TextView)vi.findViewById(R.id.List_listview_Text_content);
			TextView time = (TextView)vi.findViewById(R.id.List_listview_Text_Time);
			ImageView imageViews = (ImageView)vi.findViewById(R.id.List_listview_Image);
			
			HashMap<String, String> song = new HashMap<String, String>();
			
			song = data.get(position);
			title.setText(song.get(ListViewActivity.KEY_TITLE));
			artist.setText(song.get(ListViewActivity.KEY_DESCRIPTION));
			time.setText(song.get(ListViewActivity.KEY_TIME));
			imageLoader.DisplayImage(song.get(ListViewActivity.KEY_THUMB_URL), imageViews);
			return vi;
			
		
		
	}

}
