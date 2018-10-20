package com.garycgregg.android.myfriendgauss3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss3.Matrix;

import static com.garycgregg.android.myfriendgauss3.database.ProblemDbSchema.*;

public class MatrixCursorWrapper extends CursorWrapper {

    /***
     * Constructs a wrapper for production of Matrix objects.
     * @param cursor A cursor
     */
    public MatrixCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Creates a Matrix object.
     * @return A Matrix object
     */
    public Matrix getMatrix() {

        // Create the Matrix object. Add the problem ID and the row.
        final Matrix matrix = new Matrix();
        matrix.setProblemId(getLong(getColumnIndex(MatrixTable.Columns.PROBLEM_ID)));
        matrix.setRow(getInt(getColumnIndex(MatrixTable.Columns.ROW)));

        // Add the column and entry. Return the Matrix object.
        matrix.setColumn(getInt(getColumnIndex(MatrixTable.Columns.COLUMN)));
        matrix.setEntry(getDouble(getColumnIndex(MatrixTable.Columns.ENTRY)));
        return matrix;
    }
}
