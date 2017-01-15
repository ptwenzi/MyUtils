package com.newland.appdriver;


import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/7.
 */
public class CheckService  extends TimerTask{
    private Driver d;
    public CheckService(Driver driver){
        d = driver;
    }

    @Override
    public void run() {
        if(d.basementInterface.ServiceStatus("newland") == 1){
            d.basementInterface.StartPackage("com.newland.cloudtest/.TabHostMainActivity");
        }
    }
}
