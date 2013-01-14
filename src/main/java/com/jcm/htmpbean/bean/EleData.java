package com.jcm.htmpbean.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 用来存储的数据结构
 * @author Administrator
 *
 */
public class EleData implements Serializable {
	private static final long serialVersionUID = 8196318298844650870L;
	private List<Map<String, String>> blocks = new ArrayList<Map<String, String>>();
	private List<EleData> subData = new ArrayList<EleData>();

	public List<Map<String, String>> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Map<String, String>> blocks) {
		this.blocks = blocks;
	}

	public List<EleData> getSubData() {
		return subData;
	}

	public void setSubData(List<EleData> subData) {
		this.subData = subData;
	}

}
