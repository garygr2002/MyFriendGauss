package com.garycgregg.android.myfriendgauss3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.garycgregg.android.myfriendgauss3.content.Answer;
import com.garycgregg.android.myfriendgauss3.content.BaseGaussEntry;
import com.garycgregg.android.myfriendgauss3.content.Matrix;
import com.garycgregg.android.myfriendgauss3.content.Problem;
import com.garycgregg.android.myfriendgauss3.content.Vector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProblemLab {

    public static final int MAX_DIMENSIONS = 15;
    public static final long NULL_ID = 0L;

    private static final int conflictAlgorithm = SQLiteDatabase.CONFLICT_REPLACE;
    private static final String whereFormat = "%s = ?";
    private static final String problemWhereClause = String.format(whereFormat,
            ProblemDbSchema.ProblemTable.Columns.PROBLEM_ID);
    private final SQLiteDatabase database;
    private final WrapperManager<Answer> answerWrapperManager = new WrapperManager<Answer>() {

        @Override
        public Answer get(CursorWrapper wrapper) {
            return ((AnswerCursorWrapper) wrapper).getAnswer();
        }

        @Override
        public CursorWrapper getWrapper(Long problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.AnswerTable.Columns.PROBLEM_ID, problemId);
            return queryAnswers(arguments.first, arguments.second);
        }
    };
    private final WrapperManager<Matrix> matrixWrapperManager = new WrapperManager<Matrix>() {

        @Override
        public Matrix get(CursorWrapper wrapper) {
            return ((MatrixCursorWrapper) wrapper).getMatrix();
        }

        @Override
        public CursorWrapper getWrapper(Long problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.MatrixTable.Columns.PROBLEM_ID, problemId);
            return queryMatrices(arguments.first, arguments.second);
        }
    };
    private final WrapperManager<Problem> problemWrapperManager = new WrapperManager<Problem>() {

        @Override
        public Problem get(CursorWrapper wrapper) {
            return ((ProblemCursorWrapper) wrapper).getProblem();
        }

        @Override
        public CursorWrapper getWrapper(Long problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.ProblemTable.Columns.PROBLEM_ID, problemId);
            return queryProblems(arguments.first, arguments.second);
        }
    };
    private final WrapperManager<Vector> vectorWrapperManager = new WrapperManager<Vector>() {

        @Override
        public Vector get(CursorWrapper wrapper) {
            return ((VectorCursorWrapper) wrapper).getVector();
        }

        @Override
        public CursorWrapper getWrapper(Long problemId) {

            final Pair<String, String[]> arguments =
                    createArguments(ProblemDbSchema.VectorTable.Columns.PROBLEM_ID, problemId);
            return queryVectors(arguments.first, arguments.second);
        }
    };

    public ProblemLab(Context context) {

        database = new ProblemDatabaseHelper(context.getApplicationContext()).
                getWritableDatabase();
        insureProblemsExist();
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
                getContentValues(problem, true));
    }

    public long add(Vector vector) {
        return database.insertWithOnConflict(ProblemDbSchema.VectorTable.name, null,
                getContentValues(vector), conflictAlgorithm);
    }

    /**
     * Adds some sample problems to the database. TODO: Delete this.
     */
    private void addProblems() {

        final Problem problem = new Problem();
        for (int i = 1; i <= 5; ++i) {

            problem.setName(String.format("Problem Number %d", i));
            problem.setCreated(new Date());

            problem.setDimensions((i % 10) + 1);
            problem.setWriteLocked(false);
            add(problem);
        }
    }

    public int delete(Answer answer) {
        return database.delete(ProblemDbSchema.AnswerTable.name, String.format("%s = ? and %s = ?",
                ProblemDbSchema.AnswerTable.Columns.PROBLEM_ID,
                ProblemDbSchema.AnswerTable.Columns.ROW),
                new String[]{Long.toString(answer.getProblemId()),
                        Integer.toString(answer.getRow())});
    }

    public int delete(Matrix matrix) {
        return database.delete(ProblemDbSchema.MatrixTable.name,
                String.format("%s = ? and %s = ? and %s = ?",
                        ProblemDbSchema.MatrixTable.Columns.PROBLEM_ID,
                        ProblemDbSchema.MatrixTable.Columns.ROW,
                        ProblemDbSchema.MatrixTable.Columns.COLUMN),
                new String[]{Long.toString(matrix.getProblemId()),
                        Integer.toString(matrix.getRow()),
                        Integer.toString(matrix.getColumn())});
    }

    public int delete(Vector vector) {
        return database.delete(ProblemDbSchema.VectorTable.name, String.format("%s = ? and %s = ?",
                ProblemDbSchema.VectorTable.Columns.PROBLEM_ID,
                ProblemDbSchema.VectorTable.Columns.ROW),
                new String[]{Long.toString(vector.getProblemId()),
                        Integer.toString(vector.getRow())});
    }

    public int deleteProblem(Long problemId) {

        String[] whereArgs;
        String whereClause;
        if (null == problemId) {

            whereArgs = new String[]{};
            whereClause = "1";
        } else {

            whereArgs = new String[]{Long.toString(problemId)};
            whereClause = problemWhereClause;
        }

        return database.delete(ProblemDbSchema.ProblemTable.name, whereClause, whereArgs);
    }

    public int deleteProblems() {
        return deleteProblem(null);
    }

    private <T> T get(WrapperManager<T> wrapperManager, long problemId) {

        final CursorWrapper wrapper = wrapperManager.getWrapper(problemId);
        T returnValue = null;
        try {

            final int count = wrapper.getCount();
            if (0 < count) {

                wrapper.moveToFirst();
                returnValue = wrapperManager.get(wrapper);
            }
        } finally {
            wrapper.close();
        }

        return returnValue;
    }

    private <T> void getAll(List<T> list, WrapperManager<T> wrapperManager, Long problemId) {

        list.clear();
        final CursorWrapper wrapper = wrapperManager.getWrapper(problemId);
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

    public List<Answer> getAnswers(long problemId) {

        final List<Answer> answers = new ArrayList<>();
        getAll(answers, answerWrapperManager, problemId);
        return answers;
    }

    private ContentValues getContentCreated(Problem problem, ContentValues existingValues) {

        final ContentValues values = (null == existingValues) ? new ContentValues() :
                existingValues;

        final Date created = problem.getCreated();
        if (null != created) {

            values.put(ProblemDbSchema.ProblemTable.Columns.CREATED, created.getTime());
        }

        return values;
    }

    private ContentValues getContentDimensions(Problem problem, ContentValues existingValues) {

        final ContentValues values = (null == existingValues) ? new ContentValues() :
                existingValues;

        values.put(ProblemDbSchema.ProblemTable.Columns.DIMENSIONS, problem.getDimensions());
        return values;
    }

    private ContentValues getContentDimensions(Problem problem) {
        return getContentDimensions(problem, null);
    }

    private ContentValues getContentName(Problem problem, ContentValues existingValues) {

        final ContentValues values = (null == existingValues) ? new ContentValues() :
                existingValues;

        values.put(ProblemDbSchema.ProblemTable.Columns.NAME, problem.getName());
        return values;
    }

    private ContentValues getContentName(Problem problem) {
        return getContentName(problem,
                null);
    }

    private ContentValues getContentSolved(Problem problem, ContentValues existingValues) {

        final ContentValues values = (null == existingValues) ? new ContentValues() :
                existingValues;

        final Date solved = problem.getSolved();
        if (null != solved) {

            values.put(ProblemDbSchema.ProblemTable.Columns.SOLVED, solved.getTime());
        }

        return values;
    }

    private ContentValues getContentSolved(Problem problem) {
        return getContentSolved(problem,
                null);
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

    private ContentValues getContentValues(Problem problem, boolean setCreated) {

        final ContentValues values = getContentName(problem);
        getContentDimensions(problem, values);
        if (setCreated) {

            getContentCreated(problem, values);
        }

        getContentSolved(problem, values);
        getContentWriteLock(problem, values);
        return values;
    }

    private ContentValues getContentValues(Problem problem) {
        return getContentValues(problem, false);
    }

    private ContentValues getContentValues(Vector vector) {

        final ContentValues values = new ContentValues();
        values.put(ProblemDbSchema.VectorTable.Columns.PROBLEM_ID, vector.getProblemId());

        values.put(ProblemDbSchema.VectorTable.Columns.ROW, vector.getRow());
        values.put(ProblemDbSchema.VectorTable.Columns.ENTRY, vector.getEntry());
        return values;
    }

    private ContentValues getContentWriteLock(Problem problem, ContentValues existingValues) {

        final ContentValues values = (null == existingValues) ? new ContentValues() :
                existingValues;

        values.put(ProblemDbSchema.ProblemTable.Columns.WRITE_LOCK,
                problem.isWriteLocked() ? Problem.TRUE : Problem.FALSE);
        return values;
    }

    private ContentValues getContentWriteLock(Problem problem) {
        return getContentWriteLock(problem, null);
    }

    public List<Matrix> getMatrices(long problemId) {

        final List<Matrix> matrices = new ArrayList<>();
        getAll(matrices, matrixWrapperManager, problemId);
        return matrices;
    }

    public Problem getProblem(long problemId) {

        database.beginTransaction();
        Problem problem = get(problemWrapperManager, problemId);
        if (!((null == problem) || problem.isWriteLocked())) {

            problem.setWriteLocked(true);
            update(problem);
            problem.setWriteLocked(false);
        }

        database.endTransaction();
        return problem;
    }

    public List<Problem> getProblems() {

        final List<Problem> problems = new ArrayList<>();
        getAll(problems, problemWrapperManager, null);
        return problems;
    }

    public List<Vector> getVectors(long problemId) {

        final List<Vector> vectors = new ArrayList<>();
        getAll(vectors, vectorWrapperManager, problemId);
        return vectors;
    }

    /**
     * Insures some sample problems exist. TODO: Delete this.
     */
    private void insureProblemsExist() {

        if (DatabaseUtils.queryNumEntries(database,
                ProblemDbSchema.ProblemTable.name) <= 0) {

            addProblems();
        }
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

    public int update(Problem problem) {

        return database.update(ProblemDbSchema.ProblemTable.name,
                getContentValues(problem),
                problemWhereClause, new String[]{Long.toString(problem.getProblemId())});
    }

    public int updateDimensions(Problem problem) {

        return database.update(ProblemDbSchema.ProblemTable.name,
                getContentDimensions(problem),
                problemWhereClause, new String[]{Long.toString(problem.getProblemId())});
    }

    public int updateName(Problem problem) {

        return database.update(ProblemDbSchema.ProblemTable.name,
                getContentName(problem),
                problemWhereClause, new String[]{Long.toString(problem.getProblemId())});
    }

    public int updateSolved(Problem problem) {

        return database.update(ProblemDbSchema.ProblemTable.name,
                getContentSolved(problem),
                problemWhereClause, new String[]{Long.toString(problem.getProblemId())});
    }

    public int updateWriteLock(Problem problem) {

        return database.update(ProblemDbSchema.ProblemTable.name,
                getContentWriteLock(problem),
                problemWhereClause, new String[]{Long.toString(problem.getProblemId())});
    }

    private abstract class WrapperManager<T> {

        protected Pair<String, String[]> createArguments(String fieldName, Long problemId) {

            String[] whereArgs = null;
            String whereClause = null;
            if (null != problemId) {

                whereArgs = new String[]{Long.toString(problemId)};
                whereClause = String.format(whereFormat, fieldName);
            }

            return new Pair<>(whereClause, whereArgs);
        }

        protected abstract T get(CursorWrapper wrapper);

        protected abstract CursorWrapper getWrapper(Long problemId);
    }
}
