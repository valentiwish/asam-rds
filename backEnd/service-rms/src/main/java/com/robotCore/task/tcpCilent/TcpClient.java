package com.robotCore.task.tcpCilent;

import com.beans.redis.RedisUtil;
import com.beans.tools.BeansAppContext;
import com.utils.tools.HexOrBytesUtil;
import com.utils.tools.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import socket.handler.client.CommConst;

import java.net.InetSocketAddress;

@Slf4j
//@Data
public class TcpClient {

    private volatile  static TcpClient tcpClient;
    public TcpClient getInstance() {
        if (null == tcpClient) {
            synchronized (TcpClient.class) {
                if (null == tcpClient) {
                    tcpClient = new TcpClient();
                }
            }
        }
        return tcpClient;
    }

    /**
     *
     * @param varAddress 服务器地址
     * @param varPort 服务器端口地址
     * @throws InterruptedException
     */
    public boolean nettyClient(String varAddress, int varPort) throws InterruptedException {
        boolean isResult=false;
        try {
//            log.info("---------启动客户端----------");
            initClient(varAddress, varPort);
            if (connectSync()) {
//				sycleSendMsg();
//              sendMsg(this.sendStr);
                WaiteCloseFuture();
            }
        } catch (Exception e) {
            log.error("nettyClient:{}", e.getMessage());
        } finally {
            group.shutdownGracefully();
//            log.info("释放客户端资源client:{}", port);
        }
        return isResult;
    }

    /**
     * 消息循环发送
     *
     * @throws InterruptedException
     */
    public void sycleSendMsg() {
        while (true) {
            sendMsg(this.sendStr);
        }
    }

    /**
     * 信息发送
     *
     * @param msgStr
     * @return
     * @throws InterruptedException
     */
    public boolean sendMsg(String msgStr) {
        boolean isReuslt = false;
        if (null != varClient) {
            if (StringUtils.isNotEmpty(msgStr)) {
                if (varClient.isActive() && varClient.isWritable()) {
                    if (!isFirstSend) {
                        varClient.writeAndFlush(CommConst.ChannelCheck);// 输入数据并将数据推栈
                        isFirstSend = true;
                    }
                    System.out.print("ClientS：" + msgStr);
                    varClient.writeAndFlush(msgStr);// 输入数据并将数据推栈
                    isReuslt = true;
                }
            }
        }
        return isReuslt;
    }

    /**
     * 信息发送
     *
     * @param msgStr
     * @return
     * @throws InterruptedException
     */
    public boolean sendHexMsg(String msgStr) {
        boolean isReuslt = false;
        if (null != varClient) {
            if (StringUtils.isNotEmpty(msgStr)) {
                if (varClient.isActive() && varClient.isWritable()) {
                    if (!isFirstSend) {
                        varClient.writeAndFlush(CommConst.ChannelCheck);// 输入数据并将数据推栈
                        isFirstSend = true;
                    }
//                    log.info("客户端tcp发送Hex数据：{}", msgStr);
                    byte[] varBytes = HexOrBytesUtil.hexStringToByteArray(msgStr);
                    ByteBuf varByteBuf = HexOrBytesUtil.bytesToByteBuf(varBytes);
                    varClient.writeAndFlush(varByteBuf);// 输入数据并将数据推栈
                    isReuslt = true;
                }
            }
        }
        return isReuslt;
    }
    // ---------------------------------------------------------private

    /**
     * 客户端通讯，通道
     */
    private Channel varClient = null;
    private boolean isFirstSend = false;
//------------------------------------------------
    /**
     * 是一个启动NIO服务的辅助启动类，设置客户端引导类
     */
    private Bootstrap bootstrap;
    /**
     * 创建发送数据线程池，用来处理IO操作的多线程事件循环器
     */
    private EventLoopGroup group;
    // ---------------------------------
    private ChannelFutureListener channelFutureListener;
    /**
     *
     */
    private ChannelFuture future;
    // ------------------------------------
    private String sendStr;
    private String address;
    public int port;

//------------------------------------------------------------
    /**
     * 客户单初始化
     */
    private void initClient(String varAddress, int port) {
        group = new NioEventLoopGroup();// 创建发送数据线程池，用来处理IO操作的多线程事件循环器
        bootstrap = new Bootstrap();// 是一个启动NIO服务的辅助启动类，设置客户端引导类
        bootstrap.group(group)// 创建连接通道，加入线程池
                .channel(NioSocketChannel.class)// 设置selector模型
                .option(ChannelOption.TCP_NODELAY, true)
                // 添加处理连接请求handler
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        // 并且，实体类要实现Serializable接口，否则也无法传输
//                         p.addLast(new StringDecoder());// 字符串解码器
//                         p.addLast(new StringEncoder());// 字符串编码器

                        //针对分包的问题，进行配置
                        p.addLast(new LengthFieldBasedFrameDecoder(1000000000,4,4,8,8));

//                        //发送消息格式
                        p.addLast("encoder", new MyEncoder());
////                        //接收消息格式
//                        p.addLast("decoder", new MyDecoder());
                        p.addLast(new TcpClientHandler(TcpClient.this));
                    }
                });
//        this.sendStr = varSendStr;
        this.address = varAddress;
        this.port = port;
    }

    /**
     *
     * @return
     */
    public boolean connectSync() {
        try {
            if (null != this.channelFutureListener) {
                if (null != this.future) {
                    this.future.removeListener(this.channelFutureListener);
                }
            }
            this.future = this.bootstrap.connect(new InetSocketAddress(this.address, this.port)).sync();// 设置请求连接配置
            this.channelFutureListener = getChannelFutureListener();
            if (null != this.channelFutureListener) {
                this.future.addListener(this.channelFutureListener);
                isFirstSend = false;
                this.varClient = future.channel();
                return true;
            }
        } catch (InterruptedException e) {
            group.shutdownGracefully();
            log.info("重新启动客户端client:{}", port);
        }
        return false;
    }

    /**
     * 通道检测添加
     *
     * @return
     */
    private ChannelFutureListener getChannelFutureListener() {
        try {
            return new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    try {
                        if (future.isSuccess()) {
//                            log.info("客户端，建立连接:{},完毕", port);
                        } else {
//                            log.info("客户端，建立连接:{},失败：{}", port, future.cause().getMessage());
                        }
                    } catch (Exception e) {
                        log.error("operationComplete:{}", e.getMessage());
                    }
                }
            };
        } catch (Exception e) {
            log.error("getChannelFutureListener:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 阻塞链接
     *
     */
    private void WaiteCloseFuture() {
        try {
            if (null != this.future) {
                this.future.channel().closeFuture().sync();// 获取Channel的CloseFuture,并且阻塞当前线程直到它完成
            }
        } catch (InterruptedException e) {
//            log.error("closeFuture:{}", e.getMessage());
        } finally {
            this.closeGroup();
        }
    }

    /**
     * 释放资源
     */
    public void closeGroup() {
        group.shutdownGracefully();
    }

}