package com.rosal.iotplatform.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;


public class getApiResultUtil {


    /**
     * 
     *
     * @param url
     * @param param
     * @return
     */
    public static String getAPIResult(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setConnectTimeout(5000);
            String plainCredentials = PlatformUnion.MQTT_APP_ID+":"+PlatformUnion.MQTT_APP_PASS;
            String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
            conn.setRequestProperty("Authorization", "Basic " + base64Credentials);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
