package com.garycgregg.android.myfriendgauss3.fragment;

import android.support.annotation.NonNull;
import android.util.SparseArray;

class RecordTracker<T> {

    // An array of containers (see container definition, below)
    private final SparseArray<Container<T>> array;

    // The record capacity of the tracker
    private final int capacity;

    // The count of existing records
    private int count;

    // A count listener
    private CountListener listener;

    /**
     * Constructs the record tracker.
     *
     * @param capacity The record capacity of the tracker
     * @param listener A count listener
     */
    public RecordTracker(int capacity, CountListener listener) {

        // Set the member variables.
        array = new SparseArray<>(this.capacity = capacity);
        setListener(listener);
    }

    /**
     * Constructs the record tracker.
     *
     * @param capacity The record capacity of the tracker
     */
    public RecordTracker(int capacity) {
        this(capacity, null);
    }

    /**
     * Checks if a call to a count listener is required.
     *
     * @param listener The count listener
     * @param oldCount The old record count
     * @param newCount The new record count
     */
    private void callListener(@NonNull CountListener listener, int oldCount, int newCount) {

        // Is the new count at capacity?
        if (capacity == newCount) {

            /*
             * The new count is at capacity. Call the listener if the old count is not at
             * capacity.
             */
            if (capacity != oldCount) {
                listener.onEqual();
            }
        }

        // The new count is not at capacity. Is the old count at capacity?
        else if (capacity == oldCount) {

            // Call the listener if the new count is greater than capacity.
            if (capacity < newCount) {
                listener.onGreater();
            }

            // Call the listener if the new count is less than capacity.
            else {
                listener.onLess();
            }
        }
    }

    /**
     * Clears the tracker.
     */
    public void clearTracker() {

        // Clear the count, then clear the array.
        count = 0;
        array.clear();
    }

    /**
     * Gets a record in the tracker.
     *
     * @param key The key of the record
     * @return The record if it exists, null otherwise
     */
    public T get(int key) {

        /*
         * Try to find a container with the given key. Return null if we find no such container,
         * otherwise return the contained record.
         */
        final Container<T> container = array.get(key);
        return (null == container) ? null : container.getRecord();
    }

    /**
     * Gets the count of existing records.
     *
     * @return The count of existing records
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the count listener.
     *
     * @return The count listener
     */
    public CountListener getListener() {
        return listener;
    }

    /**
     * Maintains the count of existing records.
     *
     * @param container A container that may be changing state
     * @param newState  The new state of the container
     * @return The container that was passed in
     */
    private Container<T> maintainCount(@NonNull Container<T> container, @NonNull State newState) {

        /*
         * Track the old count. Maintain the count based on whether the record exists in the
         * database. Set the new state in the container.
         */
        final int oldCount = count;
        maintainCount(container.getState(), newState, container.doesExist());
        container.setState(newState);

        // Check if a listener call is required.
        if (null != listener) {
            callListener(listener, oldCount, count);
        }

        // Return the container.
        return container;
    }

    /**
     * Maintains the count of existing records.
     *
     * @param oldState The old state of a record
     * @param newState The new state of a record
     * @param exists   Whether the record currently exists in the database
     */
    private void maintainCount(@NonNull State oldState, @NonNull State newState,
                               boolean exists) {

        // Declare local variables. Is the 'exists' flag set?
        int value;
        State targetState;
        if (exists) {

            // The 'exists' flag is set. Initialize the local variables thusly.
            value = 1;
            targetState = State.DELETED;
        }

        // The 'exists' flag is clear.
        else {

            // Initialize the local variables based on the clear 'exists' flag.
            value = -1;
            targetState = State.CHANGED;
        }

        // Does the target state equal the new state?
        if (targetState.equals(newState)) {

            /*
             * The target state equals the new state. Adjust the count based on the
             * increment/decrement value if the target state equals the old state.
             */
            if (!targetState.equals(oldState)) {
                count -= value;
            }
        }

        /*
         * The target state does not equal the new state. Adjust the count based on the
         * increment/decrement value if the target state equals the old state.
         */
        else if (targetState.equals(oldState)) {
            count += value;
        }
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
     * @param action The actions to take (see definition for a <code>RecordAction</code>
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

        // Try to find an existing container. Is there an existing container?
        Container<T> container = array.get(key);
        if (null != container) {

            /*
             * There is an existing container. Keep track of the count of existing records, as
             * this record is being replaced.
             */
            maintainCount(container, State.DELETED);
        }

        /*
         * Create a new container. Set its state to deleted so that we will correctly increment
         * the count when the record transitions to the 'not changed' state.
         */
        container = new Container<>(record, exists);
        container.setState(State.DELETED);
        array.put(key, maintainCount(container, State.NOT_CHANGED));
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
            maintainCount(container, deleted ? State.DELETED : State.CHANGED);
        }

        // Return whether we found a container, and changed its state.
        return found;
    }

    /**
     * Sets the count listener.
     *
     * @param listener The count listener
     */
    public void setListener(CountListener listener) {
        this.listener = listener;
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

    public interface CountListener {

        /**
         * Indicates the record count has transitioned to equal capacity.
         */
        void onEqual();

        /**
         * Indicates the record count has transitioned to greater than capacity.
         */
        void onGreater();

        /**
         * Indicates the record count has transitioned to less than capacity.
         */
        void onLess();
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

                    /*
                     * The current state of the record is changed. Perform a change action, and
                     * set the container to indicate an existing record if the change action
                     * succeeds.
                     */
                    container.setExists(recordAction.onChanged(container.getRecord()));
                    break;

                case DELETED:

                    // The current state of the record is deleted. Does the record exist?
                    if (container.doesExist()) {

                        /*
                         * The current state of the record is deleted, and the record exists.
                         * Perform a delete action, and set the container to indicate a non-
                         * existent record if the delete succeeds.
                         */
                        container.setExists(!recordAction.onDeleted(container.getRecord()));
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

            // At the end, reset the container state to not changed.
            container.setState(State.NOT_CHANGED);
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
         * Determines the existence state of the record.
         *
         * @return True if the record is exists, false otherwise
         */
        public boolean doesExist() {
            return exists;
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
        void setState(@NonNull State state) {
            this.state = state;
        }
    }
}
