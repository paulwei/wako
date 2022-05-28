package com.whl.wako.plugin.service;

import java.util.ServiceLoader;

public class ServiceObtain {
    public void showAllServices(){
        ServiceLoader<PayService> serviceLoader = ServiceLoader.load(PayService.class);
        for(PayService ele : serviceLoader){
            ele.pay();
        }
    }

    public static void main(String[] args) {
        ServiceObtain serviceObtain = new ServiceObtain();
        serviceObtain.showAllServices();
    }
}
