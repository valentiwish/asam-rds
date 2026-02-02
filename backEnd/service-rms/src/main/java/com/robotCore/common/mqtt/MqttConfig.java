package com.robotCore.common.mqtt;

import com.robotCore.common.config.RequestQueue;
import com.robotCore.common.mqtt.inPlant.MqttProcess;
import com.utils.tools.JsonUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Description: MQTT发送消息配置
 * @Author: zhangqi
 * @Create: 2020/7/2 18:17
 */
@Configuration
@IntegrationComponentScan
public class MqttConfig {

    private final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    /**
     *存储设备最新一条状态数据
     */
    public static Map<String, Object> deviceStatus = new HashMap<String, Object>(1);

    @Value("${asam.mqtt.username}")
    private String username;

    @Value("${asam.mqtt.password}")
    private String password;

    @Value("${asam.mqtt.url}")
    private String hostUrl;

    @Value("${asam.mqtt.client.outId}")
    private String clientOutId;

    @Value("${asam.mqtt.client.inId}")
    private String clientInId;

    @Value("${asam.mqtt.devicead.topic}")
    private String deviceadSubTopic;

    @Value("${asam.mqtt.broker.topic}")
    private String brokerSubTopic;

    @Value("${asam.mqtt.default.topic}")
    private String defaultSubTopic;

    @Value("${asam.mqtt.queryResultTopic}")
    private String queryResultTopic;

    @Value("${asam.mqtt.setResult}")
    private String setResult;

    @Value("${asam.mqtt.stateTopic}")
    private String stateTopic;

    @Value("${asam.mqtt.inPlant_topic_subscribe}")
    private String inPlantTopic;

    @Value("${asam.mqtt.offset}")
    private boolean offset;

    @Value("${project.name}")
    private String projectName;

    @Autowired
    private MqttProcess solveMqttProcess;

    @Autowired
    private RequestQueue requestQueue;

    @Bean
    public MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{hostUrl});
        mqttConnectOptions.setKeepAliveInterval(60);
        mqttConnectOptions.setMaxInflight(100);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setConnectionTimeout(30);
        return mqttConnectOptions;
    }
    /**
     * MQTT客户端
     *
     * @return {@link org.springframework.integration.mqtt.core.MqttPahoClientFactory}
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    /**
     * MQTT信息通道（消费者）
     *
     * @return {@link MessageChannel}
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT消息订阅绑定（消费者）
     *
     * @return {@link org.springframework.integration.core.MessageProducer}
     */
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientInId + new Date().getTime(), mqttClientFactory());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        //adapter.setQos(1);
        //359厂三台重载
        adapter.addTopic(stateTopic, 0);
        //359厂九分厂
        adapter.addTopic(inPlantTopic, 0);
        //389厂7210
        adapter.addTopic(defaultSubTopic, 0);
        adapter.addTopic(deviceadSubTopic, 0);
        adapter.addTopic(brokerSubTopic, 0);
        adapter.addTopic(queryResultTopic, 0);
        adapter.addTopic(setResult, 0);
      /*  adapter.addTopic(ddj1StateTopic, 0);
        adapter.addTopic(ddj2StateTopic, 0);*/
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


    /**
     * 中控MQTT消息处理器（消费者）
     *
     * @return {@link MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    @ConditionalOnProperty(name = "mqtt.active", havingValue = "inPlant", matchIfMissing = true)
    public MessageHandler inPlantHandler(){
        return message -> {
            String recevieStr = "";
            String topic = "";
            try{
                recevieStr = message.getPayload().toString();
                topic = message.getHeaders().get("mqtt_receivedTopic").toString();
//                logger.info("主题：{}，接收MQTT数据：{}", topic, recevieStr);
                solveMqttProcess.subscribe(topic,recevieStr);
                //添加设备数据队列(389厂7210)
                if ("389厂7210".equals(projectName)) {
                    requestQueue.getEquipQueue().put(recevieStr);
                }
//                logger.info("当前设备数据队列存数：{}", requestQueue.getEquipQueue().size());
            }catch (Exception e){
                logger.error("主题：{}，接收MQTT数据异常：{}", topic, e.getMessage());
            }
        };
    }

    /**
     * 亚控MQTT消息处理器（消费者）
     *
     * @return {@link MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    @ConditionalOnProperty(name = "mqtt.active", havingValue = "king")
    public MessageHandler kingHandler(){
        return message -> {
            String recevieStr = "";
            String topic = "";
            try{
                recevieStr = message.getPayload().toString();
                topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                logger.info("主题：{}，接收MQTT数据：{}", topic, recevieStr);
                Map<String, Object> map1=JsonUtils.toMap(recevieStr);
//                asynTaskService.solveTopic(topic,map1);  //这是210仓储推送的数据

                //环境监测的数据 发给tpm的接收去处理数据
               /* Map<String, Object> map = JsonUtils.toMap(recevieStr);
                JSONObject pvsjson = null;
                JSONArray objsjson = null;
                List<DataInfo> dataList=new ArrayList<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if ("PVs".equals(entry.getKey())) {
                        //变量属性默认值数组，其数组内各元素值与PNS中变量属性数组的元素一一对应。时间戳使用UTC时间格式表示
                        //1：变量的值；
                        //2：变量的时间戳；
                        // 3：变量的质量戳；
                        pvsjson = JSONObject.parseObject(entry.getValue().toString());
                    }
                    if ("Objs".equals(entry.getKey())) {
                        //变量对象数组；
                        // N：变量名称，字符长度不超过128个字符；
                        // 1：实时值（配置偏移使能：该值与PVs中的基准值进行比较，相同则没有该字段；若没配置偏移使能则该字段代表真实值）；
                        // 2：时间戳（配置偏移使能：该值与PVs中的基准值进行比较，相同则没有该字段，不相同值为偏移量，偏移量单位为毫秒（ms）；若没配置偏移使能则该字段代表真实值）；
                        // 3：质量戳（配置偏移使能：该值与PVs中的基准值进行比较，相同则没有该字段；若没配置偏移使能则该字段代表真实值）；
                        objsjson = JSONArray.parseArray(entry.getValue().toString());
                    }
                }
                //获取变量名称、值、时间戳
                if(ObjectUtil.isNotEmpty(pvsjson)&& ObjectUtil.isNotEmpty(objsjson)) {
                    DataInfo dataInfo =null;
                    for (int i = 0; i < objsjson.size(); i++) {
                        dataInfo = new DataInfo();
                        JSONObject object = JSONObject.parseObject(objsjson.getString(i));
                        //变量名称
                        if (object.containsKey("N")&&object.getString("N").indexOf("$")==-1) {//ioserver会传过来一下自动带￥的变量 需要剔除掉
                            dataInfo.setHostname(object.getString("N"));
                            //获取值
                            if (object.containsKey("1")) {
                                dataInfo.setValue(Double.valueOf(object.getString("1")));
                            } else {
                                dataInfo.setValue(Double.valueOf(pvsjson.getString("1")));
                            }
                            //获取时间
                            if (!offset) {//如果没有配置偏移势能  该字段代表真实值 单位ms
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                Date date = sdf.parse(object.getString("2"));
                                long ts = date.getTime();
                                dataInfo.setTime(ts);
                            } else {//如果配置了配置偏移势能 该值与PVs中的基准值进行比较，相同则没有该字段，如果有值就是真实值
                                if (object.containsKey("2")) {
                                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                    Date date1 = sdf.parse(pvsjson.getString("2"));
                                    long ts1 = date1.getTime();
                                    long ts2 = Long.parseLong(object.getString("2"));
                                    dataInfo.setTime(ts1 + ts2);
                                } else {
                                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                    Date date = sdf.parse(pvsjson.getString("2"));
                                    long ts = date.getTime();
                                    dataInfo.setTime(ts);
                                }
                            }
                            if(dataInfo.getHostname()!=null&&dataInfo.getValue()!=null&&dataInfo.getTime()!=null) {
                                dataList.add(dataInfo);
                                //用push发送界面上
                                if(topic.equals("environmental_monitoring")) {
                                    //发送到界面push展示
                                    BusinessDataVO businessDataVO = new BusinessDataVO();
                                    businessDataVO.setData(dataInfo);
                                    businessDataVO.setCode("environment");
                                    pusherLiteService.sendBusinessMessage(businessDataVO);
                                }
                            }
                        }
                    }
                }
                if(topic.equals("environmental_monitoring")) {
                    if (dataList.size() > 0) {
                        //发送到TPM进行存储
                        String listStr = toJSONString(dataList, SerializerFeature.WriteMapNullValue);
                        iServiceWms.saveRealData(listStr);
                    }
                }*/

            }catch (Exception e){
                logger.error("主题：{}，接收MQTT数据异常：{}", topic, e.getMessage());
            }
        };
    }

    /**
     * MQTT消息处理器（生产者）
     *
     * @return {@link MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =  new MqttPahoMessageHandler(clientOutId + new Date().getTime(), mqttClientFactory());
        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic(ssxStateTopic);
        return messageHandler;
    }
    /**
     * MQTT信息通道（生产者）
     *
     * @return {@link MessageChannel}
     * */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(10) ;// 例如，创建一个固定大小为10的线程池
    }

}
