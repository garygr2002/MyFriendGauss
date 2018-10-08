package com.garycgregg.android.myfriendgauss2.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss2.Matrix;

import static com.garycgregg.android.myfriendgauss2.database.ProblemDbSchema.*;

public class MatrixCursorWrapper extends CursorWrapper {

    public MatrixCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Matrix getMatrix() {

        final Matrix matrix = new Matrix();
        matrix.setProblemId(getInt(getColumnIndex(MatrixTable.Columns.PROBLEM_ID)));
        matrix.setRow(getInt(getColumnIndex(MatrixTable.Columns.ROW)));

        matrix.setColumn(getInt(getColumnIndex(MatrixTable.Columns.COLUMN)));
        matrix.setEntry(getDouble(getColumnIndex(MatrixTable.Columns.ENTRY)));
        return matrix;
    }
}
