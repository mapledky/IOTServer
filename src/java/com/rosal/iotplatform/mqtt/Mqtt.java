/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.mqtt;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author maple
 */
public class Mqtt extends HttpServlet {

    private ServletConfig config;

    private MqttClient mqttClient;

    private MemoryPersistence persistence = new MemoryPersistence();

    /**
     * @param config
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;

        //订阅mqtt会话
        initMqtt();
    }

    //初始化mqtt
    private void initMqtt() {
        try {
            mqttClient = new MqttClient(MqttParam.EMQX_URL, MqttParam.EMQX_user, persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(MqttParam.EMQX_user);
            options.setPassword(MqttParam.EMQX_pass.toCharArray());
            mqttClient.connect(options);
            subAllMsg();
        } catch (MqttException e) {
            System.out.println("reason" + e.getReasonCode());
            System.out.println("msg" + e.getMessage());
            System.out.println("loc" + e.getLocalizedMessage());
            System.out.println("cause" + e.getCause());
            System.out.println("excep" + e);
            Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    //订阅所有网关的信息
    private void subAllMsg() {
        try {
            mqttClient.subscribe(MqttParam.iot_device_update_topic);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String msg = new String(message.getPayload());
                    
                    if(topic.equals(MqttParam.iot_device_dead)){
                        //如果是网关掉线
                        //数据库网关状态置1
                    } else {
                        //实时数据上传
                        JSONObject jSONObject = JSONObject.parseObject(msg);
                        String sensor_id = jSONObject.getString("sensor_id");
                        String timestamp = jSONObject.getString("timestamp");
                        String version = jSONObject.getString("version");
                        String data_version = jSONObject.getString("data_version");
                        JSONObject jSONObject_dataupload = JSONObject.parseObject(jSONObject.getString("data_upload"));
                        
                    }                 
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }

                @Override
                public void connectionLost(Throwable throwable) {
                }
            });
        } catch (MqttException ex) {
            Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
