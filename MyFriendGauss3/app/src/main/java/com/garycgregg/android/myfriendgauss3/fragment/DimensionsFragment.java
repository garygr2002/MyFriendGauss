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
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public class DimensionsFragment extends GaussDialogFragment {

    // The prefix for instance arguments
    private static final String ARGUMENT_PREFIX = DimensionsFragment.class.getName();

    // The position argument key
    private static final String CURRENT_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, "current");

    // The instance state index for dimensions
    private static final String DIMENSIONS_INDEX = "dimensions_index";

    // The prefix for return values
    private static final String RETURN_PREFIX = DimensionsFragment.class.getPackage().getName();

    // The dimensions extra
    public static final String EXTRA_DIMENSIONS = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, "dimensions");

    // The number picker for this dialog
    private NumberPicker numberPicker;

    /**
     * Customizes an instance of a DimensionsFragment with the required argument(s).
     *
     * @param current The current dimensions of a problem
     * @return A properly configured DimensionsFragment
     */
    public static DimensionsFragment createInstance(int current) {

        // Create a new arguments bundle, and add the current dimensions argument.
        final Bundle arguments = new Bundle();
        arguments.putInt(CURRENT_ARGUMENT, current);

        // Create a new DimensionsFragment, set the argument, and return the fragment.
        final DimensionsFragment fragment = new DimensionsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the context, and inflate the dimensions dialog.
        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_dimensions,
                null);

        // Find the dimensions picker, and set its minimum value.
        numberPicker = view.findViewById(R.id.dialog_dimensions_picker);
        numberPicker.setMinValue(ProblemLab.MIN_DIMENSIONS);

        // Set the maximum value and the current value of the dimensions picker.
        numberPicker.setMaxValue(ProblemLab.MAX_DIMENSIONS);
        numberPicker.setValue((null == savedInstanceState) ?
                getArguments().getInt(CURRENT_ARGUMENT, numberPicker.getMinValue()) :
                savedInstanceState.getInt(DIMENSIONS_INDEX, numberPicker.getMinValue()));

        // Create an on click listener for the dialog.
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sendResult(Activity.RESULT_OK, numberPicker.getValue());
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
        outState.putInt(DIMENSIONS_INDEX, numberPicker.getValue());
    }

    /**
     * Sets results to the target fragment.
     *
     * @param resultCode The result code
     * @param dimensions The newly selected dimensions
     * @return True if the target fragment exists to receive the results, false otherwise
     */
    private boolean sendResult(int resultCode, int dimensions) {

        // Get the target fragment. Is the target fragment not null?
        final Fragment targetFragment = getTargetFragment();
        final boolean result = (null != targetFragment);
        if (result) {

            /*
             * The target fragment is not null. Create a new intent to receive the dimensions.
             * Call the activity result on the target fragment with the intent.
             */
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_DIMENSIONS, dimensions);
            targetFragment.onActivityResult(getTargetRequestCode(), resultCode, intent);
        }

        // Return whether results were successfully sent.
        return result;
    }
}
