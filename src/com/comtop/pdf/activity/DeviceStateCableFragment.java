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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Utils;

/**
 * 设备状态监测-电缆面板 
 * @author linyong
 */
public class DeviceStateCableFragment extends Fragment {

	//电缆下拉框
    private Spinner dlSpinner;
    private Button dlQueryBtn;
    private LinearLayout dlContainer;
    private ArrayAdapter<String> adapter ;
	private JSONObject functionObj = new JSONObject();
    private List<String> cableSpinnerData = new ArrayList<String>();
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dsm_cable_main, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		dlQueryBtn.performClick();
	}

	private void init() {
		dlSpinner = (Spinner) getActivity().findViewById(R.id.dl_spinner_id);
		adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getDlData());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dlSpinner.setAdapter(adapter);
        
        dlQueryBtn = (Button) getActivity().findViewById(R.id.dl_query_id);
        dlQueryBtn.setOnClickListener(this.dlQueryClickListener);

        dlContainer = (LinearLayout) getActivity().findViewById(R.id.dl_container_id);
	}

    OnClickListener dlQueryClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			/*ProgressBar bar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
			bar.setVisibility(View.VISIBLE);*/
			
			dlContainer.removeAllViews();
			LinearLayout cableDevice = null;
			/**
			 * A、B、C相温度和告警信息
			 * 温度偏差值和告警信息
			 * 排风扇状态和告警信息
			 */
			cableDevice = (LinearLayout) View.inflate(getActivity(), R.layout.dsm_cable_device_main, null);
			TextView text = (TextView) cableDevice.findViewById(R.id.dl_name_id);
			text.setText(dlSpinner.getSelectedItem().toString());
			
			//设置A相数据
			setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.dl_a_status_img_id), 
					(TextView)cableDevice.findViewById(R.id.dl_a_temperature_id),"12℃", false);
			//设置B相数据
			setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.dl_b_status_img_id), 
					(TextView)cableDevice.findViewById(R.id.dl_b_temperature_id),"12℃", false);
			//设置C相数据
			setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.dl_c_status_img_id), 
					(TextView)cableDevice.findViewById(R.id.dl_c_temperature_id),"9℃", false);
			//设置温度偏差数据
			setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.dl_wdpc_status_img_id), 
					(TextView)cableDevice.findViewById(R.id.dl_wdpc_temperature_id),"30%", false);
			//设置电缆局放状态数据
			setDeviceTypeItem((ImageView)cableDevice.findViewById(R.id.dl_dljf_status_img_id), 
					(TextView)cableDevice.findViewById(R.id.dl_dljf_temperature_id),"开", false);
			//给温度偏差新增点击事件，显示告警位置
			((ImageView)cableDevice.findViewById(R.id.dl_wdpc_status_img_id)).setOnClickListener(wdpcClickListener);
			dlContainer.addView(cableDevice);
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
	 * 获取电缆下拉框数据
	 * @return
	 */
    private List<String> getDlData(){
    	try {
    		//根据配电房加载变压器
			JSONArray trans = Utils.newInstance().getArchiveInfoByFatherFunctionId(getActivity(),getHouseFunctionId(), 3);
			if(trans != null && trans.length()>0){
				for(int i=0;i<trans.length();i++){
					cableSpinnerData.add(trans.getJSONObject(i).getString("nodeTitle"));
		            functionObj.put(trans.getJSONObject(i).getString("nodeTitle"), 
		            		trans.getJSONObject(i).getString("functionId"));
				}
			}
		} catch (Exception e) {
			Toast.makeText(getActivity(), "获取电缆数据异常", Toast.LENGTH_SHORT).show();
		}
        return cableSpinnerData;
    }
    
    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((DeviceStateFragment)getFragmentManager().findFragmentByTag("DEVICESTATE")).getHouseFunctionId();
    }
    
    //重新加载电缆下拉框数据
    public void reloadCableSpinnerData(){
    	cableSpinnerData.clear();
    	getDlData();
    	adapter.notifyDataSetChanged();
    }
}
