package com.garycgregg.android.myfriendgauss3.synchronization;

import android.support.annotation.NonNull;

import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

public abstract class SynchronizationTask<T> extends DatabaseTask {

    // An array of items that need to be synchronized
    private final T[] items;

    /**
     * Constructs the synchronization task.
     *
     * @param problemLab A problem lab
     * @param items      An array of items that need to be synchronized
     */
    public SynchronizationTask(@NonNull ProblemLab problemLab, @NonNull T[] items) {

        super(problemLab);
        this.items = items;
    }

    /**
     * Synchronizes all items.
     */
    public void synchronize() {

        // Synchronize each item in the item list.
        for (T item : items) {
            synchronize(item);
        }
    }

    /**
     * Synchronizes a single item.
     *
     * @param item The item to synchronize
     */
    protected abstract void synchronize(@NonNull T item);
}
