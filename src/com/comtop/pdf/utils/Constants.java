package com.comtop.pdf.utils;

/**
 * 常量类
 * @author Administrator
 *
 */
public class Constants {
	public static final String SERVER_IP = "10.10.61.5";
	public static final String SERVER_PORT = "7007";
	//登录url地址
	public static final String LOGIN_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/login/userLogin/{0}/{1}.ac";
	//配电房台帐结构
	public static final String ARCHIVE_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/menutree/videoTree/{0}/{1}.ac";
	//配电房气象地址
	public static final String WEATHER_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/envmonitor/powerRoomWeather/{0}.ac";
	//配电房变压器、电缆、开关柜子地址
	public static final String DEVICE_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/devicestatemonitor/devStatePulldownInfo/{0}/*.ac";
	//视频播放URL地址
	public static final String VIDEO_PLAY_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/videomonitor/videoPlayUrl/{0}.ac";
	//视频云台控制URL地址
	public static final String VIDEO_CONTROL_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/videomonitor/videoControl.ac";
	//告警与预警url地址
	public static final String ALERT_WARN_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/warninfo/powerRoomWarnInfo.ac";
	//电气监测接线图
	public static final String ELECTRICAL_MAP_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/svg/wiringdiagram/wiringdiagram.jsp";
	//安防监测装置变更URL地址
	public static final String SECURITY_DEVICECHANGE_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/securitymonitor/secDeviceState.ac";
	
	//设备状态监测 设备温度曲线
	public static final String DEVICE_STATUS_MONITOR_TEMPERATURE_CHART_URL = "file:///android_asset/html/DeviceStateTemperature.html";
	//电气监测负载趋势图
	public static final String ELECTRICAL_LOADCHART_URL = "file:///android_asset/html/ElectricalLoadChart.html";
	//安防监测温度趋势图
	public static final String SECURITY_TEMPERATURE_CHART_URL = "file:///android_asset/html/SecurityAreaTemperature.html";
	//环境监测趋势图
	public static final String ENVIRONMENTAL_DATACHART_URL="file:///android_asset/html/EnvironmentalDataChart.html";
	

	//安防监测-中压平面图
	public static final String SECURITY_MAP_ZY_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/svg/ichnography/ichnography.jsp";
	//安防监测-变压平面图
	public static final String SECURITY_MAP_BY_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/svg/ichnography/ichnography.jsp";
	//安防监测-低压平面图
	public static final String SECURITY_MAP_DY_URL = "http://"+SERVER_IP+":"+SERVER_PORT+"/web/mobile/svg/ichnography/ichnography.jsp";
	
	//SharedPreferences文件名称
	public static final String SPF_NAME = "PDF";
	//保存在spf中的用户名key值
	public static final String LOGIN_USERNAME = "login_username";
	//保存在spf中的菜单索引key值
	public static final String MENU_POSITION = "menu_position";
	//保存在spf中的台帐结构key值
	public static final String ARCHIVE_INFO = "archive_info";
	//保存在spf中的气象信息key值
	public static final String WEATHER = "weather";
	//保存在spf中的配电房经纬度key值
	public static final String LATLNG = "latlng";
	//保存在spf中的选择配电房相关信息key值
	public static final String SELECTED_HOUSEINFO = "selectedHouseInfo";
	//配电房全路径key值
	public static final String HOUSE_PATH = "housePath";
	//配电房功能位置key值
	public static final String HOUSE_FUNCTIONID = "houseFunctionId";
	//配电房所有层级信息key值
	public static final String HOUSE_ALLLEVELOBJ = "allLevelObj";

	//保存在spf中的选择配电房相关信息key值
	public static final String SELECTED_VIDEOINFO = "selectedVideoInfo";
	//配电房全路径key值
	public static final String VIDEO_PATH = "videoPath";
	//配电房功能位置key值
	public static final String VIDEO_FUNCTIONID = "videoFunctionId";
	//配电房所有层级信息key值
	public static final String VIDEO_ALLLEVELOBJ = "allLevelObj";
	
	//到选择视频activity代码值
	public static final int REQUEST_CHOICE_VIDEO_CODE = 10001;
	//到选择配电房activity代码值
	public static final int REQUEST_CHOICE_HOUSE_CODE = 10002;
	
	public static final String DATE_PATTERN_YYYYMMDD="yyyy/MM/dd";
	public static final String DATE_PATTERN_YYYYMMDD24HH="yyyy-MM-dd HH:mm:ss";
	
}
