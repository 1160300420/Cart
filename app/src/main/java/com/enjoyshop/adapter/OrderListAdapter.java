package com.enjoyshop.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.bean.ShoppingCart;

import java.util.List;

public class OrderListAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    public OrderListAdapter(List<String> datas) {
        super(R.layout.template_order, datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper .setText(R.id.txt1_address,item)
                .addOnClickListener(R.id.txt1_edit)
                .addOnClickListener(R.id.txt1_del);
    }
}
