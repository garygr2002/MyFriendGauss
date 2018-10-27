package com.garycgregg.android.myfriendgauss3.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;
import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLabSource;
import com.garycgregg.android.myfriendgauss3.fragment.ProblemFragment;

import java.util.List;

public class ProblemPagerActivity extends AppCompatActivity implements ProblemLabSource {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String PREFIX = ProblemPagerActivity.class.getName();
    private static final String POSITION_ARGUMENT = String.format(FORMAT_STRING, PREFIX,
            "position");

    private ProblemLab problemLab;
    private List<Problem> problemList;

    public static Intent newIntent(Context packageContext, int position) {

        final Intent intent = new Intent(packageContext, ProblemPagerActivity.class);
        intent.putExtra(POSITION_ARGUMENT, position);
        return intent;
    }

    @Override
    public ProblemLab getProblemLab() {
        return problemLab;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_pager);

        problemLab = new ProblemLab(getApplicationContext());
        problemList = problemLab.getProblems();

        final ViewPager viewPager = findViewById(R.id.problem_view_pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return problemList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return ProblemFragment.createInstance(problemList.get(position).getProblemId(),
                        position);
            }
        });

        viewPager.setCurrentItem(getIntent().getIntExtra(POSITION_ARGUMENT, 0));
    }
}
