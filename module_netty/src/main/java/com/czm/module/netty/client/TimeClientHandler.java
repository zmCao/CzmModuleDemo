package com.czm.module.netty.client;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by czm on 2017/9/19.
 *
 * @Description
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    public TimeClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Logger.e("ClientHandler Active");
        EventBus.getDefault().post("与服务端连接成功");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        Logger.e("ClientHandler read Message:" + msg);
        EventBus.getDefault().post(msg.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
