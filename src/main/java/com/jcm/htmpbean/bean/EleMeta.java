package com.jcm.htmpbean.bean;

import java.io.Serializable;
import java.util.regex.Pattern;

public class EleMeta implements Serializable{
	private static final long serialVersionUID = 529153460887330822L;
	private String htmlpath="";//html 树路径
	private String jname; //json key值
	private String proname;//属性名
	private String type="txt"; //类型信息是 属性 或者 文本  attr /  txt
	private Boolean isRef=false;
	
	private Boolean isregxp=false;//是否使用正则匹配
	private Pattern fistregstr;//首正则字符
	private Pattern lastregstr;//末正则字符
	
	private Boolean equals=false;
	private String equalsname;
	private String equalsvalue;
	public String getHtmlpath() {
		return htmlpath;
	}
	public void setHtmlpath(String htmlpath) {
		this.htmlpath = htmlpath;
	}
	public String getJname() {
		return jname;
	}
	public Boolean getIsRef() {
		return isRef;
	}
	public void setIsRef(Boolean isRef) {
		this.isRef = isRef;
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

	
	
	public Pattern getFistregstr() {
		return fistregstr;
	}
	public void setFistregstr(Pattern fistregstr) {
		this.fistregstr = fistregstr;
	}
	public Pattern getLastregstr() {
		return lastregstr;
	}
	public void setLastregstr(Pattern lastregstr) {
		this.lastregstr = lastregstr;
	}
	public Boolean getIsregxp() {
		return isregxp;
	}
	public void setIsregxp(Boolean isregxp) {
		this.isregxp = isregxp;
	}
	
	public Boolean getEquals() {
		return equals;
	}
	public void setEquals(Boolean equals) {
		this.equals = equals;
	}
	public String getEqualsname() {
		return equalsname;
	}
	public void setEqualsname(String equalsname) {
		this.equalsname = equalsname;
	}
	public String getEqualsvalue() {
		return equalsvalue;
	}
	public void setEqualsvalue(String equalsvalue) {
		this.equalsvalue = equalsvalue;
	}
	public void clone(EleMeta eleMeta) {
		this.htmlpath=eleMeta.htmlpath;
		this.jname=eleMeta.jname;
		this.proname=eleMeta.proname;
		this.fistregstr=eleMeta.fistregstr;
		this.lastregstr=eleMeta.lastregstr;
		this.isregxp=eleMeta.isregxp;
		this.type=eleMeta.type;
		this.equals=eleMeta.equals;
		this.equalsname=eleMeta.equalsname;
		this.equalsvalue=eleMeta.equalsvalue;
	}
	public void initEleMeta()
	{
		this.setHtmlpath("/");
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
