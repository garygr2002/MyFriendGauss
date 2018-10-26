package com.garycgregg.android.myfriendgauss3.synchronization;

import android.support.annotation.NonNull;

import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public class NameSynchronizationTask extends SynchronizationTask<Problem> {

    /**
     * Constructs the name synchronization task.
     *
     * @param problemLab A problem lab
     * @param items      An array of items that need to be synchronized
     */
    public NameSynchronizationTask(@NonNull ProblemLab problemLab,
                                   @NonNull Problem[] items) {
        super(problemLab, items);
    }

    @Override
    protected void synchronize(@NonNull Problem item) {
        getProblemLab().updateName(item);
    }
}
