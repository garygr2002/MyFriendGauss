package com.garycgregg.android.myfriendgauss3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ControlFragment extends CardFragment {

    private Problem problem;

    public static CardFragment createInstance() {
        return new ControlFragment();
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        final View view = inflater.inflate(R.layout.content_control, container, true);
        final long problemId = getProblemId();

        problem = getProblemLab().getProblem(problemId);
        if (null != problem) {

            final TextView textView = view.findViewById(R.id.problem_name);
            textView.setText(problem.getName());
        }
    }
}
