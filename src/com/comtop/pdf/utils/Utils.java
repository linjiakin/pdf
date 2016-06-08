package com.comtop.pdf.utils;

import android.content.Context;

import com.comtop.pdf.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 帮助工具类
 * @author Administrator
 *
 */
public class Utils {
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	public static final String POST = "POST";
	public static final String GET = "GET";
	private static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟  
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟  

	private JSONObject menuObj;
	private HttpClient httpClient;
	private HttpResponse httpResponse;

	private static final Utils utils = new Utils();

	private Utils() {
		BasicHttpParams httpParams = new BasicHttpParams();  
	    HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);  
	    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);  
	    httpClient = new DefaultHttpClient(httpParams);  
	}

	public static Utils newInstance() {
		return utils;
	}
	
	/**
	 * 发送网络请求
	 * @param url 请求url地址
	 * @param params 如果是post请求，则使用该参数进行参数传递，json格式
	 * @param type GET或POST方式
	 * @return
	 */
	public String send(String url, String params, String type) {
		String result = null;
		if (null != type) {
			try {
				if (type.equalsIgnoreCase(POST)) {
					HttpPost httpPost = new HttpPost(url);
					httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
					StringEntity se = new StringEntity(params);
					se.setContentType(CONTENT_TYPE_TEXT_JSON);
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,APPLICATION_JSON));
					httpPost.setEntity(se);
					httpResponse = httpClient.execute(httpPost);
				} else if (type.equalsIgnoreCase(GET)) {
					HttpGet httpGet = new HttpGet(url);
					httpResponse = httpClient.execute(httpGet);
				}
				result = EntityUtils.toString(httpResponse.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
    /**
     * 加载服务器数据并缓存起来
     * 数据有：
     * 1、配电房台帐结构【深圳供电局/区局/片区/配电房/设备(1摄像头、2变压器、3电缆、4开关柜子)/摄像头URL地址】
     * 2、配电房气象信息
     * 3、配电房的经纬度
     */
    public void loadServerData(Context context) {
    	try {
			loadArchivesInfo(context);
			loadMeteorological(context);
			loadLatlng(context);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
     * 配电房经纬度
	 * @throws JSONException 
     */
    private void loadLatlng(Context context) throws JSONException {
		JSONObject result = new JSONObject();
		JSONObject lchyObj = new JSONObject();
		lchyObj.put("lat", 22.551132);
		lchyObj.put("lng", 113.932859);
		JSONObject bhytObj = new JSONObject();
		bhytObj.put("lat", 22.535194);
		bhytObj.put("lng", 114.002918);
		result.put("莲城花园",new JSONObject(lchyObj.toString()));
		result.put("碧海云天",new JSONObject(bhytObj.toString()));

		//把配电房经纬度存储到spf中
		new SharedPreferencesUtils(context, Constants.SPF_NAME).putString(Constants.LATLNG, 
				result.toString());
	}

	/**
     * 配电房气象信息
	 * @throws JSONException 
     */
    private void loadMeteorological(Context context) throws JSONException {
    	//[{"pdf1":{"minTemperature":10,...}},{"pdf2":{"minTemperature":6,...}}]
    	JSONArray result = getArchiveInfoByNodeLevel(context, 3);
    	JSONArray weaterResult = new JSONArray();
    	if(null != result && result.length() >0 ){
    		JSONObject obj ;
    		JSONObject objResult;
    		String funcId;
    		for(int i =0 ;i<result.length();i++){
				obj = result.getJSONObject(i);
				objResult = new JSONObject();
				funcId = obj.getString("functionId");
				if(null == funcId || "".equalsIgnoreCase(funcId) || "null".equalsIgnoreCase(funcId)){
					continue;
				}
				//objResult.put(funcId, Utils.newInstance().send(MessageFormat.format(Constants.WEATHER_URL,funcId), null,GET));
				//测试数据
				objResult.put(funcId,"{\"iResult\":1,\"errorMsg\":null,\"lstResult\":[],\"maxTemperature\":26.0,\"minTemperature\":20.0,\"avgTemperature\":23.0,\"wdirstr\":\"西南\",\"wlevelstr\":\"30\",\"rhsfc\":null,\"maxWspd\":0.0,\"minWspd\":0.0,\"wspd\":0.0,\"rain24h\":0.0,\"strWeather\":\"晴天\",\"latitude\":null,\"longitude\":null,\"powerRoonId\":null}");
				weaterResult.put(objResult);
			}
    	}
		//把台帐结构treeNodes存储到spf中
		new SharedPreferencesUtils(context, Constants.SPF_NAME).putString(Constants.WEATHER, 
				weaterResult.toString());
	}

	/**
     * 配电房台帐结构【深圳供电局/区局/片区/配电房/设备(1摄像头、2变压器、3电缆、4开关柜子)/摄像头URL地址】
	 * @throws JSONException 
     */
	private void loadArchivesInfo(Context context) throws JSONException {
		List<JSONObject> treeNodes = new ArrayList<JSONObject>();
		//JSONObject objResult = new JSONObject(Utils.newInstance().send(MessageFormat.format(Constants.ARCHIVE_URL, "*",0), null,GET));
		//下面是测试数据
		JSONObject objResult = new JSONObject("{\"iResult\":1,\"lstResult\":[{\"fatherFunctionId\":null,\"parent\":true,\"isParent\":true,\"nodeTitle\":\"深圳供电局有限公司\",\"nodeId\":\"SZ030001\",\"functionId\":null,\"nodeLevel\":0},{\"fatherFunctionId\":\"SZ030001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"宝安供电局\",\"nodeId\":\"BA034001\",\"functionId\":\"BA034001\",\"nodeLevel\":1},{\"fatherFunctionId\":\"SZ030001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"福田供电局\",\"nodeId\":\"FT031001\",\"functionId\":\"FT031001\",\"nodeLevel\":1},{\"fatherFunctionId\":\"SZ030001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"龙岗供电局\",\"nodeId\":\"LG035001\",\"functionId\":\"LG035001\",\"nodeLevel\":1},{\"fatherFunctionId\":\"SZ030001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"罗湖供电局\",\"nodeId\":\"LH033001\",\"functionId\":\"LH033001\",\"nodeLevel\":1},{\"fatherFunctionId\":\"SZ030001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"南山供电局\",\"nodeId\":\"NS032001\",\"functionId\":\"NS032001\",\"nodeLevel\":1},{\"fatherFunctionId\":\"SZ030001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"坪山供电局\",\"nodeId\":\"PS036001\",\"functionId\":\"PS036001\",\"nodeLevel\":1},{\"fatherFunctionId\":\"FT031001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"景田片区\",\"nodeId\":\"0B594AA07E300F53E050007F01002C06\",\"functionId\":\"031001001\",\"nodeLevel\":2},{\"fatherFunctionId\":\"FT031001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"香蜜湖片区\",\"nodeId\":\"0B75EE99602094CCE050007F01005592\",\"functionId\":\"031001002\",\"nodeLevel\":2},{\"fatherFunctionId\":\"FT031001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"莲花片区\",\"nodeId\":\"0B75EE99602194CCE050007F01005593\",\"functionId\":\"031001003\",\"nodeLevel\":2},{\"fatherFunctionId\":\"NS032001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"大冲片区\",\"nodeId\":\"0B75EE99602194CCE050007F01005594\",\"functionId\":\"032001001\",\"nodeLevel\":2},{\"fatherFunctionId\":\"LH033001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"老街片区\",\"nodeId\":\"0B75EE99602194CCE050007F01005595\",\"functionId\":\"033001001\",\"nodeLevel\":2},{\"fatherFunctionId\":\"BA034001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"龙华片区\",\"nodeId\":\"0B75EE99602194CCE050007F01005596\",\"functionId\":\"034001001\",\"nodeLevel\":2},{\"fatherFunctionId\":\"LG035001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"龙岗片区\",\"nodeId\":\"0B75EE99602194CCE050007F01005597\",\"functionId\":\"035001001\",\"nodeLevel\":2},{\"fatherFunctionId\":\"PS036001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"坪山片区\",\"nodeId\":\"0B75EE99602194CCE050007F01005598\",\"functionId\":\"036001001\",\"nodeLevel\":2},{\"fatherFunctionId\":\"033001001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":null,\"nodeId\":null,\"functionId\":null,\"nodeLevel\":3},{\"fatherFunctionId\":\"031001001\",\"parent\":true,\"isParent\":true,\"nodeTitle\":\"景鹏实验室配电房\",\"nodeId\":\"0B5934C54D62776BE050007F01002B95\",\"functionId\":\"0x0001\",\"nodeLevel\":3},{\"fatherFunctionId\":null,\"parent\":false,\"isParent\":false,\"nodeTitle\":null,\"nodeId\":null,\"functionId\":null,\"nodeLevel\":4},{\"fatherFunctionId\":\"0x0001\",\"parent\":false,\"isParent\":false,\"nodeTitle\":\"摄像头#2\",\"nodeId\":\"0B5A0E6D32B7AFF4E050007F01002D10\",\"functionId\":\"0B5A0E6D32B7AFF4E050007F01002D10\",\"nodeLevel\":4},{\"fatherFunctionId\":\"0x0001\",\"parent\":false,\"isParent\":false,\"nodeTitle\":\"摄像头#3\",\"nodeId\":\"0B5A0E6D32B7AFF4E050007F01002D11\",\"functionId\":\"0B5A0E6D32B7AFF4E050007F01002D11\",\"nodeLevel\":4},{\"fatherFunctionId\":\"0x0001\",\"parent\":false,\"isParent\":false,\"nodeTitle\":\"摄像头#1\",\"nodeId\":\"236a3529-3fd8-45d5-8e8e-6bdde69111ca\",\"functionId\":\"236a3529-3fd8-45d5-8e8e-6bdde69111ca\",\"nodeLevel\":4}],\"errorMsg\":null}");
		if(objResult != null){
			JSONArray arr = objResult.getJSONArray("lstResult");
			if(null != arr && arr.length() >0){
				JSONObject nodeObj;
				int nodeLevel = -1;
				for(int i =0;i<arr.length();i++){
					nodeObj = arr.getJSONObject(i);
					nodeLevel = nodeObj.getInt("nodeLevel");
					if(nodeLevel == 3 || nodeLevel == 4){//配电房或摄像头
						switch(nodeLevel){
						case 3:
							//拼接档案信息：配电房下的变压器、线缆、开关柜子
							treeNodes.addAll(getDevices(nodeObj));
							break;
						case 4:
							//摄像头
							nodeObj.put("deviceType", 1);//增加设备类型
							//需要将摄像头下的RTSP地址也拼接进来。层级定为5，设备类型定为1
							//treeNodes.add(getVideoRtsp(nodeObj));
							break;
						}
					}
					treeNodes.add(nodeObj);
				}
			}
		}
		//把台帐结构treeNodes存储到spf中
		new SharedPreferencesUtils(context, Constants.SPF_NAME).putString(Constants.ARCHIVE_INFO, 
				treeNodes.toString());
	}
	
	/**
	 * 获取摄像头URL信息
	 * @param pNode
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getVideoRtsp(JSONObject pNode) throws JSONException{
		JSONObject result = new JSONObject();
		String funcId = pNode.getString("functionId");
		if(null == funcId || "".equalsIgnoreCase(funcId)|| "null".equalsIgnoreCase(funcId)){
			return result;
		}
		//JSONObject obj = new JSONObject(Utils.newInstance().send(MessageFormat.format(Constants.VIDEO_PLAY_URL,funcId), null, GET));
		//测试数据
		//JSONObject obj = new JSONObject("{\"iResult\":1,\"errorMsg\":null,\"lstResult\":[],\"cameraId\":null,\"iState\":null,\"oType\":null,\"cType\":null,\"step\":null,\"cVal\":null,\"playUrl\":\"rtsp://211.139.194.251:554/live/2/13E6330A31193128/5iLd2iNl5nQ2s8r8.sdp\"}");
		JSONObject obj = new JSONObject("{\"iResult\":1,\"errorMsg\":null,\"lstResult\":[],\"cameraId\":null,\"iState\":null,"
				+ "\"oType\":null,\"cType\":null,\"step\":null,\"cVal\":null,"
				+ "\"playUrl\":\"rtsp://10.0.80.214:8554/test.hunter\"}");
		/*if(null != obj){
			result.put("nodeId", obj.getString("playUrl"));
			result.put("nodeTitle", obj.getString("iState"));
			result.put("nodeLevel", pNode.getInt("nodeLevel")+1);
			result.put("functionId", obj.getString("playUrl"));
			result.put("fatherFunctionId", funcId);
		}*/
		return obj;
	}
	
	/**
	 * 获取配电房下的变压器、线缆、开关柜子
	 * @return
	 * @throws JSONException 
	 */
	public List<JSONObject> getDevices(JSONObject pNode) throws JSONException{
		List<JSONObject> resultArr = new ArrayList<JSONObject>();
		String nodeId = pNode.getString("nodeId");
		if(null == nodeId || "".equalsIgnoreCase(nodeId) || "null".equalsIgnoreCase(nodeId)){
			return resultArr;
		}
		//JSONObject resultObj = new JSONObject(Utils.newInstance().send(MessageFormat.format(Constants.DEVICE_URL,pNode.get("nodeId")), null,GET));
		//测试数据
		JSONObject resultObj = new JSONObject("{\"iResult\":1,\"errorMsg\":null,\"lstResult\":[{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10001\",\"deviceCode\":\"BYQ001\",\"deviceName\":\"变压器#1\",\"classifyId\":\"10001\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10002\",\"deviceCode\":\"BYQ002\",\"deviceName\":\"变压器#2\",\"classifyId\":\"10001\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10003\",\"deviceCode\":\"BYQ003\",\"deviceName\":\"变压器#3\",\"classifyId\":\"10001\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10004\",\"deviceCode\":\"BYQ004\",\"deviceName\":\"变压器#4\",\"classifyId\":\"10001\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10005\",\"deviceCode\":\"DLT001\",\"deviceName\":\"电缆接头#1\",\"classifyId\":\"10002\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10010\",\"deviceCode\":\"KGG001\",\"deviceName\":\"电缆接头#1\",\"classifyId\":\"10003\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10006\",\"deviceCode\":\"DLT002\",\"deviceName\":\"电缆接头#2\",\"classifyId\":\"10002\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10011\",\"deviceCode\":\"KGG002\",\"deviceName\":\"电缆接头#2\",\"classifyId\":\"10003\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10012\",\"deviceCode\":\"KGG003\",\"deviceName\":\"电缆接头#3\",\"classifyId\":\"10003\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10007\",\"deviceCode\":\"DLT003\",\"deviceName\":\"电缆接头#3\",\"classifyId\":\"10002\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10008\",\"deviceCode\":\"DLT004\",\"deviceName\":\"电缆接头#4\",\"classifyId\":\"10002\",\"classifyName\":null},{\"departmentId\":null,\"departmentName\":null,\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":null,\"powerRoomCode\":null,\"deviceId\":\"10009\",\"deviceCode\":\"DLT005\",\"deviceName\":\"电缆接头#5\",\"classifyId\":\"10002\",\"classifyName\":null}]}");
		if(null != resultObj){
			JSONArray arr = resultObj.getJSONArray("lstResult");
			JSONArray byqArr = new JSONArray();
			JSONArray dltArr = new JSONArray();
			JSONArray kggArr = new JSONArray();
			if(null != arr && arr.length()>0){
				JSONObject obj ;
				for (int i = 0; i < arr.length(); i++) {
					obj = arr.getJSONObject(i);
					String classifyId = obj.getString("classifyId");
					if(classifyId.equalsIgnoreCase("10001")){//变压器
						byqArr.put(obj);
					}else if(classifyId.equalsIgnoreCase("10002")){//电缆
						dltArr.put(obj);
					}else if(classifyId.equalsIgnoreCase("10003")){//开关柜
						kggArr.put(obj);
					}
				}
			}
			resultArr.addAll(wrapDeviceInfo(byqArr, pNode, 2));//变压器
			resultArr.addAll(wrapDeviceInfo(dltArr, pNode, 3));//电缆
			resultArr.addAll(wrapDeviceInfo(kggArr, pNode, 4));//开关柜子
			/*resultArr.addAll(wrapDeviceInfo(resultObj.getJSONArray("transformerPulldown"), pNode, 2));//变压器
			resultArr.addAll(wrapDeviceInfo(resultObj.getJSONArray("cablePulldown"), pNode, 3));//电缆
			resultArr.addAll(wrapDeviceInfo(resultObj.getJSONArray("switchPulldown"), pNode, 4));//开关柜子
			*/
		}
		return resultArr;
	}
	
	/**
	 * 封装转换设备对象
	 * @param arr
	 * @param parentNode
	 * @param deviceType
	 * @return
	 * @throws JSONException
	 */
	private List<JSONObject> wrapDeviceInfo(JSONArray arr,JSONObject parentNode,int deviceType) throws JSONException{
		List<JSONObject> resultArr = new ArrayList<JSONObject>();
		if(null != arr && arr.length() >0){
			JSONObject obj = null;
			for(int i =0;i<arr.length();i++){
				obj = arr.getJSONObject(i);
				/**{"deviceName":"变压器#1","powerRoomId":"0B5934C54D62776BE050007F01002B95","deviceCode":"BYQ001","classifyId":"10001",
				"departmentId":null,"powerRoomName":null,"departmentName":null,"deviceId":"10001","powerRoomCode":null,
				"classifyName":null}**/
				obj.put("nodeId", obj.getString("deviceId"));
				obj.put("nodeTitle", obj.getString("deviceName"));
				obj.put("nodeLevel", obj.getString("classifyId"));
				obj.put("functionId", obj.getString("deviceCode"));
				obj.put("fatherFunctionId", parentNode.getString("functionId"));
				obj.put("deviceType", deviceType);
				resultArr.add(obj);
			}
		}
		return resultArr;
	}
	
	/**
	 * 根据父功能位置获取档案信息
	 * @param context
	 * @param fatherFunctionId
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getArchiveInfoByFatherFunctionId(Context context,String fatherFunctionId) throws JSONException{
		JSONArray arrResult = new JSONArray();
		String result = new SharedPreferencesUtils(context, Constants.SPF_NAME).getString(Constants.ARCHIVE_INFO, "");
		JSONArray arr = new JSONArray(result);
		if(null != arr && arr.length() > 0){
			JSONObject obj;
			for(int i =0;i<arr.length();i++){
				obj = arr.getJSONObject(i);
				if(fatherFunctionId.equalsIgnoreCase(obj.get("fatherFunctionId").toString())){
					arrResult.put(obj);
				}
			}
		}
		return arrResult;
	}
	
	/**
	 * 根据父功能位子id和设备类型获取档案信息
	 * @param context
	 * @param fatherFunctionId
	 * @param deviceType 1摄像头 2变压器 3电缆 4开关柜
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getArchiveInfoByFatherFunctionId(Context context,String fatherFunctionId,int deviceType) throws JSONException{
		JSONArray arrResult = new JSONArray();
		String result = new SharedPreferencesUtils(context, Constants.SPF_NAME).getString(Constants.ARCHIVE_INFO, "");
		JSONArray arr = new JSONArray(result);
		if(null != arr && arr.length() > 0){
			JSONObject obj;
			for(int i =0;i<arr.length();i++){
				obj = arr.getJSONObject(i);
				if(fatherFunctionId.equalsIgnoreCase(obj.get("fatherFunctionId").toString()) 
						&& deviceType == obj.getInt("deviceType")){
					arrResult.put(obj);
				}
			}
		}
		return arrResult;
	}
	
	/**
	 * 根据层级获取档案信息
	 * @param context
	 * @param nodeLevel
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getArchiveInfoByNodeLevel(Context context,int nodeLevel) throws JSONException{
		JSONArray arrResult = new JSONArray();
		String result = new SharedPreferencesUtils(context, Constants.SPF_NAME).getString(Constants.ARCHIVE_INFO, "");
		JSONArray arr = new JSONArray(result);
		if(null != arr && arr.length() > 0){
			JSONObject obj;
			for(int i =0;i<arr.length();i++){
				obj = arr.getJSONObject(i);
				if(nodeLevel == obj.getInt("nodeLevel")){
					arrResult.put(obj);
				}
			}
		}
		return arrResult;
	}
	
	/**
	 * 初始化菜单下标索引位置
	 * @param context
	 */
	public void initMenuPosition(Context context){
		try {
			menuObj = new JSONObject();
			menuObj.put(context.getString(R.string.operate_1), 1);
			menuObj.put(context.getString(R.string.operate_2), 2);
			menuObj.put(context.getString(R.string.operate_3), 3);
			menuObj.put(context.getString(R.string.operate_4), 4);
			menuObj.put(context.getString(R.string.operate_5), 5);
			menuObj.put(context.getString(R.string.operate_6), 6);
			menuObj.put(context.getString(R.string.operate_7), 7);
			menuObj.put(context.getString(R.string.operate_8), 8);
			menuObj.put(context.getString(R.string.operate_9), 9);
			menuObj.put(context.getString(R.string.operate_home), 10);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据菜单名称获取对应索引下标
	 * @param menuName
	 * @return
	 */
	public int getMenuPosition(String menuName){
		if(null == menuName || "".equalsIgnoreCase(menuName) || null == menuObj){
			return -1;
		}
		try {
			return menuObj.getInt(menuName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public String getDateString(String pattern){
		return new SimpleDateFormat(pattern).format(new Date());
	}
	
    public String[] splitStr(String str,String split){
        return str.split(split);
    }

}
