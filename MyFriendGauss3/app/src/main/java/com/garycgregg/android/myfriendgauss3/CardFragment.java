package com.garycgregg.android.myfriendgauss3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class CardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final CardView view = (CardView) inflater.inflate(R.layout.fragment_card, container, false);
        createContent(inflater, (ViewGroup) view.findViewById(R.id.card_content));
        return view;
    }

    protected abstract void createContent(LayoutInflater inflater, ViewGroup container);
}
