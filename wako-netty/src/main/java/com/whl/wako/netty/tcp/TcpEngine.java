package com.whl.wako.netty.tcp;

public interface TcpEngine {
    boolean start();
    boolean stop();
    TcpType getTcpType();
}
