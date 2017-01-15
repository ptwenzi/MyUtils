package com.newland.appdriver;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DriverResult {
    private int _code;
    private Date _startTime;
    private Date _endTime;
    private String _describe = "";
    private String _detail = "";
    private long _consume = 0;
    private String _methodName = "";

    public void setCode(int code) {
        this._code = code;
    }

    public void setStartTime(Date date){
        this._startTime = date;
    }

    public void setEndTime(Date date){
        this._endTime = date;
    }

    public void setDescribe(String desc){
        this._describe = desc;
    }

    public void setDetail(String detail){
        this._detail = detail;
    }

    public void setMethodName(String methodName){this._methodName = methodName;}

    public int getCode() {
        return _code;
    }

    public String getStrStartTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sf.format(_startTime);
    }

    public String getStrEndTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sf.format(_endTime);
    }

    public String getDetail(){
        return _detail;
    }

    public long getConsume(){
        if(_startTime == null){
            _startTime = new Date();
        }
        if(_endTime == null){
            _endTime = new Date();
        }
        _consume = _endTime.getTime() - _startTime.getTime();
        return _consume;
    }

    public String toJson(){
        String ret;
        JSONObject obj = new JSONObject();
        try {
            getConsume();
            obj.put("code", _code);
            obj.put("start_time", getStrStartTime());
            obj.put("end_time", getStrEndTime());
            obj.put("describe", _describe);
            obj.put("detail", _detail);
            obj.put("consume", _consume);
            obj.put("method_name", _methodName);
            ret = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            ret = "";
        }
        return ret;
    }

    public String getExceptResponse(String msg) {
        setCode(1);
        setDetail("Execute Exception:" + msg);
        setStartTime(new Date());
        setEndTime(new Date());
        return toJson();
    }
}
