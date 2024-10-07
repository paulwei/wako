package com.whl.wako.kernel.util.log;

/**
 * @author paul.wei
 * @Description: 脱敏上下文(全局)
 * @date 2020/04/23
 */
public class DesensitizeContext {
    private DesensitizeRegistry registry = DesensitizeRegistry.getInstance();//全局配置

    public DesensitizeRegistry getRegistry() {
        return registry;
    }

    private DesensitizeContext(){}

    private static class DesensitizeContextHoler{
        private static DesensitizeContext INSTANCE = new DesensitizeContext();
    }

    public static DesensitizeContext getInstance(){
        return DesensitizeContextHoler.INSTANCE;
    }

}
