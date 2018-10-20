package com.garycgregg.android.myfriendgauss3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ControlFragment extends CardFragment {

    private Problem problem;
    private EditText problemNameEditText;

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

    @Override
    public void onDestroyView() {

        problem = null;
        super.onDestroyView();
    }

    @Override
    public void onPause() {

        super.onPause();
        updateName();
    }

    /**
     * Updates the problem name in the problem lab.
     */
    private void updateName() {

        final String setName = problemNameEditText.getText().toString();
        final String existingName = problem.getName();
        if ((null == existingName) || (!setName.equals(problem.getName()))) {

            problem.setName(setName);
            getProblemLab().updateName(problem);
        }
    }
}
