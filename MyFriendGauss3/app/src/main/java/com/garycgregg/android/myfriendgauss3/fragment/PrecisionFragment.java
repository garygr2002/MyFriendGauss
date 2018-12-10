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
import android.widget.RadioButton;

import com.garycgregg.android.myfriendgauss3.R;

public class PrecisionFragment extends GaussDialogFragment implements View.OnClickListener {

    // The prefix for instance arguments
    private static final String ARGUMENT_PREFIX = PrecisionFragment.class.getName();

    // The bundle index for the current precision
    private static final String CURRENT_PRECISION = "current_precision";

    // The current precision argument key
    private static final String CURRENT_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, CURRENT_PRECISION);

    // The bundle index for the maximum precision
    private static final String MAXIMUM_PRECISION = "maximum_precision";

    // The maximum precision argument key
    private static final String MAXIMUM_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, MAXIMUM_PRECISION);

    // The bundle index for the minimum precision
    private static final String MINIMUM_PRECISION = "minimum_precision";

    // The minimum precision argument key
    private static final String MINIMUM_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, MINIMUM_PRECISION);

    // The prefix for return values
    private static final String RETURN_PREFIX = PrecisionFragment.class.getPackage().getName();

    // The precision extra
    public static final String EXTRA_PRECISION = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, CURRENT_PRECISION);

    // The bundle index for the scientific notation flag
    private static final String SCIENTIFIC_NOTATION = "scientific_notation";

    // The scientific notation extra
    public static final String EXTRA_SCIENTIFIC = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, SCIENTIFIC_NOTATION);

    // The current scientific notation argument key
    private static final String SCIENTIFIC_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, SCIENTIFIC_NOTATION);

    // The IDs for notation radio buttons
    private static final int[] notationIds = {R.id.decimal_notation, R.id.scientific_notation};

    static {

        // Build the argument keys.
        final Bundle bundle = getArgumentKeys();
        bundle.putString(CURRENT_PRECISION, CURRENT_ARGUMENT);
        bundle.putString(MAXIMUM_PRECISION, MAXIMUM_ARGUMENT);
        bundle.putString(MINIMUM_PRECISION, MINIMUM_ARGUMENT);
        bundle.putString(SCIENTIFIC_NOTATION, SCIENTIFIC_ARGUMENT);
    }

    // The number picker for this dialog
    private NumberPicker precisionPicker;

    // True if the output will be in scientific notation, false otherwise
    private boolean scientific;

    /**
     * Customizes an instance of a PrecisionFragment with the required argument(s).
     *
     * @param minimumPrecision  The minimum precision
     * @param maximumPrecision  The maximum precision
     * @param currentPrecision  The current precision
     * @param currentScientific The current scientific notation flag
     * @return A properly configured PrecisionFragment
     */
    public static PrecisionFragment createInstance(int minimumPrecision,
                                                   int maximumPrecision,
                                                   int currentPrecision,
                                                   boolean currentScientific) {

        /*
         * Create a new arguments bundle. Add the minimum precision argument and the maximum
         * precision argument.
         */
        final Bundle arguments = new Bundle();
        arguments.putInt(MINIMUM_ARGUMENT, minimumPrecision);
        arguments.putInt(MAXIMUM_ARGUMENT, maximumPrecision);

        // Add the current precision argument and the current scientific notation argument.
        arguments.putInt(CURRENT_ARGUMENT, currentPrecision);
        arguments.putBoolean(SCIENTIFIC_ARGUMENT, currentScientific);

        // Create a new PrecisionFragment, set the argument, and return the fragment.
        final PrecisionFragment fragment = new PrecisionFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void createState(Bundle keys, Bundle values) {

        // Declare the default value, and set the minimum in the precision picker.
        final int defaultValue = 0;
        precisionPicker.setMinValue(getInt(values, keys, MINIMUM_PRECISION, defaultValue));

        /*
         * Set the maximum in the precision picker, then set its current value. Get the
         * scientific notation flag.
         */
        precisionPicker.setMaxValue(getInt(values, keys, MAXIMUM_PRECISION, defaultValue));
        precisionPicker.setValue(getInt(values, keys, CURRENT_PRECISION, defaultValue));
        scientific = getBoolean(values, keys, SCIENTIFIC_NOTATION, false);
    }

    @Override
    protected String getLogTag() {
        return ARGUMENT_PREFIX;
    }

    @Override
    public void onClick(View view) {

        final int id = view.getId();
        switch (id) {

            case R.id.decimal_notation:

                output("Received a decimal notation push.");
                scientific = false;
                break;

            case R.id.scientific_notation:

                output("Received a scientific notation push.");
                scientific = true;
                break;

            default:

                output(String.format("Received an unexpected button push; the ID is: %d", id));
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the context, and inflate the fill dialog.
        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_precision,
                null);

        // Find the precision picker. Create the object state, and setup the radio buttons.
        precisionPicker = view.findViewById(R.id.precision_picker);
        createState(savedInstanceState);
        setupButtons(view);

        // Create an on-click listener for the dialog.
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sendResult(Activity.RESULT_OK);
            }
        };

        // Create and return a new alert dialog using the listener.
        return new AlertDialog.Builder(context)
                .setTitle(R.string.precision_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call the superclass method.
        super.onSaveInstanceState(outState);
        outState.putBoolean(SCIENTIFIC_NOTATION, scientific);
        outState.putInt(CURRENT_PRECISION, precisionPicker.getValue());
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
             * The target fragment is not null. Create a new intent to receive the extras. Put
             * in the precision extra.
             */
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_PRECISION, precisionPicker.getValue());

            /*
             * Put in the scientific notation extra, and call the activity result on the target
             * fragment with the intent.
             */
            intent.putExtra(EXTRA_SCIENTIFIC, scientific);
            targetFragment.onActivityResult(getTargetRequestCode(), resultCode, intent);
        }

        // The target fragment is null!
        else {
            output("There is no target fragment!");
        }

        // Return whether results were successfully sent.
        return result;
    }

    /**
     * Sets up the radio buttons.
     *
     * @param view The view containing the buttons
     */
    private void setupButtons(@NonNull View view) {

        /*
         * Set the correct notation selection button. Give all the buttons this dialog as a
         * listener.
         */
        setupButton((RadioButton) view.findViewById(notationIds[scientific ? 1 : 0]));
        setButtonListeners(view, notationIds, this);
    }
}
