package com.xinyan.spider.isp.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * PageUtils
 *
 * @author Yu Yangjun
 * @date 2016/8/21
 */
public class PageUtils {
    public static String getValueById(Page page, String id){
        String result="";
        DomElement domElement=((HtmlPage)page).getElementById(id);
        if(domElement!=null){
            result=domElement.getAttribute("value");
        }
        return result;
    }

    public static String getValueByName(Page page, String name){
        String result="";
        DomElement domElement=((HtmlPage)page).getElementByName(name);
        if(domElement!=null){
            result=domElement.getAttribute("value");
        }
        return result;
    }

    public static String getValueByXpath(Page page, String xpath){
        String result="";
        HtmlElement htmlElement=((HtmlPage)page).getFirstByXPath(xpath);
        if(htmlElement!=null){
            result=htmlElement.asText();
        }
        return result;
    }

    public static List<String> getListValueByXpath(Page page, String xpath){
        List<String> result=new ArrayList<>();
        @SuppressWarnings("unchecked")
		List<HtmlElement> elements=(List<HtmlElement>)((HtmlPage)page).getByXPath(xpath);
        if(CollectionUtil.isNotEmpty(elements)){
            for(HtmlElement element:elements){
                String value=element.asText();
                result.add(value) ;
            }
        }
        return result;
    }
    public static String getListValuesByXpath(Page page, String xpath){
    	StringBuffer result = new StringBuffer();
    	@SuppressWarnings("unchecked")
    	List<HtmlElement> elements=(List<HtmlElement>)((HtmlPage)page).getByXPath(xpath);
    	if(CollectionUtil.isNotEmpty(elements)){
    		for(HtmlElement element:elements){
    			String value=element.asText();
    			result.append(value+"\n") ;
    		}
    	}
    	return result.toString();
    }

    public static String getBrValuesByXpath(Page page, String xpath){
    	StringBuffer result = new StringBuffer();
    	@SuppressWarnings("unchecked")
    	List<HtmlElement> elements=(List<HtmlElement>)((HtmlPage)page).getByXPath(xpath);
    	if(CollectionUtil.isNotEmpty(elements)){
    		for(HtmlElement element:elements){
    			String value=element.asText();
    			result.append(value+"<br></br>") ;
    		}
    	}
    	return result.toString();
    }

    public static String getValueByXpath(HtmlElement element, String xpath){
        String result="";
        HtmlElement htmlElement=element.getFirstByXPath(xpath);
        if(htmlElement!=null){
            result=htmlElement.asText();
        }
        return result;
    }

    public static List<String> getListValueByXpath(HtmlElement ele, String xpath){
        List<String> result=new ArrayList<>();
        @SuppressWarnings("unchecked")
		List<HtmlElement> elements=(List<HtmlElement>)ele.getByXPath(xpath);
        if(CollectionUtil.isNotEmpty(elements)){
            for(HtmlElement element:elements){
                String value=element.asText();
                result.add(value) ;
            }
        }
        return result;
    }

    /**
     * 换行不加br处理
     * @description
     * @author heliang
     * @create 2016年9月8日 下午3:19:41
     * @param ele
     * @param xpath
     * @return
     */
    public static String getListValuesByXpath(HtmlElement ele, String xpath){
        StringBuffer result = new StringBuffer();
        @SuppressWarnings("unchecked")
        List<HtmlElement> elements=(List<HtmlElement>)ele.getByXPath(xpath);
        if(CollectionUtil.isNotEmpty(elements)){
            for(HtmlElement element:elements){
                String value=element.asText();
                result.append(value + "<br></br>") ;
            }
        }
        return result.toString();
    }

    /**
     * 换行加br处理
     * @description
     * @author heliang
     * @create 2016年9月8日 下午3:19:41
     * @param ele
     * @param xpath
     * @return
     */
    public static String getBrValuesByXpath(HtmlElement ele, String xpath){
        StringBuffer result = new StringBuffer();
        @SuppressWarnings("unchecked")
        List<HtmlElement> elements=(List<HtmlElement>)ele.getByXPath(xpath);
        if(CollectionUtil.isNotEmpty(elements)){
            for(HtmlElement element:elements){
                String value=element.asText();
                result.append(value + "<br></br>") ;
            }
        }
        return result.toString();
    }

    public static String getAttrValueByXpath(HtmlElement element, String xpath){
        String result="";
        DomAttr domAttr=element.getFirstByXPath(xpath);
        if(domAttr!=null){
            result=domAttr.getValue();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
	public static List<HtmlElement> getElementByXpath(Page page, String xpath){
       return (List<HtmlElement>)((HtmlPage)page).getByXPath(xpath);
    }

    @SuppressWarnings("unchecked")
    public static List<HtmlElement> getElementByXpath(HtmlElement element, String xpath){
        return (List<HtmlElement>)element.getByXPath(xpath);
    }

    public static HtmlElement getFirstElementByXpath(Page page, String xpath){
        return  ((HtmlPage)page).getFirstByXPath(xpath);
    }

    public static HtmlElement getFirstElementByXpath(HtmlElement element, String xpath){
        return  element.getFirstByXPath(xpath);
    }
}
