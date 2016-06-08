package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.SharedPreferencesUtils;
import com.comtop.pdf.utils.Utils;

/**
 * 主页面板
 * @author linyong
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private static final String TITLE = "title";
	private static final String ICON = "icon";
    //面板：视频监测、安防监测、电气监测、环境监测、设备状态监测、告警信息、预警信息、注销
    //元素：背景色、图标、文本
    private String[]titles = null;
    private int[]bgColors = null;
    private int[]icons = null;
    private GridView gridView = null;
    private TextView username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	//加载服务器数据并缓存
        new Thread(loadServerDataRunnable).start();
        init();
    }
    
    //加载服务数据线程
    Runnable loadServerDataRunnable = new Runnable() {
        public void run() {
        	Utils.newInstance().loadServerData(MainActivity.this);
        }
    };

	/**
     * 初始化值
     */
    private void init(){
        titles = new String[]{getString(R.string.operate_1),getString(R.string.operate_2),
                getString(R.string.operate_3),getString(R.string.operate_4),getString(R.string.operate_5),
                getString(R.string.operate_6),getString(R.string.operate_7),getString(R.string.operate_8)};
        bgColors = new int[]{getResources().getColor(R.color.main_video_monitor_bg),
        		getResources().getColor(R.color.main_security_monitor_bg),
        		getResources().getColor(R.color.main_electrical_monitor_bg),
        		getResources().getColor(R.color.main_environmental_monitor_bg),
        		getResources().getColor(R.color.main_devicestatus_monitor_bg),
        		getResources().getColor(R.color.main_warn_bg),
        		getResources().getColor(R.color.main_alarm_bg),
        		getResources().getColor(R.color.main_exit_bg)};
        
        icons = new int[]{R.drawable.o1,R.drawable.o2,R.drawable.o3,R.drawable.o4,R.drawable.o5,
                R.drawable.o6,R.drawable.o7,R.drawable.o8};
        gridView = (GridView) findViewById(R.id.main_operator_id);
        username = (TextView) findViewById(R.id.login_username_id);
        //从spf中取出登录用户名
        username.setText("欢迎您："+new SharedPreferencesUtils(MainActivity.this,Constants.SPF_NAME).getString(
        		Constants.LOGIN_USERNAME,""));
        //初始化菜单下标索引
        Utils.newInstance().initMenuPosition(getApplicationContext());
        //初始化gridview
        initGridView();
    }

    /**
     * 初始化gridview
     */
    private void initGridView(){
    	//判断屏幕方向
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){//竖屏
			gridView.setNumColumns(2);
		}else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){//横屏 
			gridView.setNumColumns(4);
		}
        SpecialAdapter simpleAdapter = new SpecialAdapter(this,getGridItemData(),R.layout.main_griditem,
                new String[]{TITLE,ICON},new int[]{R.id.title_id,R.id.icon_id});
        gridView.setAdapter(simpleAdapter);
        //设置griditem的点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.title_id);
                String itemText = textView.getText().toString();
                if(itemText.equalsIgnoreCase(getString(R.string.operate_8))){
                	Log.i(TAG, "用户:"+username.getText().toString()+"退出,"+Utils.newInstance().getDateString(
                			Constants.DATE_PATTERN_YYYYMMDD24HH));
                    //注销
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    //跳转到操作面板
                    Intent intent = new Intent(MainActivity.this, OperateMainActivity.class);
                    //保存选择的菜单索引
                    new SharedPreferencesUtils(MainActivity.this,Constants.SPF_NAME).putInt(Constants.MENU_POSITION,
                    		Utils.newInstance().getMenuPosition(itemText));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * 获取GridView Item数据
     * @return
     */
    private List<Map<String, Object>> getGridItemData() {
        List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= 8; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(TITLE, titles[i-1]);
            map.put(ICON, icons[i-1]);
            datas.add(map);
        }
        return datas;
    }

    /**
     * 自定义Grid适配器
     * gridview的item的背景色填充不上去，这里就自定义适配器，在getView中设置背景色
     */
    class SpecialAdapter extends SimpleAdapter{
        public SpecialAdapter(Context context, List<? extends Map<String, ?>> data,
                              int resource, String[] from, int[] to){
            super(context,data,resource,from,to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position,convertView,parent);
            //设置item的背景色，无法通过simpleadapter适配
            view.findViewById(R.id.bgcolor_id).setBackgroundColor(bgColors[position]);
            return view;
        }
    }

    /**
     * 监听设备状态改变
     */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){//竖屏
			gridView.setNumColumns(2);
		}else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){//横屏 
			gridView.setNumColumns(4);
		}
	}
}
