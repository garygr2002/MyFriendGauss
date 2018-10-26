package com.garycgregg.android.myfriendgauss3.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import java.util.ArrayList;
import java.util.List;

public class ProblemTaskLoader extends AsyncTaskLoader<List<Problem>> {

    // The problem ID
    private final Long problemId;

    // The problem lab
    private final ProblemLab problemLab;

    /**
     * Constructs the problem task loader.
     *
     * @param context    The context of the loader
     * @param problemLab The problem lab to be used by the loader
     * @param problemId  The problem ID to be use by the loader
     */
    public ProblemTaskLoader(Context context, @NonNull ProblemLab problemLab, Long problemId) {

        // Call through to the superclass constructor, and set the member variables.
        super(context);
        this.problemLab = problemLab;
        this.problemId = problemId;
    }

    @Override
    public List<Problem> loadInBackground() {

        /*
         * Note: This method returns a *locked* problem. Create a problem list to act as a return
         * value. Try to get a problem indexed by the problem ID. Does the problem exist?
         */
        final List<Problem> problemList = new ArrayList<>();
        final Problem lockedProblem = problemLab.getProblem(problemId);
        if (null != lockedProblem) {

            // The problem exists. Add it to the list.
            problemList.add(lockedProblem);
        }

        // Return the problem list.
        return problemList;
    }
}
