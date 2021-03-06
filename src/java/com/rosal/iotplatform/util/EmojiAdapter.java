/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosal.iotplatform.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiAdapter {
	 public static String emojiConvert(String str) {
	        String patternString = "([\\x{10000}-\\x{10ffff}\\ud800-\\udfff])";
	        Pattern pattern = Pattern.compile(patternString);
	        Matcher matcher = pattern.matcher(str);
	        StringBuffer sb = new StringBuffer();
	        while(matcher.find()) {
	            try {
	                matcher.appendReplacement(sb,"[[" + URLEncoder.encode(matcher.group(1),"UTF-8") + "]]");
	            } catch (UnsupportedEncodingException e) {
	                e.printStackTrace();
	            }
	        }
	        matcher.appendTail(sb);
	        return sb.toString();
	    }


	    /**
	     * 还原utf8数据库中保存的含转换后emoji表情的字符串
	     * @param str
	     * @return
	     */
	 public static String emojiRecovery(String str) {
	        String patternString = "\\[\\[(.*?)\\]\\]";
	        Pattern pattern = Pattern.compile(patternString);
	        Matcher matcher = pattern.matcher(str);
	        StringBuffer sb = new StringBuffer();
	        while(matcher.find()) {
	            try {
	                matcher.appendReplacement(sb,
	                        URLDecoder.decode(matcher.group(1), "UTF-8"));
	            } catch(UnsupportedEncodingException e) {
	                return "";
	            }
	        }
	        matcher.appendTail(sb);
	        return sb.toString();
	    }
}

