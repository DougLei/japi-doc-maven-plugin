package com.douglei.api.doc.metadata;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.douglei.api.doc.annotation.Api;
import com.douglei.api.doc.js.variable.entity.apis.ApiEntity;
import com.douglei.tools.utils.StringUtil;

/**
 * {@link Api} 注解的元数据
 * @author DougLei
 */
public class ApiMetadata {
	private ApiMetadata() {}
	
	private Api api;
	private String methodName;
	
	private String value;// api的url地址
	private String name;// 操作名
	private String description;// 操作详细描述
	private String requestMethod;
	
	static ApiMetadata newInstance(Method apiMethod) {
		Api api = apiMethod.getAnnotation(Api.class);
		if(api != null) {
			ApiMetadata apiMetadata = new ApiMetadata();
			apiMetadata.api = api;
			apiMetadata.methodName = apiMethod.getName();
			apiMetadata.value = Utils.getUrl(api.value(), apiMethod.getAnnotation(RequestMapping.class));
			apiMetadata.name = StringUtil.isEmpty(api.name())?apiMethod.getName():api.name();
			apiMetadata.description = StringUtil.isEmpty(api.description())?apiMetadata.name:api.description();
			apiMetadata.setRequestMethod(apiMethod.getAnnotation(RequestMapping.class), api.requestMethod());
			return apiMetadata;
		}
		return null;
	}
	
	/**
	 * 设置请求方式, 先从(springmvc中的注解)@RequestMapping中找, 没有找到再使用原生配置中的请求方式
	 * @param requestMapping
	 * @param requestMethod
	 */
	private void setRequestMethod(RequestMapping requestMapping, RequestMethod requestMethod) {
		if(requestMapping != null && requestMapping.method().length > 0) {
			this.requestMethod = requestMapping.method()[0].name();
		}else {
			this.requestMethod = requestMethod.name();
		}
	}
	
	// 插入url的头信息, 即将 {@link ApiCatalog} 中配置的url, 作为 {@link Api} 中url的前部分
	ApiMetadata insertUrlHead(String urlHead) {
		if(StringUtil.isEmpty(urlHead)) {
			urlHead = "/";
		}
		if(urlHead.charAt(urlHead.length()-1) == '/') {
			if(this.value.charAt(0) == '/') {
				this.value = urlHead + value.substring(1);
			}else {
				this.value = urlHead + value;
			}
		}else {
			if(this.value.charAt(0) == '/') {
				this.value = urlHead + value;
			}else {
				this.value = urlHead +'/'+ value;
			}
		}
		return this;
	}
	
	/**
	 * 转换为api实体对象
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public ApiEntity toApiEntity() throws ClassNotFoundException {
		return new ApiEntity(api, name, description, requestMethod, value);
	}

	public String getName() {
		return name;
	}
	public String getMethodName() {
		return methodName;
	}
}