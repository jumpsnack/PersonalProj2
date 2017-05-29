package com.ncs.eddie.personalproj2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ncs.eddie.personalproj2._control.ControlFragment;
import com.ncs.eddie.personalproj2._menu.MenuFragment;

import java.util.ArrayList;

/**
 * Created by eddie on 2017. 5. 24..
 */

public class MainPagerAdapter_dep extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;

    public MainPagerAdapter_dep(FragmentManager fragmentManager){
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
