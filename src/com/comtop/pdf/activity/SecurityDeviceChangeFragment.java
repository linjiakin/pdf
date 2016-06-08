package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.Utils;

/**
 * 安防监测-装置变更
 * @author linyong
 */
public class SecurityDeviceChangeFragment extends Fragment {

    private ListView deviceChangeListView;
    private Spinner deviceType;
    private EditText startTime;
    private EditText endTime;
    private Button queryBtn;
    private List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
    private SimpleAdapter deviceChangeListAdapter;
    private int pageNo = 1;
    private int pageSize = 20;
    private int no = 1;
    private MyDialogLoading loading;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.security_devicechange, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();//初始化控件
	}

    private void init(){
    	loading = new MyDialogLoading(getActivity());
    	deviceChangeListView = (ListView) getActivity().findViewById(R.id.security_devicechange_listview_id);
        deviceType = (Spinner) getActivity().findViewById(R.id.security_devicechange_devicetype_id);
        startTime = (EditText) getActivity().findViewById(R.id.security_devicechange_otime_start_id);
        endTime = (EditText) getActivity().findViewById(R.id.security_devicechange_otime_end_id);
        queryBtn = (Button) getActivity().findViewById(R.id.security_devicechange_query_id);

        //设备类型
        ArrayAdapter<String> deviceTypeAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getDeviceTypeData());
        deviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceType.setAdapter(deviceTypeAdapter);
        
        startTime.setText(Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));
        endTime.setText(Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));

        //列表
        deviceChangeListAdapter = new SimpleAdapter(getActivity(),getDeviceChangeListData(),R.layout.security_devicechange_listitem,
                new String[]{"no","devicename","status","otime","operson"},
                new int[]{R.id.security_devicechange_no_id,R.id.security_devicechange_devicename_id,
        		R.id.security_devicechange_status_id,R.id.security_devicechange_otime_id,R.id.security_devicechange_operson_id});
        deviceChangeListView.setAdapter(deviceChangeListAdapter);
        deviceChangeListView.setOnScrollListener(scrollListener);

        startTime.setOnClickListener(clickListener);
        endTime.setOnClickListener(clickListener);
        queryBtn.setOnClickListener(clickListener);
    }

    //获取设备列表信息
    private List<Map<String,String>> getDeviceChangeListData(){
    	//根据配电房、设备类型、操作时间
        for(int i = 1;i<=100;i++){
            Map<String,String> map = new HashMap<String, String>();
            map.put("no",i+"");
            map.put("devicename","抽风机");
            map.put("otime","2015-1-1 01:00:00");
            map.put("status","打开");
            map.put("operson","管理员"+i);
            listData.add(map);
        }
        return listData;
    }
    
    OnScrollListener scrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch(scrollState){
			case SCROLL_STATE_FLING://手指已经离开屏幕，但是由于惯性，listview还是滚动时候触发
				break;
			case SCROLL_STATE_TOUCH_SCROLL://手指按着屏幕在滚动listview时候触发
				break;
			case SCROLL_STATE_IDLE://listview不滚动时候触发
				if(view.getLastVisiblePosition() == view.getCount()-1){
					query(pageNo+1,pageSize);
				}
				break;
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	};

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.security_devicechange_otime_start_id:
                	showDateDialog(startTime);
                    break;
                case R.id.security_devicechange_otime_end_id:
                	showDateDialog(endTime);
                    break;
                case R.id.security_devicechange_query_id:
                	listData.clear();
                	no = 1;
                	query(1,20);
                	break;
            }
        }
    };
    
    private void query(int pageNo,int pageSize){
    	loading.show();
    	this.pageNo = pageNo;
    	this.pageSize = pageSize;
    	new Thread(loadDeviceChangeData).start();
    }
    
    Runnable loadDeviceChangeData = new Runnable(){
		@Override
		public void run() {
			try {
	    		//封装请求参数
				JSONObject params = new JSONObject();
				params.put("powerroomId", getHouseFunctionId());
				params.put("deviceType", getDeviceTypeId(deviceType.getSelectedItem().toString()));//1抽风机、2烟雾装置 （待补全）
				params.put("timeStart", startTime.getText());
				params.put("timeEnd", endTime.getText());
				params.put("pageNo", pageNo);
				params.put("pageSize", pageSize);
				//发送请求
				//String result = Utils.newInstance().send(Constants.SECURITY_DEVICECHANGE_URL, params.toString(), Utils.POST);
				//测试数据
				//String result = "{deviceStateInfo:[{\"deviceId\":\"11\",\"deviceName\":\"抽风机\",\"deviceState\":1,\"timeChg\":\"2014-1-1\",\"operator\":\"管理员\",\"warnInfo\":\"测试信息\"}]}";
				/**
				 * {"iResult":1,"errorMsg":null,"lstResult":[{"monTime":"2014-12-30 12:10:020","classifyId":"1002320","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-20","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三020","threshold":null},{"monTime":"2014-12-30 12:10:021","classifyId":"1002321","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-21","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三021","threshold":null},{"monTime":"2014-12-30 12:10:022","classifyId":"1002322","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-22","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三022","threshold":null},{"monTime":"2014-12-30 12:10:023","classifyId":"1002323","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-23","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三023","threshold":null},{"monTime":"2014-12-30 12:10:024","classifyId":"1002324","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-24","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三024","threshold":null},{"monTime":"2014-12-30 12:10:025","classifyId":"1002325","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-25","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三025","threshold":null},{"monTime":"2014-12-30 12:10:026","classifyId":"1002326","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-26","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三026","threshold":null},{"monTime":"2014-12-30 12:10:027","classifyId":"1002327","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-27","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三027","threshold":null},{"monTime":"2014-12-30 12:10:028","classifyId":"1002328","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-28","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三028","threshold":null},{"monTime":"2014-12-30 12:10:029","classifyId":"1002329","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-29","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三029","threshold":null},{"monTime":"2014-12-30 12:10:030","classifyId":"1002330","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-30","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三030","threshold":null},{"monTime":"2014-12-30 12:10:031","classifyId":"1002331","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-31","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三031","threshold":null},{"monTime":"2014-12-30 12:10:032","classifyId":"1002332","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-32","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三032","threshold":null},{"monTime":"2014-12-30 12:10:033","classifyId":"1002333","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-33","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三033","threshold":null},{"monTime":"2014-12-30 12:10:034","classifyId":"1002334","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-34","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三034","threshold":null},{"monTime":"2014-12-30 12:10:035","classifyId":"1002335","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-35","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三035","threshold":null},{"monTime":"2014-12-30 12:10:036","classifyId":"1002336","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-36","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三036","threshold":null},{"monTime":"2014-12-30 12:10:037","classifyId":"1002337","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-37","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三037","threshold":null},{"monTime":"2014-12-30 12:10:038","classifyId":"1002338","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-38","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三038","threshold":null},{"monTime":"2014-12-30 12:10:039","classifyId":"1002339","classifyName":"安防监测","deviceName":"烟感装置（窗户A）-39","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001","areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,"warnVal":null,"warnLevel":null,"operator":"张三039","threshold":null}]}
				 */
				String result = "{\"iResult\":1,\"errorMsg\":null,\"lstResult\":[{\"monTime\":\"2014-12-30 12:10:020\",\"classifyId\":\"1002320\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-20\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三020\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:021\",\"classifyId\":\"1002321\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-21\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三021\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:022\",\"classifyId\":\"1002322\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-22\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三022\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:023\",\"classifyId\":\"1002323\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-23\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三023\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:024\",\"classifyId\":\"1002324\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-24\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三024\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:025\",\"classifyId\":\"1002325\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-25\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三025\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:026\",\"classifyId\":\"1002326\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-26\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三026\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:027\",\"classifyId\":\"1002327\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-27\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三027\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:028\",\"classifyId\":\"1002328\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-28\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三028\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:029\",\"classifyId\":\"1002329\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-29\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三029\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:030\",\"classifyId\":\"1002330\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-30\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三030\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:031\",\"classifyId\":\"1002331\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-31\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三031\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:032\",\"classifyId\":\"1002332\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-32\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三032\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:033\",\"classifyId\":\"1002333\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-33\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三033\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:034\",\"classifyId\":\"1002334\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-34\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三034\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:035\",\"classifyId\":\"1002335\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-35\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三035\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:036\",\"classifyId\":\"1002336\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-36\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三036\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:037\",\"classifyId\":\"1002337\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-37\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三037\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:038\",\"classifyId\":\"1002338\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-38\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三038\",\"threshold\":null},{\"monTime\":\"2014-12-30 12:10:039\",\"classifyId\":\"1002339\",\"classifyName\":\"安防监测\",\"deviceName\":\"烟感装置（窗户A）-39\",\"deviceId\":\"0B5934C54D62776BE050007F01002B11\",\"deviceCode\":\"100230001\",\"areaLevel\":\"中压区\",\"areaLevelName\":null,\"deviceState\":\"在线\",\"monitorSite\":null,\"monVal\":null,\"warnContent\":null,\"warnVal\":null,\"warnLevel\":null,\"operator\":\"张三039\",\"threshold\":null}]}";
				//处理结果
				JSONObject resultObj = new JSONObject(result);
				JSONArray alarmArr = resultObj.getJSONArray("lstResult");
				if(null != alarmArr && alarmArr.length()>0){
					JSONObject tempObj;
					for(int i = 0,j=alarmArr.length();i<j;i++){
						tempObj = alarmArr.getJSONObject(i);
						//{"monTime":"2014-12-30 12:10:020","classifyId":"1002320","classifyName":"安防监测",
						//"deviceName":"烟感装置（窗户A）-20","deviceId":"0B5934C54D62776BE050007F01002B11","deviceCode":"100230001",
						//"areaLevel":"中压区","areaLevelName":null,"deviceState":"在线","monitorSite":null,"monVal":null,"warnContent":null,
						//"warnVal":null,"warnLevel":null,"operator":"张三020","threshold":null}
			            Map<String,String> map = new HashMap<String, String>();
			            map.put("no",no+++"");
			            map.put("devicename",tempObj.getString("deviceName"));
			            map.put("otime",tempObj.getString("monTime"));
			            //map.put("status",tempObj.getInt("deviceState")==1?"打开":"关闭");
			            map.put("status",tempObj.getString("deviceState"));
			            map.put("operson",tempObj.getString("operator"));
			            listData.add(map);
			        }
				}
				//更新listview
		        getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						deviceChangeListAdapter.notifyDataSetChanged();
						loading.hide();
					}
				});
			} catch (Exception e) {
		        getActivity().runOnUiThread(new Runnable(){
		            public void run(){
		            	deviceChangeListAdapter.notifyDataSetChanged();
		                Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
		                loading.hide();
		            }
		        });
				e.printStackTrace();
			}
		}
    };

    private String[] getDeviceTypeData(){
        return new String[]{"抽风机","烟感装置","红外双鉴"};
    }
    
    private int getDeviceTypeId(String deviceType){
    	if(deviceType.equalsIgnoreCase("抽风机")){
    		return 1;
    	}
    	return -1;
    }
    
    private void showDateDialog(final EditText timeText){
        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            	timeText.setText(i+"/"+(i2+1)+"/"+i3);
            }
        },Integer.parseInt(Utils.newInstance().splitStr(timeText.getText().toString(),"/")[0]),
                Integer.parseInt(Utils.newInstance().splitStr(timeText.getText().toString(),"/")[1])-1,
                Integer.parseInt(Utils.newInstance().splitStr(timeText.getText().toString(),"/")[2]));
        dateDialog.show();
    }

    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((SecurityFragment)getFragmentManager().findFragmentByTag("SECURITY")).getHouseFunctionId();
    }
}
