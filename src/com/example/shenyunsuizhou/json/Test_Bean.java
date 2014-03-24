package com.example.shenyunsuizhou.json;

import java.util.ArrayList;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class Test_Bean {

	private ArrayList<Test_Model> msg;

	public ArrayList<Test_Model> getData() {
		return msg;
	}

	public void setData(ArrayList<Test_Model> msg) {
		this.msg = msg;
	}

	/**
	 * @param data
	 * @return
	 * @throws WHT_Exception 
	 */
	public static Test_Bean dataParser(String data) throws Y_Exception {
		Test_Bean reigst = new Test_Bean();

		try {
			ArrayList<Test_Model> myanswerlist = (ArrayList<Test_Model>) JsonUtil.DataToObject(data, new TypeToken<ArrayList<Test_Model>>(){}.getType());
			reigst.setData(myanswerlist);
		} 
		catch(JsonParseException e) {
			throw Y_Exception.dataParser(e);
		}
		
		return reigst;
	}
}
