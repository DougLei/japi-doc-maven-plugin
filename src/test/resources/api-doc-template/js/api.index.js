// 页面加载完成
var beforeExpandedNode; // 记录上一次展开的节点对象
var beforeSelectedNode;// 记录上一次选中的节点
$(function(){ 
	$("title").html(index.name);
	$("#title").after("&nbsp;"+index.name);
	$("#version").html(index.version);
	// 初始化树
	$("#menuTree").treeview({
		data: index.treeData, 
		color: "#428bca", 
		expandIcon: 'glyphicon glyphicon-chevron-right',
		collapseIcon: 'glyphicon glyphicon-chevron-down',
		emptyIcon: 'glyphicon glyphicon-cog',
		showTags: true,
		levels: 1,
		onNodeExpanded: function(event, node){
			if(beforeExpandedNode != null){
				$("#menuTree").treeview("collapseNode", [beforeExpandedNode.nodeId, {silent:true}])
			}
			beforeExpandedNode = node;
		},
		onNodeCollapsed: function(event, node){
			beforeExpandedNode = null;
		},
		onNodeSelected: function(event, node){
			beforeSelectedNode = node;
			showApiPage(node.key);
			toPageHead();
		},
		onNodeUnselected: function(event, node){
			beforeExpandedNode =null;
		}
	});

	// 初始化返回顶部按钮的样式, 主要是按钮的位置
	$("#toPageHeadButton").attr("style", "position:fixed;top:"+(document.documentElement.clientHeight-50)+"px;border-radius:8px;padding:10px;cursor:pointer;background-color:#F0F2FF;");

	// 初始化展示关于说明页面
	showAboutPage();
});


// 返回顶部
function toPageHead(){
	$("html").animate({scrollTop:0}, 500);
}

//页面滚动后出现返回顶部的按钮
window.onscroll = function(){
	if (document.body.scrollTop > 20 || document.documentElement.scrollTop >= 100) {
		$("#toPageHeadButton").attr("class", "glyphicon glyphicon-menu-up show_");
	} else {
		$("#toPageHeadButton").attr("class", "glyphicon glyphicon-menu-up hide_");
	}
}

// 导航栏按钮的事件
$("li>a").mouseover(function(){
	$(this).css("background-color","#563BD0");
});
$("li>a").mouseout(function(){
	$(this).css("background-color","");
});

// -----------------------------------------------------------------------------------------------------
// 在index.html显示关于页面
function showAboutPage(){
	var page  = "<h3 style='margin-bottom: 0px;'>关于</h3>";
	page += "	<p class='small' style='margin:0px;'>本api文档通过解析配置自动生成。实现该功能的项目github地址: ";
	page += "		<a href='https://github.com/DougLei/japi-doc.git' target='_blank' title='点我去查看' alt='点我去查看'>https://github.com/DougLei/japi-doc.git</a>";
	page += "	</p>";
	page += "<table class='table table-hover'>";
	page += "	<caption>api信息</caption>";
	page += "	<tbody>";
	page += "		<tr>";
	page += "			<td style='vertical-align:middle;border-top: none;' class='col-md-2'>编写人员</td>";
	page += "			<td style='font-family: MicrosoftYaHei;border-top: none;'>"+getAuthors(about.authors)+"</td>";
	page += "		</tr>";
	page += "		<tr>";
	page += "			<td style='vertical-align:middle;' class='col-md-2'>编写时间</td>";
	page += "			<td style='font-family: MicrosoftYaHei;'>"+about.createDate+"</td>";
	page += "		</tr>";
	page += "	</tbody>";
	page += "</table>";

	page += "<table class='table table-hover'>";
	page += "	<caption>各环境地址</caption>";
	page += "	<tbody>";
	page += getUrlHtml("开发环境", about.devUrls, "border-top: none;");
	page += getUrlHtml("测试环境", about.testUrls, "");
	page += getUrlHtml("生产环境", about.prodUrls, "");
	page += "	</tbody>";
	page += "</table>";
	$("#apiDetailDIV").html(page);

	if(beforeSelectedNode != null){
		$("#menuTree").treeview("unselectNode", [beforeSelectedNode.nodeId, {silent:true}]);
		beforeExpandedNode =null;
	}
}


// 获取作者名称, 多个用, 分割
function getAuthors(authors){
	if(authors.length==1){
		return authors[0];
	}else{
		var author_ = "";
		$.each(authors, function(index, author){
			author_ += author;
			if(index < authors.length-1){
				author_ += ", ";
			}
		});
		return author_;
	}
}

// 获取url信息的html
function getUrlHtml(description, urls, borderStyle){
	var html = "<tr>";
	html += " <td rowspan='"+urls.length+"' style='vertical-align:middle;"+borderStyle+"' class='col-md-2'>"+description+"</td>";
	$.each(urls, function(i, url){
		if(i == 0){
			html += "<td style='font-family: MicrosoftYaHei;"+borderStyle+"'>"+url+"</td>";
		}else{
			html += "<tr>";
			html += "<td style='font-family: MicrosoftYaHei;'>"+url+"</td>";
		}
		html += "</tr>";
	});
	return html;
}

// -----------------------------------------------------------------------------------------------------
// 在index.html显示api页面
function showApiPage(name){
	var data = apis[name];
	if(data == null){
		$("#apiDetailDIV").html("<h3 style=\"margin-bottom: 0px;\">暂无页面</h3>");
	}else{
		var html = "";
		html += "<h3>"+data.name+"</h3>";
		html += "<p class='small'>"+data.description+"</p>";
		html += "<div style='margin-top:22px'>";
		html += "	<div class='requestMethod'>"+data.requestMethod+"</div>";
		html += "	<div class='requestUrl'>"+data.requestUrl+"</div>";
		html += "</div>";
		
		var script = {
			html: "<script type='text/javascript'>var hideState = {"
		};
		html += getTableHtml("header", data.header, commonHeader, "请求头参数", script);
		html += getTableHtml("url", data.url, commonUrl, "请求URL参数", script);
		html += getTableHtml("request", data.request, commonRequest, "请求体参数", script);
		html += getTableHtml("response", data.response, commonResponse, "响应体参数", script);
		if(script.html.charAt(script.html.length-1) == ","){
			script.html += "_end_js_douglei_$_:true";
		}
		html += script.html + "}</script>";
		$("#apiDetailDIV").html(html);
	}
}

// 获取表格html
function getTableHtml(type, innerData, commonData, description, script){
	var html = "<table class='table table-hover' id='"+type + "ParamsTable"+"'>";
	if(innerData.struct == "NULL"){
		html += "	<caption>"+description+"</caption>";
		html += "	<tbody><tr><td style='border:0px;background-color:#f5f5f5;'>无</td></tr></tbody></table>";
	}else{
		if(innerData.struct == "COMMON"){
			innerData = commonData;
		}
		var params = innerData.params;
		var egDivId = type + "ParamsEgDiv";

		html += "	<caption>"+description+"<span class='glyphicon glyphicon-info-sign pull-right info' title='查看示例' alt='查看示例' data-toggle='modal' data-target='#"+egDivId+"'></span></caption>";
		html += "	<thead>";
		html += "		<tr>";
		html += "			<th></th>";
		html += "			<th>参数名</th>";
		html += "			<th>类型</th>";
		html += "			<th>长度</th>";
		html += "			<th>精度</th>";
		html += "			<th>是否必须</th>";
		html += "			<th>默认值</th>";
		html += "			<th>描述</th>";
		html += "		</tr>";
		html += "	</thead>";
		html += "	<tbody>";
		html += getParamsHtml(params, 0, type, script, null);
		html += "	</tbody>";
		html += "</table>";
		
		var egContentCopyId = type + "ParamsEgCopy";
		var egDivHtml = "";
		egDivHtml += "<div class='modal fade' id='"+egDivId+"' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>";
		egDivHtml += "	<div class='modal-dialog'>";
		egDivHtml += "		<div class='modal-content'>";
		egDivHtml += "			<div class='modal-header'>";
		egDivHtml += "				<h4 class='modal-title' id='myModalLabel'>"+description+"示例</h4>";
		egDivHtml += "			</div>";
		egDivHtml += "			<div class='modal-body' id='"+egContentCopyId+"'>";
		egDivHtml += "				<pre>";
		egDivHtml += getParamsEgHtml(params, 0, innerData.struct);
		egDivHtml += "				</pre>";
		egDivHtml += "			</div>";
		egDivHtml += "			<div class='modal-footer'>";
		egDivHtml += "				<button type='button' class='btn btn-primary' onClick=\"copy2clipboard('"+egContentCopyId+"')\">复制</button>";
		egDivHtml += "				<button type='button' class='btn btn-default' data-dismiss='modal'>关闭</button>";
		egDivHtml += "			</div>";
		egDivHtml += "		</div>";
		egDivHtml += "	</div>";
		egDivHtml += "</div>";
		html += egDivHtml;
	}
	return html;
}

// 获取参数信息的html
function getParamsHtml(params, levels, id, script, parent){
	var html = "";
	var innerId;
	$.each(params, function(i, param){
		if(param.entity == null){
			html += "<tr";
			if(parent != null){
				html += " name='"+id+"' class='hide_' ";
			}
			html += ">";
			html += "	<td></td>";
		}else{
			innerId = id+"_"+param.name;
			html += "<tr";
			if(parent != null){
				html += " name='"+id+"' class='hide_' ";
			}
			html += " style='cursor: pointer;' onClick='toggle(\""+innerId+"\")'>";
			html += "	<td style='color: #428bca;'><span class='glyphicon glyphicon-chevron-right' id='"+innerId+"'></span></td>";
			script.html += innerId + ":true,";
		}
		html += "	<td>"+htmlLoop(levels, "", "&nbsp;&nbsp;&nbsp;&nbsp;")+param.name+"</td>";
		html += "	<td>"+param.dataType+"</td>";
		html += "	<td>"+param.length+"</td>";
		html += "	<td>"+param.precision+"</td>";
		html += "	<td>"+param.required+"</td>";
		html += "	<td>"+param.defaultValue+"</td>";
		html += "	<td>"+param.description+"</td>";
		html += "</tr>";
		
		if(param.entity != null){
			html += getParamsHtml(param.entity.params, levels+1, innerId, script, param);
		}
	});
	return html;
}

// 获取参数信息的示例的html
function getParamsEgHtml(params, levels, struct){
	var html = (struct == "OBJECT_OR_ARRAY")?"[{|{\n":((struct == "ARRAY")?"[{\n":"{\n");
	$.each(params, function(i, param){
		html += htmlLoop(levels, "\t") + "\""+param.name+"\": ";
		if(param.entity == null){
			html += getEgValue(param);
		}else{
			var val = (param.dataType == "STRING")?"\"":"";
			html += val;
			html += getParamsEgHtml(param.entity.params, levels+1, param.entity.struct);
			html += val;
		}
		if(i < params.length-1){
			html += ",\n";
		}
	});
	html += "\n" + htmlLoop(levels, "", "\t") + ((struct=="OBJECT_OR_ARRAY")?"}|}]":((struct == "ARRAY")?"}]":"}"));
	return html;
}

// 获取参数的示例值
function getEgValue(param){
	if(param.egValue != ""){
		if(param.dataType == "STRING" || param.dataType == "DATE"){
			return "\"" + param.egValue +"\"";
		}
		return param.egValue;	
	}
	switch(param.dataType){
		case "INTEGER":
			return 0;
		case "DOUBLE":
			return 0.1;
		case "STRING":
			return "\"\"";
		case "BOOLEAN":
			return true;
		default:
			return "unknow, dataType is " + param.dataType;
	}
}

// 获取循环生成的html
function htmlLoop(count, defaultValue, value){
	if(value == null){
		value = defaultValue;
		count++;
	}
	if(count == 0){
		return defaultValue;
	}else{
		var html = "";
		for(var i=0;i<count;i++){
			html += value;
		}
		return html;
	}
}

// ------------------------------------------------------------------------------------------
// api详情页面的点击事件
// ------------------------------------------------------------------------------------------

// 隐藏或显示参数树
function toggle(elementName){
	if(isHideState(elementName)){
		$("tr[name="+elementName+"]").show();
		$("#"+elementName).attr("class", "glyphicon glyphicon-chevron-down");
	}else{
		$("tr[name="+elementName+"]").hide();
		$("#"+elementName).attr("class", "glyphicon glyphicon-chevron-right");
		hideSubElements(elementName);
	}
}

// 判断指定元素是否是隐藏状态, 如果是隐藏状态, 返回true, 否则返回false
function isHideState(elementName){
	if(hideState[elementName]){
		hideState[elementName] = false;
		return true;
	}else{
		hideState[elementName] = true;
		return false;
	}
}

// 在隐藏当前元素的时候, 判断是否还有子元素需要隐藏
function hideSubElements(elementName){
	for(var prop in hideState){
		if(elementName != prop && prop.indexOf(elementName) == 0 && !hideState[prop]){
			hideState[prop] = true;
			$("tr[name="+prop+"]").hide();
			$("#"+prop).attr("class", "glyphicon glyphicon-chevron-right");
		}
	}
}

// 复制指定元素的html内容到粘贴板
function copy2clipboard(elementId){
	if(window.clipboardData){
		window.clipboardData.setData("text", document.getElementById(elementId+"PRE").innerHTML);
		alert("复制成功");
	}else if(document.body.createTextRange){
		var range = document.body.createTextRange();
		range.moveToElementText(document.getElementById(elementId));
		range.select();
		document.execCommand("Copy");
		alert("复制成功");
	}else if (window.getSelection) {
		var selection = window.getSelection();
		var range = document.createRange();
		range.selectNodeContents(document.getElementById(elementId));
		selection.removeAllRanges();
		selection.addRange(range);
		document.execCommand("Copy");
		alert("复制成功");
	} else {
		alert("暂不支持该浏览器的复制功能, 请手动复制");
	}
}