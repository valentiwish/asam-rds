package com.robotCore.task.tcpCilent;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author lx
 * @create 2022/8/8 16:58
 */
public interface IClientRead1 {
    public void parseData(ChannelHandlerContext ctx, String msg);

}
