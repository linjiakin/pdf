package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Utils;

/**
 * 设备状态监测-开关柜面板
 * @author linyong 
 */
public class DeviceStateCabinetFragment extends Fragment {

	//开关柜下拉框
    private Spinner kggSpinner;
    private Button kggQueryBtn;
    //触头温度
    private CheckBox deviceTypeCtwd;
    //柜内温度
    private CheckBox deviceTypeGnwd;
    //柜内湿度
    private CheckBox deviceTypeGnsd;
    private LinearLayout kggContainer;
    private ArrayAdapter<String> adapter;
	private JSONObject functionObj = new JSONObject();
    private List<String> cabinetSpinnerData = new ArrayList<String>();
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dsm_cabinet_main, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		//默认选中触头温度查询
		deviceTypeCtwd.setChecked(true);
		kggQueryBtn.performClick();
	}

	private void init() {
		kggSpinner = (Spinner) getActivity().findViewById(R.id.kgg_spinner_id);
		adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getKggData());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kggSpinner.setAdapter(adapter);
        
        kggQueryBtn = (Button) getActivity().findViewById(R.id.kgg_query_id);
        kggQueryBtn.setOnClickListener(this.kggQueryClickListener);

        deviceTypeCtwd = (CheckBox) getActivity().findViewById(R.id.deviceType_ctwd_id);
        deviceTypeGnwd = (CheckBox) getActivity().findViewById(R.id.deviceType_gnwd_id);
        deviceTypeGnsd = (CheckBox) getActivity().findViewById(R.id.deviceType_gnsd_id);
        kggContainer = (LinearLayout) getActivity().findViewById(R.id.kgg_container_id);
	}

    OnClickListener kggQueryClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(!deviceTypeCtwd.isChecked() && !deviceTypeGnwd.isChecked() && !deviceTypeGnsd.isChecked()){
				Toast.makeText(getActivity(), "请选择设备类型", Toast.LENGTH_SHORT).show();
				return;
			}
			kggContainer.removeAllViews();
			LinearLayout cableDevice = null;

			if(deviceTypeCtwd.isChecked()){
				/**
				 * A、B、C相温度和告警信息
				 * 温度偏差值和告警信息
				 * 排风扇状态和告警信息
				 */
				cableDevice = (LinearLayout) View.inflate(getActivity(), R.layout.dsm_cabinet_device_main, null);
				TextView text = (TextView) cableDevice.findViewById(R.id.kgg_name_id);
				text.setText(kggSpinner.getSelectedItem().toString()+"/触头温度");
				
				//设置A相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_a_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_a_temperature_id),"12℃", false);
				//设置B相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_b_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_b_temperature_id),"12℃", false);
				//设置C相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_c_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_c_temperature_id),"9℃", false);
				//设置温度偏差数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_wdpc_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_wdpc_temperature_id),"30%", false);
				//设置开关柜局放状态数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_kggjf_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_kggjf_temperature_id),"开", false);
				//给温度偏差新增点击事件，显示告警位置
				((ImageView)cableDevice.findViewById(R.id.kgg_wdpc_status_img_id)).setOnClickListener(wdpcClickListener);
				kggContainer.addView(cableDevice);
			}

			if(deviceTypeGnwd.isChecked()){
				/**
				 * A、B、C相温度和告警信息
				 * 温度偏差值和告警信息
				 * 排风扇状态和告警信息
				 */
				cableDevice = (LinearLayout) View.inflate(getActivity(), R.layout.dsm_cabinet_device_main, null);
				TextView text = (TextView) cableDevice.findViewById(R.id.kgg_name_id);
				text.setText(kggSpinner.getSelectedItem().toString()+"/柜内温度");
				
				//设置A相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_a_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_a_temperature_id),"22℃", true);
				//设置B相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_b_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_b_temperature_id),"12℃", false);
				//设置C相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_c_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_c_temperature_id),"33℃", true);
				//设置温度偏差数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_wdpc_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_wdpc_temperature_id),"10%", false);
				//设置开关柜局放状态数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_kggjf_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_kggjf_temperature_id),"开", false);
				//给温度偏差新增点击事件，显示告警位置
				((ImageView)cableDevice.findViewById(R.id.kgg_wdpc_status_img_id)).setOnClickListener(wdpcClickListener);
				kggContainer.addView(cableDevice);
			}

			if(deviceTypeGnsd.isChecked()){
				/**
				 * A、B、C相温度和告警信息
				 * 温度偏差值和告警信息
				 * 排风扇状态和告警信息
				 */
				cableDevice = (LinearLayout) View.inflate(getActivity(), R.layout.dsm_cabinet_device_main, null);
				TextView text = (TextView) cableDevice.findViewById(R.id.kgg_name_id);
				text.setText(kggSpinner.getSelectedItem().toString()+"/柜内湿度");
				
				//设置A相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_a_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_a_temperature_id),"23℃", false);
				//设置B相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_b_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_b_temperature_id),"22℃", true);
				//设置C相数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_c_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_c_temperature_id),"3℃", true);
				//设置温度偏差数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_wdpc_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_wdpc_temperature_id),"20%", false);
				//设置开关柜局放状态数据
				setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.kgg_kggjf_status_img_id), 
						(TextView)cableDevice.findViewById(R.id.kgg_kggjf_temperature_id),"关", true);
				//给温度偏差新增点击事件，显示告警位置
				((ImageView)cableDevice.findViewById(R.id.kgg_wdpc_status_img_id)).setOnClickListener(wdpcClickListener);
				kggContainer.addView(cableDevice);
			}
			//bar.setVisibility(View.GONE);
		}
	};
	
	OnClickListener wdpcClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(),CommonDialog.class);
			intent.putExtra("msg", "显示报警位置");
			startActivity(intent);
		}
	};
	
	/**
	 * 设置设备类型指标对象
	 * @param imageView
	 * @param textView
	 * @param temperature
	 * @param isWarn
	 */
	private void setDeviceTypeItem(ImageView imageView,TextView textView,String temperature,boolean isWarn){
		if(/**如果A相告警*/isWarn){
			//设置告警图标
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.red));
			//设置字体为红色
			textView.setTextColor(Color.parseColor("#FF0000"));
		}
		//设置A相温度
		textView.setText(temperature);
	}

	/**
	 * 获取开关柜下拉框数据
	 * @return
	 */
    private List<String> getKggData(){
    	try {
    		//根据配电房加载变压器
			JSONArray trans = Utils.newInstance().getArchiveInfoByFatherFunctionId(getActivity(),getHouseFunctionId(), 4);
			if(trans != null && trans.length()>0){
				for(int i=0;i<trans.length();i++){
					cabinetSpinnerData.add(trans.getJSONObject(i).getString("nodeTitle"));
		            functionObj.put(trans.getJSONObject(i).getString("nodeTitle"), 
		            		trans.getJSONObject(i).getString("functionId"));
				}
			}
		} catch (Exception e) {
			Toast.makeText(getActivity(), "获取开关柜数据异常", Toast.LENGTH_SHORT).show();
		}
        return cabinetSpinnerData;
    }
    
    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((DeviceStateFragment)getFragmentManager().findFragmentByTag("DEVICESTATE")).getHouseFunctionId();
    }
    
    //重新加载变压器下拉框数据
    public void reloadCabinetSpinnerData(){
    	cabinetSpinnerData.clear();
    	getKggData();
    	adapter.notifyDataSetChanged();
    }
}
