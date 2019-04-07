package com.enjoyshop.activity;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.adapter.AddressListAdapter;
import com.enjoyshop.adapter.OrderListAdapter;
import com.enjoyshop.bean.ShoppingCart;
import com.enjoyshop.data.DataManager;
import com.enjoyshop.data.dao.Address;
import com.enjoyshop.utils.PreferencesUtils;
import com.enjoyshop.widget.EnjoyshopToolBar;

import java.util.List;

import butterknife.BindView;

/**
 * Created by 高磊华
 * Time  2017/8/21
 * Describe: 我的订单
 */

public class MyOrdersActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @BindView(R.id.recycler1_view)
    RecyclerView mRecyclerview;

    private OrderListAdapter mAdapter;
    //private List<List<ShoppingCart>> myOrderList;
    private List<String> myOrderList;
    @Override
    protected void init() {
        initToolBar();
        initView();
    }


    @Override
    protected int getContentResourseId() {
        return R.layout.activity_myorder;
    }

    private void initToolBar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOrdersActivity.this.finish();

            }
        });
    }
    /**
     * 初始化地址信息
     * 商业项目这里是请求接口
     */
    private void initOrder() throws InterruptedException {
        myOrderList=EnjoyshopApplication.getInstance().getTotalShop();
       // myOrderList=new DataManager().getOrders();
        if (myOrderList != null && myOrderList.size() > 0) {
            for (int i = 0; i < myOrderList.size(); i++) {
                mAdapter.setNewData(myOrderList);
            }
        }else {
            myOrderList.clear();
            mAdapter.setNewData(myOrderList);
        }
    }

    private void initView() {
        if (mAdapter == null) {
            mAdapter = new OrderListAdapter(myOrderList);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
            mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration
                    .HORIZONTAL));

            mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    Address address = (Address)adapter.getData().get(position);
                        switch (view.getId()) {
                            case R.id.txt_del://确认收货，发给服务器确认消息，服务器付款给银行

                                break;
                            default:
                                break;
                        }
                    }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initOrder();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
