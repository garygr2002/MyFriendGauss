package com.garycgregg.android.myfriendgauss3.synchronization;

import android.support.annotation.NonNull;

import com.garycgregg.android.myfriendgauss3.content.Vector;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public class VectorSynchronizationTask extends SynchronizationTask<Vector> {

    /**
     * Constructs the vector synchronization task.
     *
     * @param problemLab A problem lab
     * @param items      An array of items that need to be synchronized
     */
    public VectorSynchronizationTask(@NonNull ProblemLab problemLab,
                                     @NonNull Vector[] items) {
        super(problemLab, items);
    }

    @Override
    protected void synchronize(@NonNull Vector item) {
        getProblemLab().addOrReplace(item);
    }
}
