package com.czm.module.netty.client;

import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * Created by Cloud on 2017/9/19.
 *
 * @Description
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private int id;
    public TimeClientHandler(int id) {
        this.id = id;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        System.out.println("HelloWorldClientHandler Active");
        for(int i = 0; i < 10; i++) {
            UserInfo info = new UserInfo();
            info.setAge(i);
            info.setBirthday(new Date());
            info.setName("cloud" + i);
            ctx.write(info);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("HelloWorldClientHandler read Message:" + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
