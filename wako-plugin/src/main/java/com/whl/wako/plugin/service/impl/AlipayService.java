package com.whl.wako.plugin.service.impl;

import com.whl.wako.plugin.service.PayService;

public class AlipayService implements PayService {
    @Override
    public void pay() {
        System.out.println("Alipay");
    }
}
