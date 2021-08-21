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
public class PrivilageUtil {

    //创建工厂
    public static void createFatory(HttpServletRequest request, HttpServletResponse response) {

        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");

        String name = request.getParameter("name");
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");

        if (user_id == null || token == null || name == null || latitude == null || longitude == null) {
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
                String id = IOTDAO.createFactory(name, latitude, longitude);
                
                if(id.equals("0")){
                    jSONObject.put("result", "3");
                } else {
                    jSONObject.put("result", "1");
                    jSONObject.put("company_id", id);
                }
            } else {
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

    //申请加入工厂
    public static void requestPrivilage(HttpServletRequest request, HttpServletResponse response) {
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
            //申请加入工厂
            jSONObject = IOTDAO.getintoFactory("1", user_id, company_id);
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

    //获取工厂员工
    public static void getFatoryMenber(HttpServletRequest request, HttpServletResponse response) {
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
            //申请加入工厂
            if ((jSONObject_userinfo.getString("modal").equals("2") && company_id.equals(jSONObject_userinfo.getString("company"))) || jSONObject_userinfo.getString("modal").equals("3")) {
                //操作
                JSONArray jSONArray = IOTDAO.getUserinfo(company_id);
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

    //获取所有工厂员工
    public static void getAllFatoryMenber(HttpServletRequest request, HttpServletResponse response) {
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
            //申请加入工厂
            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //操作
                JSONArray jSONArray = IOTDAO.getUserinfo("0");
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

    //清除工厂员工，管理员,系统管理
    public static void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");
        String target_user_id = request.getParameter("target_user_id");

        if (target_user_id == null || user_id == null || token == null || company_id == null) {
            return;
        }
        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();

        if (jSONObject_userinfo.getString("result").equals("1")) {

            if ((jSONObject_userinfo.getString("modal").equals("2") && company_id.equals(jSONObject_userinfo.getString("company"))) || jSONObject_userinfo.getString("modal").equals("3")) {
                //token有效
                //判断权限
                /*
            
            1:普通
            2：工厂管理
            3：系统管理
                 */

                if (IOTDAO.initUser(target_user_id, company_id)) {
                    jSONObject.put("result", "1");
                } else {
                    jSONObject.put("result", "2");
                }
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

    //添加管理员
    public static void addCN(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String target_user_id = request.getParameter("target_user_id");

        String company_id = request.getParameter("company_id");
        String type = request.getParameter("type");
        /*
        type:1添加为工厂管理
        type:2添加为系统管理
         */
        if (target_user_id == null || user_id == null || token == null || company_id == null || type == null) {
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
            if (type.equals("1") && ((jSONObject_userinfo.getString("modal").equals("2") && company_id.equals(jSONObject_userinfo.getString("company"))) || jSONObject_userinfo.getString("modal").equals("3"))) {
                //操作
                //将指定user_id置为工管
                if (IOTDAO.changeUserToCN(target_user_id, company_id, type)) {
                    jSONObject.put("result", "1");
                } else {
                    jSONObject.put("result", "3");
                }
            } else if (type.equals("2") && jSONObject_userinfo.getString("modal").equals("3")) {
                //置为系管
                if (IOTDAO.changeUserToCN(target_user_id, company_id, type)) {
                    jSONObject.put("result", "1");
                } else {
                    jSONObject.put("result", "3");
                }
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

    //操作申请
    public static void updateRequest(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        String request_id = request.getParameter("request_id");
        String type = request.getParameter("type");
        /*
        type:
        1:同意
        0:否定
         */

        if (user_id == null || token == null || company_id == null || request_id == null || type == null) {
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
            if ((jSONObject_userinfo.getString("modal").equals("2") && company_id.equals(jSONObject_userinfo.getString("company"))) || jSONObject_userinfo.getString("modal").equals("3")) {
                //操作
                jSONObject = IOTDAO.changeRequest(request_id, company_id, type);
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

    //查看所有申请
    public static void searchAllRequest(HttpServletRequest request, HttpServletResponse response) {
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
            if ((jSONObject_userinfo.getString("modal").equals("2") && company_id.equals(jSONObject_userinfo.getString("company"))) || jSONObject_userinfo.getString("modal").equals("3")) {
                //获取工厂所有申请
                JSONArray jSONArray = IOTDAO.getAllRequest(company_id);
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
