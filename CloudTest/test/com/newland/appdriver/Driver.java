package com.newland.appdriver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import java.io.*;
import java.util.*;

import android.view.KeyEvent;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;


public class Driver {
    final public static String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static final int CLICK_ELEMENT = 2;
    public static final int INPUT_ELEMENT = 3;
    public static final int CLICK_POINT = 4;
    public static final int CLICK_BACK = 5;
    public static final int CLICK_MENU = 6;
    public static final int CLICK_HOME = 7;
    public static final int CLICK_POWER = 8;
    public static final int CLICK_VOL_UP = 9;
    public static final int CLICK_VOL_DOWN = 10;
    public static final int CLICK_IF_EXIST = 11;
    public static final int SHOT = 14;
    public static final int INPUT_IF_EXIST = 15;
    public static final int LONG_PRESS = 20;
    public static final int WAIT = 21;
    public static final int DRAG = 24;
    public static final int GET_PAGE_CONTENT = 27;
    public static final int SWIPE = 30;
    public static final int SCREEN_ON = 34;
    public static final int SCREEN_OFF = 35;
    public static final int START_APP = 43;
    public static final int COMPARE_IMAGE = 47;
    public static final int INPUT_POINT = 48;
    public static final int STOP_APP = 49;
    public static final int CLEAR_CACHE = 50;
    public BasementInterface basementInterface = null;

    public  Driver(){
        basementInterface = new BasementInterface();
    }

    public  DriverResult getResponse(int method, String target, String value, String succstr, String errstr){
        DriverResult result;
        String methodName = "";
        switch (method){
            case CLICK_ELEMENT:
                result = clickElement(target);
                methodName = "元素点击";
                break;
            case INPUT_ELEMENT:
                result = inputElement(target, value);
                methodName = "元素输入";
                break;
            case CLICK_POINT:
                result = clickPoint(getPoint(target).x, getPoint(target).y);
                result.setMethodName("坐标点击");
                break;
            case INPUT_POINT:
                result = inputPoint(target, value);
                methodName = "坐标输入";
                break;
            case CLICK_BACK:
                result = press("back");
                methodName = "点击回退";
                break;
            case CLICK_MENU:
                result = press("menu");
                methodName = "点击菜单";
                break;
            case CLICK_HOME:
                result = press("home");
                methodName = "点击Home";
                break;
            case CLICK_POWER:
                result = press("power");
                methodName = "点击电源";
                break;
            case CLICK_VOL_UP:
                result = press("volume_up");
                methodName = "点击音量上";
                break;
            case CLICK_VOL_DOWN:
                result = press("volume_down");
                methodName = "点击音量下";
                break;
            case SWIPE:
                methodName = "滑动";
                if(target != null && target.length() > 0){
                    result = swipe(target);
                }else  if(value != null && value.length() > 0){
                    result = swipe(value);
                }else {
                    result = new DriverResult();
                    result.setCode(1);
                    result.setDescribe("滑动失败滑动对象[" + target + "][" + value + "]");
                }
                break;
            case SHOT:
                result = shot(target, value);
                methodName = "截图";
                break;
            case CLEAR_CACHE:
                result = clearCache(target);
                methodName = "清空缓存";
                break;
            case STOP_APP:
                result = stopApp(target);
                methodName = "停止App";
                break;
            case START_APP:
                result = startApp(target);
                methodName = "启动App";
                break;
            case LONG_PRESS:
                result = longPress(target);
                methodName = "长按";
                break;
            case DRAG:
                result = drag(target, value);
                methodName = "拖动";
                break;
            case COMPARE_IMAGE:
                int left, top, w, h;
                left = top = w = h = 0;
                int ct = 0;
                if(value.isEmpty()){
                    value = "0.99";
                }else if(value.contains(",")){
                    String[] ls = value.split(",");
                    if(ls.length == 4){
                        try{
                            left = Integer.parseInt(ls[0]);
                            top = Integer.parseInt(ls[1]);
                            w = Integer.parseInt(ls[2]);
                            h = Integer.parseInt(ls[3]);
                            ct = 1;
                            value = "0.99";
                        }catch (Exception ep){
                            ep.printStackTrace();
                            ct = 0;
                            value = "0.99";
                        }
                    }else if(ls.length == 5){
                        try{
                            left = Integer.parseInt(ls[0]);
                            top = Integer.parseInt(ls[1]);
                            w = Integer.parseInt(ls[2]);
                            h = Integer.parseInt(ls[3]);
                            ct = 1;
                            value = ls[4];
                        }catch (Exception ep){
                            ep.printStackTrace();
                            value = "0.99";
                        }
                    }
                }else {
                    ct = 0;
                }
                if(ct == 0){
                    result = waitImage(target, value);
                }else{
                    result = waitImageArea(target, value, left, top, w, h);
                }
                methodName = "图片比对";
                break;
            case WAIT:
                result = wait(target, value, succstr, errstr);
                methodName = "等待";
                break;
            case GET_PAGE_CONTENT:
                result = getPageContent();
                methodName = "获取页面内容";
                break;
            case CLICK_IF_EXIST:
                result = clickIfExist(target);
                methodName = "存在则点击";
                break;
            case INPUT_IF_EXIST:
                result = inputIfExist(target, value);
                methodName = "存在则输入";
                break;
            case SCREEN_OFF:
                result = screenOff();
                methodName = "屏幕开";
                break;
            case SCREEN_ON:
                result = screenOn();
                methodName = "屏幕关";
                break;
            default:
                result = new DriverResult();
                result.setStartTime(new Date());
                result.setEndTime(new Date());
                result.setCode(1);
                result.setDescribe("Unknown Method " + method);
                result.setDetail("");
                methodName = "未知步骤";
                break;
        }
        if(method != WAIT && ((succstr != null && succstr.trim().length() > 0)) || (errstr != null && errstr.trim().length() > 0)){
            System.out.print("Wait Key Word\r\n");
            result = wait("keyword=180", "", succstr, errstr);
            result.setDetail(getPageContent().getDetail());
        }
        result.setMethodName(methodName);
        return result;
    }

    public DriverResult wait(String target, String value, String success, String fail){
        DriverResult ret;
        String waitType;
        long timeout;
        if(target == null || target.trim().length() == 0){
            waitType = "keyword";
            timeout = 180;
        }else{
            if(target.contains("=")){
                String[] ls = target.split("=");
                waitType = ls[0];
                try{
                    timeout = Integer.parseInt(ls[1]);
                }catch (Exception e){
                    e.printStackTrace();
                    timeout = 180;
                }
            }else{
                waitType = target;
                timeout = 180;
            }
        }
        if(waitType.toLowerCase().equals("fix")){
            ret = waitFix(timeout);
        }else if(waitType.toLowerCase().equals("keyword")){
            ret = waitKeyword(success, fail, timeout);
        }else  if(waitType.toLowerCase().equals("element")){
            ret = new DriverResult();
            if(value == null || value.trim().length() == 0){
                ret.setCode(1);
                ret.setDescribe("等待元素错误元素信息为空");
            }else{
                ret.setStartTime(new Date());
                UiObject obj = new UiObject(getUiSelector(value));
                if(obj.waitForExists(timeout * 1000)){
                    ret.setCode(0);
                    ret.setDescribe("等待元素成功");
                }else {
                    ret.setCode(1);
                    ret.setDescribe("等待元素超时");
                }
                ret.setEndTime(new Date());
            }
        }else {
            ret = new DriverResult();
            ret.setCode(1);
            ret.setDescribe("等待步骤错误:未知的等待类型[" + target + "]");
        }
        return ret;
    }

    public DriverResult waitFix(long timeout){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            Thread.sleep(timeout * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        ret.setCode(0);
        ret.setDescribe("等待固定时长[" + timeout + "]成功");
        return ret;
    }

    public DriverResult screenOff(){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            UiDevice.getInstance().sleep();
            ret.setCode(0);
            ret.setDescribe("关闭屏幕成功");
        } catch (RemoteException e) {
            e.printStackTrace();
            ret.setCode(1);
            ret.setDescribe("关闭屏幕异常:" + e.getMessage());
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public DriverResult screenOn(){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            UiDevice.getInstance().wakeUp();
            ret.setCode(0);
            ret.setDescribe("唤醒屏幕成功");
        } catch (RemoteException e) {
            e.printStackTrace();
            ret.setCode(1);
            ret.setDescribe("唤醒屏幕异常:" + e.getMessage());
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public boolean pressKey(String key) throws RemoteException {
        boolean result;
        try{
            key = key.toLowerCase();
            if ("home".equals(key))
                result = UiDevice.getInstance().pressHome();
            else if ("back".equals(key))
                result = UiDevice.getInstance().pressBack();
            else if ("left".equals(key))
                result = UiDevice.getInstance().pressDPadLeft();
            else if ("right".equals(key))
                result = UiDevice.getInstance().pressDPadRight();
            else if ("up".equals(key))
                result = UiDevice.getInstance().pressDPadUp();
            else if ("down".equals(key))
                result = UiDevice.getInstance().pressDPadDown();
            else if ("center".equals(key))
                result = UiDevice.getInstance().pressDPadCenter();
            else if ("menu".equals(key))
                result = UiDevice.getInstance().pressMenu();
            else if ("search".equals(key))
                result = UiDevice.getInstance().pressSearch();
            else if ("enter".equals(key))
                result = UiDevice.getInstance().pressEnter();
            else if ("delete".equals(key) || "del".equals(key))
                result = UiDevice.getInstance().pressDelete();
            else if ("recent".equals(key))
                result = UiDevice.getInstance().pressRecentApps();
            else if ("volume_up".equals(key))
                result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
            else if ("volume_down".equals(key))
                result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
            else if ("volume_mute".equals(key))
                result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE);
            else if ("camera".equals(key))
                result = UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_CAMERA);
            else result = "power".equals(key) && UiDevice.getInstance().pressKeyCode(KeyEvent.KEYCODE_POWER);
            System.out.print(result);
            result = true;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

    public DriverResult press(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            if(pressKey(target)){
                ret.setCode(0);
                ret.setDescribe("按键[" + target + "]成功");
            }else{
                ret.setCode(1);
                ret.setDescribe("按键[" + target + "]失败");
            }
        } catch (RemoteException e) {
            ret.setCode(1);
            ret.setDescribe("按键[" + target + "]异常:" + e.getMessage());
            e.printStackTrace();
        }catch (Exception e){
            ret.setCode(1);
            ret.setDescribe("按键[" + target + "]异常:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult clickPoint(int x, int y){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            if (UiDevice.getInstance().click(x, y)) {
                ret.setCode(0);
                ret.setDescribe("点击坐标[" + x + "," + y + "]成功");
            } else {
                ret.setCode(1);
                ret.setDescribe("点击坐标[" + x + "," + y + "]失败");
            }
        }catch (Exception e){
            ret.setCode(1);
            ret.setDescribe("点击坐标[" + x + "," + y + "]异常:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult clickElement(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            UiObject obj = waitElement(target);
            if(obj != null) {
                ret.setStartTime(new Date());
                obj.click();
                ret.setCode(0);
                ret.setDescribe("点击元素[" + target + "]成功");
            }else{
                ret.setCode(1);
                ret.setDescribe("点击元素[" + target + "]失败:元素未找到");
            }
        }catch (Exception e){
            ret.setCode(1);
            ret.setDescribe("点击元素[" + target + "]异常:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult inputElement(String target, String value){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try{
            UiObject obj = waitElement(target);
            if(obj!=null) {
                ret.setStartTime(new Date());
                try {
                    obj.clearTextField();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (obj.setText(value)) {
                    ret.setCode(0);
                    ret.setDescribe("输入元素[" + target + "]值[" + value + "]成功");
                } else {
                    ret.setCode(1);
                    ret.setDescribe("输入元素[" + target + "]值[" + value + "]失败");
                }
            }else {
                ret.setCode(1);
                ret.setDescribe("输入元素[" + target + "]值[" + value + "]失败:元素未找到");
            }
        }catch (Exception e){
            ret.setCode(1);
            ret.setDescribe("输入元素[" + target + "]值[" + value + "]异常:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult inputPoint(String target, String value){
        if(target != null && target.length() > 0){
            Point p = getPoint(target);
            DriverResult ret = clickPoint(p.x, p.y);
            if(ret.getCode() != 0){
                return ret;
            }
        }
        return inputDirect(value);
    }

    public DriverResult inputDirect(String value){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        UiDevice driver = UiDevice.getInstance();
        for(int i = 0; i < value.length(); i++){
            char h = value.charAt(i);
            int c =  (int)h;
            if(c >= 48 && c <= 57){
                //0-9
                if(!driver.pressKeyCode(c - 41)){
                    ret.setCode(1);
                    ret.setDescribe("输入字符[" + h + "]错误");
                    break;
                }
            }else if (c >= 65 && c<= 90){
                //A-Z
                if(!driver.pressKeyCode(c - 36, 59)){
                    ret.setCode(1);
                    ret.setDescribe("输入字符[" + h + "]错误");
                    break;
                }
            }else  if (c >= 97 && c<= 122){
                //a-z
                if(!driver.pressKeyCode(c - 68)){
                    ret.setCode(1);
                    ret.setDescribe("输入字符[" + h + "]错误");
                    break;
                }
            }else{
                ret.setCode(1);
                ret.setDescribe("键盘输入只支持数字和字母");
                break;
            }
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult swipe(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        UiDevice driver = UiDevice.getInstance();
        if(target.contains(">>")){
            String[] lp = target.split(">>");
            try{
                android.graphics.Point[] points = new android.graphics.Point[lp.length];
                int i = 0;
                for (String aLp : lp) {
                    String[] lss = aLp.split(",");
                    Point p = new Point();
                    p.set(Integer.parseInt(lss[0]), Integer.parseInt(lss[1]));
                    points[i] = p;
                    i++;
                }
                if(driver.swipe(points, 20)){
                    ret.setCode(0);
                    ret.setDescribe("滑动坐标[" + target + "]成功");
                }else{
                    ret.setCode(0);
                    ret.setDescribe("滑动坐标[" + target + "]失败");
                }
            }catch (Exception e){
                ret.setCode(1);
                ret.setDescribe("滑动的坐标错误[" + target + "]");
            }
        }else if(target.contains(",")){
            String[] ls = target.split(",");
            if(ls.length != 4){
                ret.setCode(1);
                ret.setDescribe("错误的滑动坐标[" + target + "]");
            }else {
                try{
                    int x1 = Integer.parseInt(ls[0]);
                    int y1 = Integer.parseInt(ls[1]);
                    int x2 = Integer.parseInt(ls[2]);
                    int y2 = Integer.parseInt(ls[3]);
                    if(driver.swipe(x1, y1, x2, y2, 20)){
                        ret.setCode(0);
                        ret.setDescribe("滑动坐标成功");
                    }else{
                        ret.setCode(1);
                        ret.setDescribe("滑动坐标失败");
                    }
                }catch (Exception e){
                    ret.setCode(1);
                    ret.setDescribe("滑动坐标异常:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }else{
            ret.setCode(1);
            ret.setDescribe("错误的滑动对象[" + target + "]");
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult shot(String target, String value){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        String filePath = null;
        String fileName = "";
        String desc = "";
        if(value == null || value.length() == 0){
            value = "screen";
        }
        if(value.contains("=")) {
            fileName = value.split("=")[1];
        }
        try{
            if(value.toLowerCase().contains("screen")){
                if(fileName.length() == 0){
                    fileName = "screen.png";
                }
                filePath = screenShot(fileName, 50);
            }else if (value.toLowerCase().contains("element")){
                if(fileName.length() == 0){
                    fileName = "element.png";
                }
                filePath = elementShot(fileName, target, 50);
            }else if (value.toLowerCase().contains("area")){
                if(fileName.length() == 0){
                    fileName = "area.png";
                }
                try{
                    String[] lp = target.split(",");
                    if(lp.length != 4){
                        filePath = null;
                        desc = "获取区域[" + target + "]错误";
                    }else{
                        int x = Integer.parseInt(lp[0]);
                        int y = Integer.parseInt(lp[1]);
                        int w = Integer.parseInt(lp[2]);
                        int h = Integer.parseInt(lp[3]);
                        filePath = areaShot(fileName, x, y, w, h , 50);
                    }
                }catch (Exception e){
                    filePath = null;
                    desc = "获取区域[" + target + "]异常:" + e.getMessage();
                }
            }
            if(filePath != null){
                ret.setCode(0);
                ret.setDescribe("截图成功[" + filePath + "]");
                ret.setDetail(filePath);
            }else{
                ret.setCode(1);
                if(desc.length() == 0){
                    ret.setDescribe("截图失败");
                }else {
                    ret.setDescribe(desc);
                }
            }
        }catch (Exception e){
            ret.setCode(1);
            ret.setDescribe("截图异常:" + e.getMessage());
        }
        ret.setEndTime(new Date());
        return ret;
    }

    private String screenShot(String fileName, int quality){
        try {
            File f = new File(STORAGE_PATH + fileName);
            if(f.exists()){
                boolean b = f.delete();
                System.out.print(b);
                if(!b){
                    return null;
                }
            }
            if(UiDevice.getInstance().takeScreenshot(f, 1, quality)){
                if(f.exists()){
                    return f.getAbsolutePath();
                }else{
                    return null;
                }
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String areaShot(String fileName, int x,int y, int w, int h, int quality){
        String screenShotFile = screenShot("screen.png", quality);
        if(screenShotFile == null){
            return null;
        }
        try {
            Bitmap img = BitmapFactory.decodeFile(screenShotFile);
            Bitmap sub = Bitmap.createBitmap(img, x, y, w, h);
            File f = new File(STORAGE_PATH + fileName);
            if(f.exists()){
                boolean b = f.delete();
                System.out.print(b);
                if(!b) {
                    return null;
                }
            }
            FileOutputStream out = new FileOutputStream(f);
            sub.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            if(f.exists()){
                return f.getAbsolutePath();
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String elementShot(String fileName, String target, int quality){
        UiObject obj = new UiObject(getUiSelector(target));
        if(!obj.waitForExists(60000)){
            return null;
        }
        try {
            android.graphics.Rect rect = obj.getBounds();
            return areaShot(fileName,rect.left, rect.top, rect.width(), rect.height(), quality);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  DriverResult clearCache(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            basementInterface.ClearPackage(target);
            ret.setCode(0);
            ret.setDescribe("清空缓存[" + target + "]成功");
        }catch(Exception e){
            ret.setCode(0);
            ret.setDescribe("清空缓存[" + target + "]失败:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult stopApp(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            basementInterface.StopPackage(target);
            ret.setCode(0);
            ret.setDescribe("停止[" + target + "]成功");
        }catch(Exception e){
            ret.setCode(0);
            ret.setDescribe("停止[" + target + "]失败:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult startApp(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            basementInterface.StartPackage(target);
            ret.setCode(0);
            ret.setDescribe("启动[" + target + "]成功");
        }catch(Exception e){
            ret.setCode(0);
            ret.setDescribe("启动[" + target + "]失败:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult longPress(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        UiObject obj = waitElement(target);
        if(obj != null) {
            ret.setStartTime(new Date());
            try {
                if (obj.longClick()) {
                    ret.setCode(0);
                    ret.setDescribe("长按[" + target + "]成功");
                } else {
                    ret.setCode(1);
                    ret.setDescribe("长按[" + target + "]失败");
                }
            } catch (UiObjectNotFoundException e) {
                ret.setCode(1);
                ret.setDescribe("长按[" + target + "]异常:" + e.getMessage());
                e.printStackTrace();
            }
        }else{
            ret.setCode(1);
            ret.setDescribe("长按[" + target + "]失败:元素未找到");
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult drag(String from, String to){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        try {
            if(from.contains("=") && to.contains("=")){
                UiObject obj_f = waitElement(from);
                UiObject obj_t = waitElement(to);
                if(obj_f != null && obj_t != null) {
                    ret.setStartTime(new Date());
                    if (obj_f.dragTo(obj_t, 20)) {
                        ret.setCode(0);
                        ret.setDescribe("拖动[" + from + "]到[" + to + "]成功");
                    } else {
                        ret.setCode(1);
                        ret.setDescribe("拖动[" + from + "]到[" + to + "]失败");
                    }
                }else{
                    ret.setCode(1);
                    ret.setDescribe("拖动[" + from + "]到[" + to + "]失败:元素未找到");
                }
            }else if (from.contains("=") && to.contains(",")){
                String[] lto = to.split(",");
                int x = Integer.parseInt(lto[0]);
                int y = Integer.parseInt(lto[1]);
                UiObject obj_f = waitElement(from);
                if(obj_f != null) {
                    ret.setStartTime(new Date());
                    if (obj_f.dragTo(x, y, 20)) {
                        ret.setCode(0);
                        ret.setDescribe("拖动[" + from + "]到[" + to + "]成功");
                    } else {
                        ret.setCode(1);
                        ret.setDescribe("拖动[" + from + "]到[" + to + "]失败");
                    }
                }else{
                    ret.setCode(1);
                    ret.setDescribe("拖动[" + from + "]到[" + to + "]失败:元素未找到");
                }
            }else if(from.contains(",") && to.contains(",")){
                String[] lts = to.split(",");
                String[] lfs = from.split(",");
                int x1 = Integer.parseInt(lfs[0]);
                int y1 = Integer.parseInt(lfs[1]);
                int x2 = Integer.parseInt(lts[0]);
                int y2 = Integer.parseInt(lts[1]);
                if(UiDevice.getInstance().drag(x1, y1, x2, y2, 20)){
                    ret.setCode(0);
                    ret.setDescribe("拖动[" + from + "]到[" + to + "]成功");
                }else{
                    ret.setCode(1);
                    ret.setDescribe("拖动[" + from + "]到[" + to + "]失败");
                }
            }else{
                ret.setCode(1);
                ret.setDescribe("拖动[" + from + "]到[" + to + "]失败");
            }
        } catch (UiObjectNotFoundException e) {
            ret.setCode(1);
            ret.setDescribe("拖动[" + from + "]到[" + to + "]异常:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            ret.setCode(1);
            ret.setDescribe("拖动[" + from + "]到[" + to + "]异常:" + e.getMessage());
            e.printStackTrace();
        }
        ret.setEndTime(new Date());
        return ret;
    }


    public DriverResult waitImageArea(String target, String value, int left, int top, int w, int h){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        Date t_start = new Date();
        long timeout = 3 * 60 *1000;
        double rate;
        try {
            rate = Double.parseDouble(value);
        }catch (Exception e){
            e.printStackTrace();
            ret.setEndTime(new Date());
            ret.setCode(1);
            ret.setDescribe("图片比对错误阈值[" + value + "]转换异常:" + e.getMessage());
            return ret;
        }
        while (true){
            if(new Date().getTime() - t_start.getTime() >= timeout){
                break;
            }
            String fileName = areaShot("compareImage.png", left, top, w, h, 100);
            if(fileName != null) {
                String template = compareImage(fileName, target, rate);
                System.out.print(template);
                if(template.contains("True")){
                    ret.setEndTime(new Date());
                    ret.setCode(0);
                    ret.setDescribe("比对图片成功:" + template);
                    ret.setDetail(template.split("#")[1]);
                    return ret;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ret.setCode(1);
        ret.setDescribe("比对图片失败:规定时间内未找到图片");
        ret.setEndTime(new Date());
        return ret;
    }


    public DriverResult waitImage(String target, String value){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        Date t_start = new Date();
        long timeout = 3 * 60 *1000;
        double rate;
        try {
            rate = Double.parseDouble(value);
        }catch (Exception e){
            e.printStackTrace();
            ret.setEndTime(new Date());
            ret.setCode(1);
            ret.setDescribe("图片比对错误阈值[" + value + "]转换异常:" + e.getMessage());
            return ret;
        }
        while (true){
            if(new Date().getTime() - t_start.getTime() >= timeout){
                break;
            }
            String fileName = screenShot("compareImage.png", 100);
            if(fileName != null) {
                String template = compareImage(fileName, target, rate);
                System.out.print(template);
                if(template.contains("True")){
                    ret.setEndTime(new Date());
                    ret.setCode(0);
                    ret.setDescribe("比对图片成功:" + template);
                    ret.setDetail(template.split("#")[1]);
                    return ret;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ret.setCode(1);
        ret.setDescribe("比对图片失败:规定时间内未找到图片");
        ret.setEndTime(new Date());
        return ret;
    }

    public  String compareImage(String source, String target, double rate){
        try {
            return  basementInterface.CompareImage(source, target, rate);
        }catch (Exception e){
            e.printStackTrace();
            return "False#" + e.getMessage();
        }
    }

    public  DriverResult waitKeyword(String success, String fail, long timeout){
        if((success == null || success.length() == 0) && (fail == null || fail.length() == 0)){
            DriverResult ret = new DriverResult();
            ret.setCode(1);
            ret.setDescribe("成功关键字与失败关键字都没有设置");
            return ret;
        }
        System.out.print("Wait Keyword " + success + "||||" + fail + "\r\n");
        String[] success_list = null;
        String[] fail_list = null;
        if(success != null && success.length() > 0){
            success_list = success.split(">>");
        }
        if(fail != null && fail.length() > 0){
            fail_list = fail.split(">>");
        }
        if((success != null && success.contains("==>")) || (fail != null && fail.contains("==>"))){
            return waitKeywordContent(success_list, fail_list, timeout * 1000);
        }else{
            return waitKeywordElement(success_list, fail_list, timeout * 1000);
        }
    }

    public UiObject waitElement(String target){
        try{
            UiObject obj = new UiObject(getUiSelector(target));
            if(obj.waitForExists(60000)){
                return obj;
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }


    public DriverResult waitKeywordElement(String[] success, String[] fail, long timeout){
        System.out.print("Wait Keyword Element\r\n");
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        Date start_time = new Date();
        while (true){
            Date end_time = new Date();
            if(end_time.getTime() - start_time.getTime() < timeout){
                if(fail != null && fail.length > 0) {
                    for (String str_f : fail) {
                        UiObject obj = new UiObject(new UiSelector().textContains(str_f));
                        ret.setEndTime(new Date());
                        if (obj.exists()) {
                            ret.setEndTime(new Date());
                            ret.setCode(1);
                            ret.setDescribe("Found Fail Key Word [" + str_f + "]");
                            return ret;
                        }
                    }
                }
                if(success != null && success.length > 0) {
                    for (String str_s : success) {
                        UiObject obj = new UiObject(new UiSelector().textContains(str_s));
                        ret.setEndTime(new Date());
                        if (obj.exists()) {
                            ret.setEndTime(new Date());
                            ret.setCode(0);
                            ret.setDescribe("Found Success Key Word [" + str_s + "]");
                            return ret;
                        }
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                ret.setEndTime(new Date());
                ret.setCode(1);
                ret.setDescribe("Wait Keyword Time Out");
                break;
            }
        }
        return ret;
    }

    public boolean isBeh(String content, String beg, String beh){
        if(content.contains(beg) && content.contains(beh)){
            int beg_index = content.indexOf(beg);
            int beh_index = content.indexOf(beh, beg_index);
            return beg_index >=0 && beh_index >= 0 && beh_index > beg_index;
        }else {
            return false;
        }
    }

    public String getMidStr(String content, String beg, String beh){
        String ret;
        try{
            if(content.contains(beg) && content.contains(beh)){
                int beg_index = content.indexOf(beg);
                int beh_index = content.indexOf(beh, beg_index);
                if(beg_index >=0 && beh_index >= 0 && beh_index > beg_index){
                    ret = content.substring(beg_index + beg.length(), beh_index);
                }else{
                    ret = "";
                }
            }else{
                ret = "";
            }
        }catch (Exception e){
            e.printStackTrace();
            ret = "";
        }
        return ret;
    }

    public boolean isKeywordInPageContent(String content, String keyword){
        if(keyword.contains("==>")){
            String[] ls = keyword.split("==>");
            if(ls.length == 2){
                return isBeh(content, ls[0], ls[1]);
            }else if(ls.length == 3){
                String mid = getMidStr(content, ls[0], ls[2]);
                if(mid.length() == 0){
                    return false;
                }else{
                    if(ls[1].toLowerCase().equals("num")){
                        try{
                            Float f = Float.parseFloat(mid);
                            System.out.print("Extract Value is :" + f);
                            return true;
                        }catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }else{
                        return true;
                    }
                }
            }else{
                return false;
            }
        }else{
            return content.contains(keyword);
        }
    }

    public DriverResult waitKeywordContent(String[] success, String[] fail, long timeout){
        System.out.print("Wait Keyword Content\r\n");
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        Date start_time = new Date();
        while (true) {
            Date end_time = new Date();
            if (end_time.getTime() - start_time.getTime() < timeout) {
                DriverResult content = getPageContent();
                ret.setEndTime(new Date());
                if(content.getCode()==0){
                    if(fail != null && fail.length > 0) {
                        for (String str_f : fail) {
                            if (isKeywordInPageContent(content.getDetail(), str_f)) {
                                ret.setCode(1);
                                ret.setDescribe("Found Fail Key Word [" + str_f + "]");
                                return ret;
                            }
                        }
                    }
                    if(success != null && success.length > 0) {
                        for (String str_s : success) {
                            if (isKeywordInPageContent(content.getDetail(), str_s)) {
                                ret.setCode(0);
                                ret.setDescribe("Found Success Key Word [" + str_s + "]");
                                return ret;
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                ret.setEndTime(new Date());
                ret.setCode(1);
                ret.setDescribe("Wait Keyword Time Out");
                break;
            }
        }
        return ret;
    }

    public String dumpWindowHierarchy(boolean compressed, String filename) {
        if (Build.VERSION.SDK_INT >= 18)
            UiDevice.getInstance().setCompressedLayoutHeirarchy(compressed);
        File parent = new File(Environment.getDataDirectory(), "local/tmp");
        if (!parent.exists()){
            boolean b = parent.mkdirs();
            System.out.print(b);
        }
        boolean return_value = false;
        if (filename == null || filename.length() == 0) {
            filename = "dump.xml";
            return_value = true;
        }
        File dumpFile = new File(parent, filename).getAbsoluteFile();
        UiDevice.getInstance().dumpWindowHierarchy(filename);
        File f = new File(STORAGE_PATH, filename);
        if (!f.exists()) f = dumpFile;
        if (f.exists()) {
            if (return_value) {
                BufferedReader reader = null;
                try {
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new FileReader(f));
                    char[] buffer = new char[4096];
                    int len;
                    while ((len = reader.read(buffer)) != -1) {
                        sb.append(new String(buffer, 0, len));
                    }
                    reader.close();
                    reader = null;
                    return sb.toString();
                } catch (IOException e) {
					e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return null;
            } else
                return f.getAbsolutePath();
        } else
            return null;
    }

    public DriverResult clickIfExist(String target){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        UiObject obj = new UiObject(getUiSelector(target));
        if(obj.waitForExists(60000)){
            try{
                if(obj.click()) {
                    ret.setCode(0);
                    ret.setDescribe("点击元素[" + target + "]成功");
                }else{
                    ret.setCode(1);
                    ret.setDescribe("点击元素[" + target + "]失败");
                }
            }catch (Exception e){
                e.printStackTrace();
                ret.setCode(1);
                ret.setDescribe("点击元素[" + target + "]异常:" + e.getMessage());
            }
        }else{
            ret.setCode(0);
            ret.setDescribe("元素[" + target + "]未出现");
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public DriverResult inputIfExist(String target, String val){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        UiObject obj = new UiObject(getUiSelector(target));
        if(obj.waitForExists(60000)){
            try{
                if(obj.setText(val)) {
                    ret.setCode(0);
                    ret.setDescribe("输入元素[" + target + "]成功");
                }else{
                    ret.setCode(1);
                    ret.setDescribe("输入元素[" + target + "]失败");
                }
            }catch (Exception e){
                e.printStackTrace();
                ret.setCode(1);
                ret.setDescribe("输入元素[" + target + "]异常:" + e.getMessage());
            }
        }else{
            ret.setCode(0);
            ret.setDescribe("元素[" + target + "]未出现");
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public  DriverResult getPageContent(){
        DriverResult ret = new DriverResult();
        ret.setStartTime(new Date());
        String file = dumpWindowHierarchy(true, "pageContent.xml");
        if (file == null){
            ret.setCode(1);
            ret.setDescribe("获取页面内容失败");
        }else{
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                org.w3c.dom.Document document = builder.parse(new File(file));
                XPathFactory pathFactory = XPathFactory.newInstance();
                XPath xpath = pathFactory.newXPath();
                XPathExpression pathExpression = xpath.compile("//*[@text!='' or @content-desc!='']");
                Object result = pathExpression.evaluate(document, XPathConstants.NODESET);
                org.w3c.dom.NodeList nodes = (NodeList) result;
                String content = "";
                for (int i = 0; i < nodes.getLength(); i++) {
                    NamedNodeMap attrs = nodes.item(i).getAttributes();
                    content += attrs.getNamedItem("text").getTextContent();
                    content += attrs.getNamedItem("content-desc").getTextContent();
                }
                ret.setCode(0);
                ret.setDescribe("获取页面内容成功");
                ret.setDetail(content);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                ret.setCode(1);
                ret.setDescribe("获取页面内容异常:" + e.getMessage());
            } catch (SAXException e) {
                e.printStackTrace();
                ret.setCode(1);
                ret.setDescribe("获取页面内容异常:" + e.getMessage());
            } catch (IOException e) {
                ret.setCode(1);
                ret.setDescribe("获取页面内容异常:" + e.getMessage());
                e.printStackTrace();
            } catch (XPathExpressionException e) {
                ret.setCode(1);
                ret.setDescribe("获取页面内容异常:" + e.getMessage());
                e.printStackTrace();
            }
        }
        ret.setEndTime(new Date());
        return ret;
    }

    public UiSelector getUiSelector(String target){
        UiSelector selector = new UiSelector();
        HashMap<String, String> locator = getLocator(target);
        for(Map.Entry<String, String>entry : locator.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("text")){
                selector = selector.text(value);
            } else if(key.equals("resourceId")){
                selector = selector.resourceId(value);
            }else if (key.equals("textContains")){
                selector = selector.textContains(value);
            } else if (key.equals("className")){
                selector = selector.className(value);
            }else if (key.equals("description")){
                selector = selector.description(value);
            }else if (key.equals("descriptionContains")){
                selector = selector.descriptionContains(value);
            }else if(key.equals("index")){
                try {
                    int v = Integer.parseInt(value);
                    selector = selector.index(v);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return selector;
    }

    public Point getPoint(String target){
        String[] st = target.split(",");
        if(st.length == 2) {
            try{
                Point point = new Point();
                point.set(Integer.parseInt(st[0]), Integer.parseInt(st[1]));
                return point;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public HashMap<String, String> getLocator(String target){
        HashMap<String, String> map = new HashMap<String, String>();
        try{
            if(target.contains("@")){
                String[] ts = target.split("@");
                for (String t : ts) {
                    if (t.contains("=")) {
                        String[] tt = t.split("=");
                        map.put(tt[0], tt[1]);
                    } else {
                        map.put("text", t);
                    }
                }
            }else if(target.contains("=")){
                String[] ss = target.split("=");
                map.put(ss[0], ss[1]);
            }else{
                map.put("text", target);
            }
            return map;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
