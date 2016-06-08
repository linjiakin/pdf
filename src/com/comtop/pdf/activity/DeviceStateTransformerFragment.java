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
 * 设备状态监测-变压器面板 
 * @author linyong
 */
public class DeviceStateTransformerFragment extends Fragment {

	//变压器下拉框
    private Spinner byqSpinner;
    private Button byqQueryBtn;
    //母排复选框
    private CheckBox deviceTypeMp;
    //绕组复选框
    private CheckBox deviceTypeRz;
    //铁心复选框
    private CheckBox deviceTypeTx;
    //变压器设备容器(显示母排、绕组、铁心的信息)
    private LinearLayout byqContainer;
	//存储变压器功能位置
	private JSONObject functionObj = new JSONObject();
	private List<String> transSpinnerData = new ArrayList<String>();
	private ArrayAdapter<String> transSpinnerAdapter ;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dsm_transformer_main, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		deviceTypeMp.setChecked(true);//默认选中母排
		byqQueryBtn.performClick();
	}
	
	//初始化界面控件
	private void init() {
		byqSpinner = (Spinner) getActivity().findViewById(R.id.byq_spinner_id);
		transSpinnerAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getByqData());
		transSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        byqSpinner.setAdapter(transSpinnerAdapter);
        
        byqQueryBtn = (Button) getActivity().findViewById(R.id.byq_query_id);
        byqQueryBtn.setOnClickListener(this.byqQueryClickListener);

        deviceTypeMp = (CheckBox) getActivity().findViewById(R.id.deviceType_mp_id);
        deviceTypeRz = (CheckBox) getActivity().findViewById(R.id.deviceType_rz_id);
        deviceTypeTx = (CheckBox) getActivity().findViewById(R.id.deviceType_tx_id);
        byqContainer = (LinearLayout) getActivity().findViewById(R.id.byq_container_id);
	}

    OnClickListener byqQueryClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(!deviceTypeMp.isChecked() && !deviceTypeRz.isChecked() && !deviceTypeTx.isChecked()){
				Toast.makeText(getActivity(), "请选择设备类型", Toast.LENGTH_SHORT).show();
				return;
			}
			
			/*ProgressBar bar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
			bar.setVisibility(View.VISIBLE);*/
			
			byqContainer.removeAllViews();
			LinearLayout transDevice = null;
			if(deviceTypeMp.isChecked()){
				//查询指定配电房的变压器下的母排信息
				/**
				 * A、B、C相温度和告警信息
				 * 温度偏差值和告警信息
				 * 排风扇状态和告警信息
				 * 报警位置
				 */
				transDevice = (LinearLayout) View.inflate(getActivity(), R.layout.dsm_transformer_device_main, null);
				
				//设置类型名称
				TextView text = (TextView) transDevice.findViewById(R.id.byq_name_id);
				text.setText(byqSpinner.getSelectedItem()+"/"+"母排");
				
				//设置A相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_a_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_a_temperature_id),"30℃", true);
				//设置B相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_b_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_b_temperature_id),"20℃", false);
				//设置C相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_c_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_c_temperature_id),"19℃", false);
				//设置温度偏差数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_wdpc_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_wdpc_temperature_id),"20%", true);
				//设置排风扇状态数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_pfs_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_pfs_temperature_id),"开", false);
				
				//给温度偏差新增点击事件，显示告警位置
				((ImageView)transDevice.findViewById(R.id.byq_wdpc_status_img_id)).setOnClickListener(wdpcClickListener);
				
				byqContainer.addView(transDevice);
			}

			if(deviceTypeRz.isChecked()){
				//查询指定配电房的变压器下的绕组信息
				/**
				 * A、B、C相温度和告警信息
				 * 温度偏差值和告警信息
				 * 排风扇状态和告警信息
				 */
				transDevice = (LinearLayout) View.inflate(getActivity(), R.layout.dsm_transformer_device_main, null);
				//设置类型名称
				TextView text = (TextView) transDevice.findViewById(R.id.byq_name_id);
				text.setText(byqSpinner.getSelectedItem()+"/"+"绕组");
				
				//设置A相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_a_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_a_temperature_id),"10℃", false);
				//设置B相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_b_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_b_temperature_id),"30℃", true);
				//设置C相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_c_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_c_temperature_id),"29℃", false);
				//设置温度偏差数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_wdpc_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_wdpc_temperature_id),"10%", false);
				//设置排风扇状态数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_pfs_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_pfs_temperature_id),"关", true);
				//给温度偏差新增点击事件，显示告警位置
				((ImageView)transDevice.findViewById(R.id.byq_wdpc_status_img_id)).setOnClickListener(wdpcClickListener);
				byqContainer.addView(transDevice);
			}

			if(deviceTypeTx.isChecked()){
				//查询指定配电房的变压器下的铁心信息
				/**
				 * A、B、C相温度和告警信息
				 * 温度偏差值和告警信息
				 * 排风扇状态和告警信息
				 */
				transDevice = (LinearLayout) View.inflate(getActivity(), R.layout.dsm_transformer_device_main, null);
				TextView text = (TextView) transDevice.findViewById(R.id.byq_name_id);
				text.setText(byqSpinner.getSelectedItem()+"/"+"铁心");
				
				//设置A相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_a_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_a_temperature_id),"12℃", false);
				//设置B相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_b_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_b_temperature_id),"12℃", false);
				//设置C相数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_c_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_c_temperature_id),"9℃", false);
				//设置温度偏差数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_wdpc_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_wdpc_temperature_id),"30%", false);
				//设置排风扇状态数据
				setDeviceTypeItem((ImageView)transDevice.findViewById(R.id.byq_pfs_status_img_id), 
						(TextView)transDevice.findViewById(R.id.byq_pfs_temperature_id),"开", false);
				//给温度偏差新增点击事件，显示告警位置
				((ImageView)transDevice.findViewById(R.id.byq_wdpc_status_img_id)).setOnClickListener(wdpcClickListener);
				byqContainer.addView(transDevice);
			}
			
			//bar.setVisibility(View.GONE);
		}
	};
	
	//温度偏差点击事件
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
	 * 获取变压器下拉框数据
	 * @return
	 */
    private List<String> getByqData(){
    	try {
    		//根据配电房加载变压器
			JSONArray trans = Utils.newInstance().getArchiveInfoByFatherFunctionId(getActivity(),getHouseFunctionId(), 2);
			if(trans != null && trans.length()>0){
				for(int i=0;i<trans.length();i++){
					transSpinnerData.add(trans.getJSONObject(i).getString("nodeTitle"));
		            functionObj.put(trans.getJSONObject(i).getString("nodeTitle"), 
		            		trans.getJSONObject(i).getString("functionId"));
				}
			}
		} catch (Exception e) {
			Toast.makeText(getActivity(), "获取变压器数据异常", Toast.LENGTH_SHORT).show();
		}
        return transSpinnerData;
    }
    
    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((DeviceStateFragment)getFragmentManager().findFragmentByTag("DEVICESTATE")).getHouseFunctionId();
    }
    
    //重新加载变压器下拉框数据
    public void reloadTransSpinnerData(){
    	transSpinnerData.clear();
    	getByqData();
    	transSpinnerAdapter.notifyDataSetChanged();
    }
}
