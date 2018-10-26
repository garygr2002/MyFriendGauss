package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.garycgregg.android.myfriendgauss3.activity.ProblemPagerActivity;
import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.content.Problem;

import java.util.List;

public class ProblemListFragment extends GaussFragment {

    // The adapter for the problem list
    private ProblemAdapter adapter;

    // A recycler view for the problem list
    private RecyclerView problemRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Create a view.
        final View view = inflater.inflate(R.layout.fragment_problem_list, container,
                false);

        // Find the recycler view and set its layout manager.
        problemRecyclerView = view.findViewById(R.id.problem_recycler_view);
        problemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Update the user interface and return the view.
        updateUI();
        return view;
    }

    @Override
    public void onResume() {

        // Call through to the superclass method, and update the user interface.
        super.onResume();
        updateUI();
    }

    /**
     * Updates the user interface.
     */
    private void updateUI() {

        // Get a list of all the problems. Is there not already an adapter?
        final List<Problem> problemList = getProblemLab().getProblems();
        if (null == adapter) {

            // There is not already an adapter. Create one and set it in the problem recycler view.
            problemRecyclerView.setAdapter(adapter = new ProblemAdapter(problemList));
        }

        // There is already a problem recycler view.
        else {

            /*
             * Set the problem list in the existing adapter, and notify the adapter of a data set
             * change.
             */
            adapter.setProblems(problemList);
            adapter.notifyDataSetChanged();
        }
    }

    private class ProblemAdapter extends RecyclerView.Adapter<ProblemHolder> {

        // A list of all problems.
        private List<Problem> problemList;

        /**
         * Constructs the problem adapter.
         *
         * @param problemList A list of all the problems
         */
        ProblemAdapter(List<Problem> problemList) {
            this.problemList = problemList;
        }

        @Override
        public int getItemCount() {
            return problemList.size();
        }

        @Override
        public void onBindViewHolder(ProblemHolder holder, int position) {
            holder.bind(problemList.get(position), position);
        }

        @Override
        public ProblemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProblemHolder(LayoutInflater.from(getActivity()), parent);
        }

        /**
         * Set the list of all the problems.
         *
         * @param problemList A list of all the problems
         */
        void setProblems(List<Problem> problemList) {
            this.problemList = problemList;
        }
    }

    private class ProblemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The date the problem was created
        private final TextView problemDate;

        // The name of the problem
        private final TextView problemName;

        // A visual indication of where the problem has been solved
        private final ImageView solvedImageView;

        // The position of the problem in its recycler view
        private int position;

        /**
         * Constructs the problem holder.
         *
         * @param inflater A object for inflating content
         * @param parent   The parent of the newly inflated objects
         */
        ProblemHolder(LayoutInflater inflater, ViewGroup parent) {

            /*
             * Call through to the superclass method, and make this the object an on-click
             * listener for the item view.
             */
            super(inflater.inflate(R.layout.list_item_problem, parent, false));
            itemView.setOnClickListener(this);

            /*
             * Find the problem name display text, the problem date display text, and the 'solved'
             * image display.
             */
            problemName = itemView.findViewById(R.id.problem_name);
            problemDate = itemView.findViewById(R.id.problem_date);
            solvedImageView = itemView.findViewById(R.id.problem_solved);
        }

        /**
         * Binds the holder with the given problem.
         *
         * @param problem  The problem to find with the holder
         * @param position The position of the problem in the recycler view
         */
        void bind(Problem problem, int position) {

            // Set the position and the problem name.
            this.position = position;
            problemName.setText(problem.getName());

            // Set the problem date and the visibility status of the 'solved' image display.
            problemDate.setText(problem.getCreated().toString());
            solvedImageView.setVisibility(problem.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            startActivity(ProblemPagerActivity.newIntent(getActivity(), position));
        }
    }
}
