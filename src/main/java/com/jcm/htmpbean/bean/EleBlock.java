package com.jcm.htmpbean.bean;

import java.util.ArrayList;
import java.util.List;

public class EleBlock {
	private List<EleMeta> eleMetas=new ArrayList<EleMeta>();
	private List<String> currentPath=new ArrayList<String>();
	private List<EleBlock>subBlocks=new ArrayList<EleBlock>();
	public List<EleMeta> getEleMetas() {
		return eleMetas;
	}
	public void setEleMetas(List<EleMeta> eleMetas) {
		this.eleMetas = eleMetas;
	}
	public List<EleBlock> getSubBlocks() {
		return subBlocks;
	}
	public void setSubBlocks(List<EleBlock> subBlocks) {
		this.subBlocks = subBlocks;
	}
	public List<String> getCurrentPath() {
		return currentPath;
	}
	public void setCurrentPath(List<String> currentPath) {
		this.currentPath = currentPath;
	}
}
