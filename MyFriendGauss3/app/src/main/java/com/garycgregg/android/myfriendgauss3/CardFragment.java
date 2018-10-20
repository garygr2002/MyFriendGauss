package com.garycgregg.android.myfriendgauss3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class CardFragment extends GaussFragment {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String PREFIX = CardFragment.class.getName();
    private static final String PROBLEM_ID_ARGUMENT = String.format(FORMAT_STRING, PREFIX,
            "problem_id");

    private long problemId = ProblemLab.NULL_ID;

    public static void customizeInstance(CardFragment fragment, long problemId) {

        Bundle arguments = fragment.getArguments();
        if (null == arguments) {

            arguments = new Bundle();
        }

        arguments.putLong(PROBLEM_ID_ARGUMENT, problemId);
        fragment.setArguments(arguments);
    }

    protected abstract void createContent(LayoutInflater inflater, ViewGroup container);

    protected long getProblemId() {
        return problemId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final Bundle arguments = (null == savedInstanceState) ? getArguments() :
                savedInstanceState;
        problemId = arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final CardView view = (CardView) inflater.inflate(R.layout.fragment_card, container,
                false);

        createContent(inflater, (ViewGroup) view.findViewById(R.id.card_content));
        return view;
    }

    @Override
    public void onDestroy() {

        problemId = ProblemLab.NULL_ID;
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putLong(PROBLEM_ID_ARGUMENT, problemId);
    }
}
