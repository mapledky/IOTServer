/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.mqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.rosal.iotplatform.util.PlatformUnion;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;

/**
 *
 * @author maple
 */
public class MqttInfo {

    //踢出服务器
    public static boolean kickUser(String client_id){
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpDelete httpDelete = new HttpDelete(PlatformUnion.MQTT_Client + "?clientid=" + client_id);
        //添加http头信息 
        httpDelete.addHeader("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((PlatformUnion.MQTT_APP_ID + ":" + PlatformUnion.MQTT_APP_PASS).getBytes()));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        HttpEntity postEntity = builder.build();

        String result = "";
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        try {
            httpResponse = closeableHttpClient.execute(httpDelete);
            entity = httpResponse.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            closeableHttpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭连接
        return true;
    }
    //客户端信息    
    public static JSONObject getMqttClientTotal() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpGet httpget = new HttpGet(PlatformUnion.MQTT_Client_INFO);
        //添加http头信息 
        httpget.addHeader("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((PlatformUnion.MQTT_APP_ID + ":" + PlatformUnion.MQTT_APP_PASS).getBytes()));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        HttpEntity postEntity = builder.build();

        String result = "";
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        try {
            httpResponse = closeableHttpClient.execute(httpget);
            entity = httpResponse.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            closeableHttpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭连接
        return JSONObject.parseObject(result);
    }
    //查询mqtt服务器信息

    public static JSONObject getMqttClientInfo(String page, String page_con) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpGet httpget = new HttpGet(PlatformUnion.MQTT_Client + "?_page=" + page + "&_limit=" + page_con);
        //添加http头信息 
        httpget.addHeader("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((PlatformUnion.MQTT_APP_ID + ":" + PlatformUnion.MQTT_APP_PASS).getBytes()));

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        HttpEntity postEntity = builder.build();

        String result = "";
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        try {
            httpResponse = closeableHttpClient.execute(httpget);
            entity = httpResponse.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            closeableHttpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(result);
    }

    public static JSONObject getMqttInfo() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpGet httpget = new HttpGet(PlatformUnion.MQTT_INFO);
        //添加http头信息 
        httpget.addHeader("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((PlatformUnion.MQTT_APP_ID + ":" + PlatformUnion.MQTT_APP_PASS).getBytes()));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        HttpEntity postEntity = builder.build();

        String result = "";
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        try {
            httpResponse = closeableHttpClient.execute(httpget);
            entity = httpResponse.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            closeableHttpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭连接
        return JSONObject.parseObject(result);
    }

}
