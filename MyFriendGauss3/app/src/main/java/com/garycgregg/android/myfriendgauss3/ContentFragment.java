package com.garycgregg.android.myfriendgauss3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ContentFragment<T> extends GaussFragment<T> {

    // The problem ID argument
    private static final String PROBLEM_ID_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            ContentFragment.class.getName(), "problem_id");

    // The problem ID associated with this instance
    private long problemId = ProblemLab.NULL_ID;

    /**
     * Customizes an instance of a ContentFragment with the required argument(s).
     *
     * @param fragment  An existing ContentFragment
     * @param problemId The problem ID to be associated with the instance
     */
    public static void customizeInstance(ContentFragment<?> fragment, long problemId) {

        // Get the existing arguments, if any.
        Bundle arguments = fragment.getArguments();
        if (null == arguments) {

            // Create a new, empty arguments object if there is none already.
            arguments = new Bundle();
        }

        // Add the problem ID, and set or reset the arguments in the fragment instance.
        arguments.putLong(PROBLEM_ID_ARGUMENT, problemId);
        fragment.setArguments(arguments);
    }

    /**
     * Creates subclass content.
     *
     * @param inflater  An inflater for layouts
     * @param container A container for the subclass content
     */
    protected abstract void createControls(LayoutInflater inflater, ViewGroup container);

    /**
     * Gets the problem ID.
     *
     * @return The problem ID
     */
    protected long getProblemId() {
        return problemId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        /*
         * Call through to the superclass method. Use the saved instance state for arguments if it
         * is not null. Otherwise use the instance supplied arguments. Set the problem ID.
         */
        super.onCreate(savedInstanceState);
        final Bundle arguments = (null == savedInstanceState) ? getArguments() :
                savedInstanceState;
        problemId = arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Create a card view followed by subclass content. Return the card view.
        final CardView view = (CardView) inflater.inflate(R.layout.fragment_card, container,
                false);
        createControls(inflater, (ViewGroup) view.findViewById(R.id.card_content));
        return view;
    }

    @Override
    public void onDestroy() {

        // Clear the problem ID. Call through to the superclass method.
        problemId = ProblemLab.NULL_ID;
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call through to the superclass method, and save the problem ID.
        super.onSaveInstanceState(outState);
        outState.putLong(PROBLEM_ID_ARGUMENT, getProblemId());
    }

    /**
     * Receives content for the controls.
     *
     * @param content The received content
     */
    protected abstract void receiveContent(@NonNull T[] content);

    /**
     * Requests content for the controls.
     */
    protected abstract void requestContent();
}
