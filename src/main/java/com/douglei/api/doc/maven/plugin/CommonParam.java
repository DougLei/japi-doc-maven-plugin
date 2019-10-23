package com.douglei.api.doc.maven.plugin;

import com.douglei.api.doc.enums.ParamStructureType;
import com.douglei.tools.utils.reflect.ClassLoadUtil;
import com.douglei.tools.utils.reflect.ValidationUtil;

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
	
	public boolean unEmpty() {
		return ValidationUtil.classExists(clz);
	}
	
	public ParamStructureType getStruct() {
		if("array".equalsIgnoreCase(struct)) {
			return ParamStructureType.ARRAY;
		}else if("object_or_array".equalsIgnoreCase(struct)) {
			return ParamStructureType.OBJECT_OR_ARRAY;
		}else {
			return ParamStructureType.OBJECT;
		}
	}
	
	public Class<?> getClz(){
		return ClassLoadUtil.loadClass(clz);
	}
}
