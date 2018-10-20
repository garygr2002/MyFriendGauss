package com.garycgregg.android.myfriendgauss3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProblemListFragment extends GaussFragment {

    private ProblemAdapter adapter;
    private RecyclerView problemRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_problem_list, container,
                false);

        problemRecyclerView = view.findViewById(R.id.problem_recycler_view);
        problemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        updateUI();
    }

    private void updateUI() {

        final List<Problem> problemList = getProblemLab().getProblems();
        if (null == adapter) {

            problemRecyclerView.setAdapter(adapter = new ProblemAdapter(problemList));
        }

        else {

            adapter.setProblems(problemList);
            adapter.notifyDataSetChanged();
        }
    }

    private class ProblemAdapter extends RecyclerView.Adapter<ProblemHolder> {

        private List<Problem> problemList;
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

        void setProblems(List<Problem> problemList) {
            this.problemList = problemList;
        }
    }

    private class ProblemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView problemDate;
        private final TextView problemName;
        private final ImageView solvedImageView;
        private int position;

        ProblemHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_problem, parent, false));
            itemView.setOnClickListener(this);

            problemName = itemView.findViewById(R.id.problem_name);
            problemDate = itemView.findViewById(R.id.problem_date);
            solvedImageView = itemView.findViewById(R.id.problem_solved);
        }

        void bind(Problem problem, int position) {

            this.position = position;
            problemName.setText(problem.getName());

            problemDate.setText(problem.getCreated().toString());
            solvedImageView.setVisibility(problem.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            startActivity(ProblemPagerActivity.newIntent(getActivity(), position));
        }
    }
}
