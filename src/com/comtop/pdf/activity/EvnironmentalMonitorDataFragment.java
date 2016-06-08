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
 * 环境监测-监测数据
 * @author Administrator
 *
 */
public class EvnironmentalMonitorDataFragment extends Fragment {

	private ListView monitorDataListView;
	private MyenvironmentalListViewAdapter monitroDataListAdapter;
    private List<Map<String,String>> monitorDataList = new ArrayList<Map<String,String>>();
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.environmental_monitordata, container,false);
	}
	
	@Override
	public void onStart() {
		init();
		super.onStart();
	}

	private void init() {
		monitorDataListView = (ListView) getActivity().findViewById(R.id.environmental_monitordata_listview_id);
		monitroDataListAdapter = new MyenvironmentalListViewAdapter(getActivity(),getMonitorData());
        monitorDataListView.setAdapter(monitroDataListAdapter);
	}
	
	/**
	 * 获取监测数据
	 * @return
	 */
	private List<Map<String,String>> getMonitorData(){
		for(int i = 1,j=20;i<j;i++){
            Map<String,String> map = new HashMap<String, String>();
            map.put("no", i+"");
            map.put("monitor_datetime", Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));
            map.put("monitor_project", "301出线"+i);
            map.put("inarea", "#"+i+"变压器");
            map.put("device_no", "00"+i);
            map.put("message_status", (i%2==0)?"在线":"离线");
        	map.put("monitor_status", (i%2==0)?"红色预警":"未知");
            map.put("value", "0.2A");
            monitorDataList.add(map);
		}
		return monitorDataList;
	}
	
	class MyenvironmentalListViewAdapter extends BaseAdapter{

		private Context context;
		private List<Map<String,String>> monitorDataList;
		public MyenvironmentalListViewAdapter(Context context,List<Map<String,String>> monitorDataList){
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
			View view = View.inflate(context, R.layout.enviromental_monitordata_listitem, null);
			Map<String,String> map = monitorDataList.get(position);
			((TextView)view.findViewById(R.id.environmental_no_id)).setText(map.get("no"));
			((TextView)view.findViewById(R.id.environmental_monitor_datetime)).setText(map.get("monitor_datetime"));
			((TextView)view.findViewById(R.id.environmental_monitor_project)).setText(map.get("monitor_project"));
			((TextView)view.findViewById(R.id.environmental_inarea)).setText(map.get("inarea"));
			((TextView)view.findViewById(R.id.environmental_device_no)).setText(map.get("device_no"));
			((TextView)view.findViewById(R.id.environmental_message_status)).setText(map.get("message_status"));
			TextView monitorStatusTextView = (TextView)view.findViewById(R.id.environmental_monitor_status);
			if(map.get("monitor_status") != null && map.get("monitor_status").equalsIgnoreCase("红色预警")){
				monitorStatusTextView.setBackgroundColor(Color.parseColor("#FF0000"));
			}
			monitorStatusTextView.setText(map.get("monitor_status"));
			((TextView)view.findViewById(R.id.environmental_value)).setText(map.get("value"));
			return view;
		}
		
	}
}
