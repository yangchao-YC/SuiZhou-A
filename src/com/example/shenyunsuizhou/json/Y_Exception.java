package com.example.shenyunsuizhou.json;

import android.content.Context;
import android.widget.Toast;


public class Y_Exception extends Exception {

	/**
	 * åº???¨ç??åº?å¼?å¸¸å?????????????·ç±»
	 * 
	 * @author Administrator
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int HTTP_ERROR = 1;
	private static final int NETWORK_ERROR = 2;
	private static final int DATA_PARSER_ERROR = 3;
	private static final int FILE_ERROT = 4;

	private int type;
	private int code;

	public Y_Exception(int type, int code, Exception exception) {
		super(exception);

		this.type = type;
		this.code = code;
	}

	/**
	 * å¼?å¸¸æ??ç¤?
	 * @param context
	 */
	public void exception_Prompt(Context context) {
		switch (type) {
		case HTTP_ERROR:
//			String msg = String.format(context.getResources().getString(R.string.http_error), code);
//			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			break;
		case NETWORK_ERROR:
//			Toast.makeText(context, R.string.network_connection_fails, Toast.LENGTH_SHORT).show();
			break;
		case DATA_PARSER_ERROR:
//			Toast.makeText(context, R.string.data_parser_error, Toast.LENGTH_SHORT).show();
			break;
		case FILE_ERROT:
//			Toast.makeText(context, R.string.file_error, Toast.LENGTH_SHORT).show();
			break;
		}
	}

	/**
	 * httpå¼?å¸?
	 * @param code
	 * @return
	 */
	public static Y_Exception http(int code) {
		return new Y_Exception(HTTP_ERROR, code, null);
	}

	/**
	 * ç½?ç»?å¼?å¸?
	 * @param e
	 * @return
	 */
	public static Y_Exception network(Exception e) {
		return new Y_Exception(NETWORK_ERROR, 0, e);
	}

	/**
	 * ??°æ??è§£æ??å¼?å¸?
	 * @param e
	 * @return
	 */
	public static Y_Exception dataParser(Exception e) {
		return new Y_Exception(DATA_PARSER_ERROR, 0, e);
	}

	/**
	 * ???ä»¶å??å¸?
	 * @param e
	 * @return
	 */
	public static Y_Exception file(Exception e) {
		return new Y_Exception(FILE_ERROT, 0, e);
	}
}
