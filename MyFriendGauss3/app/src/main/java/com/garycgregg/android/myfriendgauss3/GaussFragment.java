package com.garycgregg.android.myfriendgauss3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Set;

public abstract class GaussFragment<T> extends Fragment implements ProblemLabSource {

    // The format of an instance argument
    protected static final String ARGUMENT_FORMAT_STRING = "%s.%s_argument";

    // The list of changes, in change order
    private List<T> changeList;

    // The set of unique changes
    private Set<T> changeSet;

    // The source of the problem lab
    private ProblemLabSource problemLabSource;

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
     * Clears the changes.
     */
    protected void clearChanges() {

        // Clear both the change list and change set.
        changeList.clear();
        changeSet.clear();
    }

    @Override
    public ProblemLab getProblemLab() {
        return (null == problemLabSource) ? null : problemLabSource.getProblemLab();
    }

    @Override
    public void onAttach(Context context) {

        /*
         * Call through to the superclass method, and if possible cast the given context to a
         * problem lab source. Save the source.
         */
        super.onAttach(context);
        problemLabSource = (context instanceof ProblemLabSource) ? ((ProblemLabSource) context) :
                null;
    }

    @Override
    public void onDetach() {

        // Clear the problem lab source, and call through to the superclass method.
        problemLabSource = null;
        super.onDetach();
    }

    /**
     * Releases the change collections.
     */
    protected void releaseCollections() {

        // Clear the changes before making the change set and the change list null.
        clearChanges();
        changeSet = null;
        changeList = null;
    }

    /**
     * Sets the change list.
     *
     * @param changeList The change list.
     */
    protected void setChangeList(@NonNull List<T> changeList) {
        this.changeList = changeList;
    }

    /**
     * Sets the change set.
     *
     * @param changeSet The change set
     */
    protected void setChangeSet(@NonNull Set<T> changeSet) {
        this.changeSet = changeSet;
    }

    /**
     * Synchronizes changes.
     */
    public abstract void synchronizeChanges();
}
