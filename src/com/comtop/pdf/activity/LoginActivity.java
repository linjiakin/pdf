package com.comtop.pdf.activity;

import java.text.MessageFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.comtop.pdf.R;
import com.comtop.pdf.utils.Constants;
import com.comtop.pdf.utils.SharedPreferencesUtils;
import com.comtop.pdf.utils.Utils;

/**
 * 登录
 * @author linyong
 */
public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//设置窗体没有标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * 登录点击事件
     * @param view
     */
    public void login(View view) {
    	//在工作线程中处理网络请求，避免堵塞ui进程
        new Thread(loginRunnable).start();
    }

    //登录线程
    Runnable loginRunnable = new Runnable() {
        public void run() {
            EditText username = (EditText) findViewById(R.id.login_username);
            EditText pwd = (EditText) findViewById(R.id.login_pwd);
            if(username.getText().toString().equals("")){
                showMsg("请输入用户名");
                return;
            }if(pwd.getText().toString().equals("")){
                showMsg("请输入密码");
                return;
            }
            String url = MessageFormat.format(Constants.LOGIN_URL, username.getText(),pwd.getText());
            String loginResult = "-1";
			/*try {
				loginResult = new JSONObject(Utils.newInstance().send(url, null, "GET")).getString("iResult");
			} catch (Exception e) {
                showMsg("登录异常");
				e.printStackTrace();
				return;
			}*/
            loginResult = "1";//测试代码
            if(loginResult != null && loginResult.equalsIgnoreCase("1")){
            	Log.i(TAG, "用户:"+username.getText().toString()+"登录,"+Utils.newInstance().getDateString(
            			Constants.DATE_PATTERN_YYYYMMDD24HH));
                //将登录用户存储起来
                new SharedPreferencesUtils(LoginActivity.this,Constants.SPF_NAME).putString(
                		Constants.LOGIN_USERNAME,username.getText().toString());
                //跳转到主页面
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                //提示失败,在工作线程中不能直接使用Toast(工作线程不能操作任何组件),可以调用activity的runOnUiThread方法
                showMsg("登录失败,请检查用户名或密码");
            }
        }
    };

    /**
     * 显示信息
     * @param msg
     */
    private void showMsg(final String msg){
        runOnUiThread(new Runnable(){
            public void run(){
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
