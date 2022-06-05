package com.whl.wako.netty.json;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;


/**
 * 服务器端的程序仅仅读取客户端数据包并完成解码，服务器端的程序没有写出任何的输出数据包对端（客户端）
 *  */

public class JsonServer {
    private final int serverPort;

    ServerBootstrap b = new ServerBootstrap();

    /**
     * 构造函数
     * @param port
     */
    public JsonServer(int port) {
        this.serverPort = port;
    }

    public void runServer(){
        //创建reactor线程组
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workderLoopGroup = new NioEventLoopGroup(); //使用默认cpu个数*2 的线程个数

        try {
            //设置reacotor线程组
            b.group(bossLoopGroup, workderLoopGroup);
            //设置Nio类型的channel
            b.channel(NioServerSocketChannel.class); //服务器端的serverSocket
            //设置监听端口
            b.localAddress(serverPort); //设置监听本地地址，并且传入端口
            //设置通道的参数
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            //装配子通道流水线
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                //有连接到达时会创建一个channel
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    //pipeline管离子通道channel中的handler
                    //向子channel流水线添加3个handler处理器
//                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(2048, 0, 4, 0, 4));

                    ByteBuf delemiter= Unpooled.buffer();
                    byte[] bytes = new byte[3];
                    delemiter.writeBytes(bytes);
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(5000, true,true, delemiter));

                    ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                    ch.pipeline().addLast(new JsonMsgDecoder());
                }
            });

            //开始绑定server
            //通过sync同步方法阻塞直到绑定陈宫
            ChannelFuture channelFuture = b.bind().sync();
            System.out.println("服务器启动，监听端口： " + channelFuture.channel().localAddress());

            //等待通道关闭的异步任务结束
            //服务器监听通道一致等待通道关闭的异步任务结束
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //优雅关闭eventLoopGroup 线程
            workderLoopGroup.shutdownGracefully();
            bossLoopGroup.shutdownGracefully();
        }
    }


    //服务器端业务处理器
    static class JsonMsgDecoder extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String json = (String) msg;
            System.out.println("收到一个 Json 数据包 =》" + json);
        }
    }

    /**
     * 程序执行入口
     * @param args
     */
    public static void main(String[] args) {
        int port = 8888;
        new JsonServer(port).runServer();
    }

}
