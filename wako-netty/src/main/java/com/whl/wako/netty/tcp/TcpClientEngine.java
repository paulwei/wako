package com.whl.wako.netty.tcp;

public class TcpClientEngine extends AbstractTcpEngine{
    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public TcpType getTcpType() {
        return TcpType.CLIENT;
    }
}
