package com.ncs.eddie.personalproj2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.ncs.eddie.personalproj2._control.ControlFragment;
import com.ncs.eddie.personalproj2._menu.MenuFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements onMainPagerCallback{

    private ViewPager controlPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controlPager = (ViewPager) findViewById(R.id.vp_container);
        controlPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public MainActivity getActivity() {
        return this;
    }

    @Override
    public FragmentManager getFragmentManagerFromActivity() {
        return getSupportFragmentManager();
    }

    class MainPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> fragments;

        public MainPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);

            fragments = new ArrayList<>();
            fragments.add(ControlFragment.create());
            fragments.add(MenuFragment.create());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
