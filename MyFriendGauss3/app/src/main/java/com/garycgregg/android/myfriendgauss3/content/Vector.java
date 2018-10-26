package com.garycgregg.android.myfriendgauss3.content;

import android.os.Parcel;
import android.os.Parcelable;

public class Vector extends BaseGaussEntry {

    // A creator for this class
    public static final Parcelable.Creator<Vector> CREATOR =
            new Parcelable.Creator<Vector>() {

                @Override
                public Vector createFromParcel(Parcel parcel) {
                    return new Vector(parcel);
                }

                @Override
                public Vector[] newArray(int size) {
                    return new Vector[size];
                }
            };

    /**
     * Constructs the Vector.
     */
    public Vector() {
        this(null);
    }

    /**
     * Constructs the Vector from a parcel.
     *
     * @param parcel A parcel containing member data
     */
    protected Vector(Parcel parcel) {
        super(parcel);
    }
}
