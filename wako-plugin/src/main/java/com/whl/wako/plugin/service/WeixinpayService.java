package com.whl.wako.plugin.service;

public class WeixinpayService implements PayService {
    @Override
    public void pay() {
        System.out.println("Weixin Pay");
    }
}
