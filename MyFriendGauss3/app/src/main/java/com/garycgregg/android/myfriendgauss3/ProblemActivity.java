package com.garycgregg.android.myfriendgauss3;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ProblemActivity extends SingleFragmentActivity {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String PREFIX = ProblemActivity.class.getName();
    private static final String ID_ARGUMENT = String.format(FORMAT_STRING, PREFIX, "problem_id");
    private static final String NULL_ID_ARGUMENT = String.format(FORMAT_STRING, PREFIX,
            "null_id");

    public static Intent newIntent(Context packageContext, long problemId, long nullId) {

        final Intent intent = new Intent(packageContext, ProblemActivity.class);
        intent.putExtra(ID_ARGUMENT, problemId);

        intent.putExtra(NULL_ID_ARGUMENT, nullId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        final Intent intent = getIntent();
        final long nullId = intent.getLongExtra(NULL_ID_ARGUMENT, 0L);

        return ProblemFragment.createInstance(intent.getLongExtra(ID_ARGUMENT, nullId),
                nullId);
    }
}
