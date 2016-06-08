package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.SharedPreferencesUtils;

/**
 * 地图
 * @author linyong
 *
 */
public class MyMapActivity extends Activity {

	//配电房下拉框
	private Spinner spinnerPdf;
	private List<String> pdfList;
	private RadioGroup roadPlan;

	private int pdfIdx;
	private int roadPlanTypeIdx;

	private MapView mapView;
	private BaiduMap map;
	
	//卫星展示
	private boolean isSatellite;
	// 是否开启实时交通标志
	private boolean isTraffic;
	// 线路规划
	private RoutePlanSearch routePlanSearch;
	// 定位
	private LocationClient locationClient;
	private BDLocationListener myListener = new MyLocationListener();
	// 首次定位
	private boolean isFirstLoc = true;

	// 纬度
	private double latitude;
	// 经度
	private double longitude;

	//private boolean mIsEngineInitSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		initPdf();
		initRoadPlan();
		initDefaultValue();
		initMap();
		initActionBar();
	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		//设置左上角图标可以点击 
		actionBar.setHomeButtonEnabled(true);
		//设置左上角图标左边有返回按钮
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void initMap() {
		initBasicMap();
		initRoadPlanSearch();
		initLocation();
		initNavi();
	}

	private void initBasicMap() {
		mapView = (MapView) findViewById(R.id.myMapView);
		map = mapView.getMap();
	}

	private void initRoadPlanSearch() {
		// 获取线路规划实体
		routePlanSearch = RoutePlanSearch.newInstance();
		// 设置线路规划监听器
		routePlanSearch
				.setOnGetRoutePlanResultListener(getRoutePlanResultListener);
	}

	private void initLocation() {
		// 开启定位图层
		map.setMyLocationEnabled(true);
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(myListener);

		// 配置定位参数
		LocationClientOption option = new LocationClientOption();
		/**
		 * 高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
		 * 低功耗定位模式：这种定位模式下，不会使用GPS，只会使用网络定位（Wi-Fi和基站定位）
		 * 仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位
		 */
		option.setLocationMode(LocationMode.Hight_Accuracy);// Hight_Accuracy高精度、Battery_Saving低功耗、Device_Sensors仅设备(GPS)
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02,不然会出现定位不准
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		locationClient.setLocOption(option);
		locationClient.start();// 启动定位
	}

	//初始化导航引擎  
	private void initNavi(){
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
            mNaviEngineInitListener, new LBSAuthManagerListener() {
                @Override
                public void onAuthResult(int status, String msg) {
                    /*String str = null;
                    if (0 == status) {
                        str = "key校验成功!";
                    } else {
                        str = "key校验失败, " + msg;
                    }
                    Toast.makeText(MyMapActivity.this, str,Toast.LENGTH_LONG).show();*/
                }
            });
	}

	private void initDefaultValue() {
		//默认进行驾车路线规划
		((RadioButton) findViewById(R.id.roadplan_jc_id)).setChecked(true);
	}

	private void initRoadPlan() {
		roadPlan = (RadioGroup) findViewById(R.id.roadplan_group_id);
		roadPlan.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				roadPlanTypeIdx = checkedId;
			}
		});
	}

	/**
	 * 初始化配电房下拉框
	 */
	private void initPdf() {
		spinnerPdf = (Spinner) findViewById(R.id.spinner_pdf_id);
		pdfList = new ArrayList<String>();
		pdfList.add("莲城花园");
		pdfList.add("碧海云天");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, pdfList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPdf.setAdapter(adapter);
		spinnerPdf.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				pdfIdx = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	/**
	 * 执行线路规划
	 * 
	 * @param view
	 */
	public void roadPlan(View view) {
		String msg = "";
		if (pdfIdx == -1) {
			msg = "请选择配电房";
		} else if (roadPlanTypeIdx == 0) {
			msg = "请选择规划方式";
		}
		if ("".equals(msg)) {
			execRoadPlan(this.spinnerPdf.getAdapter().getItem(pdfIdx).toString(),
					((RadioButton) findViewById(this.roadPlanTypeIdx)).getText().toString());
		} else {
			Toast.makeText(MyMapActivity.this, msg, Toast.LENGTH_SHORT).show();
		}

	}
	
	/**
	 * 执行线路规划
	 * @param pdfName 目标地点名称
	 * @param roadPlanType 线路规划方式
	 */
	private void execRoadPlan(String pdfName,String roadPlanType){
		map.clear();//先把地图上的路线规划清除
		PlanNode startNode = PlanNode.withLocation(new LatLng(this.latitude,this.longitude));//当前位置
		PlanNode endNode = PlanNode.withLocation(getTargetPdfLatLng(pdfName));//选择的配电房经纬度
		if(roadPlanType.equalsIgnoreCase(getString(R.string.roadplan_jc))){//驾车
			routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
		}else if(roadPlanType.equalsIgnoreCase(getString(R.string.roadplan_gj))){//公交，注意公交路线规划这里需要给出城市名称
			routePlanSearch.transitSearch(new TransitRoutePlanOption().from(startNode).city("深圳").to(endNode));
		}else if(roadPlanType.equalsIgnoreCase(getString(R.string.roadplan_bx))){//步行
			routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(startNode).to(endNode));
		}
	}
	
	/**
	 * 根据配电房名称获取经纬度
	 * @param pdfName
	 * @return
	 */
	private LatLng getTargetPdfLatLng(String pdfName){
		try {
			JSONObject obj = new JSONObject(new SharedPreferencesUtils(this, Constants.SPF_NAME).getString(Constants.LATLNG, ""));
			JSONObject target = obj.getJSONObject(pdfName);
			return new LatLng(target.getDouble("lat"),target.getDouble("lng")); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	/**
	 * 获取sd卡路径
	 * @return
	 */
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	/**
	 * 地理位置定位监听器类
	 */
	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(com.baidu.location.BDLocation location) {
			if (location == null)
				return;
			/*
			 * StringBuffer sb = new StringBuffer(256); sb.append("time : ");
			 * sb.append(location.getTime()); sb.append("\nerror code : ");
			 * sb.append(location.getLocType()); sb.append("\nlatitude : ");
			 * sb.append(location.getLatitude()); sb.append("\nlontitude : ");
			 * sb.append(location.getLongitude()); sb.append("\nradius : ");
			 * sb.append(location.getRadius()); if (location.getLocType() ==
			 * BDLocation.TypeGpsLocation){ sb.append("\nspeed : ");
			 * sb.append(location.getSpeed()); sb.append("\nsatellite : ");
			 * sb.append(location.getSatelliteNumber()); } else if
			 * (location.getLocType() == BDLocation.TypeNetWorkLocation){
			 * sb.append("\naddr : "); sb.append(location.getAddrStr()); }
			 * Toast.makeText(MainActivity.this, sb.toString(),
			 * Toast.LENGTH_SHORT).show();
			 */
			// 构建定位数据
			MyLocationData data = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).direction(100).build();
			// 设置定位数据
			map.setMyLocationData(data);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				// 设置地图新中心点
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				// 以动画方式更新地图状态，动画耗时 300 ms
				map.animateMapStatus(u);
			}

			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}

	}

	/**
	 * 导航引擎监听器
	 */
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			// 导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航
			//mIsEngineInitSuccess = true;
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
		}
	};

	/**
	 * 路径规划监听器
	 */
	OnGetRoutePlanResultListener getRoutePlanResultListener = new OnGetRoutePlanResultListener() {

		// 步行
		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MyMapActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				WalkingRouteOverlay overlay = new WalkingRouteOverlay(map);
				map.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}

		// 公交
		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MyMapActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				// 这里可以重写TransitRouteOverlay，自定义起止图标
				TransitRouteOverlay overlay = new TransitRouteOverlay(map);
				map.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}

		// 驾车
		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MyMapActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				// 这里可以重写TransitRouteOverlay，自定义起止图标
				DrivingRouteOverlay overlay = new DrivingRouteOverlay(map);
				map.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}
	};

	/**
	 * 进行导航
	 * @param isGPSNav
	 * @param startLatitude
	 * @param startLongitude
	 * @param startName
	 * @param endLatitude
	 * @param endLongitude
	 * @param endName
	 */
	private void goNavi(boolean isGPSNav,double startLatitude,double startLongitude,String startName,
			double endLatitude,double endLongitude,String endName) {

		/**
		 * activity - 启动导航所在的Activity
			startLatitude - 起始点纬度
			startLongitude - 起始点经度
			startName - 起点名称
			endLatitude - 起始点纬度
			endLongitude - 起始点经度
			endName - 终点名称
			nRPPolicy - 路线规划方式：
			isGPSNav - 真实/模拟导航: true-真实; false-模拟
			strategy - 策略参数：STRATEGY_FORCE_ONLINE_PRIORITY, STRATEGY_USER_SETTING
		 */
		
	    BNaviPoint startPoint = new BNaviPoint(startLongitude, startLatitude,startName, BNaviPoint.CoordinateType.BD09_MC);
		BNaviPoint endPoint = new BNaviPoint(endLongitude,endLatitude, endName,BNaviPoint.CoordinateType.BD09_MC);
		BaiduNaviManager.getInstance().launchNavigator(   
				this,
				startPoint,
				endPoint,
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,
				isGPSNav,
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY,
				new OnStartNavigationListener() {                //跳转监听  
	            @Override  
	            public void onJumpToNavigator(Bundle configParams) {  
	                Intent intent = new Intent(MyMapActivity.this, BNavigatorActivity.class);  
	                intent.putExtras(configParams);  
	                startActivity(intent);  
	            }  
	
	            @Override  
	            public void onJumpToDownloader() {  
	            }  
	        });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		routePlanSearch.destroy();
		mapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myUid());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//获取所选择配电房的经纬度
		LatLng latlng = getTargetPdfLatLng(spinnerPdf.getSelectedItem().toString());
		switch (item.getItemId()) {
		case R.id.map_satellite_id://地图模式切换
			if(isSatellite){
				item.setTitle(R.string.map_satellite_label);
				map.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通
			}else{
				item.setTitle(R.string.map_norm_label);
				map.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//卫星
			}
			isSatellite = !isSatellite;
			break;
		case R.id.map_traffic_id://显示路况
			if(isTraffic){
				map.setTrafficEnabled(false);
			}else{
				map.setTrafficEnabled(true);			
			}
			isTraffic = !isTraffic;
			break;
		case R.id.map_virtual_navi_id://模拟导航
			goNavi(false, latitude, longitude, "当前位置", latlng.latitude, latlng.longitude, spinnerPdf.getSelectedItem().toString());
			/*if(mIsEngineInitSuccess){
			}else{
				showMsg("导航初始化尚未完成,无法发起导航");
			}*/
			break;
		case R.id.map_real_navi_id://真实导航
			goNavi(true, latitude, longitude, "当前位置", latlng.latitude, latlng.longitude, spinnerPdf.getSelectedItem().toString());
			/*if(mIsEngineInitSuccess){
			}else{
				showMsg("导航初始化尚未完成,无法发起导航");
			}*/
			break;
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}
}
