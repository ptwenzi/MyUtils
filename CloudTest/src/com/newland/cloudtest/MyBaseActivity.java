package com.newland.cloudtest;


import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class MyBaseActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState); 
		MyApplication app = (MyApplication) getApplication();//获取应用程序全局的实例引用
        app.activities.add(this);    //把当前Activity放入集合中
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication app = (MyApplication) getApplication();//获取应用程序全局的实例引用
		app.activities.remove(this);
	}
	
	
	
	
}
