/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.database;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maple
 */
public class PGDAO {

    /*
    
    
    修改远程鉴权库密码或注册新的身份
     */
    public static boolean registerCertification(String client_id, String password_sha256) {
        Connection connection = PGBase.getConnectionToMQTT();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql_search = "SELECT * FROM mqtt_user WHERE username = ?";

        String sql_update = "UPDATE mqtt_user SET password = ? WHERE username = ?";

        String sql_insert = "INSERT INTO mqtt_user (is_superuser,username,password) VALUES (?,?,?)";

        try {
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, client_id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //不是新用户，更改密码
                preparedStatement = connection.prepareStatement(sql_update);
                preparedStatement.setString(1, password_sha256);
                preparedStatement.setString(2, client_id);
                preparedStatement.executeUpdate();
                return true;
            } else {
                //是新用户，插入数据
                preparedStatement = connection.prepareStatement(sql_insert);
                preparedStatement.setBoolean(1, false);
                preparedStatement.setString(2, client_id);
                preparedStatement.setString(3, password_sha256);
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PGDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(PGDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }

    }

    //获取单一设备实时数据
    public static JSONArray getDeviceData(String sensor_id) {
        Connection connection = PGBase.getConnectionToData();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        JSONArray jSONArray = new JSONArray();

        try {
            String sql_insert = "SELECT * FROM lathe WHERE sensor_id = ?";

            preparedStatement = connection.prepareStatement(sql_insert);
            preparedStatement.setString(1, sensor_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("time", resultSet.getString("time"));
                jSONObject.put("sensor_id", resultSet.getString("sensor_id"));
                jSONObject.put("data", resultSet.getObject("data"));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("device_id", resultSet.getString("device_id"));
                jSONArray.add(jSONObject);
            }
            return jSONArray;

        } catch (SQLException ex) {
            Logger.getLogger(PGDAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();

                }
            } catch (SQLException ex) {
                Logger.getLogger(PGDAO.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            return jSONArray;
        }
    }
    //sql操作

    public static JSONArray sql_con(String sql) {
        Statement statement = null;
        Connection connection = PGBase.getConnectionToData();
        JSONArray jSONArray = new JSONArray();

        try {
            statement = connection.createStatement();
            statement.execute(sql);

            while (true) {
                int rowCount = statement.getUpdateCount();
                if (rowCount > 0) { // 它是更新计数

                    jSONArray.add("Rows changed = " + rowCount);
                    statement.getMoreResults();
                    continue;
                }
                if (rowCount == 0) { // DDL 命令或 0 个更新
                    statement.getMoreResults();
                    continue;
                }
// 执行到这里，证明有一个结果集
// 或没有其它结果'
                ResultSet rs = statement.getResultSet();
                if (rs != null) {
                    jSONArray.add(resultSetToJson(rs));
                    statement.getMoreResults();
                    continue;
                }
                break; // 没有其它结果
            }
        } catch (SQLException ex) {
            jSONArray.add(ex.toString());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();

                }
            } catch (SQLException ex) {
                jSONArray.add(ex.toString());
            }
        }
        return jSONArray;
    }

    public static JSONArray resultSetToJson(ResultSet rs) {
        // json数组
        JSONArray array = new JSONArray();
        // 获取列数
        ResultSetMetaData metaData;
        try {
            metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 遍历ResultSet中的每条数据
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonObj.put(columnName, value);
                }
                array.add(jsonObj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PGDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return array;
    }

    //添加实时数据
    public static boolean addData(String company_id, String device_id, String sensor_id, String timestamp, String data_upload) {
        Connection connection = PGBase.getConnectionToData();
        PreparedStatement preparedStatement = null;
        try {
            String sql_insert = "INSERT INTO lathe (time,sensor_id,data,device_id,company_id) VALUES (?,?,?,?,?)";

            preparedStatement = connection.prepareStatement(sql_insert);
            preparedStatement.setString(1, timestamp);
            preparedStatement.setString(2, sensor_id);
            preparedStatement.setString(3, data_upload);
            preparedStatement.setString(4, device_id);
            preparedStatement.setString(5, company_id);
            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(PGDAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();

                }
            } catch (SQLException ex) {
                Logger.getLogger(PGDAO.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }

    }

}
