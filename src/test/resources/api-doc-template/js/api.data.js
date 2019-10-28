var index = {
	name: "国际化服务API",
	version: "1.0",
	treeData: [
		{
		text: '国际化消息api',
		selectable: false,
		tags: ['1'],
		nodes: [
			  {
				text: '添加国际化消息',
				key: 'xxx.xxx.xxxController'
			  }
			]
		}
	]
}

var about = {
	authors:["StoneKing", "DougLei"], 
	createDate:"2019-12-12",
	devUrls: ["http://192.168.1.111:8080/dev"],
	testUrls: ["http://192.168.1.111:8081/test"],
	prodUrls: ["-"]
}

var commonHeader={struct:"OBJECT", params:[]}
var commonUrl={struct:"OBJECT", params:[]}
var commonRequest={struct:"OBJECT", params:[]}
var commonResponse={struct:"OBJECT", params:[]}

var apis = {
	"xxx.xxx.xxxController": {
		"name":"创建国际化文件x",
		description:"描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述",
		requestMethod: "POST",
		requestUrl: "/i18n/message/add",
		url: {
			struct:"NULL",
			params:[]
		},
		header: {
			struct:"NULL",
			params:[]
		},
		request: {
			struct:"OBJECT",
			params:[
				{
					name:"_token",
					dataType:"STRING",
					length:"36",
					precision:"-",
					required:"true",
					defaultValue:"-",
					description:"登录时系统返回的token值",
					egValue: "12"
				},
				{
					name:"_log",
					dataType:"STRING",
					length:"-",
					precision:"-",
					required:"false",
					defaultValue:"-",
					description:"提供操作日志相关的信息",

					entity: {
						struct: "OBJECT",
						params: [
							{
								name:"batch",
								dataType:"STRING",
								length:"36",
								precision:"-",
								required:"false",
								defaultValue:"-",
								description:"操作批次, 用来区分是否是同一次操作的请求",
								egValue:""
							}	
						]
					}
					
				}
			]
		},
		response: {
			struct:"OBJECT",
			params:[
				{
					name:"_token",
					dataType:"STRING",
					length:"36",
					precision:"-",
					required:"true",
					defaultValue:"-",
					description:"登录时系统返回的token值",
					egValue: "12"
				}
			]
		}
	}
}