package Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * 文件工具类
 * @作者 lichao
 * @时间 2016年10月18日 上午8:55:50
 * @说明
 */
public class FileUtil {
	
	private static final Logger logger = Logger.getLogger(FileUtil.class);
	
	/**
     * 复制文件
     * @param sourceFile 源文件地址及文件名
     * @param targetFile 目标文件地址及文件名
     * @throws IOException
     */
	public static void copyFile(String sourceFile, String targetFile) throws IOException {
		logger.info("【复制文件】开始");
		logger.info("【复制文件】原文件："+sourceFile);
		logger.info("【复制文件】目标文件:"+targetFile);
		
		BufferedInputStream inBuff = null;
	    BufferedOutputStream outBuff = null;
        // 新建文件输入流并对它进行缓冲
        inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
        // 新建文件输出流并对它进行缓冲
        outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        
        // 刷新此缓冲的输出流
        outBuff.flush();
        // 关闭流
        if (inBuff != null)
            inBuff.close();
        if (outBuff != null)
            outBuff.close();
	    
	    logger.info("【复制文件】结束");
	}
	
}
