package com.czm.module.netty.client;

import com.czm.module.netty.decoder.MsgPackDecoder;
import com.czm.module.netty.decoder.MsgpackEncoder;
import com.orhanobut.logger.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;


/**
 * Created by Cloud on 2017/9/19.
 *
 * @Description
 */
public class TimeClient {
    private final String TAG = TimeClient.class.getName();
    private Channel mChannerl;
    private EventLoopGroup workerGroup = null;

    public TimeClient() {

    }

    public void run() {
        String host = "192.168.1.226";
        int port = 8085;
        workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new TimeClientInitializer());
//            b.handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                public void initChannel(SocketChannel ch) throws Exception {
//                    //LengthFieldBasedFrameDecoder用于处理半包消息
//                    //这样后面的MsgpackDecoder接收的永远是整包消息
//                    ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
//                    ch.pipeline().addLast("msgpack decoder", new MsgPackDecoder());
//                    //在ByteBuf之前增加2个字节的消息长度字段
//                    ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
//                    ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
//                    ch.pipeline().addLast(new TimeClientHandler());
//                }
//            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync();
            mChannerl = f.channel();

            // 等待连接关闭
            mChannerl.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public Channel getmChannerl() {
        return mChannerl;
    }

    public void destroy() {
        workerGroup.shutdownGracefully();
    }

    /**
     * 发送消息
     *
     * @param sMsg 发送消息字符串
     */
    public void sendMessage(String sMsg) {
        boolean flag = mChannerl != null;
        if (!flag) {
            Logger.e(TAG, "------尚未连接");
            return;
        }
//        byte[] req = sMsg.getBytes();
//        ByteBuf sMessage = Unpooled.buffer(req.length);
//        sMessage.writeBytes(req);
        mChannerl.writeAndFlush(sMsg);
    }
}
