package com.douglei.api.doc.js.variable.entity.apis;

import java.util.List;
import java.util.Set;

import com.douglei.api.doc.ApiDocBuilderContext;
import com.douglei.api.doc.annotation.ApiEntityParam;
import com.douglei.api.doc.types.DataType;
import com.douglei.api.doc.types.ParamStructType;
import com.douglei.tools.utils.StringUtil;

/**
 * {@link ApiEntityParam} 注解对应的实体
 * @author DougLei
 */
public class ApiEParamEntity extends ApiParamEntity_{

	public ApiEParamEntity(EntityParameter parameter, ApiEntityParam apiEntityParam) throws ClassNotFoundException {
		this.name = StringUtil.isEmpty(apiEntityParam.name())?parameter.getName():apiEntityParam.name();
		this.length = apiEntityParam.length()==-1?"-":Integer.toString(apiEntityParam.length());
		this.precision = apiEntityParam.precision()==-1?"-":Integer.toString(apiEntityParam.precision());
		this.required = Boolean.toString(apiEntityParam.required());
		this.defaultValue = apiEntityParam.defaultValue();
		this.description = apiEntityParam.description();
		
		Class<?> parameterType = parameter.getType();
		DataType dataType = setDataType(apiEntityParam.dataType(), parameterType);
		setEgValue(apiEntityParam.egValue(), dataType, apiEntityParam.dateFormatPattern());
		setEntity(apiEntityParam, parameter, parameterType);
	}

	private DataType setDataType(DataType dataType, Class<?> type) {
		if(dataType == DataType.NULL) {
			dataType = DataType.match(type);
		}
		this.dataType = dataType.name();
		return dataType;
	}
	
	private void setEntity(ApiEntityParam apiEntityParam, EntityParameter parameter, Class<?> parameterType) throws ClassNotFoundException {
		Class<?> entityClass = (apiEntityParam.entity() != Object.class)?apiEntityParam.entity():getEntityClass(parameterType, parameter);
		ParamStructType structType = (apiEntityParam.entityStruct() != ParamStructType.NULL)?apiEntityParam.entityStruct():getStructType(parameterType);
		this.entity = ApiEEntity.newInstance(entityClass, structType);
	}
	
	// 获取实体的类型, 目前只支持数组和List,Set集合
	private Class<?> getEntityClass(Class<?> type, EntityParameter parameter) throws ClassNotFoundException {
		if(type == List.class || type == Set.class) {
			return ApiDocBuilderContext.getClassLoader().loadClass((parameter.getGenericType()).getActualTypeArguments()[0].getTypeName());
		}
		if(type.isArray()) {
			return parameter.getType().getComponentType();
		}
		return type;
	}
	
	// 获取属性类型的结构, 目前只支持数组和List,Set集合
	private ParamStructType getStructType(Class<?> type) {
		if(type.isArray() || type == List.class || type == Set.class) {
			return ParamStructType.ARRAY;
		}
		return ParamStructType.OBJECT;
	}
}
