package com.douglei.api.doc.maven.plugin;

import com.douglei.api.doc.types.ParamStructType;

/**
 * 通用参数
 * @author DougLei
 */
public class CommonParam {
	
	/**
	 * 通用参数的结构, 值包括 [object, array, object_or_array], 不区分大小写, 默认值为object
	 * object标识为对象结构, array标识为数组/集合结构, object_or_array标识为对象或数组/集合结构
	 */
	private String struct = "object";
	
	/**
	 * 对应配置类的全路径
	 */
	private String clz;
	
	public boolean unEmpty(ClassLoader classloader) {
		try {
			classloader.loadClass(clz);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	public ParamStructType getStruct() {
		if("array".equalsIgnoreCase(struct)) {
			return ParamStructType.ARRAY;
		}else if("object_or_array".equalsIgnoreCase(struct)) {
			return ParamStructType.OBJECT_OR_ARRAY;
		}else {
			return ParamStructType.OBJECT;
		}
	}
	
	public Class<?> getClz(ClassLoader classloader) throws ClassNotFoundException{
		return classloader.loadClass(clz);
	}
}
