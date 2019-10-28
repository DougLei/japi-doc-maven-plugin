package com.douglei.api.doc.js.variable.entity.common;

import com.alibaba.fastjson.JSONObject;
import com.douglei.api.doc.js.variable.entity.apis.ApiEEntity;
import com.douglei.api.doc.types.ParamStructType;

/**
 * 通用参数
 * @author DougLei
 */
public class CommonParamEntity{
	private String varName;
	private ParamStructType type = ParamStructType.OBJECT;
	private Class<?> paramClass = Object.class;

	public CommonParamEntity(String varName) {
		this.varName = varName;
	}
	
	public void setProperties(ParamStructType type, Class<?> paramClass) {
		this.type = type;
		this.paramClass = paramClass;
	}

	public ParamStructType getType() {
		return type;
	}
	public Class<?> getParamClass() {
		return paramClass;
	}
	
	public String toJScript() throws ClassNotFoundException {
		ApiEEntity api = ApiEEntity.newInstance(paramClass, type);
		if(api == null) {
			api = ApiEEntity.emptyInstance();
		}		
		return "var "+varName+"="+JSONObject.toJSONString(api) +"\n";
	}
	
	@Override
	public String toString() {
		return "type="+type.name()+", class="+paramClass.getName();
	}
}
