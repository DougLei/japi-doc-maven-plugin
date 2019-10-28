package com.douglei.api.doc.js.variable.entity.apis;

import java.util.Date;

import com.douglei.api.doc.ApiDocBuilderContext;
import com.douglei.api.doc.annotation.ApiParam_;
import com.douglei.api.doc.types.DataType;

/**
 * {@link ApiParam_} 注解对应的实体
 * @author DougLei
 */
public class ApiParamEntity_ {
	
	protected String name;
	protected String dataType;
	protected String length;
	protected String precision;
	protected String required;
	protected String defaultValue;
	protected String description;
	protected String egValue;
	protected ApiEEntity entity;
	
	protected ApiParamEntity_() {
	}
	public ApiParamEntity_(ApiParam_ apiParam_) throws ClassNotFoundException {
		this.name = apiParam_.name();
		this.dataType = apiParam_.dataType().name();
		this.length = apiParam_.length()==-1?"-":Integer.toString(apiParam_.length());
		this.precision = apiParam_.precision()==-1?"-":Integer.toString(apiParam_.precision());
		this.required = Boolean.toString(apiParam_.required());
		this.defaultValue = apiParam_.defaultValue();
		this.description = apiParam_.description();
		setEgValue(apiParam_.egValue(), apiParam_.dataType(), apiParam_.dateFormatPattern());
		this.entity = ApiEEntity.newInstance(apiParam_.entity(), apiParam_.entityStruct());
	}

	protected void setEgValue(String egValue, DataType dataType, String formatPattern) {
		this.egValue = egValue.trim();
		if(dataType == DataType.DATE && "".equals(this.egValue)) {
			this.egValue = ApiDocBuilderContext.getDateFormat(formatPattern).format(new Date());
		}
	}
	
	public String getName() {
		return name;
	}
	public String getDataType() {
		return dataType;
	}
	public String getLength() {
		return length;
	}
	public String getPrecision() {
		return precision;
	}
	public String getRequired() {
		return required;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public String getDescription() {
		return description;
	}
	public String getEgValue() {
		return egValue;
	}
	public ApiEEntity getEntity() {
		return entity;
	}
}
