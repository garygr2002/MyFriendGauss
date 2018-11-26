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

    // Our problem lab
    private ProblemLab problemLab;

    /**
     * Creates a single fragment for this activity.
     *
     * @return A single fragment for this activity
     */
    protected abstract Fragment createFragment();

    @Override
    public ProblemLab getProblemLab() {
        return problemLab;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and create the problem lab. Set the content view.
        super.onCreate(savedInstanceState);
        problemLab = new ProblemLab(getApplicationContext());
        setContentView(R.layout.activity_single_fragment);

        /*
         * Get the fragment manager, and find the fragment container. Is the fragment container not
         * null?
         */
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (null == fragment) {

            // The fragment manager is not null. Create a new fragment for the container.
            fragmentManager.beginTransaction().add(R.id.fragment_container,
                    createFragment()).commit();
        }
    }

    @Override
    protected void onDestroy() {

        // Clear the problem lab, and call the superclass method.
        problemLab = null;
        super.onDestroy();
    }
}
