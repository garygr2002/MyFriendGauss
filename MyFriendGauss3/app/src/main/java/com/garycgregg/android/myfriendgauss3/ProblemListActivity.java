package com.garycgregg.android.myfriendgauss3;

import android.support.v4.app.Fragment;

public class ProblemListActivity extends SingleFragmentActivity {

    @Override
    public ProblemLab getProblemLab() {
        return new ProblemLab(getApplicationContext());
    }

    @Override
    protected Fragment createFragment() {
        return new ProblemListFragment();
    }
}
