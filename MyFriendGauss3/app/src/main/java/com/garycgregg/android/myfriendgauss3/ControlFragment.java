package com.garycgregg.android.myfriendgauss3;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlFragment extends ContentFragment<Problem> {

    // The list of changes, in change order
    private List<Problem> changeList;

    // The set of unique changes
    private Set<Problem> changeSet;

    // The problem associated with this instance
    private Problem problem;

    // The edit control for the problem name
    private EditText problemNameEditText;

    @Override
    protected void createControls(LayoutInflater inflater, ViewGroup container) {

        /*
         * Inflate our content and find the edit control for the problem name. Request content
         * for the problem name control.
         */
        final View view = inflater.inflate(R.layout.content_control, container, true);
        problemNameEditText = view.findViewById(R.id.problem_name);
        requestContent();
    }

    @Override
    public void onDestroyView() {

        // Release the change collections.
        releaseCollections();
        changeSet = null;
        changeList = null;

        // Clear the problem object and call through to the superclass method.
        problem = null;
        super.onDestroyView();
    }

    @Override
    public void onPause() {

        // Call through to the superclass method, and update the problem name.
        super.onPause();
        synchronizeChanges();
    }

    @Override
    protected void receiveContent(@NonNull Problem[] content) {

        // Get the first problem if there is at least one.
        final int firstIndex = 0;
        if (firstIndex < content.length) {

            // Okay, there is at least one.
            problem = content[firstIndex];
        }

        // There is not at least one problem.
        else {

            /*
             * Create a new problem. Set the new problem's name and ID. Note: This problem will
             * incur an error if an attempt is made to update it to the database.
             */
            problem = new Problem();
            problem.setName("");
            problem.setProblemId(getProblemId());
        }

        // Create a new change list and change set.
        setChangeList(changeList = new ArrayList<>());
        setChangeSet(changeSet = new HashSet<>());

        /*
         * Set the text of the edit control with the problem name. Give the problem name a
         * text watcher.
         */
        problemNameEditText.setText(problem.getName());
        problemNameEditText.addTextChangedListener(new GaussTextWatcher<Problem>(problem) {

            @Override
            protected boolean isChanged(String candidate) {
                return !candidate.equals(getContent().getName());
            }

            @Override
            protected void setChange(String change) {

                // Set the name in the content, and add the content to the change list.
                final Problem content = getContent();
                content.setName(change);
                addChange(content);
            }
        });
    }

    @Override
    protected void requestContent() {

        // Get the problem indicated by the ID, and directly receive the result.
        final Problem[] content = {getProblemLab().getProblem(getProblemId())};
        receiveContent(content);
    }

    @Override
    public void synchronizeChanges() {

        // Get the problem lab, and cycle for each change.
        final ProblemLab problemLab = getProblemLab();
        for (Problem problem : changeList) {

            // Update the name for the first/next problem.
            problemLab.updateName(problem);
        }

        // Clear the changes.
        clearChanges();
    }
}
