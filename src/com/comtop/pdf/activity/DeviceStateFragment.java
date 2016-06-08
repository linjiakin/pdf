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
 * 设备状态监测
 * @author linyong
 *
 */
public class DeviceStateFragment extends Fragment {

	private static final String LOG = "DeviceStateFragment";
	private EditText choicePdf ;
	private Button transformerId;
	private Button cableId;
	private Button cabinetId;
	private Button temperatureId;
    private FragmentManager fm ;
    //是否加载变压器面板
    private boolean isLoadTransformer;
    //是否加载电缆面板
    private boolean isLoadCable;
    //是否加载开关柜面板
    private boolean isLoadCinet;
    //是否加载设备温度曲线面板
    private boolean isLoadDeviceTemperature;
    private String houseFunctionId;//选择配电房的功能位置id
    private String housePath;//选择配电房的全路径

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.device_status_monitor_main, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();//初始化后台数据
		init();//初始化控件
		transformerId.performClick();
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
		choicePdf = (EditText) getActivity().findViewById(R.id.dsm_choice_pdf_panel_id).findViewById(R.id.select_pdf_id);
		choicePdf.setText(housePath);
		choicePdf.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//选择配电房
				Intent intent = new Intent(getActivity(),ChoiceHouseDialog.class);
				startActivityForResult(intent, Constants.REQUEST_CHOICE_HOUSE_CODE);
			}
		});

		transformerId = (Button) getActivity().findViewById(R.id.device_type_transformer_id);
		cableId = (Button) getActivity().findViewById(R.id.device_type_cable_id);
		cabinetId = (Button) getActivity().findViewById(R.id.device_type_cabinet_id);
		temperatureId = (Button) getActivity().findViewById(R.id.device_type_temperature_id);
		transformerId.setOnClickListener(deviceTypeClickListener);
		cableId.setOnClickListener(deviceTypeClickListener);
		cabinetId.setOnClickListener(deviceTypeClickListener);
		temperatureId.setOnClickListener(deviceTypeClickListener);
		
		//获取fragment管理器
		fm = getActivity().getSupportFragmentManager();
	}
	
	OnClickListener deviceTypeClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setBtnStyle();
			if("".equalsIgnoreCase(choicePdf.getText().toString())){
				Toast.makeText(getActivity(), "请先选择要查看的配电房", Toast.LENGTH_SHORT).show();return;
			}
			//先将“具体大类设备明细面板”下面的设备明细面板隐藏
			LinearLayout layout =  (LinearLayout) getView().findViewById(R.id.device_type_container_id);
			int count = layout.getChildCount();
			for (int i = 0; i < count; i++) {
				layout.getChildAt(i).setVisibility(View.GONE);
			}
			try {
				FragmentTransaction trans = fm.beginTransaction();
				switch (v.getId()) {
				case R.id.device_type_transformer_id://变压器
					if(!isLoadTransformer){
						isLoadTransformer = true;
						trans.add(R.id.device_type_container_id, new DeviceStateTransformerFragment(),"TRANSFORMER");
					}else{//如果是第二次点击，则只需要将隐藏的面板显示出来即可
						fm.findFragmentByTag("TRANSFORMER").getView().setVisibility(View.VISIBLE);
					}
					break;
				case R.id.device_type_cable_id://电缆
					if(!isLoadCable){
						isLoadCable = true;
						trans.add(R.id.device_type_container_id, new DeviceStateCableFragment(),"CABLE");
					}else{
						fm.findFragmentByTag("CABLE").getView().setVisibility(View.VISIBLE);
					}
					break;
				case R.id.device_type_cabinet_id://开关柜
					if(!isLoadCinet){
						isLoadCinet = true;
						trans.add(R.id.device_type_container_id, new DeviceStateCabinetFragment(),"CABINET");
					}else{
						fm.findFragmentByTag("CABINET").getView().setVisibility(View.VISIBLE);
					}
					break;
				case R.id.device_type_temperature_id://设备温度趋势
					if(!isLoadDeviceTemperature){
						isLoadDeviceTemperature = true;
						trans.add(R.id.device_type_container_id, new DeviceStateTemperatureFragment(),"TEMPERATURE");
					}else{
						fm.findFragmentByTag("TEMPERATURE").getView().setVisibility(View.VISIBLE);
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
	
	private void setBtnStyle(){
		((Button)getActivity().findViewById(R.id.device_type_transformer_id)).setTextColor(Color.parseColor("#000000"));
		((Button)getActivity().findViewById(R.id.device_type_cable_id)).setTextColor(Color.parseColor("#000000"));
		((Button)getActivity().findViewById(R.id.device_type_cabinet_id)).setTextColor(Color.parseColor("#000000"));
		((Button)getActivity().findViewById(R.id.device_type_temperature_id)).setTextColor(Color.parseColor("#000000"));
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Constants.REQUEST_CHOICE_HOUSE_CODE && requestCode == Constants.REQUEST_CHOICE_HOUSE_CODE){
			choicePdf.setText(data.getExtras().getString(Constants.HOUSE_PATH));
			houseFunctionId = data.getExtras().getString(Constants.HOUSE_FUNCTIONID);
			
			//改变了配电房后，需要重新加载变压器中的变压器下拉框数据，电缆中的电缆数据，开关柜中的开关柜数据，设备曲线（变压器下拉框数据）
			LinearLayout layout =  (LinearLayout) getView().findViewById(R.id.device_type_container_id);
			int count = layout.getChildCount();
			for (int i = 0; i < count; i++) {
				if(layout.getChildAt(i).getVisibility() == View.VISIBLE){
					if(getFragmentManager().findFragmentByTag("TRANSFORMER").getView() == layout.getChildAt(i)){
						//变压器 重新加载变压器下拉框数据
						((DeviceStateTransformerFragment)getFragmentManager().findFragmentByTag("TRANSFORMER")).reloadTransSpinnerData();
					}else if((getFragmentManager().findFragmentByTag("CABLE") != null) &&
							getFragmentManager().findFragmentByTag("CABLE").getView() == layout.getChildAt(i)){
						//电缆
						((DeviceStateCableFragment)getFragmentManager().findFragmentByTag("CABLE")).reloadCableSpinnerData();
					}else if((getFragmentManager().findFragmentByTag("CABINET") != null) &&
							getFragmentManager().findFragmentByTag("CABINET").getView() == layout.getChildAt(i)){
						//开关柜
						((DeviceStateCabinetFragment)getFragmentManager().findFragmentByTag("CABINET")).reloadCabinetSpinnerData();
					}else if((getFragmentManager().findFragmentByTag("TEMPERATURE") != null) &&
							getFragmentManager().findFragmentByTag("TEMPERATURE").getView() == layout.getChildAt(i)){
						//设备温度曲线
					}					
				}
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public String getHouseFunctionId(){
		return this.houseFunctionId;
	}
}
