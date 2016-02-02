package com.github.chaossss.ishuhui.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.github.chaossss.httplibrary.listener.BaseCallbackListener;
import com.github.chaossss.ishuhui.R;
import com.github.chaossss.ishuhui.domain.dao.AppDao;
import com.github.chaossss.ishuhui.domain.handler.AdvPagerHandler;
import com.github.chaossss.ishuhui.domain.model.AdvModel;
import com.github.chaossss.ishuhui.domain.util.LogUtils;
import com.github.chaossss.ishuhui.ui.adapter.AdvPagerAdapter;
import com.github.chaossss.ishuhui.ui.adapter.ComicPagerAdapter;
import com.github.chaossss.ishuhui.ui.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by chaossss on 2016/1/8.
 */
public class CategoryFragment extends Fragment implements PullRefreshLayout.OnRefreshListener, AdvPagerHandler.AdvPagerMsgListener {
    @Bind(R.id.category_tab)
    TabLayout tabLayout;
    @Bind(R.id.category_adv_viewpager)
    ViewPager advViewPager;
    @Bind(R.id.category_comic_viewpager)
    ViewPager comicViewPager;
    @Bind(R.id.category_indicator)
    CircleIndicator circleIndicator;
    @Bind(R.id.category_refresh_layout)
    PullRefreshLayout pullRefreshLayout;

    private AdvPagerAdapter advPagerAdapter;
    private ComicPagerAdapter comicPagerAdapter;

    private AdvPagerHandler advPagerHandler;

    private boolean isAntiClockWise;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        advPagerHandler = new AdvPagerHandler(this);
        advPagerAdapter = new AdvPagerAdapter(getActivity());
        comicPagerAdapter = new ComicPagerAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAdvPager();
        initComicPager();
        pullRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initAdvPager(){
        advViewPager.setAdapter(advPagerAdapter);
        getAdvData();
    }

    private void initComicPager() {
        comicViewPager.setAdapter(comicPagerAdapter);
        comicViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(comicViewPager);
    }

    private void getAdvData(){
        AppDao.getInstance().getSlideData(new BaseCallbackListener<AdvModel>() {
            @Override
            public void onStringResult(String result) {
                super.onStringResult(result);
                LogUtils.logI(this, "onStringResult" + result);
            }

            @Override
            public void onSuccess(AdvModel result) {
                super.onSuccess(result);
                if (result != null) {
                    advPagerAdapter.updateAdvs(result.list);
                    circleIndicator.setViewPager(advViewPager);
                    pullRefreshLayout.setRefreshing(false);
                    advPagerHandler.slidingPlayView(advViewPager.getCurrentItem());
                }
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                ToastUtils.showToast(getActivity(), e.toString());
                pullRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        getAdvData();
    }

    @Override
    public void onAdvPagerMsgRespond(Message msg) {
        if(msg.what == AdvPagerHandler.ADV_PAGER_MSG){
            int currIndex = msg.arg1;

            if(!isAntiClockWise){
                if(currIndex != advPagerAdapter.getCount() - 1){
                    currIndex++;
                } else {
                    currIndex--;
                    isAntiClockWise = !isAntiClockWise;
                }
            } else {
                if(currIndex != 0){
                    currIndex--;
                } else {
                    currIndex++;
                    isAntiClockWise = !isAntiClockWise;
                }
            }

            advViewPager.setCurrentItem(currIndex);
            advPagerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    advPagerHandler.slidingPlayView(advViewPager.getCurrentItem());
                }
            }, AdvPagerHandler.DEFAULT_TIME);
        }
    }
}
