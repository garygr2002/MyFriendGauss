package com.garycgregg.android.myfriendgauss3;

class BaseGaussEntry {

    // The entry in the row
    private double entry;

    // The ID associated with the problem
    private long problemId;

    // The row of the entry
    private int row;

    /**
     * Gets the entry in the row.
     *
     * @return The entry in the row
     */
    public double getEntry() {
        return entry;
    }

    /**
     * Gets the ID associated with the problem.
     *
     * @return The ID associated with the problem
     */
    public long getProblemId() {
        return problemId;
    }

    /**
     * Gets the row of the entry.
     *
     * @return The row of the entry
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row of the entry.
     *
     * @param entry The row of the entry
     */
    public void setEntry(double entry) {
        this.entry = entry;
    }

    /**
     * Sets the ID associated with the problem.
     *
     * @param problemId The ID associated with the problem
     */
    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    /**
     * Sets the row of the entry.
     *
     * @param row The row of the entry
     */
    public void setRow(int row) {
        this.row = row;
    }
}
