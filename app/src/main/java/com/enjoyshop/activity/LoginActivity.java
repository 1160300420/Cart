package com.enjoyshop.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.bean.MessageEvent;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.data.DataManager;
import com.enjoyshop.data.dao.User;
import com.enjoyshop.utils.StringUtils;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Created by 高磊华
 * Time  2017/8/9
 * Describe: 登录界面
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.etxt_phone)
    ClearEditText    mEtxtPhone;
    @BindView(R.id.etxt_pwd)
    ClearEditText    mEtxtPwd;
    @BindView(R.id.txt_toReg)
    TextView         mTxtToReg;
    String phone=null;
    String pwd=null;
    final String imageUrl = "https://timgsa.baidu" +
            ".com/timg?image&quality=80&size=b9999_10000&sec=1535017475541&di" +
            "=a5e08ea47bc24083efd75c547b8fc083&imgtype=0&src=http%3A%2F%2Fbos.pgzs" +
            ".com%2Frbpiczy%2FWallpaper%2F2013%2F10%2F8%2F8ad17e66b69046af812665a0e24ce862.jpg";
    private final int LOG_SUCCESS=1;
    @Override
    protected void init() {
        initToolBar();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_login;
    }

    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });
    }


    @OnClick({R.id.btn_login, R.id.txt_toReg})
    public void viewclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();   //登录
                EnjoyshopApplication.getInstance().initUser();
                break;
            case R.id.txt_toReg:
                Intent intent = new Intent(this, RegActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 登录
     * 注意注意.商业项目是直接请求登录接口.这次是对数据库进行操作
     */
    private void login() {

       phone = mEtxtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showSafeToast(LoginActivity.this, "请输入手机号码");
            return;
        }

        if (!StringUtils.isMobileNum(phone)) {
            ToastUtils.showSafeToast(LoginActivity.this, "请核对手机号码");
            return;
        }

        pwd = mEtxtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showSafeToast(LoginActivity.this, "请输入密码");
            return;
        }
        if(new DataManager().loginlogic(phone, pwd)){
            ToastUtils.showSafeToast(LoginActivity.this, "登陆成功");
            EnjoyshopApplication application = EnjoyshopApplication.getInstance();
            com.enjoyshop.bean.User user = new com.enjoyshop.bean.User();
            user.setMobi(phone);
            user.setUsername(phone);
            user.setId(Long.parseLong(phone));
            user.setLogo_url(imageUrl);
            application.putUser(user, "12345678asfghdssa");
            new DataManager().get_init_cer();
            finish();
        }
        else {
            ToastUtils.showSafeToast(LoginActivity.this, "用户名或密码错误");
        }

    }
        /**
         * 链接数据库
         */

        @Override
        public void onBackPressed () {
            super.onBackPressed();
            finish();
        }

}
