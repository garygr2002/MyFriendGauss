package com.garycgregg.android.myfriendgauss3.fragment;

import com.garycgregg.android.myfriendgauss3.content.Vector;

public class VectorFragment extends NumbersFragment<Vector> {

    @Override
    public Vector[] exportChanges() {

        // TODO: Change this.
        return new Vector[0];
    }

    @Override
    public void onDestroyView() {

        /*
         * Release the change collections and call through to the super class method. TODO: More
         * class specific work.
         */
        release();
        super.onDestroyView();
    }
}
