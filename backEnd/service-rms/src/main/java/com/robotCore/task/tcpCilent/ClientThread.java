package com.robotCore.task.tcpCilent;

/**
 * @Author lx
 * @create 2022/8/8 14:55
 */
public class ClientThread {

    private Thread threadClient;

    private TcpClient tcpClient;

    public Thread getThreadClient() {
        return threadClient;
    }

    public void setThreadClient(Thread threadClient) {
        this.threadClient = threadClient;
    }

    public TcpClient getTcpClient() {
        return tcpClient;
    }

    public void setTcpClient(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }
}

