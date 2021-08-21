/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.database;

import com.rosal.iotplatform.util.PlatformUnion;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 *
 * @author maple
 */
public class LDAP {

    static LdapContext context = null;

    public static void connect() {
        String factory = "com.sun.jndi.ldap.LdapCtxFactory";
        String simple = "simple";
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, PlatformUnion.ldap_url);
        env.put(Context.SECURITY_AUTHENTICATION, simple);
        env.put(Context.SECURITY_PRINCIPAL, PlatformUnion.ldap_root);
        env.put(Context.SECURITY_CREDENTIALS, PlatformUnion.ldap_pwd);
        Control[] connCtls = null;
        try {
            context = new InitialLdapContext(env, connCtls);
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败：");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("认证出错：");
            e.printStackTrace();
        }
    }

    public static void closeCon() {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //更改密码
    public static boolean modifyPass(String uid, String newpass) {
        connect();
        try {
            ModificationItem[] mods = new ModificationItem[1];
            String dn = "uid=" + uid + ",ou=user,dc=server";

            /*修改属性*/
            Attribute attr0 = new BasicAttribute("userPassword", newpass);
            mods[0] = new ModificationItem(context.REPLACE_ATTRIBUTE, attr0);
            context.modifyAttributes(dn, mods);
            return true;
        } catch (NamingException ne) {
            System.out.println("修改失败");
            ne.printStackTrace();
            return false;
        } finally {
            closeCon();
        }

    }

    //将用户删除组
    public static boolean deleteUserFromGroup(String uid) {
        connect();
        try {
            //将用户添加进普通组
            ModificationItem[] mods = new ModificationItem[1];
            String dn_groups_DEV = "cn=DEV,ou=groups,dc=server";
            String dn_groups_OPS = "cn=OPS,ou=groups,dc=server";
            Attribute attr0 = new BasicAttribute("memberUid", uid);
            
            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr0);
            context.modifyAttributes(dn_groups_DEV, mods);
            context.modifyAttributes(dn_groups_OPS, mods);
            return true;
        } catch (Exception e) {
            return true;
        } finally {
            closeCon();
        }
    }

    //将用户添加进其他组
    public static boolean addUserToGroup(String uid, String group) {
        connect();
        try {

            //将用户添加进普通组
            ModificationItem[] mods = new ModificationItem[1];
            String dn_groups = null;
            switch (group) {
                case "2":
                    dn_groups = "cn=DEV,ou=groups,dc=server";
                    break;
                case "3":
                    dn_groups = "cn=OPS,ou=groups,dc=server";
                    break;
                default:
                    break;
            }

            Attribute attr0 = new BasicAttribute("memberUid", uid);
            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr0);
            /*删除属性*/
            //  Attribute attr0 = new BasicAttribute("description", "测试");
            //  mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr0);
            context.modifyAttributes(dn_groups, mods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon();
        }
    }

    //添加user
    public static boolean addUser(String uid) {
        connect();
        BasicAttributes attrsbu = new BasicAttributes();
        BasicAttribute objclassSet = new BasicAttribute("objectClass");
        objclassSet.add("posixAccount");
        objclassSet.add("inetOrgPerson");
        objclassSet.add("top");
        objclassSet.add("shadowAccount");
        attrsbu.put(objclassSet);
        attrsbu.put("uid", uid);// 显示账号
        attrsbu.put("sn", uid);// 显示姓名
        attrsbu.put("cn", uid);// 显示账号
        attrsbu.put("userPassword", "123456");// 显示密码
        attrsbu.put("homeDirectory", uid);// 显示home地址
        attrsbu.put("uidNumber", 1);/* 显示id */
        attrsbu.put("gidNumber", 1);/* 显示组id */
        try {
            String dn = "uid=" + uid + ",ou=user,dc=server";
            context.createSubcontext(dn, attrsbu);

            //将用户添加进普通组
            ModificationItem[] mods = new ModificationItem[1];
            String dn_groups = "cn=ODI,ou=groups,dc=server";
            Attribute attr0 = new BasicAttribute("memberUid", uid);
            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr0);
            /*删除属性*/
            //  Attribute attr0 = new BasicAttribute("description", "测试");
            //  mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr0);
            context.modifyAttributes(dn_groups, mods);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon();
        }
    }
}
