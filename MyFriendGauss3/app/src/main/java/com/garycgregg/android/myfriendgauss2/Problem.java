package com.garycgregg.android.myfriendgauss2;

import java.util.Date;

public class Problem {

    private Date created;
    private int dimensions;
    private String name;
    private Integer problemId;
    private Date solved;
    private int writeLocked;

    public static int getFalse() {
        return 0;
    }

    public static int getTrue() {
        return -1;
    }

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

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public Date getSolved() {
        return solved;
    }

    public void setSolved(Date solved) {
        this.solved = solved;
    }

    public boolean isWriteLocked() {
        return getFalse() != writeLocked;
    }

    public void setWriteLocked(boolean writeLocked) {
        this.writeLocked = writeLocked ? getTrue() : getFalse();
    }
}
