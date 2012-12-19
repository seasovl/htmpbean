package com.jcm.htmpbean.bean;

import java.io.Serializable;

public class EleMeta implements Serializable{
	private static final long serialVersionUID = 529153460887330822L;
	private String htmlpath;
	private String jname;
	private String type;
	private String proname;
	private String regstr;
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
	public String getRegstr() {
		return regstr;
	}
	public void setRegstr(String regstr) {
		this.regstr = regstr;
	}
	protected EleMeta clone() {
		EleMeta eleMeta=new EleMeta();
		eleMeta.htmlpath=this.htmlpath;
		eleMeta.jname=this.jname;
		eleMeta.proname=this.proname;
		eleMeta.regstr=this.regstr;
		eleMeta.type=this.type;
		return eleMeta;
	}
}
