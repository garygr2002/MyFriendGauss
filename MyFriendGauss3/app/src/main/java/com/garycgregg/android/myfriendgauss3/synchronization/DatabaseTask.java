package com.garycgregg.android.myfriendgauss3.synchronization;

import android.support.annotation.NonNull;

import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

class DatabaseTask {

    // A problem lab
    private final ProblemLab problemLab;

    /**
     * Constructs the database task.
     *
     * @param problemLab A problem lab
     */
    public DatabaseTask(@NonNull ProblemLab problemLab) {
        this.problemLab = problemLab;
    }

    /**
     * Get the problem lab.
     *
     * @return The problem lab
     */
    protected ProblemLab getProblemLab() {
        return problemLab;
    }
}
