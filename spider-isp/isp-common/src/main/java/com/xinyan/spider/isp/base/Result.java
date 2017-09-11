package com.xinyan.spider.isp.base;

/**
 * 数据抓取处理结果
 */
public class Result implements java.io.Serializable{
	
    /**
	 * @Fields serialVersionUID 
	 */
	private static final long serialVersionUID = 3548459998108941175L;
	
	//处理消息码
    private String code;
    //处理描述
    private String msg;
    //处理结果
    private Object data;

    public Result(){
    	super();
    	setFail();
    }
    
    public Result(StatusCode statusCode){
    	super();
    	this.code = statusCode.getCode();
    	this.msg = statusCode.getMsg();
    }

    public void setResult(StatusCode statusCode){
    	this.code = statusCode.getCode();
    	this.msg = statusCode.getMsg();
    }
    
    public void setSuccess(){
    	this.code = StatusCode.SUCCESS.getCode();
    	this.msg = StatusCode.SUCCESS.getMsg();
    }

    public void setFail(){
    	this.code = StatusCode.FAILURE.getCode();
    	this.msg = StatusCode.FAILURE.getMsg();
    }

    public void setErrorCode(String code){
        setCode(code);
        setMsg("处理失败");
    }

	public boolean isSuccess(){
        if(StatusCode.SUCCESS.getCode().equals(getCode())){
            return true;
        }
        return false;
    }

    public String getCode() {
        return null==code?"":code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return null==msg?"":msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

	@Override
	public String toString() {
		return " [code=" + code + ", msg=" + msg + ", result=" + isSuccess() + "]";
	}
}
