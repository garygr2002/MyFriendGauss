package com.garycgregg.android.myfriendgauss3.content;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Problem implements Parcelable {

    // A creator for this class
    public static final Parcelable.Creator<Problem> CREATOR =
            new Parcelable.Creator<Problem>() {

                @Override
                public Problem createFromParcel(Parcel parcel) {
                    return new Problem(parcel);
                }

                @Override
                public Problem[] newArray(int size) {
                    return new Problem[size];
                }
            };

    // Constant for the unlocked problem state
    public static final int FALSE = 0;

    // Constant for the locked problem state
    public static final int TRUE = ~FALSE;

    // Constant for an invalid date
    private static final long INVALID_DATE = ~0;

    // The date the problem was created
    private Date created;

    // The number of dimensions in the problem
    private int dimensions;

    // The name of the problem
    private String name;

    // The ID associated with the problem
    private long problemId;

    // The date the problem was solved
    private Date solved;

    // TRUE if the problem is write locked, FALSE otherwise
    private int writeLocked;

    /**
     * Constructs the Problem.
     */
    public Problem() {
        this(null);
    }

    /**
     * Constructs the Problem from a parcel.
     *
     * @param parcel A parcel containing member data
     */
    protected Problem(Parcel parcel) {

        // Is the parcel not null?
        if (null != parcel) {

            // The parcel is not null. Read the problem ID and problem name.
            setProblemId(parcel.readLong());
            setName(parcel.readString());

            // Read the dimensions and the date the problem was created.
            setDimensions(parcel.readInt());
            setCreated(translateDate(parcel.readLong()));

            // Read the date the problem was solved, and the write lock flag.
            setSolved(translateDate(parcel.readLong()));
            setWriteLocked(isWriteLocked(parcel.readInt()));
        }
    }

    /**
     * Determines if a value indicates a write lock.
     *
     * @param writeLocked An integer value
     * @return True if the flag indicates a write lock, false otherwise
     */
    private static boolean isWriteLocked(int writeLocked) {
        return FALSE != writeLocked;
    }

    /**
     * Translates a date.
     *
     * @param date A date object
     * @return A long integer representation of the date
     */
    private static long translateDate(Date date) {
        return (null == date) ? INVALID_DATE : date.getTime();
    }

    /**
     * Translate a date.
     *
     * @param date A long integer representation of a date
     * @return A date object
     */
    private static Date translateDate(long date) {
        return (INVALID_DATE < date) ? null : new Date(date);
    }

    /**
     * Translates a write lock flag.
     *
     * @param writeLocked The write lock flag
     * @return TRUE if the flag is true, FALSE otherwise
     */
    private static int translateWriteLock(boolean writeLocked) {
        return writeLocked ? TRUE : FALSE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Gets the date the problem was created.
     *
     * @return The date the problem was created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Gets the number of dimensions in the problem.
     *
     * @return The number of dimensions in the problem
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * Gets the name of the problem.
     *
     * @return The name of the problem
     */
    public String getName() {
        return name;
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
     * Gets the date the problem was solved.
     *
     * @return The date the problem was solved
     */
    public Date getSolved() {
        return solved;
    }

    /**
     * Determines if the problem has been solved.
     *
     * @return True if the problem has been solved, false otherwise
     */
    public boolean isSolved() {
        return null != getSolved();
    }

    /**
     * Determine if the problem is write locked.
     *
     * @return True if the problem is write locked, false otherwise
     */
    public boolean isWriteLocked() {
        return isWriteLocked(writeLocked);
    }

    /**
     * Sets the date the problem was created.
     *
     * @param created The date the problem was created
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Sets the number of dimensions in the problem.
     *
     * @param dimensions The number of dimensions in the problem
     */
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * Sets the name of the name of the problem.
     *
     * @param name The name of the problem
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets the date the problem was solved.
     *
     * @param solved The date the problem was solved
     */
    public void setSolved(Date solved) {
        this.solved = solved;
    }

    /**
     * Sets whether the problem is write locked.
     *
     * @param writeLocked True if the problem is write locked, false otherwise
     */
    public void setWriteLocked(boolean writeLocked) {
        this.writeLocked = translateWriteLock(writeLocked);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        // Write the write lock flag and the date the problem was solved.
        parcel.writeInt(translateWriteLock(isWriteLocked()));
        parcel.writeLong(translateDate(getSolved()));

        // Write the date the problem was solved, and the number of dimensions.
        parcel.writeLong(translateDate(getCreated()));
        parcel.writeInt(getDimensions());

        // Write the problem name and the problem ID.
        parcel.writeString(getName());
        parcel.writeLong(getProblemId());
    }
}
