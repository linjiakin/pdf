package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.comtop.pdf.R;

public class TestActivity extends Activity {
	private ArrayList<String> titleList;
	private ViewPager viewPager;

	private int currIndex = 1;// 当前页卡编号
	private int ivCursorWidth;// 动画图片宽度
	private int tabWidth;// 每个tab头的宽度
	private int offsetX;// tab头的宽度减去动画图片的宽度再除以2（保证动画图片相对tab头居中）
	private ImageView ivCursor;// 下划线图片
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		ImageView imageView = (ImageView) findViewById(R.id.my_cursor);
		
		Animation a = AnimationUtils.loadAnimation(this, R.anim.rotate_repeat);
		imageView.setAnimation(a);

    	MyDialogLoading loading = new MyDialogLoading(this);
    	loading.show();
		
		/*TextView page1 = (TextView) findViewById(R.id.page1);
		TextView page2 = (TextView) findViewById(R.id.page2);
		TextView page3 = (TextView) findViewById(R.id.page3);
		page1.setOnClickListener(clickListener);

        //初始化页卡标题
        titleList = new ArrayList<String>();
        titleList.add("第一页");
        titleList.add("第2页");
        titleList.add("第三页");
        titleList.add("第4页");

        List viewList = new ArrayList<View>();

        *//**
         * 使用View对象做ViewPager的数据源
         * 将layout布局文件转换成view对象,用view对象去做viewpager的数据源
         *//*
        View view1 = View.inflate(this,R.layout.security_map_zy,null);
        View view2 = View.inflate(this,R.layout.security_map_by,null);
        View view3 = View.inflate(this,R.layout.security_map_dy,null);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);



        //获取viewPager对象
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //获取pagertabstrip对象
        PagerTabStrip tab = (PagerTabStrip) findViewById(R.id.tab);
        //为页卡标题PagerTabStrip设置文本颜色
        tab.setTextColor(Color.WHITE);
        //设置背景色
        tab.setBackgroundColor(Color.GRAY);
        //设置是否填充下划线，false不显示
        tab.setDrawFullUnderline(false);
        //设置当前标题下横线的颜色
        tab.setTabIndicatorColor(Color.GREEN);

        //实例化PagerAdapter适配器
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(viewList,titleList);

        //加载PagerAdapter适配器
        //viewPager.setAdapter(pagerAdapter);

        //加载FragmentPagerAdapter适配器
        *//**
         * 第一个参数是FragmentManager对象，这里不能像之前直接调用getFragmentManager()了，需要调用
         * getSupportFragmentManager(),那么我们的Activity就需要继承FragmentActivity
         *
         * 注意：其实如果应用不做3.0以下版本的兼容，是可以不用使用v4包下的FragmentManage，v4包下的Fragment
         * 主要是为了兼容3.0以下版本，因为Fragment是3.0才提出的，但是这里我们使用的ViewPager组件是v4包中才有的
         * 所有这里我们需要使用v4包中的FragmentManage
         *//*

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,titleList);
        viewPager.setAdapter(fragmentPagerAdapter);

        //注册页卡改变事件
        //viewPager.setOnPageChangeListener(this);
        
        viewPager.setAdapter(pagerAdapter);
        
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {

				Animation animation = new TranslateAnimation(tabWidth * currIndex
						+ offsetX, tabWidth * arg0 + offsetX, 0, 0);// 显然这个比较简洁，只有一行代码。
				currIndex = arg0;
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(350);
				ivCursor.startAnimation(animation);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});*/
	}
	
	class MyPagerAdapter extends PagerAdapter{


	    private List<View> viewList;

	    private List<String> titleList;

	    public MyPagerAdapter(List<View> viewList,List<String> titleList){
	        this.viewList = viewList;
	        this.titleList = titleList;
	    }

	    /**
	     * 返回的是ViewPager页卡的数量,就是所有view的数量，viewpager中的都是view对象
	     * @return
	     */
	    @Override
	    public int getCount() {
	        return viewList.size();
	    }

	    /**
	     * 判断当前的View对象是否来自o对象
	     * @param view
	     * @param o
	     * @return
	     */
	    @Override
	    public boolean isViewFromObject(View view, Object o) {
	        return view == o;
	    }

	    /**
	     * 实例化页卡(view对象)
	     * @param container
	     * @param position
	     * @return
	     */
	    @Override
	    public Object instantiateItem(ViewGroup container, int position) {
	        //根据position获取view对象
	        View view = viewList.get(position);
	        //将view对象添加到container中
	        container.addView(view);
	        //将view对象返回
	        return view;
	    }

	    /**
	     * 销毁页卡(view对象)
	     * @param container
	     * @param position
	     * @param object
	     */
	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        //从container中移除view
	        container.removeView(this.viewList.get(position));
	        //super.destroyItem(container, position, object); 需要注释掉
	    }

	    /**
	     * 设置ViewPager页卡的标题
	     * 注意：配置文件中一定要在ViewPager中申明PagerTabStrip或PagerTitleStrip
	     * @param position
	     * @return
	     */
	    /*@Override
	    public CharSequence getPageTitle(int position) {
	        return this.titleList.get(position);
	    }*/
	}
	
	/*OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.page1:
				viewPager.setCurrentItem(0);
				break;
			case R.id.page2:
				viewPager.setCurrentItem(1);
				break;
			case R.id.page3:
				viewPager.setCurrentItem(2);
				break;

			default:
				break;
			}
		}
	};*/
}
