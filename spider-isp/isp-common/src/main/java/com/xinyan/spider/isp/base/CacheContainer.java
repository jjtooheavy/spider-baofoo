package com.xinyan.spider.isp.base;

import com.gargoylesoftware.htmlunit.Page;

import java.util.HashMap;
import java.util.List;

/**
 * CacheConta
 *
 * @author Yu Yangjun
 * @date 2016/8/21
 */
public final  class CacheContainer {
    /*存放缓存内容*/
    private HashMap<String,Page> contentPageMap = new HashMap<>();
    /*存放缓存内容*/
    private HashMap<String,List<Page>> contentPagesMap = new HashMap<>();

    private HashMap<String,String> contentStringMap = new HashMap<>();
    private HashMap<String,List<String>> contentStringsMap = new HashMap<>();

    /**
     *向缓存中取数据
     *
     * @param key
     * @param value
     */
    public void putPage(String key, Page value){
    	contentPageMap.put(key,value);
    }
    
    /**
     * 从缓存中取数据
     * @param key
     * @return
     */
    public Page getPage(String key){
        return contentPageMap.get(key);
    }
    
    /**
     *向缓存中取数据
     *
     * @param key
     * @param value
     */
    public void putString(String key, String value){
    	contentStringMap.put(key,value);
    }

    /**
     * 从缓存中取数据
     * @param key
     * @return
     */
    public String getString(String key){
        return contentStringMap.get(key);
    }

    /**
     *向缓存中存数据
     * @param key
     * @param pages
     */
    public void putPages(String key, List<Page> pages){
    	contentPagesMap.put(key,pages);
    }
    
    /**
     * 从缓存中取数据
     * @param key
     * @return
     */
    public List<Page> getPages(String key){
        return contentPagesMap.get(key);
    }
    
    /**
     *向缓存中存数据
     * @param key
     * @param pagesInfo
     */
    public void putStrings(String key, List<String> pagesInfo){
      	contentStringsMap.put(key, pagesInfo);
    }
    
    /**
     * 从缓存中取数据
     * @param key
     * @return
     */
    public List<String> getStrings(String key){
        return contentStringsMap.get(key);
    }
}
