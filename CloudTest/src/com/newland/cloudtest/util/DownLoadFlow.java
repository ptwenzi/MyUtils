package com.newland.cloudtest.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 消耗流量
 * @author TongLee
 *
 */
public class DownLoadFlow {
	private DownLoadFlow() {
	}

	private static DownLoadFlow single = null;
	  FileOutputStream fos = null;  
      BufferedInputStream bis = null;  
      HttpURLConnection httpUrl = null;  

	// 静态工厂方法
	public static DownLoadFlow getInstance() {
		if (single == null) {
			single = new DownLoadFlow();
		}
		return single;
	}
	
	
	public boolean downloadfile(String downUrl, File dest,File dir) throws IOException {
		try{
			if(!dir.exists()){
				FileHelper.createDir(dir);
			}
			  
		    fos = null;  
	        bis = null;  
	        httpUrl = null;  
	        URL url = null;  
	        byte[] buf = new byte[1024];  
	        int size = 0;  
	  
	        // 建立链接  
	        url = new URL(downUrl);  
	        httpUrl = (HttpURLConnection) url.openConnection();  
	        // 连接指定的资源  
	        httpUrl.connect();  
	        // 获取网络输入流  
	        bis = new BufferedInputStream(httpUrl.getInputStream());  
	        // 建立文件  
	        fos = new FileOutputStream(dest);  
	  
	  
	        // 保存文件  
	        while ((size = bis.read(buf)) != -1){
	        	fos.write(buf, 0, size);  
	        }
	  
	        fos.close();  
	        bis.close();  
	        httpUrl.disconnect();  
		          
	        return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}

	} 
	
	public boolean downloadfile(String downUrl, File dest) throws IOException {
		FileOutputStream fos1 = null;  
	    BufferedInputStream bis1 = null;  
	    HttpURLConnection httpUrl1 = null;  
		try{		  
	        URL url = null;  
	        byte[] buf = new byte[1024];  
	        int size = 0;  
	  
	        // 建立链接  
	        url = new URL(downUrl);  
	        httpUrl1 = (HttpURLConnection) url.openConnection();  
	        // 连接指定的资源  
	        httpUrl1.connect();  
	        // 获取网络输入流  
	        bis1 = new BufferedInputStream(httpUrl1.getInputStream());  
	        // 建立文件  
	        fos1 = new FileOutputStream(dest);  
	  
	  
	        // 保存文件  
	        while ((size = bis1.read(buf)) != -1){
	        	fos1.write(buf, 0, size);  
	        }
	  
	        fos1.close();  
	        bis1.close();  
	        httpUrl1.disconnect();  
		          
	        return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}

	} 
	
	/**
	 * 关闭下载
	 */
	public void closeDown()
	{
		   try {
			fos.close();
			 bis.close();  
		        httpUrl.disconnect();  
		} catch (IOException e) {
			e.printStackTrace();
		}  
	       
	}
}
