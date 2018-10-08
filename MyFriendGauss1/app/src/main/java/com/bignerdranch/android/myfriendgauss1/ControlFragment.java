package com.bignerdranch.android.myfriendgauss1;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class ControlFragment extends CardFragment {

    public static Fragment createInstance() {
        return new ControlFragment();
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        final TextView text = new TextView(getActivity());
        text.setText(R.string.control_panel_message);
        container.addView(text);
    }
}
