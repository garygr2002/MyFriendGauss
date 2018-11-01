package com.garycgregg.android.myfriendgauss3.fragment;

import com.garycgregg.android.myfriendgauss3.content.BaseGaussEntry;

abstract class NumberTextWatcher<T extends BaseGaussEntry> extends GaussTextWatcher<T> {

    // The number check bypass pattern
    private final String bypassPattern;

    /**
     * Constructs a number text watcher.
     *
     * @param content       The content of the watcher
     * @param bypassPattern The number check bypass pattern
     */
    NumberTextWatcher(T content, String bypassPattern) {

        // Set the member variables.
        super(content);
        this.bypassPattern = bypassPattern;
    }

    /**
     * Converts a string to a double.
     *
     * @param string A string to convert
     * @return The converted string, or null if the string could not be converted
     */
    private static Double convert(String string) {

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
     * Gets the number check bypass pattern.
     *
     * @return The number check bypass pattern
     */
    public String getBypassPattern() {
        return bypassPattern;
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
         * the bypass pattern?
         */
        boolean result = super.isChanged(candidate);
        if (result && (!getBypassPattern().matches(candidate))) {

            /*
             * The superclass thinks the result has changed *and* the candidate is does not match
             * the bypass pattern. Make sure there has been a change by checking if the number
             * representations of the candidate has also changed.
             */
            final Double convertedCandidate = convert(candidate);
            result = !((null == convertedCandidate) || convertedCandidate.equals(getEntry()));
        }

        // Return the result.
        return result;
    }
}
