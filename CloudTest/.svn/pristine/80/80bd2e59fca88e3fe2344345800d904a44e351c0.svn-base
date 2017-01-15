package com.newland.cloudtest;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.newland.cloudtest.R.id;
import com.newland.mobileterminalmonitor.AppActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import de.greenrobot.event.EventBus;

public class TabHostMainActivity extends TabActivity  {
	private TabHost tabHost;

	private Context mContext = null;
	private View tabcontent;
	private String currentTag="3";
	
	 @ViewInject(R.id.jc_iv)
	 private ImageView jc_iv;
	 @ViewInject(R.id.jc_tv)
	 private TextView jc_tv;
	 @ViewInject(R.id.msg_iv)
	 private ImageView msg_iv;
	 @ViewInject(R.id.msg_tv)
	 private TextView msg_tv;
	 @ViewInject(R.id.about_iv)
	 private ImageView about_iv;
	 @ViewInject(R.id.about_tv)
	 private TextView about_tv;
	 @ViewInject(R.id.phone_iv)
	 private ImageView phone_iv;
	 @ViewInject(R.id.phone_tv)
	 private TextView phone_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		statusBar();
		setContentView(R.layout.tabhost_main);
		x.view().inject(this);
		tabHost = this.getTabHost();
		mContext=this;
		tabcontent = findViewById(R.id.content);
		Intent intent1 = new Intent(this, MainActivity.class);
		Intent intent2 = new Intent(this, DebugActivit.class);
		Intent intent3 = new Intent(this, AppActivity.class);
		Intent intent4 = new Intent(this, AboutActivity.class);
		

		tabHost.addTab(tabHost.newTabSpec("1").setIndicator("1")
				.setContent(intent1));
		tabHost.addTab(tabHost.newTabSpec("2").setIndicator("2")
				.setContent(intent2));
		tabHost.addTab(tabHost.newTabSpec("3").setIndicator("3")
				.setContent(intent3));
		tabHost.addTab(tabHost.newTabSpec("4").setIndicator("4")
				.setContent(intent4));
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@SuppressLint("NewApi")
	public void reloadJc(View view)
	{
		chearStyle();
		tabHost.setCurrentTabByTag("1");
		jc_iv.setBackground(getResources().getDrawable(R.drawable.jc_pre));
		jc_tv.setTextColor(this.getResources().getColor(R.color.font_blue));
	}
	@SuppressLint("NewApi")
	public void reloadMsg(View view)
	{
		chearStyle();
		msg_iv.setBackground(getResources().getDrawable(R.drawable.msg_pre));
		msg_tv.setTextColor(this.getResources().getColor(R.color.font_blue));
		
		tabHost.setCurrentTabByTag("2");
	}
	@SuppressLint("NewApi")
	public void reloadAbout(View view)
	{
		chearStyle();
		about_iv.setBackground(getResources().getDrawable(R.drawable.gy_pre));
		about_tv.setTextColor(this.getResources().getColor(R.color.font_blue));
		tabHost.setCurrentTabByTag("4");
	}
	@SuppressLint("NewApi")
	public void reloadPhone(View view)
	{
		chearStyle();
		phone_iv.setBackground(getResources().getDrawable(R.drawable.phone_pre));
		phone_tv.setTextColor(this.getResources().getColor(R.color.font_blue));
		tabHost.setCurrentTabByTag("3");
	}
	
	@SuppressLint("NewApi")
	private void chearStyle()
	{
		jc_iv.setBackground(getResources().getDrawable(R.drawable.jc_nor));
		msg_iv.setBackground(getResources().getDrawable(R.drawable.msg_nor));
		about_iv.setBackground(getResources().getDrawable(R.drawable.gy_nor));
		phone_iv.setBackground(getResources().getDrawable(R.drawable.phone_nor));
		jc_tv.setTextColor(this.getResources().getColor(R.color.font_gray));
		msg_tv.setTextColor(this.getResources().getColor(R.color.font_gray));
		about_tv.setTextColor(this.getResources().getColor(R.color.font_gray));
		phone_tv.setTextColor(this.getResources().getColor(R.color.font_gray));
	}
	

	@Override
	protected void onDestroy() {
	
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
