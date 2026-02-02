package com.robotCore.task.tcpCilent;

import com.beans.tools.BeansAppContext;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.scheduing.controller.RobotTaskController;
import com.robotCore.scheduing.service.impl.RobotWaybillServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @SuppressWarnings("unused")
    private TcpClient tcpClient;

    public TcpClientHandler(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    /**
     * 断开连接时触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		System.err.println("运行中断开重连 ...");
//		this.tcpClient.connectSync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

//		IClientRead varIClientRead = BeansAppContext.getBean(IClientRead.class);
        IClientRead1 varIClientRead1 = BeansAppContext.getBean(IClientRead1Impl.class);
//		IClientRead1 varIClientRead2 = BeansAppContext.getBean(RobotMonitorServiceImpl.class);
        IClientRead1 varIClientRead3 = BeansAppContext.getBean(RobotWaybillServiceImpl.class);
        // 测试：获取机器人任务状态数据
//        IClientRead1 varIClientReadTest = BeansAppContext.getBean(RobotTaskController.class);
        if (null != varIClientRead1) {
            String hexString = byteBufToHexString(msg);
            varIClientRead1.parseData(ctx, hexString);
        }
//        else {
//            log.error("channelRead0:{}", "客户端，信息读取服务端信息失败");
//        }
//		if(null!=varIClientRead2){
//			varIClientRead2.parseData(ctx, msg);
//		}else {
//			log.error("channelRead0:{}","客户端，信息读取服务端信息失败");
//		}

        if (null != varIClientRead3) {
            String hexString = byteBufToHexString(msg);
            varIClientRead3.parseData(ctx, hexString);
        } else {
            log.error("channelRead0:{}", "客户端，信息读取服务端信息失败");
        }

        // 调用机器人任务状态数据解析方法
//        if (null != varIClientReadTest) {
//            // 机器人任务状态数据前两个字节对应16进制的2B和66，则认为是机器人任务状态数据
//            varIClientReadTest.parseData(ctx, msg);
//        } else {
//            log.error("channelRead0:{}", "客户端，读取服务端<机器人任务状态>信息失败");
//        }
    }

    // 读取完缓存区收到消息
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    /*
     * 异常信息打印
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        // 处理异常消息
        log.error("exceptionCaught:{}", cause.getMessage());
        ctx.close();
        removeResources(ctx);
    }

    // 连接到服务端处理逻辑
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("TCP连接已建立");
    }


    /**
     * 释放端口
     *
     * @param ctx
     */
    private void removeResources(ChannelHandlerContext ctx) {
        this.tcpClient.closeGroup();
        TcpClientThread.interruptThread(this.tcpClient.port);
    }

    // 转换为UTF-8字符串
    public static String byteBufToString(ByteBuf byteBuf) {
        if (byteBuf == null || byteBuf.readableBytes() == 0) {
            return "";
        }
        // Netty会自动处理readerIndex，此处不会修改原始ByteBuf状态
        return byteBuf.toString(StandardCharsets.UTF_8);
    }

    // 转换为全大写HEX字符串
    public static String byteBufToHexString(ByteBuf byteBuf) {
        if (byteBuf == null || byteBuf.readableBytes() == 0) {
            return "";
        }

        // 直接通过数组拷贝提高效率
        byte[] bytes = new byte[byteBuf.readableBytes()];
        int readerIndex = byteBuf.readerIndex();
        byteBuf.getBytes(readerIndex, bytes);  // 不会改变原始readerIndex

        return bytesToHex(bytes);
    }

    // 辅助方法：字节数组转HEX
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hexString.append(CHARACTERS[(b & 0xF0) >> 4])
                    .append(CHARACTERS[b & 0x0F]);
        }
        return hexString.toString();
    }

    // 预定义十六进制字符表（大写）
    private static final char[] CHARACTERS = "0123456789ABCDEF".toCharArray();
}