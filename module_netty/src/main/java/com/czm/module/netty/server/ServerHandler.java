package com.czm.module.netty.server;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Description:
 * Date: 2018-04-04
 * Time: 10:13
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        group.add(ctx.channel());
        ctx.writeAndFlush("收到了你的连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        group.remove(ctx.channel());
        ctx.close();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Logger.d("ServerHandler read Message:" + msg);
        EventBus.getDefault().post(msg.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        group.remove(ctx.channel());
        ctx.close();

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }
}


