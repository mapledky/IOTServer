/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.main;

import com.alibaba.fastjson.JSONObject;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.rosal.iotplatform.database.IOTDAO;
import com.rosal.iotplatform.database.RedisUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author maple
 */
public class SMS extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String requestCode = request.getParameter("requestCode");

        switch (requestCode) {
            case "001"://请求发送短信验证码
                requestSMS(request, response);
                break;
            case "002"://请求验证短信，并完成后续注册步骤
                requestCheckSMS(request, response);
                break;
            default:
                break;

        }
    }

    //请求发送验证码
    private void requestSMS(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jSONObject = new JSONObject();
        String phoneNumber = request.getParameter("phoneNumber");

        if (phoneNumber == null) {
            return;
        }
        int code = (int) (Math.random() * 8998.0) + 1000 + 1;//随机获取验证码
        boolean requestTime = true;
        //将数据加入缓存数据库存储,检测用户是否重复请求
        if (RedisUtil.exist("sms_request" + phoneNumber)) {
            //检测是否在请求间隔60秒
            Map data = RedisUtil.getMap("sms_request" + phoneNumber);
            /*
                data格式
                phoneNumber:xxx
                smsCode:xxx
                time:xxx(时间戳)
             */
            long timestate = System.currentTimeMillis();
            if ((timestate - Long.parseLong((String) data.get("time"))) < 60000) {
                //间隔小于60秒
                requestTime = false;
            }
        }
        if (requestTime) {
            //生产环境请求地址：app.cloopen.com
            String serverIp = "app.cloopen.com";
            //请求端口
            String serverPort = "8883";
            //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
            String accountSId = "8aaf070870e20ea101712bd4c20d26c1";
            String accountToken = "073cddfa15ef40b3becff595671d30a2";
            //请使用管理控制台中已创建应用的APPID
            String appId = "8aaf070870e20ea101712bd4c27426c8";
            CCPRestSmsSDK sdk = new CCPRestSmsSDK();
            sdk.init(serverIp, serverPort);
            sdk.setAccount(accountSId, accountToken);
            sdk.setAppId(appId);
            sdk.setBodyType(BodyType.Type_JSON);
            String to = phoneNumber;
            String templateId = "579197";
            String[] datas = {String.valueOf(code), "60秒", ""};
            HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
            if ("000000".equals(result.get("statusCode"))) {
                Map<String, String> data = new HashMap<String, String>();
                long timestamp = System.currentTimeMillis();
                data.put("phoneNumber", phoneNumber);
                data.put("smsCode", String.valueOf(code));
                data.put("time", String.valueOf(timestamp));
                RedisUtil.delete("sms_request" + phoneNumber);
                RedisUtil.setMap("sms_request" + phoneNumber, data, 300);//设置过期时间5分钟
                jSONObject.put("result", "1");
                jSONObject.put("sms_id", timestamp);
            } else {
                //异常返回输出错误码和错误信息
                jSONObject.put("result", "0");
            }
        } else {
            //用户重复请求次数过多，1分钟后再次请求
            jSONObject.put("result", "2");
        }
        try ( PrintWriter out = response.getWriter()) {
            out.write(jSONObject.toString());
        } catch (IOException ex) {
            Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //验证短信
    /*
    
    
    
    进行下一步注册等业务
     */
    private void requestCheckSMS(HttpServletRequest request, HttpServletResponse response) {

        String phoneNumber;
        String code;
        String smsCode;
        String password;

        phoneNumber = request.getParameter("phoneNumber");
        code = request.getParameter("code");
        smsCode = request.getParameter("sms_id");
        password = request.getParameter("password");

        if (phoneNumber == null || code == null || smsCode == null || password == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        //验证短信验证码
        if (RedisUtil.exist("sms_request" + phoneNumber)) {
            Map data = RedisUtil.getMap("sms_request" + phoneNumber);
            //检测时间戳
            long timestate = System.currentTimeMillis();
            if (data.get("time").equals(smsCode) && (timestate - Long.parseLong((String) data.get("time"))) < 300000) {
                //时间戳符合且时间小于5分钟
                if (code.equals(data.get("smsCode"))) {
                    //验证码符合要求
                    //查找数据库匹配用户Id
                    /*
                    
                    注册或验证用户
                    
                     */
                    String user_id = null;
                    jsonObject = IOTDAO.register(phoneNumber, password, "1");
                } else {
                    jsonObject.put("result", "2");
                }
            } else {
                jsonObject.put("result", "2");
            }
        } else {
            jsonObject.put("result", "2");
        }
        try ( PrintWriter out = response.getWriter()) {
            out.write(jsonObject.toString());
        } catch (IOException ex) {
            Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
