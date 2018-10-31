package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public abstract class ContentFragment<T> extends GaussFragment {

    // True if a fragment is enabled by default, false otherwise
    private static final boolean DEFAULT_ENABLED_STATE = true;

    // The prefix for instance arguments
    private static final String PREFIX_STRING = ContentFragment.class.getName();

    // The fragment enabled argument key
    private static final String ENABLED_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "enabled");

    // The problem ID argument key
    private static final String PROBLEM_ID_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "problem_id");

    // A tag for logging statements
    private static final String TAG = ContentFragment.class.getSimpleName();

    // True if the fragment is enabled, false otherwise
    private boolean enabled = DEFAULT_ENABLED_STATE;

    // The problem ID associated with this instance
    private long problemId = ProblemLab.NULL_ID;

    /**
     * Customizes an instance of a ContentFragment with the required argument(s).
     *
     * @param fragment  An existing ContentFragment
     * @param problemId The problem ID to be associated with the instance
     * @param enabled   The fragment enabled argument
     */
    protected static void customizeInstance(@NonNull ContentFragment<?> fragment, long problemId,
                                            boolean enabled) {

        // Get the fragment arguments. Add the problem ID and enabled flag arguments.
        final Bundle arguments = getArguments(fragment);
        arguments.putLong(PROBLEM_ID_ARGUMENT, problemId);
        arguments.putBoolean(ENABLED_ARGUMENT, enabled);
    }

    /**
     * Gets the arguments from a fragment.
     *
     * @param fragment A fragment
     * @return The arguments from the fragment
     */
    protected static Bundle getArguments(@NonNull Fragment fragment) {

        // Get the existing arguments, if any.
        Bundle arguments = fragment.getArguments();
        if (null == arguments) {

            // Create a new, empty arguments object if there is none already.
            fragment.setArguments(arguments = new Bundle());
        }

        // Return the arguments.
        return arguments;
    }

    /**
     * Clears the changes.
     */
    public abstract void clearChanges();

    /**
     * Creates the content.
     *
     * @param inflater  An inflater for layouts
     * @param container A container for the content
     */
    protected abstract void createContent(LayoutInflater inflater, ViewGroup container);

    @Override
    protected String getLogTag() {
        return TAG;
    }

    /**
     * Gets the problem ID.
     *
     * @return The problem ID
     */
    protected long getProblemId() {
        return problemId;
    }

    /**
     * Determines if the fragment is enabled.
     *
     * @return True if the fragment is enabled, false otherwise
     */
    protected boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method. Get the instance arguments.
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();

        // Set the problem ID and enabled state arguments.
        setProblemId(arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID));
        setEnabled(arguments.getBoolean(ENABLED_ARGUMENT, DEFAULT_ENABLED_STATE));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Create a card view.
        final CardView view = (CardView) inflater.inflate(R.layout.fragment_card, container,
                false);

        // Create the subclass contentCollection. Return the card view.
        createContent(inflater, (ViewGroup) view.findViewById(R.id.card_content));
        return view;
    }

    @Override
    public void onDestroy() {

        // Clear the problem ID, and reset the enabled state flag. Call the superclass method.
        setEnabled(DEFAULT_ENABLED_STATE);
        setProblemId(ProblemLab.NULL_ID);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call through to the superclass method, and save the problem ID.
        super.onSaveInstanceState(outState);
        outState.putLong(PROBLEM_ID_ARGUMENT, getProblemId());
    }

    /**
     * Releases the change collections.
     */
    protected void releaseChanges() {

        // TODO: Fix this.
        // Clear the change collections before setting them to null.
        // clearChanges();
        // setChangeList(null);
        // setChangeSet(null);
    }

    /**
     * Sets the enabled state of the fragment.
     *
     * @param enabled True if the fragment is enabled, false otherwise
     */
    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the problem ID.
     *
     * @param problemId The problem ID
     */
    private void setProblemId(long problemId) {
        this.problemId = problemId;
    }
}
