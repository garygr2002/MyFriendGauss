package com.garycgregg.android.myfriendgauss3;

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

public class ProblemFragment extends Fragment implements ProblemLabSource {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String PREFIX = ProblemFragment.class.getName();
    private static final String ID_ARGUMENT = String.format(FORMAT_STRING, PREFIX, "problem_id");
    private static final String TAG = ProblemFragment.class.getSimpleName();

    private final ControlFragmentFactory controlFragmentFactory = new ControlFragmentFactory();
    private final NumbersFragmentFactory numbersFragmentFactory = new NumbersFragmentFactory();
    private final SparseArray<PaneCharacteristics> characteristicsArray = new SparseArray<>();

    private long nullProblemId;
    private Problem problem;
    private ProblemLab problemLab;

    public static Fragment createInstance(long problemId) {

        final Bundle arguments = new Bundle();
        arguments.putLong(ID_ARGUMENT, problemId);

        final Fragment fragment = new ProblemFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public long getNullProblemId() {
        return nullProblemId;
    }

    @Override
    public ProblemLab getProblemLab() {
        return problemLab;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        final Resources resources = getActivity().getResources();
        characteristicsArray.put(R.id.matrix_pane, new PaneCharacteristics("Matrix\nEntries",
                resources.getColor(R.color.tableEntryMatrix), true, true));

        characteristicsArray.put(R.id.answer_pane, new PaneCharacteristics("Answers\n",
                resources.getColor(R.color.tableEntryAnswer), false, false));

        characteristicsArray.put(R.id.vector_pane, new PaneCharacteristics("Vector\nEntries",
                resources.getColor(R.color.tableEntryVector), true, false));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_problem, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_problem, container, false);
        final ProblemLabSource problemLabSource = ((ProblemLabSource) getActivity());

        nullProblemId = problemLabSource.getNullProblemId();
        problemLab = problemLabSource.getProblemLab();

        int size = 1;
        problem = problemLab.getProblem(getArguments().getLong(ID_ARGUMENT, nullProblemId));
        if (null != problem) {

            size = problem.getDimensions();
        }

        final FragmentManager manager = getFragmentManager();
        addFragment(manager, R.id.control_pane, controlFragmentFactory);

        numbersFragmentFactory.setSize(size);
        addNumbersFragment(manager, R.id.matrix_pane);

        addNumbersFragment(manager, R.id.answer_pane);
        addNumbersFragment(manager, R.id.vector_pane);
        return view;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        characteristicsArray.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

    /**
     * Uses a fragment factory to add a fragment manager for a given ID.
     *
     * @param manager The fragment manager
     * @param paneId  The ID for which to add a fragment
     * @param factory A factory for generating a fragment
     */
    private void addFragment(FragmentManager manager, int paneId, FragmentFactory factory) {
        addFragment(manager, paneId, factory, false);
    }

    /**
     * Uses a fragment factory to add a fragment manager for a given ID.
     *
     * @param manager The fragment manager
     * @param paneId  The ID for which to add a fragment
     * @param factory A factory for generating a fragment
     * @param replace True to replace the indicated pane if it exists, false otherwise
     */
    private void addFragment(FragmentManager manager, int paneId, FragmentFactory factory,
                             boolean replace) {

        /*
         * Try to find an existing fragment for the given ID. Is there no such existing
         * fragment?
         */
        Fragment fragment = manager.findFragmentById(paneId);
        if (null == fragment) {

            // There is no such existing fragment. Create one using the given factory.
            manager.beginTransaction().add(paneId,
                    factory.createFragment((null == problem) ? getNullProblemId() :
                            problem.getProblemId())).commit();
        }

        else if (replace) {

            /*
             * There is an existing fragment with this pane ID, but the caller wants
             * it replaced.
             */
            manager.beginTransaction().replace(paneId,
                    factory.createFragment((null == problem) ? getNullProblemId() :
                            problem.getProblemId())).commit();
        }
    }

    /**
     * Adds a numbers fragment to the fragment manager for a given ID.
     *
     * @param manager The fragment manager
     * @param paneId  The ID for which to add a fragment
     */
    private void addNumbersFragment(FragmentManager manager, int paneId) {

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
            numbersFragmentFactory.setLabel(paneCharacteristics.getLabel());
            numbersFragmentFactory.setBackgroundColor(paneCharacteristics.getColorResource());

            /*
             * Set the enabled status and the matrix status in the fragment factory from the
             * pane characteristics. As needed, used the factory to create a new fragment for
             * the fragment manager.
             */
            numbersFragmentFactory.setEnabled(paneCharacteristics.isEnabled());
            numbersFragmentFactory.setMatrix(paneCharacteristics.isMatrix());
            addFragment(manager, paneId, numbersFragmentFactory);
        }
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

    private static class PaneCharacteristics {

        private final int colorResource;

        private final boolean enabled;

        private final String label;

        private final boolean matrix;

        PaneCharacteristics(String label, int colorResource, boolean enabled, boolean matrix) {

            this.label = label;
            this.colorResource = colorResource;
            this.enabled = enabled;
            this.matrix = matrix;
        }

        int getColorResource() {
            return colorResource;
        }

        String getLabel() {
            return label;
        }

        boolean isEnabled() {
            return enabled;
        }

        boolean isMatrix() {
            return matrix;
        }
    }

    private static class ControlFragmentFactory implements FragmentFactory {

        @Override
        public Fragment createFragment(long problemId) {

            final CardFragment fragment = ControlFragment.createInstance();
            CardFragment.customizeInstance(fragment, problemId);
            return fragment;
        }
    }

    private static class NumbersFragmentFactory implements FragmentFactory {

        private int backgroundColor;
        private boolean enabled;
        private String label;
        private int size;
        private boolean matrix;

        int getBackgroundColor() {
            return backgroundColor;
        }

        String getLabel() {
            return label;
        }

        int getSize() {
            return size;
        }

        boolean isEnabled() {
            return enabled;
        }

        boolean isMatrix() {
            return matrix;
        }

        void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        void setLabel(String label) {
            this.label = label;
        }

        void setMatrix(boolean matrix) {
            this.matrix = matrix;
        }

        void setSize(int size) {
            this.size = size;
        }

        @Override
        public Fragment createFragment(long problemId) {

            final boolean isNotMatrix = !isMatrix();
            final int size = getSize();

            final CardFragment fragment = NumbersFragment.createInstance(getLabel(),
                    getBackgroundColor(), isEnabled(),
                    size, isNotMatrix ? 1 : size, isNotMatrix);

            CardFragment.customizeInstance(fragment, problemId);
            return fragment;
        }
    }
}
