package com.garycgregg.android.myfriendgauss3;

import android.support.v4.app.Fragment;

public class GaussActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ProblemFragment.createInstance(10);
    }
}
