package com.whl.wako.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class HeartBeatServer {
    private int port = 8082;

    private void run() {
        // 首先，netty通过ServerBootstrap启动服务端
        ServerBootstrap bootstrap = new ServerBootstrap();
        //第1步定义两个线程组，用来处理客户端通道的accept和读写事件
        //parentGroup用来处理accept事件，childgroup用来处理通道的读写事件
        //parentGroup获取客户端连接，连接接收到之后再将连接转发给childgroup去处理
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            bootstrap.group(boss, worker)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    //用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。
                    //用来初始化服务端可连接队列
                    //服务端处理客户端连接请求是按顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //第2步绑定服务端通道
                    .channel(NioServerSocketChannel.class)
                    //第3步绑定handler，处理读写事件，ChannelInitializer是给通道初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
                            pipeline.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
                            pipeline.addLast(new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS));
                            pipeline.addLast(new HeartBeatHandler());

                        }

                    });
            //第4步绑定8082端口
            ChannelFuture future = bootstrap.bind(port).sync();
            //当通道关闭了，就继续往下走
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new HeartBeatServer().run();
    }
}
