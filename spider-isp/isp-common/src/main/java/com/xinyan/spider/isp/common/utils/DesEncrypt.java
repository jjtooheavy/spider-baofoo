package com.xinyan.spider.isp.common.utils;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * des加密解密
 * 
 * @author liutq
 * 
 */
public class DesEncrypt {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesEncrypt.class);
	Key key;
	
	private static final Map<String,String> replaceStr = new HashMap<String,String>();
	
	static {
		replaceStr.put("\\+", "\\[j]"); 
		replaceStr.put("/", "\\[x]");
		replaceStr.put("=", "\\[d]");
	}


	public DesEncrypt(String str) {
		setKey(str);// 生成密匙
	}
	/**
	 * 替换密文中的特殊字符
	 * @param str
	 * @return
	 */
	public String replaceKeyByValue(String str){
		Iterator<Entry<String, String>> entrys = replaceStr.entrySet().iterator();
		while(entrys.hasNext()){
			Entry<String, String> entry = entrys.next();
			str = str.replaceAll(entry.getKey(), entry.getValue());
		}
		return str;
	}
	/**
	 * 还原密文中的特殊字符
	 * @param str
	 * @return
	 */
	public String replaceValueByKey(String str){
		Iterator<Entry<String, String>> entrys = replaceStr.entrySet().iterator();
		while(entrys.hasNext()){
			Entry<String, String> entry = entrys.next();
			str = str.replaceAll(entry.getValue(), entry.getKey());
		}
		return str;
	}

	/**
	 * 根据参数生成KEY
	 */
	public void setKey(String strKey) {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			this.key = keyFactory.generateSecret(new DESKeySpec(strKey
					.getBytes("UTF8")));
		} catch (Exception e) {
			throw new RuntimeException(
					"Error initializing SqlMap class. Cause: " + e);
		}
	}

	/**
	 * 加密list中的某个值，加密后的值key=e+原来的key
	 * @param list
	 * @param keys
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void encryptInList(List<Map> list, Object[] keys) {
		for (Map m : list) {
			for (Object key : keys) {
				if (m.containsKey(key)) {
					Object val = m.get(key);
					String param = "";
					if (val instanceof String) {
						param = (String) val;
					} else if (val instanceof Integer) {
						param = String.valueOf((Integer) val);
					} else if (val instanceof Long) {
						param = String.valueOf((Long) val);
					}
					String encKey = "e" + key;
					m.put(encKey, encrypt(param));
				}
			}
		}
	}

	/**
	 * 加密String明文输入,String密文输出
	 */
	public String encrypt(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		Base64 base64en = new Base64();
		try {
			byteMing = strMing.getBytes("UTF8");
			byteMi = this.getEncCode(byteMing);
			strMi = base64en.encodeAsString(byteMi);
		} catch (Exception e) {
			LOGGER.error("encrypt error:"+strMing,e);
			return null;
		} finally {
			base64en = null;
			byteMing = null;
			byteMi = null;
		}
		return replaceKeyByValue(strMi);
	}

	/**
	 * 解密 以String密文输入,String明文输出
	 * 
	 * @param strMi
	 * @return
	 */
	public String decrypt(String strMi) {
		strMi = replaceValueByKey(strMi);
		Base64 base64De = new Base64();
		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			byteMi = base64De.decode(strMi);
			byteMing = this.getDesCode(byteMi);
			strMing = new String(byteMing, Charset.defaultCharset());
		} catch (Exception e) {
			LOGGER.error("decrypt error:"+strMi,e);
			return null;
		} finally {
			base64De = null;
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	/**
	 * 加密以byte[]明文输入,byte[]密文输出
	 * 
	 * @param byteS
	 * @return
	 */
	private byte[] getEncCode(byte[] byteS) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key,
					SecureRandom.getInstance("SHA1PRNG"));
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			throw new RuntimeException(
					"Error initializing SqlMap class. Cause: " + e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 解密以byte[]密文输入,以byte[]明文输出
	 * 
	 * @param byteD
	 * @return
	 */
	private byte[] getDesCode(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key,
					SecureRandom.getInstance("SHA1PRNG"));
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			throw new RuntimeException(
					"Error initializing SqlMap class. Cause: " + e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	


}
