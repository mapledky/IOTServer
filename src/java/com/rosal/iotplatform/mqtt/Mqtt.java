/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.mqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rosal.iotplatform.database.PGBase;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
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

        //开启实时数据上传线程
        UploadThread();

        //24小时数据刷新
        refreshData();
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

                    if (topic.equals(MqttParam.iot_device_dead)) {
                        //如果是网关掉线
                        //数据库网关状态置1
                    } else {
                        //实时数据上传
                        JSONObject jSONObject = JSONObject.parseObject(msg);
                        MqttMsg mqttdata = new MqttMsg();
                        mqttdata.sensor_id = jSONObject.getString("sensor_id");
                        mqttdata.company_id = jSONObject.getString("company_id");
                        mqttdata.device_id = jSONObject.getString("device_id");
                        mqttdata.timestamp = jSONObject.getString("timestamp");
                        mqttdata.version = jSONObject.getString("version");
                        mqttdata.data_version = jSONObject.getString("data_version");
                        mqttdata.data_upload = jSONObject.getString("data_upload");

                        //将消息存入对应设备实时数据队列
                        if (SensorDevice.sensor_deivce.containsKey(mqttdata.sensor_id)) {
                            //设备存在
                            SensorDevice.sensor_deivce.get(mqttdata.sensor_id).addMessage(mqttdata);
                        } else {
                            //设备不存在
                            SensorDevice newdevice = new SensorDevice();
                            newdevice.company_id = mqttdata.company_id;
                            newdevice.net_id = mqttdata.device_id;
                            newdevice.sensor_id = mqttdata.sensor_id;
                            newdevice.addMessage(mqttdata);
                            SensorDevice.sensor_deivce.put(mqttdata.sensor_id, newdevice);
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }

                @Override
                public void connectionLost(Throwable throwable) {
                    initMqtt();
                }
            });
        } catch (MqttException ex) {
            Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //实时数据批量上传
    private void UploadThread() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        //5分钟刷新一次
                        Thread.sleep(5 * 60 * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    Connection connection = PGBase.getConnectionToData();
                    PreparedStatement preparedStatement = null;
                    try {

                        String sql_insert = "INSERT INTO lathe (time,sensor_id,data,device_id,company_id) VALUES (?,?,?,?,?)";

                        //将数据存入时序数据库
                        //iterating over values only
                        for (SensorDevice device_val : SensorDevice.sensor_deivce.values()) {
                            while (device_val.message.size() != 0) {
                                try {
                                    MqttMsg msg = device_val.message.take();
                                    preparedStatement = connection.prepareStatement(sql_insert);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = new Date(Long.parseLong(msg.timestamp));

                                    preparedStatement.setTimestamp(1, Timestamp.valueOf(sdf.format(date)));
                                    preparedStatement.setString(2, msg.sensor_id);
                                    preparedStatement.setString(3, msg.data_upload);
                                    preparedStatement.setString(4, msg.device_id);
                                    preparedStatement.setString(5, msg.company_id);
                                    preparedStatement.executeUpdate();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            if (preparedStatement != null) {
                                preparedStatement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }
        }).start();
    }

    //24小时数据刷新
    private void refreshData() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * 60 * 60 * 24);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //刷新数据
                    for (SensorDevice device_val : SensorDevice.sensor_deivce.values()) {
                        device_val.msg_number = 0;
                    }
                }
            }
        }).start();
    }
}
