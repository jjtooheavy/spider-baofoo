package com.xinyan.spider.isp.common;

/**
 * Constants
 *
 * @author Yu Yangjun
 * @date 2016/7/9
 */
public class Constants {
    //日志跟踪标识
    public final static String SESSION_ID="sessionId";
	public final static String SPIDER_NAME = "运营商爬虫";//爬虫名称

	//运营商类型
	public final static String UNICOM="联通";
	public final static String CMCC = "移动";
	public final static String TELECOM = "电信";
    //验证码错误尝试次数
    public final static int MAX_RETRY_TIIMES=5;
    //短信验证发送尝试次数
    public final static int MAX_SEND_SMS_TIIMES=2;
	//尝试调去接口次数
	public final static int MAX_SENDMSG_TIIMES=3;
	public final static int MAX_SENDCRAWLERMSG_TIIMES=3;
	public final static int MAX_SENDANALYSISMSG_TIIMES=3;
	//验证码类型
	public final static String VALIDATE_TYPE_CODE_SMS = "SMS";//验证码-短信
	public final static String VALIDATE_TYPE_CODE_IMG = "IMG";//验证码-图片
	public final static String VALIDATE_TYPE_CODE_PWD = "PWD";//验证码-二次验证码等
	
	/**
     * 费用类型
     */
    public enum HistoryBill {

        月固定费("月固定费"),
        基本月租("基本月租"),
        上网费("上网费"),
        短信通信费("短信通信费"),
        代收费("代收费（信息费）"),
        特服费("特服费"),
        其他费用("其他费用"),
        抵扣合计("抵扣合计"),
        实际应缴合计("实际应缴合计"),
        基本套餐费("基本套餐费"),
        增值业务费("增值业务费"),
        语音通话费("语音通话费"),
        网络流量费("网络流量费"),
        总费用("总费用"),
        优惠费("优惠费"),
        其它优惠("其它优惠"),
        个人实际费用("个人实际费用"),
        承诺话费补足费用("承诺话费补足费用"),
        联通在信("联通在信"),
        短彩信费("短彩信费"),
        国内通话费("国内通话费"),;

        HistoryBill(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        //费用名称
        private String name;
    }
	/*
	 *手机号状态码
	 * -1  未知
     0 	正常
     1 	单向停机
     2 	停机
     3 	预销户
     4 	销户
     5 	过户
     6 	改号
     99 	号码不存在
	 */
	public static final String UNKNOWN = "未知";
	public static final String NORMAL = "正常/开户";
	public static final String UNIDIRECTIONAL_STOP = "单向停机";
	public static final String STOP = "停机";
	public static final String PRE_CANCELLATION = "预销户";
	public static final String CANCELLATION = "销户";
	public static final String TRANSFER = "过户";
	public static final String CHANGE = "改号";
	public static final String NOT_EXIST = "号码不存在";


	/**
	 * 服务器端ip
	 */
	public static final String LOCAL_IP="127.0.0.1";
	public static final String LOCALHOST_IP="0:0:0:0:0:0:0:1";

	/**
	 * 超时时间
	 */
	public final static int ENQUEUE_TIME_OUT=120;//入队过期时间
	public final static int DEQUEUE_TIME_OUT=300;//出队等待时间
	
	/**
	 * 消息交互
	 */
	public final static String SMS_RES_PREFIX="sms_res_";//消息交互响应
	public final static String SMS_REQ_PREFIX="sms_req_";//消息交互请求
}
