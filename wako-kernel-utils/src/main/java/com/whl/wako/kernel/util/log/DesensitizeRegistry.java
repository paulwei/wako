package com.whl.wako.kernel.util.log;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.whl.wako.kernel.util.BeanUtils;
import com.whl.wako.kernel.util.enums.MaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DesensitizeRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DesensitizeRegistry.class);

    private static final Map<MaskType, Set<String>> map = new ConcurrentHashMap<>(16);
    private DesensitizeRegistry(){
                map.put(MaskType.DEFAULT, Sets.newHashSet(""));
                map.put(MaskType.NAME, Sets.newHashSet("name","receiverName","buyer","payer","signMan","courierName"));
                map.put(MaskType.MOBILE, Sets.newHashSet("mobile","phone","receiverMobile","courier_mobile","mobileEncrypt","buyerMobile","courierMobile"));
                map.put(MaskType.ADDRESS, Sets.newHashSet("address","receiverAddress","buyerAddress"));
                map.put(MaskType.PASSWORD, Sets.newHashSet("password"));
                map.put(MaskType.EMAIL, Sets.newHashSet("email"));
                map.put(MaskType.BANK_CARD, Sets.newHashSet("idNo","cardNo","passport","bankAccount"));
                map.put(MaskType.CAPTCHA, Sets.newHashSet("captcha"));
                map.put(MaskType.REMARK, Sets.newHashSet("remark","transport_detail","transportDetail","track_detail","trackDetail","msgParams"));
                map.put(MaskType.OTP_TOKEN, Sets.newHashSet("lockerCode"));
    }


    public MaskType getByFiledName(String fieldName){
        if(BeanUtils.isEmpty(fieldName)){
            return null;
        }
        for(Map.Entry<MaskType,Set<String>> entry: map.entrySet()){
            if(BeanUtils.isEmpty(entry.getValue()) || !entry.getValue().contains(fieldName.trim())){
                continue;
            }
            return entry.getKey();
        }
        return null;
    }


    public void setting(Map<String,Set<String>> confMap){
        if(BeanUtils.isEmpty(confMap)){
            return;
        }
        for(Map.Entry<String,Set<String>> entry:confMap.entrySet()){
            try {
                if (BeanUtils.isEmpty(entry.getKey())
                        || BeanUtils.isEmpty(entry.getValue())) {
                    continue;
                }
                MaskType maskType = MaskType.valueOf(entry.getKey());
                map.computeIfAbsent(maskType, k -> Sets.newHashSet()).addAll(entry.getValue());
            }catch (Exception e){
                logger.error("DesensitizeRegistry setting exception entry:"+JSON.toJSONString(entry),e);
            }
        }
    }


    private static class DesensitizeRegistryHoler{
        private static DesensitizeRegistry INSTANCE = new DesensitizeRegistry();
    }

    public static DesensitizeRegistry getInstance(){
        return DesensitizeRegistryHoler.INSTANCE;
    }

}
