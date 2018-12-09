package com.garycgregg.android.myfriendgauss3.content;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseGaussEntry implements Parcelable {

    // A creator for this class
    public static final Parcelable.Creator<BaseGaussEntry> CREATOR =
            new Parcelable.Creator<BaseGaussEntry>() {

                @Override
                public BaseGaussEntry createFromParcel(Parcel parcel) {
                    return new BaseGaussEntry(parcel);
                }

                @Override
                public BaseGaussEntry[] newArray(int size) {
                    return new BaseGaussEntry[size];
                }
            };

    // An invalid entry
    public static double INVALID_ENTRY = Double.NaN;

    // The entry in the row
    private double entry;

    // The ID associated with the problem
    private long problemId;

    // The row of the entry
    private int row;

    /**
     * Constructs the base Gauss entry.
     */
    public BaseGaussEntry() {
        this(null);
    }

    /**
     * Constructs the base Gauss entry from a parcel.
     *
     * @param parcel A parcel containing member data
     */
    protected BaseGaussEntry(Parcel parcel) {

        // Is the parcel not null?
        if (null != parcel) {

            // The parcel is not null. Read the member variables.
            setProblemId(parcel.readLong());
            setRow(parcel.readInt());
            setEntry(parcel.readDouble());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        // Write the member variables to the parcel.
        parcel.writeDouble(getEntry());
        parcel.writeInt(getRow());
        parcel.writeLong(getProblemId());
    }
}
