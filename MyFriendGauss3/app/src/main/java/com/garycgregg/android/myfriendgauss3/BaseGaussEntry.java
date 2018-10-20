package com.garycgregg.android.myfriendgauss3;

class BaseGaussEntry {

    private double entry;
    private long problemId;
    private int row;

    public double getEntry() {
        return entry;
    }

    public long getProblemId() {
        return problemId;
    }

    public int getRow() {
        return row;
    }

    public void setEntry(double entry) {
        this.entry = entry;
    }

    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
