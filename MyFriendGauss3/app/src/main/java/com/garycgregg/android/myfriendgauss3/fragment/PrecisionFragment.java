package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;

public class PrecisionFragment extends GaussDialogFragment {

    // The prefix for instance arguments
    private static final String ARGUMENT_PREFIX = PrecisionFragment.class.getName();

    // The default precision argument key
    private static final String DEFAULT_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, "default");

    // The current precision argument key
    private static final String PRECISION_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, "current_precision");

    // The instance state index for precision
    private static final String PRECISION_INDEX = "precision_index";

    // The prefix for return values
    private static final String RETURN_PREFIX = PrecisionFragment.class.getPackage().getName();

    // The precision extra
    public static final String EXTRA_PRECISION = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, "precision");

    // The scientific notation extra
    public static final String EXTRA_SCIENTIFIC = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, "scientific");

    // The current scientific notation argument key
    private static final String SCIENTIFIC_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, "current_scientific");

    // The instance state index for scientific notation
    private static final String SCIENTIFIC_INDEX = "scientific_index";

    // A tag for logging statements
    private static final String TAG = PrecisionFragment.class.getSimpleName();

    /**
     * Customizes an instance of a PrecisionFragment with the required argument(s).
     *
     * @param currentPrecision  The current precision
     * @param currentScientific The current scientific notation flag
     * @return A properly configured PrecisionFragment
     */
    public static PrecisionFragment createInstance(int currentPrecision,
                                                   boolean currentScientific) {

        /*
         * Create a new arguments bundle. Add the current precision argument and current scientific
         * notation argument.
         */
        final Bundle arguments = new Bundle();
        arguments.putInt(PRECISION_ARGUMENT, currentPrecision);
        arguments.putBoolean(SCIENTIFIC_ARGUMENT, currentScientific);

        // Create a new PrecisionFragment, set the argument, and return the fragment.
        final PrecisionFragment fragment = new PrecisionFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call the superclass method. TODO: Get current values from widgets.
        super.onSaveInstanceState(outState);
        outState.putBoolean(SCIENTIFIC_INDEX, false);
        outState.putInt(PRECISION_INDEX, 0);
    }
}
