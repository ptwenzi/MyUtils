package com.newland.cloudtest;

import java.io.File;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.view.annotation.ViewInject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newland.cloudtest.bean.ImageUpload;
import com.newland.cloudtest.service.PhoneInfoService;
import com.newland.cloudtest.util.Contant;
import com.newland.cloudtest.util.DownloadUtils;
import com.newland.cloudtest.util.SharedPreferencesUtils;
import com.newland.cloudtest.util.SystemUtils;
import com.orhanobut.logger.Logger;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity{
	 @ViewInject(R.id.version)
	 private TextView about;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		x.view().inject(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		about.setText(SystemUtils.getAppVersionName(this));
//		String pic = Environment.getExternalStorageDirectory()+"/175493-106.jpg";
//		 ImageUpload imageUpload = new ImageUpload();
//		 imageUpload.setGroupid(8238L);
//		 imageUpload.setStrategyid(3347L);
//		 imageUpload.setTaskid(740551L);
//		 imageUpload.setTasksetid(24565L);
//		 imageUpload.setTasksetinstancename("2016-09-09 12:05:00");
//		 imageUpload.setTestsrl(997L);
//		 
//		DownloadUtils.uploadImg(Contant.IP+"/NativeFile/upload",pic,new CommonCallback<String>() {
//
//			@Override
//			public void onCancelled(CancelledException arg0) {
//				
//			}
//
//			@Override
//			public void onError(Throwable arg0, boolean arg1) {
//			}
//
//			@Override
//			public void onFinished() {
//				
//			}
//
//			@Override
//			public void onSuccess(String result) {
//				Logger.v("成功"+result);
//			}
//
//
//		
//
//		} ,imageUpload);
		
//		DownloadUtils.getInstance().downloadfile("https://211.138.144.117:1443/cdp//resources/pluploadImg/2016-09/09/e429e08f-03cb-460d-a227-0b03f70c23ad.jpg", Environment.getExternalStorageDirectory()+"/xx.jpg", this);
		
//		DownloadUtils.getInstance().DownLoadFileDiy("https://211.138.144.117:1443/cdp//resources/pluploadImg/2016-09/09/e429e08f-03cb-460d-a227-0b03f70c23ad.jpg", Environment.getExternalStorageDirectory()+"/xx.jpg"
//				,new Callback.ProgressCallback<File>(){
//
//					@Override
//					public void onCancelled(CancelledException arg0) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onError(Throwable arg0, boolean arg1) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onFinished() {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onSuccess(File arg0) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onLoading(long arg0, long arg1, boolean arg2) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onStarted() {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onWaiting() {
//						// TODO Auto-generated method stub
//						
//					}
//			
//		});
	}
	

	public void checkUpdate(View view){
		//发送广播，检查是否有版本更新
		Toast.makeText(this, "正在检测升级,请稍后!", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(PhoneInfoService.isNeedUpdateApkAction);
		intent.putExtra("manualCheck", "manualCheck");
		sendBroadcast(intent);
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
