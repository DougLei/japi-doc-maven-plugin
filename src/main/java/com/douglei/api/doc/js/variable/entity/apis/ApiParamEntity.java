package com.douglei.api.doc.js.variable.entity.apis;

import java.util.ArrayList;
import java.util.List;

import com.douglei.api.doc.annotation.ApiParam;
import com.douglei.api.doc.annotation.ApiParam_;
import com.douglei.api.doc.types.ParamStructType;
import com.douglei.tools.utils.StringUtil;

/**
 * {@link ApiParam} 注解对应的实体
 * @author DougLei
 */
public class ApiParamEntity {
	
	private String struct;
	private List<ApiParamEntity_> params;
	
	private boolean onlyOneEntityParam;
	
	public ApiParamEntity(ApiParam apiParam, boolean existsCommonParam) throws ClassNotFoundException {
		ApiParam_[] apiParams = apiParam.params();
		if(setStruct(apiParam.struct(), (short)apiParams.length, existsCommonParam)) {
			params = new ArrayList<ApiParamEntity_>(apiParams.length);
			for (ApiParam_ apiParam_ : apiParams) {
				params.add(new ApiParamEntity_(apiParam_));
			}
			
			// 当顶层的参数配置中, 只有一个参数, 且是没有配置参数名的类参数时, 将类的属性作为参数放到顶层参数
			this.onlyOneEntityParam = params.size() == 1 && params.get(0).getEntity() != null && StringUtil.isEmpty(params.get(0).getName());
		}
	}

	private boolean setStruct(ParamStructType struct, short apiParamCount, boolean existsCommonParam) {
		if(struct == ParamStructType.COMMON && !existsCommonParam) {
			struct = ParamStructType.NULL;
		}
		this.struct = struct.name();
		return struct != ParamStructType.NULL && struct != ParamStructType.COMMON && apiParamCount > 0;
	}
	
	public String getStruct() {
		if(onlyOneEntityParam) {
			return params.get(0).getEntity().getStruct();
		}
		return struct;
	}
	public List<? extends ApiParamEntity_> getParams() {
		if(onlyOneEntityParam) {
			return params.get(0).getEntity().getParams();
		}
		return params;
	}
}
