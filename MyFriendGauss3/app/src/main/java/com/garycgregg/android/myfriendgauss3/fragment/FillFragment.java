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

    // The instance state index for all entries
    private static final String ALL_ENTRIES_INDEX = "all_entries_index";

    // The instance state index for fill value
    private static final String FILL_VALUE_INDEX = "fill_value_index";

    // The instance state index for pane choice
    private static final String PANE_CHOICE_INDEX = "pane_choice_index";

    // The prefix for return values
    private static final String RETURN_PREFIX = FillFragment.class.getPackage().getName();

    // The fill value extra
    public static final String EXTRA_FILL = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, "fill");

    // The pane indicator extra
    public static final String EXTRA_PANE = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, "pane");

    // The all entries flag
    public static final String EXTRA_ALL_ENTRIES = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, "all");

    // A tag for logging statements
    private static final String TAG = FillFragment.class.getSimpleName();

    // The IDs for all-entry radio buttons
    private static final int[] entryIds = {R.id.empty_cells, R.id.all_cells};

    // The IDs for pane radio buttons
    private static final int[] paneIds = {R.id.matrix_only, R.id.vector_only,
            R.id.both_matrix_vector};

    // The all-entries flag
    private boolean allEntries;

    // The fill value
    private Container<Double> fillValue;

    // The pane choice
    private PaneChoice paneChoice;

    /**
     * Customizes an instance of a FillFragment.
     *
     * @return A properly configured FillFragment
     */
    public static FillFragment createInstance() {
        return new FillFragment();
    }

    /**
     * Creates object state when a saved instance state bundle exists.
     *
     * @param savedInstanceState The saved instance state
     */
    private void createState(@NonNull Bundle savedInstanceState) {

        // Set the fill value and the pane choice.
        fillValue = new Container<>(savedInstanceState.getDouble(FILL_VALUE_INDEX));
        paneChoice = (PaneChoice) savedInstanceState.getSerializable(PANE_CHOICE_INDEX);

        // Give the pane choice a default setting if it is null.
        if (null == paneChoice) {
            paneChoice = PaneChoice.BOTH;
        }

        // Get the all-entries flag.
        allEntries = savedInstanceState.getBoolean(ALL_ENTRIES_INDEX, false);
    }

    /**
     * Gets a tag for logging statements.
     *
     * @return A tag for logging statements
     */
    protected String getLogTag() {
        return TAG;
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

        // Create object state, and setup the radio buttons.
        createState(null == savedInstanceState ? new Bundle() : savedInstanceState);
        setupButtons(view);

        // Find the fill entry, and give it a text changed listener.
        ((EditText) view.findViewById(R.id.fill_entry))
                .addTextChangedListener(new GaussTextWatcher<Container<Double>>(fillValue) {

                    @Override
                    protected String getContentString() {

                        // Return a zero-length string if the content is null.
                        final Double value = getContent().getObject();
                        return (null == value) ? "" : value.toString();
                    }

                    @Override
                    protected void setChange(@NonNull String change) {
                        getContent().setObject(GaussTextWatcher.isWhitespace(change) ? null :
                                Double.parseDouble(change));
                    }
                });

        // Create an on click listener for the dialog.
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sendResult(Activity.RESULT_OK, fillValue.getObject(), paneChoice, allEntries);
            }
        };

        // Build a new alert dialog, configure it, create it, and return it.
        return new AlertDialog.Builder(context).setTitle(R.string.fill_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        /*
         * Call the superclass method. Write the all-entries flag and the pane choice to the
         * out bundle.
         */
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALL_ENTRIES_INDEX, allEntries);
        outState.putSerializable(PANE_CHOICE_INDEX, paneChoice);

        // Get the fill value. Is the fill value not null?
        final Double value = fillValue.getObject();
        if (null != value) {

            // The fill value is not null. Write the fill value to the out bundle.
            outState.putDouble(FILL_VALUE_INDEX, value);
        }
    }

    /**
     * Sets results to the target fragment.
     *
     * @param resultCode The result code
     * @param fill       The fill value to use
     * @param pane       The pane indicator
     * @param allEntries True to fill all entries (even those with existing settings), false
     *                   otherwise
     * @return True if the fill value is not null and the target fragment exists to receive the
     * results, false otherwise
     */
    private boolean sendResult(int resultCode, Double fill, @NonNull PaneChoice pane,
                               boolean allEntries) {

        // Is the fill value not null?
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
                intent.putExtra(EXTRA_PANE, pane);

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
     * Sets this dialog as an on-click listener for each radio button.
     *
     * @param view      The view containing all the radio buttons
     * @param buttonIds The button IDs for which to set the listener
     */
    private void setButtonListeners(@NonNull View view, int[] buttonIds) {

        // Declare a button variable, and cycle for each of the known buttons.
        RadioButton button;
        for (int buttonId : buttonIds) {

            // Try to find the first/next button. Is there no such button?
            button = view.findViewById(buttonId);
            if (null == button) {

                // There is no such button.
                output(String.format("I cannot find a button for which I need to set a " +
                        "listener; the ID is: %d", buttonId));
            }

            // Found the required button. Set this dialog as a listener.
            else {
                button.setOnClickListener(this);
            }
        }
    }

    /**
     * Checks a radio button.
     *
     * @param button The radio button to check
     * @param id     The ID of the button that is being checked
     */
    private void setupButton(RadioButton button, int id) {

        // Is the button null?
        if (null == button) {

            // Sorry, the button is null.
            output(String.format("Could not find a pane button whose value I need to set; " +
                    "the ID is: %d", id));
        }

        // The button is not null. Check it.
        else {
            button.setChecked(true);
        }
    }

    /**
     * Sets up the radio buttons.
     *
     * @param view The view containing the buttons
     */
    private void setupButtons(@NonNull View view) {

        // Set the correct pane ID button.
        int id = paneIds[paneChoice.ordinal()];
        setupButton((RadioButton) view.findViewById(id), id);

        // Set the correct entry selection button.
        id = entryIds[allEntries ? 1 : 0];
        setupButton((RadioButton) view.findViewById(id), id);

        // Give all the buttons this dialog as a listener.
        setButtonListeners(view, paneIds);
        setButtonListeners(view, entryIds);
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
