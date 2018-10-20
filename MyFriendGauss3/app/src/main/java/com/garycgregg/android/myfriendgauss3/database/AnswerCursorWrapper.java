package com.garycgregg.android.myfriendgauss3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss3.Answer;

import static com.garycgregg.android.myfriendgauss3.database.ProblemDbSchema.*;

public class AnswerCursorWrapper extends CursorWrapper {

    /***
     * Constructs a wrapper for production of Answer objects.
     * @param cursor A cursor
     */
    public AnswerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Creates an Answer object.
     * @return An Answer object
     */
    public Answer getAnswer() {

        // Create the Answer object, and add the problem ID.
        final Answer answer = new Answer();
        answer.setProblemId(getLong(getColumnIndex(AnswerTable.Columns.PROBLEM_ID)));

        // Add the row and entry. Return the Answer object.
        answer.setRow(getInt(getColumnIndex(AnswerTable.Columns.ROW)));
        answer.setEntry(getDouble(getColumnIndex(AnswerTable.Columns.ENTRY)));
        return answer;
    }
}
