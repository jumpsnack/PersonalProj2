package com.ncs.eddie.personalproj2._menu.smsManager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncs.eddie.personalproj2.R;

/**
 * Created by eddie on 2017. 5. 24..
 */

public class SmsMngFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms_manager, container, false);

        return view;
    }
}
