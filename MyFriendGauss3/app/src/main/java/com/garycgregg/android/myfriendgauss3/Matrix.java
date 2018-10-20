package com.garycgregg.android.myfriendgauss3;

public class Matrix extends BaseGaussEntry {

    // The column associated with the entry
    private int column;

    /**
     * Gets the column associated with the entry.
     *
     * @return The column associated with the entry
     */
    public int getColumn() {
        return column;
    }

    /**
     * Sets the column associated with the entry.
     *
     * @param column The column associated with the entry
     */
    public void setColumn(int column) {
        this.column = column;
    }
}
