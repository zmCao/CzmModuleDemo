package com.czm.module.netty.server;

import android.util.Log;

import com.czm.module.netty.decoder.MsgPackDecoder;
import com.czm.module.netty.decoder.MsgpackEncoder;
import com.orhanobut.logger.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * Created by Cloud on 2017/9/19.
 *
 * @Description
 */
public class Server {
    private final String TAG = Server.class.getName();
    private int port;
    private Channel mChannerl;
    //第一个线程组用于接收client连接
    private EventLoopGroup bossGroup = null;
    //第二个线程组用于实际的业务处理
    private EventLoopGroup workerGroup = null;
    public Server(int port) {
        this.port = port;
    }

    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            //创建辅助类bootstrap,就是对我们的server进行一系列配置
            ServerBootstrap b = new ServerBootstrap();
            //把两个工作线程加入进来
            b.group(bossGroup, workerGroup)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
//                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    //设置tcp缓冲区
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //指定日志级别
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    //使用childHandler绑定具体的事件处理器
                    .childHandler(new ServerInitializer());
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) {
//                            //LengthFieldBasedFrameDecoder用于处理半包消息
//                            //这样后面的MsgpackDecoder接收的永远是整包消息
//                            ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
//                            ch.pipeline().addLast("msgpack decoder", new MsgPackDecoder());
//                            //在ByteBuf之前增加2个字节的消息长度字段
//                            ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
//                            ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
//
//                            ch.pipeline().addLast(new ServerHandler());
//                        }
//                    });
            b.channel(NioServerSocketChannel.class);
            //绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync();
//            mChannerl = f.channel();
            //等待服务器 socket关闭
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            //Context.springContext.close();
        }
    }

    public Channel getmChannerl() {
        return mChannerl;
    }
    public void destroy()
    {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
    /**
     * 发送消息
     *
     * @param sMsg 发送消息字符串
     */
    public void sendMessage(String sMsg) {
//        ServerHandler.group.find()
        ServerHandler.group.writeAndFlush(sMsg);
//        boolean flag = mChannerl != null;
//        if (!flag) {
//            Logger.e(TAG, "------尚未连接");
//            return;
//        }
////        byte[] req = sMsg.getBytes();
////        ByteBuf sMessage = Unpooled.buffer(req.length);
////        sMessage.writeBytes(req);
//        mChannerl.writeAndFlush(sMsg);
    }
}
