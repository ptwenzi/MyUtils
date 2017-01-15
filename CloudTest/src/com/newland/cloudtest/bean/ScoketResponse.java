package com.newland.cloudtest.bean;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ScoketResponse {
    private int _code;  //0成功
    private Date _startTime;
    private Date _endTime;
    private String _describe = "";//描述
    private String _detail = "";//内容
    private long _consume = 0;//耗时 
    private String method_name;
    
    

    public String getMethod_name() {
		return method_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}

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

    public int getCode() {
        return _code;
    }

    public Date getStartTime(){
        return _startTime;
    }

    public String getStrStartTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA);
        return sf.format(_startTime);
    }

    public String getStrEndTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.CHINA);
        return sf.format(_endTime);
    }

    public Date getEndTime(){
        return this._endTime;
    }

    public String getDescribe(){
        return _describe;
    }

    public String getDetail(){
        return _detail;
    }

    public long getConsume(){
        _consume = _endTime.getTime() - _startTime.getTime();
        return _consume;
    }

    public String toJson(){
        String ret = "";
        JSONObject obj = new JSONObject();
        try {
            getConsume();
            obj.put("code", _code);
            obj.put("start_time", getStrStartTime());
            obj.put("end_time", getStrEndTime());
            obj.put("describe", _describe);
            obj.put("detail", _detail);
            obj.put("consume", _consume);
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
