package com.garycgregg.android.myfriendgauss3.content;

import android.os.Parcel;
import android.os.Parcelable;

public class Matrix extends BaseGaussEntry {

    // A create for this class
    public static final Parcelable.Creator<Matrix> CREATOR =
            new Parcelable.Creator<Matrix>() {

                @Override
                public Matrix createFromParcel(Parcel parcel) {
                    return new Matrix(parcel);
                }

                @Override
                public Matrix[] newArray(int size) {
                    return new Matrix[size];
                }
            };

    // The column associated with the entry
    private int column;

    /**
     * Constructs the Matrix.
     */
    public Matrix() {
        this(null);
    }

    /**
     * Constructs the Matrix parcel.
     *
     * @param parcel A parcel containing member data
     */
    protected Matrix(Parcel parcel) {

        // Call the superclass constructor. Is the parcel not null?
        super(parcel);
        if (null != parcel) {

            // The parcel is not null. Read the member variables.
            setColumn(parcel.readInt());
        }
    }

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

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        // Write the member variables to the parcel.
        parcel.writeInt(getColumn());
        super.writeToParcel(parcel, flags);
    }
}
