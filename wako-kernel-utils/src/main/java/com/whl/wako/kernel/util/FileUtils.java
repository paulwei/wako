package com.whl.wako.kernel.util;

import com.whl.wako.kernel.util.enums.ByteUnit;
import com.vip.vjtools.vjkit.base.Platforms;
import com.vip.vjtools.vjkit.base.annotation.NotNull;
import com.vip.vjtools.vjkit.io.FileUtil;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author paul.wei
 * @Description: 文件操作类
 * @date 2018/7/19
 */
public class FileUtils extends FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);


    public static List<String> delete(long max,String path,ByteUnit byteUnit){
        List<File> files  =  traverse(path);
        return delete(max,byteUnit,files,null);
    }

    public static List<String> deleteExcel(long max,String path,ByteUnit byteUnit){
        List<File> files  =  traverse(path);
        return delete(max,byteUnit,files,"xls","xlsx");
    }

    public static List<String> delete(long max,String path,ByteUnit byteUnit,String... extensions){
        List<File> files  =  traverse(path);
        return delete(max,byteUnit,files,extensions);
    }
    /**
     * @Description:超过max阈值，删除时间最早
     * @params [max, byteUnit, list]
     * @return java.util.List<java.lang.String>
     * @author paul.wei
     * @date 2018/7/19
     */
    public static List<String> delete(long max,ByteUnit byteUnit,List<File> list,String... extensions){
        List<String> deleted = new ArrayList<>();
        if(BeanUtils.isEmpty(list)){
            return deleted;
        }
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(o1==null){
                    return 1;
                }else if(o2==null){
                    return -1;
                }
                long diff = o2.lastModified()-o1.lastModified();//强转int溢出
                return diff<=0?-1:1;
            }
        });
        long total=0;
        long maxByte = byteUnit.toByte(max);
        for(File file:list){
            total+=file.length();
            String extension = getFileExtension(file);
            if(total<=maxByte || !BeanUtils.contains(extension,extensions)){
                continue;
            }
            try {
                deleteFile(file);
                deleted.add(file.getAbsolutePath());
            } catch (IOException e) {}
        }
        return deleted;
    }
    /**
     * @Description:递归创建目录
     * @params [file]
     * @return void
     * @author paul.wei
     * @date 2018/7/19
     */
    public static void createParent(File file){
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            createParent(file.getParentFile());
            file.mkdir();
        }
    }

    /**
     * 获取文件名(不包含路径)
     */
    public static String getFileDir(@NotNull String fullName) {
        try {
            Validate.notEmpty(fullName);
            int last = fullName.lastIndexOf(Platforms.FILE_PATH_SEPARATOR_CHAR);
            return fullName.substring(0, last);
        }catch (Exception e){
            logger.error("FileUtils getFileDir exception by fullName:"+fullName+",seprator:"+Platforms.FILE_PATH_SEPARATOR_CHAR,e);
            throw e;
        }
    }


    public static boolean createParentDir(@NotNull String fullName) throws IOException {
        Validate.notEmpty(fullName);
        String parentDir = getFileDir(fullName);
        Validate.notEmpty(parentDir);
        if(isDirExists(parentDir)){
            return true;
        }
        makesureDirExists(parentDir);
        return true;
    }


    /**
     * @Description:遍历文件
     * @params [path]
     * @return java.util.List<java.io.File>
     * @author paul.wei
     * @date 2018/7/19
     */
    public static List<File> traverse(String path){
        List<File> list = new ArrayList<>();
        if(BeanUtils.isEmpty(path)){
            return list;
        }
        File file = new File(path);
        return traverse(file,list);
    }

    public static List<File> traverse(File path,List<File> list){
        if(path==null){
            return list;
        }
        File[] fs = path.listFiles();
        if(fs==null || fs.length<=0){
            return list;
        }
        for(File f:fs){
            if(f.isDirectory()){
                traverse(f,list);
            }
            if(f.isFile()) {
                list.add(f);
            }
        }
        return list;
    }
}
