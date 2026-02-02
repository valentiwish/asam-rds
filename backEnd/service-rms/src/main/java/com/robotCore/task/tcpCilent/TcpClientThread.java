package com.robotCore.task.tcpCilent;

import com.beans.redis.RedisUtil;
import com.beans.tools.BeansAppContext;
import com.entity.EntityResult;
import com.entity.R;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.common.utils.ObjectUtil;
import com.utils.tools.IPAddress;
import lombok.extern.slf4j.Slf4j;

import java.lang.Thread.State;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class TcpClientThread {
	//	private static final Map<Integer, Thread> threadNetty = new ConcurrentHashMap<>();
	private static final Map<Integer, ClientThread> threadNetty = new ConcurrentHashMap<>();
	private static final Map<String, ClientThread> ipThreadNetty = new ConcurrentHashMap<>();
	private static RedisUtil redisUtil= BeansAppContext.getBean(RedisUtil.class);
	/**
	 *
	 * @Description: 1.启动之前关闭该端口绑定的线程；2.启动端口；
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public static EntityResult start(String ipAddress, int port) {
//		interruptThread(port);
		interruptThread(ipAddress,port);
		return multiRemoteClient(ipAddress, port);
	}

	/**
	 *
	 * @Description: 1.启动之前关闭该端口绑定的线程；2.启动端口；
	 * @param port
	 * @return
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public static EntityResult start(int port) {
		interruptThread(port);
		return multiLocalClient(port);
	}

	/**
	 * 客户端信息发送
	 *
	 * @param port
	 * @param msg
	 * @return
	 */
	public static EntityResult sendMsg(int port, String msg) {
		EntityResult varEntityResult = new EntityResult();
		if (threadNetty.containsKey(port)) {
			ClientThread clientThread = threadNetty.get(port);
			if (clientThread.getTcpClient().sendHexMsg(msg)) {
				varEntityResult.setMsg("客户端信息发送成功");
			}
		} else {
			varEntityResult.setMsg("客户端信息发送失败");
			varEntityResult.setSuccess(false);
		}
		return varEntityResult;
	}
	/**
	 * 客户端信息发送
	 *
	 * @param port
	 * @param msg
	 * @return
	 */
	public static EntityResult sendHexMsg(String ip, int port, String msg) {
		EntityResult varEntityResult = new EntityResult();
		if (ipThreadNetty.containsKey(ip + port)) {
			ClientThread clientThread = ipThreadNetty.get(ip + port);
			if (clientThread.getTcpClient().sendHexMsg(msg)) {
				try {
					Thread.sleep(500L);
					varEntityResult.setMsg("客户端信息发送成功");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				varEntityResult.setMsg("客户端信息发送失败");
				varEntityResult.setSuccess(false);
			}
		} else {
			varEntityResult.setMsg("客户端信息发送失败");
			varEntityResult.setSuccess(false);
		}
		return varEntityResult;
	}
	/**
	 * 断开线程
	 * @param port
	 */
	public static EntityResult interruptThread(int port) {
		EntityResult varResult = new EntityResult();
		try {
			if (threadNetty.containsKey(port)) {
				ClientThread clientThread = threadNetty.get(port);
				try {
					clientThread.getThreadClient().interrupt();
					clientThread.getTcpClient().closeGroup();
					varResult.setData(clientThread.getThreadClient().toString());
				} catch (Exception e) {
					log.error(e.getMessage());
					varResult.setSuccess(false);
					varResult.setMsg(String.format("{},线程中断失败", port));
				}
				threadNetty.remove(port);
//				log.info("========线程关闭客户端端口：{}========", port);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return varResult;
	}
	/**
	 * 断开线程
	 * @param port
	 */
	public static EntityResult interruptThread(String ip,int port) {
		EntityResult varResult = new EntityResult();
		try {
			if (ipThreadNetty.containsKey(ip+port)) {
				ClientThread clientThread = ipThreadNetty.get(ip+port);
				try {
					clientThread.getThreadClient().interrupt();
					clientThread.getTcpClient().closeGroup();
					varResult.setData(clientThread.getThreadClient().toString());
				} catch (Exception e) {
					log.error(e.getMessage());
					varResult.setSuccess(false);
					varResult.setMsg(String.format("{},线程中断失败", ip+port));
				}
				ipThreadNetty.remove(ip+port);
//				log.info("========线程关闭客户端端口：{}========", port);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return varResult;
	}
	public static EntityResult threadStatus(int port) {
		EntityResult varResult = new EntityResult();
		try {
			if (threadNetty.containsKey(port)) {
				ClientThread clientThread = threadNetty.get(port);
				State varState = clientThread.getThreadClient().getState();
				varResult.setData(varState);
			} else {
				varResult.setData(State.NEW);
				varResult.setSuccess(false);
			}
			log.info(varResult.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return varResult;
	}

	// -----------------------------------------------------------private
	/**
	 * 获取接口列表
	 *
	 * @return
	 */
	private static Object[] getPortList() {
		return threadNetty.keySet().toArray();
	}

	/**
	 * 保存当前启动的线程，便于关闭，释放资源
	 *
	 * @param port     端口
	 * @param channel 线程
	 * @throws InterruptedException
	 */
	private static EntityResult addThread(Integer port, Thread channel, TcpClient tcpClient) {
		EntityResult varResult = new EntityResult();
		try {
			if (threadNetty.containsKey(port)) {
				ClientThread clientThread = threadNetty.get(port);
				clientThread.getThreadClient().interrupt();
				Thread.sleep(100L);
			}

		} catch (InterruptedException e) {
			varResult.setSuccess(false);
			varResult.setMsg(String.format("{},线程中断失败", port));
			log.error(e.getMessage());
		}
		ClientThread varTest = new ClientThread();
		varTest.setThreadClient(channel);
		varTest.setTcpClient(tcpClient);
		threadNetty.put(port, varTest);
		return varResult;
	}


	private static EntityResult addThreadTest(String ip,Integer port, Thread channel, TcpClient tcpClient) {
		EntityResult varResult = new EntityResult();
		try {
			if (ipThreadNetty.containsKey(ip+port)) {
				ClientThread clientThread = ipThreadNetty.get(ip+port);
				clientThread.getThreadClient().interrupt();
				Thread.sleep(100L);
			}

		} catch (InterruptedException e) {
			varResult.setSuccess(false);
			varResult.setMsg(String.format("{},线程中断失败", port));
			log.error(e.getMessage());
		}
		ClientThread varTest = new ClientThread();
		varTest.setThreadClient(channel);
		varTest.setTcpClient(tcpClient);
		ipThreadNetty.put(ip+port, varTest);
		return varResult;
	}

	/**
	 * 线程启动多客户端
	 *
	 * @param port
	 */
	private static EntityResult multiRemoteClient(String ipAdress, int port) {
		EntityResult varResult = new EntityResult();
		try {
			if (IPAddress.isIPAddress(ipAdress)) {
				for (int varInt = 0; varInt < 1; varInt++) {
					InetAddress geek = InetAddress.getByName(ipAdress);
					if(geek.isReachable(1000)){
						String varStr = String.valueOf(varInt + 1);
						TcpClient varIOTClient = new TcpClient();
						Thread nettyClient = new Thread(() -> {
							try {
								if (!varIOTClient.nettyClient(ipAdress, port)) {

//									log.info("----ip:{},port:{}----启动失败", ipAdress, port);
									interruptThread(ipAdress,port);
								}
							} catch (InterruptedException e) {
								log.error(e.getMessage());
							}
						});
						addThreadTest(ipAdress,port, nettyClient, varIOTClient);
						nettyClient.setName(String.format("thread:%s:port:%s", varStr, port));
						nettyClient.start();
					}
				}
			} else {
				varResult.setSuccess(false);
				varResult.setMsg("ip地址不合法");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			varResult.setSuccess(false);
		}
		return varResult;
	}

	private static EntityResult multiLocalClient(int port) {
		EntityResult varResult = new EntityResult();
		try {
			for (int varInt = 0; varInt < 1; varInt++) {
				String varStr = String.valueOf(varInt + 1);
				TcpClient varIOTClient = new TcpClient();
				Thread nettyClient = new Thread(() -> {
					try {
						String ipAdress = IPAddress.getLocalIpAdress();
						if (!varIOTClient.nettyClient(ipAdress, port)) {
//							log.info("----ip:{},port:{}----启动失败", ipAdress, port);
							interruptThread(port);
						}
					} catch (InterruptedException e) {
						log.error(e.getMessage());
					}
				});
				addThread(port, nettyClient, varIOTClient);
				nettyClient.setName(String.format("thread:%s:port:%s", varStr, port));
				nettyClient.start();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			varResult.setSuccess(false);
		}
		return varResult;
	}

	/**
	 * 获取端口对应的 线程列表
	 *
	 * @return
	 */
	public static Map<Integer, Thread> getThreadNetty() {
		Set<Integer> varSet = threadNetty.keySet();
		Map<Integer, Thread> varTest = new ConcurrentHashMap<>();
		for (Integer iterm : varSet) {
			ClientThread varClientThread = threadNetty.get(iterm);
			varTest.put(iterm, varClientThread.getThreadClient());
		}
		return varTest;
	}

	/**
	 * 获取端口和ip对应的 线程列表
	 *
	 * @return
	 */
	public static Map<String, Thread> getIpThreadNetty() {
		Set<String> varSet = ipThreadNetty.keySet();
		Map<String, Thread> varTest = new ConcurrentHashMap<>();
		for (String iterm : varSet) {
			ClientThread varClientThread = ipThreadNetty.get(iterm);
			varTest.put(iterm, varClientThread.getThreadClient());
		}
		return varTest;
	}

}