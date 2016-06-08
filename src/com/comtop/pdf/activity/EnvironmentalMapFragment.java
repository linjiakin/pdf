package com.comtop.pdf.activity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;

/**
 * 环境监测-平面图
 * @author linyong
 */
public class EnvironmentalMapFragment extends Fragment {

	private int pageSize = 3;//平面图数量
	private int currIndex = 1;// 当前页卡编号
	private int ivCursorWidth;// 动画图片宽度
	private int tabWidth;// 每个tab头的宽度
	private int offsetX;// tab头的宽度减去动画图片的宽度再除以2（保证动画图片相对tab头居中）
	private ImageView ivCursor;// 下划线图片
	//中压、变压、低压
	private TextView zyMapTextView,byMapTextView,dyMapTextView;
	//湿度、温度
	private TextView humidityText,temperatureText;
	//加载平面图
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.environmental_map, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		//默认显示中压
		zyMapTextView.performClick();
	}
	
	private void init() {
		initTextView();
		initImageView();
	}

	/**
	 * 初始化控件
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initTextView() {
		zyMapTextView = (TextView) getActivity().findViewById(R.id.environmental_map_zymap_id);
		byMapTextView = (TextView) getActivity().findViewById(R.id.environmental_map_bymap_id);
		dyMapTextView = (TextView) getActivity().findViewById(R.id.environmental_map_dymap_id);
		zyMapTextView.setOnClickListener(new MyOnClickListener(0));
		byMapTextView.setOnClickListener(new MyOnClickListener(1));
		dyMapTextView.setOnClickListener(new MyOnClickListener(2));

		humidityText = (TextView) getActivity().findViewById(R.id.environmental_map_humidity_id);
		temperatureText = (TextView) getActivity().findViewById(R.id.environmental_map_temperature_id);

		webView = (WebView) getActivity().findViewById(R.id.environmental_map_webview_id);
		webView.setWebViewClient(new WebViewClient() {
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

	/**
	 * 初始化动画
	 */
	private void initImageView() {
		ivCursor = (ImageView) getActivity().findViewById(R.id.environmental_map_cursor_id);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int screenW = dm.widthPixels;// 获取分辨率宽度
		ivCursorWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.pager_cursor_unline).getWidth();// 获取图片宽度
		tabWidth = screenW / pageSize;
		if (ivCursorWidth > tabWidth) {
			ivCursor.getLayoutParams().width = tabWidth;
			ivCursorWidth = tabWidth;
		}
		offsetX = (tabWidth - ivCursorWidth) / 2;
	}
	
	/**
	 * 加载平面图
	 * @param index
	 */
	private void changeMap(int index){
		getHouseFunctionId();
		//根据配电房功能位置id和index获取配电房的平面图和温湿度信息
		switch (index) {
		case 0://中压
			humidityText.setText("10%");
			temperatureText.setText("20℃");
			webView.loadUrl(Constants.SECURITY_MAP_ZY_URL);
			//webView.loadUrl("http://baidu.com");
			break;
		case 1://变压
			humidityText.setText("5%");
			temperatureText.setText("20℃");
			webView.loadUrl(Constants.SECURITY_MAP_BY_URL);
			//webView.loadUrl("http://qq.com");
			break;
		case 2://低压
			humidityText.setText("9%");
			temperatureText.setText("20℃");
			webView.loadUrl(Constants.SECURITY_MAP_DY_URL);
			//webView.loadUrl("http://hb.qq.com");
			break;
		}
	}
	
	/**
	 * 改变光标
	 * @param index
	 */
	private void changeCursorImage(int index){
		Animation animation = new TranslateAnimation(tabWidth * currIndex
				+ offsetX, tabWidth * index + offsetX, 0, 0);// 显然这个比较简洁，只有一行代码。
		currIndex = index;
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(350);
		ivCursor.startAnimation(animation);
	}

	/**
	 * 头标点击监听
	 */
	class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			changeMap(index);
			changeCursorImage(index);
		}
	}

    //获取选择的配电房功能位置
    private String getHouseFunctionId(){
    	return ((EnvironmentalFragment)getFragmentManager().findFragmentByTag("ENVIRONMENTAL")).getHouseFunctionId();
    }
}