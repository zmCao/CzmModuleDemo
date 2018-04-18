package com.czm.module.netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class HelloClient {
    public static String host = "192.168.1.226";

    public static int port = 7878;


    public  void run(){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HelloClientInitializer());

            // 连接服务端
            Channel ch = null;
            try {
                ch = b.connect(host, port).sync().channel();
                ch.writeAndFlush("Hello word" + "\r\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            // 控制台输入
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            for (; ; ) {
//                String line = in.readLine();
//                if (line == null) {
//                    continue;
//                }
//                /*
//                 * 向服务端发送在控制台输入的文本 并用"\r\n"结尾
//                 * 之所以用\r\n结尾 是因为我们在handler中添加了 DelimiterBasedFrameDecoder 帧解码。
//                 * 这个解码器是一个根据\n符号位分隔符的解码器。所以每条消息的最后必须加上\n否则无法识别和解码
//                 * */
//
//
//            }

        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();

        }

    }
}
