package com.czm.module.netty.decoder;

import com.czm.module.netty.client.UserInfo;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by Cloud on 2018/3/5 0005.
 */
public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
        final byte[] array;
        final int length=arg1.readableBytes();
        array=new byte[length];
        arg1.getBytes(arg1.readerIndex(), array,0,length);
        MessagePack msgpack=new MessagePack();
        arg2.add(msgpack.read(array, UserInfo.class));
    }

}
