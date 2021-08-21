/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rosal.iotplatform.database.IOTDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author maple
 */
public class MapUtil {

    public static void getFactoryById(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        if (user_id == null || token == null || company_id == null) {
            return;
        }
        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();

        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */
            if (company_id.equals(jSONObject_userinfo.getString("company"))  || jSONObject_userinfo.getString("modal").equals("3")) {
                //获取工厂信息 
                jSONObject.put("result", "1");
                jSONObject.put("data", IOTDAO.getFactoryByid(company_id));
            } else {
                //权限不足
                jSONObject.put("result", "2");
            }
        } else {
            //token失效
            jSONObject.put("result", "0");
        }
        try ( PrintWriter out = response.getWriter()) {
            out.write(jSONObject.toString());
        } catch (IOException ex) {
            Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getSensorInfo(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");

        if (user_id == null || token == null) {
            return;
        }
        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();

        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */

            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //获取设备 
                JSONArray jSONArray = IOTDAO.getAllDevice();
                jSONObject.put("result", "1");
                jSONObject.put("data", jSONArray);
            } else {
                //权限不足
                jSONObject.put("result", "2");
            }
        } else {
            //token失效
            jSONObject.put("result", "0");
        }
        try ( PrintWriter out = response.getWriter()) {
            out.write(jSONObject.toString());
        } catch (IOException ex) {
            Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
