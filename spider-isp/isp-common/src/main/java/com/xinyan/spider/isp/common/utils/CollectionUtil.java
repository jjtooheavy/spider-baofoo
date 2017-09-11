package com.xinyan.spider.isp.common.utils;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

/**
 * CollectionUtil
 *
 * @author yyj
 * @version 1.0 16-6-18
 */
public class CollectionUtil {
	/**
	 * 判断集合是否为空
	 * @param coll
	 * @return
	 */
	public static boolean isEmpty(Collection<?> coll){
		return CollectionUtils.isEmpty(coll);
	}
	
	/**
	 * 判断集合是否不为空
	 * @param coll
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> coll){
		return CollectionUtils.isNotEmpty(coll);
	}
	
	/**
	 * 合并集合a b
	 * @param a
	 * @param b
	 * @return
	 */
	public static Collection<?> union(Collection<?> a,Collection<?> b){
		return CollectionUtils.union(a, b);
	}
}
