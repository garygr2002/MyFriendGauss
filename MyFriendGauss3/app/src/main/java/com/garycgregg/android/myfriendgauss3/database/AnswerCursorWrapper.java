package com.garycgregg.android.myfriendgauss3.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.garycgregg.android.myfriendgauss3.Answer;

import static com.garycgregg.android.myfriendgauss3.database.ProblemDbSchema.*;

public class AnswerCursorWrapper extends CursorWrapper {

    public AnswerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Answer getAnswer() {

        final Answer answer = new Answer();
        answer.setProblemId(getInt(getColumnIndex(AnswerTable.Columns.PROBLEM_ID)));

        answer.setRow(getInt(getColumnIndex(AnswerTable.Columns.ROW)));
        answer.setEntry(getDouble(getColumnIndex(AnswerTable.Columns.ENTRY)));
        return answer;
    }
}
