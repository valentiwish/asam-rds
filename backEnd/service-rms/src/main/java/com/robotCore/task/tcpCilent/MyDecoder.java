package com.robotCore.task.tcpCilent;

import com.robotCore.common.utils.DataConvertUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/4/23
 **/
public class MyDecoder extends MessageToMessageDecoder<ByteBuf> {
    public MyDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) {
        int length = byteBuf.readableBytes();
        byte[] bytes = ByteBufUtil.getBytes(byteBuf,0,length);
        out.add(DataConvertUtil.BytesToHexString(bytes));
    }

}
