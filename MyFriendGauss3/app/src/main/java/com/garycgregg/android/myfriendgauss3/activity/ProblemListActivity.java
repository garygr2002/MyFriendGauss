package com.garycgregg.android.myfriendgauss3.activity;

import android.support.v4.app.Fragment;

import com.garycgregg.android.myfriendgauss3.fragment.ProblemListFragment;

public class ProblemListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ProblemListFragment();
    }
}
