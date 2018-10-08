package com.garycgregg.android.myfriendgauss3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss3.Vector;

public class VectorCursorWrapper extends CursorWrapper {

    public VectorCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Vector getVector() {

        final Vector vector = new Vector();
        vector.setProblemId(getInt(getColumnIndex(ProblemDbSchema.VectorTable.Columns.PROBLEM_ID)));

        vector.setRow(getInt(getColumnIndex(ProblemDbSchema.VectorTable.Columns.ROW)));
        vector.setEntry(getDouble(getColumnIndex(ProblemDbSchema.VectorTable.Columns.ENTRY)));
        return vector;
    }
}
