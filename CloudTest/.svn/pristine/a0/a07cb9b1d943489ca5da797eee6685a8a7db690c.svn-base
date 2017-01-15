package com.newland.appdriver;



public class TestUseCase{

    public void GoRun(){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BasementInterface basementInterface = new BasementInterface();
                    int a = basementInterface.StartPackage("com.voole.mobilestore/com.voole.newmobilestore.start.StartActivity");
                    System.out.print(a);
                    System.out.print("\r\n");
                    a = basementInterface.StartPackage("com.greenpoint.android.mc10086.activity/com.leadeon.cmcc.base.StartPageActivity");
                    System.out.print(a);
                    System.out.print("\r\n");
                    int b = basementInterface.ServiceStatus("newland");
                    System.out.print(b);
                    System.out.print("\r\n");
                    b = basementInterface.ServiceStatus("google");
                    System.out.print(b);
                    System.out.print("\r\n");
//                    System.out.print("Run\r\n");
//                    Driver driver = new Driver();
//                    DriverResult result = driver.getResponse(3, "text=请输入验证码", "123456.7", "", "");
//                    System.out.print(result.toJson());
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void img(){
        Driver driver = new Driver();
//        String s = driver.basementInterface.CompareImage("/storage/emulated/0/compareImage.png", "/sdcard/CloudTestCash/1472610139419.png", 0.99);
//        System.out.print("\r\n" + s + "\r\n");
//        DriverResult result = driver.startApp("com.xwtec.xjmc/com.qihoo.util.StartActivity");
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.print(result.toJson());
//        result = driver.shot("resourceId=com.xwtec.xjmc:id/btn_getcode1", "element");
//        System.out.print(result.toJson());
//        result = driver.getPageContent();
//        System.out.print(result.toJson());
        DriverResult result = driver.getResponse(47, "/sdcard/CloudTestCash/1472541408785.png", "100,600,300,300,0.99", "", "");
        System.out.print(result.toJson());

    }

}