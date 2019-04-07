package com.enjoyshop.data;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.activity.LoginActivity;
import com.enjoyshop.activity.RegActivity;
import com.enjoyshop.activity.RegSecondActivity;
import com.enjoyshop.bean.ShoppingCart;
import com.enjoyshop.data.dao.Address;
import com.enjoyshop.data.dao.AddressDao;
import com.enjoyshop.data.dao.User;
import com.enjoyshop.data.dao.UserDao;
import com.enjoyshop.utils.Base64Utils;
import com.enjoyshop.utils.HttpEncryptUtil;
import com.enjoyshop.utils.MD5Util;
import com.enjoyshop.utils.RSATest;
import com.enjoyshop.utils.RSAUtils;
import com.enjoyshop.utils.TestRSA;
import com.enjoyshop.utils.ToastUtils;

import org.apache.commons.codec.binary.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.greenrobot.greendao.annotation.Id;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import sun.misc.BASE64Decoder;

import static android.os.Build.VERSION_CODES.BASE;
import static java.lang.Thread.sleep;

/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     time   : 2018/09/03
 *     desc   : 数据库管理类
 *     version: 1.0
 * </pre>
 */

public class DataManager {
    private String ip="172.20.40.148";
    private String ip_ca="172.20.10.4";
    /**
     * 登陆
     * @param ph
     * @param pw
     */
    public boolean loginlogic(final String ph,final String pw) {

            boolean login=false;
            myThreadZ myThread0=new myThreadZ(ph,pw);
            myThread0.start();
            while (myThread0.is_log_success==-1){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(myThread0.is_log_success==1){
                return true;
            }
            return login;

        }
        private class myThreadZ extends Thread{
            String phone_temp;
            String passeword;
            int is_log_success=-1;
           public myThreadZ(String ph,String pw){
               phone_temp=ph;
               passeword=pw;
           }
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/Login");
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("ID", encryptBASE64(phone_temp)));
                    //params.add(new BasicNameValuePair("PW", passeword);
                    params.add(new BasicNameValuePair("PW", encryptbyServerpubkey(passeword)));
                    final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                    httppost.setEntity(entity);
                    HttpResponse httpResponse = httpclient.execute(httppost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity1 = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity1, "utf-8");
                        if(response.equals("true")) {
                            is_log_success = 1;
                        }
                        else{
                            is_log_success=0;
                        }
                    }
                    else{
                        is_log_success=0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



    /**
     * 添加用户
     */
    public boolean insertUser(final String ph,final String pw) {
        boolean is_reg=false;
        myThreadA myThread0=new myThreadA(ph,pw);
        myThread0.start();
        while (myThread0.is_Reg_success==-1){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(myThread0.is_Reg_success==1){
            return true;
        }
        return is_reg;
    }
    /**
     * 添加用户线程
     */
    private class myThreadA extends Thread{
        String phone_temp;
        String passeword;
        int is_Reg_success=-1;
        public myThreadA(String ph,String pw){
            phone_temp=ph;
            passeword=pw;
        }
        @Override
        public void run() {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/Adduser");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ID", encryptBASE64(phone_temp)));
                params.add(new BasicNameValuePair("PW", encryptbyServerpubkey(passeword)));
                //params.add(new BasicNameValuePair("PW", passeword));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("true")) {
                        is_Reg_success = 1;
                    }
                    else{
                        is_Reg_success=0;
                    }
                }
                else{
                    is_Reg_success=0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除用户
     *
     * @param id
     */
    public static void deleteUser(Long id) {
        EnjoyshopApplication.getDaoSession().getUserDao().deleteByKey(id);
    }

    /**
     * 更新数据
     *
     * @param user
     */
    public static void updateUser(User user) {
        EnjoyshopApplication.getDaoSession().getUserDao().update(user);
    }
    private class myThreadO extends Thread{
        int HasReg=-1;
        String phone_temp;
        public myThreadO(String ph){
            phone_temp=ph;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost=new HttpPost("http://"+ip+":8080/testtesthttp/Reg");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name",phone_temp));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse=httpclient.execute(httppost);
                if(httpResponse.getStatusLine().getStatusCode()==200) {
                    HttpEntity entity1=httpResponse.getEntity();
                    String response=EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("true")) {
                        HasReg = 1;
                    }
                    else{
                        HasReg=0;
                    }
                }
                else {
                    HasReg=0;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    /**
     * 查询是否已注册，是返回true，否返回false
     *
     * @return
     */
    public  boolean queryUser(final String phone) throws InterruptedException {

       myThreadO thread1 = new myThreadO(phone);
       thread1.start();
       while(thread1.HasReg==-1){
           sleep(100);
       }
       if(thread1.HasReg==1){
           return true;
       }
        /*return EnjoyshopApplication.getDaoSession().getUserDao().queryBuilder().where
                (UserDao.Properties.Phone.eq(phone)).list();*/
        return false;
    }


    /**
     * 获取数据库的商品列表
     */
    private class myThread3 extends Thread{
        String text_goods=null;
        int cur=1;
        int ps=10;
        public myThread3(int curpage,int pagesize){
            cur=curpage;
            ps=pagesize;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/queryGoods");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ID", cur+""));
                params.add(new BasicNameValuePair("PW",ps+""));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response!=null) {
                        text_goods = response;
                    }
                    else {
                        text_goods="";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    public Map<Integer,List<String>> getGoods(int curpage,int pagesize) throws InterruptedException {
        Map<Integer,List<String>> map=new HashMap<Integer,List<String>>();

        myThread3 t=new myThread3(curpage,pagesize);
        t.start();
        while(t.text_goods==null){
          sleep(100);
        }
        if(!t.text_goods.equals("")) {
            String[] A = t.text_goods.split("\n");
            for (int i_count = 0; i_count < 10; i_count++) {
                List<String> goods_list = new ArrayList<>();
                for (int j = 0; j < 7; j++) {
                    goods_list.add(A[i_count * 7 + j]);
                }
                map.put(i_count + 1, goods_list);
            }
        }
        return map;
    }


    /**
     *增加user=id的收货地址
     */
    private class myThread6 extends Thread{
        int recp=-1;
        Long addressId;
        Long userId;
        String name;
        String phone;
        String bigadd;
        String smalladd;
        String add;
        public myThread6(Long addressId,Long userId,String name,String phone,String bigadd,String smalladd,String add){
            this.addressId=addressId;
            this.userId=userId;
            this.name=name;
            this.phone=phone;
            this.bigadd=bigadd;
            this.smalladd=smalladd;
            this.add=add;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/addAddress");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("addressId", encryptbyServerpubkey(addressId+"")));
                params.add(new BasicNameValuePair("userId",encryptbyServerpubkey(userId+"")));
                params.add(new BasicNameValuePair("name",encryptbyServerpubkey(name)));
                params.add(new BasicNameValuePair("phone",encryptbyServerpubkey(phone)));
                params.add(new BasicNameValuePair("bigAddress",encryptbyServerpubkey(bigadd)));
                params.add(new BasicNameValuePair("smallAddress",encryptbyServerpubkey(smalladd)));
                params.add(new BasicNameValuePair("address",encryptbyServerpubkey(add)));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("true")) {
                        recp=1;
                    }
                    else{
                        recp=0;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };


    /**
     * 添加地址,每个地址后需加逗号
     *
     * @param address
     */
    public int insertAddress(Address address) throws InterruptedException {
        myThread6 m6=new myThread6(address.getAddressId(),address.getUserId(),address.getName(),address.getPhone(),address.getBigAddress(),address.getSmallAddress(),address.getAddress());
        m6.start();
        while (m6.recp==-1){
            sleep(100);
        }
        if(m6.recp==1) {
           // EnjoyshopApplication.getDaoSession().getAddressDao().insert(address);
            return 0;
        }
        return 1;
    }

    /**
     * 删除地址
     *
     * @param addressId
     */
    public int deleteAddress(Long addressId) throws InterruptedException {
        myThread5 thread5=new myThread5(addressId);
        thread5.start();
        while (thread5.address==null){
            sleep(100);
        }
        Long userId=EnjoyshopApplication.getInstance().getUser().getId();
        EnjoyshopApplication.getDaoSession().getAddressDao().deleteByKey(userId);
        return 0;
    }
    /**
     *删除address=id的收货地址
     */
    private class myThread5 extends Thread{
        String address=null;
        Long id;
        public myThread5(Long id){
            this.id=id;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/delAddress");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid",encryptbyServerpubkey(EnjoyshopApplication.getInstance().getUser().getId()+"")));
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(id+"")));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("true")) {
                        address="ok";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    /**
     * 修改地址
     *
     * @param address
     */
    public  void updateAddress(Address address) throws InterruptedException {
        deleteAddress(address.getAddressId());
        EnjoyshopApplication.getDaoSession().getAddressDao().update(address);
    }

    /**
     * 查询条件为Type=UserId的地址
     *
     * @return
     */
    public  List<Address> queryAddress(String userId) throws InterruptedException {
        myThread4 thread4=new myThread4(userId);
        thread4.start();
        while (thread4.address==null){
            sleep(100);
        }
          /*
        查询数据库返回的应该是对应userID的所有Address类实例的list，在这个list中每个Address实例用“;”隔开,每个Address实例的各个参数用“，”隔开
         */
        String address_return =thread4.address;
        List<Address> list_address = new ArrayList<Address>();
        if(!address_return.equals("")) {
            String[] temp_address_return = address_return.split(";");
            for (int i_count = 0; i_count < temp_address_return.length; i_count++) {
                boolean is_def = false;
                String[] param_return = temp_address_return[i_count].split(",");
                if (Long.parseLong(param_return[0]) ==1) {
                    is_def = true;
                }
                Address address = new Address(Long.parseLong(param_return[1]), Long.parseLong(param_return[0]), param_return[2], param_return[3], is_def, param_return[4], param_return[5], param_return[6]);
                list_address.add(address);
            }
        }
        return list_address;
       /* return EnjoyshopApplication.getDaoSession().getAddressDao().queryBuilder().where
                (AddressDao.Properties.UserId.eq(userId)).list();*/
    }
    /**
     *获取user=id的收货地址
     */
    private class myThread4 extends Thread{
        String address=null;
        String id="";
        public myThread4(String id){
            this.id=id;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/queryAddress");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(id)));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response!=null) {
                        address = response;
                        address=HttpEncryptUtil.appDecrypt(EnjoyshopApplication.getInstance().get_appprivatekey(),address);
                        System.out.print("======"+address);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    /**
     * 获取订单
     */
    public List<String> getOrders() throws InterruptedException {
        List<String> temp_list=new ArrayList<>();
        myThread7 mt=new myThread7(EnjoyshopApplication.getInstance().getUser().getId());
        mt.start();
        while (mt.orders==null){
            sleep(100);
        }
          /*
        查询数据库返回的应该是对应userID的所有订单实例的String，在这个String中每个实例用“;”隔开,每个Order实例的各个参数用“：”隔开,多个商品在同一个订单中时用“，”隔开
        形如：商品id：商品name：商品count，商品id：商品name：商品count；
         */
        String ordertotal=mt.orders;
        String[] ordersingle=ordertotal.split(";");
        temp_list=Arrays.asList(ordersingle);
        return temp_list;
    }
    /**
     * 获取订单
     */
    private class myThread7 extends Thread{
        String orders=null;
        Long id;
        public myThread7(Long id){
            this.id=id;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/queryOrder");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(id+"")));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response!=null) {
                        orders= response;
                        orders=HttpEncryptUtil.appDecrypt(EnjoyshopApplication.getInstance().get_appprivatekey(),orders);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    /**
     * 向购物车列表添加商品
     */
    public boolean add_shopcartlist(int id) throws InterruptedException {
        myThread_addshopcart m=new myThread_addshopcart(id);
        m.start();
        while (m.orders==null){
            sleep(100);
        }
        if(m.orders.equals("true")){
            return true;
        }
        return false;
    }
    private class myThread_addshopcart extends Thread{
        String orders=null;
        int id;
        public myThread_addshopcart(int id){
            this.id=id;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/Addshopcart");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(id+"")));
                params.add(new BasicNameValuePair("userid",encryptbyServerpubkey(EnjoyshopApplication.getInstance().getUser().getId()+"")));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    orders= response;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    /**
     * 从数据库加载购物车商品
     */
    /**
     * 获取订单
     */
    private class t_getShopcart extends Thread{
        String text_goods=null;
        public t_getShopcart(){
        super();
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/queryShopcart");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("ID", encryptbyServerpubkey(EnjoyshopApplication.getInstance().getUser().getId()+"")));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response!=null) {
                        text_goods = response;
                    }
                    else {
                        text_goods="";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    public Map<Integer,List<String>> get_shopcart() throws InterruptedException {
        Map<Integer,List<String>> map=new HashMap<Integer,List<String>>();
        t_getShopcart t=new t_getShopcart();
        t.start();
        while(t.text_goods==null){
            sleep(100);
        }
        if(!t.text_goods.equals("")) {
            String[] A = t.text_goods.split("\n");
            for (int i_count = 0; i_count<(A.length/7); i_count++) {
                List<String> goods_list = new ArrayList<>();
                for (int j = 0; j < 7; j++) {
                    goods_list.add(A[i_count * 7 + j]);
                }
                map.put(i_count + 1, goods_list);
            }
        }
        return map;
    }
    /**
     * 删除数据库中的购物车商品，删除的多个商品id之间用”，”隔开
     */
    public void delshopcart(List<ShoppingCart> list) throws InterruptedException {
        String sendstr="";
        for(int i=0;i<list.size();i++){
            sendstr=sendstr+list.get(i).getId()+",";
        }
        delshopcartone de=new delshopcartone(sendstr);
        de.start();
        while (de.resp==null){
            sleep(100);
        }
        if(de.resp.equals("ok")){
            return ;
        }
    }
    /**
     *删除商品id为cartid的商品
     */
    private class delshopcartone extends Thread{
        String resp=null;
        String oplist;
        public delshopcartone(String integerList){
            this.oplist=integerList;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/delshopcart");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(oplist)));
                params.add(new BasicNameValuePair("id", encryptbyServerpubkey(EnjoyshopApplication.getInstance().getUser().getId()+"")));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("true")) {
                        resp="ok";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
    /**
 * 提交订单请求
 */
public boolean order_request(String str){
    Orderrequest orderrequest=new Orderrequest(str);
    orderrequest.start();
    while(orderrequest.resp==null){
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    if(orderrequest.resp.equals("ok")){
       return true;
    }
    return false;
}
    private class Orderrequest extends Thread{
        String resp=null;
        String oplist;
        public Orderrequest(String integerList){
            this.oplist=integerList;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/orderrequest");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(oplist)));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("ok")) {
                        resp="ok";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };
/**
 * 双签名付款
 */
public boolean order_pay(String a) {
    Payrequest payrequest = new Payrequest(a);
    payrequest.start();
    while (payrequest.resp == null) {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    if(payrequest.resp.equals("ok")){
        return true;
    }
    return false;
}
    private class Payrequest extends Thread{
        String resp=null;
        String oplist;
        public Payrequest(String integerList){
            this.oplist=integerList;
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/PayRequest");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(oplist)));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("ok")) {
                        resp="ok";
                    }
                    else {
                        resp="no";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };

    /**
     * server公钥加密
     */
    private String encryptbyServerpubkey(String str) throws Exception {
        return HttpEncryptUtil.appEncrypt(EnjoyshopApplication.getInstance().get_apppublickey(),str);
    }
    /**
     * 用Base64加密用户账号
     */
    public static String encryptBASE64(String key) {
     byte[] bt = key.getBytes();
     return new String(new Base64().encode(bt));
    }
    /**
     * 向服务器发送注册CA的请求获得客户端证书//服务器证书，加密方式：Client->SERVER:不加密消息请求，SERVER->Client：不加密传递前四者，拿到后
     * 验证证书和公钥，正确则存储返回true，否则返回false
     */
    public boolean get_init_cer(){
        getinitcer fet=new getinitcer();
        fet.start();
        while(fet.a!=null) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(fet.a!=null){
            return true;
        }
        return false;
    }
    private class getinitcer extends Thread{
        String a=null;
        public getinitcer(){
            super();
        }
        @Override
        public void run(){
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip+":8080/testtesthttp/cerRequest");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", encryptbyServerpubkey(EnjoyshopApplication.getInstance().getUser().getId()+"")));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    a="yes";
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response!=null&&!response.equals("")) {
                        BASE64Decoder base64Decoder=new BASE64Decoder();
                        byte[] bytes=base64Decoder.decodeBuffer(response);

                        CertificateFactory cf = null;
                        try {
                            cf = CertificateFactory.getInstance("X.509");
                        } catch (CertificateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        X509Certificate cert = null;
                        try {
                            cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(bytes));
                        } catch (CertificateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 向CA发送获取私钥的请求，得到私钥，加密方式为Client->CA:用CA公钥加密AES密钥发送请求，CA用该AES密钥加密私钥发挥客户端，客户用AES密钥解密
     */
    public boolean  get_appprikey(){
        getappprikey a=new getappprikey();
        a.start();
        return true;
    }
    private  class getappprikey extends  Thread{
        String resp=null;
        public getappprikey(){
            super();
        }
        @Override
        public void run() {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://"+ip_ca+":8080/ca/connectServlet");
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", HttpEncryptUtil.appEncryptca(EnjoyshopApplication.getInstance().get_apppublickey(),EnjoyshopApplication.getInstance().getUser().getId()+"")));
                final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                httppost.setEntity(entity);
                HttpResponse httpResponse = httpclient.execute(httppost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity1, "utf-8");
                    if(response.equals("ok")) {
                        response=HttpEncryptUtil.appDecrypt(EnjoyshopApplication.getInstance().get_appprivatekey(),response);

                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
