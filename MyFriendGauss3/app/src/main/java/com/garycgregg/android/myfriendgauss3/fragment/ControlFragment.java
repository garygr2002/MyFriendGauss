package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;
import com.garycgregg.android.myfriendgauss3.loader.ProblemTaskLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlFragment extends ContentFragment<Problem> {

    // The tag for our logging
    private static final String TAG = ControlFragment.class.getSimpleName();

    // The list of changes, in change order
    private List<Problem> changeList;

    // The set of unique changes
    private Set<Problem> changeSet;

    // The edit control for the problem name
    private EditText problemNameEditText;

    public ControlFragment() {

        // TODO: Remove this.
        outputLog("ControlFragment()");
    }

    @Override
    protected void clearContentList() {

        // TODO: Remove this.
        outputLog("clearContentList()");
        super.clearContentList();
    }

    @Override
    protected void createControls(LayoutInflater inflater, ViewGroup container) {

        outputLog(String.format("createControls(LayoutInflator, ViewGroup: Content list is %snull.",
                (null == getContentList()) ? "" : "not "));

        /*
         * Inflate our content and find the edit control for the problem name.
         */
        final View view = inflater.inflate(R.layout.content_control, container, true);
        problemNameEditText = view.findViewById(R.id.problem_name);

        // Get the problem. Is the problem not null?
        final Problem problem = getProblem();
        if (null != problem) {

            // The problem is not null. Create a new change list and change set.
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
    }

    @Override
    public Problem[] exportChanges() {

        outputLog("exportChanges()");

        // Create and fill a changes array.
        final Problem[] changes = Problem.CREATOR.newArray(changeList.size());
        changeList.toArray(changes);

        // Clear the changes, and return the change array.
        clearChanges();
        return changes;
    }

    /**
     * Gets the problem for this fragment.
     *
     * @return The problem for this fragment.
     */
    private Problem getProblem() {

        outputLog("getProblem()");

        // Identify the first index, and get the content list.
        final int firstIndex = 0;
        final List<Problem> problemList = getContentList();

        // Return the first problem if there is one. Otherwise return null.
        return ((null != problemList) && (firstIndex < problemList.size()) ?
                problemList.get(firstIndex) : null);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // TODO: Remove this.
        outputLog("onActivityCreated(Bundle)");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {

        // TODO: Remove this.
        outputLog("onAttach(Context)");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        outputLog("onCreate(Bundle)");

        // Call through to the superclass method. Get the problem lab. Is the problem lab not null?
        super.onCreate(savedInstanceState);
        final ProblemLab problemLab = getProblemLab();
        if (null != problemLab) {

            // The problem lab it not null. Set content. TODO: Try getting content asynchronously.
            setContentSynchronously(problemLab);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // TODO: Remove this.
        outputLog("onCreateView(LayoutInflator, ViewGroup, Bundle)");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {

        outputLog("onDestroy");

        // Clear collections, and call through to the superclass method.
        release();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        outputLog("onDestroyView");

        // Release the change collections. Clear the change set.
        releaseChanges();
        changeSet = null;

        // Clear the change list. Call through to the superclass method.
        changeList = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        // TODO: Remove this.
        outputLog("onDetach()");
        super.onDetach();
    }

    @Override
    public void onPause() {

        // TODO: Remove this.
        outputLog("onPause()");
        super.onPause();
    }

    @Override
    public void onResume() {

        // TODO: Remove this.
        outputLog("onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // TODO: Remove this.
        outputLog("onSaveInstanceState(Bundle)");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {

        // TODO: Remove this.
        outputLog("onStart()");
        super.onStart();
    }

    @Override
    public void onStop() {

        // TODO: Remove this.
        outputLog("onStop()");
        super.onStop();
    }

    /**
     * Outputs an informational message.
     *
     * @param message The message to output
     */
    private void outputLog(String message) {

        // TODO: Remove this.
        Log.i(TAG, String.format("%s (%d)", message, hashCode()));
    }

    @Override
    protected void release() {

        // TODO: Remove this.
        outputLog("release()");
        super.release();
    }

    /**
     * Sets content asynchronously.
     *
     * @param problemLab The problem lab from which to retrieve content
     */
    private void setContentAsynchronously(@NonNull final ProblemLab problemLab) {

        outputLog("setContentAsynchronously(ProblemLab)");

        final Bundle arguments = new Bundle();
        final String key = "key";

        arguments.putLong(key, getProblemId());
        final FragmentActivity activity = getActivity();

        activity.getSupportLoaderManager().initLoader(0, arguments,
                new LoaderManager.LoaderCallbacks<List<Problem>>() {

                    @Override
                    public Loader<List<Problem>> onCreateLoader(int id, Bundle arguments) {
                        return new ProblemTaskLoader(activity, problemLab,
                                arguments.getLong(key, ProblemLab.NULL_ID));
                    }

                    @Override
                    public void onLoadFinished(Loader<List<Problem>> loader, List<Problem> data) {

                        outputLog("The load is finished, setting content list...");
                        setContentList(data);

                        outputLog(String.format("The content list is %snull.",
                                (null == getContentList()) ? "" : "not "));
                        final Fragment fragment = ControlFragment.this;
                        if (fragment.isAdded()) {

                            outputLog("The fragment is added; reattaching it...");

                            // TODO: Think of something else here.
                            getFragmentManager().beginTransaction().detach(fragment).
                                    attach(fragment).commit();
                        } else {
                            outputLog("No fragment is added...");
                        }
                    }

                    @Override
                    public void onLoaderReset(Loader<List<Problem>> loader) {

                        // Nothing to do here.
                    }
                }).forceLoad();
    }

    /**
     * Sets content synchronously.
     *
     * @param problemLab The problem lab from which to retrieve content
     */
    private void setContentSynchronously(@NonNull ProblemLab problemLab) {

        outputLog("setContentSynchronously");

        /*
         * Create a new problem list. Get the problem corresponding to the problem ID. Is the
         * problem not null?
         */
        final List<Problem> problemList = new ArrayList<>();
        final Problem problem = problemLab.getProblem(getProblemId());
        if (null != problem) {

            // The problem is not null. Add the problem to the problem list.
            problemList.add(problem);
        }

        // Set the problem list as the content list.
        setContentList(problemList);
    }
}
