package com.newland.cloudtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.newland.cloudtest.util.Contant;

import android.net.LocalSocketAddress;

public class MISocketClient {
	private ClientSocketThread mServerSocketThread;
	private String request;

	public void start() {
		mServerSocketThread = new ClientSocketThread();
		mServerSocketThread.start();
	}
	
	public MISocketClient( String request)
	{
		this.request = request;
	}

	public void stop() {
		mServerSocketThread.stopRun();
	}

	private class ClientSocketThread extends Thread {
		private boolean keepRunning = true;
		private String SOCKET_ADDRESS = "/data/data/com.example.sendeventtest/newland.socket";
		private int i = 0;
		private void stopRun() {
			keepRunning = false;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				Socket sender = new Socket("localhost",8908);
				sender.setSoTimeout(Contant.scoketTimeOut);  
//				SocketAddress addr = new SocketAddress(SOCKET_ADDRESS,LocalSocketAddress.Namespace.FILESYSTEM);
//				sender.connect(addr);
				//String request = "{\"method\":\"15\", \"target\":\"home\", \"postparam\":\"\", \"succstr1\":\"\", \"errstr1\":\"\"}";
				sender.getOutputStream().write(request.getBytes());
				StringBuilder recvStrBuilder = new StringBuilder();
				InputStream inputStream = null;
				try {
					inputStream = sender.getInputStream();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					char[] buf = new char[4096];
					int readBytes = -1;
					while ((readBytes = inputStreamReader.read(buf)) != -1) {
						String tempStr = new String(buf, 0, readBytes);
						recvStrBuilder.append(tempStr);
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println( "resolve data error !");
				} 
				System.out.print("=============================" + recvStrBuilder.toString());
				sender.close();
				} 
				catch (SocketTimeoutException e)
				{
					e.printStackTrace();
				}

				catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
		}
	}

}
