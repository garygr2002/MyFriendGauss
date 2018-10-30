package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.content.Problem;

import java.util.ArrayList;
import java.util.HashSet;

public class ControlFragment extends ContentFragment<Problem> {

    // The tag for our logging
    private static final String TAG = ControlFragment.class.getSimpleName();

    // Our content producer
    private final ProblemContentProducer contentProducer = new ProblemContentProducer();

    // The problem
    private Problem problem;

    // The edit control for the problem name
    private EditText problemNameEditText;

    /**
     * Creates an instance of a ControlFragment with the required argument(s).
     *
     * @param problemId The problem ID to be associated with the instance
     * @return An instance of a ControlFragment
     */
    public static ControlFragment createInstance(long problemId) {

        /*
         * Create an instance of a ControlFragment, and customize it with the problem ID.
         * Return the fragment.
         */
        final ControlFragment fragment = new ControlFragment();
        ContentFragment.customizeInstance(fragment, problemId);
        return fragment;
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        output("createContent(LayoutInflater, ViewGroup)");

        /*
         * Inflate our content and find the edit control for the problem name. Find the edit
         * control for the problem name.
         */
        final View view = inflater.inflate(R.layout.content_control, container, true);
        problemNameEditText = view.findViewById(R.id.problem_name);

        /*
         * Set the text of the edit control with the problem name. Give the problem name a text
         * watcher.
         */
        problemNameEditText.setText(problem.getName());
        problemNameEditText.addTextChangedListener(new GaussTextWatcher<Problem>(problem) {

            @Override
            protected String getContentString() {
                return getContent().getName();
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
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and get the problem.
        super.onCreate(savedInstanceState);
        problem = contentProducer.getContent(getProblemId());

        // Set the change list and the change set.
        setChangeList(new ArrayList<Problem>());
        setChangeSet(new HashSet<Problem>());
    }

    @Override
    public void onDestroy() {

        // Release the changes, and set the problem to null. Call the superclass method.
        releaseChanges();
        problem = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        // Release the edit control for the problem name. Call the superclass method.
        problemNameEditText = null;
        super.onDestroyView();
    }
}
