package com.enjoyshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.enjoyshop.R;
import com.enjoyshop.activity.GoodsDetailsActivity;
import com.enjoyshop.activity.SearchActivity;
import com.enjoyshop.activity.defaultactivity;
import com.enjoyshop.activity.goods_detailActivity;
import com.enjoyshop.adapter.HotGoodsAdapter;
import com.enjoyshop.bean.HotGoods;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.data.DataManager;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.zhy.http.okhttp.log.LoggerInterceptor.TAG;


/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     time   : 2017/08/02
 *     desc   : 热卖商品fragment
 *     version: 2.0
 * </pre>
 */
public class HotFragment extends BaseFragment {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE   = 2;
    private              int state        = STATE_NORMAL;       //正常情况


    @BindView(R.id.recyclerview)
    RecyclerView          mRecyclerView;
    @BindView(R.id.refresh_view)
    MaterialRefreshLayout mRefreshLaout;

    private int  currPage  = 1;     //当前是第几页
    private int  totalPage = 1;    //一共有多少页
    private int  pageSize  = 10;     //每页数目
    private Gson mGson     = new Gson();
    private List<HotGoods.ListBean> datas;
    private HotGoodsAdapter         mAdatper;


    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_hot;
    }

    @Override
    protected void init() {
        initRefreshLayout();     //控件初始化
        try {
            getData();              //获取后台数据
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //跳转到搜索界面
    @OnClick({R.id.toolbar})
    public void searchView(View view) {
        startActivity(new Intent(getContext(), SearchActivity.class));
    }


    private void initRefreshLayout() {

        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                try {
                    refreshData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if (currPage <= totalPage) {
                    try {
                        loadMoreData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showSafeToast(getContext(),"没有更多数据啦");
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }

    /**
     * 加载更多
     */
    private void loadMoreData() throws InterruptedException {
        state = STATE_MORE;
        currPage = 1+currPage;
        getData();
    }

    /**
     * 刷新
     */
    private void refreshData() throws InterruptedException {
        state = STATE_REFREH;
        currPage = 1;
        getData();
    }
    //调用DataManager的getgoods方法获取数据库的商品信息
    private void getData() throws InterruptedException {
         DataManager dm=new DataManager();
        //初始化参数
         String  copyright="xxxxx";//设置不从数据库获取，默认为空
         int totalCount=28;
        //以下数据均需从数据库获取
         List<HotGoods.OrdersBean> orders=new ArrayList<HotGoods.OrdersBean>();
         List<HotGoods.ListBean>   list = new ArrayList<HotGoods.ListBean>();
         Map<Integer,List<String>> goods_map=dm.getGoods(currPage,pageSize);
         for(int i_count=0;i_count<10;i_count++){
             List<String> temp_goods_info=goods_map.get(i_count+1);
             String[] temp=new String[temp_goods_info.size()];
             temp_goods_info.toArray(temp);
             HotGoods.ListBean temp_listbean=new HotGoods.ListBean(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]),Integer.parseInt(temp[2]),temp[3],temp[4],Double.parseDouble(temp[5]),Double.parseDouble(temp[6]));
             list.add(temp_listbean);
         }
         HotGoods hotGoods=new HotGoods(copyright,totalCount,currPage,totalPage,pageSize,orders,list);
         currPage=hotGoods.getCurrentPage();
         totalPage=hotGoods.getTotalPage();
         datas=hotGoods.getList();
         showData();
    }

    /**
     * 展示数据
     */
    private void showData() {
        switch (state) {
            case STATE_NORMAL:
                mAdatper = new HotGoodsAdapter(datas, getContext());
                mRecyclerView.setAdapter(mAdatper);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.HORIZONTAL));
                break;
            case STATE_REFREH:
                mAdatper.clearData();
                mAdatper.addData(datas);
                mRecyclerView.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;
            case STATE_MORE:
                mAdatper.addData(mAdatper.getDatas().size(), datas);
                mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;
        }


        mAdatper.setOnItemClickListener(new HotGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //借助currPage 和pageSize 可以实现默认情况和刷新时,都可以使用
                HotGoods.ListBean listBean = mAdatper.getDatas().get(position);
                Intent intent=new Intent(getContext(),goods_detailActivity.class);
                //Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
               // Bundle bundle = new Bundle();
                //bundle.putSerializable("itemClickGoods", (Serializable) listBean);
                //intent.putExtras(bundle);
                intent.putExtra("imgurl",listBean.getImgUrl());
                intent.putExtra("name",listBean.getName());
                intent.putExtra("price",listBean.getPrice());
                intent.putExtra("sale",listBean.getSale());
                startActivity(intent);
            }
        });
    }
}
