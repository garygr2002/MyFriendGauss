package com.garycgregg.android.myfriendgauss3;

import java.util.Date;

public class Problem {

    public static final int FALSE = 0;
    public static final int TRUE = ~FALSE;

    private Date created;
    private int dimensions;
    private String name;
    private long problemId;
    private Date solved;
    private int writeLocked;

    public Date getCreated() {
        return created;
    }

    public int getDimensions() {
        return dimensions;
    }

    public String getName() {
        return name;
    }

    public long getProblemId() {
        return problemId;
    }

    public Date getSolved() {
        return solved;
    }

    public boolean isSolved() {
        return null != getSolved();
    }

    public boolean isWriteLocked() {
        return FALSE != writeLocked;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    public void setSolved(Date solved) {
        this.solved = solved;
    }

    public void setWriteLocked(boolean writeLocked) {
        this.writeLocked = writeLocked ? TRUE : FALSE;
    }
}
