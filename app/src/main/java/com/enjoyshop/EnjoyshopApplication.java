package com.enjoyshop;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.support.annotation.NonNull;

import com.enjoyshop.bean.ShoppingCart;
import com.enjoyshop.bean.User;
import com.enjoyshop.data.DataManager;
import com.enjoyshop.data.dao.DaoMaster;
import com.enjoyshop.data.dao.DaoSession;
import com.enjoyshop.service.LocationService;
import com.enjoyshop.utils.UserLocalData;
import com.enjoyshop.utils.Utils;
import com.mob.MobApplication;
import com.mob.MobSDK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     time   : 2017/08/05
 *     desc   : 整个app的管理
 *     version: 1.0
 * </pre>
 */


public class EnjoyshopApplication extends MobApplication {

    //    mob信息    app key:201f8a7a91c30      App Secret:  c63ec5c1eeac1f873ec78c1365120431
    //百度地图的 ak   zbqExff1uz8XyUVn5GbyylomCa0rOkmP

    private User user;
    public LocationService locationService;
    public  Vibrator mVibrator;

    private        DaoMaster.DevOpenHelper mHelper;
    private        SQLiteDatabase          db;
    private        DaoMaster               mDaoMaster;
    private static DaoSession              mDaoSession;
    private List<List<ShoppingCart>> total_order=new ArrayList<List<ShoppingCart>>();//所有订单
    private List<ShoppingCart> list_shopcart;//每次订单列表
    private List<String> ordertotal;
    private String salerid="11111111111";//服务器在银行的账户，用于支付消息的构造
    private List<ShoppingCart> list_shop=new ArrayList<ShoppingCart>();//购物车

    //服务器公私钥
    public String serverpublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqqKPH/L0AZyn1fJ9xK2ol2nHY5jPu8qw7COwFukkRdr2j0oNJmD8vCTmxgzKWV0CkihiJ7Y0OekrGc78JL5tpL2SqeZTLa2bCJZJaTM3KFOXYb82nc8Xbr2caDnf7mgjyt0AALHG/YfYwd7hifZRB6Ct89uBTn6W5x/7oxGT6D1C8siXKV+99AZPMv2HobglWyquyjIL5TZOhYmCMzFUPMOiXzzGYXMZj2gmfUFXMf/2jitMPGg3zQPJxPSYunjoE1fMInk1obEhEfU8n2YxT5ZbGMWZGjt4hZwF+FJJLV+WOantfUJ4rMBB8qxgQtkT+VzddfLCEoyy4Rl50fvjzwIDAQAB";
    //public String appprivateKey="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCqoo8f8vQBnKfV8n3EraiXacdjmM+7yrDsI7AW6SRF2vaPSg0mYPy8JObGDMpZXQKSKGIntjQ56SsZzvwkvm2kvZKp5lMtrZsIlklpMzcoU5dhvzadzxduvZxoOd/uaCPK3QAAscb9h9jB3uGJ9lEHoK3z24FOfpbnH/ujEZPoPULyyJcpX730Bk8y/YehuCVbKq7KMgvlNk6FiYIzMVQ8w6JfPMZhcxmPaCZ9QVcx//aOK0w8aDfNA8nE9Ji6eOgTV8wieTWhsSER9TyfZjFPllsYxZkaO3iFnAX4UkktX5Y5qe19QniswEHyrGBC2RP5XN118sISjLLhGXnR++PPAgMBAAECggEAAP6dkvQZlADTwZ1+Oi1A9FD7hosXeuK9kULL/fYx7e5OzZsC5JxgHMCiT7k3XLn8D9oIaG7ZcxT22VmpgpVRkkpAlpjvFy8R3kTx/Jj901BZa4pvyQ+x9UVJqhncQkl9G+uZ2mcu379w9gBUlDdJVaAMI4W+BTUbsBExqEur7wiZ8S3XK3rDltKINIguPRjwVTTuepzrTNd3k7WKPD8za/OV87bGEbaGLLo8KSKzm7bJnyMSbUwkjKA+WezNidUatB1fqaXDnlVYokCdfBD7xypbbVO+HoBNNBgAwT5p3dm3hbgYsb9WNZLJXREMaNtp+AhWHGFj4qeYRl8dfGO7oQKBgQDYXTChQhKdo80nFWMtFvh1yw75uma0oOpnHEiIfYh1ekcTBLlgsGTbg60t5Klb2lF1SdfhNXb+4WQNmSMlQYoKwakiRn2JyKbk+zs/j6q1r1PV1QAP5LJ28J/FjG5bEyCXQdE9nVhTps1EX2Fuy6I9pKCQKuv86NPOsX7IMat4VwKBgQDJ5NFcORrRgxLwcx/eYGjyeyf7EDKacVpd6SzobakXnvaX3WCxw2wjvbex+zL1s+XnLmp+4l87FfuP0+yd78pRHASXwDh6MNqs8bN2ksT4LOb7bImXygcxkX8Iog9qkydKZ1kUgLtDPZMzSUyic0+/6qVro9JR3aw0oJly9p4lSQKBgQCli6gJumRD+XCe1t5rQYgZmKR8rwKmcfjnq9xTkrk2Kbj39EVilZSV4MpAsxRiE0kAVN+4kQ/bNNk5DlK1zs+wKz0d3JFxOvV3fkJ2/5W+LcgXdEH35yQlnTaiEDDfvmLRWKqgWiOa3aVxCwmhnG0mfS/dHvoxKHPnUiePRXHNQQKBgQC7lZrgsT41xC9osc6+c52PDtbK8vXRgdiQwQI0ww8FH3HHEK2y/PwRCUkQWXGz0P6fmgTg97u7zmT58dI7vHyieAHcbYEMJzBG2BwC48OXQ0EqAmKlYdTlPWZmwwzH3Qn4m6Ws4x8bDq8iS8ykc7d5fa9NH91eqzRBgaaRpoqx4QKBgEzWhwVHhTxdSBzqh6JaTKVUOrt1CwsbQOSlOy/Y8k/TJFJaQh+/yGKSBpkGLfWkY5HVra+nhAgWuCB2X301DpSMCQtTiABYvjGNNysrkm40xQOuTOmO6OTqDfyVQZmi/xUXeztiT2vKjz0em+tButyg7OP7zKzYwqW3KhAxZ94t";
    //public String apppublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqqKPH/L0AZyn1fJ9xK2ol2nHY5jPu8qw7COwFukkRdr2j0oNJmD8vCTmxgzKWV0CkihiJ7Y0OekrGc78JL5tpL2SqeZTLa2bCJZJaTM3KFOXYb82nc8Xbr2caDnf7mgjyt0AALHG/YfYwd7hifZRB6Ct89uBTn6W5x/7oxGT6D1C8siXKV+99AZPMv2HobglWyquyjIL5TZOhYmCMzFUPMOiXzzGYXMZj2gmfUFXMf/2jitMPGg3zQPJxPSYunjoE1fMInk1obEhEfU8n2YxT5ZbGMWZGjt4hZwF+FJJLV+WOantfUJ4rMBB8qxgQtkT+VzddfLCEoyy4Rl50fvjzwIDAQAB";


    //整个app的上下文
    public static Context sContext;

    private static EnjoyshopApplication mInstance;

    public static EnjoyshopApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // 通过代码注册你的AppKey和AppSecret
        MobSDK.init(this, "201f8a7a91c30", "c63ec5c1eeac1f873ec78c1365120431");

        sContext = getApplicationContext();
        initUser();
        Utils.init(this);

        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        setDatabase();

    }


    public void initUser() {
        this.user = UserLocalData.getUser(this);
    }


    public User getUser() {
        return user;
    }

    public void putUser(User user, String token) {
        this.user = user;
        UserLocalData.putUser(this, user);
        UserLocalData.putToken(this, token);
    }

    public void clearUser() {
        this.user = null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);
    }


    public String getToken() {
        return UserLocalData.getToken(this);
    }

    private Intent intent;

    public void putIntent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

    public void jumpToTargetActivity(Context context) {
        context.startActivity(intent);
        this.intent = null;
    }


    public static EnjoyshopApplication getApplication() {
        return mInstance;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "shop-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
    public void addShopCart(ShoppingCart c){
        list_shopcart.add(c);
    }
    public List<ShoppingCart> getListshopcart(){
        Iterator<ShoppingCart> it = list_shop.iterator();
        while(it.hasNext()){
            ShoppingCart temp_cart = it.next();
            if(temp_cart.getCount()==0){
                it.remove();
            }
        }
        return  list_shopcart;
    }
    public void setList_shopcart(List<ShoppingCart> temp_list){
        this.list_shopcart=temp_list;
        total_order.add(this.list_shopcart);
    }
    public void list_shop_add(ShoppingCart c){
        list_shop.add(c);
        Iterator<ShoppingCart> it = list_shop.iterator();
        while(it.hasNext()){
            ShoppingCart temp_cart = it.next();
            if(temp_cart.getCount()==0){
                it.remove();
            }
        }
    }
    public List<ShoppingCart> get_list_shop(){
        List<ShoppingCart> list_list=new ArrayList<>();
        Iterator<ShoppingCart> it = list_shopcart.iterator();
        int i=0;
        while(it.hasNext()){
            ShoppingCart temp_cart = it.next();
            if(temp_cart.getCount()==0){
                it.remove();
            }else{
                list_list.add(list_shopcart.get(i));
            }
            i++;
        }
        return list_list;
    }
    public List<String> getTotalShop() throws InterruptedException {
        this.ordertotal = new DataManager().getOrders();
        return this.ordertotal;
    }
    public String get_serverpublickey(){
        return this.serverpublicKey;
    }

    public void castart(){
        new DataManager().get_appprikey();
    }
    public String get_apppublickey(){
        return this.apppublicKey;
    }
    public String get_appprivatekey(){
        return this.appprivateKey;
    }
    public String get_salerid(){
        return this.salerid;
    }
    public void setApppublicKey(String a){
        this.apppublicKey=a;
    }
    public void setAppprivateKey(String ab){
        this.appprivateKey=ab;
    }
    public void setServerpublicKey(String a){
        this.serverpublicKey=a;
    }
    public String appprivateKey="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCmp/eqr9b1/Su/J+u1N3EQ84+Q\n" +
            "Q7inafuwNYyZQf5KTl+aTqcGQfcqxgj7E1XvPfvc4C93ycXc77GdP6gP1UkPhLEQiBoQGfQa/nEs\n" +
            "TxD+GMu0VoMy1zq9gGH/NIS/9B1o7Mhe6ftTOiuoEfCp3ZcUCWB0NfkQmPyurnH7vaZn3U9Wf2H7\n" +
            "u2aB2GS+5fpuw9KDpZ++vhiTnvPZBbbyQT+FH/1l9X7LhJ737/tYwIJvsOVZAN3fUcVV3JQVm0dn\n" +
            "OSq4KklI9U99YDVApiXghe5xYigwqEuN/RcBam5Ris4RcIhL2tK/4sh2KITfYqWnwlmd+pwuKGGZ\n" +
            "X9/VrDHK/KiPAgMBAAECggEAZPeUY/O+Xy/wBhLQiey58pQtITD2OW9LTflTYjKKNkh/QURviqoC\n" +
            "bgamwD55rMU3xxyHhaJX3r/VMrizQExuxBkXETXz3Fds/cGznS2V1Ov+1hYnzHB7JP4X4P7XglgK\n" +
            "TVxPFLnbSxQOCPg9fxxCAKChsCZW7AhT5hzH78V5gKioJjcWVtpBU+Y2cfrXkWyXPmFsNmvVrG7+\n" +
            "6w95uqyu5osDMonGB56t1TAHdw+agx8NZzUpdaSQM6kSJjCSiY7N/hLAQBGlSDLdZBy561tJqfeA\n" +
            "NUxcPICAVceWZJkFQmo4ReKYh5somCj9xtovGKctT0J274CyINJ5UhK8Vx4WoQKBgQDc2j/fga60\n" +
            "8dqwLcdCQ9u57vMWJ6czIsFZrQwpSg6AKDD4KLRdG3vGJJldqgCnMEXfkKrPuef1DUA9MIeor51P\n" +
            "M6b0LH9DopuPhUiSZyAIoNBoT8HHAyKFB2uWD3GKYN/VInuYV7huxOKqaF2mu9vuoT/1biG+Yu3Z\n" +
            "FUQCAeimvwKBgQDBLbPW2Yv/1AqccgI7CCWYS0tQlqGgqcLNz1Ireq0Fh7738oO80dJUe+XF7Xoq\n" +
            "6LOI6UKZRCYOqy9/4v1LO8ZjcHGFsdNkRVUTPmKtBntkyoiKkktxurWOTlsRxOdoC21vCuUvrUdb\n" +
            "ShbEb0USZozRCUcKAQAR41N/DYuMfujCMQKBgAPmInyKqI/vOSIlHMBxvD5TFp2Mg6omce1oXa+Y\n" +
            "7BqdGwg2h8ChDJVZ7g82Qh6xfnpM7ocOTCcKXdj2s15qA0fzrH3hDzodc8ub2cUj5u/gDu3Ygp5F\n" +
            "0uUwVVjn2uRLg4gavL3axpnGBg5mG0knReG3nQ5zLEtVKcfMVVAHwpi3AoGABOhlVkMGpIQ9lMXs\n" +
            "upU1Z04aZry89Hj7owDuZH5kYOyW9HOr1yM5hb30E3g6D1cEIKlUcNJjaSH+PfCFYODu5F3Z9HyV\n" +
            "E0oTpD6EgULV5bSjjWm0EvqcXDYvaB8pheIscJLFSdXMJ0yVkX4GMjZ6w2DJ9j4aQ1oUJ/kvH22l\n" +
            "+jECgYBWAm6EA81MEqoEpUt3mH7KuEfNxNCaXZFhS1p3wHSoaciynW7CWHyELLYPoFdQ2yKnZt8D\n" +
            "7Znaos0DY0wkOO/OKFz/QsPUSAbCS4VjCFmA0sew2zT6EwWVhKMfhgHIC4ffgipduht2CXko4Xnw\n" +
            "/hs9e4QbxUnFfGP/OTCk1TVskg==";
    public String apppublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApqf3qq/W9f0rvyfrtTdxEPOPkEO4p2n7\n" +
            "sDWMmUH+Sk5fmk6nBkH3KsYI+xNV7z373OAvd8nF3O+xnT+oD9VJD4SxEIgaEBn0Gv5xLE8Q/hjL\n" +
            "tFaDMtc6vYBh/zSEv/QdaOzIXun7UzorqBHwqd2XFAlgdDX5EJj8rq5x+72mZ91PVn9h+7tmgdhk\n" +
            "vuX6bsPSg6Wfvr4Yk57z2QW28kE/hR/9ZfV+y4Se9+/7WMCCb7DlWQDd31HFVdyUFZtHZzkquCpJ\n" +
            "SPVPfWA1QKYl4IXucWIoMKhLjf0XAWpuUYrOEXCIS9rSv+LIdiiE32Klp8JZnfqcLihhmV/f1awx\n" +
            "yvyojwIDAQAB";
}
