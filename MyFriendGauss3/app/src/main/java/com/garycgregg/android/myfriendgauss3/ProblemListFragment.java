package com.garycgregg.android.myfriendgauss3;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProblemListFragment extends Fragment {

    private ProblemAdapter adapter;
    private ProblemLab problemLab;
    private RecyclerView problemRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_problem_list, container,
                false);

        final Context context = getActivity();
        problemLab = ((ProblemLabSource) context).getProblemLab();

        problemRecyclerView = view.findViewById(R.id.problem_recycler_view);
        problemRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        updateUI();
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        updateUI();
    }

    private void updateUI() {

        final List<Problem> problemList = problemLab.getProblems();
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

        public ProblemAdapter(List<Problem> problemList) {
            this.problemList = problemList;
        }

        @Override
        public void onBindViewHolder(ProblemHolder holder, int position) {
            holder.bind(problemList.get(position));
        }

        @Override
        public ProblemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProblemHolder(LayoutInflater.from(getActivity()), parent);
        }

        @Override
        public int getItemCount() {
            return problemList.size();
        }

        public void setProblems(List<Problem> problemList) {
            this.problemList = problemList;
        }
    }

    private class ProblemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Problem problem;
        private final TextView problemDate;
        private final TextView problemName;
        private final ImageView solvedImageView;

        public ProblemHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_problem, parent, false));
            itemView.setOnClickListener(this);

            problemName = itemView.findViewById(R.id.problem_name);
            problemDate = itemView.findViewById(R.id.problem_date);
            solvedImageView = itemView.findViewById(R.id.problem_solved);
        }

        public void bind(Problem problem) {

            this.problem = problem;
            problemName.setText(problem.getName());

            problemDate.setText(problem.getCreated().toString());
            solvedImageView.setVisibility(problem.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            startActivity(ProblemActivity.newIntent(getActivity(), problem.getProblemId(),
                    problemLab.getNullId()));
        }
    }
}
