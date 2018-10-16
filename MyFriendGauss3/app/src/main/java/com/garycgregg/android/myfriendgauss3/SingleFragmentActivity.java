package com.garycgregg.android.myfriendgauss3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public abstract class SingleFragmentActivity extends AppCompatActivity implements ProblemLabSource {

    private long nullProblemId;
    private ProblemLab problemLab;

    protected abstract Fragment createFragment();

    @Override
    public long getNullProblemId() {
        return nullProblemId;
    }

    @Override
    public ProblemLab getProblemLab() {
        return problemLab;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // See: https://stackoverflow.com/questions/9732761/prevent-the-keyboard-from-displaying-on-activity-start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        nullProblemId = ((GaussApplication) getApplication()).getNullProblemId();
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
}
