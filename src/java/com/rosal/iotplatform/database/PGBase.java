/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.database;

import com.rosal.iotplatform.util.PlatformUnion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maple
 */
public class PGBase {

    //连接pg数据库，mqtt鉴权数据库
    public static Connection getConnectionToMQTT() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection(PlatformUnion.PGSQL_Mqtt_State,PlatformUnion.PGSQL_Username,PlatformUnion.PGSQL_password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(PGBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
}
