package com.garycgregg.android.myfriendgauss3;

import android.content.Context;
import android.support.v4.app.Fragment;

public class GaussFragment extends Fragment implements ProblemLabSource {

    // The format of an instance argument
    protected static final String ARGUMENT_FORMAT_STRING = "%s.%s_argument";

    // The source of the problem lab
    private ProblemLabSource problemLabSource;

    @Override
    public ProblemLab getProblemLab() {
        return (null == problemLabSource) ? null : problemLabSource.getProblemLab();
    }

    @Override
    public void onAttach(Context context) {

        /*
         * Call through to the superclass method, and if possible cast the given context to a
         * problem lab source. Save the source.
         */
        super.onAttach(context);
        problemLabSource = (context instanceof ProblemLabSource) ? ((ProblemLabSource) context) :
                null;
    }

    @Override
    public void onDetach() {

        // Clear the problem lab source, and call through to the superclass method.
        problemLabSource = null;
        super.onDetach();
    }
}
