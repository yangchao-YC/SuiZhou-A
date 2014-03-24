package com.example.shenyunsuizhou;


/**
 * Json参数总结
 * title  			标题
 * note   			判断当前列表是否为滑动列表 值为yes时
 * azhuadong		判断下级页面是否为滑动列表 值为yes为滑动
 * modified_time	时间
 * introtext		网页详情显示内容/或存放网页地址
 * haveimg			值为yes为有图列表反之则无图
 * cnparams			有图列表与滑动列表显示图片
 * description		内容简介
 * zcategory		获取当前列表等级，如为no则为最下级列表，点击跳转至浏览页面
 * zcategoryurl		获取下级列表的申请数据地址
 * 
 */

public class User {
	
	private int id;//主键ID
	private String infoID;//类别ID
	private String title;//信息标题
	private String note;//判断是否为滑动
	private String introtext;//判断是否为滑动
	private String modified_time;//时间
	private String cnparams;//图片地址
	private String description;//内容简介
	private String zcategory;//判断列表等级
	private String zcategoryurl;//下级列表数据地址
	
	
	/////////////getter and setter///////////////
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getInfoID() {
		return infoID;
	}
	public void setInfoID(String infoID) {
		this.infoID = infoID;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public String getModifiedTime() {
		return modified_time;
	}

	public void setModifiedTime(String modified_time) {
		this.modified_time = modified_time;
	}

	
	public String getCnparams() {
		return cnparams;
	}

	public void setCnparams(String cnparams) {
		this.cnparams = cnparams;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getZcategory() {
		return zcategory;
	}

	public void setZcategory(String zcategory) {
		this.zcategory = zcategory;
	}

	public String getZcategoryurl() {
		return zcategoryurl;
	}

	public void setZcategoryurl(String zcategoryurl) {
		this.zcategoryurl = zcategoryurl;
	}

	public String getIntrotext()
	{
		return introtext;
	}
	public void	setIntrotext(String introtext)
	{
		this.introtext = introtext;
	}
}
