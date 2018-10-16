package com.garycgregg.android.myfriendgauss3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ControlFragment extends CardFragment {

    private Problem problem;
    private EditText problemNameEditText;

    public static CardFragment createInstance() {
        return new ControlFragment();
    }

    @Override
    public void onPause() {

        super.onPause();
        problem.setName(problemNameEditText.getText().toString());
        getProblemLab().updateName(problem);
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        final View view = inflater.inflate(R.layout.content_control, container, true);
        problemNameEditText = view.findViewById(R.id.problem_name);

        final long problemId = getProblemId();
        problem = getProblemLab().getProblem(problemId);
        if (null == problem) {

            problem = new Problem();
            problem.setProblemId(problemId);
        }

        problemNameEditText.setText(problem.getName());
    }
}
