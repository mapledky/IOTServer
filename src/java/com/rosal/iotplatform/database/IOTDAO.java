/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.database;

import com.alibaba.fastjson.JSONObject;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maple
 */
public class IOTDAO {

    //注册及获取ID（唯一标识符）
    /*
    
    提供
    1.注册所用账号（电话号码，邮箱，智慧云平台标识符)
    2.个人密码
    3.注册方式
        1.短信
        2.邮箱
        3.智慧云平台
    
     */
    public static JSONObject register(String registerNumber, String password, String registerway) {
        JSONObject jSONObject = new JSONObject();
        return jSONObject;
    }

    //根据设备device_sk获取MQTT连接参数
    public static JSONObject getDeviceInfo(String device_sk) {
        JSONObject jSONObject = new JSONObject();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql_search = "SELECT * FROM iot_device WHERE device_id = ?";
        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, device_sk);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String Id = resultSet.getString("Id");
                String device_name = resultSet.getString("device_name");
                String company = resultSet.getString("company");
                String password = resultSet.getString("password");
                String dismiss = resultSet.getString("dismiss");

                jSONObject.put("result", "1");
                jSONObject.put("device_id", device_sk);
                jSONObject.put("device_name", device_name);
                jSONObject.put("company", company);
                jSONObject.put("password", password);
                jSONObject.put("dismiss", dismiss);
            } else {
                //设备未注册
                jSONObject.put("result", "0");
            }
            return jSONObject;
        } catch (PropertyVetoException | SQLException ex) {
            jSONObject.put("result", "0");
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONObject;
    }

}
