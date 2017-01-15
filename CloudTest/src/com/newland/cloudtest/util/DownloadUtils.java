package com.newland.cloudtest.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;

import com.alibaba.fastjson.JSON;
import com.newland.cloudtest.MainActivity;
import com.newland.cloudtest.R;
import com.newland.cloudtest.bean.ConfigDb;
import com.newland.cloudtest.bean.ImageUpload;
import com.newland.cloudtest.bean.MyXutilsRequestParams;
import com.newland.cloudtest.bean.PhoneTask;
import com.newland.cloudtest.bean.RequestData;
import com.orhanobut.logger.Logger;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * 下载服务
 * 
 * @author TongLee
 *
 */
public class DownloadUtils {
	private DownloadUtils() {
	}

	Map<String, String>map = new HashMap<String, String>();
	
	private Context context;
	private static DownloadUtils single = null;

	// 静态工厂方法
	public static DownloadUtils getInstance() {
		if (single == null) {
			single = new DownloadUtils();
		}
		return single;
	}

	public void downloadfile(String downUrl) {
		/**
		 * 自定义实体参数类请参考: 请求注解 {@link org.xutils.http.annotation.HttpRequest}
		 * 请求注解处理模板接口 {@link org.xutils.http.app.ParamsBuilder}
		 *
		 * 需要自定义类型作为callback的泛型时, 参考: 响应注解
		 * {@link org.xutils.http.annotation.HttpResponse} 响应注解处理模板接口
		 * {@link org.xutils.http.app.ResponseParser}
		 *
		 * 示例: 查看 org.xutils.sample.http 包里的代码
		 */
		MyXutilsRequestParams params = new MyXutilsRequestParams(downUrl);
		params.setSaveFilePath(Environment.getExternalStorageDirectory()
				+ "/1.apk");
		params.setCharset("utf-8");
		// 有上传文件时使用multipart表单, 否则上传原始文件流.
		// params.setMultipart(true);
		// 上传文件方式 1
		// params.uploadFile = new File("/sdcard/test.txt");
		// 上传文件方式 2
		// params.addBodyParameter("uploadFile", new File("/sdcard/test.txt"));

		Callback.Cancelable cancelable = x.http().get(params,
		/**
		 * 1. callback的泛型: callback参数默认支持的泛型类型参见
		 * {@link org.xutils.http.loader.LoaderFactory}, 例如: 指定泛型为File则可实现文件下载,
		 * 使用params.setSaveFilePath(path)指定文件保存的全路径.
		 * 默认支持断点续传(采用了文件锁和尾端校验续传文件的一致性). 其他常用类型可以自己在LoaderFactory中注册, 也可以使用
		 * {@link org.xutils.http.annotation.HttpResponse}
		 * 将注解HttpResponse加到自定义返回值类型上, 实现自定义ResponseParser接口来统一转换. 如果返回值是json形式,
		 * 那么利用第三方的json工具将十分容易定义自己的ResponseParser. 如示例代码
		 * {@link org.xutils.sample.http.BaiduResponse}, 可直接使用BaiduResponse作为
		 * callback的泛型.
		 *
		 * 2. callback的组合: 可以用基类或接口组合个种类的Callback, 见
		 * {@link org.xutils.common.Callback}. 例如: a.
		 * 组合使用CacheCallback将使请求检测缓存或将结果存入缓存(仅GET请求生效). b.
		 * 组合使用PrepareCallback的prepare方法将为callback提供一次后台执行耗时任务的机会,
		 * 然后将结果给onCache或onSuccess. c. 组合使用ProgressCallback将提供进度回调. ...(可参考
		 * {@link org.xutils.image.ImageLoader} 或 示例代码中的
		 * {@link org.xutils.sample.download.DownloadCallback})
		 *
		 * 3. 请求过程拦截或记录日志: 参考 {@link org.xutils.http.app.RequestTracker}
		 *
		 * 4. 请求Header获取: 参考
		 * {@link org.xutils.http.app.RequestInterceptListener}
		 *
		 * 5. 其他(线程池, 超时, 重定向, 重试, 代理等): 参考
		 * {@link org.xutils.http.RequestParams}
		 *
		 **/
		new Callback.ProgressCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				Logger.v("onError"+arg0);

			}

			@Override
			public void onFinished() {
				Logger.v("onFinishedget4Gflow" + SystemUtils.get4Gflow());

			}

			@Override
			public void onSuccess(File arg0) {
				Logger.v("onSuccess");

			}

			@Override
			public void onLoading(long total, long current,
					boolean isDownloading) {
				Logger.v("onLoading" + (int) (current * 100 / total));

			}

			@Override
			public void onStarted() {
				Logger.v("onStarted");

			}

			@Override
			public void onWaiting() {
				// TODO Auto-generated method stub

			}

		});
	}
	
	public void downloadfileHandle(String downUrl,String dest,final Context context)
	{
		
		Message msg = new Message();
		msg.what = 2;
		map.put("downUrl", downUrl);
		map.put("dest", dest);
	
		handler.sendMessage(msg);
	}
	
	public void downloadfile(String downUrl,String dest,final Context context) {
		/**
		 * 自定义实体参数类请参考: 请求注解 {@link org.xutils.http.annotation.HttpRequest}
		 * 请求注解处理模板接口 {@link org.xutils.http.app.ParamsBuilder}
		 *
		 * 需要自定义类型作为callback的泛型时, 参考: 响应注解
		 * {@link org.xutils.http.annotation.HttpResponse} 响应注解处理模板接口
		 * {@link org.xutils.http.app.ResponseParser}
		 *
		 * 示例: 查看 org.xutils.sample.http 包里的代码
		 */
		MyXutilsRequestParams params = new MyXutilsRequestParams(downUrl);
		params.setCharset("utf-8");
		params.setSaveFilePath(dest);
		// 有上传文件时使用multipart表单, 否则上传原始文件流.
		// params.setMultipart(true);
		// 上传文件方式 1
		// params.uploadFile = new File("/sdcard/test.txt");
		// 上传文件方式 2
		// params.addBodyParameter("uploadFile", new File("/sdcard/test.txt"));

		Callback.Cancelable cancelable = x.http().get(params,
		/**
		 * 1. callback的泛型: callback参数默认支持的泛型类型参见
		 * {@link org.xutils.http.loader.LoaderFactory}, 例如: 指定泛型为File则可实现文件下载,
		 * 使用params.setSaveFilePath(path)指定文件保存的全路径.
		 * 默认支持断点续传(采用了文件锁和尾端校验续传文件的一致性). 其他常用类型可以自己在LoaderFactory中注册, 也可以使用
		 * {@link org.xutils.http.annotation.HttpResponse}
		 * 将注解HttpResponse加到自定义返回值类型上, 实现自定义ResponseParser接口来统一转换. 如果返回值是json形式,
		 * 那么利用第三方的json工具将十分容易定义自己的ResponseParser. 如示例代码
		 * {@link org.xutils.sample.http.BaiduResponse}, 可直接使用BaiduResponse作为
		 * callback的泛型.
		 *
		 * 2. callback的组合: 可以用基类或接口组合个种类的Callback, 见
		 * {@link org.xutils.common.Callback}. 例如: a.
		 * 组合使用CacheCallback将使请求检测缓存或将结果存入缓存(仅GET请求生效). b.
		 * 组合使用PrepareCallback的prepare方法将为callback提供一次后台执行耗时任务的机会,
		 * 然后将结果给onCache或onSuccess. c. 组合使用ProgressCallback将提供进度回调. ...(可参考
		 * {@link org.xutils.image.ImageLoader} 或 示例代码中的
		 * {@link org.xutils.sample.download.DownloadCallback})
		 *
		 * 3. 请求过程拦截或记录日志: 参考 {@link org.xutils.http.app.RequestTracker}
		 *
		 * 4. 请求Header获取: 参考
		 * {@link org.xutils.http.app.RequestInterceptListener}
		 *
		 * 5. 其他(线程池, 超时, 重定向, 重试, 代理等): 参考
		 * {@link org.xutils.http.RequestParams}
		 *
		 **/
		new Callback.ProgressCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				Logger.v("onError"+arg0);
				SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "downpicStatue", "erro");
			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(File arg0) {
				Logger.v("onSuccess");
				SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "downpicStatue", "sucess");

			}

			@Override
			public void onLoading(long total, long current,
					boolean isDownloading) {
				Logger.v("onLoading" + (int) (current * 100 / total));

			}

			@Override
			public void onStarted() {
				Logger.v("onStarted");

			}

			@Override
			public void onWaiting() {
				// TODO Auto-generated method stub

			}

		});
	}
	
	/**
	 * 上传图片验证码
	 * @param <T>
	 * @param downUrl
	 * @param dest
	 */
	public static <T> void uploadImg(String downUrl,String dest,CommonCallback<T> callback,ImageUpload imageUpload)
	{
		MyXutilsRequestParams params = new MyXutilsRequestParams(downUrl);
		params.setCharset("utf-8");
		RequestData requestData = new RequestData();
		requestData.setData(imageUpload);
		params.addBodyParameter("data", JSON.toJSONString(requestData));
		// 有上传文件时使用multipart表单, 否则上传原始文件流.
		params.setMultipart(true);
		new File(dest).exists();
			
		params.addBodyParameter("file", new File(dest));
    
        Cancelable cancelable = x.http().post(params,  callback);  
			        

	}
	
	public boolean downloadfile(String downUrl, File dest,File dir) throws Exception {
		try {
			if(!dir.exists())
			  {
				  FileHelper.createDir(dir);
			  }
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");

			sslContext.init(null,tm,new java.security.SecureRandom());

			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			// 创建URL对象
			URL myURL = null;
			try {
				myURL = new URL(downUrl);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
			HttpsURLConnection httpsConn = (HttpsURLConnection) myURL.openConnection();
			httpsConn.setSSLSocketFactory(ssf);

			// 取得该连接的输入流，以读取响应内容
//			InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());

//			// 读取服务器的响应内容并显示
//			int respInt = insr.read();while(respInt!=-1)
//				{
//				System.out.print((char) respInt);
//				respInt = insr.read();
//			}
			    FileOutputStream fos = null;  
		        BufferedInputStream bis = null;  
		        URL url = null;  
		        byte[] buf = new byte[1025];  
		        int size = 0;  
		        bis = new BufferedInputStream(httpsConn.getInputStream());  
		        // 建立文件  
		        fos = new FileOutputStream(dest);  
		        // 保存文件  
		        while ((size = bis.read(buf)) != -1)  
		            fos.write(buf, 0, size);  
		  
		        fos.close();  
		        bis.close();  
		        httpsConn.disconnect();  
		        return true;
		} catch (Exception e) {
			    e.printStackTrace();  
	            return false;  
		}
		 
		
		
		
		
		
		
		
//		  try {  
//			  if(!dir.exists())
//				  {
//					  FileHelper.createDir(dir);
//				  }
//			  
//			    FileOutputStream fos = null;  
//		        BufferedInputStream bis = null;  
//		        HttpURLConnection httpUrl = null;  
//		        URL url = null;  
//		        byte[] buf = new byte[1025];  
//		        int size = 0;  
//		  
//		        // 建立链接  
//		        url = new URL(downUrl);  
//		        httpUrl = (HttpURLConnection) url.openConnection();  
//		        // 连接指定的资源  
//		        httpUrl.connect();  
//		        // 获取网络输入流  
//		        bis = new BufferedInputStream(httpUrl.getInputStream());  
//		        // 建立文件  
//		        fos = new FileOutputStream(dest);  
//		  
//		  
//		        // 保存文件  
//		        while ((size = bis.read(buf)) != -1)  
//		            fos.write(buf, 0, size);  
//		  
//		        fos.close();  
//		        bis.close();  
//		        httpUrl.disconnect();  
//		        
//
//	        } catch (Exception e) {  
//	        	
//	            e.printStackTrace();  
//	            return false;  
//	        }   
//	        return true;

	}

	
	

	      
	    public  String getFileNameFromUrl(String url){  
	        String name = new Long(System.currentTimeMillis()).toString() + ".X";  
	        int index = url.lastIndexOf("/");  
	        if(index > 0){  
	            name = url.substring(index + 1);  
	            if(name.trim().length()>0){  
	                return name;  
	            }  
	        }  
	        return name;  
	    }  
	
	
	
	

	private int localVersion, serverVersion;
	private int len;
	private NotificationManager manager;
	private Notification notif;
	private String tempApkPath;

	/**
	 * 下载带提示框
	 * 
	 * @param downUrl
	 */
	public void downloadfileWithNotifycation(String downUrl, final Context context,final boolean isUpdateDb,final PhoneTask phoneTask) {
		this.context = context;
		// 点击通知栏后打开的activity
		Intent intent = new Intent(context, MainActivity.class);

		PendingIntent pIntent = PendingIntent
				.getActivity(context, 0, intent, 0);
		manager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notif = new Notification();
		notif.icon = R.drawable.ic_launcher;
		if(isUpdateDb)
		{
			notif.tickerText = "下载非探测任务";
		}
		else {
			notif.tickerText = "云探APP有更新";
		}
		
		// 通知栏显示所用到的布局文件
		notif.contentView = new RemoteViews(context.getPackageName(),
				R.layout.content_view);
		notif.contentIntent = pIntent;
		manager.notify(0, notif);

		MyXutilsRequestParams params = new MyXutilsRequestParams(downUrl);
		params.setCharset("utf-8");
		String name = System.currentTimeMillis()+"";
		tempApkPath = Environment.getExternalStorageDirectory()+ "/"+name+".apk";
		params.setSaveFilePath(Environment.getExternalStorageDirectory()
				+ "/"+name+".apk");
		
		Callback.Cancelable cancelable = x.http().get(params,

		new Callback.ProgressCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				Logger.v("onError");
				 if(isUpdateDb )
				 {
					phoneTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date())); 
					phoneTask.setStatus(6);//已执行
					phoneTask.setTaskResult(2);//失败
					try {
						SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					} catch (DbException e) {
						e.printStackTrace();
					}
				 }
			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(File arg0) {
				Logger.v("onSuccess");
				//handler.sendEmptyMessage(1);  
				 if(isUpdateDb )
				 {
					phoneTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date())); 
					phoneTask.setStatus(2);//已执行
					phoneTask.setTaskResult(1);//成功
					try {
						SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					} catch (DbException e) {
						e.printStackTrace();
					}
				 }
				 else{
					ApkController.install(tempApkPath, context);
				 }
			}

			@Override
			public void onLoading(long total, long current,
					boolean isDownloading) {
				Logger.v("onLoading" + (int) (current * 100 / total));
				len =  (int) (current * 100 / total);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = len;
				handler.sendMessage(msg);

			}

			@Override
			public void onStarted() {
				Logger.v("onStarted");

			}

			@Override
			public void onWaiting() {
				// TODO Auto-generated method stub

			}

		});

	}
	
	/**
	 * 下载带提示框
	 * 
	 * @param downUrl
	 */
	public void downloadfileWithNotifycation(String downUrl, final Context context,final boolean isUpdateDb,final PhoneTask phoneTask, final int versionCode) {
		this.context = context;
		// 点击通知栏后打开的activity
		Intent intent = new Intent(context, MainActivity.class);

		PendingIntent pIntent = PendingIntent
				.getActivity(context, 0, intent, 0);
		manager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		notif = new Notification();
		notif.icon = R.drawable.ic_launcher;
		if(isUpdateDb)
		{
			notif.tickerText = "下载非探测任务";
		}
		else {
			notif.tickerText = "云探APP有更新";
		}
		
		// 通知栏显示所用到的布局文件
		notif.contentView = new RemoteViews(context.getPackageName(),
				R.layout.content_view);
		notif.contentIntent = pIntent;
		manager.notify(0, notif);

		MyXutilsRequestParams params = new MyXutilsRequestParams(downUrl);
		params.setCharset("utf-8");
		String name = System.currentTimeMillis()+"";
		tempApkPath = Environment.getExternalStorageDirectory()+ "/"+name+".apk";
		params.setSaveFilePath(Environment.getExternalStorageDirectory()
				+ "/"+name+".apk");
		
		Callback.Cancelable cancelable = x.http().get(params,

		new Callback.ProgressCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				Logger.v("onError");
				 if(isUpdateDb )
				 {
					phoneTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date())); 
					phoneTask.setStatus(6);//已执行
					phoneTask.setTaskResult(2);//失败
					try {
						SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					} catch (DbException e) {
						e.printStackTrace();
					}
				 }
			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(File arg0) {
				Logger.v("onSuccess");
				handler.sendEmptyMessage(1);  
				 if(isUpdateDb )
				 {
					phoneTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA).format(new Date())); 
					phoneTask.setStatus(2);//已执行
					phoneTask.setTaskResult(1);//成功
					try {
						SystemUtils.getDbManage().saveOrUpdate(phoneTask);
					} catch (DbException e) {
						e.printStackTrace();
					}
				 }
				 else{
					SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "apkName",tempApkPath);
					SharedPreferencesUtils.setConfigStr(context, Contant.cachName, "versionCode",String.valueOf(versionCode));
//					boolean succ = ApkController.install(tempApkPath, context);
//					if (succ){
//						Toast.makeText(context, "安装成功", 1).show();
//					} else {
//						Toast.makeText(context, "安装失败", 1).show();
//					}
					 
				 }
			}

			@Override
			public void onLoading(long total, long current,
					boolean isDownloading) {
				Logger.v("onLoading" + (int) (current * 100 / total));
				len =  (int) (current * 100 / total);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = len;
				handler.sendMessage(msg);

			}

			@Override
			public void onStarted() {
				Logger.v("onStarted");

			}

			@Override
			public void onWaiting() {
				// TODO Auto-generated method stub

			}

		});

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				notif.contentView.setTextViewText(R.id.content_view_text1, len
						+ "%");
				notif.contentView.setProgressBar(R.id.content_view_progress,
						100, len, false);
				manager.notify(0, notif);

				break;
			case 1:
				Toast.makeText(context, "下载完成", 0).show();
				//安装apk
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						boolean isinitScuess = AppSytemUtils.getInstance().installApp(tempApkPath);
						//boolean isinitScuess = ApkController.install(tempApkPath, context);
						Logger.v("静默安装情况"+isinitScuess);
						if (!isinitScuess) {
							 	Intent i = new Intent(Intent.ACTION_VIEW); 
				    	    	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    	    	
				    	    	i.setDataAndType(Uri.parse("file://" + tempApkPath),"application/vnd.android.package-archive"); 
				    	    	context.startActivity(i);
						}
					}
				}).start();
    	    	
				//installApp(tempApkPath);
				break;
				
			case 2:
				
				
				downloadfile(map.get("downUrl"),map.get("dest"),context);
				
				break;
				
			default:
				break;
			}
		}

	};
	
	
	  
	   /** 
	     * 下载文件 
	     * @param <T> 
	    */  
	    public  <T> Cancelable DownLoadFileDiy(String url,String filepath,CommonCallback<T> callback){  
	        RequestParams params=new RequestParams(url);  
	        //设置断点续传  
	        params.setAutoResume(false);  
	       params.setSaveFilePath(filepath);  
	        Cancelable cancelable = x.http().get(params, callback);  
	        return cancelable;  
	    }  

	
	
}
