package com.garycgregg.android.myfriendgauss2;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ControlFragment extends CardFragment {

    public static Fragment createInstance() {
        return new ControlFragment();
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {
        inflater.inflate(R.layout.content_control, container, true);
    }
}
