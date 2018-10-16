package com.garycgregg.android.myfriendgauss3;

interface ProblemLabSource {

    /**
     * Gets the null problem ID to be used in the problem lab.
     *
     * @return The null problem ID to be used in the problem lab
     */
    long getNullProblemId();

    /**
     * Gets a problem lab.
     *
     * @return A problem lab
     */
    ProblemLab getProblemLab();
}
