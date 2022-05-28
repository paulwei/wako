package com.whl.wako.plugin;

import com.whl.wako.plugin.service.ServiceObtain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WakoPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(WakoPluginApplication.class, args);
        ServiceObtain serviceObtain = new ServiceObtain();
        serviceObtain.showAllServices();
    }

}
