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
import com.rosal.iotplatform.util.PlatformUnion;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
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
            case "003"://发送邮箱
                requestMail(request, response);
                break;
            case "004"://验证邮箱
                requestCheckMail(request, response);
                break;
            default:
                break;

        }
    }

    //请求发送邮件
    private void requestMail(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jSONObject = new JSONObject();
        String address = request.getParameter("address");

        if (address == null) {
            return;
        }

        int code = (int) (Math.random() * 8998.0) + 1000 + 1;//随机获取验证码
        boolean requestTime = true;
        //将数据加入缓存数据库存储,检测用户是否重复请求
        if (RedisUtil.exist("sms_request" + address)) {
            //检测是否在请求间隔60秒
            Map data = RedisUtil.getMap("sms_request" + address);
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
            try {
                URL url = new URL(PlatformUnion.mail_url);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                // 设置请求方法
                urlConnection.setRequestMethod("POST");
                // 设置请求的超时时间
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);
                // 传递的数据
                String data = "apiUser=" + URLEncoder.encode(PlatformUnion.mail_api_user, "UTF-8")
                        + "&apiKey=" + URLEncoder.encode(PlatformUnion.mail_api_key, "UTF-8")
                        + "&fromName=" + URLEncoder.encode(PlatformUnion.mail_api_fromName, "UTF-8")
                        + "&from=" + URLEncoder.encode(PlatformUnion.mail_api_from, "UTF-8")
                        + "&to=" + URLEncoder.encode(address, "UTF-8")
                        + "&subject=" + URLEncoder.encode("枫叶物联平台验证", "UTF-8")
                        + "&plain=" + URLEncoder.encode("尊敬的用户，您的验证码为:" + String.valueOf(code) + ",请在5分钟内进行验证。", "UTF-8");
                // 设置请求的头
                urlConnection.setRequestProperty("Connection", "keep-alive");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                // 发送POST请求必须设置允许输出
                urlConnection.setDoOutput(true);
                // 发送POST请求必须设置允许输入
                urlConnection.setDoInput(true);
                // setDoInput的默认值就是true
                // 获取输出流,将参数写入
                OutputStream os;
                os = urlConnection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    // 获取响应的输入流对象
                    InputStream is = urlConnection.getInputStream();
                    // 创建字节输出流对象
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // 定义读取的长度
                    int len = 0;
                    // 定义缓冲区
                    byte[] buffer = new byte[1024];
                    // 按照缓冲区的大小，循环读取
                    while ((len = is.read(buffer)) != -1) {
                        // 根据读取的长度写入到os对象中
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    // 释放资源
                    is.close();
                    byteArrayOutputStream.close();
                    // 返回字符串
                    JSONObject result = JSONObject.parseObject(new String(byteArrayOutputStream.toByteArray()));
                    if (result.getString("result").equals("true")) {
                        //请求成功
                        Map<String, String> data_redis = new HashMap<String, String>();
                        long timestamp = System.currentTimeMillis();
                        data_redis.put("phoneNumber", address);
                        data_redis.put("smsCode", String.valueOf(code));
                        data_redis.put("time", String.valueOf(timestamp));
                        RedisUtil.delete("sms_request" + address);
                        RedisUtil.setMap("sms_request" + address, data_redis, 300);//设置过期时间5分钟
                        jSONObject.put("result", "1");
                        jSONObject.put("sms_id", timestamp);
                    } else {
                        jSONObject.put("result", "0");
                    }
                } else {
                    jSONObject.put("result", "0");
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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

    //验证邮箱
    private void requestCheckMail(HttpServletRequest request, HttpServletResponse response) {

        String address;
        String code;
        String smsCode;
        String password;

        address = request.getParameter("address");
        code = request.getParameter("code");
        smsCode = request.getParameter("sms_id");
        password = request.getParameter("password");

        if (address == null || code == null || smsCode == null || password == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        //验证短信验证码
        if (RedisUtil.exist("sms_request" + address)) {
            Map data = RedisUtil.getMap("sms_request" + address);
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
                    jsonObject = IOTDAO.register(address, password, "2");
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
