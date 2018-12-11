package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import java.io.Serializable;

public class GaussDialogFragment extends DialogFragment {

    // The format of an instance argument key
    protected static final String ARGUMENT_FORMAT_STRING = "%s.%s_argument";

    // The format of an instance return key
    protected static final String RETURN_FORMAT_STRING = "%s.%s_return";

    // A tag for logging statements
    private static final String TAG = GaussDialogFragment.class.getSimpleName();

    // The bundle for argument keys
    private static final Bundle argumentKeys = new Bundle();

    // The bundle for saved instance state keys
    private static final Bundle savedInstanceStateKeys = new Bundle();

    /**
     * Gets the bundle for argument keys.
     *
     * @return The bundle for argument keys
     */
    protected static Bundle getArgumentKeys() {
        return argumentKeys;
    }

    /**
     * Gets a boolean from a values bundle.
     *
     * @param values       The values bundle
     * @param keys         A keys bundle containing a key indirection
     * @param key          The key to use for lookup in the keys bundle
     * @param defaultValue The default value for return
     * @return A boolean from the values bundle, or the default value
     */
    protected static boolean getBoolean(Bundle values, Bundle keys, String key,
                                        boolean defaultValue) {
        return values.getBoolean(keys.getString(key, key), defaultValue);
    }

    /**
     * Gets a double from a values bundle.
     *
     * @param values       The values bundle
     * @param keys         A keys bundle containing a key indirection
     * @param key          The key to use for lookup in the keys bundle
     * @param defaultValue The default value for return
     * @return A double from the values bundle, or the default value
     */
    protected static double getDouble(Bundle values, Bundle keys, String key,
                                      double defaultValue) {
        return values.getDouble(keys.getString(key, key), defaultValue);
    }

    /**
     * Gets an integer from a values bundle.
     *
     * @param values       The values bundle
     * @param keys         A keys bundle containing a key indirection
     * @param key          The key to use for lookup in the keys bundle
     * @param defaultValue The default value for return
     * @return An integer from the values bundle, or the default value
     */
    protected static int getInt(Bundle values, Bundle keys, String key,
                                int defaultValue) {
        return values.getInt(keys.getString(key, key), defaultValue);
    }

    /**
     * Gets the bundle for saved instance state keys.
     *
     * @return The bundle for saved instance state keys
     */
    protected static Bundle getSavedInstanceStateKeys() {
        return savedInstanceStateKeys;
    }

    /**
     * Gets a serializable from a values bundle.
     *
     * @param values The values bundle
     * @param keys   A keys bundle containing a key indirection
     * @param key    The key to use for lookup in the keys bundle
     * @return A serializable from the values bundle, or null
     */
    protected static Serializable getSerializable(Bundle values, Bundle keys, String key) {
        return values.getSerializable(keys.getString(key, key));
    }

    /**
     * Gets a string from a values bundle.
     *
     * @param values       The values bundle
     * @param keys         A keys bundle containing a key indirection
     * @param key          The key to use for lookup in the keys bundle
     * @param defaultValue The default value for return
     * @return A string from the values bundle, or the default value
     */
    protected static String getString(Bundle values, Bundle keys, String key,
                                      String defaultValue) {
        return values.getString(keys.getString(key, key), defaultValue);
    }

    /**
     * Sets a listener for an array of radio buttons
     *
     * @param view      The view containing all the radio buttons
     * @param buttonIds The button IDs for which to set the listener
     * @param listener  A listener to set for each button
     */
    protected static void setButtonListeners(@NonNull View view, int[] buttonIds,
                                             View.OnClickListener listener) {

        // Declare a button variable, and cycle for each of the known buttons.
        RadioButton button;
        for (int buttonId : buttonIds) {

            // Try to find the first/next button. Is there such a button?
            button = view.findViewById(buttonId);
            if (null != button) {

                // There is such a button. Set the listener.
                button.setOnClickListener(listener);
            }
        }
    }

    /**
     * Checks a radio button.
     *
     * @param button The radio button to check
     */
    protected static void setupButton(RadioButton button) {

        // Check the button if it is not null.
        if (null != button) {
            button.setChecked(true);
        }
    }

    /**
     * Creates state for the dialog.
     *
     * @param savedInstanceState A saved instance state bundle, if any
     */
    protected void createState(Bundle savedInstanceState) {

        // Create state using the arguments if the saved instance state is null...
        if (null == savedInstanceState) {
            createState(getArgumentKeys(), getArguments());

        }

        // ...otherwise create state using the saved instance state.
        else {
            createState(getSavedInstanceStateKeys(), savedInstanceState);
        }
    }

    /**
     * Creates state for the dialog.
     *
     * @param keys   A bundle with keys
     * @param values A bundle with values
     */
    protected void createState(Bundle keys, Bundle values) {

        // The default is to create no state.
    }

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
