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
import android.widget.EditText;
import android.widget.RadioButton;

import com.garycgregg.android.myfriendgauss3.R;

public class FillFragment extends GaussDialogFragment implements View.OnClickListener {

    // The bundle index for the 'all entries' flag
    private static final String ALL_ENTRIES = "all_entries";

    // The prefix for instance arguments
    private static final String ARGUMENT_PREFIX = FillFragment.class.getName();

    // The 'all entries' flag argument key
    private static final String ALL_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, ALL_ENTRIES);

    // The bundle index for the the fill value
    private static final String FILL_VALUE = "fill_value";

    // The fill value argument key
    private static final String FILL_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, FILL_VALUE);

    // The bundle index for the pane choice
    private static final String PANE_CHOICE = "pane_choice";

    // The pane choice argument key
    private static final String PANE_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, PANE_CHOICE);

    // The prefix for return values
    private static final String RETURN_PREFIX = FillFragment.class.getPackage().getName();

    // The fill value extra
    public static final String EXTRA_FILL = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, FILL_VALUE);

    // The pane indicator extra
    public static final String EXTRA_PANE = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, PANE_CHOICE);

    // The all entries flag
    public static final String EXTRA_ALL_ENTRIES = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, ALL_ENTRIES);

    // The IDs for all-entry radio buttons
    private static final int[] entryIds = {R.id.empty_cells, R.id.all_cells};

    // The IDs for pane radio buttons
    private static final int[] paneIds = {R.id.matrix_only, R.id.vector_only,
            R.id.both_matrix_vector};

    static {

        // Build the argument keys.
        Bundle bundle = getArgumentKeys();
        bundle.putString(ALL_ENTRIES, ALL_ARGUMENT);
        bundle.putString(FILL_VALUE, FILL_ARGUMENT);
        bundle.putString(PANE_CHOICE, PANE_ARGUMENT);
    }

    // The all-entries flag
    private boolean allEntries;

    // The fill edit text for this dialog
    private EditText fillEditText;

    // The fill value
    private Container<Double> fillValue;

    // The pane choice
    private PaneChoice paneChoice;

    /**
     * Customizes an instance of a FillFragment.
     *
     * @param fillValue  The value to pre-populate in the fill edit text (null for no
     *                   pre-filled value)
     * @param paneChoice The pane choice to check initially
     * @param allEntries The 'all entries' initial selection
     * @return A properly configured FillFragment
     */
    public static FillFragment createInstance(Double fillValue, PaneChoice paneChoice,
                                              boolean allEntries) {

        // Create a new arguments bundle and add the fill argument.
        final Bundle arguments = new Bundle();
        arguments.putSerializable(FILL_ARGUMENT, fillValue);

        // Add the pane argument and the 'all entries' flag.
        arguments.putSerializable(PANE_ARGUMENT, paneChoice);
        arguments.putBoolean(ALL_ARGUMENT, allEntries);

        // Create a new FillFragment, set the argument and return the fragment.
        final FillFragment fragment = new FillFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void createState(Bundle keys, Bundle values) {

        // Set the fill value. Is the fill value not null?
        fillValue = new Container<>((Double) getSerializable(values, keys, FILL_VALUE));
        final Double value = fillValue.getObject();
        if (null != value) {

            // The fill value is not null. Set the value in the fill edit text.
            fillEditText.setText(Double.toString(value));
        }

        // Get the pane choice. Is the pane choice null?
        paneChoice = (PaneChoice) getSerializable(values, keys, PANE_CHOICE);
        if (null == paneChoice) {

            // The pane choice is null. Use 'both' as a default.
            paneChoice = PaneChoice.BOTH;
        }

        // Get the 'all entries' flag.
        allEntries = getBoolean(values, keys, ALL_ENTRIES, false);
    }

    /**
     * Gets a tag for logging statements.
     *
     * @return A tag for logging statements
     */
    protected String getLogTag() {
        return ARGUMENT_PREFIX;
    }

    /**
     * Determines if there is a fill value.
     *
     * @return True if there is a fill value, false otherwise
     */
    private boolean hasFillValue() {
        return (null != fillValue) && (null != fillValue.getObject());
    }

    @Override
    public void onClick(@NonNull View view) {

        final int id = view.getId();
        switch (id) {

            case R.id.all_cells:

                output("Received an all-cells push.");
                allEntries = true;
                break;

            case R.id.both_matrix_vector:

                output("Received both matrix & vector panes push.");
                paneChoice = PaneChoice.VECTOR;
                break;

            case R.id.empty_cells:

                output("Received an empty cells push.");
                allEntries = false;
                break;

            case R.id.matrix_only:

                output("Received a matrix pane push.");
                paneChoice = PaneChoice.MATRIX;
                break;

            case R.id.vector_only:

                output("Received a vector pane push.");
                paneChoice = PaneChoice.BOTH;
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
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_fill,
                null);

        // Find the fill edit text. Create the object state, and setup the radio buttons.
        fillEditText = view.findViewById(R.id.fill_entry);
        createState(savedInstanceState);
        setupButtons(view);

        // Create object state, and setup the radio buttons.
        createState(savedInstanceState);
        setupButtons(view);

        // Create an on-click listener for the dialog.
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sendResult(Activity.RESULT_OK);
            }
        };

        // Create a new alert dialog using the listener.
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.fill_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        // Give the dialog a show listener that sets the initial state of the ok button.
        dialog.setOnShowListener(new Dialog.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                ((AlertDialog) dialogInterface)
                        .getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(hasFillValue());
            }
        });

        // Give the fill edit text a text changed listener.
        fillEditText.addTextChangedListener(new GaussTextWatcher<Container<Double>>(fillValue) {

            @Override
            protected String getContentString() {

                // Return a zero-length string if the content is null.
                final Double value = getContent().getObject();
                return (null == value) ? "" : value.toString();
            }

            @Override
            protected void setChange(@NonNull String change) {

                /*
                 * Set the content depending on the change string. Enable or disable the
                 * ok button depending on whether there is a fill value.
                 */
                getContent().setObject(GaussTextWatcher.convert(change));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(hasFillValue());
            }
        });

        // Return the dialog.
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        /*
         * Call the superclass method. Write the all-entries flag and the pane choice to the
         * out bundle.
         */
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALL_ENTRIES, allEntries);
        outState.putSerializable(PANE_CHOICE, paneChoice);

        // Get the fill value. Is the fill value not null?
        final Double value = fillValue.getObject();
        if (null != value) {

            // The fill value is not null. Write the fill value to the out bundle.
            outState.putDouble(FILL_VALUE, value);
        }
    }

    /**
     * Sets results to the target fragment.
     *
     * @param resultCode The result code
     * @return True if the fill value is not null and the target fragment exists to receive the
     * results, false otherwise
     */
    private boolean sendResult(int resultCode) {

        // Is the fill value not null?
        final Double fill = fillValue.getObject();
        boolean result = (null != fill);
        if (result) {

            /*
             * The fill value is not null. Get the target fragment. Is the target fragment not
             * null?
             */
            final Fragment targetFragment = getTargetFragment();
            result = (null != targetFragment);
            if (result) {

                /*
                 * The target fragment is not null. Create a new intent to receive the extras. Put
                 * in the fill value extra and the pane extra.
                 */
                final Intent intent = new Intent();
                intent.putExtra(EXTRA_FILL, fill);
                intent.putExtra(EXTRA_PANE, paneChoice);

                /*
                 * Put in the all entries flag, and call the activity result on the target fragment
                 * with the intent.
                 */
                intent.putExtra(EXTRA_ALL_ENTRIES, allEntries);
                targetFragment.onActivityResult(getTargetRequestCode(), resultCode, intent);
            }

            // The target fragment is null!
            else {
                output("There is no target fragment!");
            }
        }

        // The fill value is null!
        else {
            output("The fill value is null!");
        }

        // Return whether the results were successfully sent.
        return result;
    }

    /**
     * Sets up the radio buttons.
     *
     * @param view The view containing the buttons
     */
    private void setupButtons(@NonNull View view) {

        // Set the correct pane ID and entry selection button.
        setupButton((RadioButton) view.findViewById(paneIds[paneChoice.ordinal()]));
        setupButton((RadioButton) view.findViewById(entryIds[allEntries ? 1 : 0]));

        // Give all the buttons this dialog as a listener.
        setButtonListeners(view, paneIds, this);
        setButtonListeners(view, entryIds, this);
    }

    public enum PaneChoice {

        // Fill entries only in the matrix pane
        MATRIX,

        // Fill entries only in the vector pane
        VECTOR,

        // Fill entries in both the matrix and vector panes
        BOTH
    }

    private static class Container<T> {

        // The contained object
        private T object;

        /**
         * Constructs the container.
         *
         * @param object The object to be contained
         */
        public Container(T object) {
            setObject(object);
        }

        /**
         * Gets the contained object.
         *
         * @return The contained object
         */
        public T getObject() {
            return object;
        }

        /**
         * Sets the contained object.
         *
         * @param object The contained object
         */
        public void setObject(T object) {
            this.object = object;
        }
    }
}
