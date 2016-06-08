function getParameter(name) {
	var paramStr = location.search;
	if (paramStr.length == 0)
		return null;
	if (paramStr.charAt(0) != '?')
		return null;
	paramStr = unescape(paramStr);
	paramStr = paramStr.substring(1);
	if (paramStr.length == 0)
		return null;
	var params = paramStr.split('&');
	for (var i = 0; i < params.length; i++) {
		var parts = params[i].split('=', 2);
		if (parts[0] == name) {
			if (parts.length < 2 || typeof (parts[1]) == "undefined"
					|| parts[1] == "undefined" || parts[1] == "null")
				return "";
			return parts[1];
		}
	}
	return null;
}

/**
 * 发送ajax请求
 * @param url
 * @param method POST GET
 * @param isAsyn 是否异步
 * @param fnSucc 成功回调函数
 * @param fnFaild 失败回调函数
 */
function send(url,method,isAsyn, fnSucc, fnFaild,params) {
	// 1.创建对象
	var oAjax = null;
	if (window.XMLHttpRequest) {
		oAjax = new XMLHttpRequest();
	} else {
		oAjax = new ActiveXObject("Microsoft.XMLHTTP");
	}

	// 2.连接服务器
	oAjax.open(method, url, isAsyn); // open(方法, url, 是否异步)

	// 3.接收返回
	oAjax.onreadystatechange = function() { // OnReadyStateChange事件
		if (oAjax.readyState == 4) { // 4为完成
			if (oAjax.status == 200) { // 200为成功
				fnSucc(oAjax.responseText)
			} else {
				if (fnFaild) {
					fnFaild();
				}
			}
		}
	};
	oAjax.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	
	// 4.发送请求
	oAjax.send(params);

}