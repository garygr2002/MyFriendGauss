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

    // The dimensions dialog identifier
    private static final String DIALOG_DIMENSIONS = "DialogDimensions";

    // The fill dialog identifier
    private static final String DIALOG_FILL = "DialogFill";

    // An illegal position
    private static final int ILLEGAL_POSITION = ~0;

    // The prefix for instance arguments
    private static final String PREFIX_STRING = ProblemFragment.class.getName();

    // The position argument key
    private static final String POSITION_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "position");

    // The problem ID argument key
    private static final String PROBLEM_ID_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "problem_id");

    // The identifier for a dimensions request
    private static final int REQUEST_DIMENSIONS = 0;

    // The identifier for a fill request
    private static final int REQUEST_FILL = 1;

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

    // Our state change listener
    private StateChangeListener listener;

    // The position of this instance
    private int position = ILLEGAL_POSITION;

    // The problem
    private Problem problem;

    // The problem ID associated with this instance
    private long problemId = ProblemLab.NULL_ID;

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
        listener = (context instanceof StateChangeListener) ?
                ((StateChangeListener) context) : null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and indicate that the fragment has an options menu.
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get the fragment arguments, and set the position.
        final Bundle arguments = getArguments();
        position = arguments.getInt(POSITION_ARGUMENT, ILLEGAL_POSITION);

        // Set the problem ID and the problem.
        problemId = arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID);
        problem = contentProducer.getContent(problemId);

        // Configure the mutable characteristics of the fragment factories.
        output(String.format("Problem with ID '%d' has been locked.", problemId));
        configureControlPaneFactory();
        configureEntryPaneFactoriesMutable();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Call the superclass method, and inflate the options menu.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_problem, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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

        // Unlock the problem. Reset the problem and problem ID.
        unlockProblem();
        problem = null;
        problemId = ProblemLab.NULL_ID;

        // Reset the position. Call the superclass method.
        position = ILLEGAL_POSITION;
        super.onDestroy();
    }

    @Override
    public void onDetach() {

        // Clear the state change listener, and call the superclass method.
        listener = null;
        super.onDetach();
    }

    @Override
    public void onEqual(int id) {

        // TODO: Fill this in.
        output(String.format("onEqual(int): %d", id));
    }

    @Override
    public void onGreater(int id) {

        // TODO: Fill this in.
        output(String.format("onGreater(int): %d", id));
    }

    @Override
    public void onLess(int id) {

        // TODO: Fill this in.
        output(String.format("onLess(int): %d", id));
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

                // TODO: Create dialog.
                output("Copy problem menu item selected.");
                break;

            case R.id.edit_problem:

                // TODO: Delete this.
                output("Edit problem menu item selected.");
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

                // TODO: Create dialog.
                output("Solve problem menu item selected.");
                break;

            default:

                output("Unknown menu item selected.");
                returnValue = super.onOptionsItemSelected(item);
                break;
        }

        return returnValue;
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

    public interface StateChangeListener {

        /**
         * Indicates that the problem changed state in a way that will require the fragment to
         * be redrawn.
         *
         * @param position  The position of the fragment, as identified by its arguments
         * @param problemId The problem ID, as identified by the fragment arguments.
         */
        void onStateChange(int position, long problemId);
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
