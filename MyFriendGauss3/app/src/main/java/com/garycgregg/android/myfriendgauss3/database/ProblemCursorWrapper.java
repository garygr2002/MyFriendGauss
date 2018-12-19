package com.garycgregg.android.myfriendgauss3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss3.content.Problem;

import java.util.Date;

import static com.garycgregg.android.myfriendgauss3.database.ProblemDbSchema.*;

public class ProblemCursorWrapper extends CursorWrapper {

    /***
     * Constructs a wrapper for production of Problem objects.
     * @param cursor A cursor
     */
    public ProblemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Creates a Problem object.
     *
     * @return A Problem object
     */
    public Problem getProblem() {

        // Create the Problem object, and set the problem ID.
        final Problem problem = new Problem();
        problem.setProblemId(getLong(getColumnIndex(ProblemTable.Columns.PROBLEM_ID)));

        // Set the name, dimensions, and date created fields.
        problem.setName(getString(getColumnIndex(ProblemTable.Columns.NAME)));
        problem.setDimensions(getInt(getColumnIndex(ProblemTable.Columns.DIMENSIONS)));
        problem.setCreated(new Date(getLong(getColumnIndex(ProblemTable.Columns.CREATED))));

        // Set the precision field, and the 'scientific notation' field.
        problem.setPrecision(getInt(getColumnIndex(ProblemTable.Columns.PRECISION)));
        problem.setScientific(Problem.translateBoolean(
                getInt(getColumnIndex(ProblemTable.Columns.SCIENTIFIC))));

        /*
         * Set the rank field. The boolean 'solved' field is set if the 'solved' field in the
         * record is not null. It is cleared otherwise.
         */
        problem.setRank(getInt(getColumnIndex(ProblemTable.Columns.RANK)));
        final int columnIndex = getColumnIndex(ProblemTable.Columns.SOLVED);
        problem.setSolved(isNull(columnIndex) ? null : new Date(getLong(columnIndex)));

        // Set the 'write locked' field, and return the Problem object.
        problem.setWriteLocked(Problem.translateBoolean(
                getInt(getColumnIndex(ProblemTable.Columns.WRITE_LOCK))));
        return problem;
    }
}
