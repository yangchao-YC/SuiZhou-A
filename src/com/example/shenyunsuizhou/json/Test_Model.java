package com.example.shenyunsuizhou.json;

/**
 * Json参数总结
 * title  			标题
 * note   			判断此列表是否为滑动列表 值为yes时
 * azhuadong		判断下级页面是否为滑动列表 值为yes为滑动
 * modified_time	时间
 * haveimg			值为yes为有图列表反之则无图
 * cnparams			有图列表与滑动列表显示图片
 * description		内容简介
 * introtext		网页详情显示内容
 * zcategory		获取当前列表等级，如为no则为最下级列表，点击跳转至浏览页面
 * zcategoryurl		获取下级列表的申请数据地址
 * 
 */


public class Test_Model {

	
	private String id;
	private String title;
	private String note;
	private String modified_time;
	private String cnparams;
	private String description;
	private String zcategory;
	private String zcategoryurl;
	private String introtext;
	
	private String stype;
    private String metakey;
    private String metadesc;
	
	public String getMetadesc() {
		return metadesc;
	}

	public void setMetadesc(String metadesc) {
		this.metadesc = metadesc;
	}

	public String getMetakey() {
		return metakey;
	}

	public void setMetakey(String metakey) {
		this.metakey = metakey;
	}

	public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public String getId() {
		return id;
	}

	public void setId(String Id) {
		this.id = Id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String Title) {
		this.title = Title;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String Note) {
		this.note = Note;
	}
	
	public String getModifiedTime() {
		return modified_time;
	}

	public void setModifiedTime(String Modified_Time) {
		this.modified_time = Modified_Time;
	}

	public String getCnparams() {
		return cnparams;
	}

	public void setCnparams(String CnParams) {
		this.cnparams = CnParams;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String Description) {
		this.description = Description;
	}
	
	public String getZcategory() {
		return zcategory;
	}

	public void setZcategory(String ZCategory) {
		this.zcategory = ZCategory;
	}

	public String getZcategoryurl() {
		return zcategoryurl;
	}

	public void setZcategoryurl(String ZCategoryUrl) {
		this.zcategoryurl = ZCategoryUrl;
	}

	public String getIntrotext()
	{
		return introtext;
	}
	public void	setIntrotext(String IntroText)
	{
		this.introtext = IntroText;
	}
}
