package com.garycgregg.android.myfriendgauss3.content;

import android.os.Parcel;
import android.os.Parcelable;

public class CompleteProblem implements Parcelable {

    // A creator for this class
    public static final Parcelable.Creator<CompleteProblem> CREATOR =
            new Parcelable.Creator<CompleteProblem>() {

                @Override
                public CompleteProblem createFromParcel(Parcel parcel) {
                    return new CompleteProblem(parcel);
                }

                @Override
                public CompleteProblem[] newArray(int size) {
                    return new CompleteProblem[size];
                }
            };

    // The answer entries
    private Answer[] answerEntries;

    // The matrix entries
    private Matrix[] matrixEntries;

    // The problem metadata
    private Problem problem;

    // The vector entries
    private Vector[] vectorEntries;

    /**
     * Constructs the Complete Problem.
     */
    public CompleteProblem() {
        this(null);
    }

    /**
     * Constructs the Complete Problem from a parcel.
     *
     * @param parcel A parcel containing member data
     */
    protected CompleteProblem(Parcel parcel) {

        // Is the parcel not null?
        if (null != parcel) {

            // The parcel is not null. Read the problem metadata and answer entries.
            setProblem((Problem) parcel.readParcelable(Problem.class.getClassLoader()));
            setAnswerEntries((Answer[]) parcel.readParcelableArray(Answer.class.getClassLoader()));

            // Read the matrix entries and the vector entries.
            setMatrixEntries((Matrix[]) parcel.readParcelableArray(Matrix.class.getClassLoader()));
            setVectorEntries((Vector[]) parcel.readParcelableArray(Vector.class.getClassLoader()));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Gets the answer entries.
     *
     * @return The answer entries
     */
    public Answer[] getAnswerEntries() {
        return answerEntries;
    }

    /**
     * Gets the coefficient matrix entries.
     *
     * @return The coefficient matrix entries
     */
    public Matrix[] getMatrixEntries() {
        return matrixEntries;
    }

    /**
     * Gets the problem metadata.
     *
     * @return The problem metadata
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * Gets the vector entries.
     *
     * @return The vector entries
     */
    public Vector[] getVectorEntries() {
        return vectorEntries;
    }

    /**
     * Sets the answer entries.
     *
     * @param answerEntries The answer entries
     */
    public void setAnswerEntries(Answer[] answerEntries) {
        this.answerEntries = answerEntries;
    }

    /**
     * Sets the coefficient matrix entries.
     *
     * @param matrixEntries The coefficient matrix entries
     */
    public void setMatrixEntries(Matrix[] matrixEntries) {
        this.matrixEntries = matrixEntries;
    }

    /**
     * Sets the problem metadata.
     *
     * @param problem The problem metadata
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * Sets the vector entries.
     *
     * @param vectorEntries The vector entries
     */
    public void setVectorEntries(Vector[] vectorEntries) {
        this.vectorEntries = vectorEntries;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        // Write the vector entries and the coefficient matrix entries.
        parcel.writeParcelableArray(getVectorEntries(), flags);
        parcel.writeParcelableArray(getMatrixEntries(), flags);

        // Write the answer entries and the problem metadata.
        parcel.writeParcelableArray(getAnswerEntries(), flags);
        parcel.writeParcelable(getProblem(), flags);
    }
}
