package com.garycgregg.android.myfriendgauss3.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;
import com.garycgregg.android.myfriendgauss3.database.ProblemLabSource;

public abstract class SingleFragmentActivity extends AppCompatActivity implements
        ProblemLabSource {

    private ProblemLab problemLab;

    protected abstract Fragment createFragment();

    @Override
    public ProblemLab getProblemLab() {
        return problemLab;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        problemLab = new ProblemLab(getApplicationContext());
        setContentView(R.layout.activity_single_fragment);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (null == fragment) {

            fragment = createFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container,
                    fragment).commit();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        problemLab = null;
    }
}
