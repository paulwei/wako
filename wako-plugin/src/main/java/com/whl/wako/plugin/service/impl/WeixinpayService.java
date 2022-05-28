package com.whl.wako.plugin.service.impl;

import com.whl.wako.plugin.service.PayService;

public class WeixinpayService implements PayService {
    @Override
    public void pay() {
        System.out.println("Weixin Pay");
    }
}
