/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rosal.iotplatform.database.IOTDAO;
import com.rosal.iotplatform.database.LDAP;
import java.io.IOException;
import java.io.PrintWriter;
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
public class ServerMain extends HttpServlet {

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
            case "001"://请求根据设备密钥,获取设备MQTT协议参数
                requestMQTTparam(request, response);
                break;

            case "002"://用户web前端登录
                requestLogin(request, response);
                break;

            case "003"://获取工厂网关
                requestBelongModal(request, response);
                break;

            case "004"://创建产品
                createProduct(request, response);
                break;

            case "005"://获取产品信息
                getProduct(request, response);
                break;

            case "006"://创建设备
                createDevice(request, response);
                break;

            case "007"://获取设备
                getDevice(request, response);
                break;

            case "008"://添加网关
                addNetModal(request, response);
                break;

            case "009"://注册网关
                registerNetModal(request, response);
                break;

            case "010"://测试请求
                testRequest(request, response);
                break;

            case "011"://请求工厂信息
                requestFactory(request, response);
                break;

            case "012"://删除设备
                deleteDevice(request, response);
                break;
            case "013"://申请加入工厂
                PrivilageUtil.requestPrivilage(request, response);
                break;

            case "014"://查看所有申请
                PrivilageUtil.searchAllRequest(request, response);
                break;

            case "015"://操作申请
                PrivilageUtil.updateRequest(request, response);
                break;

            case "016"://查看工厂员工信息
                PrivilageUtil.getFatoryMenber(request, response);
                break;

            case "017"://添加管理员
                PrivilageUtil.addCN(request, response);
                break;

            case "018"://删除工厂管理员或工厂员工
                PrivilageUtil.deleteUser(request, response);
                break;

            case "019"://获取所有产品信息
                MapUtil.getSensorInfo(request, response);
                break;

            case "020"://修改产品信息
                changeProductInfo(request, response);
                break;

            case "021"://查看单个工厂信息
                MapUtil.getFactoryById(request, response);
                break;

            case "022"://获取所有用户信息
                PrivilageUtil.getAllFatoryMenber(request, response);
                break;

            case "023"://更改streamsets密码
                changeStreamsetsPass(request, response);
                break;

            case "024"://创建工厂
                PrivilageUtil.createFatory(request, response);
                break;
            default:
                break;

        }
    }

    //工业设备根据产品密钥,请求MQTT参数
    private void requestMQTTparam(HttpServletRequest request, HttpServletResponse response) {
        String device_sk = request.getParameter("device_sk");
        JSONObject jSONObject = null;
        if (device_sk == null) {
            return;
        }
        //根据device_sk在数据库中获取设备对应MQTT参数以及设备的基本参数
        jSONObject = IOTDAO.getDeviceInfo(device_sk);

        try ( PrintWriter out = response.getWriter()) {
            out.write(jSONObject.toString());
        } catch (IOException ex) {
            Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //更改streamsets密码
    private void changeStreamsetsPass(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        if (user_id == null || token == null || password == null) {
            return;
        }

        JSONObject jSONObject = new JSONObject();
        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */
            LDAP.modifyPass(user_id, password);
            jSONObject.put("result", "1");
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

    //用户web前端登录
    private void requestLogin(HttpServletRequest request, HttpServletResponse response) {
        String account = request.getParameter("account");
        String loginway = request.getParameter("loginway");
        /*
        
        1：短信
        2：邮箱
        3：第三方平台
        
         */
        String password = request.getParameter("password");

        if (account == null || loginway == null || password == null) {
            return;
        }

        JSONObject jSONObject = IOTDAO
                .login(account, password, loginway);
        try ( PrintWriter out = response.getWriter()) {
            out.write(jSONObject.toString());
        } catch (IOException ex) {
            Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //注册网关
    private void registerNetModal(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");
        String device_id = request.getParameter("device_id");

        if (user_id == null || token == null || company_id == null || device_id == null) {
            return;
        }

        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();

        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            
            只有系统管理员才能注册网关
            1:普通
            2：工厂管理
            3：系统管理
             */

            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //注册网关
                if (IOTDAO.registerNetModal(device_id)) {
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

    //获取所有网关
    private void requestBelongModal(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        if (user_id == null || token == null || company_id == null) {
            return;
        }

        JSONObject jSONObject = new JSONObject();
        JSONArray jSONONArray = null;
        //验证token有效性
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //获取所有网关
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */

            if (company_id.equals(jSONObject_userinfo.getString("company")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //获取网关
                jSONONArray = IOTDAO.getNetworkModal(company_id);
                jSONObject.put("result", "1");
                jSONObject.put("data", jSONONArray);
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

    //获取设备
    private void getDevice(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        if (user_id == null || token == null || company_id == null) {
            return;
        }
        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */

            if (company_id.equals(jSONObject_userinfo.getString("company")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //获取设备信息
                jSONArray = IOTDAO.getDevice(company_id);
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

    //添加网关
    private void addNetModal(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        String device_name = request.getParameter("device_name");
        String password = request.getParameter("password");

        if (user_id == null || token == null || company_id == null || device_name == null || password == null) {
            return;
        }

        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();

        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */

            if ((company_id.equals(jSONObject_userinfo.getString("company")) && jSONObject_userinfo.getString("modal").equals("2")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //创建信息
                String device_id = IOTDAO.addNetModal(device_name, company_id, password);
                if (!device_id.equals("0")) {
                    jSONObject.put("result", "1");
                    jSONObject.put("device_id", device_id);
                } else {
                    jSONObject.put("result", "0");
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

    //创建设备
    private void createDevice(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        String sensor_name = request.getParameter("sensor_name");
        String product_id = request.getParameter("product_id");

        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");

        if (user_id == null || token == null || company_id == null || sensor_name == null || product_id == null || latitude == null || longitude == null) {
            return;
        }

        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();

        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */

            if ((company_id.equals(jSONObject_userinfo.getString("company")) && jSONObject_userinfo.getString("modal").equals("2")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //创建设备信息
                String sensor_id = IOTDAO.createDevice(sensor_name, company_id, product_id, latitude, longitude);
                if (!sensor_id.equals("0")) {
                    jSONObject.put("result", "1");
                    jSONObject.put("sensor_id", sensor_id);
                } else {
                    jSONObject.put("result", "0");
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

    //获取产品
    private void getProduct(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        if (user_id == null || token == null || company_id == null) {
            return;
        }
        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            1:普通
            2：工厂管理
            3：系统管理
             */

            if (company_id.equals(jSONObject_userinfo.getString("company")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //获取产品信息
                jSONArray = IOTDAO.getAllProduct(company_id);
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

    //修改产品
    private void changeProductInfo(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");
        String product_name = request.getParameter("product_name");
        String product_des = request.getParameter("product_des");
        String images = request.getParameter("images");
        String data_format = request.getParameter("data_format");
        String product_id = request.getParameter("product_id");

        if (product_id == null || user_id == null || token == null || company_id == null || product_name == null || product_des == null || images == null || data_format == null) {
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
            if ((company_id.equals(jSONObject_userinfo.getString("company")) && jSONObject_userinfo.getString("modal").equals("2")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //更改产品信息
                if (IOTDAO.changeProduct(product_id, product_name, product_des, company_id, images, data_format)) {
                    jSONObject.put("result", "1");
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

    //创建产品
    private void createProduct(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");
        String product_name = request.getParameter("product_name");
        String product_des = request.getParameter("product_des");
        String images = request.getParameter("images");
        String data_format = request.getParameter("data_format");

        if (user_id == null || token == null || company_id == null || product_name == null || product_des == null || images == null || data_format == null) {
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

            if ((company_id.equals(jSONObject_userinfo.getString("company")) && jSONObject_userinfo.getString("modal").equals("2")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //获取产品信息
                String product_id = IOTDAO.createProduct(product_name, product_des, company_id, images, data_format);
                if (!product_id.equals("0")) {
                    jSONObject.put("result", "1");
                    jSONObject.put("product_id", product_id);
                } else {
                    jSONObject.put("result", "0");
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

    //获取所有工厂信息
    private void requestFactory(HttpServletRequest request, HttpServletResponse response) {
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
                //获取工厂信息
                JSONArray jSONArray = IOTDAO.getFactoryInfo();
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

    //删除设备
    private void deleteDevice(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");

        String device_id = request.getParameter("device_id");

        if (user_id == null || token == null || company_id == null || device_id == null) {
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

            if ((company_id.equals(jSONObject_userinfo.getString("company")) && jSONObject_userinfo.getString("modal").equals("2")) || jSONObject_userinfo.getString("modal").equals("3")) {
                //删除设备
                if (IOTDAO.deleteDevice(device_id)) {
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

    //测试请求
    private void testRequest(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("result", "1");
        try ( PrintWriter out = response.getWriter()) {
            out.write(jSONObject.toString());
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
