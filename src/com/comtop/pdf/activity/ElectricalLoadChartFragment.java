package com.comtop.pdf.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.Utils;

/**
 * 电气监测-负载趋势图
 * @author linyong
 */
public class ElectricalLoadChartFragment extends Fragment {
    
	private WebView webView;
	private EditText queryTime;
	private Button query ;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.electrical_loadchart, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		query.performClick();
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void init() {
		//查询时间
		queryTime = (EditText) getActivity().findViewById(R.id.electrical_loadchart_querytime_id);
		queryTime.setText(Utils.newInstance().getDateString(Constants.DATE_PATTERN_YYYYMMDD));
		queryTime.setOnClickListener(clickListener);
        
		//查询按钮
		query = (Button) getActivity().findViewById(R.id.electrical_loadchart_query_id);
		query.setOnClickListener(clickListener);
		
		//加载图表
		webView = (WebView) getActivity().findViewById(R.id.electrical_loadchart_webview_id);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setBackgroundColor(0);
		webView.getBackground().setAlpha(0);
	}
	
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.electrical_loadchart_querytime_id:
				showDateDialog(queryTime);
				break;
			case R.id.electrical_loadchart_query_id:
				webView.loadUrl(Constants.ELECTRICAL_LOADCHART_URL+"?queryTime="+queryTime.getText()+
						"&powerroomId="+getHouseFunctionId());
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 显示日期选择框
	 * @param timeText
	 */
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
    	return ((ElectricalFragment)getFragmentManager().findFragmentByTag("ELECTRICAL")).getHouseFunctionId();
    }
}
