package com.comtop.pdf.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;

/**
 * 电气监测-接线图
 * @author linyong
 */
public class ElectricalMapFragment extends Fragment {
    
	private WebView webView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.electrical_map, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void init() {
		webView = (WebView) getActivity().findViewById(R.id.electrical_map_webview_id);
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
		loadMap();
	}
	
	public void loadMap(){
		getHouseFunctionId();
		webView.loadUrl(Constants.ELECTRICAL_MAP_URL);
		//webView.loadUrl("http://qq.com");
	}

    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((ElectricalFragment)getFragmentManager().findFragmentByTag("ELECTRICAL")).getHouseFunctionId();
    }
}
