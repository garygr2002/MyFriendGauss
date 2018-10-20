package com.garycgregg.android.myfriendgauss3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss3.Vector;

public class VectorCursorWrapper extends CursorWrapper {

    /**
     * Creates a Vector object.
     */
    public VectorCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Creates a Vector object.
     *
     * @return A Vector object
     */
    public Vector getVector() {

        // Create the Vector object, and add the problem ID.
        final Vector vector = new Vector();
        vector.setProblemId(getLong(getColumnIndex(
                ProblemDbSchema.VectorTable.Columns.PROBLEM_ID)));

        // Add the row and entry. Return the Vector object.
        vector.setRow(getInt(getColumnIndex(ProblemDbSchema.VectorTable.Columns.ROW)));
        vector.setEntry(getDouble(getColumnIndex(ProblemDbSchema.VectorTable.Columns.ENTRY)));
        return vector;
    }
}
