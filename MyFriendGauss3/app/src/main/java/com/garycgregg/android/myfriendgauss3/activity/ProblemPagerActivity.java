package com.garycgregg.android.myfriendgauss3.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;
import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.database.ProblemLabSource;
import com.garycgregg.android.myfriendgauss3.fragment.ProblemFragment;

import java.util.List;

public class ProblemPagerActivity extends AppCompatActivity implements ProblemFragment.Callbacks,
        ProblemLabSource {

    // The format of an instance argument key
    private static final String ARGUMENT_FORMAT_STRING = "%s.%s_argument";

    // The prefix for instance arguments
    private static final String PREFIX_STRING = ProblemPagerActivity.class.getName();

    // The position argument key
    private static final String POSITION_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "position");

    // A tag for logging statements
    private static final String TAG = ProblemPagerActivity.class.getSimpleName();

    // Our pager adapter
    private PagerAdapter pagerAdapter;

    // Our problem lab
    private ProblemLab problemLab;

    // Our list of problems
    private List<Problem> problemList;

    /**
     * Creates a new intent for this activity than contains the list position to display.
     *
     * @param packageContext The context for this activity
     * @param position       The list position to display
     * @return A new intent for this activity
     */
    public static Intent newIntent(Context packageContext, int position) {

        /*
         * Create an new intent for this context and class. Add the position argument, and return
         * the intent.
         */
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

        // Call the superclass method, and set the content view.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_pager);

        // Create the problem lab, and get the list of problems to display.
        problemLab = new ProblemLab(getApplicationContext());
        problemList = problemLab.getProblems();

        // Find the view pager, and give it a fragment state pager adapter.
        final ViewPager viewPager = findViewById(R.id.problem_view_pager);
        viewPager.setAdapter(pagerAdapter =
                new FragmentStatePagerAdapter(getSupportFragmentManager()) {

                    @Override
                    public int getCount() {
                        return problemList.size();
                    }

                    @Override
                    public Fragment getItem(int position) {
                        return ProblemFragment.createInstance(
                                problemList.get(position).getProblemId(), position);
                    }

                    @Override
                    public int getItemPosition(Object object) {

                        /*
                         * Let the default item position be whatever the superclass thinks it
                         * should be. Is the argument an instance of a problem fragment?
                         */
                        int itemPosition = super.getItemPosition(object);
                        final ProblemFragment problemFragment =
                                (object instanceof ProblemFragment) ? ((ProblemFragment) object) :
                                        null;
                        if (null != problemFragment) {

                            /*
                             * The argument is an instance of a problem fragment. Redraw the
                             * fragment if it needs to be redrawn.
                             */
                            itemPosition = problemFragment.isNeedingRedraw() ? POSITION_NONE :
                                    POSITION_UNCHANGED;
                        }

                        // Return the item position.
                        return itemPosition;
                    }
                });

        // Set the current item in the view pager.
        viewPager.setCurrentItem(getIntent().getIntExtra(POSITION_ARGUMENT, 0));
    }

    @Override
    public void onDimensionsChanged(int position, long problemId, int dimensions) {

        Log.d(TAG, String.format(
                "onDimensionsChanged(position: %d, problemId: %d, dimensions: %d)", position,
                problemId, dimensions));

        // Simply notify the pager adapter of a data set change.
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProblemCopied(int position, long problemId, String problemName, long newProblemId) {

        // TODO: Implement this.
        Log.d(TAG, String.format(
                "onProblemCopied(position: %d, problemId: %d, problemName: '%s', " +
                        "newProblemId: %d)",
                position, problemId, problemName, newProblemId));

        // Simply notify the pager adapter of a data set change.
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onValuesSet(int position, long problemId, double value, boolean allEntries) {

        // TODO: Implement this.
        Log.d(TAG, String.format(
                "onValuesSet(position: %d, problemId: %d, value: %f, " +
                        "allEntries: %s)",
                position, problemId, value, allEntries ? "true" : "false"));

        // Simply notify the pager adapter of a data set change.
        pagerAdapter.notifyDataSetChanged();
    }
}
