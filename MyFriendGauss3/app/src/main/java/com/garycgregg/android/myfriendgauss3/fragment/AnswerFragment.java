package com.garycgregg.android.myfriendgauss3.fragment;

import com.garycgregg.android.myfriendgauss3.content.Answer;

public class AnswerFragment extends NumbersFragment<Answer> {

    @Override
    public Answer[] exportChanges() {

        // TODO: Change this.
        return new Answer[0];
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
