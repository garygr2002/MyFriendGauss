package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class ContentFragment<T> extends GaussFragment {

    // The prefix for instance arguments
    private static final String PREFIX_STRING = NumbersFragment.class.getName();

    // The content set argument
    private static final String CONTENT_SET_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "content_set");

    // The problem ID argument
    private static final String PROBLEM_ID_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "problem_id");

    // The list of changes, in change order
    private List<T> changeList;

    // The set of unique changes
    private Set<T> changeSet;

    // The list of content objects
    private List<T> contentList;

    // True if content has been set, false otherwise
    private boolean contentSet;

    // The problem ID associated with this instance
    private long problemId = ProblemLab.NULL_ID;

    /**
     * Clears a collection.
     *
     * @param collection A collection
     * @param <T>        The type contained in the collection
     */
    private static <T> void clearCollection(Collection<T> collection) {

        // Clear the collection if it is not null.
        if (null != collection) {
            collection.clear();
        }
    }

    /**
     * Customizes an instance of a ContentFragment with the required argument(s).
     *
     * @param fragment  An existing ContentFragment
     * @param problemId The problem ID to be associated with the instance
     */
    public static void customizeInstance(ContentFragment<?> fragment, long problemId) {

        // Get the existing arguments, if any.
        Bundle arguments = fragment.getArguments();
        if (null == arguments) {

            // Create a new, empty arguments object if there is none already.
            arguments = new Bundle();
        }

        // Add the problem ID, and set or reset the arguments in the fragment instance.
        arguments.putLong(PROBLEM_ID_ARGUMENT, problemId);
        fragment.setArguments(arguments);
    }

    /**
     * Adds a change.
     *
     * @param change A change to add
     */
    protected void addChange(T change) {

        // Is the change set not null, and does the change set not already contain the change?
        if (!((null == changeSet) || changeSet.contains(change))) {

            /*
             * The change set does not already contain the change. Add the change to the change
             * list and the change set.
             */
            changeList.add(change);
            changeSet.add(change);
        }
    }

    /**
     * Checks for fragment content.
     */
    protected void checkForContent() {

        // Request content if there currently is not any.
        if (null == getContentList()) {
            requestContent();
        }

        // Set content if it currently exists.
        else {
            setContent();
        }
    }

    /**
     * Clears the changes.
     */
    protected void clearChanges() {

        // Clear both the change list and change set.
        clearCollection(changeList);
        clearCollection(changeSet);
    }

    /**
     * Creates subclass content.
     *
     * @param inflater  An inflater for layouts
     * @param container A container for the subclass content
     */
    protected abstract void createControls(LayoutInflater inflater, ViewGroup container);

    /**
     * Exports changes in the fragment to the caller.
     *
     * @return An array of changes
     */
    public abstract T[] exportChanges();

    /**
     * Gets the content list.
     *
     * @return The content list
     */
    protected List<T> getContentList() {
        return contentList;
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
     * Determines if content has been set.
     *
     * @return True if content has been set, false otherwise
     */
    protected boolean isContentSet() {
        return contentSet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        /*
         * Call through to the superclass method. Use the saved instance state for arguments if it
         * is not null. Otherwise use the instance supplied arguments.
         */
        super.onCreate(savedInstanceState);
        final Bundle arguments = (null == savedInstanceState) ? getArguments() :
                savedInstanceState;

        // Set the content set flag and the problem ID.
        setContentSet(arguments.getBoolean(CONTENT_SET_ARGUMENT, false));
        setProblemId(arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Create a card view.
        final CardView view = (CardView) inflater.inflate(R.layout.fragment_card, container,
                false);

        // Create the subclass content. Return the card view.
        createControls(inflater, (ViewGroup) view.findViewById(R.id.card_content));
        return view;
    }

    @Override
    public void onDestroy() {

        // Clear the content set flag and the problem ID. Call through to the superclass method.
        setContentSet(false);
        setProblemId(ProblemLab.NULL_ID);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call through to the superclass method. Save the content set flag and the problem ID.
        super.onSaveInstanceState(outState);
        outState.putBoolean(CONTENT_SET_ARGUMENT, isContentSet());
        outState.putLong(PROBLEM_ID_ARGUMENT, getProblemId());
    }

    /**
     * Releases the change collections.
     */
    protected void release() {

        // Clear the changes before making the change set and the change list null.
        clearChanges();
        setChangeList(null);
        setChangeSet(null);
    }

    /**
     * Requests content. Note: Call this superclass method after requesting content.
     */
    protected void requestContent() {
        setContentSet(false);
    }

    /**
     * Sets the change list.
     *
     * @param changeList The change list.
     */
    protected void setChangeList(List<T> changeList) {
        this.changeList = changeList;
    }

    /**
     * Sets the change set.
     *
     * @param changeSet The change set
     */
    protected void setChangeSet(Set<T> changeSet) {
        this.changeSet = changeSet;
    }

    /**
     * Sets content. Note: Call this superclass method after setting content.
     */
    protected void setContent() {
        setContentSet(true);
    }

    /**
     * Sets the content list.
     *
     * @param contentList The content list
     */
    protected void setContentList(List<T> contentList) {
        this.contentList = contentList;
    }

    /**
     * Sets whether content has been set.
     *
     * @param contentSet True if content has been set, false otherwise
     */
    private void setContentSet(boolean contentSet) {
        this.contentSet = contentSet;
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
