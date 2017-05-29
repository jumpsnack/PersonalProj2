package com.ncs.eddie.personalproj2._menu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncs.eddie.personalproj2.R;
import com.ncs.eddie.personalproj2._menu.scheduleManager.ScheduleMngFragment;
import com.ncs.eddie.personalproj2._menu.smsManager.SmsMngFragment;
import com.ncs.eddie.personalproj2._menu.trackerManager.TrackerMngFragment;
import com.ncs.eddie.personalproj2.onMainPagerCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eddie on 2017. 5. 24..
 */

public class MenuFragment extends Fragment{
    private static MenuFragment instance = null;
    private static onMainPagerCallback onMainPagerCallback;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static MenuFragment create(){
        if(instance == null){
            instance = new MenuFragment();
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        onMainPagerCallback.getActivity().setSupportActionBar(toolbar);

        onMainPagerCallback.getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.onMainPagerCallback = (onMainPagerCallback) activity;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(onMainPagerCallback.getFragmentManagerFromActivity());
        adapter.addFragment(new ScheduleMngFragment(), "운동 일정관리");
        adapter.addFragment(new SmsMngFragment(), "문자전송 관리");
        adapter.addFragment(new TrackerMngFragment(), "활동 모니터링");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
