package com.example.shenyunsuizhou;

public class UserLaunch {

	private int id;//主键ID
	private String infoID;//类别ID
	private String title;//信息标题
	//private String cnparams;
	private String zcategoryurl;
	private String stype;
	private String metakey;
	
	
	
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
	/*
	public String getCnparams() {
		return cnparams;
	}

	public void setCnparams(String cnparams) {
		this.cnparams = cnparams;
	}
	*/
	public String getZcategoryurl() {
		return zcategoryurl;
	}

	public void setZcategoryurl(String zcategoryurl) {
		this.zcategoryurl = zcategoryurl;
	}
	
	public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}
	
	public String getMetakey() {
		return metakey;
	}

	public void setMetakey(String metakey) {
		this.metakey = metakey;
	}
	
}
