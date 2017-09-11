package com.xinyan.spider.isp.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


/**
 * CrawlerRequestModel
 *
 * @author heliang
 * @date 2016/7/6
 */
public class CookieInfo implements Serializable {
    private static final long serialVersionUID = 4147219031540970562L;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("httpOnly")
    private String httpOnly;
    @JsonProperty("name")
    private String name;
    @JsonProperty("path")
    private String path;
    @JsonProperty("secure")
    private String secure;
    @JsonProperty("value")
    private String value;
    @JsonProperty("expires")
    private String expires;
    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(String httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
