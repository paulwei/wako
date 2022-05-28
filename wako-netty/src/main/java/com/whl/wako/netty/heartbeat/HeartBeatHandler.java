package com.whl.wako.netty.heartbeat;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Date;

public class HeartBeatHandler extends ChannelDuplexHandler {
    /** 空闲次数 */
    private volatile int idle_count = 1;
    /** 发送次数 */
    private volatile int count = 1;

    /**
     * 超时处理，如果5秒没有收到客户端的心跳，就触发; 如果超过两次，则直接关闭;
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.ALL_IDLE.equals(event.state())) { // 如果读通道处于空闲状态，说明没有接收到心跳命令
                if (idle_count > 20) {
                    System.out.println("超过两次无客户端请求，关闭该channel");
                    ctx.channel().close();
                }else{
                    System.out.println("已等待"+idle_count*5+"秒还没收到客户端发来的消息");
                    idle_count++;
                    ctx.write("PING");
                    ctx.flush();
                }

            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接时：" + new Date());
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭连接时：" + new Date());
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第" + count + "次" + "，服务端收到的消息:" + msg +"  "+ new Date());

        String message = (String) msg;
        // 如果是心跳命令，服务端收到命令后回复一个相同的命令给客户端
        if ("PONG".equals(message)) {
            ctx.write("服务端成功收到心跳ACK信息"+new Date());
            ctx.flush();
        }

        count++;
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
