package com.jcm.htmpbean.bean;

import java.io.Serializable;

public class EleMeta implements Serializable{
	private static final long serialVersionUID = 529153460887330822L;
	private String htmlpath="";//html 树路径
	private String jname; //json key值
	private String proname;//属性名
	private String type="txt"; //类型信息是 属性 或者 文本  attr /  txt
	
	
	private Boolean isregxp=false;//是否使用正则匹配
	private String fistregstr;//首正则字符
	private String lastregstr;//末正则字符
	public String getHtmlpath() {
		return htmlpath;
	}
	public void setHtmlpath(String htmlpath) {
		this.htmlpath = htmlpath;
	}
	public String getJname() {
		return jname;
	}
	public void setJname(String jname) {
		this.jname = jname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProname() {
		return proname;
	}
	public void setProname(String proname) {
		this.proname = proname;
	}

	public String getFistregstr() {
		return fistregstr;
	}
	public void setFistregstr(String fistregstr) {
		this.fistregstr = fistregstr;
	}
	public String getLastregstr() {
		return lastregstr;
	}
	public void setLastregstr(String lastregstr) {
		this.lastregstr = lastregstr;
	}
	
	public Boolean getIsregxp() {
		return isregxp;
	}
	public void setIsregxp(Boolean isregxp) {
		this.isregxp = isregxp;
	}
	public void clone(EleMeta eleMeta) {
		this.htmlpath=eleMeta.htmlpath;
		this.jname=eleMeta.jname;
		this.proname=eleMeta.proname;
		this.fistregstr=eleMeta.fistregstr;
		this.lastregstr=eleMeta.lastregstr;
		this.isregxp=eleMeta.isregxp;
		this.type=eleMeta.type;
	}
	public String print()
	{
		return 	"htmlpath="+this.htmlpath+
		"\tjname="+this.jname+
		"\tproname="+this.proname+
		"\tfistregstr="+this.fistregstr+
		"\tlastregstr="+this.lastregstr+
		"\tisregxp="+this.isregxp+
		"\ttype="+this.type;
	}
}
