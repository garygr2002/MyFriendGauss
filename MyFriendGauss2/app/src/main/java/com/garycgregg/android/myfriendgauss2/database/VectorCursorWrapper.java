package com.garycgregg.android.myfriendgauss2.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss2.Vector;

import static com.garycgregg.android.myfriendgauss2.database.ProblemDbSchema.*;

public class VectorCursorWrapper extends CursorWrapper {

    public VectorCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Vector getVector() {

        final Vector vector = new Vector();
        vector.setProblemId(getInt(getColumnIndex(VectorTable.Columns.PROBLEM_ID)));

        vector.setRow(getInt(getColumnIndex(VectorTable.Columns.ROW)));
        vector.setEntry(getDouble(getColumnIndex(VectorTable.Columns.ENTRY)));
        return vector;
    }
}
