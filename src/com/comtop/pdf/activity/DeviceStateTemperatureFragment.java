package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.Utils;

/**
 * 设备状态监测-设备温度曲线面板
 * @author linyong
 */
public class DeviceStateTemperatureFragment extends Fragment {

	/**装置类型*/
	private Spinner zzTypeSpinner;
	/**设备名称*/
    private Spinner deviceNameSpinner;
	/**设备类型*/
    private Spinner deviceTypeSpinner;
    private ArrayAdapter<String> zzTypeAdapter;
    private ArrayAdapter<String> dnAdapter;
    private ArrayAdapter<String> dtAdapter;
    private Button queryBtn;
    private EditText queryDate;
    private List<String> deviceNameData = new ArrayList<String>();
    private List<String> deviceTypeData = new ArrayList<String>();
    private String[]zzTypeData = {"变压器","电缆","开关柜"};
    private WebView webView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dsm_device_temperature_main, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		queryBtn.performClick();
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void init() {
		//装置类型
		zzTypeSpinner = (Spinner) getActivity().findViewById(R.id.zz_type_spinner_id);
		zzTypeAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getZzTypeData());
		zzTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		zzTypeSpinner.setAdapter(zzTypeAdapter);
		zzTypeSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);

		//设备名称
		deviceNameSpinner = (Spinner) getActivity().findViewById(R.id.device_name_spinner_id);
		dnAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getDeviceNameData());
		dnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		deviceNameSpinner.setAdapter(dnAdapter);
        
		//设备类型
		deviceTypeSpinner = (Spinner) getActivity().findViewById(R.id.device_type_spinner_id);
		dtAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,getDeviceTypeData());
		dtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		deviceTypeSpinner.setAdapter(dtAdapter);
		
		queryDate = (EditText) getActivity().findViewById(R.id.sbwd_query_date_id);
		queryDate.setOnClickListener(this.queryDateClickListener);

		queryBtn = (Button) getActivity().findViewById(R.id.sbwd_query_id);
		queryBtn.setOnClickListener(this.sbwdQueryClickListener);

		queryDate.setText(Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));
		
		webView = (WebView) getActivity().findViewById(R.id.deviceTemperature_webview_id);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new MyClass(){
			@Override
			public void run() {
				Toast.makeText(getActivity(), "aaa", Toast.LENGTH_SHORT).show();
			}
		}, "deviceStateTemperature");
		webView.setBackgroundColor(0);
		webView.getBackground().setAlpha(0);
	}
	
	interface MyClass{
		public void run();
	}
	
	//下拉框选中事件
	OnItemSelectedListener spinnerItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			switch (parent.getId()) {
			case R.id.zz_type_spinner_id://装置类型
				String zzType = ((TextView) view).getText().toString();
				if(zzType.equalsIgnoreCase(zzTypeData[0])){
					//变压器
					getActivity().findViewById(R.id.dt_label_id).setVisibility(View.VISIBLE);
					getActivity().findViewById(R.id.device_type_spinner_id).setVisibility(View.VISIBLE);
				}else if(zzType.equalsIgnoreCase(zzTypeData[1])){
					//电缆
					getActivity().findViewById(R.id.dt_label_id).setVisibility(View.GONE);
					getActivity().findViewById(R.id.device_type_spinner_id).setVisibility(View.GONE);
				}else if(zzType.equalsIgnoreCase(zzTypeData[2])){
					//开关柜
					getActivity().findViewById(R.id.dt_label_id).setVisibility(View.VISIBLE);
					getActivity().findViewById(R.id.device_type_spinner_id).setVisibility(View.VISIBLE);
				}
				getDeviceNameData();
				dnAdapter.notifyDataSetChanged();
				getDeviceTypeData();
				dtAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};
	
	//选择日期事件
	OnClickListener queryDateClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			new DatePickerDialog(getActivity(), new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					queryDate.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
				}
			}, Integer.parseInt(getQueryDate()[0]),Integer.parseInt(getQueryDate()[1])-1,Integer.parseInt(getQueryDate()[2])).show();
		}
	};

	//查询事件
    OnClickListener sbwdQueryClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.sbwd_query_id:
				webView.loadUrl(Constants.DEVICE_STATUS_MONITOR_TEMPERATURE_CHART_URL+"?powerroomId="+getHouseFunctionId()+
						"&deviceId=xx&queryTime="+queryDate.getText());
				break;

			default:
				break;
			}
		}
	};
	
	private String[] getQueryDate(){
		String[]dates = queryDate.getText().toString().split("/");
		return dates;
	}

	//装置类型
	private String[] getZzTypeData() {
		return zzTypeData;
	}

	//设备名称
    private List<String> getDeviceNameData(){
    	//根据配电房和装置类型加载对应的数据
    	deviceNameData.clear();
    	//装置类型
    	String zzName = zzTypeSpinner.getSelectedItem().toString();
    	//配电房功能位置
    	String houseFunctionId = ((DeviceStateFragment)getFragmentManager().findFragmentByTag("DEVICESTATE")).getHouseFunctionId();
		if(zzName.equalsIgnoreCase(zzTypeData[0])){
			//变压器
			deviceNameData.addAll(getDeviceNameData(houseFunctionId, 2));
		}else if(zzName.equalsIgnoreCase(zzTypeData[1])){
			//电缆
			deviceNameData.addAll(getDeviceNameData(houseFunctionId, 3));
		}else if(zzName.equalsIgnoreCase(zzTypeData[2])){
			//开关柜
			deviceNameData.addAll(getDeviceNameData(houseFunctionId, 4));
		}
        return deviceNameData;
    }
    
    private List<String> getDeviceNameData(String houseFunctionId,int deviceType){
    	List<String> deviceNameData = new ArrayList<String>();
		try {
			JSONArray datas = Utils.newInstance().getArchiveInfoByFatherFunctionId(getActivity(), houseFunctionId, deviceType);
			for (int i = 0;i<datas.length();i++) {
				deviceNameData.add(datas.getJSONObject(i).getString("nodeTitle"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return deviceNameData;
    }
    
    /**
     * 获取设备类型
     * @return
     */
    private List<String> getDeviceTypeData(){
    	deviceTypeData.clear();
    	String zzName = zzTypeSpinner.getSelectedItem().toString();
    	if(zzName.equalsIgnoreCase(zzTypeData[0])){
    		//变压器
    		deviceTypeData.add("母排");
    		deviceTypeData.add("绕组");
    		deviceTypeData.add("铁心");
    	}else if(zzName.equalsIgnoreCase(zzTypeData[2])){
    		//开关柜
    		deviceTypeData.add("触头温度");
    		deviceTypeData.add("柜内温度");
    		deviceTypeData.add("柜内湿度");
    	}
    	return deviceTypeData;
    }

    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((DeviceStateFragment)getFragmentManager().findFragmentByTag("DEVICESTATE")).getHouseFunctionId();
    }
}
