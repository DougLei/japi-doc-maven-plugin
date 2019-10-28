package com.douglei.api.doc.js.variable.entity.apis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.douglei.api.doc.annotation.ApiEntity;
import com.douglei.api.doc.annotation.ApiEntityParam;
import com.douglei.api.doc.types.ParamStructType;

/**
 * {@link ApiEntity} 注解对应的实体
 * @author DougLei
 */
public class ApiEEntity {
	private static final ApiEEntity EMPTY = new ApiEEntity(ParamStructType.OBJECT.name(), 0);
	private String struct;
	private List<ApiEParamEntity> params;
	
	private ApiEEntity(String struct, int length) {
		this.struct = struct;
		params = new ArrayList<ApiEParamEntity>(length);
	}
	
	public static ApiEEntity newInstance(Class<?> entityClass, ParamStructType type) throws ClassNotFoundException {
		ApiEEntity apiEEntity = null;
		ApiEntity apiEntity = entityClass.getAnnotation(ApiEntity.class);
		if(apiEntity != null) {
			ApiEntityParam apiEntityParam=null;
			Field[] fields;
			Method[] methods;
			do {
				fields = entityClass.getDeclaredFields();
				for (Field field : fields) {
					if((apiEntityParam = field.getAnnotation(ApiEntityParam.class)) != null) {
						if(apiEEntity == null) {
							apiEEntity = new ApiEEntity(type.name(), fields.length);
						}
						apiEEntity.addParam(new EntityParameter(field), apiEntityParam);
					}
				}
				
				methods = entityClass.getDeclaredMethods();
				for (Method method : methods) {
					if((apiEntityParam = method.getAnnotation(ApiEntityParam.class)) != null && isOK(method)) {
						if(apiEEntity == null) {
							apiEEntity = new ApiEEntity(type.name(), methods.length);
						}
						apiEEntity.addParam(new EntityParameter(method), apiEntityParam);
					}
				}
				
				entityClass = entityClass.getSuperclass();
			}while(entityClass != Object.class);
		}
		return apiEEntity;
	}
	
	/**
	 * 判断方法:
	 * 	1.有返回值, 没有参数, 相当于get方法
	 * 	2.无返回值, 有一个参数, 相当于set方法
	 * @param method
	 * @return
	 */
	private static boolean isOK(Method method) {
		if(method.getReturnType() == void.class) { 
			return method.getParameterCount() == 1;
		}else {
			return method.getParameterCount() == 0;
		}
	}
	
	// 添加entity的属性配置
	private void addParam(EntityParameter parameter, ApiEntityParam apiEntityParam) throws ClassNotFoundException {
		params.add(new ApiEParamEntity(parameter, apiEntityParam));
	}
	
	public static ApiEEntity emptyInstance() {
		return EMPTY;
	}
	
	public String getStruct() {
		return struct;
	}
	public List<ApiEParamEntity> getParams() {
		return params;
	}
}
