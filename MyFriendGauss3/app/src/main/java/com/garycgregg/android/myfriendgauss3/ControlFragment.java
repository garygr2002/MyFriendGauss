package com.garycgregg.android.myfriendgauss3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ControlFragment extends CardFragment {

    // The problem associated with this instance
    private Problem problem;

    // The edit control for the problem name
    private EditText problemNameEditText;

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        // Inflate our content, and find the edit control for the problem name.
        final View view = inflater.inflate(R.layout.content_control, container, true);
        problemNameEditText = view.findViewById(R.id.problem_name);

        /*
         * Get the problem ID and the associated problem. Is there no problem associated with the
         * problem ID?
         */
        final long problemId = getProblemId();
        problem = getProblemLab().getProblem(problemId);
        if (null == problem) {

            /*
             * There is no problem with the associated problem ID. Create a new problem, and set
             * the problem ID. Note: It will not be possible to update this problem in the
             * database, as no record exists.
             */
            problem = new Problem();
            problem.setProblemId(problemId);
        }

        // Set the text of the edit control with the problem name.
        problemNameEditText.setText(problem.getName());
    }

    @Override
    public void onDestroyView() {

        // Clear the problem object and call through to the superclass method.
        problem = null;
        super.onDestroyView();
    }

    @Override
    public void onPause() {

        // Call through to the superclass method, and update the problem name.
        super.onPause();
        updateName();
    }

    /**
     * Updates the problem name in the problem lab. TODO: Change this functionality to update the
     * name in the problem object every time any change occurs in the edit control. Then update
     * The database either due to a pause event, or a command from an external caller.
     */
    private void updateName() {

        /*
         * Get the name current set in the edit text. Get the name from the problem object. Do
         * these names not match?
         */
        final String setName = problemNameEditText.getText().toString();
        final String existingName = problem.getName();
        if ((null == existingName) || (!setName.equals(problem.getName()))) {

            /*
             * The names set in the edit text and problem object do not match. Update the
             * problem object to match the edit text, and update the database.
             */
            problem.setName(setName);
            getProblemLab().updateName(problem);
        }
    }
}
