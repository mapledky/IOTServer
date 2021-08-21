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
}
