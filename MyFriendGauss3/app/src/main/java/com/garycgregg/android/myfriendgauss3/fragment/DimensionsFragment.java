package com.garycgregg.android.myfriendgauss3.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.garycgregg.android.myfriendgauss3.R;

public class DimensionsFragment extends GaussDialogFragment {

    // The prefix for instance arguments
    private static final String ARGUMENT_PREFIX = DimensionsFragment.class.getName();

    // The bundle index for the current dimensions
    private static final String CURRENT_DIMENSIONS = "current_dimensions";

    // The current dimensions argument key
    private static final String CURRENT_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, CURRENT_DIMENSIONS);

    // The bundle index for the maximum dimensions
    private static final String MAXIMUM_DIMENSIONS = "maximum_dimensions";

    // The maximum dimensions argument key
    private static final String MAXIMUM_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, MAXIMUM_DIMENSIONS);

    // The bundle index for the minimum dimensions
    private static final String MINIMUM_DIMENSIONS = "minimum_dimensions";

    // The minimum dimensions argument key
    private static final String MINIMUM_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, MINIMUM_DIMENSIONS);

    // The prefix for return values
    private static final String RETURN_PREFIX = DimensionsFragment.class.getPackage().getName();

    // The dimensions extra
    public static final String EXTRA_DIMENSIONS = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, CURRENT_DIMENSIONS);

    static {

        // Build the argument keys.
        final Bundle bundle = getArgumentKeys();
        bundle.putString(CURRENT_DIMENSIONS, CURRENT_ARGUMENT);
        bundle.putString(MAXIMUM_DIMENSIONS, MAXIMUM_ARGUMENT);
        bundle.putString(MINIMUM_DIMENSIONS, MINIMUM_ARGUMENT);
    }

    // The number picker for this dialog
    private NumberPicker dimensionsPicker;

    /**
     * Customizes an instance of a DimensionsFragment with the required argument(s).
     *
     * @param minimumPrecision The minimum dimensions
     * @param maximumPrecision The maximum dimensions
     * @param currentPrecision The current dimensions of the problem
     * @return A properly configured DimensionsFragment
     */
    public static DimensionsFragment createInstance(int minimumPrecision,
                                                    int maximumPrecision,
                                                    int currentPrecision) {

        /*
         * Create a new arguments bundle. Add the minimum precision argument and the maximum
         * dimensions argument.
         */
        final Bundle arguments = new Bundle();
        arguments.putInt(MINIMUM_ARGUMENT, minimumPrecision);
        arguments.putInt(MAXIMUM_ARGUMENT, maximumPrecision);

        // Add the current dimensions argument and create a new dimensions fragment.
        arguments.putInt(CURRENT_ARGUMENT, currentPrecision);
        final DimensionsFragment fragment = new DimensionsFragment();

        // Set the argument and return the fragment.
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void createState(Bundle keys, Bundle values) {

        // Declare the default value, and set the minimum in the dimensions picker.
        final int defaultValue = 0;
        dimensionsPicker.setMinValue(getInt(values, keys, MINIMUM_DIMENSIONS, defaultValue));

        // Set the maximum in the dimensions picker, then set its current value.
        dimensionsPicker.setMaxValue(getInt(values, keys, MAXIMUM_DIMENSIONS, defaultValue));
        dimensionsPicker.setValue(getInt(values, keys, CURRENT_DIMENSIONS, defaultValue));
    }

    /**
     * Gets a tag for logging statements.
     *
     * @return A tag for logging statements
     */
    protected String getLogTag() {
        return ARGUMENT_PREFIX;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the context, and inflate the dimensions dialog.
        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_dimensions,
                null);

        // Find the dimensions picker, and create state.
        dimensionsPicker = view.findViewById(R.id.dimensions_picker);
        createState(savedInstanceState);

        // Create an on click listener for the dialog.
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sendResult(Activity.RESULT_OK);
            }
        };

        // Build a new alert dialog, configure it, create it, and return it.
        return new AlertDialog.Builder(context)
                .setTitle(R.string.dimensions_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call the superclass method, and save the current value of the number picker.
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_DIMENSIONS, dimensionsPicker.getValue());
    }

    /**
     * Sets results to the target fragment.
     *
     * @param resultCode The result code
     * @return True if the target fragment exists to receive the results, false otherwise
     */
    private boolean sendResult(int resultCode) {

        // Get the target fragment. Is the target fragment not null?
        final Fragment targetFragment = getTargetFragment();
        final boolean result = (null != targetFragment);
        if (result) {

            /*
             * The target fragment is not null. Create a new intent to receive the dimensions.
             * Call the activity result on the target fragment with the intent.
             */
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_DIMENSIONS, dimensionsPicker.getValue());
            targetFragment.onActivityResult(getTargetRequestCode(), resultCode, intent);
        }

        // Return whether results were successfully sent.
        return result;
    }
}
