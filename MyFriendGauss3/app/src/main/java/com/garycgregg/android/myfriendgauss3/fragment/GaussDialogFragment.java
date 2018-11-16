package com.garycgregg.android.myfriendgauss3.fragment;

import android.support.v4.app.DialogFragment;
import android.util.Log;

public class GaussDialogFragment extends DialogFragment {

    // The format of an instance argument key
    protected static final String ARGUMENT_FORMAT_STRING = "%s.%s_argument";

    // The format of an instance return key
    protected static final String RETURN_FORMAT_STRING = "%s.%s_return";

    // A tag for logging statements
    private static final String TAG = GaussDialogFragment.class.getSimpleName();

    /**
     * Gets a tag for logging statements.
     *
     * @return A tag for logging statements
     */
    protected String getLogTag() {
        return TAG;
    }

    /**
     * Outputs an informational message.
     *
     * @param message The message to output
     */
    protected void output(String message) {
        Log.d(getLogTag(), String.format("%s (Object hashcode: %d)", message, hashCode()));
    }
}
