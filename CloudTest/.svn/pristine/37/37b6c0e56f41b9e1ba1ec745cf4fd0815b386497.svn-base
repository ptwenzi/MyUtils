package com.newland.appdriver;


import android.util.Log;
import java.io.*;
import java.net.URL;
import java.util.Locale;

public class BasementInterface {
    static {
        try {
            InputStream is = null;
            ClassLoader c = BasementInterface.class.getClassLoader();
            URL url;
            if(c != null) {
                if (isCPUInfo64()) {
                    System.out.print("64 cpu\r\n");
                    url = c.getResource("libs/arm64-v8a/libappdriver.so");
                    if(url != null){
                        is = url.openStream();
                    }
                } else {
                    System.out.print("32 cpu\r\n");
                   url = c.getResource("libs/armeabi/libappdriver.so");
                    if(url != null){
                        is = url.openStream();
                    }
                }
                if(is != null){
                    File tempFile = File.createTempFile("libappdriver", ".so", new File("/data/local/tmp"));
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    int i;
                    byte[] buf = new byte[1024];
                    while ((i = is.read(buf)) != -1) {
                        fos.write(buf, 0, i);
                    }
                    is.close();
                    fos.close();
                    System.load(tempFile.getAbsolutePath());
                    boolean b = tempFile.delete();
                    System.out.print(b);
                    tempFile.deleteOnExit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    static {
//        try{
//            System.load("/data/local/tmp/libappdriver.so");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private static boolean isCPUInfo64() {
        File cpuInfo = new File("/proc/cpuinfo");
        if (cpuInfo.exists()) {
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = new FileInputStream(cpuInfo);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 512);
                String line = bufferedReader.readLine();
                return line != null && line.length() > 0 && line.toLowerCase(Locale.US).contains("arch64");
            } catch (Throwable t) {
                Log.d("isCPUInfo64", " error = " + t.toString());
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public native String CompareImage(String src, String template, double similarity);
    public native int StartPackage(String name);
    public native int StopPackage(String name);
    public native int ClearPackage(String name);
    public native int ServiceStatus(String condition);
}
