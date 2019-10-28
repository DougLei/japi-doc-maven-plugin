package com.douglei.api.doc.js.variable.entity.apis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 
 * @author DougLei
 */
class EntityParameter {
	private Field field;
	private Method method;
	
	public EntityParameter(Field field) {
		this.field = field;
	}
	public EntityParameter(Method method) {
		this.method = method;
	}
	
	public String getName() {
		if(field != null) {
			return field.getName();
		}
		String name = method.getName();
		if(name.startsWith("get") || name.startsWith("set")) {
			if(name.charAt(3) > 64 && name.charAt(3) < 91) {
				return ((char)(name.charAt(3)+32)) + name.substring(4);
			}
			return name.substring(3);
		}
		return name;
	}
	
	public Class<?> getType() {
		if(field != null) {
			return field.getType();
		}
		if(method.getParameterCount() == 0) {// get
			return method.getReturnType();
		}else {// set
			return method.getParameterTypes()[0];
		}
	}
	
	public ParameterizedType getGenericType() {
		if(field != null) {
			return (ParameterizedType) field.getGenericType();
		}
		if(method.getParameterCount() == 0) {// get
			return (ParameterizedType) method.getGenericReturnType();
		}else {// set
			return (ParameterizedType) method.getGenericParameterTypes()[0];
		}
	}
}
