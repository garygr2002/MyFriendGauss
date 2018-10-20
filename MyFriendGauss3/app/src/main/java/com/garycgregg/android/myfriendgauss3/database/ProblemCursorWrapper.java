package com.garycgregg.android.myfriendgauss3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss3.Problem;

import java.util.Date;

import static com.garycgregg.android.myfriendgauss3.database.ProblemDbSchema.*;

public class ProblemCursorWrapper extends CursorWrapper {

    /**
     * Creates a Problem object.
     * @return A problem object
     */
    public ProblemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Creates a Problem object.
     * @return A Problem object
     */
    public Problem getProblem() {

        // Create the Problem object, and add the problem ID.
        final Problem problem = new Problem();
        problem.setProblemId(getLong(getColumnIndex(ProblemTable.Columns.PROBLEM_ID)));

        // Add the name and dimensions.
        problem.setName(getString(getColumnIndex(ProblemTable.Columns.NAME)));
        problem.setDimensions(getInt(getColumnIndex(ProblemTable.Columns.DIMENSIONS)));

        /*
         * Add the date and the solved field. The boolean 'solved' field is set if the 'solved'
         * field in the record is not null. It is cleared otherwise.
         */
        problem.setCreated(new Date(getLong(getColumnIndex(ProblemTable.Columns.CREATED))));
        final int columnIndex = getColumnIndex(ProblemTable.Columns.SOLVED);
        problem.setSolved(isNull(columnIndex) ? null : new Date(getLong(columnIndex)));

        /*
         * Set the 'write locked' field if the 'write locked' field in the record is anything other
         * than numeric FALSE. Return the Problem object.
         */
        problem.setWriteLocked(Problem.FALSE ==
                getInt(getColumnIndex(ProblemTable.Columns.WRITE_LOCK)));
        return problem;
    }
}
