package com.douglei.api.doc.types;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.douglei.tools.instances.file.resources.reader.PropertiesReader;
import com.douglei.tools.utils.reflect.ClassLoadUtil;

/**
 * 参数的数据类型
 * @author DougLei
 */
public enum DataType {
	NULL, INTEGER, DOUBLE, STRING, DATE, BOOLEAN, OBJECT;
	
	/**
	 * 根据传入的类型, 决定是哪种数据类型
	 * @param type
	 * @return
	 */
	public static DataType match(Class<?> type) {
		if(type.isArray()) return OBJECT;
		if(type.isEnum()) return STRING;
		
		DataType dataType = DataTypeMatchCache.cache.get(type);
		if(dataType == null) {
			throw new IllegalAccessError("目前不支持属性class=["+type+"]的类型, 可通过datatype.match.config.properties配置文件追加该类型, 或联系开发人员");
		}
		return dataType;
	}
	
	private static class DataTypeMatchCache{
		static final Map<Class<? extends Object>, DataType> cache = new HashMap<Class<? extends Object>, DataType>(32);
		static {
			cache.put(byte.class, INTEGER);
			cache.put(short.class, INTEGER);
			cache.put(int.class, INTEGER);
			cache.put(long.class, INTEGER);
			cache.put(Byte.class, INTEGER);
			cache.put(Short.class, INTEGER);
			cache.put(Integer.class, INTEGER);
			cache.put(Long.class, INTEGER);
			
			cache.put(float.class, DOUBLE);
			cache.put(double.class, DOUBLE);
			cache.put(Float.class, DOUBLE);
			cache.put(Double.class, DOUBLE);
			
			cache.put(char.class, STRING);
			cache.put(Character.class, STRING);
			cache.put(String.class, STRING);
			
			cache.put(Date.class, DATE);
			
			cache.put(boolean.class, BOOLEAN);
			cache.put(Boolean.class, BOOLEAN);
			
			cache.put(Object.class, OBJECT);
			cache.put(List.class, OBJECT);
			cache.put(Set.class, OBJECT);
			cache.put(Map.class, OBJECT);
			
			PropertiesReader reader = new PropertiesReader("datatype.match.config.properties");
			if(reader.ready()) {
				reader.entrySet().forEach(entry -> {
					cache.put(ClassLoadUtil.loadClass(entry.getValue().toString()), toValue(entry.getKey().toString()));
				});
			}
		}
	}
	
	private static final Map<String, DataType> dataTypeMap = new HashMap<String, DataType>(8);
	static DataType toValue(String type) {
		if(dataTypeMap.isEmpty()) {
			DataType[] dts = DataType.values();
			for (DataType dataType : dts) {
				switch(dataType) {
					case NULL:
					case OBJECT:
						continue;
					default:
						dataTypeMap.put(dataType.name(), dataType);
				}
			}
		}
		DataType dt = dataTypeMap.get(type.toUpperCase());
		if(dt == null) {
			throw new IllegalAccessError("目前不支持DataType="+type);
		}
		return dt;
	}
}
