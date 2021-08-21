/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.rosal.iotplatform.util.PlatformUnion;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author maple
 */
public class C3P0Util extends HttpServlet {

    ServletConfig config;//定义一个ServletConfig对象
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();

    private static String user = PlatformUnion.MYSQL_username;
    private static String password = PlatformUnion.MYSQL_password;

    private static String expressDatabase = PlatformUnion.MYSQL_platformdata;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }

    /**
     *
     *
     * @return Connection
     * @throws java.beans.PropertyVetoException
     */
    public static Connection getConnection() throws PropertyVetoException {
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver");
            cpds.setJdbcUrl(expressDatabase);
            cpds.setUser(user);
            cpds.setPassword(password);
            setProperty(cpds);
            return cpds.getConnection();
        } catch (SQLException e) {
            return null;
        }
    }

    public static void setProperty(ComboPooledDataSource cpds) {
        cpds.setAcquireIncrement(10);// 可以设置连接池的各种属性
        cpds.setMaxPoolSize(1000);
        cpds.setAutoCommitOnClose(true);
        cpds.setCheckoutTimeout(3000);
        cpds.setMinPoolSize(3);
        cpds.setMaxConnectionAge(20);
        cpds.setMaxIdleTimeExcessConnections(20);
        cpds.setMaxStatements(0);
        cpds.setMaxIdleTime(10);
    }

    public static void close(Connection conn, PreparedStatement pst, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (pst != null) {
            try {
                pst.close();
            } catch (SQLException e) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

}
