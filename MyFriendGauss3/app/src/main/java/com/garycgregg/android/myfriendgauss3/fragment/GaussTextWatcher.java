package com.garycgregg.android.myfriendgauss3.fragment;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

abstract class GaussTextWatcher<T> implements TextWatcher {

    // A regular expression for whitespace
    private static final String WHITESPACE_PATTERN = "^\\s*$";

    // The content of the watcher
    private final T content;

    /**
     * Constructs a Gauss text watcher.
     *
     * @param content The content of the watcher
     */
    public GaussTextWatcher(T content) {
        this.content = content;
    }

    /**
     * Converts a string to a double.
     *
     * @param string A string to convert
     * @return The converted string, or null if the string could not be converted
     */
    protected static Double convert(String string) {

        // Declare the result.
        Double result;
        try {

            // Try to convert the argument.
            result = Double.parseDouble(string);
        }

        // Set the result to null if the string is not a number.
        catch (NumberFormatException exception) {
            result = null;
        }

        // Return the result
        return result;
    }

    /**
     * Determines if a string is empty, or contains only whitespace.
     *
     * @param string Any non-null string
     * @return True if the string is empty, or contains only whitespace; false otherwise
     */
    public static boolean isWhitespace(@NonNull String string) {
        return string.matches(WHITESPACE_PATTERN);
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
     * Gets the comparison string from the content.
     *
     * @return The comparison string from the content
     */
    protected abstract String getContentString();

    /**
     * Determines if a change has occurred in the content.
     *
     * @param candidate The candidate change
     * @return True if content has been changed, false otherwise
     */
    protected boolean isChanged(String candidate) {
        return !candidate.equals(getContentString());
    }

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
    protected abstract void setChange(@NonNull String change);
}
