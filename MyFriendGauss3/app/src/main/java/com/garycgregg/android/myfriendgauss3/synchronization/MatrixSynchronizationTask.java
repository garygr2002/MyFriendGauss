package com.garycgregg.android.myfriendgauss3.synchronization;

import android.support.annotation.NonNull;

import com.garycgregg.android.myfriendgauss3.content.Matrix;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public class MatrixSynchronizationTask extends SynchronizationTask<Matrix> {

    /**
     * Constructs the matrix synchronization task.
     *
     * @param problemLab A problem lab
     * @param items      An array of items that need to be synchronized
     */
    public MatrixSynchronizationTask(@NonNull ProblemLab problemLab,
                                     @NonNull Matrix[] items) {
        super(problemLab, items);
    }

    @Override
    protected void synchronize(@NonNull Matrix item) {
        getProblemLab().add(item);
    }
}
