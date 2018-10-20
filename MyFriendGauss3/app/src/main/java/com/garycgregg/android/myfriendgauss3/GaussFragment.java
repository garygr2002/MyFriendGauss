package com.garycgregg.android.myfriendgauss3;

import android.content.Context;
import android.support.v4.app.Fragment;

public class GaussFragment extends Fragment implements ProblemLabSource {

    private ProblemLabSource problemLabSource;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        problemLabSource = (context instanceof ProblemLabSource) ? ((ProblemLabSource) context) :
                null;
    }

    @Override
    public void onDetach() {

        problemLabSource = null;
        super.onDetach();
    }

    @Override
    public ProblemLab getProblemLab() {
        return (null == problemLabSource) ? null : problemLabSource.getProblemLab();
    }
}
