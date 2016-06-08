package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.Utils;

/**
 * 电气监测-监测数据
 * @author linyong
 */
public class ElectricalMonitorDataFragment extends Fragment {
    
	private ListView monitorDataListView;
	private MyElectricalListViewAdapter monitroDataListAdapter;
    private List<Map<String,String>> monitorDataList = new ArrayList<Map<String,String>>();
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.electrical_monitordata, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		monitorDataListView = (ListView) getActivity().findViewById(R.id.electrical_monitordata_listview_id);
		monitroDataListAdapter = new MyElectricalListViewAdapter(getActivity(),getMonitorData());
        monitorDataListView.setAdapter(monitroDataListAdapter);
	}
	
	/**
	 * 获取监测数据
	 * @return
	 */
	private List<Map<String,String>> getMonitorData(){
		getHouseFunctionId();
		for(int i = 1,j=20;i<j;i++){
            Map<String,String> map = new HashMap<String, String>();
            map.put("no", i+"");
            map.put("monitor_datetime", Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));
            map.put("monitor_project", "201出线"+i);
            map.put("inarea", "#"+i+"变压器");
            map.put("monitor_point", "A相电流");
            map.put("value", "0.5A");
            map.put("monitor_status", i%2==0?"红色预警":"未知");
            map.put("threshold_value", "0.2A");
            monitorDataList.add(map);
		}
		return monitorDataList;
	}
	
	class MyElectricalListViewAdapter extends BaseAdapter{

		private Context context;
		private List<Map<String,String>> monitorDataList;
		public MyElectricalListViewAdapter(Context context,List<Map<String,String>> monitorDataList){
			this.context = context;
			this.monitorDataList = monitorDataList;
		}
		
		@Override
		public int getCount() {
			return monitorDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return monitorDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(context, R.layout.electrical_monitordata_listitem, null);
			Map<String,String> map = monitorDataList.get(position);
			((TextView)view.findViewById(R.id.electrical_no_id)).setText(map.get("no"));
			((TextView)view.findViewById(R.id.electrical_monitor_datetime)).setText(map.get("monitor_datetime"));
			((TextView)view.findViewById(R.id.electrical_monitor_project)).setText(map.get("monitor_project"));
			((TextView)view.findViewById(R.id.electrical_inarea)).setText(map.get("inarea"));
			((TextView)view.findViewById(R.id.electrical_monitor_point)).setText(map.get("monitor_point"));
			((TextView)view.findViewById(R.id.electrical_value)).setText(map.get("value"));
			TextView monitorStatusTextView = (TextView)view.findViewById(R.id.electrical_monitor_status);
			if(map.get("monitor_status") != null && map.get("monitor_status").equalsIgnoreCase("红色预警")){
				monitorStatusTextView.setBackgroundColor(Color.parseColor("#FF0000"));
			}
			monitorStatusTextView.setText(map.get("monitor_status"));
			((TextView)view.findViewById(R.id.electrical_threshold_value)).setText(map.get("threshold_value"));
			return view;
		}
	}
	
    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((ElectricalFragment)getFragmentManager().findFragmentByTag("ELECTRICAL")).getHouseFunctionId();
    }
}
