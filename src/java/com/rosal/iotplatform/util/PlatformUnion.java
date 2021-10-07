/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.util;

/**
 *
 * @author maple
 */
public class PlatformUnion {

    //database
    public static String PGSQL_Mqtt_State = "jdbc:postgresql://116.63.40.126:5432/mqtt";
    public static String PGSQL_data_State = "jdbc:postgresql://116.63.40.126:5432/activedata";
    public static String PGSQL_Username = "postgres";
    public static String PGSQL_password = "200105DuKeyu";

    public static String MYSQL_platformdata = "jdbc:mysql://rm-bp1rhj2u2554p5up0.mysql.rds.aliyuncs.com:3306/maple_iot?useUnicode=true&characterEncoding=utf8&useSSL=false";
    public static String MYSQL_username = "maple";
    public static String MYSQL_password = "200105DuKeyu";

    //ldap
    public static String ldap_url = "ldap://116.63.40.126:389";
    public static String ldap_basedn = "dc=server";
    public static String ldap_root = "cn=admin,dc=server";
    public static String ldap_pwd = "200105DuKeyu";
    
    
    //mail
    
    public static String mail_url = "https://api.sendcloud.net/apiv2/mail/send";
    public static String mail_api_user = "mallow_dky";
    public static String mail_api_key= "bb2d76fe13ea76b705623f8f1e0851d2";
    public static String mail_api_from = "maple_iot@maple.today";
    public static String mail_api_fromName = "枫叶物联平台";
    
    //mqtt
    public static String MQTT_INFO = "http://116.63.40.126:8081/api/v4/metrics";
    public static String MQTT_Client = "http://116.63.40.126:8081/api/v4/clients";
    public static String MQTT_Client_INFO = "http://116.63.40.126:8081/api/v4/stats";
    public static String MQTT_APP_ID = "941a1b3dc0452";
    public static String MQTT_APP_PASS = "MzAwNjQwNTIzMzg2MDc3Mzc0MjExNTMzODg3MDk3NDA1NDE";
    
    
}
