package com.enjoyshop.bean;

import java.io.Serializable;

/**
 * Created by 高磊华
 * Time  2017/8/9
 * Describe:  购物车商品信息.数据来源于商品数据.但多了数量、是否选中两个 属性
 */

public class ShoppingCart extends HotGoods.ListBean implements Serializable {

    private int count;
    private boolean isChecked = true;
    public ShoppingCart(){

    }
    public ShoppingCart(HotGoods.ListBean l){
        this.setId(l.getId());
        this.setCampaignId(l.getCampaignId());
        this.setCategoryId(l.getCategoryId());
        this.setImgUrl(l.getImgUrl());
        this.setName(l.getName());
        this.setPrice(l.getPrice());
        this.setSale(l.getSale());
        this.setCount(1);
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}
