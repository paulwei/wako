package com.whl.wako.kernel.util;

import com.alibaba.fastjson.JSON;
import com.whl.wako.kernel.util.exception.ClockBackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 序列号生成器
 * paul.wei
 * 正数位（占1比特）+时间戳（占31比特）+自增值（占22比特）+机械id（占10比特）,避免浪费ID空间
 */
public class SeqGenerator implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SeqGenerator.class);

    public static final ConcurrentMap<Long, SeqGenerator> seqGeneratorMap = new ConcurrentHashMap<>();

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1574006400000L;//2019-11-18

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long SEQUENCE_LEFT = MACHINE_BIT;//序列号第12（右至左）
    private final static long TIMESTMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;//时间第12+10（右至左）

    private final long machineId;     //机器标识
    private volatile long sequence = 0L; //序列号
    private volatile long lastStmp = -1L;//上一次时间戳

    private SeqGenerator(Long machineId) {
        AssertUtils.isTrue(machineId>0 && machineId<MAX_MACHINE_NUM,String.format("machineId is not in range machineId:%s,max:%s",machineId,MAX_MACHINE_NUM));
        this.machineId = machineId;
        logger.info("SeqGenerator machineId:{},binary:{}",machineId,Long.toBinaryString(machineId));
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(String.format("machineId =%s can't be greater than MAX_MACHINE_NUM or less than 0", machineId));
        }
    }

    public static SeqGenerator getInstance(Long machineId) {
        SeqGenerator seqGenerator = seqGeneratorMap.computeIfAbsent(machineId, aLong -> new SeqGenerator(machineId));
        logger.info((String.format(" getInstance seqGenerator:%s by currency machineId:%s already machineId:%s",seqGenerator,machineId, JSON.toJSONString(seqGeneratorMap.keySet()))));
        return seqGenerator;
    }


    public synchronized long nextId(Integer prefix){
         AssertUtils.notNull(prefix,"SeqGenerator nextId prefix is not number type prefix="+prefix);
         long nextId = nextId();
         long id = Long.parseLong(prefix.toString()+nextId);
         if(id>0){
            return id;
         }
         logger.warn("SeqGenerator nextId id:{} overflow with prefix:{} nextId:{}",id,prefix,nextId);
         return nextId;
    }
    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {//防止时钟回拨
            long back = lastStmp-currStmp;
            String msg = String.format("Clock moved backwards machineId:%s,currStmp:%s,lastStmp:%s,back:%s (ms)",machineId,currStmp,lastStmp,back);
            logger.warn(msg);
            throw new ClockBackException("230",msg);
        }
        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {//如果序列也用完等至后一毫秒
                currStmp = getNextMill();
            }
        } else {
            sequence = 0;
        }
        lastStmp = currStmp;
        return (currStmp - START_STMP) << TIMESTMP_LEFT   //时间戳部分，毫秒值整体左移22位
                | sequence  << SEQUENCE_LEFT              //序列号部分，最右不用左移
                | machineId  ;                            //机器标识部分，机器码左移10位
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    protected long getMachineId() {
        long machineId=0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            if(ip==null){
                logger.warn("InetAddress.getLocalHost ip is empty ");
                machineId = BeanUtils.random(0, 1000);
                return machineId;
            }
            if(BeanUtils.isNotEmpty(ip.getHostAddress())){
                String[] str = ip.getHostAddress().split("\\.");
                if(str!=null && str.length>0){
                    machineId = NumberUtils.trimToLong(str[str.length-1])+NumberUtils.trimToLong(str[str.length-2]);
                }
                logger.info("InetAddress.getLocalHost ip:{}",ip.getHostAddress());
                if(machineId>0){
                    return machineId;
                }
            }
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (null != network) {
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        String[] str = ip.getHostAddress().split("\\.");
                        if(str!=null && str.length>0){
                            machineId = NumberUtils.trimToLong(str[str.length-1])+NumberUtils.trimToLong(str[str.length-2]);
                        }
                        logger.info("InetAddress.getInetAddresses enum ip:{}",ip.getHostAddress());
                        if(machineId>0){
                            return machineId;
                        }
                    }
                }
            }
            logger.info(String.format(" SeqGenerator getMachineId  MachineId= %s ", machineId));
        } catch (Throwable e) {
            logger.error(" SeqGenerator getMachineId  exception ", e);//如果异常随机数代替
        }
        machineId = BeanUtils.random(0, 1000);
        return machineId;
    }

    public static long getStartStmp() {
        return START_STMP;
    }

    public static long getSequenceBit() {
        return SEQUENCE_BIT;
    }

    public static long getMachineBit() {
        return MACHINE_BIT;
    }

    public static long getMaxMachineNum() {
        return MAX_MACHINE_NUM;
    }

    public static long getMaxSequence() {
        return MAX_SEQUENCE;
    }

    public static long getSequenceLeft() {
        return SEQUENCE_LEFT;
    }

    public static long getTimestmpLeft() {
        return TIMESTMP_LEFT;
    }
}
