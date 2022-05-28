package com.whl.wako.netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

public class HeartBeatClient {
    public static void main(String[] args) {
        int port = 8082;

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            // 首先，netty通过Bootstrap启动客户端
            Bootstrap bootstrap = new Bootstrap();
            // 第1步 定义线程组，处理读写和链接事件，没有了accept事件
            bootstrap.group(eventLoopGroup)
                    // 第2步 绑定客户端通道
                    .channel(NioSocketChannel.class)
                    // 第3步 给NIoSocketChannel初始化handler， 处理读写事件
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
                            pipeline.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
//                            pipeline.addLast(new IdleStateHandler(0, 0, 3, TimeUnit.SECONDS));
                            pipeline.addLast(new HeartBeatClientHandler());
                        }

                    });

            // 连接服务端
            Channel channel = bootstrap.connect("localhost", port).sync().channel();
            Thread.sleep(1000*20);
            String text = "Hello HeartBeatServer";
            for(int i=0;i<5;i++){
                if(channel.isActive()){
                    channel.writeAndFlush(text + i);
                    Thread.sleep(1000*3);
                }
            }
            channel.closeFuture().sync();
        } catch (Exception e) {
            // do something
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
