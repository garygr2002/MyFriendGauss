package com.garycgregg.android.myfriendgauss3.fragment;

import android.support.annotation.NonNull;

import com.garycgregg.android.myfriendgauss3.content.BaseGaussEntry;

abstract class NumberTextWatcher<T extends BaseGaussEntry> extends GaussTextWatcher<T> {

    /**
     * Constructs a number text watcher.
     *
     * @param content The content of the watcher
     */
    NumberTextWatcher(T content) {
        super(content);
    }

    @Override
    protected String getContentString() {
        return Double.toString(getEntry());
    }

    /**
     * Gets the entry from the content.
     *
     * @return The entry from the content
     */
    private double getEntry() {
        return getContent().getEntry();
    }

    @Override
    protected boolean isChanged(String candidate) {

        /*
         * Does the superclass think the result has changed? If so, does the candidate not match
         * whitespace?
         */
        boolean result = super.isChanged(candidate);
        if (result && (!isWhitespace(candidate))) {

            /*
             * The superclass thinks the result has changed *and* the candidate does not match
             * whitespace. Make sure there has been a change by checking if the number
             * representations of the candidate has also changed.
             */
            final Double convertedCandidate = convert(candidate);
            result = !((null == convertedCandidate) || convertedCandidate.equals(getEntry()));
        }

        // Return the result.
        return result;
    }

    /**
     * Sets a double precision content change.
     *
     * @param change The double precision content change; null if the change is a deletion
     */
    protected abstract void setChange(Double change);

    @Override
    protected final void setChange(@NonNull String change) {
        setChange(convert(change));
    }
}
