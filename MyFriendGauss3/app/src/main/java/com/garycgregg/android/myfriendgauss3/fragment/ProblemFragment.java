package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.res.Resources;
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
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;
import com.garycgregg.android.myfriendgauss3.content.Answer;
import com.garycgregg.android.myfriendgauss3.content.Matrix;
import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.content.Vector;

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

    // A factory for control fragments
    private final ControlFragmentFactory controlFragmentFactory = new ControlFragmentFactory();

    // A factory for matrix fragments
    private final NumbersFragmentFactory matrixFragmentFactory = new MatrixFragmentFactory();

    // A factory for vector fragments
    private final NumbersFragmentFactory vectorFragmentFactory = new VectorFragmentFactory();

    // The position of this instance
    private int position;

    // The problem associated with this instance
    private Problem problem;

    // The problem ID associated with this instance
    private long problemId;

    /**
     * Creates an instance of a ProblemFragment with the required argument(s).
     *
     * @param problemId The problem ID associated with this instance
     * @param position The position of this instance
     * @return A new Gauss fragment
     */
    public static GaussFragment createInstance(long problemId, int position) {

        // Create the arguments bundle. Add the problem ID and position.
        final Bundle arguments = new Bundle();
        arguments.putLong(PROBLEM_ID_ARGUMENT, problemId);
        arguments.putInt(POSITION_ARGUMENT, position);

        // Create a new problem fragment and set the arguments. Return the fragment.
        final GaussFragment fragment = new ProblemFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * Uses a fragment factory to add a fragment for a given ID.
     *
     * @param paneId  The ID for which to add a fragment
     * @param factory A factory for generating a fragment
     */
    private void addFragment(int paneId, FragmentFactory factory) {
        addFragment(paneId, factory, false);
    }

    /**
     * Uses a fragment factory to add a fragment for a given ID.
     *
     * @param paneId  The ID for which to add a fragment
     * @param factory A factory for generating a fragment
     * @param replace True to replace the indicated pane if it exists, false otherwise
     */
    private void addFragment(int paneId, FragmentFactory factory, boolean replace) {

        /*
         * Try to find an existing fragment for the given ID. Is there no such existing
         * fragment?
         */
        final FragmentManager manager = getChildFragmentManager();
        final Fragment fragment = manager.findFragmentById(paneId);
        if (null == fragment) {

            // There is no such existing fragment. Create one using the given factory.
            manager.beginTransaction().add(paneId,
                    factory.createFragment((null == problem) ? ProblemLab.NULL_ID :
                            problem.getProblemId())).commit();
        } else if (replace) {

            /*
             * There is an existing fragment with this pane ID, but the caller wants
             * it replaced.
             */
            manager.beginTransaction().replace(paneId,
                    factory.createFragment((null == problem) ? ProblemLab.NULL_ID :
                            problem.getProblemId())).commit();
        }
    }

    /**
     * Configures a numbers fragment factory with characteristics for a given pane ID.
     *
     * @param factory The numbers fragment factory
     * @param paneId  The pane ID to use for configuration
     */
    private PaneCharacteristics configure(NumbersFragmentFactory factory, int paneId) {

        /*
         * We need pane characteristics for the given ID. Try to find any published
         * characteristics. Are there any?
         */
        final PaneCharacteristics paneCharacteristics = characteristicsArray.get(paneId);
        if (null != paneCharacteristics) {

            /*
             * There are existing pane characteristics for the given ID. Set the label and
             * background color in the fragment factory from the pane characteristics.
             */
            factory.setLabel(paneCharacteristics.getLabel());
            factory.setBackgroundColor(paneCharacteristics.getColorResource());

            /*
             * Set the enabled status and the matrix status in the fragment factory from the
             * pane characteristics.
             */
            factory.setEnabled(paneCharacteristics.isEnabled());
            factory.setMatrix(paneCharacteristics.isMatrix());
        }

        // Return the pane characteristics.
        return paneCharacteristics;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        /*
         * Call through to the superclass method, and indicate that this fragment has an options
         * menu.
         */
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /*
         * Use the saved instance state for arguments if it is not null. Otherwise use the instance
         * supplied arguments.
         */
        final Bundle arguments = (null == savedInstanceState) ? getArguments() :
                savedInstanceState;

        // Set the position and the problem ID.
        position = arguments.getInt(POSITION_ARGUMENT, ILLEGAL_POSITION);
        problemId = arguments.getLong(PROBLEM_ID_ARGUMENT, ProblemLab.NULL_ID);

        // Get the activity's resources, and set the characteristics for the matrix pane.
        final Resources resources = getActivity().getResources();
        characteristicsArray.put(R.id.matrix_pane, new PaneCharacteristics("Matrix\nEntries",
                resources.getColor(R.color.tableEntryMatrix), true, true));

        // Set the characteristics for for the answer.
        characteristicsArray.put(R.id.answer_pane, new PaneCharacteristics("Answers\n",
                resources.getColor(R.color.tableEntryAnswer), false, false));

        // Set the characteristics for the vector pane.
        characteristicsArray.put(R.id.vector_pane, new PaneCharacteristics("Vector\nEntries",
                resources.getColor(R.color.tableEntryVector), true, false));

        // Try to configure the numbers fragment factories.
        configure(answerFragmentFactory, R.id.answer_pane);
        configure(matrixFragmentFactory, R.id.matrix_pane);
        configure(vectorFragmentFactory, R.id.vector_pane);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Call through to the superclass method, and inflate the options menu.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_problem, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the problem fragment, and get the problem associated with the problem ID.
        final View view = inflater.inflate(R.layout.fragment_problem, container, false);
        problem = getProblemLab().getProblem(problemId);

        // Add the control pane fragment. Get the dimensions of the problem.
        addFragment(R.id.control_pane, controlFragmentFactory);
        final int dimensions = (null == problem) ? 1 : problem.getDimensions();

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

        /*
         * Clear the characteristics array and the problem ID. Call through to the superclass
         * method.
         */
        characteristicsArray.clear();
        problemId = ProblemLab.NULL_ID;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        // Set the problem to null and call through to the superclass method.
        problem = null;
        super.onDestroyView();
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
    public void onPrepareOptionsMenu(Menu menu) {

        // TODO: This is an experiment.
        menu.findItem(R.id.solve_problem).setEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Call through to the superclass method. Save the problem ID and position.
        super.onSaveInstanceState(outState);
        outState.putLong(PROBLEM_ID_ARGUMENT, problemId);
        outState.putInt(POSITION_ARGUMENT, position);
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

            // Create a new answer fragment. Customize the fragment, and return it.
            final NumbersFragment<Answer> fragment = new AnswerFragment();
            customize(fragment, problemId);
            return fragment;
        }
    }

    private static class ControlFragmentFactory implements FragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {

            /*
             * Create a card fragment and customize it with the problem ID. Return the fragment.
             */
            final ContentFragment<Problem> fragment = new ControlFragment();
            ContentFragment.customizeInstance(fragment, problemId);
            return fragment;
        }
    }

    private static class MatrixFragmentFactory extends NumbersFragmentFactory
            implements FragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {

            // Create a new matrix fragment. Customize the fragment, and return it.
            final NumbersFragment<Matrix> fragment = new MatrixFragment();
            customize(fragment, problemId);
            return fragment;
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
        protected void customize(NumbersFragment<?> fragment, long problemId) {

            // Get the inversion of the matrix flag. Get the size of factory output.
            final boolean isNotMatrix = !isMatrix();
            final int size = getSize();

            /*
             * Customize the given fragment with a label, background color, enabled setting,
             * size, and matrix flag.
             */
            NumbersFragment.customizeInstance(fragment, getLabel(),
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

        // The color resources of the pane
        private final int colorResource;

        // The enabled state of the pane
        private final boolean enabled;

        // The label of the pane
        private final String label;

        // The matrix flag of the pane
        private final boolean matrix;

        /**
         * Creates the pane characteristics object.
         *
         * @param label         The label of the pane
         * @param colorResource The color resources of the pane
         * @param enabled       The enabled state of the pane
         * @param matrix        The matrix flag of the pane
         */
        PaneCharacteristics(String label, int colorResource, boolean enabled, boolean matrix) {

            // Set all the characteristics.
            this.label = label;
            this.colorResource = colorResource;
            this.enabled = enabled;
            this.matrix = matrix;
        }

        /**
         * Gets the color resources of the pane.
         *
         * @return The color resources of the pane
         */
        int getColorResource() {
            return colorResource;
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

            // Create a new vector fragment. Customize the fragment, and return it.
            final NumbersFragment<Vector> fragment = new VectorFragment();
            customize(fragment, problemId);
            return fragment;
        }
    }
}
