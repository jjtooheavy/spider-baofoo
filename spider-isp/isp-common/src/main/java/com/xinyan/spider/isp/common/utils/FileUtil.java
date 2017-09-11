package com.xinyan.spider.isp.common.utils;

import java.io.File ;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    public static byte[] getBytesFromFile(File f) throws Exception {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(f);
            byte[] b = new byte[fileInputStream.available()];
            fileInputStream.read(b);
            return b;
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
            }
        }

    }
    
    /**
     * 将数据写到文件中
     * @description 
     * @author heliang
     * @create 2016年9月10日 上午9:56:15
     * @param savePath
     * @param data
     * @throws Exception
     */
    public static void wirteDataToFile(String filename, byte[] data) throws Exception{
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(data);
            fileOutputStream.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * 将流写到文件 中
     * @description 
     * @author heliang
     * @create 2016年9月10日 上午9:55:53
     * @param filename
     * @param input
     * @throws Exception
     */
    public static void wirteStreamToFile(String filename, InputStream input) {
    	FileOutputStream fileOutputStream = null;
    	try {
    		File file = new File(filename);
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = input.read(buffer, 0, 8192)) != -1) {
			     os.write(buffer, 0, bytesRead);
			}
			os.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if (fileOutputStream != null) {
    				fileOutputStream.close();
    			}
    		} catch (IOException e) {
    		}
    	}
    }

}
