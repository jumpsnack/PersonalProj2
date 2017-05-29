package com.ncs.eddie.personalproj2._control;

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

public class ControlFragment extends Fragment {
    private static ControlFragment instance = null;

    public static ControlFragment create() {
        if (instance == null){
            instance = new ControlFragment();
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        return view;
    }
}
