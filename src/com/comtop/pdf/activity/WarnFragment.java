package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.comtop.pdf.utils.SharedPreferencesUtils;
import com.comtop.pdf.utils.Utils;

/**
 * 告警监测
 * @author linyong
 *
 */
public class WarnFragment extends Fragment {

	private static final String LOG = "WarnFragment";
    private ListView warnListView;
    private Spinner warnArea;
    private Spinner warnType;
    private EditText startTime;
    private EditText endTime;
    private Button queryBtn;
    private EditText choiceHouse;
    private List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
    private SimpleAdapter warnListAdapter;
    private String houseFunctionId;//选择配电房的功能位置id
    private String housePath;//选择配电房的全路径
    private int pageNo = 1;
    private int pageSize = 20;
    private int no = 1;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.warn_main, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();//初始化后台数据
		init();//初始化控件
	}
	
	private void initData(){
		//获取配电房,从sdf中取出最近选择的配电房
		String houseInfo = new SharedPreferencesUtils(getActivity(), Constants.SPF_NAME).
				getString(Constants.SELECTED_HOUSEINFO, "");
		if(null == houseInfo || "".equalsIgnoreCase(houseInfo)){
			Log.e(LOG, "没有从sdf中获取到配电房信息");
			return;
		}
		try {
			JSONObject houseObj = new JSONObject(houseInfo);
			housePath = houseObj.getString(Constants.HOUSE_PATH);
			houseFunctionId = houseObj.getString(Constants.HOUSE_FUNCTIONID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    private void init(){
        warnListView = (ListView) getActivity().findViewById(R.id.main_warn_listview_id);
        warnArea = (Spinner) getActivity().findViewById(R.id.main_warn_inarea_spinner_id);
        warnType = (Spinner) getActivity().findViewById(R.id.main_warn_type_id);
        startTime = (EditText) getActivity().findViewById(R.id.main_warn_start_time_id);
        endTime = (EditText) getActivity().findViewById(R.id.main_warn_end_time_id);
        queryBtn = (Button) getActivity().findViewById(R.id.main_warn_query_id);

        ArrayAdapter<String> warnAreaAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getWarnAreaData());
        warnAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warnArea.setAdapter(warnAreaAdapter);

        ArrayAdapter<String> warnTypeAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getWarnTypeData());
        warnTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warnType.setAdapter(warnTypeAdapter);
        
        startTime.setText(Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));
        endTime.setText(Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));

        warnListAdapter = new SimpleAdapter(getActivity(),getwarnListData(),R.layout.warn_listview_item,
                new String[]{"no","inarea","time","type","content"},new int[]{R.id.no_id,R.id.in_area_id,R.id.warn_time_id,
                R.id.warn_type_id,R.id.warn_content_id});
        warnListView.setAdapter(warnListAdapter);
        
        warnListView.setOnScrollListener(scrollListener);

        startTime.setOnClickListener(clickListener);
        endTime.setOnClickListener(clickListener);
        queryBtn.setOnClickListener(clickListener);

        choiceHouse = (EditText) getActivity().findViewById(R.id.warn_choice_pdf_panel_id).findViewById(R.id.select_pdf_id);
        choiceHouse.setText(housePath);
        choiceHouse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//选择配电房
				Intent intent = new Intent(getActivity(),ChoiceHouseDialog.class);
				intent.putExtra("housePath", choiceHouse.getText().toString());
				startActivityForResult(intent, Constants.REQUEST_CHOICE_HOUSE_CODE);
			}
		});
        
    }

    private List<Map<String,String>> getwarnListData(){
        for(int i = 1;i<=100;i++){
            Map<String,String> map = new HashMap<String, String>();
            map.put("no",i+"");
            map.put("inarea","中压区"+i);
            map.put("time","2015-1-1 01:00:00");
            map.put("type","告警类别"+i);
            map.put("content","告警内容告警内容告警内容告警内容告警内容告警内容告警内容告警内容告警内容告警内容告警内容告警"+i);
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
                case R.id.main_warn_start_time_id:
                	showDateDialog(startTime);
                    break;
                case R.id.main_warn_end_time_id:
                	showDateDialog(endTime);
                    break;
                case R.id.main_warn_query_id:
                	listData.clear();
                	no = 1;
                	query(1,20);
                	break;
            }
        }
    };
    
    private void query(int pageNo,int pageSize){
    	this.pageNo = pageNo;
    	this.pageSize = pageSize;
    	new Thread(loadWarnData).start();
    }
    
    Runnable loadWarnData = new Runnable(){
		@Override
		public void run() {
			try {
	    		//封装请求参数
				JSONObject params = new JSONObject();
				params.put("powerroomId", houseFunctionId);
				params.put("warnType", 2);
				params.put("businessType", warnType.getSelectedItemPosition()+1);
				params.put("warnArea", warnArea.getSelectedItem().toString());//?
				params.put("warnStartTime", startTime.getText());//?
				params.put("warnEndTime", endTime.getText());//?
				params.put("pageNo", pageNo);//?
				params.put("pageSize", pageSize);//?
				//发送请求
				//String result = Utils.newInstance().send(Constants.ALERT_WARN_URL, params.toString(), Utils.POST);
				//测试数据
				/*String result = "{warnInfo:[{\"warnId\":\"11\",\"warnArea\":\"重压区\",\"warnDate\":\"2013-3-3\",\"businessType\":1,\"warnMsg\":\"测试内容\"},"
						+ "{\"warnId\":\"11\",\"warnArea\":\"重压区\",\"warnDate\":\"2013-3-3\",\"businessType\":1,\"warnMsg\":\"测试内容\"},"
						+ "{\"warnId\":\"11\",\"warnArea\":\"重压区\",\"warnDate\":\"2013-3-3\",\"businessType\":1,\"warnMsg\":\"测试内容\"},"
						+ "{\"warnId\":\"11\",\"warnArea\":\"重压区\",\"warnDate\":\"2013-3-3\",\"businessType\":1,\"warnMsg\":\"测试内容\"},"
						+ "{\"warnId\":\"11\",\"warnArea\":\"重压区\",\"warnDate\":\"2013-3-3\",\"businessType\":1,\"warnMsg\":\"测试内容\"}]}";*/
				/**
				 * {\"iResult\":1,\"errorMsg\":null,\"lstResult\":[{\"warnId\":\"0DB47312D7A6F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-27 10:36:19\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到50毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D783F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-26 15:16:49\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到34.63\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D791F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-26 15:07:22\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到68毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7E1F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-26 11:48:32\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#14门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D79FF0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-26 10:34:32\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到71毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7C1F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-26 05:59:22\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#13门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D772F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-25 17:45:58\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到36.1\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D79BF0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-25 10:29:27\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到56毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D76EF0A4E050007F010028FD\",\"areaLevel\":\"3\",\"areaLevelName\":\"低压\",\"warnDate\":\"2015-02-25 09:06:51\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到59.74\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A1F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-25 08:00:55\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到18毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D787F0A4E050007F010028FD\",\"areaLevel\":\"3\",\"areaLevelName\":\"低压\",\"warnDate\":\"2015-02-25 06:36:15\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到54.01\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D796F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-24 11:15:16\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到95毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A4F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-24 10:42:19\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到49毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D78BF0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-24 07:24:08\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到57.95\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7D4F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-23 23:09:35\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#12门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A9F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-23 08:45:17\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到95毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A0F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-22 21:31:41\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到16毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D795F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-22 08:49:56\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到69毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7C5F0A4E050007F010028FD\",\"areaLevel\":\"3\",\"areaLevelName\":\"低压\",\"warnDate\":\"2015-02-22 02:52:23\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#4门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D797F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-22 01:28:51\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到73毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null}]}
				 */
				String result = "{\"iResult\":1,\"errorMsg\":null,\"lstResult\":[{\"warnId\":\"0DB47312D7A6F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-27 10:36:19\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到50毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D783F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-26 15:16:49\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到34.63\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D791F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-26 15:07:22\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到68毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7E1F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-26 11:48:32\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#14门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D79FF0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-26 10:34:32\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到71毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7C1F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-26 05:59:22\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#13门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D772F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-25 17:45:58\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到36.1\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D79BF0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-25 10:29:27\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到56毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D76EF0A4E050007F010028FD\",\"areaLevel\":\"3\",\"areaLevelName\":\"低压\",\"warnDate\":\"2015-02-25 09:06:51\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到59.74\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A1F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-25 08:00:55\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到18毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D787F0A4E050007F010028FD\",\"areaLevel\":\"3\",\"areaLevelName\":\"低压\",\"warnDate\":\"2015-02-25 06:36:15\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到54.01\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D796F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-24 11:15:16\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到95毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A4F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-24 10:42:19\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到49毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D78BF0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-24 07:24:08\",\"businessType\":\"1\",\"businessTypeName\":\"环境监测\",\"warnContent\":\"变压器#A过高，温度值达到57.95\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7D4F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-23 23:09:35\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#12门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A9F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-23 08:45:17\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到95毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"1\",\"warnLevelName\":\"I级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7A0F0A4E050007F010028FD\",\"areaLevel\":\"1\",\"areaLevelName\":\"高压\",\"warnDate\":\"2015-02-22 21:31:41\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到16毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D795F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-22 08:49:56\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到69毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D7C5F0A4E050007F010028FD\",\"areaLevel\":\"3\",\"areaLevelName\":\"低压\",\"warnDate\":\"2015-02-22 02:52:23\",\"businessType\":\"3\",\"businessTypeName\":\"安防监测\",\"warnContent\":\"#4门口没有关紧\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"3\",\"warnLevelName\":\"III级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null},{\"warnId\":\"0DB47312D797F0A4E050007F010028FD\",\"areaLevel\":\"2\",\"areaLevelName\":\"中压\",\"warnDate\":\"2015-02-22 01:28:51\",\"businessType\":\"2\",\"businessTypeName\":\"电气监测\",\"warnContent\":\"电表A相电流过大,达到73毫安\",\"powerRoomId\":\"0B5934C54D62776BE050007F01002B95\",\"powerRoomName\":\"景鹏实验室配电房\",\"warnLevel\":\"2\",\"warnLevelName\":\"II级\",\"deviceId\":\"0BFB4B1F73D02067E050007F01000E40\",\"pageNo\":0,\"pageSize\":0,\"timeStart\":null,\"timeEnd\":null}]}";
				//处理结果
				JSONObject resultObj = new JSONObject(result);
				JSONArray alarmArr = resultObj.getJSONArray("lstResult");
				if(null != alarmArr && alarmArr.length()>0){
					JSONObject tempObj;
					for(int i = 0,j=alarmArr.length();i<j;i++){
						tempObj = alarmArr.getJSONObject(i);
						/**
						 * {"warnId":"0DB47312D7A6F0A4E050007F010028FD","areaLevel":"2","areaLevelName":"中压",
						 * "warnDate":"2015-02-27 10:36:19","businessType":"2","businessTypeName":"电气监测",
						 * "warnContent":"电表A相电流过大,达到50毫安","powerRoomId":"0B5934C54D62776BE050007F01002B95",
						 * "powerRoomName":"景鹏实验室配电房","warnLevel":"2","warnLevelName":"II级",
						 * "deviceId":"0BFB4B1F73D02067E050007F01000E40","pageNo":0,"pageSize":0,"timeStart":null,"timeEnd":null}
						 */
			            Map<String,String> map = new HashMap<String, String>();
			            map.put("no",no+++"");
			            map.put("inarea",tempObj.getString("areaLevelName"));
			            map.put("time",tempObj.getString("warnDate"));
			            map.put("type",tempObj.getString("businessTypeName"));
			            map.put("content",tempObj.getString("warnContent"));
			            listData.add(map);
			        }
				}
				//更新listview
		        getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						warnListAdapter.notifyDataSetChanged();
					}
				});
			} catch (Exception e) {
				showMsg("查询失败");
				e.printStackTrace();
			}
		}
    };

    private String[] getWarnAreaData(){
        return new String[]{"中压区","变压区","低压区"};
    }

    private String[] getWarnTypeData(){
        return new String[]{"视频预警","安防预警","电气预警","环境预警","设备状态预警"};
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

    /**
     * 显示信息
     * @param msg
     */
    private void showMsg(final String msg){
        getActivity().runOnUiThread(new Runnable(){
            public void run(){
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Constants.REQUEST_CHOICE_HOUSE_CODE && requestCode == Constants.REQUEST_CHOICE_HOUSE_CODE){
			choiceHouse.setText(data.getExtras().getString(Constants.HOUSE_PATH));
			houseFunctionId = data.getExtras().getString(Constants.HOUSE_FUNCTIONID);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
