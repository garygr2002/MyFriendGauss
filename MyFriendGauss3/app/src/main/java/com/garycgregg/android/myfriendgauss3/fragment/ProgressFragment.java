package com.garycgregg.android.myfriendgauss3.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garycgregg.android.myfriendgauss3.R;

public class ProgressFragment extends GaussDialogFragment {

    /**
     * Customizes an instance of a ProgressFragment.
     *
     * @return A properly configured ProgressFragment
     */
    public static ProgressFragment createInstance() {
        return new ProgressFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the context, and inflate the fill dialog.
        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress,
                null);

        /*
         * Get the progress bar, and set its progress. TODO: Put a listener on the solution
         * service.
         */
        final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setProgress(50);

        // Get the percent solved text view, and set its value.
        final TextView percentSolved = view.findViewById(R.id.percent_solved);
        percentSolved.setText("50%");

        // Build a new alert dialog, configure it, create it, and return it.
        return new AlertDialog.Builder(context)
                .setTitle(R.string.check_title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
