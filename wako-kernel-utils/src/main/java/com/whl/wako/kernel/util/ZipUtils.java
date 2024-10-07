package com.whl.wako.kernel.util;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * 压缩文件工具类
 *
 * @author paul.wei 2022年3月1日
 */
public class ZipUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);


    public static void doCompress(String srcFile, String zipFile) throws Exception {
        doCompress(new File(srcFile), new File(zipFile));
    }

    /**
     * 文件压缩
     *
     * @param srcFile  目录或者单个文件
     * @param destFile 压缩后的ZIP文件
     */
    public static void doCompress(File srcFile, File destFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destFile));
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File file : files) {
                doCompress(file, out);
            }
        } else {
            doCompress(srcFile, out);
        }
    }

    public static void doCompress(String pathname, ZipOutputStream out) throws IOException {
        doCompress(new File(pathname), out);
    }

    public static void doCompress(File file, ZipOutputStream out) throws IOException {
        if (!file.exists()) {
            return;
        }
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(file);
        out.putNextEntry(new ZipEntry(file.getName()));
        int len = 0;
        // 读取文件的内容,打包到zip文件
        while ((len = fis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.closeEntry();
        fis.close();
    }

    /**
     * 压缩文件列表中的文件
     *
     * @param files
     * @param outputStream
     * @throws IOException
     */
    public static void zipFile(List files, ZipOutputStream outputStream) {
            int size = files.size();
            // 压缩列表中的文件
            for (int i = 0; i < size; i++) {
                try {
                    File file = (File) files.get(i);
                    zipFile(file, outputStream);
                }catch (Throwable e) {
                    logger.error("zipFile exception",e);
                }
            }
    }

    /**
     * 将文件写入到zip文件中
     *
     * @param inputFile
     * @param outputstream
     * @throws Exception
     */
    public static void zipFile(File inputFile, ZipOutputStream outputstream)  {
        try {
            AssertUtils.notNull(inputFile,"zipFile outputstream inputFile must not empty");
            if (inputFile.exists()) {
                if (inputFile.isFile()) {
                    FileInputStream inStream = new FileInputStream(inputFile);
                    BufferedInputStream bInStream = new BufferedInputStream(inStream);
                    ZipEntry entry = new ZipEntry(inputFile.getName());
                    outputstream.putNextEntry(entry);

                    final int MAX_BYTE = 10 * 1024 * 1024; // 最大的流为10M
                    long streamTotal = 0; // 接受流的容量
                    int streamNum = 0; // 流需要分开的数量
                    int leaveByte = 0; // 文件剩下的字符数
                    byte[] inOutbyte; // byte数组接受文件的数据

                    streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
                    streamNum = (int) Math.floor(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
                    leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量

                    if (streamNum > 0) {
                        for (int j = 0; j < streamNum; ++j) {
                            inOutbyte = new byte[MAX_BYTE];
                            // 读入流,保存在byte数组
                            bInStream.read(inOutbyte, 0, MAX_BYTE);
                            outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
                        }
                    }
                    // 写出剩下的流数据
                    inOutbyte = new byte[leaveByte];
                    bInStream.read(inOutbyte, 0, leaveByte);
                    outputstream.write(inOutbyte);
                    outputstream.closeEntry();
                    bInStream.close(); // 关闭
                    inStream.close();
                }
            } else {
                logger.error("zipFile inputFile not exists path:"+inputFile.getCanonicalPath());
            }
        }catch (IllegalArgumentException e) {
            logger.error("zipFile IllegalArgumentException exception ",e);
        } catch (Exception e) {
            logger.error("zipFile outputstream exception "+inputFile.getName(),e);
        }
    }
}

