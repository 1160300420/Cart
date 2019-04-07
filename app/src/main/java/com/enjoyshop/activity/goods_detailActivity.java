package com.enjoyshop.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.telecom.TelecomManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.enjoyshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class goods_detailActivity extends Activity {
    private ImageView imageview;
    private TextView price;
    private TextView name;
    private TextView sale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_detail);
        imageview = (ImageView) this.findViewById(R.id.imageView);
        price=(TextView)this.findViewById(R.id.details_price);
        name=(TextView)this.findViewById(R.id.details_name);
        sale=(TextView)this.findViewById(R.id.details_sale);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy( new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        String strURL = getIntent().getStringExtra("imgurl");
        String strname=getIntent().getStringExtra("name");
        double strprice=getIntent().getDoubleExtra("price",0);
        double strsale=getIntent().getDoubleExtra("sale",0);
        Bitmap bitmap = null;
        try {
            bitmap = getBitmap(strURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageview.setImageBitmap(bitmap);
        price.setText("商品价格："+strprice);
        name.setText("商品详情："+strname);
        sale.setText("商品库存："+strsale);
    }
    public Bitmap getBitmap(String path) throws IOException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap; }
        } catch (IOException e) { // TODO Auto-generated catch block
             e.printStackTrace();
        } return null;
        }
}
