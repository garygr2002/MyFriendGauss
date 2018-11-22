package com.garycgregg.android.myfriendgauss3.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import java.util.List;

public class ProblemFragment extends GaussFragment implements NumbersFragment.CountListener {

    // The copy problem dialog identifier
    private static final String DIALOG_COPY = "DialogCopy";

    // The dimensions dialog identifier
    private static final String DIALOG_DIMENSIONS = "DialogDimensions";

    // The fill dialog identifier
    private static final String DIALOG_FILL = "DialogFill";

    // The solve dialog identifier
    private static final String DIALOG_SOLVE = "DialogSolve";

    // An illegal position
    private static final int ILLEGAL_POSITION = ~0;

    // The instance state index for position
    private static final String POSITION_INDEX = "position";

    // The prefix for instance arguments
    private static final String PREFIX_STRING = ProblemFragment.class.getName();

    // The position argument key
    private static final String POSITION_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, POSITION_INDEX);

    // The instance state index for problem ID
    private static final String PROBLEM_ID_INDEX = "problem_id";

    // The problem ID argument key
    private static final String PROBLEM_ID_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, PROBLEM_ID_INDEX);

    // The identifier for a copy problem request
    private static final int REQUEST_COPY = 0;

    // The identifier for a dimensions request
    private static final int REQUEST_DIMENSIONS = 1;

    // The identifier for a fill request
    private static final int REQUEST_FILL = 2;

    // The identifier for a solve request
    private static final int REQUEST_SOLVE = 3;

    // A tag for logging statements
    private static final String TAG = ProblemFragment.class.getSimpleName();

    // A factory for answer fragments
    private final NumbersFragmentFactory answerFragmentFactory = new AnswerFragmentFactory();

    // A container for pane characteristics
    private final SparseArray<PaneCharacteristics> characteristicsArray = new SparseArray<>();

    // Our content producer
    private final ProblemContentProducer contentProducer = new ProblemContentProducer();

    // A factory for control fragments
    private final ControlFragmentFactory controlFragmentFactory = new ControlFragmentFactory();

    // A factory for matrix fragments
    private final NumbersFragmentFactory matrixFragmentFactory = new MatrixFragmentFactory();

    // A factory for vector fragments
    private final NumbersFragmentFactory vectorFragmentFactory = new VectorFragmentFactory();

    // Our callback listener
    private Callbacks callbackListener;

    // True if all the entries in the matrix pane is full, false otherwise
    private boolean matrixPaneFull;

    // The options menu
    private Menu menu;

    // The position of this instance
    private int position = ILLEGAL_POSITION;

    // The problem
    private Problem problem;

    // The problem ID associated with this instance
    private long problemId = ProblemLab.NULL_ID;

    // True if all entries in the vector pane is full, false otherwise
    private boolean vectorPaneFull;

    /**
     * Constructs the problem fragment.
     */
    public ProblemFragment() {

        // Configure the characteristics. Configure the immutable features of the entry panes.
        configureCharacteristics();
        configureEntryPaneFactoriesImmutable();
    }

    /**
     * Configures a fragment factory.
     *
     * @param factory The fragment factory
     * @param enabled True if the fragment is enabled, false otherwise
     */
    private static void configureFactory(FragmentFactory factory, boolean enabled) {
        factory.setEnabled(enabled);
    }

    /**
     * Copies the state of a problem from a source to a destination. Only updates the fields that
     * this class does not explicitly modify.
     *
     * @param destination The destination problem
     * @param source      The source problem
     */
    private static void copyProblemState(@NonNull Problem destination,
                                         @NonNull Problem source) {

        /*
         * Note: This class works with problem dimensions and the write lock. It considers invalid
         * any changes made to these fields from another source (namely, from the control
         * fragment).
         */
        destination.setProblemId(source.getProblemId());
        destination.setName(source.getName());

        // Copy the solved and created dates.
        destination.setSolved(source.getSolved());
        destination.setCreated(source.getCreated());
    }

    /**
     * Creates an instance of a ProblemFragment with the required argument(s).
     *
     * @param problemId The problem ID to be associated with the instance
     * @param position  The position of this instance
     * @return An instance of a ProblemFragment
     */
    public static ProblemFragment createInstance(long problemId, int position) {

        // Create an instance of a ProblemFragment, and give it some arguments.
        final ProblemFragment fragment = new ProblemFragment();
        final Bundle arguments = new Bundle();
        fragment.setArguments(arguments);

        // Set the problem ID and the position in the arguments, and return the fragment.
        arguments.putLong(PROBLEM_ID_ARGUMENT, problemId);
        arguments.putInt(POSITION_ARGUMENT, position);
        return fragment;
    }

    /**
     * Gets the first element from a list, if any.
     *
     * @param list The list to examine
     * @param <T>  The type of element to return
     * @return The first element from the list, if any
     */
    private static <T> T getFirst(List<? extends T> list) {

        /*
         * Declare and initialize the return value, and the value of the first index. Does the list
         * have at least one element?
         */
        T returnValue = null;
        final int firstIndex = 0;
        if ((null != list) && (firstIndex < list.size())) {

            // The list has at least one element. Get the first.
            returnValue = list.get(firstIndex);
        }

        // Return the element, or null.
        return returnValue;
    }

    /**
     * Uses a fragment factory to add a fragment for a given ID.
     *
     * @param paneId  The ID for which to add a fragment
     * @param factory A factory for generating a fragment
     */
    private void addFragment(int paneId, FragmentFactory factory) {

        /*
         * Try to find an existing fragment for the given ID. Is there no such existing
         * fragment?
         */
        final FragmentManager manager = getChildFragmentManager();
        final Fragment fragment = manager.findFragmentById(paneId);
        if (null == fragment) {

            // There is no such existing fragment. Create one using the given factory.
            manager.beginTransaction().add(paneId, factory.createFragment(problemId)).commit();
        }
    }

    /**
     * Determines the answer fragment contains missing entries.
     *
     * @return True if the answer fragment contains missing entries, false otherwise
     */
    private boolean areAnswerEntriesMissing() {
        return areEntriesMissing(R.id.answer_pane);
    }

    /**
     * Determines if a pane in the problem fragment contains missing entries.
     *
     * @param id The ID of the pane
     * @return True if the identified pane contains missing entries, false otherwise
     */
    private boolean areEntriesMissing(int id) {

        // Make sure the caller did not give us a bum fragment ID.
        final NumbersFragment<?> fragment = ((NumbersFragment<?>)
                getChildFragmentManager().findFragmentById(id));
        return (null == fragment) || fragment.areEntriesMissing();
    }

    /**
     * Determines the matrix fragment contains missing entries.
     *
     * @return True if the matrix fragment contains missing entries, false otherwise
     */
    private boolean areMatrixEntriesMissing() {
        return areEntriesMissing(R.id.matrix_pane);
    }

    /**
     * Determines the vector fragment contains missing entries.
     *
     * @return True if the vector fragment contains missing entries, false otherwise
     */
    private boolean areVectorEntriesMissing() {
        return areEntriesMissing(R.id.vector_pane);
    }

    /**
     * Checks to make sure the problem is not locked, and throws a runtime exception if it is.
     */
    private void checkProblemNotLocked() {

        // Is the problem write locked? It should not be.
        if ((null != problem) && problem.isWriteLocked()) {

            // The problem is write locked. Throw an illegal state exception.
            throw new IllegalStateException("Attempting to make a problem change " +
                    "although the problem has been locked by someone else!");
        }
    }

    /**
     * Configures the characteristics of the entry panes.
     */
    private void configureCharacteristics() {

        /*
         * Declare and initialize the default color for the entry pane fragments. Set the
         * characteristics of the matrix pane.
         */
        final int defaultColor = Color.WHITE;
        characteristicsArray.put(R.id.matrix_pane, new PaneCharacteristics("Matrix\nEntries",
                true, true));

        // Set the characteristics for the answer pane.
        characteristicsArray.put(R.id.answer_pane, new PaneCharacteristics("Answers\n",
                false, false));

        // Set the characteristics for the vector pane.
        characteristicsArray.put(R.id.vector_pane, new PaneCharacteristics("Vector\nEntries",
                true, false));
    }

    /**
     * Configures the characteristics of the control pane factory.
     */
    private void configureControlPaneFactory() {

        // Enable or disable the control fragment factory, and set its problem.
        configureFactory(controlFragmentFactory, !isProblemWriteLocked());
        controlFragmentFactory.setProblem(problem);
    }

    /**
     * Configures a numbers fragment factory with characteristics for a given pane ID.
     *
     * @param factory The numbers fragment factory
     * @param paneId  The pane ID to use for configuration
     */
    private void configureEntryPane(NumbersFragmentFactory factory, int paneId) {

        /*
         * We need pane characteristics for the given ID. Try to find any published
         * characteristics. Are there any?
         */
        final PaneCharacteristics paneCharacteristics = characteristicsArray.get(paneId);
        if (null != paneCharacteristics) {

            /*
             * There are existing pane characteristics for the given ID. Set the label,
             * enabled status and matrix status in the fragment factory from the pane
             * characteristics.
             */
            factory.setLabel(paneCharacteristics.getLabel());
            factory.setEnabled(paneCharacteristics.isEnabled());
            factory.setMatrix(paneCharacteristics.isMatrix());
        }
    }

    /**
     * Configures the immutable characteristics of the entry pane factories.
     */
    private void configureEntryPaneFactoriesImmutable() {

        /*
         * All answer fragments are always disabled. Configure other immutable characteristics of
         * the answer fragment factory.
         */
        configureFactory(answerFragmentFactory, false);
        configureEntryPane(answerFragmentFactory, R.id.answer_pane);

        // Configure the immutable characteristics of the matrix and vector fragment factories.
        configureEntryPane(matrixFragmentFactory, R.id.matrix_pane);
        configureEntryPane(vectorFragmentFactory, R.id.vector_pane);
    }

    /**
     * Configures the mutable characteristics of the entry pane factories.
     */
    private void configureEntryPaneFactoriesMutable() {

        // Enable or disable the matrix and vector fragment factories.
        final boolean enabled = !(isProblemSolved() || isProblemWriteLocked());
        configureFactory(matrixFragmentFactory, enabled);
        configureFactory(vectorFragmentFactory, enabled);

        // Get the resources. Set the background color of the answer pane.
        final Resources resources = getResources();
        answerFragmentFactory.setBackgroundColor(resources.getColor(R.color.tableEntryAnswer));

        // Set the background colors of the matrix and vector panes.
        matrixFragmentFactory.setBackgroundColor(resources.getColor(R.color.tableEntryMatrix));
        vectorFragmentFactory.setBackgroundColor(resources.getColor(R.color.tableEntryVector));

        // Get the dimensions of the problem. Set that size in the answer fragment factory.
        final int dimensions = problem.getDimensions();
        answerFragmentFactory.setSize(dimensions);

        // Set the problem size in the matrix and vector fragment factories.
        matrixFragmentFactory.setSize(dimensions);
        vectorFragmentFactory.setSize(dimensions);
    }

    /**
     * Creates object state from fragment arguments.
     */
    private void createStateFromArguments() {

        // Get the fragment arguments. Set the position and problem ID.
        final Bundle arguments = getArguments();
        position = arguments.getInt(POSITION_ARGUMENT, ILLEGAL_POSITION);
        problemId = arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID);
    }

    /**
     * Creates object state when a saved instance state bundle exists.
     *
     * @param savedInstanceState The saved instance state
     */
    private void createStateFromSaved(@NonNull Bundle savedInstanceState) {

        // Set the position and problem ID.
        position = savedInstanceState.getInt(POSITION_INDEX, ILLEGAL_POSITION);
        problemId = savedInstanceState.getLong(PROBLEM_ID_INDEX, ProblemLab.NULL_ID);
    }

    /**
     * Enables or disables a menu item.
     *
     * @param itemId The ID of the menu item to enable or disable
     * @param enable True to enable the menu item, false to disable it
     * @return True if the indicated menu item was enabled or disabled, false otherwise
     */
    private boolean enableDisable(int itemId, boolean enable) {

        // Try to find the menu item. Did we find it?
        final MenuItem item = menu.findItem(itemId);
        final boolean result = (null != item);
        if (result) {

            // We found the menu item. Enable or disable it.
            item.setEnabled(enable);
        }

        // Return whether we found the indicated menu item.
        return result;
    }

    /**
     * Enables or disables the solve menu option.
     *
     * @return True if the solve menu item was enabled or disabled, false otherwise
     */
    private boolean enableSolve() {
        return enableDisable(R.id.solve_problem, matrixPaneFull && vectorPaneFull);
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    /**
     * Determines if the problem is solved.
     *
     * @return True if the problem is solved, false otherwise
     */
    private boolean isProblemSolved() {
        return (null != problem) && problem.isSolved();
    }

    /**
     * Determines if the problem is write locked.
     *
     * @return True if the problem is write locked, false otherwise
     */
    private boolean isProblemWriteLocked() {
        return (null != problem) && problem.isWriteLocked();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO: Take effective action for each result.
        if (Activity.RESULT_OK == resultCode) {

            switch (requestCode) {

                case REQUEST_COPY:

                    output(String.format("Received request to copy problem with new name of '%s'.",
                            data.getStringExtra(CopyFragment.EXTRA_NEW_NAME)));
                    break;

                case REQUEST_DIMENSIONS:

                    output(String.format("Received new dimensions of: '%d'.",
                            data.getIntExtra(DimensionsFragment.EXTRA_DIMENSIONS, 0)));
                    break;

                case REQUEST_FILL:

                    output(String.format("Received fill request of '%f'; pane of '%s'; " +
                                    "all entries: '%s'",
                            data.getDoubleExtra(FillFragment.EXTRA_FILL, 0.),
                            data.getSerializableExtra(FillFragment.EXTRA_PANE).toString(),
                            data.getBooleanExtra(FillFragment.EXTRA_ALL_ENTRIES, false)));
                    break;

                case REQUEST_SOLVE:

                    output("Received a request to solve a problem.");
                    break;

                default:

                    output(String.format("Received unknown request code of '%d'", requestCode));
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {

        // Call the superclass method. Set the state change listener.
        super.onAttach(context);
        callbackListener = (context instanceof Callbacks) ? ((Callbacks) context) : null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and indicate that the fragment has an options menu.
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Create state from the fragment arguments if the saved instance state is null...
        if (null == savedInstanceState) {
            createStateFromArguments();
        }

        // ...otherwise create state from the non-null saved instance state.
        else {
            createStateFromSaved(savedInstanceState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Call the superclass method, and inflate the options menu.
        super.onCreateOptionsMenu(this.menu = menu, inflater);
        inflater.inflate(R.menu.fragment_problem, menu);

        // Enable or disable the solve menu item.
        setFullState(R.id.matrix_pane, !areMatrixEntriesMissing());
        setFullState(R.id.vector_pane, !areVectorEntriesMissing());
        enableSolve();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /*
         * Get the problem from the content producer using the problem ID. Configure the mutable
         * characteristics of the fragment factories.
         */
        problem = contentProducer.getContent(problemId);
        configureControlPaneFactory();
        configureEntryPaneFactoriesMutable();

        /*
         * Inflate the problem fragment. Add the control and answer fragments to the problem
         * fragment.
         */
        final View view = inflater.inflate(R.layout.fragment_problem, container, false);
        addFragment(R.id.control_pane, controlFragmentFactory);
        addFragment(R.id.answer_pane, answerFragmentFactory);

        /*
         * Add the matrix and vector fragments to the problem fragment. Return the problem
         * fragment.
         */
        addFragment(R.id.matrix_pane, matrixFragmentFactory);
        addFragment(R.id.vector_pane, vectorFragmentFactory);
        return view;
    }

    @Override
    public void onDestroy() {

        // Reset the problem ID and position. Call the superclass method.
        problemId = ProblemLab.NULL_ID;
        position = ILLEGAL_POSITION;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        // Unlock the problem. Reset the problem before calling the superclass method.
        unlockProblem();
        problem = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        // Clear the callback listener before calling the superclass method.
        callbackListener = null;
        super.onDetach();
    }

    @Override
    public void onEqual(int id) {

        output(String.format("onEqual(int): %d", id));
        onPaneStateChange(id, true);
    }

    @Override
    public void onGreater(int id) {

        output(String.format("onGreater(int): %d", id));
        onPaneStateChange(id, true);
    }

    @Override
    public void onLess(int id) {

        output(String.format("onLess(int): %d", id));
        onPaneStateChange(id, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO: Do correct actions with each options menu, and comment everything.
        boolean returnValue = true;
        switch (item.getItemId()) {

            case R.id.change_dimension:

                output("Change dimension menu item selected.");

                // Create a dimensions dialog.
                final DimensionsFragment dimensionsDialog =
                        DimensionsFragment.createInstance(problem.getDimensions());

                // Set the target fragment, and show the dialog.
                dimensionsDialog.setTargetFragment(this, REQUEST_DIMENSIONS);
                dimensionsDialog.show(getFragmentManager(), DIALOG_DIMENSIONS);
                break;

            case R.id.copy_problem:

                output("Copy problem menu item selected.");

                // Create the copy dialog. Set the target fragment.
                final CopyFragment copyFragment = CopyFragment.createInstance();
                copyFragment.setTargetFragment(this, REQUEST_COPY);

                // Show the dialog.
                copyFragment.show(getFragmentManager(), DIALOG_COPY);
                break;

            case R.id.fill_entries:

                output("Fill entries menu item selected.");

                // Create a fill dialog. Set the target fragment.
                final FillFragment fillDialog = FillFragment.createInstance();
                fillDialog.setTargetFragment(this, REQUEST_FILL);

                // Show the dialog.
                fillDialog.show(getFragmentManager(), DIALOG_FILL);
                break;

            case R.id.solve_problem:

                output("Solve problem menu item selected.");

                // Create the solve dialog. Set the target fragment.
                final SolveFragment solveFragment = SolveFragment.createInstance();
                solveFragment.setTargetFragment(this, REQUEST_SOLVE);

                // Show the dialog.
                solveFragment.show(getFragmentManager(), DIALOG_SOLVE);
                break;

            default:

                output("Unknown menu item selected.");
                returnValue = super.onOptionsItemSelected(item);
                break;
        }

        return returnValue;
    }

    /**
     * Manages a pane state change.
     *
     * @param id     The ID of the pane that has undergone a change
     * @param isFull True if the pane if full; false otherwise
     * @return True if the pane state of the indicated pane changed; false otherwise
     */
    private boolean onPaneStateChange(int id, boolean isFull) {

        // Set the full state of the pane. Did the full state of the pane change?
        final boolean result = setFullState(id, isFull);
        if (result) {

            // The full state of the pane changed. Enable or disable the solve menu option.
            enableSolve();
        }

        // Return whether the full state of the indicated pane changed.
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call the superclass method. Save the problem ID and the position.
        super.onSaveInstanceState(outState);
        outState.putLong(PROBLEM_ID_INDEX, problemId);
        outState.putInt(POSITION_INDEX, position);
    }

    /**
     * Sets the value for entries in the answer fragment.
     *
     * @param value     The value to set
     * @param forceFill Fill even controls that already have content
     */
    private void setAnswerValues(double value, boolean forceFill) {
        setValue(R.id.answer_pane, value, forceFill);
    }

    /**
     * Sets the full state of the fragment.
     *
     * @param paneId The pane ID of the child fragment that is changing.
     * @param isFull True if the indicated pane is now full; false otherwise
     * @return True if the full state of the indicated pane changed; false otherwise
     */
    private boolean setFullState(int paneId, boolean isFull) {

        // Is the indicated pane the matrix pane?

        // Declare and initialize the return value. Is the indicated pane the matrix pane?
        boolean result = false;
        if (R.id.matrix_pane == paneId) {

            /*
             * The indicated pane is the matrix pane. Reinitialize the return value: It will
             * be true if the matrix pane full flag is changing; false otherwise. Set the
             * matrix pane full flag.
             */
            result = matrixPaneFull ^ isFull;
            matrixPaneFull = isFull;
        }

        // Is the indicated pane the vector pane?
        else if (R.id.vector_pane == paneId) {

            /*
             * The indicated pane is the vector pane. Reinitialize the return value: It will
             * be true if the vector pane full flag is changing; false otherwise. Set the
             * vector pane full flag.
             */
            result = vectorPaneFull ^ isFull;
            vectorPaneFull = isFull;
        }

        // Return the result.
        return result;
    }

    /**
     * Sets the value for entries in the matrix fragment.
     *
     * @param value     The value to set
     * @param forceFill Fill even controls that already have content
     */
    private void setMatrixValues(double value, boolean forceFill) {
        setValue(R.id.matrix_pane, value, forceFill);
    }

    /**
     * Sets the value for entries in a pane.
     *
     * @param id        The ID of the pane
     * @param value     The value to set
     * @param forceFill Fill even controls that already have content
     */
    private void setValue(int id, double value, boolean forceFill) {

        // Did the caller not give us a bum fragment ID?
        final NumbersFragment<?> fragment = ((NumbersFragment<?>)
                getChildFragmentManager().findFragmentById(id));
        if (null != fragment) {

            // The caller did not give us a bum fragment ID. Set values in the fragment.
            fragment.setValue(value, forceFill);
        }
    }

    /**
     * Sets the value for entries in the vector fragment.
     *
     * @param value     The value to set
     * @param forceFill Fill even controls that already have content
     */
    private void setVectorValues(double value, boolean forceFill) {
        setValue(R.id.vector_pane, value, forceFill);
    }

    /**
     * Unlocks the problem.
     */
    private void unlockProblem() {

        /*
         * Is the problem not write locked? This is the lock state of the problem before this
         * fragment instance accessed it. The problem instance may not show a lock, but this
         * fragment *has* locked it on access, so now we have to resynchronize the database
         * state.
         */
        if (!problem.isWriteLocked()) {

            // The problem is not write locked. Is the problem lab not not null?
            final ProblemLab problemLab = getProblemLab();
            if (null != problemLab) {

                // The problem lab is not null. Unlock the problem.
                problemLab.updateWriteLock(problem);
                output(String.format("Problem with ID '%d' has been unlocked.", problemId));
            }
        }
    }

    public interface Callbacks {

        /**
         * Indicates that the dimensions of this problem changed.
         *
         * @param position   The position of this problem
         * @param problemId  The ID of this problem
         * @param dimensions The dimensions update
         */
        void onDimensionsChanged(int position, long problemId, int dimensions);

        /**
         * Indicates that this problem has been copied.
         *
         * @param position     The position of this problem
         * @param problemId    The ID of this problem
         * @param problemName  The name of the copied problem
         * @param newProblemId The ID of the copied problem
         */
        void onProblemCopied(int position, long problemId, String problemName, long newProblemId);

        /**
         * Indicates that values have been set in this problem have been set.
         *
         * @param position   The position of this problem
         * @param problemId  The ID of this problem
         * @param value      The value that has been set in the problem
         * @param allEntries True if all entries in the problem were set;
         *                   false if only missing entries
         */
        void onValuesSet(int position, long problemId, double value, boolean allEntries);
    }

    private static class AnswerFragmentFactory extends NumbersFragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {
            return AnswerFragment.createInstance(problemId, getLabel(), getBackgroundColor(),
                    getSize());
        }
    }

    private static class ControlFragmentFactory extends FragmentFactory {

        // The problem
        private Problem problem;

        @Override
        public Fragment createFragment(long problemId) {
            return ControlFragment.createInstance(problemId, isEnabled(), getProblem());
        }

        /**
         * Gets the problem.
         *
         * @return The problem
         */
        Problem getProblem() {
            return problem;
        }

        /**
         * Sets the problem.
         *
         * @param problem The problem
         */
        void setProblem(Problem problem) {
            this.problem = problem;
        }
    }

    /**
     * Contains an interface for a fragment factory.
     */
    private abstract static class FragmentFactory {

        // The enabled state of factory output
        private boolean enabled;

        /**
         * Creates a fragment.
         *
         * @param problemId The problem ID associated with the fragment
         * @return A newly created fragment
         */
        abstract Fragment createFragment(long problemId);

        /**
         * Gets the enabled state of the factory output.
         *
         * @return The enabled state of the factory output
         */
        boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets the enabled state of the factory output.
         *
         * @param enabled The enabled state of the factory output
         */
        void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private static class MatrixFragmentFactory extends NumbersFragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {

            // Use the size for both the number of rows and the number of columns.
            final int size = getSize();
            return MatrixFragment.createInstance(problemId, getLabel(), getBackgroundColor(),
                    isEnabled(), size, size);
        }
    }

    private abstract static class NumbersFragmentFactory extends FragmentFactory {

        // The background color of factory output
        private int backgroundColor;

        // The label of factory output
        private String label;

        // True if the factory output is matrix, otherwise a vector
        private boolean matrix;

        // The size of factory output
        private int size;

        /**
         * Customizes a NumbersFragment.
         *
         * @param fragment  The fragment to customize
         * @param problemId The problem ID to set in the fragment
         */
        protected void customize(NumbersFragment fragment, long problemId) {

            // Get the inversion of the matrix flag. Get the size of factory output.
            final boolean isNotMatrix = !isMatrix();
            final int size = getSize();

            /*
             * Customize the given fragment with a label, problem ID, background color, enabled
             * setting, size, and matrix flag.
             */
            NumbersFragment.customizeInstance(fragment, problemId, getLabel(),
                    getBackgroundColor(), isEnabled(),
                    size, isNotMatrix ? 1 : size, isNotMatrix);
        }

        /**
         * Gets the background color of the factory output.
         *
         * @return The background color of the factory output
         */
        int getBackgroundColor() {
            return backgroundColor;
        }

        /**
         * Gets the label of the factory output.
         *
         * @return The label of the factory output
         */
        String getLabel() {
            return label;
        }

        /**
         * Gets the size of the factory output.
         *
         * @return The size of the factory output
         */
        int getSize() {
            return size;
        }

        /**
         * Gets the matrix flag of the factory output.
         *
         * @return The matrix flag of the factory output
         */
        boolean isMatrix() {
            return matrix;
        }

        /**
         * Sets the background color of the factory output.
         *
         * @param backgroundColor The background color of the factory output
         */
        void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        /**
         * Sets the label of the factory output.
         *
         * @param label The label of the factory output
         */
        void setLabel(String label) {
            this.label = label;
        }

        /**
         * Sets the matrix flag of the factory output.
         *
         * @param matrix The matrix flag of the factory output
         */
        void setMatrix(boolean matrix) {
            this.matrix = matrix;
        }

        /**
         * Sets the size of the factory output.
         *
         * @param size The size of the factory output
         */
        void setSize(int size) {
            this.size = size;
        }
    }

    private static class PaneCharacteristics {

        // The enabled state of the pane
        private final boolean enabled;

        // The label of the pane
        private final String label;

        // The matrix flag of the pane
        private final boolean matrix;

        /**
         * Creates the pane characteristics object.
         *
         * @param label   The label of the pane
         * @param enabled The enabled state of the pane
         * @param matrix  The matrix flag of the pane
         */
        PaneCharacteristics(String label, boolean enabled, boolean matrix) {

            // Set all the characteristics.
            this.label = label;
            this.enabled = enabled;
            this.matrix = matrix;
        }

        /**
         * Gets the label of the pane.
         *
         * @return The label of the pane
         */
        String getLabel() {
            return label;
        }

        /**
         * Gets the enabled state of the pane.
         *
         * @return The enabled state of the pane
         */
        boolean isEnabled() {
            return enabled;
        }

        /**
         * Gets the matrix flag of the pane.
         *
         * @return The matrix flag of the pane
         */
        boolean isMatrix() {
            return matrix;
        }
    }


    private static class VectorFragmentFactory extends NumbersFragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {
            return VectorFragment.createInstance(problemId, getLabel(), getBackgroundColor(),
                    isEnabled(), getSize());
        }
    }
}
