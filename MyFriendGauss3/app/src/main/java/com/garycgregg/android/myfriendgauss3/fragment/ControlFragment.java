package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public class ControlFragment extends ContentFragment<Problem> {

    // The prefix for instance arguments
    private static final String PREFIX_STRING = ControlFragment.class.getName();

    // The problem argument key
    private static final String PROBLEM_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "problem");

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
     * @param enabled   The fragment enabled argument
     * @param problem   The problem associated with the instance
     * @return An instance of a ControlFragment
     */
    public static ControlFragment createInstance(long problemId, boolean enabled,
                                                 Problem problem) {

        /*
         * Create an instance of a ControlFragment, and customize it with parameters required
         * of a ContentFragment. Is the problem not null?
         */
        final ControlFragment fragment = new ControlFragment();
        ContentFragment.customizeInstance(fragment, problemId, enabled);
        if (null != problem) {

            // The problem is not null. Get the arguments from the fragment, and add the problem.
            fragment.getArguments().putParcelable(PROBLEM_ARGUMENT, problem);
        }

        // Return the fragment.
        return fragment;
    }

    /**
     * Creates an instance of a ControlFragment with the required argument(s).
     *
     * @param problemId The problem ID to be associated with the instance
     * @param enabled   The fragment enabled argument
     * @return An instance of a ControlFragment
     */
    public static ControlFragment createInstance(long problemId, boolean enabled) {
        return createInstance(problemId, enabled, null);
    }

    @Override
    protected boolean change(Problem record, ProblemLab problemLab) {

        // Update the problem.
        problemLab.update(record);
        return true;
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        output("createContent(LayoutInflater, ViewGroup)");

        // Create a record tracker, and inflate our content.
        final View view = inflater.inflate(R.layout.content_control, container, true);
        setRecordTracker(new RecordTracker<Problem>(1));

        // Find the edit control for the problem name, and get the enabled flag.
        problemNameEditText = view.findViewById(R.id.problem_name);
        final boolean enabled = isEnabled();

        // Make the edit control clickable or focusable if it is enabled.
        problemNameEditText.setClickable(enabled);
        problemNameEditText.setFocusable(enabled);

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

                // Set the name in the content, and add the content to the record tracker.
                final Problem content = getContent();
                content.setName(change);
                getRecordTracker().put(problemNameEditText.getId(), content, true);
            }
        });
    }

    @Override
    protected boolean delete(Problem record, ProblemLab problemLab) {
        return false;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        /*
         * Call the superclass method. Try to get the problem as an argument. Was there no
         * problem argument?
         */
        super.onCreate(savedInstanceState);
        problem = getArguments().getParcelable(PROBLEM_ARGUMENT);
        if (null == problem) {

            // There was no problem argument. Create a dummy problem.
            problem = contentProducer.onNotFound(null, ProblemLab.NULL_ID);
        }
    }

    @Override
    public void onDestroy() {

        // Set the problem to null, and call the superclass method.
        problem = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        // Release the changes, and set the edit control to null. Call the superclass method.
        releaseChanges();
        problemNameEditText = null;
        super.onDestroyView();
    }
}
