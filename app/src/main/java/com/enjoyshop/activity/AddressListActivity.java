package com.enjoyshop.activity;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.adapter.AddressListAdapter;
import com.enjoyshop.data.DataManager;
import com.enjoyshop.data.dao.Address;
import com.enjoyshop.utils.PreferencesUtils;
import com.enjoyshop.widget.EnjoyshopToolBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by 高磊华
 * Time  2017/8/10
 * Describe: 收货地址
 */

public class AddressListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerview;

    private AddressListAdapter mAdapter;
    private List<Address> mAddressDataList;
    /**
     * 默认设置的地址
     */
    private int isDefaultPosition = 0;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void init() {
        initToolbar();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initAddress();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isDefaultPosition = PreferencesUtils.getInt(this, "isDefaultPosition", 0);
    }


    private void initView() {
        if (mAdapter == null) {
            mAdapter = new AddressListAdapter(mAddressDataList);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
            mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration
                    .HORIZONTAL));

                mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    Address address = (Address)adapter.getData().get(position);
                    Intent intent=getIntent();
                    if(intent.getIntExtra("tag",0)!=1) {
                        switch (view.getId()) {
                            case R.id.txt_edit://修改
                                updateAddress(address);
                                break;
                            case R.id.txt_del://删除
                                try {
                                    delAddress(address);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.cb_is_defualt://选定默认
                                try {
                                    chooseDefult(mAddressDataList, position);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    else{
                        switch (view.getId()) {
                            case R.id.txt_edit://修改
                                break;
                            case R.id.txt_del://删除
                                break;
                            case R.id.cb_is_defualt://选定默认
                                Intent intent1=new Intent(AddressListActivity.this,CreateOrderActivity.class);
                                intent1.putExtra("name",address.getName());
                                intent1.putExtra("phone",address.getPhone());
                                intent1.putExtra("address",address.getAddress());
                                startActivity(intent1);
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    }

    /**
     * 初始化地址信息
     * 商业项目这里是请求接口
     */
    private void initAddress() throws InterruptedException {

        String userId = EnjoyshopApplication.getInstance().getUser().getUsername();
        try {
            mAddressDataList =new DataManager().queryAddress(userId);
            /**
             * test for default modify
             */
            if(mAddressDataList.size()>=1){
                for(int i = mAddressDataList.size() - 1; i >= 0; i--) {
                    Address item = mAddressDataList.get(i);
                    if (item.getAddressId()==1) {
                        item.setIsDefaultAddress(true);
                        isDefaultPosition=i;
                    }
                }
            }
            /**
             *
             */
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mAddressDataList != null && mAddressDataList.size() > 0) {
            for (int i = 0; i < mAddressDataList.size(); i++) {
                mAdapter.setNewData(mAddressDataList);
            }
        }else {
            mAddressDataList.clear();
            mAdapter.setNewData(mAddressDataList);
        }
    }


    private void updateAddress(Address address) {
        jumpAddressAdd(address);
    }

    private void delAddress(Address address) throws InterruptedException {
        Long addressId = address.getAddressId();
        if(new DataManager().deleteAddress(addressId)==0) {
            remove(mAddressDataList,address);
            mAdapter.notifyDataSetChanged();
        }
    }
    public void remove(List<Address> li, Address target){
        for(int i = li.size() - 1; i >= 0; i--) {
            Address item = li.get(i);
            if (target.equals(item)) {
                li.remove(item);
            }
        }
    }

    /**
     * 需要改变2个对象的值.一个是 之前默认的.一个是当前设置默认的
     * @param mAddressDataList
     * @param position
     */
    private void chooseDefult(List<Address> mAddressDataList, int position) throws InterruptedException {
        Address preAddress = mAddressDataList.get(isDefaultPosition);
        Address nowAddress = mAddressDataList.get(position);

        isDefaultPosition = position;
        PreferencesUtils.putInt(this, "isDefaultPosition", isDefaultPosition);

        changeBean(preAddress);
        changeBean(nowAddress);

        //initAddress();
        mAdapter.notifyDataSetChanged();

    }

    private void changeBean(Address address) throws InterruptedException {

        Long addressId = address.getAddressId();
        address.setAddressId(addressId);
        address.setIsDefaultAddress(!address.getIsDefaultAddress());
        updateAddress(address);
    }

    /**
     * 标题的初始化
     */
    private void initToolbar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到添加地址界面
                jumpAddressAdd(null);
            }
        });
    }

    private void jumpAddressAdd(Address address) {
        Intent intent = new Intent(AddressListActivity.this, AddressAddActivity.class);
        intent.putExtra("addressid",mAddressDataList.size());
        //这里不能使用序列化
        if (address != null) {
            intent.putExtra("addressId", address.getAddressId());
            intent.putExtra("name", address.getName());
            intent.putExtra("phone", address.getPhone());
            intent.putExtra("BigAddress", address.getBigAddress());
            intent.putExtra("SmallAddress", address.getSmallAddress());
        }
        startActivity(intent);
    }
}
