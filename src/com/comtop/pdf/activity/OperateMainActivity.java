package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.comtop.pdf.R;
import com.comtop.pdf.component.MyHorizontalScrollView;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.SharedPreferencesUtils;
import com.comtop.pdf.utils.Utils;

/**
 * 操作主面板
 * @author linyong
 */
public class OperateMainActivity extends FragmentActivity {

    private String[]titles = null;
    private int[]icons = null;
    //面板导航题头图标
    private ImageView pageHeadIcon;
    //面板导航题头标题
    private TextView pageHeadTitle;
    private WebView webView;
    //隐藏菜单列表
    private ListView listView;
    //隐藏菜单中显示的登录用户名文本
    private TextView menuUserName;
    private FragmentManager fm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_main);
        init();
    }

    /**
     * 初始化值
     */
    private void init() {
        titles = new String[]{getString(R.string.operate_home),getString(R.string.operate_1), getString(R.string.operate_2),
                getString(R.string.operate_3), getString(R.string.operate_4), getString(R.string.operate_5),
                getString(R.string.operate_6), getString(R.string.operate_7), getString(R.string.operate_9), getString(R.string.operate_8)};
        icons = new int[]{R.drawable.o_home,R.drawable.o1,R.drawable.o2,R.drawable.o3,R.drawable.o4,R.drawable.o5,
                R.drawable.o6,R.drawable.o7,R.drawable.o9,R.drawable.o8};
        pageHeadIcon = (ImageView) findViewById(R.id.page_head_icon_id);
        pageHeadTitle = (TextView) findViewById(R.id.page_head_title_id);
        menuUserName = (TextView) findViewById(R.id.menu_username_id);
        fm = getSupportFragmentManager();
        loadLoginInfo();
        loadBusinessPanel();
        loadMenu();
    }

    /**
     * 设置用户登录信息
     */
    private void loadLoginInfo(){
        //设置登录用户名称
        menuUserName.setText(new SharedPreferencesUtils(OperateMainActivity.this,Constants.SPF_NAME).getString(
        		Constants.LOGIN_USERNAME,""));
        //设置登录用户头像
    }

    /**
     * 设置操作面板信息
     */
    private void loadBusinessPanel(){
    	//获取选择的菜单索引
        int menuPosition = new SharedPreferencesUtils(OperateMainActivity.this,Constants.SPF_NAME).getInt(
        		Constants.MENU_POSITION,0);
        //设置头部导航
        pageHeadIcon.setImageResource(icons[menuPosition]);
        pageHeadTitle.setText(this.titles[menuPosition]);
        FragmentTransaction ft = fm.beginTransaction();
        //获取业务面板容器
		LinearLayout layout = (LinearLayout)findViewById(R.id.container_id);
		int count = layout.getChildCount();
		for (int i = 0; i < count; i++) {
			//先隐藏容器中的所有业务面板
			layout.getChildAt(i).setVisibility(View.GONE);
		}
        //根据选择的菜单项加载对应的fragment面板
        switch(menuPosition){
        case 1://视频监测
			if(fm.findFragmentByTag("VIDEO") == null){
	        	ft.add(R.id.container_id, new VideoFragment(),"VIDEO");
			}else{
				fm.findFragmentByTag("VIDEO").getView().setVisibility(View.VISIBLE);
			}
        	break;
        case 2://安防监测
			if(fm.findFragmentByTag("SECURITY") == null){
	        	ft.add(R.id.container_id, new SecurityFragment(),"SECURITY");
			}else{
				fm.findFragmentByTag("SECURITY").getView().setVisibility(View.VISIBLE);
			}
        	break;
        case 3://电气监测
			if(fm.findFragmentByTag("ELECTRICAL") == null){
	        	ft.add(R.id.container_id, new ElectricalFragment(),"ELECTRICAL");
			}else{
				fm.findFragmentByTag("ELECTRICAL").getView().setVisibility(View.VISIBLE);
			}
        	break;
        case 4://环境监测
			if(fm.findFragmentByTag("ENVIRONMENTAL") == null){
	        	ft.add(R.id.container_id, new EnvironmentalFragment(),"ENVIRONMENTAL");
			}else{
				fm.findFragmentByTag("ENVIRONMENTAL").getView().setVisibility(View.VISIBLE);
			}
        	break;
        case 5://设备状态监测
			if(fm.findFragmentByTag("DEVICESTATE") == null){
	        	ft.add(R.id.container_id, new DeviceStateFragment(),"DEVICESTATE");
			}else{
				fm.findFragmentByTag("DEVICESTATE").getView().setVisibility(View.VISIBLE);
			}
        	break;
        case 6://告警信息
			if(fm.findFragmentByTag("WARN") == null){
	        	ft.add(R.id.container_id, new WarnFragment(),"WARN");
			}else{
				fm.findFragmentByTag("WARN").getView().setVisibility(View.VISIBLE);
			}
        	break;
        case 7://预警信息
			if(fm.findFragmentByTag("ALARM") == null){
	        	ft.add(R.id.container_id, new AlarmFragment(),"ALARM");
			}else{
				fm.findFragmentByTag("ALARM").getView().setVisibility(View.VISIBLE);
			}
        	break;
        }
        ft.commit();
    }

    /**
     * 加载菜单
     */
    private void loadMenu(){
        listView = (ListView) findViewById(R.id.menu_listView_id);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,getMenuListData(),R.layout.menu_list_item,
                new String[]{"title","icon"},new int[]{R.id.menu_title_id,R.id.menu_icon_id});
        listView.setAdapter(simpleAdapter);
    }

    /**
     * 菜单列表数据源
     * @return
     */
    private List<Map<String, Object>> getMenuListData() {
        List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", titles[i-1]);
            map.put("icon", icons[i-1]);
            datas.add(map);
        }
        return datas;
    }

    /**
     * 折叠菜单
     * @param view
     */
    public void toggleMenu(View view){
    	//获取自定义的横向滚动视图
        MyHorizontalScrollView scrollView = (MyHorizontalScrollView) findViewById(R.id.my_hscroll_view_id);
        //触发显示或隐藏菜单事件
        scrollView.toggle();
    }

    /**
     * 菜单跳转
     * @param view
     */
    public void menuClick(View view){
        RelativeLayout layout = (RelativeLayout) view;
        //获取菜单标题
        TextView textView = (TextView) layout.getChildAt(1);
        String itemText = textView.getText().toString();
        /*//获取菜单图标
        ImageView imageView = (ImageView) layout.getChildAt(0);
        //获取选择的功能id
        String path = getResources().getResourceName(layout.getId());
        String layoutId = path.substring(path.indexOf("/o")+2,path.indexOf("/o")+3);*/
        if(itemText.equalsIgnoreCase(getString(R.string.operate_home))){
            //主页
            Intent intent = new Intent(OperateMainActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        else if(itemText.equalsIgnoreCase(getString(R.string.operate_8))){
            //注销
            Intent intent = new Intent(OperateMainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        else if(itemText.equalsIgnoreCase(getString(R.string.operate_9))){
            //线路导航
            Intent intent = new Intent(OperateMainActivity.this,MyMapActivity.class);
            startActivity(intent);
            return;
        }
        //保存菜单索引
        new SharedPreferencesUtils(OperateMainActivity.this,Constants.SPF_NAME).putInt(Constants.MENU_POSITION,
        		Utils.newInstance().getMenuPosition(itemText));
        //设置操作面板
        loadBusinessPanel();
        //折叠菜单
        toggleMenu(null);
    }

    /**
     * 设置业务面板头部导航信息
     * @param drawable 显示图标
     * @param title 标题
     */
    public void setPageHead(String title,Drawable drawable){
        //pageHeadIcon.setImageResource(icon);
        //pageHeadIcon.setImageDrawable(getResources().getDrawable(icon));
        pageHeadIcon.setImageDrawable(drawable);
        pageHeadTitle.setText(title);
    }

    /**
     * 打开url
     * @param url
     */
    @SuppressLint("SetJavaScriptEnabled")
	public void openUrl(String url){
        //加载外部资源
        webView.loadUrl(url);
        //覆盖默认打开url的方式，采用webview来打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        //启用javascript功能
        settings.setJavaScriptEnabled(true);
        //启用缓存功能
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

    }
    
    /**
     * 注意：不要使用该方法,否则布局出现问题
     */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//Toast.makeText(this, "configuration changed", Toast.LENGTH_SHORT).show();
		/*Toast.makeText(this, "configuration changed", Toast.LENGTH_SHORT).show();
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){//竖屏
			Toast.makeText(this, "竖屏", Toast.LENGTH_SHORT).show();
		}else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){//横屏 
			Toast.makeText(this, "横屏", Toast.LENGTH_SHORT).show();
		}*/
	}
	
}
