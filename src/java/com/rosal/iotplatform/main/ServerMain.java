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
import com.rosal.iotplatform.database.PGDAO;
import com.rosal.iotplatform.mqtt.MqttInfo;
import com.rosal.iotplatform.mqtt.SensorDevice;
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

            case "025"://根据产品id获取产品所属设备
                getDeviceByProduct(request, response);
                break;

            case "026"://根据用户输入的sql语句查询数据库
                searchDataBaseBysql(request, response);
                break;
            case "027"://根据设备id获取其时序数据
                getDeviceTimeData(request, response);
                break;

            case "028"://查看设备24小时内数据
                getDevice24(request, response);
                break;

            case "029"://获取mqtt服务器信息
                getMqttInfo(request, response);
                break;

            case "030"://获取客户端信息
                getMqttClientInfo(request, response);
                break;

            case "031"://获取整体客户端信息
                getMqttTotalClient(request, response);
                break;
            case "032"://将用户踢出服务器
                kickUser(request, response);
                break;
            case "033"://修改用户名
                changeUserName(request, response);
                break;
            default:
                break;

        }
    }

    //更改用户名
    private void changeUserName(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        
        String username = request.getParameter("username");

        if (user_id == null || token == null || username == null) {
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
            if(IOTDAO.changeUserName(username, user_id)){
                jSONObject.put("result", "1");
            } else {
                jSONObject.put("result", "3");
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

    private void getMqttTotalClient(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");

        if (user_id == null || token == null) {
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
            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //查询
                JSONObject jSONObject1 = MqttInfo.getMqttClientTotal();
                jSONObject.put("data", jSONObject1);
                jSONObject.put("result", "1");
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

    //将用户踢出服务器
    private void kickUser(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String client_id = request.getParameter("client_id");

        if (user_id == null || token == null || client_id == null) {
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
            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //查询
                if(MqttInfo.kickUser(client_id)){
                    jSONObject.put("result", "1");
                } else {
                    jSONObject.put("result", "0");
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

    //获取mqtt服务器信息
    private void getMqttInfo(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");

        if (user_id == null || token == null) {
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
            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //查询
                JSONObject jSONObject1 = MqttInfo.getMqttInfo();
                jSONObject.put("data", jSONObject1);
                jSONObject.put("result", "1");
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

    //获取mqtt服务器客户端信息
    private void getMqttClientInfo(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String page = request.getParameter("page");
        String page_con = request.getParameter("page_con");

        if (user_id == null || token == null || page == null || page_con == null) {
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
            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //查询
                JSONObject jSONArray = MqttInfo.getMqttClientInfo(page, page_con);
                jSONObject.put("data", jSONArray);
                jSONObject.put("result", "1");
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

    //根据sql语句操控时序数据库
    private void searchDataBaseBysql(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String sql = request.getParameter("sql");

        if (user_id == null || token == null || sql == null) {
            return;
        }

        //验证token
        JSONObject jSONObject_userinfo = IOTDAO.checkToken(user_id, token);
        JSONObject jSONObject = new JSONObject();
        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            只有系统管理员才能sql操作
            1:普通
            2：工厂管理
            3：系统管理
             */
            if (jSONObject_userinfo.getString("modal").equals("3")) {
                //sql操作
                jSONObject.put("result", "1");
                jSONObject.put("back", PGDAO.sql_con(sql));
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

    //获取设备24小时数据
    private void getDevice24(HttpServletRequest request, HttpServletResponse response) {
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
            
            
            只有系统管理员才能注册网关
            1:普通
            2：工厂管理
            3：系统管理
             */

            if (jSONObject_userinfo.getString("company").equals(company_id) || jSONObject_userinfo.getString("modal").equals("3")) {
                for (SensorDevice device_val : SensorDevice.sensor_deivce.values()) {
                    JSONObject jSONObject1 = new JSONObject();
                    jSONObject1.put("company_id", device_val.company_id);
                    jSONObject1.put("net_id", device_val.net_id);
                    jSONObject1.put("device_id", device_val.sensor_id);
                    jSONObject1.put("dataNumber", device_val.msg_number);
                    jSONArray.add(jSONObject1);
                }
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

    //根据设备id获取
    private void getDeviceTimeData(HttpServletRequest request, HttpServletResponse response) {
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

        JSONArray jSONArray = new JSONArray();
        if (jSONObject_userinfo.getString("result").equals("1")) {
            //token有效
            //判断权限
            /*
            
            
            只有系统管理员才能注册网关
            1:普通
            2：工厂管理
            3：系统管理
             */

            if (jSONObject_userinfo.getString("company").equals(company_id)) {
                if (IOTDAO.getDeviceById(device_id).getString("company_id").equals(company_id)) {
                    //根据id获取设备信息
                    jSONArray = PGDAO.getDeviceData(device_id);
                    jSONObject.put("result", "1");
                    jSONObject.put("data", jSONArray);
                } else {
                    jSONObject.put("result", "2");
                }
            } else if (jSONObject_userinfo.getString("modal").equals("3")) {
                jSONArray = PGDAO.getDeviceData(device_id);
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

    //根据产品id获取product
    private void getDeviceByProduct(HttpServletRequest request, HttpServletResponse response) {
        String user_id = request.getParameter("user_id");
        String token = request.getParameter("token");
        String company_id = request.getParameter("company_id");
        String product_id = request.getParameter("product_id");

        if (user_id == null || token == null || company_id == null || product_id == null) {
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

            if (company_id.equals(jSONObject_userinfo.getString("company"))) {
                if (IOTDAO.getProductById(product_id).getString("company_id").equals(company_id)) {
                    //根据产品获取设备信息
                    jSONArray = IOTDAO.getDeviceByproduct(product_id);
                    jSONObject.put("result", "1");
                    jSONObject.put("data", jSONArray);
                } else {
                    jSONObject.put("result", "2");
                }
            } else if (jSONObject_userinfo.getString("modal").equals("3")) {
                jSONArray = IOTDAO.getDeviceByproduct(product_id);
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
