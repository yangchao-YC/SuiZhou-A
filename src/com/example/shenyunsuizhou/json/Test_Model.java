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

	
	private String Id;
	private String Title;
	private String Note;
	private String Modified_Time;
	private String CnParams;
	private String Description;
	private String ZCategory;
	private String ZCategoryUrl;
	private String IntroText;

	
	public String getId() {
		return Id;
	}

	public void setId(String Id) {
		this.Id = Id;
	}
	
	public String getTitle() {
		return Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}
	
	public String getNote() {
		return Note;
	}

	public void setNote(String Note) {
		this.Note = Note;
	}
	
	public String getModifiedTime() {
		return Modified_Time;
	}

	public void setModifiedTime(String Modified_Time) {
		this.Modified_Time = Modified_Time;
	}

	public String getCnparams() {
		return CnParams;
	}

	public void setCnparams(String CnParams) {
		this.CnParams = CnParams;
	}
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String Description) {
		this.Description = Description;
	}
	
	public String getZcategory() {
		return ZCategory;
	}

	public void setZcategory(String ZCategory) {
		this.ZCategory = ZCategory;
	}

	public String getZcategoryurl() {
		return ZCategoryUrl;
	}

	public void setZcategoryurl(String ZCategoryUrl) {
		this.ZCategoryUrl = ZCategoryUrl;
	}

	public String getIntrotext()
	{
		return IntroText;
	}
	public void	setIntrotext(String IntroText)
	{
		this.IntroText = IntroText;
	}
}
