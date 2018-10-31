package com.garycgregg.android.myfriendgauss3.fragment;

import android.util.SparseArray;

class RecordTracker<T> {

    // An array of containers (see container definition, below)
    private final SparseArray<Container<T>> array;

    // The container action to clear each record in the array
    private final ContainerAction<T> clearAction = new ContainerAction<T>() {

        @Override
        public void perform(Container<T> container) {

            // Set the container state to existing, and its state to not changed.
            container.setExists(true);
            container.setState(State.NOT_CHANGED);
        }
    };

    /**
     * Constructs the record tracker.
     *
     * @param initialCapacity The initial record capacity of the tracker
     */
    public RecordTracker(int initialCapacity) {
        array = new SparseArray<>(initialCapacity);
    }

    /**
     * Clears changes in the array.
     */
    public void clearChanges() {
        performAction(clearAction);
    }

    /**
     * Clears the tracker.
     */
    public void clearTracker() {
        array.clear();
    }

    /**
     * Performs an action on each container in the array.
     *
     * @param action The action to perform on each container in the array
     */
    private void performAction(ContainerAction<T> action) {

        /*
         * Declare a container object, and get the size of the tracking array. Cycle for each
         * record in the tracker.
         */
        Container<T> container;
        final int size = array.size();
        for (int i = 0; i < size; ++i) {

            // Perform the action.
            action.perform(array.valueAt(i));
        }
    }

    /**
     * Performs actions on each record in the tracker.
     *
     * @param action The actions to take (see definition for a <code>RecordAction</code>.
     */
    public void performAction(RecordAction<T> action) {
        performAction(new CallerContainerAction<>(action));
    }

    /**
     * Puts a record in the tracker.
     *
     * @param key    The key of the record
     * @param record The record to put in the tracker
     * @param exists True if the record exists, false otherwise
     */
    public void put(int key, T record, boolean exists) {
        array.put(key, new Container<T>(record, exists));
    }

    /**
     * Sets a change for a record in the tracker.
     *
     * @param key     The key of the record
     * @param deleted True if the record is being deleted, false otherwise
     * @return True if the tracker found a record with the given key, false otherwise
     */
    public boolean set(int key, boolean deleted) {

        // Try to find a container with the given key. Did we find such a container?
        final Container<T> container = array.get(key);
        final boolean found = (null != container);
        if (found) {

            /*
             * We found a container with the given key. Set the state of the container to 'deleted'
             * if the flag so indicates. Otherwise set the state to 'changed.'
             */
            container.setState(deleted ? State.DELETED : State.CHANGED);
        }

        // Return whether we found a container, and changed its state.
        return found;
    }

    // An enumerator indicating the current state of a record
    private enum State {
        CHANGED, DELETED, NOT_CHANGED
    }

    private interface ContainerAction<U> {

        /**
         * Performs an action on a container.
         *
         * @param container The container on which to perform the action
         */
        void perform(Container<U> container);
    }

    public interface RecordAction<U> {

        /**
         * Performs an action if a record has been changed.
         *
         * @param record The record that has been changed
         * @return True if the action was successfully completed, false otherwise
         */
        boolean onChanged(U record);

        /**
         * Performs an action if a record has been deleted.
         *
         * @param record The record that has been deleted
         * @return True if the action was successfully completed, false otherwise
         */
        boolean onDeleted(U record);
    }

    private static class CallerContainerAction<U> implements ContainerAction<U> {

        // The record action
        private final RecordAction<U> recordAction;

        /**
         * Constructs the caller container action.
         *
         * @param recordAction The action to perform on each record
         */
        public CallerContainerAction(RecordAction<U> recordAction) {
            this.recordAction = recordAction;
        }

        /**
         * Gets the record action.
         *
         * @return The record action
         */
        public RecordAction<U> getRecordAction() {
            return recordAction;
        }

        @Override
        public void perform(Container<U> container) {

            /*
             * Get the container at the first/next index. Determine the state of its contained
             * record.
             */
            final RecordAction<U> recordAction = getRecordAction();
            switch (container.getState()) {

                case CHANGED:

                    // The current state of the record is changed. Perform a change action.
                    recordAction.onChanged(container.getRecord());
                    break;

                case DELETED:

                    /*
                     * The current state of the record is deleted. Perform a delete action only if
                     * the record currently exists.
                     */
                    if (container.isExists()) {
                        recordAction.onDeleted(container.getRecord());
                    }

                    // Do not fall through!
                    break;

                default:

                    /*
                     * There is nothing to do for the NOT_CHANGED state, either for exists or
                     * not-exists records.
                     */
                    break;
            }
        }
    }

    private static class Container<U> {

        // The contained record
        private final U record;

        // Indicates record exists
        private boolean exists;

        // The current state of the record
        private State state;

        /**
         * Constructs the container.
         *
         * @param record The record to be placed inside the container
         * @param exists True if a record is exists, false otherwise
         */
        public Container(U record, boolean exists) {

            // Set the member variables.
            this.record = record;
            setExists(exists);
            setState(State.NOT_CHANGED);
        }

        /**
         * Gets the contained record.
         *
         * @return The contained record
         */
        public U getRecord() {
            return record;
        }

        /**
         * Gets the current state of the record.
         *
         * @return The current state of the record
         */
        State getState() {
            return state;
        }

        /**
         * Determines the existence state of the record.
         *
         * @return True if the record is exists, false otherwise
         */
        public boolean isExists() {
            return exists;
        }

        /**
         * Sets whether a record exists.
         *
         * @param exists True if a record exists, false otherwise
         */
        public void setExists(boolean exists) {
            this.exists = exists;
        }

        /**
         * Sets the current state of the record.
         *
         * @param state The current state of the record
         */
        void setState(State state) {
            this.state = state;
        }
    }
}
