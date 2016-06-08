package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.SharedPreferencesUtils;
import com.comtop.pdf.utils.Utils;

public class ChoiceVideoDialog extends Activity {
	private static final String LOG = "ChoiceVideoDialog";

    private ListView listview;
    private Button preBtn;
    private Button nextBtn;
    private Button okBtn;
    private ListViewAdapter adapter ;
    //存储每层数据
    private Map<Integer,JSONObject> levelData = new HashMap<Integer,JSONObject>();
    //存储当前选中状态
    private Map<Integer,Integer> radioCheckedStatus = new HashMap<Integer,Integer>();
    //listview数据
    private List<Map<String,String>> listviewData = new ArrayList<Map<String,String>>();
    //用于回滚数据
    private List<Map<String,String>> listviewBackData = new ArrayList<Map<String,String>>();
    //记录当前层级
    private int level = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_dialog);
        init();
        initData();
    }
    
    /**
     * 加载数据
     */
    private void initData(){
		String videoInfo = new SharedPreferencesUtils(this, Constants.SPF_NAME).
				getString(Constants.SELECTED_VIDEOINFO, "");
		if(null == videoInfo || "".equalsIgnoreCase(videoInfo)){
			Log.e(LOG, "没有从sdf中获取到视频节点信息");
			return;
		}
		try {
			JSONObject houseObj = new JSONObject(videoInfo);
			JSONObject allLevelObj = new JSONObject(houseObj.get(Constants.VIDEO_ALLLEVELOBJ).toString());
			//设置存储每层数据
			levelData.clear();
			for(int i = 1;i<=allLevelObj.length() ;i++){
				levelData.put(i, new JSONObject(allLevelObj.get(String.valueOf(i)).toString()));
			}
			setBtnVisiable(allLevelObj.length());
			getVideoData(levelData.get(allLevelObj.length()-1).getString("functionId"));
			//设置层级标识
			level = allLevelObj.length();
			//设置配电房选中
			radioCheckedStatus.clear();
			radioCheckedStatus.put(levelData.get(allLevelObj.length()).getInt("itemPosition"),1);
		} catch (JSONException e) {
			Log.e(LOG, "加载数据失败,"+e);
			e.printStackTrace();
		}
    }


    //初始化控件
    private void init(){
        listview = (ListView) findViewById(R.id.listview_id);
        preBtn = (Button) findViewById(R.id.pre_btn_id);
        nextBtn = (Button) findViewById(R.id.next_btn_id);
        okBtn = (Button) findViewById(R.id.ok_btn_id);
        preBtn.setOnClickListener(clickListener);
        nextBtn.setOnClickListener(clickListener);
        okBtn.setOnClickListener(clickListener);
        listview.setOnItemClickListener(itemClickListener);
        initListView();
    }
    
    //获取区局数据
    private List<Map<String,String>> getRegionData(){
    	backData(listviewData);
    	listviewData.clear();
    	try {
			JSONArray regions = Utils.newInstance().getArchiveInfoByNodeLevel(this, 1);
			if(regions != null && regions.length()>0){
				for(int i=0;i<regions.length();i++){
		            Map<String,String> map = new HashMap<String,String>();
		            map.put("itemText",regions.getJSONObject(i).getString("nodeTitle"));
		            map.put("functionId",regions.getJSONObject(i).getString("functionId"));
					listviewData.add(map);
				}
			}else{
				Toast.makeText(this, "获取区局数据异常", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return listviewData;
    }

    //获取区局下的片区数据
    private List<Map<String,String>> getAreaData(String regionFunctionId){
    	backData(listviewData);
    	listviewData.clear();
    	try {
			JSONArray regions = Utils.newInstance().getArchiveInfoByFatherFunctionId(this, regionFunctionId);
			if(regions != null && regions.length()>0){
				for(int i=0;i<regions.length();i++){
		            Map<String,String> map = new HashMap<String,String>();
		            map.put("itemText",regions.getJSONObject(i).getString("nodeTitle"));
		            map.put("functionId",regions.getJSONObject(i).getString("functionId"));
					listviewData.add(map);
				}
			}else{
				Toast.makeText(this, "获取片区数据异常", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return listviewData;
    }

    //获取片区下的配电房数据
    private List<Map<String,String>> getHouseData(String areaFunction){
    	backData(listviewData);
    	listviewData.clear();
    	try {
			JSONArray regions = Utils.newInstance().getArchiveInfoByFatherFunctionId(this, areaFunction);
			if(regions != null && regions.length()>0){
				for(int i=0;i<regions.length();i++){
		            Map<String,String> map = new HashMap<String,String>();
		            map.put("itemText",regions.getJSONObject(i).getString("nodeTitle"));
		            map.put("functionId",regions.getJSONObject(i).getString("functionId"));
					listviewData.add(map);
				}
			}else{
				Toast.makeText(this, "获取配电房数据异常", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return listviewData;
    }
    
    //获取配电房下的视频数据
    private List<Map<String,String>> getVideoData(String houseFunctionId){
    	listviewData.clear();
    	try {
			JSONArray regions = Utils.newInstance().getArchiveInfoByFatherFunctionId(this, houseFunctionId,1);
			if(regions != null && regions.length()>0){
				for(int i=0;i<regions.length();i++){
		            Map<String,String> map = new HashMap<String,String>();
		            map.put("itemText",regions.getJSONObject(i).getString("nodeTitle"));
		            map.put("functionId",regions.getJSONObject(i).getString("functionId"));
					listviewData.add(map);
				}
			}else{
				Toast.makeText(this, "获取视频数据异常", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return listviewData;
    }
    
    private void backData(List<Map<String,String>> listviewData){
    	listviewBackData.clear();
    	listviewBackData.addAll(listviewData);
    }
    
    //设置listview适配器
    private void initListView(){
        adapter = new ListViewAdapter(this, getRegionData());
        listview.setAdapter(adapter);
        
    }
    
    //获取listview中选中的item
    private LinearLayout getCheckedItem(){
    	int count = listview.getChildCount();
    	LinearLayout layout = null;
    	boolean flag = false;
    	for(int  i = 0;i<count;i++){
    		layout = (LinearLayout) listview.getChildAt(i);
    		flag = ((RadioButton)layout.findViewById(R.id.radio_id)).isChecked();
    		if(!flag){layout = null;}else{break;}
    	}
    	return layout;
    }
    
    //封装item
    private JSONObject wrapItem(String itemText,int itemPosition,String functionId){
    	JSONObject obj = new JSONObject();
		try {
			obj.put("itemText", itemText);
			obj.put("itemPosition", itemPosition);
			obj.put("functionId", functionId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return obj;
    }
    
    //设置状态的可见性
    private void setBtnVisiable(int level){
    	preBtn.setVisibility(View.GONE);
    	nextBtn.setVisibility(View.GONE);
    	okBtn.setVisibility(View.GONE);
    	switch (level) {
		case 1:
	    	nextBtn.setVisibility(View.VISIBLE);
			break;
		case 2:
		case 3:
	    	preBtn.setVisibility(View.VISIBLE);
	    	nextBtn.setVisibility(View.VISIBLE);
			break;
		case 4:
	    	preBtn.setVisibility(View.VISIBLE);
	    	okBtn.setVisibility(View.VISIBLE);
			break;
		}
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            radioCheckedStatus.clear();
            radioCheckedStatus.put(i, 1);
            adapter.notifyDataSetChanged();
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	//获取选中的item
        	LinearLayout itemLayout = getCheckedItem();
        	if(itemLayout == null && view.getId() != R.id.pre_btn_id){
        		Toast.makeText(ChoiceVideoDialog.this, "请选择项目", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	String itemText = "";
        	String functionId ="";
        	int itemPosition = -1;
        	if(itemLayout != null){
	        	//选中的item文本
	        	itemText = ((TextView)itemLayout.findViewById(R.id.itemText_id)).getText().toString();
	        	//选中的item下标索引
	        	itemPosition = Integer.valueOf(((TextView)itemLayout.findViewById(R.id.item_position_id)).getText().toString());
	        	functionId = ((TextView)itemLayout.findViewById(R.id.function_id)).getText().toString();
        	}
            switch(view.getId()){
                case R.id.next_btn_id:
                	switch (level) {
						case 1:
							//加载区局下的片区信息
			        		getAreaData(functionId);
							break;
						case 2:
							//加载片区下的配电房信息
			        		getHouseData(functionId);
							break;
						case 3:
							//加载配电房下的视频信息
			        		getVideoData(functionId);
							break;
					}
                	if(listviewData.size() == 0){
                		listviewData.addAll(listviewBackData);
                		return;
                	}
                	adapter.notifyDataSetChanged();
	        		//保存当前节点信息
	        		levelData.put(level, wrapItem(itemText,itemPosition,functionId));
	        		//清楚radio选中状态
	        		radioCheckedStatus.clear();//如果缓存中有下一层信息，则要选中
	        		//升级
	        		level++;
                    break;
                case R.id.pre_btn_id:
					try {
						switch (level) {
							case 2:
								//加载区局
				        		getRegionData();
								break;
							case 3:
								//加载片区
								getAreaData(levelData.get(level-2).get("functionId").toString());
								break;
							case 4:
								//加载配电房
								getHouseData(levelData.get(level-2).get("functionId").toString());
								break;
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
                	//保存当前节点信息
					if(itemPosition != -1 && !itemText.equalsIgnoreCase("")){
						levelData.put(level, wrapItem(itemText,itemPosition,functionId));								
					}
	        		//清楚radio选中状态
	        		radioCheckedStatus.clear();
	        		//获取上一层item选中的索引
                	JSONObject obj = levelData.get(level-1);
	        		if(obj != null){
	        			try {
							radioCheckedStatus.put(obj.getInt("itemPosition"),1);
						} catch (JSONException e) {
							e.printStackTrace();
						}
	        		}
	        		level--;
	        		adapter.notifyDataSetChanged();
                    break;
                case R.id.ok_btn_id:
	        		levelData.put(level, wrapItem(itemText,itemPosition,functionId));
                	int size = levelData.keySet().size();
                	if(size >0){
                		String videoPath = "";
                		String videoFunctionId = "";
                		JSONObject selectedObj = new JSONObject();//所有层级节点信息
                		try {
							for(int i =1;i<=levelData.size();i++){
								videoPath += levelData.get(i).getString("itemText")+"/";
								selectedObj.put(String.valueOf(i), levelData.get(i));
							}
							videoPath = videoPath.substring(0,videoPath.lastIndexOf("/"));
							videoFunctionId = levelData.get(levelData.size()).getString("functionId");
						} catch (JSONException e) {
							e.printStackTrace();
						}
                		Intent intent = getIntent();
                		intent.putExtra("videoPath", videoPath);
                		intent.putExtra("videoFunctionId", videoFunctionId);
                		setResult(Constants.REQUEST_CHOICE_VIDEO_CODE, intent);
                		saveVideoInfoBySdf(videoPath, videoFunctionId, selectedObj.toString());
                		finish();
                	}
                    break;
            }
            setBtnVisiable(level);
        }
    };

    class ListViewItemWrap{
    	private RadioButton radioButton;
    	private TextView itemText;
    	private TextView itemPosition;
    	private TextView functionId;
    	private TextView fatherFunctionId;
    }

    class ListViewAdapter extends BaseAdapter{
    	
    	private List<Map<String,String>> datas;
    	private Context context;
        public ListViewAdapter(Context context,List<Map<String,String>> datas){
        	this.context = context;
        	this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
        	
        	ListViewItemWrap itemWrap;
        	if(view == null){
        		view = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
        		itemWrap = new ListViewItemWrap();
        		itemWrap.radioButton = (RadioButton) view.findViewById(R.id.radio_id);
        		itemWrap.itemText = (TextView)view.findViewById(R.id.itemText_id);
        		itemWrap.itemPosition = (TextView)view.findViewById(R.id.item_position_id);
        		itemWrap.functionId =  (TextView)view.findViewById(R.id.function_id);
        		itemWrap.fatherFunctionId =  (TextView)view.findViewById(R.id.father_function_id);
        		view.setTag(itemWrap);
        	}else{
        		itemWrap = (ListViewItemWrap) view.getTag();
        	}
        	if(datas.get(i) != null){
        		itemWrap.itemText.setText(datas.get(i).get("itemText"));
        		itemWrap.functionId.setText(datas.get(i).get("functionId"));
        		itemWrap.fatherFunctionId.setText(datas.get(i).get("fatherFunctionId"));
        	}
        	itemWrap.radioButton.setChecked((radioCheckedStatus.get(i) != null));
        	itemWrap.itemPosition.setText(String.valueOf(i));
        	return view;
        }
    }
    

    /**
     * 将选中的视频相关信息保存到sdf中
     * @param selectedHousePath 选择视频的全路径
     * @param selectedHouseFunctionId 选择视频的功能位置id
     * @param selectedAllLevelObject 所有层级相关信息
     */
    private void saveVideoInfoBySdf(String selectedVideoPath,String selectedVideoFunctionId,String selectedAllLevelObject){
		JSONObject videoInfo = new JSONObject();
		try {
			videoInfo.put(Constants.VIDEO_PATH, selectedVideoPath);
			videoInfo.put(Constants.VIDEO_FUNCTIONID, selectedVideoFunctionId);
			videoInfo.put(Constants.VIDEO_ALLLEVELOBJ, selectedAllLevelObject);
    		new SharedPreferencesUtils(ChoiceVideoDialog.this, Constants.SPF_NAME).
    			putString(Constants.SELECTED_VIDEOINFO, videoInfo.toString());
			Log.e(LOG, "将选中的视频相关信息保存到sdf中成功"+selectedVideoPath);
		} catch (JSONException e) {
			Log.e(LOG, "将选中的视频相关信息保存到sdf中保存失败,"+e);
			e.printStackTrace();
		}
    }
}
