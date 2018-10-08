package com.garycgregg.android.myfriendgauss2.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss2.Problem;

import java.util.Date;

import static com.garycgregg.android.myfriendgauss2.database.ProblemDbSchema.*;

public class ProblemCursorWrapper extends CursorWrapper {

    public ProblemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Problem getProblem() {

        final Problem problem = new Problem();
        int columnIndex = getColumnIndex(ProblemTable.Columns.PROBLEM_ID);
        problem.setProblemId(isNull(columnIndex) ? null : getInt(columnIndex));

        problem.setName(getString(getColumnIndex(ProblemTable.Columns.NAME)));
        problem.setDimensions(getInt(getColumnIndex(ProblemTable.Columns.DIMENSIONS)));

        problem.setCreated(new Date(getLong(getColumnIndex(ProblemTable.Columns.CREATED))));
        columnIndex = getColumnIndex(ProblemTable.Columns.SOLVED);
        problem.setSolved(isNull(columnIndex) ? null : new Date(getLong(columnIndex)));

        problem.setWriteLocked(Problem.getFalse() ==
                getInt(getColumnIndex(ProblemTable.Columns.WRITE_LOCK)));
        return problem;
    }
}
