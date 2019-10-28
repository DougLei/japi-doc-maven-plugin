package com.douglei.api.doc.js.variable.entity.about;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.douglei.api.doc.ApiDocBuilderContext;

/**
 * 关于页面的数据
 * @author DougLei
 */
public class AboutEntity {
	private String[] authors;
	private String[] devEnvironmentUrls;
	private String[] testEnvironmentUrls;
	private String[] prodEnvironmentUrls;
	
	public AboutEntity(String[] authors, String[] devEnvironmentUrls, String[] testEnvironmentUrls, String[] prodEnvironmentUrls) {
		this.authors = authors;
		this.devEnvironmentUrls = devEnvironmentUrls;
		this.testEnvironmentUrls = testEnvironmentUrls;
		this.prodEnvironmentUrls = prodEnvironmentUrls;
	}
	
	public String[] getAuthors() {
		return authors;
	}
	public String getCreateDate() {
		return ApiDocBuilderContext.getDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	public String[] getDevUrls() {
		return devEnvironmentUrls;
	}
	public String[] getTestUrls() {
		return testEnvironmentUrls;
	}
	public String[] getProdUrls() {
		return prodEnvironmentUrls;
	}
	
	public String toJScript() {
		return "var about="+JSONObject.toJSONString(this) +"\n";
	}
}
