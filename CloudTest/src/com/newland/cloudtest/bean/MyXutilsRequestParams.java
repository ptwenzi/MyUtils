package com.newland.cloudtest.bean;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.xutils.http.RequestParams;

public class MyXutilsRequestParams extends RequestParams{

	public MyXutilsRequestParams()
	{
		//this.setSslSocketFactory(null); // 设置ssl
	}

	public MyXutilsRequestParams(String str) {
		super(str);
		setConnectTimeout(1000*60);
		SSLContext sc;
		try {
			

			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());  
			this.setSslSocketFactory(sc.getSocketFactory());
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}  
		
	}


	private class MyHostnameVerifier implements HostnameVerifier{
        @Override
        public boolean verify(String hostname, SSLSession session) {
                // TODO Auto-generated method stub
                return true;
        }

   }

   private class MyTrustManager implements X509TrustManager{
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                // TODO Auto-generated method stub  
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                        throws CertificateException {
                // TODO Auto-generated method stub    
        }
        @Override
        public X509Certificate[] getAcceptedIssuers() {
                // TODO Auto-generated method stub
                return null;
        }        

  }   

}

