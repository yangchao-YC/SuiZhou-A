package com.example.shenyunsuizhou;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPageAdapter extends PagerAdapter {

	private ArrayList<View>views;
	public  ViewPageAdapter(ArrayList<View>views) {
		this.views = views;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (views !=null) {
			return views.size();	
		}
		return 0;
	}

	
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager) container).addView(views.get(position),0);
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return (arg0 == arg1);
	}

}
