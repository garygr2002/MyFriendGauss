package com.garycgregg.android.myfriendgauss3;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.garycgregg.android.myfriendgauss2.R;

import java.util.List;

public class ProblemListFragment extends Fragment {

    private ProblemAdapter adapter;
    private ProblemLab problemLab;
    private RecyclerView problemRecyclerView;

    @Override
    public void onAttach(Context context) {

        // TODO: Move creating the problem lab to the ProblemActivity.
        super.onAttach(context);
        problemLab = new ProblemLab(context);
    }

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

    private void updateUI() {
        problemRecyclerView.setAdapter(adapter = new ProblemAdapter(problemLab.getProblems()));
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
    }

    private class ProblemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Problem problem;
        private TextView problemDate;
        private TextView problemName;

        public ProblemHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_problem, parent, false));
            itemView.setOnClickListener(this);

            problemName = itemView.findViewById(R.id.problem_name);
            problemDate = itemView.findViewById(R.id.problem_date);
        }

        public void bind(Problem problem) {

            this.problem = problem;
            problemName.setText(problem.getName());
            problemDate.setText(problem.getCreated().toString());
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), String.format("'%s' has been clicked!",
                    problem.getName()), Toast.LENGTH_SHORT).show();
        }
    }
}
