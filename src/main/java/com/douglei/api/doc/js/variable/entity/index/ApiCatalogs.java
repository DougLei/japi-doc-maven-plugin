package com.douglei.api.doc.js.variable.entity.index;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author DougLei
 */
public class ApiCatalogs {
	private String text;
	private short apiCount;
	private String className;
	private List<Apis> apis;
	
	public ApiCatalogs(String name, short apiCount, String className) {
		this.text = name;
		this.apiCount = apiCount;
		this.className = className;
		apis = new ArrayList<Apis>(apiCount);
	}
	
	public String addApi(String name, String methodName) {
		String key = className+"."+methodName;
		this.apis.add(new Apis(name, key));
		return key;
	}
	
	public String getText() {
		return text;
	}
	public boolean getSelectable() {
		return false;
	}
	public String[] getTags() {
		return new String[] {Integer.toString(apiCount)};
	}
	public List<Apis> getNodes() {
		return apis;
	}
}
