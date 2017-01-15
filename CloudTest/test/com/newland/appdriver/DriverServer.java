package com.newland.appdriver;


import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;


public class DriverServer {
    private boolean keepRunning = true;
    private ServerSocket serverSocket;
    private Driver driver;

    public void stopRun() {
        keepRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(){
        try {
            driver = new Driver();
            serverSocket = new ServerSocket(8908);
            System.out.print("\r\n" + driver.STORAGE_PATH + "\r\n");
            Timer timer = new Timer();
            timer.schedule(new CheckService(driver), 1000, 60000);
        } catch (IOException e) {
            e.printStackTrace();
            keepRunning = false;
            return;
        }
        while (keepRunning) {
            try {
                final Socket interactClientSocket = serverSocket.accept();
                dealSocketConnection(interactClientSocket);
            } catch (Exception e) {
                e.printStackTrace();
                keepRunning = false;
            }
        }
    }

    private void dealSocketConnection(Socket socket){
        StringBuilder recvStrBuilder = new StringBuilder();
        InputStream inputStream;
        try {
            inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            char[] buf = new char[4096];
            int readBytes;
            while ((readBytes = inputStreamReader.read(buf)) != -1) {
                String tempStr = new String(buf, 0, readBytes);
                recvStrBuilder.append(tempStr);
                if(tempStr.contains("}")){
                    break;
                }
            }
            String request = recvStrBuilder.toString();
            String response = Response(request);
            socket.getOutputStream().write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.getOutputStream().write(new DriverResult().getExceptResponse("Deal Socket Except" + e.getMessage()).getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String Response(String request){
        JSONObject jsonObject;
        DriverResult ret;
        try {
            if(request == null || request.length() == 0){
                ret = new DriverResult();
                return ret.getExceptResponse("Request Is Null Or Length Is 0");
            }
            jsonObject = new JSONObject(request);
            int act = Integer.parseInt(jsonObject.get("method").toString());
            String target = jsonObject.has("target")?jsonObject.get("target").toString():"";
            String value = jsonObject.has("postparam")?jsonObject.get("postparam").toString():"";
            String s_str = jsonObject.has("succstr1")?jsonObject.get("succstr1").toString():"";
            String f_str = jsonObject.has("errstr1")?jsonObject.get("errstr1").toString():"";
            ret = driver.getResponse(act, target, value, s_str, f_str);
            return ret.toJson();
        } catch (JSONException e) {
            e.printStackTrace();
            ret = new DriverResult();
            return ret.getExceptResponse("getResponse Exception " + e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            ret = new DriverResult();
            return ret.getExceptResponse("getResponse Exception " + e.getMessage());
        }
    }

    public boolean isAlive(){
        return serverSocket != null;
    }
}
