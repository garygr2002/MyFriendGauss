package com.garycgregg.android.myfriendgauss3.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.garycgregg.android.myfriendgauss3.R;

public class SolveFragment extends GaussDialogFragment {

    /**
     * Customizes an instance of a SolveFragment.
     *
     * @return A properly configured SolveFragment
     */
    public static SolveFragment createInstance() {
        return new SolveFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the context, and inflate the fill dialog.
        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_solve,
                null);

        // Create an on click listener for the dialog.
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sendResult(Activity.RESULT_OK);
            }
        };

        // Build a new alert dialog, configure it, create it, and return it.
        return new AlertDialog.Builder(context)
                .setTitle(R.string.solve_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
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

            // The target fragment is not null. Set the activity result of the target fragment.
            targetFragment.onActivityResult(getTargetRequestCode(), resultCode, null);
        }

        // Return whether the results were successfully sent.
        return result;
    }
}
