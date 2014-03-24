package com.example.shenyunsuizhou.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Json??°æ??å¤????ç±?
 * @author Administrator
 *
 */
public class JsonUtil {

	
	/**
	 * Json??°æ??è½?Listå¯¹è±¡
	 * @param data
	 * @return
	 */
	public static List DataToList(String data) {
		if(data != null) {
			Gson gson = new Gson();
			List<Map> map = (List<Map>)gson.fromJson(data, new TypeToken<List<Map>>(){}.getType());
			return map;
		}
		return null;
	}
	
	/**
	 * Json??°æ??è½?Mapå¯¹è±¡
	 * @param data
	 * @return
	 */
	public static Map DataToMap(String data) {
		if(data != null) {
			Gson gson = new Gson();
			Map map = (Map)gson.fromJson(data, new TypeToken<Map>(){}.getType());
			return map;
		}
		return null;
	}
	
	/**
	 * Json??°æ??è½?å¯¹è??
	 * @param data
	 * @param c
	 * @return
	 */
	public static Object DataToObject(String data, Type c) {
		
		if(data != null) {
			Gson gson = new Gson();
			Object object = (Object)gson.fromJson(data, c);
			return object;
		}
		return null;
	}
	
}
