package com.garycgregg.android.myfriendgauss3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class CardFragment extends Fragment implements ProblemLabSource {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String PREFIX = CardFragment.class.getName();
    private static final String ID_ARGUMENT = String.format(FORMAT_STRING, PREFIX, "problem_id");

    private long problemId;
    private ProblemLab problemLab;

    public static void customizeInstance(CardFragment fragment, long problemId) {

        Bundle arguments = fragment.getArguments();
        if (null == arguments) {

            arguments = new Bundle();
        }

        arguments.putLong(ID_ARGUMENT, problemId);
        fragment.setArguments(arguments);
    }

    @Override
    public ProblemLab getProblemLab() {
        return problemLab;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        problemId = getArguments().getLong(ID_ARGUMENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final CardView view = (CardView) inflater.inflate(R.layout.fragment_card, container,
                false);

        final ProblemLabSource problemLabSource = ((ProblemLabSource) getActivity());
        problemLab = problemLabSource.getProblemLab();

        createContent(inflater, (ViewGroup) view.findViewById(R.id.card_content));
        return view;
    }

    protected abstract void createContent(LayoutInflater inflater, ViewGroup container);

    protected long getProblemId() {
        return problemId;
    }
}
