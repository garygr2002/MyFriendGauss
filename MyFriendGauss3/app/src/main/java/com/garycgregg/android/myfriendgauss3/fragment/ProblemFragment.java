package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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

public class ProblemFragment extends GaussFragment {

    // An illegal position
    private static final int ILLEGAL_POSITION = ~0;

    // The prefix for instance arguments
    private static final String PREFIX_STRING = ProblemFragment.class.getName();

    // The position argument
    private static final String POSITION_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "position");

    // The problem ID argument
    private static final String PROBLEM_ID_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "problem_id");

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

        // Configure the immutable features of the content panes.
        configure();
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
            manager.beginTransaction().add(paneId,
                    factory.createFragment(problemId)).commit();
        }
    }

    /**
     * Configures a numbers fragment factory with characteristics for a given pane ID.
     *
     * @param factory The numbers fragment factory
     * @param paneId  The pane ID to use for configuration
     */
    private void configure(NumbersFragmentFactory factory, int paneId) {

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
     * Configures the fragment factories.
     */
    private void configure() {

        // Set the characteristics for the matrix pane.
        final int defaultColor = Color.WHITE;
        characteristicsArray.put(R.id.matrix_pane, new PaneCharacteristics("Matrix\nEntries",
                true, true));

        // Set the characteristics for the answer pane.
        characteristicsArray.put(R.id.answer_pane, new PaneCharacteristics("Answers\n",
                false, false));

        // Set the characteristics for the vector pane.
        characteristicsArray.put(R.id.vector_pane, new PaneCharacteristics("Vector\nEntries",
                true, false));

        // Try to configure the numbers fragment factories.
        configure(answerFragmentFactory, R.id.answer_pane);
        configure(matrixFragmentFactory, R.id.matrix_pane);
        configure(vectorFragmentFactory, R.id.vector_pane);
    }

    /**
     * Configures the color of the various number content panes.
     */
    private void configureColor() {

        // Get the resources. Set the background color of the answer pane.
        final Resources resources = getResources();
        answerFragmentFactory.setBackgroundColor(resources.getColor(R.color.tableEntryAnswer));

        // Set the background colors of the matrix and vector panes.
        matrixFragmentFactory.setBackgroundColor(resources.getColor(R.color.tableEntryMatrix));
        vectorFragmentFactory.setBackgroundColor(resources.getColor(R.color.tableEntryVector));
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        /*
         * Call the superclass method, and indicate that this fragment has an options menu.
         * Configure the color of the content panes.
         */
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        configureColor();

        // Get the fragment arguments. Set the position.
        final Bundle arguments = getArguments();
        position = arguments.getInt(POSITION_ARGUMENT, ILLEGAL_POSITION);

        // Set the problem ID and the problem.
        problemId = arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID);
        problem = contentProducer.getContent(problemId);
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

        // Inflate the problem fragment, and get the problem associated with the problem ID.
        final View view = inflater.inflate(R.layout.fragment_problem, container, false);
        configureColor();

        // Set the editable state of the matrix and vector fragment factories.
        final boolean enable = !problem.isWriteLocked() && (null == problem.getSolved());
        matrixFragmentFactory.setEnabled(enable);
        vectorFragmentFactory.setEnabled(enable);

        // Add the control pane fragment. Get the dimensions of the problem.
        addFragment(R.id.control_pane, controlFragmentFactory);
        final int dimensions = problem.getDimensions();

        // Set the size of the problem in the matrix fragment factory. Add the matrix pane.
        matrixFragmentFactory.setSize(dimensions);
        addFragment(R.id.matrix_pane, matrixFragmentFactory);

        // Set the size of the problem in the answer fragment factory. Add the answer pane.
        answerFragmentFactory.setSize(dimensions);
        addFragment(R.id.answer_pane, answerFragmentFactory);

        // Set the size of the problem in the vector fragment factory. Add the vector pane.
        vectorFragmentFactory.setSize(dimensions);
        addFragment(R.id.vector_pane, vectorFragmentFactory);
        return view;
    }

    @Override
    public void onDestroy() {

        // Reset the problem and problem ID.
        problem = null;
        problemId = ProblemLab.NULL_ID;

        // Reset the position. Call the superclass method.
        position = ILLEGAL_POSITION;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO: Do correct actions with each options menu, and comment everything.
        boolean returnValue = true;
        switch (item.getItemId()) {

            case R.id.change_dimension:

                Log.d(TAG, "Change dimension menu item selected.");
                break;

            case R.id.copy_problem:

                Log.d(TAG, "Copy problem menu item selected.");
                break;

            case R.id.edit_problem:

                Log.d(TAG, "Edit problem menu item selected.");
                break;

            case R.id.prefill_entries:

                Log.d(TAG, "Prefill entries menu item selected.");
                break;

            case R.id.solve_problem:

                Log.d(TAG, "Solve problem menu item selected.");
                break;

            default:

                Log.d(TAG, "Unknown menu item selected.");
                returnValue = super.onOptionsItemSelected(item);
                break;
        }

        return returnValue;
    }

    @Override
    public void onPause() {

        // TODO: Put the changes in the database.
        super.onPause();
    }

    /**
     * Contains an interface for a fragment factory.
     */
    private interface FragmentFactory {

        /**
         * Creates a fragment.
         *
         * @param problemId The problem ID associated with the fragment
         * @return A newly created fragment
         */
        Fragment createFragment(long problemId);
    }

    private static class AnswerFragmentFactory extends NumbersFragmentFactory
            implements FragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {
            return AnswerFragment.createInstance(problemId, getLabel(), getBackgroundColor(),
                    getSize());
        }
    }

    private static class ControlFragmentFactory implements FragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {
            return ControlFragment.createInstance(problemId);
        }
    }

    private static class MatrixFragmentFactory extends NumbersFragmentFactory
            implements FragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {

            // Use the size for both the number of rows and the number of columns.
            final int size = getSize();
            return MatrixFragment.createInstance(problemId, getLabel(), getBackgroundColor(),
                    isEnabled(), size, size);
        }
    }

    private abstract static class NumbersFragmentFactory implements FragmentFactory {

        // The background color of factory output
        private int backgroundColor;

        // The enabled state of factory output
        private boolean enabled;

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

            // Customize the number fragment with the problem ID.
            ContentFragment.customizeInstance(fragment, problemId);
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
         * Gets the enabled state of the factory output.
         *
         * @return The enabled state of the factory output
         */
        boolean isEnabled() {
            return enabled;
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
         * Sets the enabled state of the factory output.
         *
         * @param enabled The enabled state of the factory output
         */
        void setEnabled(boolean enabled) {
            this.enabled = enabled;
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


    private static class VectorFragmentFactory extends NumbersFragmentFactory
            implements FragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {
            return VectorFragment.createInstance(problemId, getLabel(), getBackgroundColor(),
                    isEnabled(), getSize());
        }
    }
}
