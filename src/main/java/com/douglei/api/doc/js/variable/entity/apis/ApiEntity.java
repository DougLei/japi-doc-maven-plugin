package com.douglei.api.doc.js.variable.entity.apis;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.douglei.api.doc.ApiDocBuilderContext;
import com.douglei.api.doc.annotation.Api;
import com.douglei.api.doc.types.ParamType;

/**
 * {@link Api} 注解对应的实体
 * @author DougLei
 */
public class ApiEntity {
	private String name;
	private String description;
	private String requestMethod;
	private String requestUrl;
	private Map<ParamType, ApiParamEntity> params = new HashMap<ParamType, ApiParamEntity>(4);
	
	public ApiEntity(Api api, String name, String description, String requestMethod, String requestUrl) throws ClassNotFoundException {
		this.name = name;
		this.description = description;
		this.requestMethod = requestMethod;
		this.requestUrl = requestUrl;
		
		params.put(ParamType.HEADER, new ApiParamEntity(api.header(), ApiDocBuilderContext.existsCommonHeaderParam()));
		params.put(ParamType.URL, new ApiParamEntity(api.url(), ApiDocBuilderContext.existsCommonUrlParam()));
		params.put(ParamType.REQUEST, new ApiParamEntity(api.request(), ApiDocBuilderContext.existsCommonRequestParam()));
		params.put(ParamType.RESPONSE, new ApiParamEntity(api.response(), ApiDocBuilderContext.existsCommonResponseParam()));
	}

	public String toJScript() {
		return JSONObject.toJSONString(this);
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getRequestMethod() {
		return requestMethod;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public ApiParamEntity getHeader() {
		return params.get(ParamType.HEADER);
	}
	public ApiParamEntity getUrl() {
		return params.get(ParamType.URL);
	}
	public ApiParamEntity getRequest() {
		return params.get(ParamType.REQUEST);
	}
	public ApiParamEntity getResponse() {
		return params.get(ParamType.RESPONSE);
	}
}
