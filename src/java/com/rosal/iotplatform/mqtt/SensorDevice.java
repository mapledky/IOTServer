/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.mqtt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maple
 */
public class SensorDevice {

    public static ConcurrentHashMap<String, SensorDevice> sensor_deivce = new ConcurrentHashMap<>();

    public int msg_number = 0;//每日数据量
    public String company_id = "0";//工厂id
    public String net_id = "0";//网关id
    public String sensor_id = "0";//设备id

    public LinkedBlockingQueue<MqttMsg> message = new LinkedBlockingQueue();

    public void addMessage(MqttMsg msg) {
        msg_number++;
        if (message.size() == 0) {
            try {
                message.put(msg);
            } catch (InterruptedException ex) {
                Logger.getLogger(SensorDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //做数据筛选判断
            MqttMsg last_msg = message.peek();
            if (Long.parseLong(msg.timestamp) > Long.parseLong(last_msg.timestamp)) {
                if (!msg.data_upload.equals(last_msg.data_upload)) {
                    try {
                        message.put(msg);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SensorDevice.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
