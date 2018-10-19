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

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProblemId() {
        return problemId;
    }

    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    public Date getSolved() {
        return solved;
    }

    public boolean isSolved() {
        return null != getSolved();
    }

    public void setSolved(Date solved) {
        this.solved = solved;
    }

    public boolean isWriteLocked() {
        return FALSE != writeLocked;
    }

    public void setWriteLocked(boolean writeLocked) {
        this.writeLocked = writeLocked ? TRUE : FALSE;
    }
}
