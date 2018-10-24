package com.garycgregg.android.myfriendgauss3;

import android.text.Editable;
import android.text.TextWatcher;

abstract class GaussTextWatcher<T> implements TextWatcher {

    // The content of the watcher
    private final T content;

    /**
     * Constructs a Gauss text watcher.
     *
     * @param content The content of the watcher
     */
    GaussTextWatcher(T content) {
        this.content = content;
    }

    @Override
    public void afterTextChanged(Editable editable) {

        // Nothing to do here currently.
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        // Nothing to do here currently.
    }

    /***
     * Gets the content.
     * @return The content of the watcher
     */
    protected T getContent() {
        return content;
    }

    /**
     * Determines if a content change occurred.
     *
     * @param candidate The candidate change
     * @return True if there was a content change, false otherwise
     */
    protected abstract boolean isChanged(String candidate);

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        // Is the candidate a change?
        final String candidate = charSequence.toString();
        if (isChanged(candidate)) {

            // The candidate is a change. Set it.
            setChange(candidate);
        }
    }

    /***
     * Sets a content change.
     * @param change The content change
     */
    protected abstract void setChange(String change);
}
