package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.content.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlFragment extends ContentFragment<Problem> {

    // The problem argument
    private static final String PROBLEM_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ControlFragment.class.getName(), "problem");

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

        /*
         * Get the problem indicated by the ID, and directly receive the result. TODO: Get
         * content asynchronously.
         */
        final Problem[] content = {getProblemLab().getProblem(getProblemId())};
        setContentList(Arrays.asList(content));
        checkForContent();
    }

//    private void examineThis() {
//
//        // TODO:
//        final Bundle arguments = new Bundle();
//        final String key = "key";
//
//        arguments.putLong(key, getProblemId());
//        final FragmentActivity activity = getActivity();
//        activity.getSupportLoaderManager().initLoader(0, arguments,
//                new LoaderManager.LoaderCallbacks<List<Problem>>() {
//
//                    @Override
//                    public Loader onCreateLoader(int id, Bundle args) {
//
//                        Log.i(TAG, "onCreateLoader(int, Bundle)");
//                        return new ProblemTaskLoader(activity, getProblemLab(),
//                                args.getLong(key, ProblemLab.NULL_ID));
//                    }
//
//                    @Override
//                    public void onLoadFinished(Loader<List<Problem>> loader, List<Problem> data) {
//
//                        Log.i(TAG, "onLoadFinished(Loader<List<Problem>>, List<Problem>)");
//                        receiveContent(Problem.CREATOR.newArray(data.size()));
//                    }
//
//                    @Override
//                    public void onLoaderReset(Loader<List<Problem>> loader) {
//
//                        // Nothing to do here yet.
//                        Log.i(TAG, "onLoaderReset(Loader<List<Problem>>)");
//                    }
//                }).forceLoad();
//    }

    @Override
    public Problem[] exportChanges() {

        // Create and fill a changes array.
        final Problem[] changes = Problem.CREATOR.newArray(changeList.size());
        changeList.toArray(changes);

        // Clear the changes, and return the change array.
        clearChanges();
        return changes;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        /*
         * Call through to the superclass method. Use the saved instance state for arguments if it
         * is not null. Otherwise use the instance supplied arguments. Set the problem.
         */
        super.onCreate(savedInstanceState);
        final Bundle arguments = (null == savedInstanceState) ? getArguments() :
                savedInstanceState;

        // Set the problem. Is the problem null?
        problem = arguments.getParcelable(PROBLEM_ARGUMENT);

    }

    @Override
    public void onDestroy() {

        // Clear the problem. Call through to the superclass method.
        problem = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        // Release the change collections. Clear the change set.
        release();
        changeSet = null;

        // Clear the change list. Call through to the superclass method.
        changeList = null;
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call through to the superclass method, and save the problem.
        super.onSaveInstanceState(outState);
        outState.putParcelable(PROBLEM_ARGUMENT, problem);
    }

    /**
     * Receives content. TODO: Remove or modify this.
     *
     * @param content Content for this fragment
     */
    private void receiveContent(@NonNull Problem[] content) {

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
    protected void setContent() {

        // Create a content array.
        final List<Problem> contentList = getContentList();
        final Problem[] content = Problem.CREATOR.newArray(contentList.size());

        /*
         * Copy the content list to the content array. Receive the contents. Has content
         * already been set in this fragment?
         */
        contentList.toArray(content);
        receiveContent(content);
        if (isContentSet()) {

            // Content has already been set in this fragment. Replace the fragment.
            // TODO: Replace the fragment.
        }

        // Call the superclass method to maintain the set content flag.
        super.setContent();
    }
}
