/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * use to send mail autification
 *
 * @author maple
 */
public class Mail {
    
    public static void sendMail(){
                        
    }

    public static MimeMessage createSimpleMail(Session session,String from,String to,String title,String content)
            throws Exception {
        //创建邮件对象                                                                                                                                 
        MimeMessage message = new MimeMessage(session);
        //指明邮件的发件人                                                                                                                               
        message.setFrom(new InternetAddress(from));
        //指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发                                                                                                       
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        //邮件的标题                                                                                                                                  
        message.setSubject(title);
        //邮件的文本内容                                                                                                                                
        message.setContent(content, "text/html;charset=UTF-8");
        //返回创建好的邮件对象                                                                                                                             
        return message;
    }
}
