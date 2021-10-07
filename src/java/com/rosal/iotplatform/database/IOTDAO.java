/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.database;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rosal.iotplatform.util.EmojiAdapter;
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

    //用户web前端登录
    /*
    
    account:电话号码，邮箱，或者第三方平台识别吗
    password:sha256加密密码
    loginway 
    1;短信
    2;邮箱
    3；第三方平台
    
    
    返回
    1：登录成功
    2：密码错误
    0：不存在该用户
    
     */
    public static JSONObject login(String account, String password, String loginway) {
        JSONObject jSONObject = new JSONObject();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql_search = null;
        String sql_update_token = null;

        String token_new = String.valueOf(System.currentTimeMillis());

        switch (loginway) {
            case "1":
                sql_search = "SELECT * FROM userinfo WHERE phoneNumber = ?";
                sql_update_token = "UPDATE userinfo SET token = ? WHERE Id = ?";
                break;
            case "2":
                sql_search = "SELECT * FROM userinfo WHERE email = ?";
                sql_update_token = "UPDATE userinfo SET token = ? WHERE Id = ?";
                break;
            case "3":
                sql_search = "SELECT * FROM userinfo WHERE otherplat = ?";
                sql_update_token = "UPDATE userinfo SET token = ? WHERE Id = ?";
                break;
            default:
                break;
        }

        try {
            connection = C3P0Util.getConnection();
            //根据账号搜索是否存在用户
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, account);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                //存在用户
                String Id = resultSet.getString("Id");
                String user_id = resultSet.getString("user_id");
                String password_correct = resultSet.getString("password");
                String phoneNumber = resultSet.getString("phoneNumber");
                String email = resultSet.getString("email");
                String otherplat = resultSet.getString("otherplat");
                String company = resultSet.getString("company");
                String username = resultSet.getString("username");
                String modal = resultSet.getString("modal");
                String token = resultSet.getString("token");
                String dismiss = resultSet.getString("dismiss");

                //判断密码是否正确
                if (password_correct.equals(password)) {
                    //密码正确
                    //更新token
                    preparedStatement = connection.prepareStatement(sql_update_token);
                    preparedStatement.setString(1, token_new);
                    preparedStatement.setString(2, Id);
                    preparedStatement.executeUpdate();

                    jSONObject.put("result", "1");
                    jSONObject.put("user_id", user_id);
                    jSONObject.put("password", password);
                    jSONObject.put("phoneNumber", phoneNumber);
                    jSONObject.put("email", email);
                    jSONObject.put("otherplat", otherplat);
                    jSONObject.put("company", company);
                    jSONObject.put("username", username);
                    jSONObject.put("modal", modal);
                    jSONObject.put("token", token_new);
                    jSONObject.put("dismiss", dismiss);

                    return jSONObject;
                } else {
                    //密码错误
                    jSONObject.put("result", "2");

                    return jSONObject;
                }
            } else {
                //该用户不存在
                jSONObject.put("result", "0");

                return jSONObject;
            }
        } catch (PropertyVetoException | SQLException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONObject;
    }

    //修改用户名
    public static boolean changeUserName(String username,String user_id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        JSONObject jSONObject = new JSONObject();
        String sql_update = "UPDATE userinfo SET username = ? WHERE user_id = ?";
        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_update);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, user_id);
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return false;
    }
    //获取用户信息
    public static JSONArray getUserinfo(String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();

        String sql_search = null;
        if (company_id.equals("0")) {
            sql_search = "SELECT * FROM userinfo";
        } else {
            sql_search = "SELECT * FROM userinfo WHERE company = ?";
        }

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            if (!company_id.equals("0")) {
                preparedStatement.setString(1, company_id);
            }
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
//                String Id = resultSet.getString("Id");
                String user_id = resultSet.getString("user_id");
//                String password_correct = resultSet.getString("password");
                String phoneNumber = resultSet.getString("phoneNumber");
                String email = resultSet.getString("email");
                String otherplat = resultSet.getString("otherplat");
                String company = resultSet.getString("company");
                String username = resultSet.getString("username");
                String modal = resultSet.getString("modal");
                String token = resultSet.getString("token");
                String dismiss = resultSet.getString("dismiss");
                jSONObject.put("user_id", user_id);
                jSONObject.put("phoneNumber", phoneNumber);
                jSONObject.put("email", email);
                jSONObject.put("otherplat", otherplat);
                jSONObject.put("company", company);
                jSONObject.put("username", username);
                jSONObject.put("modal", modal);
                jSONObject.put("token", token);
                jSONObject.put("dismiss", dismiss);
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //初始化用户
    public static boolean initUser(String user_id, String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql_user = "SELECT * FROM userinfo WHERE user_id = ?";
        String sql_search = "UPDATE userinfo SET company = ?,modal = ? WHERE user_id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_user);
            preparedStatement.setString(1, user_id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if ((!resultSet.getString("modal").equals("3")) && (company_id.equals(resultSet.getString("company")) || company_id.equals("0"))) {
                    preparedStatement = connection.prepareStatement(sql_search);
                    preparedStatement.setString(1, "0");
                    preparedStatement.setString(2, "1");
                    preparedStatement.setString(3, user_id);

                    preparedStatement.executeUpdate();
                    //更改streamsets权限
                    LDAP.deleteUserFromGroup(user_id);
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return false;
    }

    //将用户置为管理
    public static boolean changeUserToCN(String user_id, String company_id, String type) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();

        String sql_search = "UPDATE userinfo SET company = ?,modal = ? WHERE user_id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            if (type.equals("1")) {
                preparedStatement.setString(1, company_id);
                preparedStatement.setString(2, "2");
                preparedStatement.setString(3, user_id);
            } else {
                preparedStatement.setString(1, "0");
                preparedStatement.setString(2, "3");
                preparedStatement.setString(3, user_id);
            }

            preparedStatement.executeUpdate();
            //更改streamsets权限
            LDAP.deleteUserFromGroup(user_id);
            if (type.equals("1")) {
                LDAP.addUserToGroup(user_id, "2");
            } else {
                LDAP.addUserToGroup(user_id, "3");
            }
            return true;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return false;
    }

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
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql_search = null;
        String sql_insert = null;

        switch (registerway) {
            case "1":
                //短信验证:
                sql_search = "SELECT * FROM userinfo WHERE phoneNumber = ?";
                sql_insert = "INSERT INTO userinfo (phoneNumber,password) VALUES (?,?)";
                break;
            case "2":
                sql_search = "SELECT * FROM userinfo WHERE email = ?";
                sql_insert = "INSERT INTO userinfo (email,password) VALUES (?,?)";
                break;
            case "3":
                sql_search = "SELECT * FROM userinfo WHERE otherplat = ?";
                sql_insert = "INSERT INTO userinfo (otherplat,password) VALUES (?,?)";
                break;
            default:
                break;

        }

        String sql_update = "UPDATE userinfo SET password = ?,token = ? WHERE Id = ?";
        String sql_updateUser_id = "UPDATE userinfo SET user_id = ?,token = ? WHERE Id = ?";

        String token_new = String.valueOf(System.currentTimeMillis());
        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, registerNumber);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //用户已经注册过

                String Id = resultSet.getString("Id");
                String user_id = resultSet.getString("user_id");
                String phoneNumber = resultSet.getString("phoneNumber");
                String email = resultSet.getString("email");
                String otherplat = resultSet.getString("otherplat");
                String company = resultSet.getString("company");
                String username = resultSet.getString("username");
                String modal = resultSet.getString("modal");
                String token = resultSet.getString("token");
                String dismiss = resultSet.getString("dismiss");

                //该业务进行密码更改，，，同时更改EMQ服务器鉴权数据表
                //更改PGSQL鉴权库数据
                if (PGDAO.registerCertification(user_id, password)) {
                    //已更改数据到PGSQL
                    //更新密码
                    //更新token

                    preparedStatement = connection.prepareStatement(sql_update);
                    preparedStatement.setString(1, password);
                    preparedStatement.setString(2, token_new);
                    preparedStatement.setString(3, Id);
                    preparedStatement.executeUpdate();

                    jSONObject.put("result", "1");
                    jSONObject.put("user_id", user_id);
                    jSONObject.put("password", password);
                    jSONObject.put("phoneNumber", phoneNumber);
                    jSONObject.put("email", email);
                    jSONObject.put("otherplat", otherplat);
                    jSONObject.put("company", company);
                    jSONObject.put("username", username);
                    jSONObject.put("modal", modal);
                    jSONObject.put("token", token_new);
                    jSONObject.put("dismiss", dismiss);

                    return jSONObject;
                }

            } else {
                //用户未注册
                /*
                
                注册流程
                 */
                preparedStatement = connection.prepareStatement(sql_insert, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, registerNumber);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();

                if (resultSet.next()) {
                    //更新数据
                    String Id = String.valueOf(resultSet.getInt(1));
                    String user_id = "user_" + Id;
                    preparedStatement = connection.prepareStatement(sql_updateUser_id);
                    preparedStatement.setString(1, user_id);
                    preparedStatement.setString(2, token_new);
                    preparedStatement.setString(3, Id);
                    preparedStatement.executeUpdate();

                    //更新PGSQL
                    //更新完成
                    if (PGDAO.registerCertification(user_id, password)) {
                        //更新streamsets鉴权数据
                        if (LDAP.addUser(user_id)) {
                            jSONObject.put("result", "1");
                            jSONObject.put("user_id", user_id);
                            jSONObject.put("password", password);
                            jSONObject.put("company", "0");
                            jSONObject.put("username", "username");
                            jSONObject.put("modal", "1");
                            jSONObject.put("token", token_new);
                            jSONObject.put("dismiss", "1");
                            return jSONObject;
                        }
                    } else {
                        jSONObject.put("result", "0");
                        return jSONObject;
                    }
                }
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

    //根据设备device_sk获取MQTT连接参数
    public static JSONObject getDeviceInfo(String device_sk) {
        JSONObject jSONObject = new JSONObject();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql_search = "SELECT * FROM iot_device WHERE device_id = ?";

        String sql_update_state = "UPDATE iot_device SET dismiss = ? WHERE Id = ?";
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

                if (dismiss.equals("3")) {
                    //网关未进行MQTT注册
                    jSONObject.put("result", "2");
                } else {
                    //更新网关状态为在线状态
                    preparedStatement = connection.prepareStatement(sql_update_state);
                    preparedStatement.setString(1, "2");
                    preparedStatement.setString(2, Id);
                    preparedStatement.executeUpdate();
                    jSONObject.put("result", "1");
                    jSONObject.put("device_id", device_sk);
                    jSONObject.put("device_name", device_name);
                    jSONObject.put("company", company);
                    jSONObject.put("password", password);
                    jSONObject.put("dismiss", dismiss);
                }
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

    //获取所有网关
    /*
    
    获取特定工厂网关
     */
    public static JSONArray getNetworkModal(String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();
        String sql_search = "SELECT * FROM iot_device WHERE company = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, company_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("device_id", resultSet.getString("device_id"));
                jSONObject.put("device_name", resultSet.getString("device_name"));
                jSONObject.put("company", resultSet.getString("company"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //添加网关，非注册
    public static String addNetModal(String netdevice_name, String company_id, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql_insert = "INSERT INTO iot_device (device_name,company,password) VALUES (?,?,?)";
        String sql_update = "UPDATE iot_device SET device_id = ? WHERE Id = ?";
        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_insert, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, EmojiAdapter.emojiConvert(netdevice_name));
            preparedStatement.setString(2, company_id);
            preparedStatement.setString(3, password);

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                String device_id = String.valueOf(resultSet.getInt(1));
                preparedStatement = connection.prepareStatement(sql_update);
                preparedStatement.setString(1, "device_" + device_id);
                preparedStatement.setString(2, device_id);
                preparedStatement.executeUpdate();
                return "device_" + device_id;
            }
            return "0";
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return "0";
    }

    //注册网关
    public static boolean registerNetModal(String device_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql_search = "SELECT * FROM iot_device WHERE device_id = ?";

        String sql_update = "UPDATE iot_device SET dismiss = ? WHERE Id = ?";
        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, device_id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String password = resultSet.getString("password");
                String Id = resultSet.getString("Id");
                //更新pg数据库
                if (PGDAO.registerCertification(device_id, password)) {
                    preparedStatement = connection.prepareStatement(sql_update);
                    preparedStatement.setString(1, "1");
                    preparedStatement.setString(2, Id);
                    preparedStatement.executeUpdate();

                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return false;
    }

    //根据设备id获取设备
    public static JSONObject getDeviceById(String sensor_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONObject jSONObject = new JSONObject();
        String sql_search = "SELECT * FROM sensorinfo WHERE Id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, sensor_id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                jSONObject.put("sensor_id", resultSet.getString("Id"));
                jSONObject.put("sensor_name", EmojiAdapter.emojiRecovery(resultSet.getString("sensor_name")));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("create_time", resultSet.getString("create_time"));
                jSONObject.put("product_id", resultSet.getString("product_id"));
                jSONObject.put("latitude", resultSet.getString("latitude"));
                jSONObject.put("longitude", resultSet.getString("longitude"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
            }
            return jSONObject;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONObject;
    }
    
    //根据产品获取设备
    public static JSONArray getDeviceByproduct(String product_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();
        String sql_search = "SELECT * FROM sensorinfo WHERE product_id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, product_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("sensor_id", resultSet.getString("Id"));
                jSONObject.put("sensor_name", EmojiAdapter.emojiRecovery(resultSet.getString("sensor_name")));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("create_time", resultSet.getString("create_time"));
                jSONObject.put("product_id", resultSet.getString("product_id"));
                jSONObject.put("latitude", resultSet.getString("latitude"));
                jSONObject.put("longitude", resultSet.getString("longitude"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //获取设备
    public static JSONArray getDevice(String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();
        String sql_search = "SELECT * FROM sensorinfo WHERE company_id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, company_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("sensor_id", resultSet.getString("Id"));
                jSONObject.put("sensor_name", EmojiAdapter.emojiRecovery(resultSet.getString("sensor_name")));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("create_time", resultSet.getString("create_time"));
                jSONObject.put("product_id", resultSet.getString("product_id"));
                jSONObject.put("latitude", resultSet.getString("latitude"));
                jSONObject.put("longitude", resultSet.getString("longitude"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //获取所有设备
    public static JSONArray getAllDevice() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();
        String sql_search = "SELECT * FROM sensorinfo";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("sensor_id", resultSet.getString("Id"));
                jSONObject.put("sensor_name", EmojiAdapter.emojiRecovery(resultSet.getString("sensor_name")));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("create_time", resultSet.getString("create_time"));
                jSONObject.put("product_id", resultSet.getString("product_id"));
                jSONObject.put("latitude", resultSet.getString("latitude"));
                jSONObject.put("longitude", resultSet.getString("longitude"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //  创建新的设备
    public static String createDevice(String sensor_name, String company_id, String product_id, String latitude, String longitude) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql_search = "INSERT INTO sensorinfo (sensor_name,company_id,create_time,product_id,latitude,longitude) VALUES (?,?,?,?,?,?)";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, EmojiAdapter.emojiConvert(sensor_name));
            preparedStatement.setString(2, company_id);
            preparedStatement.setString(3, String.valueOf(System.currentTimeMillis()));
            preparedStatement.setString(4, product_id);

            preparedStatement.setString(5, latitude);
            preparedStatement.setString(6, longitude);
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                String sensor_id = String.valueOf(resultSet.getInt(1));
                return sensor_id;
            }
            return "0";
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return "0";
    }
    
    
    //根据id获取产品
    public static JSONObject getProductById(String product_id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONObject jSONObject = new JSONObject();
        String sql_search = "SELECT * FROM productinfo WHERE Id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, product_id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                jSONObject.put("product_id", resultSet.getString("Id"));
                jSONObject.put("product_name", EmojiAdapter.emojiRecovery(resultSet.getString("product_name")));
                jSONObject.put("product_des", EmojiAdapter.emojiRecovery(resultSet.getString("product_des")));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("images", JSONArray.parse(resultSet.getString("images")));
                jSONObject.put("data_format", JSONArray.parse(resultSet.getString("data_format")));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
            }
            return jSONObject;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONObject;
    }

    //获取所有产品]
    public static JSONArray getAllProduct(String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();
        String sql_search = "SELECT * FROM productinfo WHERE company_id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, company_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("product_id", resultSet.getString("Id"));
                jSONObject.put("product_name", EmojiAdapter.emojiRecovery(resultSet.getString("product_name")));
                jSONObject.put("product_des", EmojiAdapter.emojiRecovery(resultSet.getString("product_des")));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("images", JSONArray.parse(resultSet.getString("images")));
                jSONObject.put("data_format", JSONArray.parse(resultSet.getString("data_format")));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //修改产品
    public static boolean changeProduct(String product_id, String product_name, String product_des, String company_id, String images, String data_format) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql_search = "UPDATE productinfo SET product_name = ?,product_des = ?,company_id = ?,images = ?,data_format = ? WHERE Id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, EmojiAdapter.emojiConvert(product_name));
            preparedStatement.setString(2, EmojiAdapter.emojiConvert(product_des));
            preparedStatement.setString(3, company_id);
            preparedStatement.setString(4, images);
            preparedStatement.setString(5, data_format);
            preparedStatement.setString(6, product_id);
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return false;
    }

    //创建工厂
    public static String createFactory(String name, String latitude, String longitude) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String createtime = String.valueOf(System.currentTimeMillis());

        String sql_search = "INSERT INTO company (company_name,create_time,latitude,longitude) VALUES (?,?,?,?)";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, EmojiAdapter.emojiConvert(name));
            preparedStatement.setString(2, createtime);
            preparedStatement.setString(3, latitude);
            preparedStatement.setString(4, longitude);

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                String factory_id = String.valueOf(resultSet.getInt(1));
                return factory_id;
            }
            return "0";
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return "0";
    }

    //创建新的产品
    public static String createProduct(String product_name, String product_des, String company_id, String images, String data_format) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql_search = "INSERT INTO productinfo (product_name,product_des,company_id,images,data_format) VALUES (?,?,?,?,?)";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, EmojiAdapter.emojiConvert(product_name));
            preparedStatement.setString(2, EmojiAdapter.emojiConvert(product_des));
            preparedStatement.setString(3, company_id);
            preparedStatement.setString(4, images);
            preparedStatement.setString(5, data_format);

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                String product_id = String.valueOf(resultSet.getInt(1));
                return product_id;
            }
            return "0";
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return "0";
    }

    //删除设备
    public static boolean deleteDevice(String device_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql_search = "UPDATE sensorinfo SET dismiss = ? WHERE Id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, "0");
            preparedStatement.setString(2, device_id);

            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return false;
    }

    //获取单个工厂
    public static JSONObject getFactoryByid(String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONObject jSONObject = new JSONObject();
        String sql_search = "SELECT * FROM company WHERE Id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, company_id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                jSONObject.put("company_id", resultSet.getString("Id"));
                jSONObject.put("company_name", EmojiAdapter.emojiRecovery(resultSet.getString("company_name")));
                jSONObject.put("create_time", resultSet.getString("create_time"));
                jSONObject.put("latitude", resultSet.getString("latitude"));
                jSONObject.put("longitude", resultSet.getString("longitude"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
            }
            return jSONObject;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONObject;
    }
    //获取工厂信息

    public static JSONArray getFactoryInfo() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();
        String sql_search = "SELECT * FROM company";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("company_id", resultSet.getString("Id"));
                jSONObject.put("company_name", EmojiAdapter.emojiRecovery(resultSet.getString("company_name")));
                jSONObject.put("create_time", resultSet.getString("create_time"));
                jSONObject.put("latitude", resultSet.getString("latitude"));
                jSONObject.put("longitude", resultSet.getString("longitude"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //申请加入工厂
    public static JSONObject getintoFactory(String type, String user_id, String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONObject jSONObject = new JSONObject();

        String sql_con = "INSERT INTO request_form (type,user_id,company_id) VALUES (?,?,?)";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_con, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, user_id);
            preparedStatement.setString(3, company_id);

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                String id = String.valueOf(resultSet.getString(1));
                jSONObject.put("result", "1");
                jSONObject.put("request_id", id);
            } else {
                jSONObject.put("result", "0");
            }
            return jSONObject;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONObject;
    }

    //获取工厂所有申请
    public static JSONArray getAllRequest(String company_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONArray jSONONArray = new JSONArray();
        String sql_search = "SELECT * FROM request_form WHERE company_id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, company_id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("request_id", resultSet.getString("Id"));
                jSONObject.put("type", resultSet.getString("type"));
                jSONObject.put("user_id", resultSet.getString("user_id"));
                jSONObject.put("company_id", resultSet.getString("company_id"));
                jSONObject.put("dismiss", resultSet.getString("dismiss"));
                jSONONArray.add(jSONObject);
            }
            return jSONONArray;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONONArray;
    }

    //操作申请
    public static JSONObject changeRequest(String request_id, String company_id, String type) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        JSONObject jSONObject = new JSONObject();

        String sql_search = "SELECT * FROM request_form WHERE Id = ?";
        String sql_con = "UPDATE request_form SET dismiss = ? WHERE Id = ?";
        String sql_updare = "UPDATE userinfo SET company = ?,modal = ? WHERE user_id = ?";

        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, request_id);
            resultSet = preparedStatement.executeQuery();
            String user_id = null;
            String company = null;
            if (resultSet.next()) {
                user_id = resultSet.getString("user_id");
                company = resultSet.getString("company_id");

            }
            if (company.equals(company_id)) {
                preparedStatement = connection.prepareStatement(sql_con);
                switch (type) {
                    case "0":
                        //否定
                        preparedStatement.setString(1, "0");
                        break;
                    case "1":
                        //同意
                        preparedStatement.setString(1, "2");
                        break;
                    default:
                        break;
                }

                preparedStatement.setString(2, request_id);
                preparedStatement.executeUpdate();

                if (type.equals("1")) {
                    preparedStatement = connection.prepareStatement(sql_updare);
                    preparedStatement.setString(1, company_id);
                    preparedStatement.setString(2, "1");
                    preparedStatement.setString(3, user_id);
                    preparedStatement.executeUpdate();
                    jSONObject.put("result", "1");
                } else {
                    jSONObject.put("result", "1");
                }
            } else {
                jSONObject.put("result", "0");
            }
            return jSONObject;
        } catch (SQLException | PropertyVetoException ex) {
            Logger.getLogger(IOTDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            C3P0Util.close(connection, preparedStatement, resultSet);
        }
        return jSONObject;
    }

    /*
    
    
    
    验证token有效性
    
    token设置为1天内有效
    
    无效用户需退出重新登录
    
    
     */
    public static JSONObject checkToken(String user_id, String token) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        JSONObject jSONObject = new JSONObject();
        String sql_search = "SELECT * FROM userinfo WHERE user_id = ?";
        try {
            connection = C3P0Util.getConnection();
            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.setString(1, user_id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String token_old = resultSet.getString("token");
                if (token.equals(token_old)) {
                    //token匹配
                    //验证token时效性
                    if (Math.abs(System.currentTimeMillis() - Long.parseLong(token)) <= 1000 * 60 * 60 * 24) {
                        //token有效
                        String Id = resultSet.getString("Id");
                        String phoneNumber = resultSet.getString("phoneNumber");
                        String email = resultSet.getString("email");
                        String otherplat = resultSet.getString("otherplat");
                        String company = resultSet.getString("company");
                        String username = resultSet.getString("username");
                        String modal = resultSet.getString("modal");
                        String dismiss = resultSet.getString("dismiss");

                        jSONObject.put("result", "1");
                        jSONObject.put("user_id", user_id);
                        jSONObject.put("phoneNumber", phoneNumber);
                        jSONObject.put("email", email);
                        jSONObject.put("otherplat", otherplat);
                        jSONObject.put("company", company);
                        jSONObject.put("username", username);
                        jSONObject.put("modal", modal);
                        jSONObject.put("token", token);
                        jSONObject.put("dismiss", dismiss);
                    } else {
                        jSONObject.put("result", "0");
                    }
                } else {
                    jSONObject.put("result", "0");
                }
            } else {
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
