package com.garycgregg.android.myfriendgauss3.content;

import android.os.Parcel;
import android.os.Parcelable;

public class Answer extends BaseGaussEntry {

    // A creator for this class
    public static final Parcelable.Creator<Answer> CREATOR =
            new Parcelable.Creator<Answer>() {

                @Override
                public Answer createFromParcel(Parcel parcel) {
                    return new Answer(parcel);
                }

                @Override
                public Answer[] newArray(int size) {
                    return new Answer[size];
                }
            };

    /**
     * Constructs the Answer.
     */
    public Answer() {
        this(null);
    }

    /**
     * Constructs the Answer from a parcel.
     *
     * @param parcel A parcel containing member data
     */
    protected Answer(Parcel parcel) {
        super(parcel);
    }
}
