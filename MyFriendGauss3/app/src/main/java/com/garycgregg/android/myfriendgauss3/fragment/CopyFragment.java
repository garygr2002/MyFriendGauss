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

    // The instance state index for the new problem name
    private static final String NEW_NAME_INDEX = "new_name_index";

    // The prefix for return values
    private static final String RETURN_PREFIX = FillFragment.class.getPackage().getName();

    // The new name extra
    public static final String EXTRA_NEW_NAME = String.format(RETURN_FORMAT_STRING,
            RETURN_PREFIX, "new_name");

    // The edit text for the new problem name
    private EditText newProblemName;

    /**
     * Customizes an instance of a CopyFragment.
     *
     * @return A properly configured CopyFragment
     */
    public static CopyFragment createInstance() {
        return new CopyFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the context, and inflate the fill dialog.
        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_copy,
                null);

        // Get the new problem name edit text. Is the saved instance state not null?
        newProblemName = view.findViewById(R.id.new_problem_name);
        if (null != savedInstanceState) {

            // The saved instance state is not null. Set the text of the new problem name.
            newProblemName.setText(savedInstanceState.getString(NEW_NAME_INDEX, ""));
        }

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
        outState.putString(NEW_NAME_INDEX, newProblemName.getText().toString());
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
