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

import com.garycgregg.android.myfriendgauss3.R;

public class CopyFragment extends GaussDialogFragment {

    // The prefix for instance arguments
    private static final String ARGUMENT_PREFIX = CopyFragment.class.getName();

    // The bundle index for the new problem name
    private static final String NEW_NAME = "new_name";

    // The name argument key
    private static final String NAME_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ARGUMENT_PREFIX, NEW_NAME);

    // The prefix for return values
    private static final String RETURN_PREFIX = CopyFragment.class.getPackage().getName();

    // The new name extra
    public static final String EXTRA_NEW_NAME = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, NEW_NAME);

    static {
        getArgumentKeys().putString(NEW_NAME, NAME_ARGUMENT);
    }

    // The edit text for the new problem name
    private EditText newProblemName;

    /**
     * Customizes an instance of a CopyFragment.
     *
     * @return A properly configured CopyFragment
     */
    public static CopyFragment createInstance(String existingName) {

        // Create a new arguments bundle and add the name argument.
        final Bundle arguments = new Bundle();
        arguments.putString(NAME_ARGUMENT, existingName);

        // Create a new CopyFragment, set the argument, and return the fragment.
        final CopyFragment fragment = new CopyFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void createState(@NonNull Bundle keys, @NonNull Bundle values) {
        newProblemName.setText(getString(values, keys, NEW_NAME, ""));
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

        // Get the context, and inflate the fill dialog.
        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_copy,
                null);

        // Get the new problem name edit text, and create state.
        newProblemName = view.findViewById(R.id.new_problem_name);
        createState(savedInstanceState);

        // Create an on-click listener for the dialog.
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sendResult(Activity.RESULT_OK);
            }
        };

        // Build a new alert dialog, configure it, create it, and return it.
        return new AlertDialog.Builder(context)
                .setTitle(R.string.copy_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call the superclass method, and save the text of the new problem name.
        super.onSaveInstanceState(outState);
        outState.putString(NEW_NAME, newProblemName.getText().toString());
    }

    /**
     * Sets results to the target fragment.
     *
     * @param resultCode The result code
     * @return True if the target fragment exists to receive the results, false otherwise
     */
    private boolean sendResult(int resultCode) {

        // Is the target fragment not null?
        final Fragment targetFragment = getTargetFragment();
        final boolean result = (null != targetFragment);
        if (result) {

            /*
             * The target fragment is not null. Create a new intent to receive the extras. Put
             * in the new name extra. Set the activity result of the target fragment using the
             * intent.
             */
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_NEW_NAME, newProblemName.getText().toString());
            targetFragment.onActivityResult(getTargetRequestCode(), resultCode, intent);
        }

        // Return whether the results were successfully sent.
        return result;
    }
}
