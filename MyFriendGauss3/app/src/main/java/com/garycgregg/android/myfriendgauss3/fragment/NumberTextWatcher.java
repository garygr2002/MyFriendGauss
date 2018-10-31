package com.garycgregg.android.myfriendgauss3.fragment;

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

        // Does the superclass think the result has changed?
        boolean result = super.isChanged(candidate);
        if (result) {

            /*
             * The superclass thinks the result has changed. Make sure by checking if the number
             * representations also changed. Do not let a change occur if the candidate is not a
             * number.
             */
            final Double convertedCandidate = convert(candidate);
            result = !((null == convertedCandidate) || convertedCandidate.equals(getEntry()));
        }

        // Return the result.
        return result;
    }
}
