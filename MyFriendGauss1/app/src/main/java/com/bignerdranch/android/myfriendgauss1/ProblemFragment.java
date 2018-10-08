package com.bignerdranch.android.myfriendgauss1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProblemFragment extends Fragment {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String SIZE_ARGUMENT = String.format(FORMAT_STRING,
            NumbersFragment.class.getName(),
            "size");
    private final ControlFragmentFactory controlFragmentFactory = new ControlFragmentFactory();
    private final NumbersFragmentFactory numbersFragmentFactory = new NumbersFragmentFactory();

    public static Fragment createInstance(int size) {

        final Bundle arguments = new Bundle();
        arguments.putSerializable(SIZE_ARGUMENT, size);

        final Fragment fragment = new ProblemFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_problem, container, false);
        final FragmentManager manager = getFragmentManager();
        addCard(manager, R.id.control_pane, controlFragmentFactory);

        numbersFragmentFactory.setMatrix(true);
        numbersFragmentFactory.setSize(getArguments().getInt(SIZE_ARGUMENT, 1));
        addCard(manager, R.id.matrix_pane, numbersFragmentFactory);

        numbersFragmentFactory.setMatrix(false);
        addCard(manager, R.id.answer_pane, numbersFragmentFactory);

        addCard(manager, R.id.vector_pane, numbersFragmentFactory);
        return view;
    }

    private void addCard(FragmentManager manager, int paneId, FragmentFactory factory) {

        Fragment fragment = manager.findFragmentById(paneId);
        if (null == fragment) {

            fragment = factory.createFragment();
            manager.beginTransaction().add(paneId, fragment).commit();
        }
    }

    private interface FragmentFactory {

        Fragment createFragment();
    }

    private static class ControlFragmentFactory implements FragmentFactory {

        @Override
        public Fragment createFragment() {
            return ControlFragment.createInstance();
        }
    }

    private static class NumbersFragmentFactory implements FragmentFactory {

        private int size;
        private boolean matrix;

        int getSize() {
            return size;
        }

        void setSize(int size) {
            this.size = size;
        }

        boolean isMatrix() {
            return matrix;
        }

        void setMatrix(boolean matrix) {
            this.matrix = matrix;
        }

        @Override
        public Fragment createFragment() {

            final int size = getSize();
            return NumbersFragment.createInstance(size, isMatrix() ? size : 1);
        }
    }
}
