package com.garycgregg.android.myfriendgauss3.synchronization;

import android.support.annotation.NonNull;

import com.garycgregg.android.myfriendgauss3.content.Answer;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public class AnswerSynchronizationTask extends SynchronizationTask<Answer> {

    /**
     * Constructs the answer synchronization task.
     *
     * @param problemLab A problem lab
     * @param items      An array of items that need to be synchronized
     */
    public AnswerSynchronizationTask(@NonNull ProblemLab problemLab,
                                     @NonNull Answer[] items) {
        super(problemLab, items);
    }

    @Override
    protected void synchronize(@NonNull Answer item) {
        getProblemLab().addOrReplace(item);
    }
}
