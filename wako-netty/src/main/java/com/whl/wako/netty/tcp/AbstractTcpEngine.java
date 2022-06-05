package com.whl.wako.netty.tcp;

import java.util.Map;

public abstract class AbstractTcpEngine implements TcpEngine{
    private TcpEndPoint tcpEndPoint;
    private Map<String,Object> tcpOptions;
    public void heartbeat(){};
}
