package com.comtop.pdf.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.Util;
import org.videolan.libvlc.WeakHandler;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.SharedPreferencesUtils;
import com.comtop.pdf.utils.Utils;

/**
 * 视频监测
 * @author linyong
 *
 */
public class VideoFragment extends Fragment implements IVideoPlayer {

	protected static final String TAG = "VideoActivity/Vlc";
	private static final String LOG = "VideoFragment";

	private LibVLC mLibVLC = null;

	private SurfaceView surfaceView = null;
	private SurfaceHolder surfaceHolder = null;
	
	//步长
	private Spinner bcSpinner;
	//视频背景图
	private ImageView videoNoBg;
	//云台控制-上下左右
	private ImageView controlLeft,controlRight,controlUp,controlDown;
	//云台控制-聚焦等操作
	private Button controlBbUp,controlBbDown,controlJjUp,controlJjDown,controlGqUp,controlGqDown;
	//选择视频
	private EditText selectVideoId;

	private int mVideoHeight;
	private int mVideoWidth;
	private int mSarDen;
	private int mSarNum;
	private static final int SURFACE_SIZE = 3;
	private static final int SURFACE_BEST_FIT = 0;
	private static final int SURFACE_FIT_HORIZONTAL = 1;
	private static final int SURFACE_FIT_VERTICAL = 2;
	private static final int SURFACE_FILL = 3;
	private static final int SURFACE_16_9 = 4;
	private static final int SURFACE_4_3 = 5;
	private static final int SURFACE_ORIGINAL = 6;
	private int mCurrentSize = SURFACE_BEST_FIT;
    private String videoFunctionId;//选择视频的功能位置id
    private String videoPath;//选择视频的全路径
    private boolean isPlay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_video,container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();//初始化后台数据
		init();//组件的初始化操作要放在onStart中进行，onCreateView中无法获取
	}
	
	private void initData(){
		//获取配电房,从sdf中取出最近选择的配电房
		String houseInfo = new SharedPreferencesUtils(getActivity(), Constants.SPF_NAME).
				getString(Constants.SELECTED_VIDEOINFO, "");
		if(null == houseInfo || "".equalsIgnoreCase(houseInfo)){
			Log.e(LOG, "没有从sdf中获取到视频信息");
			return;
		}
		try {
			JSONObject houseObj = new JSONObject(houseInfo);
			videoPath = houseObj.getString(Constants.VIDEO_PATH);
			videoFunctionId = houseObj.getString(Constants.VIDEO_FUNCTIONID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		initVideo();
		initCom();
	}

	/**
	 * 初始化下拉框
	 */
	private void initCom() {
		bcSpinner = (Spinner) getActivity().findViewById(R.id.bc_text_id);
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(),R.layout.spinner_item, new Integer[]{1,2,3,4,5,6});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bcSpinner.setAdapter(adapter);
	}

	/**
	 * 初始化视频相关控件
	 */
	private void initVideo() {
		videoNoBg = (ImageView) getActivity().findViewById(R.id.video_no_bg_Id);
		controlLeft = (ImageView) getActivity().findViewById(R.id.control_left_id);
		controlRight = (ImageView) getActivity().findViewById(R.id.control_right_id);
		controlUp = (ImageView) getActivity().findViewById(R.id.control_up_id);
		controlDown = (ImageView) getActivity().findViewById(R.id.control_down_id);
		controlBbUp = (Button) getActivity().findViewById(R.id.control_bb_up_id);
		controlBbDown = (Button) getActivity().findViewById(R.id.control_bb_down_id);
		controlJjUp = (Button) getActivity().findViewById(R.id.control_jj_up_id);
		controlJjDown = (Button) getActivity().findViewById(R.id.control_jj_down_id);
		controlGqUp = (Button) getActivity().findViewById(R.id.control_gq_up_id);
		controlGqDown = (Button) getActivity().findViewById(R.id.control_gq_down_id);
		
		controlLeft.setOnClickListener(videoControlListener);
		controlRight.setOnClickListener(videoControlListener);
		controlUp.setOnClickListener(videoControlListener);
		controlDown.setOnClickListener(videoControlListener);
		
		controlBbUp.setOnClickListener(videoControlListener);
		controlBbDown.setOnClickListener(videoControlListener);
		controlJjUp.setOnClickListener(videoControlListener);
		controlJjDown.setOnClickListener(videoControlListener);
		controlGqUp.setOnClickListener(videoControlListener);
		controlGqDown.setOnClickListener(videoControlListener);

		//选择配电房视频
		selectVideoId = (EditText) getActivity().findViewById(R.id.select_video_id);
		selectVideoId.setText(videoPath);
		selectVideoId.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//启动选择视频弹出框 
				Intent intent = new Intent(getActivity(),ChoiceVideoDialog.class);
				//将当前的视频路径发送过去用作路径构建
				//intent.putExtra("videoPath", selectVideoId.getText().toString());
				//启用回调模式
				startActivityForResult(intent, Constants.REQUEST_CHOICE_VIDEO_CODE);
			}
		});
		
		surfaceView = (SurfaceView) getActivity().findViewById(R.id.main_surface);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setFormat(PixelFormat.RGBX_8888);
		surfaceHolder.addCallback(mSurfaceCallback);
		EventHandler em = EventHandler.getInstance();
		em.addHandler(handler);
		try {
			mLibVLC = Util.getLibVlcInstance();
		} catch (LibVlcException e) {
			e.printStackTrace();
		}
		
		if(videoFunctionId != null && !"".equalsIgnoreCase(videoFunctionId)){
			//play(videoFunctionId);
		}
	}
	
	/**
	 * 接收选择的视频路径
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Constants.REQUEST_CHOICE_VIDEO_CODE && requestCode == Constants.REQUEST_CHOICE_VIDEO_CODE){
			//解析获取到的视频路径，用于进行视频加载
			selectVideoId.setText(data.getExtras().getString(Constants.VIDEO_PATH));
			videoFunctionId = data.getExtras().getString(Constants.VIDEO_FUNCTIONID);
			//视频功能位置
			play(videoFunctionId);
		}
	}
	
	//传入视频的功能位置id
	private void play(final String videoFunctionId){
	    Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject obj = new JSONObject();
				try {
					obj.put("functionId", videoFunctionId);
					final JSONObject result = Utils.newInstance().getVideoRtsp(obj);
					if(result != null){
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									playVideo(result.getString("playUrl"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				} catch (Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getActivity(), "播放失败", Toast.LENGTH_SHORT).show();
						}
					});
					e.printStackTrace();
				}
			}
		});
	    thread.start();
	}

	//播放视频
	public void playVideo(String rtspUrl) {
		//将背景图变成加载中字样
		setVideoBg(R.drawable.video_load);
		
		if (mLibVLC != null) {
			String pathUri = rtspUrl;//"rtsp://10.10.61.5:7008/CAM";// 获取选择的视频地址
			mLibVLC.playMyMRL("rtsp://10.0.80.214:8554/test");
		}else{
			//Toast.makeText(getActivity(), "播放失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 设置视频背景图
	 */
	public void setVideoBg(int resource){
		videoNoBg.setImageResource(resource);
		if(resource == R.drawable.video_load){
			videoNoBg.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_repeat));
		}else{
			videoNoBg.setAnimation(null);
		}
	}
	
	//云台控制
	OnClickListener videoControlListener = new OnClickListener(){
		@Override
		public void onClick(final View v) {
			if(!isPlay){//还没播放视频
				return;
			}
		    Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
		        	videoControl(v);
				}
			});
		    thread.start();
		}
	};
	
	/**
	 * 云台控制
	 * @param view
	 */
	public void videoControl(View view){
		//操作类型 1云台2变焦3变倍
		int oType = -1;
		//控制类型对应操作类型1时： 1启动控制 2停止控制 ； 对应操作类型2、3时：1变大 2变小
		int cType = -1;
		//步长
		int step = Integer.valueOf(bcSpinner.getSelectedItem().toString());
		//操作量
		int cVal = -1;
		
		switch(view.getId()){
		case R.id.control_up_id:
			oType = 1;
			cType = 1;//启动控制
			cVal = 1;
			break;
		case R.id.control_down_id:
			oType = 1;
			cType = 1;//启动控制
			cVal = 2;
			break;
		case R.id.control_left_id:
			oType = 1;
			cType = 1;//启动控制
			cVal = 3;
			break;
		case R.id.control_right_id:
			oType = 1;
			cType = 1;//启动控制
			cVal = 4;
			break;
		case R.id.control_bb_up_id:
			oType = 3;
			cType = 1;
			break;
		case R.id.control_bb_down_id:
			oType = 3;
			cType = 2;
			break;
		case R.id.control_jj_up_id:
			oType = 2;
			cType = 1;
			break;
		case R.id.control_jj_down_id:
			oType = 2;
			cType = 2;
			break;
		case R.id.control_gq_up_id:
			oType = 4;
			cType = 1;
			break;
		case R.id.control_gq_down_id:
			oType = 4;
			cType = 2;
			break;
		}
		
		try {
			JSONObject obj = new JSONObject();
			obj.put("cameraId", videoFunctionId);//获取选中的摄像头功能位置id
			obj.put("oType", oType);
			obj.put("cType", cType);
			obj.put("step", step);
			obj.put("cVal", cVal);
			//{"iResult":-1,"errorMsg":"请求类型不匹配：1云台2变焦3变倍","lstResult":[],"cameraId":null,"iState":0,"oType":0,"cType":0,"step":0,"cVal":0,"playUrl":null}
			Utils.newInstance().send(Constants.VIDEO_CONTROL_URL, obj.toString(), Utils.POST);
		} catch (Exception e) {
			Toast.makeText(getActivity(), "云台控制失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerPlaying:
				break;
			case EventHandler.MediaPlayerPaused:
				break;
			case EventHandler.MediaPlayerStopped:
				break;
			case EventHandler.MediaPlayerEndReached:
				break;
			case EventHandler.MediaPlayerVout:
				//播放成功，清除背景图
				setVideoBg(-1);
				isPlay = true;
				if (msg.getData().getInt("data") > 0) {
					if (mLibVLC != null) {
						EventHandler em = EventHandler.getInstance();
						em.addHandler(eventHandler);
						handler.sendEmptyMessageDelayed(0, 1000);
						//ListenRecording();
					}
				}
				break;
			case EventHandler.MediaPlayerPositionChanged:
				break;
			case EventHandler.MediaPlayerEncounteredError:
				//处理播放失败信息
				setVideoBg(R.drawable.video_alert);
				Toast.makeText(getActivity(), "播放失败", Toast.LENGTH_SHORT).show();
				/*AlertDialog dialog = new AlertDialog.Builder(getActivity())
						.setTitle("提示信息")
						.setMessage("无法连接到摄像头，请确保手机已经连接到摄像头所在的wifi热点")
						.setNegativeButton("知道了",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										getActivity().finish();
									}
								}).create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();*/
				break;
			default:
				Log.d(TAG, "Event not handled ");
				break;
			}
		}
	};

	/**
	 * attach and disattach surface to the lib
	 */
	private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (format == PixelFormat.RGBX_8888)
				Log.d(TAG, "Pixel format is RGBX_8888");
			else if (format == PixelFormat.RGB_565)
				Log.d(TAG, "Pixel format is RGB_565");
			else if (format == ImageFormat.YV12)
				Log.d(TAG, "Pixel format is YV12");
			else
				Log.d(TAG, "Pixel format is other/unknown");
			mLibVLC.attachSurface(holder.getSurface(), VideoFragment.this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mLibVLC.detachSurface();
		}
	};

	public final Handler mHandler = new VideoPlayerHandler(this);

	private static class VideoPlayerHandler extends
			WeakHandler<VideoFragment> {
		public VideoPlayerHandler(VideoFragment owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoFragment activity = getOwner();
			if (activity == null) // WeakReference could be GC'ed early
				return;

			switch (msg.what) {
			case SURFACE_SIZE:
				activity.changeSurfaceSize();
				break;
			}
		}
	};
	

	private final Handler eventHandler = new VideoPlayerEventHandler(this);

	private static class VideoPlayerEventHandler extends
			WeakHandler<VideoFragment> {
		public VideoPlayerEventHandler(VideoFragment owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoFragment activity = getOwner();
			if (activity == null)
				return;
			Log.d(TAG, "Event = "+msg.getData().getInt("event"));
			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerPlaying:
				Log.i(TAG, "MediaPlayerPlaying");
				break;
			case EventHandler.MediaPlayerPaused:
				Log.i(TAG, "MediaPlayerPaused");
				break;
			case EventHandler.MediaPlayerStopped:
				Log.i(TAG, "MediaPlayerStopped");
				break;
			case EventHandler.MediaPlayerEndReached:
				Log.i(TAG, "MediaPlayerEndReached");
				//activity.getActivity().finish();
				break;
			case EventHandler.MediaPlayerVout:
				//activity.getActivity().finish();
				break;
			default:
				Log.d(TAG, "Event not handled");
				break;
			}
			// getActivity().updateOverlayPausePlay();
		}
	}

	private void changeSurfaceSize() {
		// get screen size
		int dw = getActivity().getWindow().getDecorView().getWidth();
		int dh = getActivity().getWindow().getDecorView().getHeight();

		// getWindow().getDecorView() doesn't always take orientation into
		// account, we have to correct the values
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		if (dw > dh && isPortrait || dw < dh && !isPortrait) {
			int d = dw;
			dw = dh;
			dh = d;
		}
		if (dw * dh == 0)
			return;
		// compute the aspect ratio
		double ar, vw;
		double density = (double) mSarNum / (double) mSarDen;
		if (density == 1.0) {
			/* No indication about the density, assuming 1:1 */
			vw = mVideoWidth;
			ar = (double) mVideoWidth / (double) mVideoHeight;
		} else {
			/* Use the specified aspect ratio */
			vw = mVideoWidth * density;
			ar = vw / mVideoHeight;
		}

		// compute the display aspect ratio
		double dar = (double) dw / (double) dh;

		// // calculate aspect ratio
		// double ar = (double) mVideoWidth / (double) mVideoHeight;
		// // calculate display aspect ratio
		// double dar = (double) dw / (double) dh;

		switch (mCurrentSize) {
		case SURFACE_BEST_FIT:
			//mTextShowInfo.setText(R.string.video_player_best_fit);
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_FIT_HORIZONTAL:
			//mTextShowInfo.setText(R.string.video_player_fit_horizontal);
			dh = (int) (dw / ar);
			break;
		case SURFACE_FIT_VERTICAL:
			//mTextShowInfo.setText(R.string.video_player_fit_vertical);
			dw = (int) (dh * ar);
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			//mTextShowInfo.setText(R.string.video_player_16x9);
			ar = 16.0 / 9.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_4_3:
			//mTextShowInfo.setText(R.string.video_player_4x3);
			ar = 4.0 / 3.0;
			if (dar < ar)
				dh = (int) (dw / ar);
			else
				dw = (int) (dh * ar);
			break;
		case SURFACE_ORIGINAL:
			//mTextShowInfo.setText(R.string.video_player_original);
			dh = mVideoHeight;
			dw = mVideoWidth;
			break;
		}

		surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
		LayoutParams lp = surfaceView.getLayoutParams();
		lp.width = dw;
		lp.height = dh;
		surfaceView.setLayoutParams(lp);
		surfaceView.invalidate();
	}

	@Override
	public void onDestroy() {
		EventHandler em = EventHandler.getInstance();
		em.removeHandler(handler);
		super.onDestroy();
	}

	public void setSurfaceSize(int width, int height, int sar_num, int sar_den) {
		if (width * height == 0)
			return;

		mVideoHeight = height;
		mVideoWidth = width;
		mSarNum = sar_num;
		mSarDen = sar_den;
		Message msg = mHandler.obtainMessage(SURFACE_SIZE);
		mHandler.sendMessage(msg);
	}

	@Override
	public void setSurfaceSize(int width, int height, int visible_width,
			int visible_height, int sar_num, int sar_den) {
		mVideoHeight = height;
		mVideoWidth = width;
		mSarNum = sar_num;
		mSarDen = sar_den;
		Message msg = mHandler.obtainMessage(SURFACE_SIZE);
		mHandler.sendMessage(msg);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//getActivity().finish();
			break;
		}
		return true;
	}
}
