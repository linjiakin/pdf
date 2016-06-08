package com.comtop.pdf.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.comtop.pdf.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * 自定义横向滚动视图
 * 用于实现抽屉式菜单功能
 * @author linyong
 */
public class MyHorizontalScrollView extends HorizontalScrollView{

	//scroll中唯一容器布局
    private LinearLayout mWrapper;
    //菜单布局
    private ViewGroup mMenu;
    //内容布局
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mMenuRightPadding;
    private int mMenuWidth;
    //是否第一次加载
    private boolean once ;
    //菜单是否打开
    private boolean isOpen;

    /**
     * 未使用自定义属性时候调用
     * @param context
     * @param attrs
     */
    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当使用了自定义属性时，调用此方法
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        //获取自定义的属性集合
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyHorizontalScrollView,defStyle,0);
        //获取所有自定义属性数量
        int n = a.getIndexCount();
        for(int i = 0;i<n;i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.MyHorizontalScrollView_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;

        //将50dp转换成像素值
        //把dp转换成px
        //mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,dm);
    }

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    /**
     * 设置子view的宽和高，设置自己的宽和高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once) {
            mWrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 通过设置偏移量，将menu隐藏
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);//注意：要先执行父方法
        if(changed) {
            this.scrollTo(mMenuWidth, 0);
        }
    }

    /**
     * touch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
        	//手指离开屏幕事件
            case MotionEvent.ACTION_UP:
                //获取当前x的坐标
                int scrollX = getScrollX();
                //如果当前x所处坐标小于菜单宽度的一半，则隐藏菜单
                if(scrollX >= mMenuWidth / 2 ){
                    //注意：这里直接调用smoothscrollto方法无效。
                    //this.smoothScrollTo(mMenuWidth,0);
                    this.post(new Runnable() {
                        @Override
                        public void run() {
                            smoothScrollTo(mMenuWidth,0);
                        }
                    });//隐藏菜单
                    isOpen = false;
                }else{//否则显示菜单
                    this.post(new Runnable() {
                        @Override
                        public void run() {
                            smoothScrollTo(0,0);
                        }
                    });//显示菜单
                    isOpen = true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    //打开菜单
    public void openMenu(){
        if(!isOpen) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(0, 0);
                }
            });
            isOpen = true;
        }
    }

    //关闭菜单
    public void closeMenu(){
        if(isOpen){
            this.post(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(mMenuWidth,0);
                }
            });
            isOpen = false;
        }
    }

    //折叠菜单
    public void toggle(){
        if(isOpen){
            closeMenu();
        }else{
            openMenu();
        }
    }
    
    /**
     * 根据指定参数选择打开还是关闭菜单
     * @param isOpen
     */
    public void changeMenuStatus(boolean isOpen){
    	if(isOpen){
    		openMenu();
    	}else{
    		closeMenu();
    	}
    }

    /**
     * 布局滚动时触发
     * @param l 横向滚动后的起点值
     * @param t 纵向滚动后的起点值
     * @param oldl 横向滚动前的起点值
     * @param oldt 纵向滚动前的起点值
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //计算偏移量
        float scale = l * 1.0f / mMenuWidth;//1 ~ 0

        //使用动画帮助类，使得菜单面板根据拖动变化,就是根据当前横向滚动的起点值实时更新菜单面板的坐标，使其看起来好像是隐藏在类容面板的后面。
        //设置菜单面板x起点
        //ViewHelper.setTranslationX(mMenu, l);//如果只是显示抽屉式菜单，这个代码就可以了
        ViewHelper.setTranslationX(mMenu,mMenuWidth * scale * 0.3f);

        //设置内容面板在x/y方向缩放
        float rightScale = 0.7f + 0.3f * scale;
        //ViewHelper.setScaleX(mContent,rightScale);
        ViewHelper.setScaleY(mContent,rightScale);

        //设置菜单面板在x/y方向缩放
        //设置缩放偏移量
        float leftScale = 1.0f - scale * 0.3f;
        ViewHelper.setScaleX(mMenu,leftScale);
        ViewHelper.setScaleY(mMenu,leftScale);
        //设置透明度
        float leftAlpha = 0.6f + 0.4f * (1-scale);
        ViewHelper.setAlpha(mMenu,leftAlpha);
    }
    
    /**
     * 一定要覆盖这个方法，不然和webview有冲突,莫名其妙
     */
    @Override
    public void requestChildFocus(View child, View focused) {
    	//super.requestChildFocus(child, focused);
    }
}
