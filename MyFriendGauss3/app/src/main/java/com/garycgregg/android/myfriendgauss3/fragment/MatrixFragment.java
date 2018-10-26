package com.garycgregg.android.myfriendgauss3.fragment;

import com.garycgregg.android.myfriendgauss3.content.Matrix;

public class MatrixFragment extends NumbersFragment<Matrix> {

    @Override
    public Matrix[] exportChanges() {

        // TODO: Change this.
        return new Matrix[0];
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
