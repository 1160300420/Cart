package com.enjoyshop.activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.adapter.GoodsOrderAdapter;
import com.enjoyshop.bean.Charge;
import com.enjoyshop.bean.OrderEle;
import com.enjoyshop.bean.ShoppingCart;
import com.enjoyshop.contants.Contants;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.data.DataManager;
import com.enjoyshop.msg.CreateOrderRespMsg;
import com.enjoyshop.msg.LoginRespMsg;
import com.enjoyshop.utils.CartShopProvider;
import com.enjoyshop.utils.HttpEncryptUtil;
import com.enjoyshop.utils.KeyUtil;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.MD5Util;
import com.enjoyshop.utils.RSAUtil;
import com.enjoyshop.utils.RSAUtils;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.FullyLinearLayoutManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import static java.lang.Thread.sleep;


/**
 * Created by 高磊华
 * Time  2017/8/9
 * Describe: 订单确认
 */

public class CreateOrderActivity extends BaseActivity implements View.OnClickListener {

    //微信支付渠道
    private static final String CHANNEL_WECHAT = "wx";
    //支付支付渠道
    private static final String CHANNEL_ALIPAY = "alipay";
    //百度支付渠道
    private static final String CHANNEL_BFB    = "bfb";

    @BindView(R.id.txt_order)
    TextView       txtOrder;
    @BindView(R.id.recycler_view)
    RecyclerView   mRecyclerView;
    @BindView(R.id.rl_alipay)
    RelativeLayout mLayoutAlipay;
    @BindView(R.id.rl_wechat)
    RelativeLayout mLayoutWechat;
    @BindView(R.id.rl_bd)
    RelativeLayout mLayoutBd;
    @BindView(R.id.rb_alipay)
    View           mRbAlipay;
    @BindView(R.id.rb_webchat)
    View           mRbWechat;
    @BindView(R.id.rb_bd)
    View           mRbBd;
    @BindView(R.id.btn_createOrder)
    Button         mBtnCreateOrder;
    @BindView(R.id.txt_total)
    TextView       mTxtTotal;
    /**
     * test for modify
     */
    @BindView(R.id.order_client)
    EditText order_client;
    @BindView(R.id.order_address)
    EditText order_address;
    /**
     *
     */
    private CartShopProvider  cartProvider;
    private GoodsOrderAdapter mAdapter;
    private String            orderNum;
    private String payChannel = CHANNEL_ALIPAY;           //默认为支付宝支付
    private float amount;
    private String o_name;
    private String o_phone;
    private String o_client;
    private String o_address;


    private HashMap<String, RelativeLayout> channels = new HashMap<>();

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_create_order;
    }

    @Override
    protected void init() {
        showData();
        initView();
    }


    private void initView() {

        channels.put(CHANNEL_ALIPAY, mLayoutAlipay);
        channels.put(CHANNEL_WECHAT, mLayoutWechat);
        channels.put(CHANNEL_BFB, mLayoutBd);

        mLayoutAlipay.setOnClickListener(this);
        mLayoutWechat.setOnClickListener(this);
        mLayoutBd.setOnClickListener(this);
        Intent intent=getIntent();
        o_name= intent.getStringExtra("name");
        o_phone="联系方式："+intent.getStringExtra("phone");
        o_client=o_name+"   "+o_phone;
        o_address=intent.getStringExtra("address");
        order_client.setText(o_client);
        order_address.setText(o_address);

        amount = mAdapter.getTotalPrice();
        mTxtTotal.setText("应付款： ￥" + amount);
    }


    public void showData() {

        cartProvider = new CartShopProvider(this);
        /**
         * test for modify
         */
        mAdapter = new GoodsOrderAdapter(EnjoyshopApplication.getInstance().getListshopcart());
        /**
         *
         */


       // mAdapter = new GoodsOrderAdapter(cartProvider.getAll());

        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        //recyclerView外面嵌套ScrollView.数据显示不全
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onClick(View v) {
        selectPayChannle(v.getTag().toString());
    }

    @OnClick(R.id.rl_addr)
    public void chooseAddress(View view) {
        Intent intent = new Intent(CreateOrderActivity.this, AddressListActivity.class);
        startActivityForResult(intent, Contants.REQUEST_CHOOSE_ADDRESS);
    }


    /**
     * 当前的支付渠道 以及三个支付渠道互斥 的功能
     */
    public void selectPayChannle(String paychannel) {

        for (Map.Entry<String, RelativeLayout> entry : channels.entrySet()) {
            payChannel = paychannel;
            RelativeLayout rb = entry.getValue();
            if (entry.getKey().equals(payChannel)) {
                int childCount = rb.getChildCount();
                LogUtil.e("测试子控件", childCount + "", true);

                View viewCheckBox = rb.getChildAt(2);      //这个是类似checkBox的控件
                viewCheckBox.setBackgroundResource(R.drawable.icon_check_true);
            } else {
                View viewCheckBox = rb.getChildAt(2);      //这个是类似checkBox的控件
                viewCheckBox.setBackgroundResource(R.drawable.icon_check_false);
            }

        }
    }


    @OnClick(R.id.btn_createOrder)
    public void createNewOrder(View view) {
        postNewOrder();     //提交订单
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //跳转我的订单中显示


    }


    private void postNewOrder() {
        /**
         * 构造PI，OI
         */
        //OI:userid;shopcartid,shopcart;payprice
        String OI=EnjoyshopApplication.getInstance().getUser().getId()+";";
        List<ShoppingCart> order_list=EnjoyshopApplication.getInstance().get_list_shop();//获取订单列表
        for(int i=0;i<order_list.size();i++){
            OI=OI+order_list.get(i).getId()+","+order_list.get(i).getCount()+"%";
        }
        OI=OI+";"+mAdapter.getTotalPrice();
        //PI:customerid;payamount;salerid
        String PI=EnjoyshopApplication.getInstance().getUser().getId()+";"+mAdapter.getTotalPrice()+";"+EnjoyshopApplication.getInstance().get_salerid();
        //分别对pi和oi进行hash
        String PIMD=MD5Util.encodebyMD5(PI);
        String OIMD=MD5Util.encodebyMD5(OI);
        String POMD=MD5Util.encodebyMD5(PIMD+OIMD);
        System.out.println("POMD"+POMD);
        String DS="";
         try {
            DS=HttpEncryptUtil.encryptByappprivate1(POMD);
            // DS=RSAUtil.byte2Base64(RSAUtils.encryptData(POMD.getBytes(),RSAUtil.string2PrivateKey(KeyUtil.SERVER_PRIVATE_KEY)));
             System.out.print("DS:"+DS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(new DataManager().order_request(DS+"//"+OI+"//"+PIMD)){
            if(new DataManager().order_pay(DS+"//"+PI+"//"+OIMD)){
                ToastUtils.showSafeToast(this,"交易成功");
            }
            else{
                ToastUtils.showSafeToast(this,"交易失败");
            }
        }





        /*final List<ShoppingCart> carts = mAdapter.getData();

        List<WareItem> items = new ArrayList<>(carts.size());
        for (ShoppingCart c : carts) {
            // c.getPrice()  是double类型    而接口总价为int 类型,需要转化

            WareItem item = new WareItem(Long.parseLong(String.valueOf(c.getId())), (int) Math
                    .floor(c.getPrice()));
            items.add(item);
        }

        String item_json = new Gson().toJson(items);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", EnjoyshopApplication.getInstance().getUser().getId() + "");
        params.put("item_json", item_json);
        params.put("pay_channel", payChannel);
        params.put("amount", (int) amount + "");
        params.put("addr_id", 1 + "");

        mBtnCreateOrder.setEnabled(false);

        OkHttpUtils.post().url(HttpContants.ORDER_CREATE)
                .params(params).build()
                .execute(new Callback<CreateOrderRespMsg>() {
                    @Override
                    public CreateOrderRespMsg parseNetworkResponse(Response response, int id)
                            throws Exception {

                        LogUtil.e("支付", "AAAAAAAAAAA", true);
                        String string = response.body().string();
                        CreateOrderRespMsg msg = new Gson().fromJson(string, new
                                TypeToken<LoginRespMsg>() {
                                }.getType());
                        return msg;

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mBtnCreateOrder.setEnabled(true);
                        LogUtil.e("支付", e.toString(), true);
                    }

                    @Override
                    public void onResponse(CreateOrderRespMsg response, int id) {

                        mBtnCreateOrder.setEnabled(true);

                        orderNum = response.getData().getOrderNum();
                        Charge charge = response.getData().getCharge();
                    }
                });
*/
    }

    /**
     * 因为接口文档要求,商品列表为json格式,所以这里需要定义一个内部类
     */
  /*  class WareItem {

        private Long ware_id;
        private int  amount;

        public WareItem(Long ware_id, int amount) {
            this.ware_id = ware_id;
            this.amount = amount;
        }

        public Long getWare_id() {
            return ware_id;
        }

        public void setWare_id(Long ware_id) {
            this.ware_id = ware_id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }*/

}
