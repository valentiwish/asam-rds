package com.robotCore.task.tcpCilent;

import com.beans.tools.PropertiesUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 负责监听启动时连接失败，重新连接功能
 * @author yinjihuan
 *
 */
@Slf4j
public class ConnectionListener implements ChannelFutureListener {
    private TcpClient client;
    public ConnectionListener(TcpClient client) {
        this.client = client;
    }
    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    System.err.println("服务端链接不上，开始重连操作...");
                    String ip = PropertiesUtil.getString("socket.clientIp1");
                    int port = PropertiesUtil.getIntByKey("socket.clientport1");
                    try {
                        client.nettyClient(ip,port);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            }, 1L, TimeUnit.SECONDS);
        } else {
            System.err.println("服务端链接成功...");
        }
    }
}
