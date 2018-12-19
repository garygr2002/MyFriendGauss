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

    // Constant for integer encoded FALSE boolean values
    public static final int FALSE = 0;

    // The invalid rank
    public static final int INVALID_RANK = -1;

    // The maximum number of problem dimensions
    public static final int MAX_DIMENSIONS = 15;

    // The maximum entry precision
    public static final int MAX_PRECISION = 15;

    // The minimum number of problem dimensions
    public static final int MIN_DIMENSIONS = 1;

    // The minimum entry precision
    public static final int MIN_PRECISION = 1;

    // The null problem ID
    public static final long NULL_ID = 0L;

    // Constant for integer encoded TRUE boolean values
    public static final int TRUE = ~FALSE;

    // Constant for an invalid date
    private static final long INVALID_DATE = -1L;

    // The date the problem was created
    private Date created;

    // The number of dimensions in the problem
    private int dimensions;

    // The name of the problem
    private String name;

    // The precision of the problem
    private int precision;

    // The ID associated with the problem
    private long problemId;

    // The rank of the problem
    private int rank;

    // TRUE if scientific notation is to be used in entries display, FALSE otherwise
    private int scientific;

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

            // Read the precision, and the scientific notation flag.
            setPrecision(parcel.readInt());
            setScientific(translateBoolean(parcel.readInt()));

            // Read the rank, the date the problem was solved, and the write lock flag.
            setRank(parcel.readInt());
            setSolved(translateDate(parcel.readLong()));
            setWriteLocked(translateBoolean(parcel.readInt()));
        }
    }

    /**
     * Translate an encoded boolean integer.
     *
     * @param encodedBoolean An encoded boolean integer
     * @return True if the flag indicates a write lock, false otherwise
     */
    public static boolean translateBoolean(int encodedBoolean) {
        return FALSE != encodedBoolean;
    }

    /**
     * Translates a boolean.
     *
     * @param bool The boolean
     * @return TRUE if the boolean is true, FALSE otherwise
     */
    public static int translateBoolean(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    /**
     * Translate a date.
     *
     * @param date A long integer representation of a date
     * @return A date object
     */
    public static Date translateDate(long date) {
        return (INVALID_DATE < date) ? null : new Date(date);
    }

    /**
     * Translates a date.
     *
     * @param date A date object
     * @return A long integer representation of the date
     */
    public static long translateDate(Date date) {
        return (null == date) ? INVALID_DATE : date.getTime();
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
     * Gets the precision of the problem.
     *
     * @return The precision of the problem
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Gets the ID associated with the problem.
     *
     * @return The ID associated with the problem
     */
    public long getProblemId() {
        return problemId;
    }

    public int getRank() {
        return rank;
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
     * Determines if the problem entries use scientific notation.
     *
     * @return True if the problem entries use scientific notation, false otherwise
     */
    public boolean isScientific() {
        return translateBoolean(scientific);
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
     * Determines if the problem is write locked.
     *
     * @return True if the problem is write locked, false otherwise
     */
    public boolean isWriteLocked() {
        return translateBoolean(writeLocked);
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
     * Sets the precision of the problem.
     *
     * @param precision The precision of the problem
     */
    public void setPrecision(int precision) {
        this.precision = precision;
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
     * Sets the rank of the problem.
     *
     * @param rank The rank of the problem
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Sets whether the problem entries use scientific notation.
     *
     * @param scientific True if the problem entries use scientific notation, false otherwise
     */
    public void setScientific(boolean scientific) {
        this.scientific = translateBoolean(scientific);
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
        this.writeLocked = translateBoolean(writeLocked);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        // Write the write lock flag, the date the problem was solved, and the rank.
        parcel.writeInt(translateBoolean(isWriteLocked()));
        parcel.writeLong(translateDate(getSolved()));
        parcel.writeInt(getRank());

        // Write the scientific notation flag, and the precision.
        parcel.writeInt(translateBoolean(isScientific()));
        parcel.writeInt(getPrecision());

        // Write the date the problem was solved, and the number of dimensions.
        parcel.writeLong(translateDate(getCreated()));
        parcel.writeInt(getDimensions());

        // Write the problem name and the problem ID.
        parcel.writeString(getName());
        parcel.writeLong(getProblemId());
    }
}
