package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;
import com.garycgregg.android.myfriendgauss3.database.ProblemLabSource;

public abstract class GaussFragment extends Fragment implements ProblemLabSource {

    // The format of an instance argument
    protected static final String ARGUMENT_FORMAT_STRING = "%s.%s_argument";

    // A tag for logging statements
    private static final String TAG = GaussFragment.class.getSimpleName();

    // The source of the problem lab
    private ProblemLabSource problemLabSource;

    /**
     * Gets a tag for logging statements.
     *
     * @return A tag for logging statements
     */
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public ProblemLab getProblemLab() {
        return (null == problemLabSource) ? null : problemLabSource.getProblemLab();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        output("onActivityCreated(Bundle)");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {

        output("onAttach(Context)");

        /*
         * Call through to the superclass method, and if possible cast the given context to a
         * problem lab source. Save the source.
         */
        super.onAttach(context);
        problemLabSource = (context instanceof ProblemLabSource) ? ((ProblemLabSource) context) :
                null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        output("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        output("onCreateOptionsMenu(Menu, MenuInflater");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        output("onCreateView(LayoutInflater, ViewGroup, Bundle)");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {

        output("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        output("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        output("onDetach()");

        // Clear the problem lab source, and call through to the superclass method.
        problemLabSource = null;
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        output("onOptionsItemSelected(MenuItem)");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {

        output("onPause()");
        super.onPause();
    }

    @Override
    public void onResume() {

        output("onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        output("onSaveInstanceState(Bundle)");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {

        output("onStart()");
        super.onStart();
    }

    @Override
    public void onStop() {

        output("onStop()");
        super.onStop();
    }

    /**
     * Outputs an informational message.
     *
     * @param message The message to output
     */
    protected void output(String message) {
        Log.d(getLogTag(), String.format("%s (Object hashcode: %d)", message, hashCode()));
    }

    protected abstract class ContentProducer<T> {

        /**
         * Gets content.
         *
         * @param problemId The problem ID of the requested content
         * @return The requested content, or default content if the requested content does not exist
         */
        public T getContent(long problemId) {

            // Get the problem lab. Declare and initialize the content. Is the content null?
            final ProblemLab problemLab = getProblemLab();
            T content = (null == problemLab) ? null : produceContent(problemLab, problemId);
            if (null == content) {

                // The content is null. Reinitialize the content to default content.
                content = onNotFound(problemLab, problemId);
            }

            // Return the content.
            return content;
        }

        /**
         * Produces content when the desired content is not available.
         *
         * @param problemLab A problem lab
         * @return Default content
         */
        public abstract T onNotFound(ProblemLab problemLab, long problemId);

        /**
         * Produces requested content.
         *
         * @param problemLab A problem lab
         * @param problemId  The problem ID of the requested content
         * @return The requested content
         */
        public abstract T produceContent(ProblemLab problemLab, long problemId);
    }

    protected class ProblemContentProducer extends ContentProducer<Problem> {

        @Override
        public Problem onNotFound(ProblemLab problemLab, long problemId) {

            // Create a new problem and set its problem ID.
            final Problem problem = new Problem();
            problem.setProblemId(problemId);
            return problem;
        }

        @Override
        public Problem produceContent(ProblemLab problemLab, long problemId) {
            return problemLab.getProblem(problemId);
        }
    }
}
