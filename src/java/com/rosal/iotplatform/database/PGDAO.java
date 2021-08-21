/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.database;

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

}
