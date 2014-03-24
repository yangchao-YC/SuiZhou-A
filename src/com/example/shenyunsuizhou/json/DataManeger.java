package com.example.shenyunsuizhou.json;


public class DataManeger {

	public static Test_Bean getTestData(String urlString) throws Y_Exception {
		String url = urlString;
		
		return Test_Bean.dataParser(HttpUtil.httpGet(null, url));
	}
}
