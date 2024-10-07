package com.whl.wako.kernel.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.IllegalReferenceCountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import static com.whl.wako.kernel.util.DateUtils.DATE_FORMAT_SEQ;

public class ByteUtils {
   private final static Logger logger = LoggerFactory.getLogger(ByteUtils.class);

    public static boolean release(ByteBuf buf) {
        if (buf != null && buf.refCnt() > 0) {
            try {
                do {
                    buf.release();
                }while (buf.refCnt() > 0);
                return true;
            } catch (IllegalReferenceCountException e) { // in case buf is being released at the same time by netty
                logger.warn("Release release failed", e);
                return false;
            }
        }
        return true;
    }
    public static String hex2Str(ByteBuf buf) {
       String hex = ByteBufUtil.hexDump(buf);
       release(buf);
       return hex;
    }

    public static String hex2StrNoCycle(ByteBuf buf) {
        String hex = ByteBufUtil.hexDump(buf);
        release(buf);
        return hex;
    }


    public static byte[] str2HexByte(String str) {
        if(BeanUtils.isEmpty(str)){
            return null;
        }
        return parseHexStr2Byte(str);
    }

    public static byte[] str2HexByte(String str,int size) {
        if(BeanUtils.isEmpty(str)){
            return new byte[size];
        }
        return parseHexStr2Byte(str,size);
    }

    public static byte[] str2Byte(String str,int size) {
        byte[] dest = new byte[size];
        if(BeanUtils.isEmpty(str)){
            return dest;
        }
        byte[] src = str.getBytes();
        System.arraycopy(src,0,dest,0,(src.length>size?size:src.length));
        return dest;
    }

    public static int hex2Int(ByteBuf buf) {
        AssertUtils.notNull(buf,"hex2Int buf must not null");
        String hex = ByteBufUtil.hexDump(buf);
        release(buf);
        return Integer.parseInt(hex,16);
    }

    public static int hex2Int(byte[] bytes) {
        AssertUtils.notNull(bytes,"hex2Int bytes must not null");
        String hex = ByteBufUtil.hexDump(bytes);
        return Integer.parseInt(hex,16);
    }

    public static byte hex2Byte(ByteBuf buf) {
       int size = buf.readableBytes();
       byte b=0;
       for(int i=0;i<size;i++){
           b = buf.readByte();
       }
       release(buf);
       return b;
    }

    public static boolean equals(byte[] bytes,int i,int j,int k){
        boolean result = true;
        if(bytes==null){
            return false;
        }
        while(k-->0){
            if(i>=bytes.length || j>=bytes.length){
                throw new IllegalArgumentException(String.format("ByteUtils equals illegal i:%s,j:%s,k:%s,len:%s",i,j,k,bytes.length));
            }
            result = result && (bytes[i++]==bytes[j++]);
        }
        return result;
    }


    public static byte[] int2HexByte(Integer n) {
        if(BeanUtils.isEmpty(n)){
            return null;
        }
        return int2HexByte(n,4);
    }
    public static byte[] int2HexByte(Integer n,int size) {
        byte[] dest = new byte[size];
        if(BeanUtils.isEmpty(n)){
            return dest;
        }
        for(int i=size-1;i>=0;i--){
            if(n<=0){
                break;
            }
            dest[i] = (byte) (n & 0xff);
            n=n>>8;
        }
        return dest;
    }

    public static Long hex2Long(ByteBuf buf) {
        String hex = ByteBufUtil.hexDump(buf);
        release(buf);
        return Long.parseLong(hex,16);
    }

    public static byte[] byte2Byte(Byte b,int size) {
        byte[] arr = new byte[size];
        if(BeanUtils.isEmpty(b) || size<=0){
            return arr;
        }
        arr[size-1]=b;
        return arr;
    }

    public static byte[] long2HexByte(Long n) {
        if(BeanUtils.isEmpty(n)){
            return null;
        }
        return long2HexByte(n,8);
    }

    public static byte[] long2HexByte(Long n,int size) {
        byte[] dest = new byte[size];
        if(BeanUtils.isEmpty(n)){
            return dest;
        }
        for(int i=size-1;i>=0;i--){
            if(n<=0){
                break;
            }
            dest[i] = (byte) (n & 0xff);
            n=n>>8;
        }
        return dest;
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr==null || hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static byte[] date2HexByte(Date date) {
        if(BeanUtils.isEmpty(date)){
            return new byte[8];
        }
        String strDate = DateUtils.formatDate(date,DATE_FORMAT_SEQ);
        return new byte[]{
           parseHexStr2Byte(strDate.substring(2,4),1)[0],
           parseHexStr2Byte(strDate.substring(4,6),1)[0],
           parseHexStr2Byte(strDate.substring(6,8),1)[0],
           parseHexStr2Byte(strDate.substring(8,10),1)[0],
           parseHexStr2Byte(strDate.substring(10,12),1)[0],
           parseHexStr2Byte(strDate.substring(12,14),1)[0],
           parseHexStr2Byte(strDate.substring(14,16),1)[0],
           parseHexStr2Byte(strDate.substring(16,17),1)[0],
        };
    }

    public static byte[] parseHexStr2Byte(String hexStr,int size) {
        if (BeanUtils.isEmpty(hexStr) || hexStr.length() < 1) {
            return new byte[size];
        }
        byte[] result = new byte[size];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static String parseByte2HexStr(byte buf) {
        String hex = Integer.toHexString(buf & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    public static String convertStringToHex(String str){
        if(str==null){
            return StringUtil.EMPTY;
        }
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }
        return hex.toString();
    }

    public static String convertHexToString(String hex){
        if(hex==null){
            return StringUtil.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static byte[] inputStreamToByteArray(InputStream in)  {
        try {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024*4];
            int n=0;
            while ( (n=in.read(buffer)) >0) {
                out.write(buffer,0,n);
            }
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("inputStreamToByteArray exception",e);
            return null;
        }
    }

    public static void main(String[] args) {

        date2HexByte(new Date());
        String hex = Integer.toHexString(9527);
        System.out.println(hex);

        byte[] bytes =  parseHexStr2Byte(hex,2);
        String ss = ByteBufUtil.hexDump(bytes);
        System.out.println(ss);
        Integer xx = Integer.parseInt(ss,10);
        System.out.println(xx);

//        Integer.parseInt(ByteUtils.parseByte2HexStr(value), 16)//16进制转10进制
//        byte[] bytes = {1, 8, 1, 8};
//        boolean eq = equals(bytes,0,2,3);
//        System.out.println(eq);
//        System.out.println(eq);
    }
}
