package com.enjoyshop.utils;


import com.enjoyshop.EnjoyshopApplication;
import com.google.gson.JsonObject;

import net.sf.json.JSONObject;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class HttpEncryptUtil {
    //APP加密请求内容
    public static String appEncrypt(String appPublicKeyStr, String content) throws Exception{
        //将Base64编码后的Server公钥转换成PublicKey对象
        PublicKey serverPublicKey = RSAUtil.string2PublicKey(KeyUtil.SERVER_PUBLIC_KEY);
        //每次都随机生成AES秘钥
        String aesKeyStr = AESUtil.genKeyAES();
        SecretKey aesKey = AESUtil.loadKeyAES(aesKeyStr);
        //用Server公钥加密AES秘钥
        byte[] encryptAesKey = RSAUtil.publicEncrypt(aesKeyStr.getBytes(), serverPublicKey);
        //用AES秘钥加密APP公钥
        byte[] encryptAppPublicKey = AESUtil.encryptAES(appPublicKeyStr.getBytes(), aesKey);
        //用AES秘钥加密请求内容
        //byte[] temp=content.getBytes("utf-8");
        byte[] encryptRequest = AESUtil.encryptAES(content.getBytes(), aesKey);

        JSONObject result = new JSONObject();
        result.put("ak", RSAUtil.byte2Base64(encryptAesKey).replaceAll("\r\n", ""));
        result.put("apk", RSAUtil.byte2Base64(encryptAppPublicKey).replaceAll("\r\n", ""));
        result.put("ct", RSAUtil.byte2Base64(encryptRequest).replaceAll("\r\n", ""));
        return result.toString();
    }
    //APP加密请求内容
    public static String appEncryptca(String appPublicKeyStr, String content) throws Exception{
        //将Base64编码后的Server公钥转换成PublicKey对象
        PublicKey serverPublicKey = RSAUtil.string2PublicKey(KeyUtil.CA_PUBLIC_KEY);
        //每次都随机生成AES秘钥
        String aesKeyStr = AESUtil.genKeyAES();
        SecretKey aesKey = AESUtil.loadKeyAES(aesKeyStr);
        //用Server公钥加密AES秘钥
        byte[] encryptAesKey = RSAUtil.publicEncrypt(aesKeyStr.getBytes(), serverPublicKey);
        //用AES秘钥加密APP公钥
        byte[] encryptAppPublicKey = AESUtil.encryptAES(appPublicKeyStr.getBytes(), aesKey);
        //用AES秘钥加密请求内容
        //byte[] temp=content.getBytes("utf-8");
        byte[] encryptRequest = AESUtil.encryptAES(content.getBytes(), aesKey);

        JSONObject result = new JSONObject();
        result.put("ak", RSAUtil.byte2Base64(encryptAesKey).replaceAll("\r\n", ""));
        result.put("apk", RSAUtil.byte2Base64(encryptAppPublicKey).replaceAll("\r\n", ""));
        result.put("ct", RSAUtil.byte2Base64(encryptRequest).replaceAll("\r\n", ""));
        return result.toString();
    }
    //APP解密服务器的响应内容
    public static String appDecrypt(String appPrivateKeyStr, String content) throws Exception{
        JSONObject result = JSONObject.fromObject(content);
        String encryptAesKeyStr = (String) result.get("ak");
        String encryptContent = (String) result.get("ct");

        //将Base64编码后的APP私钥转换成PrivateKey对象
        PrivateKey appPrivateKey = RSAUtil.string2PrivateKey(appPrivateKeyStr);
        //用APP私钥解密AES秘钥
        byte[] aesKeyBytes = RSAUtil.privateDecrypt(RSAUtil.base642Byte(encryptAesKeyStr), appPrivateKey);
        //用AES秘钥解密请求内容
        SecretKey aesKey = AESUtil.loadKeyAES(new String(aesKeyBytes));
        byte[] response = AESUtil.decryptAES(RSAUtil.base642Byte(encryptContent), aesKey);
        JSONObject result2 = new JSONObject();
        //!!!注意汉字转码
        result2.put("ak", new String(aesKeyBytes,"UTF-8"));
        //result2.put("ct", new String(request));
        result2.put("ct", new String(response,"UTF-8"));
        return result2.get("ct").toString();
    }
    //用app的私钥加密
    public static String encryptByappprivate(String str) throws Exception {
        //将Base64编码后的Server公钥转换成PublicKey对象
        PrivateKey apppivateKey = RSAUtil.string2PrivateKey(KeyUtil.APP_PRIVATE_KEY);
        //用app私钥加密
        byte[] encryptAesKey = RSAUtil.privateEncrypt(str.getBytes(), apppivateKey);
        JSONObject result = new JSONObject();
        result.put("content", RSAUtil.byte2Base64(encryptAesKey).replaceAll("\r\n", ""));
        return result.get("content").toString();
    }
    //用app的私钥加密
    public static String encryptByappprivate1(String str) throws Exception {
        //将Base64编码后的Server公钥转换成PublicKey对象
        PrivateKey apppivateKey = RSAUtil.string2PrivateKey(KeyUtil.APP_PRIVATE_KEY);
        //用app私钥加密
        byte[] encryptAesKey = RSAUtil.privateEncrypt(str.getBytes(), apppivateKey);
       /* JSONObject result = new JSONObject();
        result.put("content", RSAUtil.byte2Base64(encryptAesKey).replaceAll("\r\n", ""));*/
        return RSAUtil.byte2Base64(encryptAesKey).replaceAll("\r\n", "");
    }
    //用app的公钥解密
    public static String decryptByapppublic(String appPublicKeyStr,String str) throws  Exception{
        JSONObject result = JSONObject.fromObject(str);
        String encryptContent=(String)result.get("content");
        PublicKey appPublickey=RSAUtil.string2PublicKey(appPublicKeyStr);
        byte[] decryptcontent=RSAUtil.publicDecrypt(RSAUtil.base642Byte(encryptContent),appPublickey);
        JSONObject result2=new JSONObject();
        result2.put("content",new String(decryptcontent,"UTF-8"));
        return result2.get("content").toString();
    }
}
