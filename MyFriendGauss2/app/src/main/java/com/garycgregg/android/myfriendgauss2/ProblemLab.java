package com.garycgregg.android.myfriendgauss2;

import android.content.ContentValues;
import android.content.Context;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.garycgregg.android.myfriendgauss2.database.AnswerCursorWrapper;
import com.garycgregg.android.myfriendgauss2.database.MatrixCursorWrapper;
import com.garycgregg.android.myfriendgauss2.database.ProblemCursorWrapper;
import com.garycgregg.android.myfriendgauss2.database.ProblemDatabaseHelper;
import com.garycgregg.android.myfriendgauss2.database.ProblemDbSchema;
import com.garycgregg.android.myfriendgauss2.database.VectorCursorWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProblemLab {

    private static final int conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE;
    private static final String TAG = ProblemLab.class.getSimpleName();
    private static final String whereFormat = "%s = ?";
    private static final String problemWhereClause = String.format(whereFormat,
            ProblemDbSchema.ProblemTable.Columns.PROBLEM_ID);
    private final Context context;
    private final SQLiteDatabase database;
    private final WrapperManager<Answer> answerWrapperManager = new WrapperManager<Answer>() {

        @Override
        public CursorWrapper getWrapper(Integer problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.AnswerTable.Columns.PROBLEM_ID, problemId);
            return queryAnswers(arguments.first, arguments.second);
        }

        @Override
        public Answer get(CursorWrapper wrapper) {
            return ((AnswerCursorWrapper) wrapper).getAnswer();
        }
    };
    private final WrapperManager<Matrix> matrixWrapperManager = new WrapperManager<Matrix>() {

        @Override
        public CursorWrapper getWrapper(Integer problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.MatrixTable.Columns.PROBLEM_ID, problemId);
            return queryMatrices(arguments.first, arguments.second);
        }

        @Override
        public Matrix get(CursorWrapper wrapper) {
            return ((MatrixCursorWrapper) wrapper).getMatrix();
        }
    };
    private final WrapperManager<Problem> problemWrapperManager = new WrapperManager<Problem>() {

        @Override
        public CursorWrapper getWrapper(Integer problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.ProblemTable.Columns.PROBLEM_ID, problemId);
            return queryProblems(arguments.first, arguments.second);
        }

        @Override
        public Problem get(CursorWrapper wrapper) {
            return ((ProblemCursorWrapper) wrapper).getProblem();
        }
    };
    private final WrapperManager<Vector> vectorWrapperManager = new WrapperManager<Vector>() {

        @Override
        public CursorWrapper getWrapper(Integer problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.VectorTable.Columns.PROBLEM_ID, problemId);
            return queryVectors(arguments.first, arguments.second);
        }

        @Override
        public Vector get(CursorWrapper wrapper) {
            return ((VectorCursorWrapper) wrapper).getVector();
        }
    };

    public ProblemLab(Context context) {
        database = new ProblemDatabaseHelper(this.context = context.getApplicationContext()).
                getWritableDatabase();
    }

    private static <T extends BaseGaussEntry> void build(Double[] vector, T entry) {

        final int row = entry.getRow();
        if ((0 < row) && (row < vector.length)) {

            vector[row] = entry.getEntry();
        }
    }

    private static <T extends BaseGaussEntry> void build(Double[] vector, List<T> entries) {

        for (BaseGaussEntry entry : entries) {
            build(vector, entry);
        }
    }

    public static Double[] buildAnswers(int dimensions, List<Answer> entries) {

        final Double[] answers = new Double[dimensions];
        build(answers, entries);
        return answers;
    }

    public static Double[][] buildMatrices(int dimensions, List<Matrix> entries) {

        final Double[][] matrix = new Double[dimensions][dimensions];
        for (Matrix entry : entries) {

            build(matrix[entry.getColumn()], entry);
        }

        return matrix;
    }

    public static Double[] buildVectors(int dimensions, List<Vector> entries) {

        final Double[] vectors = new Double[dimensions];
        build(vectors, entries);
        return vectors;
    }

    public void add(Answer answer) {
        database.insertWithOnConflict(ProblemDbSchema.AnswerTable.name, null,
                getContentValues(answer), conflictAlgorithm);
    }

    public void add(Matrix matrix) {
        database.insertWithOnConflict(ProblemDbSchema.MatrixTable.name, null,
                getContentValues(matrix), conflictAlgorithm);
    }

    public long add(Problem problem) {
        return database.insert(ProblemDbSchema.ProblemTable.name, null,
                getContentValues(problem));
    }

    public int delete(int problemId) {
        return database.delete(ProblemDbSchema.ProblemTable.name, problemWhereClause,
                new String[]{Integer.toString(problemId)});
    }

    public int delete(Answer answer) {
        return database.delete(ProblemDbSchema.AnswerTable.name, String.format("%s = ? and %s = ?",
                ProblemDbSchema.AnswerTable.Columns.PROBLEM_ID,
                ProblemDbSchema.AnswerTable.Columns.ROW),
                new String[]{Integer.toString(answer.getProblemId()),
                        Integer.toString(answer.getRow())});
    }

    public int delete(Matrix matrix) {
        return database.delete(ProblemDbSchema.MatrixTable.name,
                String.format("%s = ? and %s = ? and %s = ?",
                        ProblemDbSchema.MatrixTable.Columns.PROBLEM_ID,
                        ProblemDbSchema.MatrixTable.Columns.ROW,
                        ProblemDbSchema.MatrixTable.Columns.COLUMN),
                new String[]{Integer.toString(matrix.getProblemId()),
                        Integer.toString(matrix.getRow()),
                        Integer.toString(matrix.getColumn())});
    }

    public int delete(Vector vector) {
        return database.delete(ProblemDbSchema.VectorTable.name, String.format("%s = ? and %s = ?",
                ProblemDbSchema.VectorTable.Columns.PROBLEM_ID,
                ProblemDbSchema.VectorTable.Columns.ROW),
                new String[]{Integer.toString(vector.getProblemId()),
                        Integer.toString(vector.getRow())});
    }

    public Answer getAnswer(int problemId) {
        return get(answerWrapperManager, problemId);
    }

    public List<Answer> getAnswers() {

        final List<Answer> answers = new ArrayList<>();
        getAll(answers, answerWrapperManager);
        return answers;
    }

    public Matrix getMatrix(int problemId) {
        return get(matrixWrapperManager, problemId);
    }

    public List<Matrix> getMatrices() {

        final List<Matrix> matrices = new ArrayList<>();
        getAll(matrices, matrixWrapperManager);
        return matrices;
    }

    public Problem getProblem(int problemId) {
        return get(problemWrapperManager, problemId);
    }

    public List<Problem> getProblems() {

        final List<Problem> problems = new ArrayList<Problem>();
        getAll(problems, problemWrapperManager);
        return problems;
    }

    public Vector getVector(int problemId) {
        return get(vectorWrapperManager, problemId);
    }

    public List<Vector> getVectors() {

        final List<Vector> vectors = new ArrayList<>();
        getAll(vectors, vectorWrapperManager);
        return vectors;
    }

    public int update(Problem problem) {

        int returnValue = 0;
        final Integer problemId = problem.getProblemId();
        if (null != problemId) {

            returnValue = database.update(ProblemDbSchema.ProblemTable.name,
                    getContentValues(problem),
                    problemWhereClause, new String[]{Integer.toString(problemId)});
        }

        return returnValue;
    }

    private ContentValues getContentValues(Answer answer) {

        final ContentValues values = new ContentValues();
        values.put(ProblemDbSchema.AnswerTable.Columns.PROBLEM_ID, answer.getProblemId());

        values.put(ProblemDbSchema.AnswerTable.Columns.ROW, answer.getRow());
        values.put(ProblemDbSchema.AnswerTable.Columns.ENTRY, answer.getEntry());
        return values;
    }

    private ContentValues getContentValues(Matrix matrix) {

        final ContentValues values = new ContentValues();
        values.put(ProblemDbSchema.MatrixTable.Columns.PROBLEM_ID, matrix.getProblemId());

        values.put(ProblemDbSchema.MatrixTable.Columns.ROW, matrix.getRow());
        values.put(ProblemDbSchema.MatrixTable.Columns.COLUMN, matrix.getColumn());

        values.put(ProblemDbSchema.MatrixTable.Columns.ENTRY, matrix.getEntry());
        return values;
    }

    private ContentValues getContentValues(Problem problem) {

        final ContentValues values = new ContentValues();
        values.put(ProblemDbSchema.ProblemTable.Columns.NAME, problem.getName());

        values.put(ProblemDbSchema.ProblemTable.Columns.DIMENSIONS, problem.getDimensions());
        values.put(ProblemDbSchema.ProblemTable.Columns.CREATED, problem.getCreated().getTime());

        final Date solved = problem.getSolved();
        if (null != solved) {

            values.put(ProblemDbSchema.ProblemTable.Columns.CREATED, problem.getSolved().getTime());
        }

        values.put(ProblemDbSchema.ProblemTable.Columns.WRITE_LOCK,
                problem.isWriteLocked() ? Problem.getTrue() : Problem.getFalse());
        return values;
    }

    private ContentValues getContentValues(Vector vector) {

        final ContentValues values = new ContentValues();
        values.put(ProblemDbSchema.VectorTable.Columns.PROBLEM_ID, vector.getProblemId());

        values.put(ProblemDbSchema.VectorTable.Columns.ROW, vector.getRow());
        values.put(ProblemDbSchema.VectorTable.Columns.ENTRY, vector.getEntry());
        return values;
    }

    private AnswerCursorWrapper queryAnswers(String whereClause, String[] whereArgs) {
        return new AnswerCursorWrapper(database.query(ProblemDbSchema.AnswerTable.name,
                null, whereClause, whereArgs, null, null, null));
    }

    private MatrixCursorWrapper queryMatrices(String whereClause, String[] whereArgs) {
        return new MatrixCursorWrapper(database.query(ProblemDbSchema.MatrixTable.name,
                null, whereClause, whereArgs, null, null, null));
    }

    private ProblemCursorWrapper queryProblems(String whereClause, String[] whereArgs) {
        return new ProblemCursorWrapper(database.query(ProblemDbSchema.ProblemTable.name,
                null, whereClause, whereArgs, null, null, null));
    }

    private VectorCursorWrapper queryVectors(String whereClause, String[] whereArgs) {
        return new VectorCursorWrapper(database.query(ProblemDbSchema.VectorTable.name,
                null, whereClause, whereArgs, null, null, null));
    }

    private <T> T get(WrapperManager<T> wrapperManager, int problemId) {

        final CursorWrapper wrapper = wrapperManager.getWrapper(problemId);
        T returnValue = null;
        try {

            if (0 < wrapper.getCount()) {

                wrapper.moveToFirst();
                returnValue = wrapperManager.get(wrapper);
            }
        } finally {
            wrapper.close();
        }

        return returnValue;
    }

    private <T> void getAll(List<T> list, WrapperManager<T> wrapperManager) {

        list.clear();
        final CursorWrapper wrapper = wrapperManager.getWrapper(null);
        try {

            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()) {

                list.add(wrapperManager.get(wrapper));
                wrapper.moveToNext();
            }
        } finally {
            wrapper.close();
        }
    }

    private abstract class WrapperManager<T> {

        protected Pair<String, String[]> createArguments(String fieldName, Integer problemId) {

            String[] whereArgs = null;
            String whereClause = null;
            if (null != problemId) {

                whereArgs = new String[]{Integer.toString(problemId)};
                whereClause = String.format(whereFormat, fieldName);
            }

            return new Pair<String, String[]>(whereClause, whereArgs);
        }

        protected abstract CursorWrapper getWrapper(Integer problemId);

        protected abstract T get(CursorWrapper wrapper);
    }
}
