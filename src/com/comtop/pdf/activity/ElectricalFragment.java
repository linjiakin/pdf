package com.comtop.pdf.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.SharedPreferencesUtils;

/**
 * 电气监测
 * @author linyong
 *
 */
public class ElectricalFragment extends Fragment {

	private static final String LOG = "ElectricalFragment";
	private EditText houseText ;
	private Button mapBtn;
	private Button monitorDataBtn;
	private Button loadChartBtn;
    private FragmentManager fm ;
    private boolean isLoadMap;
    private boolean isLoadMonitroData;
    private boolean isLoadChart;
    private String houseFunctionId;//选择配电房的功能位置id
    private String housePath;//选择配电房的全路径

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_electrical, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();//初始化后台数据
		init();//初始化控件
		mapBtn.performClick();
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
		//选择配电房
		houseText = (EditText) getActivity().findViewById(R.id.electrical_choice_pdf_panel_id).findViewById(R.id.select_pdf_id);
		houseText.setText(housePath);
		houseText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//选择配电房
				Intent intent = new Intent(getActivity(),ChoiceHouseDialog.class);
				startActivityForResult(intent, Constants.REQUEST_CHOICE_HOUSE_CODE);
			}
		});

		mapBtn = (Button) getActivity().findViewById(R.id.electrical_map_id);
		monitorDataBtn = (Button) getActivity().findViewById(R.id.electrical_monitordata_id);
		loadChartBtn = (Button) getActivity().findViewById(R.id.electrical_load_chart_id);
		mapBtn.setOnClickListener(clickListener);
		monitorDataBtn.setOnClickListener(clickListener);
		loadChartBtn.setOnClickListener(clickListener);
		
		//获取fragment管理器
		fm = getActivity().getSupportFragmentManager();
	}
	
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setBtnStyle();
			if("".equalsIgnoreCase(houseText.getText().toString())){
				Toast.makeText(getActivity(), "请先选择要查看的配电房", Toast.LENGTH_SHORT).show();return;
			}
			//先隐藏接线图、监测数据、负载趋势面板
			LinearLayout layout =  (LinearLayout) getView().findViewById(R.id.electrical_container_id);
			int count = layout.getChildCount();
			for (int i = 0; i < count; i++) {
				layout.getChildAt(i).setVisibility(View.GONE);
			}
			try {
				FragmentTransaction trans = fm.beginTransaction();
				switch (v.getId()) {
				case R.id.electrical_map_id://接线图
					if(!isLoadMap){
						isLoadMap = true;
						trans.add(R.id.electrical_container_id, new ElectricalMapFragment(),"ELECTRICAL_MAP");
					}else{
						fm.findFragmentByTag("ELECTRICAL_MAP").getView().setVisibility(View.VISIBLE);
					}
					break;
				case R.id.electrical_monitordata_id://监测数据
					if(!isLoadMonitroData){
						isLoadMonitroData = true;
						trans.add(R.id.electrical_container_id, new ElectricalMonitorDataFragment(),"ELECTRICAL_MONITOR_DATA");
					}else{
						fm.findFragmentByTag("ELECTRICAL_MONITOR_DATA").getView().setVisibility(View.VISIBLE);
					}
					break;
				case R.id.electrical_load_chart_id://负载趋势图
					if(!isLoadChart){
						isLoadChart = true;
						trans.add(R.id.electrical_container_id, new ElectricalLoadChartFragment(),"ELECTRICAL_LOAD_CHART");
					}else{
						fm.findFragmentByTag("ELECTRICAL_LOAD_CHART").getView().setVisibility(View.VISIBLE);
					}
					break;
				}
				trans.commit();
			} catch (Exception e) {
				Toast.makeText(getActivity(), "发生错误", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			
			Button btn = (Button) v;
			btn.setTextColor(Color.parseColor("#FFFFFF"));
		}
	};

	//设置按钮样式
	private void setBtnStyle(){
		((Button)getActivity().findViewById(R.id.electrical_map_id)).setTextColor(Color.parseColor("#000000"));
		((Button)getActivity().findViewById(R.id.electrical_monitordata_id)).setTextColor(Color.parseColor("#000000"));
		((Button)getActivity().findViewById(R.id.electrical_load_chart_id)).setTextColor(Color.parseColor("#000000"));
	}

	//获取配电房功能位置
	public String getHouseFunctionId(){
		return this.houseFunctionId;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Constants.REQUEST_CHOICE_HOUSE_CODE && requestCode == Constants.REQUEST_CHOICE_HOUSE_CODE){
			houseText.setText(data.getExtras().getString(Constants.HOUSE_PATH));
			houseFunctionId = data.getExtras().getString(Constants.HOUSE_FUNCTIONID);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
