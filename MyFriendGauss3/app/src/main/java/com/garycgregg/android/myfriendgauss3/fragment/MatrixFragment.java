package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.content.Matrix;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import java.util.List;

public class MatrixFragment extends NumbersFragment<Matrix> {

    // The tag for our logging
    private static final String TAG = MatrixFragment.class.getSimpleName();

    // Our content producer
    private final ContentProducer<Matrix[]> contentProducer = new ContentProducer<Matrix[]>() {

        @Override
        public Matrix[] onNotFound(ProblemLab problemLab, long problemId) {
            return Matrix.CREATOR.newArray(0);
        }

        @Override
        public Matrix[] produceContent(@NonNull ProblemLab problemLab, long problemId) {

            // Get matrix entries for the given problem ID. Create an array to hold them.
            final List<Matrix> matrixList = problemLab.getMatrices(problemId);
            final Matrix[] matrices = Matrix.CREATOR.newArray(matrixList.size());

            // Copy the list entries to the array, and return the array.
            matrixList.toArray(matrices);
            return matrices;
        }
    };

    // Our index producer
    private final IndexProducer<Matrix> indexProducer = new IndexProducer<Matrix>() {

        @Override
        public int produceId(Matrix contentItem) {
            return calculateId(contentItem.getRow(), contentItem.getColumn());
        }
    };

    // The matrix entries
    private Matrix[] matrixEntries;

    /**
     * Customizes an instance of a MatrixFragment with the required argument(s).
     *
     * @param problemId       The problem ID to be associated with the instance
     * @param label           The label argument
     * @param backgroundColor The background color argument
     * @param enabled         The fragment enabled argument
     * @param rows            The number of rows argument
     * @param columns         The number of columns argument
     * @return A properly configured MatrixFragment
     */
    public static MatrixFragment createInstance(long problemId,
                                                String label, int backgroundColor,
                                                boolean enabled, int rows, int columns) {

        /*
         * Create an instance of a MatrixFragment, and customize it with parameters required
         * of a NumbersFragment. Return the fragment.
         */
        final MatrixFragment fragment = new MatrixFragment();
        NumbersFragment.customizeInstance(fragment, problemId, label, backgroundColor,
                enabled, rows, columns, false);
        return fragment;
    }

    @Override
    protected void addIfMissing(int row, int column) {

        // Get the record tracker. Calculate the control ID from the row and the column.
        final RecordTracker<Matrix> recordTracker = getRecordTracker();
        final int controlId = calculateId(row, column);

        // Is there no existing record with the calculated control ID?
        Matrix matrix = recordTracker.get(controlId);
        if (null == matrix) {

            /*
             * There is no existing record with the calculated control ID. Create one, and set the
             * problem ID.
             */
            matrix = new Matrix();
            matrix.setProblemId(getProblemId());

            // Set the row and column numbers, and add the content to the record tracker.
            matrix.setRow(row);
            matrix.setColumn(column);
            recordTracker.put(controlId, matrix, false);
        }
    }

    @Override
    protected void addWatcher(final EditText editText, int row, int column) {

        // Get the record tracker. Calculate the control ID from the row and the column.
        final RecordTracker<Matrix> recordTracker = getRecordTracker();
        final int controlId = calculateId(row, column);

        /*
         * Find the existing matrix entry for the control ID. It better be there if
         * addIfMissing(row, column) has been called before this method!
         */
        final Matrix matrixEntry = recordTracker.get(controlId);
        if (null != matrixEntry) {

            // Give the control a number text changed listener.
            editText.addTextChangedListener(new NumberTextWatcher<Matrix>(matrixEntry) {

                @Override
                protected void setChange(Double change) {

                    // Was the change not a deletion?
                    final boolean deleted = (null == change);
                    if (!deleted) {

                        /*
                         * The change was not a deletion. Set the entry in the content of the
                         * control.
                         */
                        getContent().setEntry(change);
                    }

                    // Update the delete status in the record tracker.
                    recordTracker.set(controlId, deleted);
                }
            });
        }
    }

    @Override
    protected boolean change(@NonNull Matrix record, @NonNull ProblemLab problemLab) {

        // Add or replace the record, and return true for handling the request.
        problemLab.addOrReplace(record);
        return true;
    }

    @Override
    protected boolean delete(@NonNull Matrix record, @NonNull ProblemLab problemLab) {

        // Add or replace the record, and return true for handling the request.
        problemLab.delete(record);
        return true;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and get the matrixEntries.
        super.onCreate(savedInstanceState);
        matrixEntries = contentProducer.getContent(getProblemId());
    }

    @Override
    public void onDestroy() {

        // Set the matrix entries to null, and call the superclass method.
        matrixEntries = null;
        super.onDestroy();
    }

    @Override
    protected void setContent(EditText editText) {

        // Is there content for this control?
        final Matrix matrix = getRecordTracker().get(editText.getId());
        if (null != matrix) {

            // There is content for this control. Set it.
            editText.setText(Double.toString(matrix.getEntry()));
        }
    }

    @Override
    protected void setRecordTracker() {

        // Create a new record tracker.
        final RecordTracker<Matrix> recordTracker = new RecordTracker<>(getControlCount(),
                this);

        // Copy the matrix entries in the tracker.
        copy(recordTracker, matrixEntries, indexProducer);
        setRecordTracker(recordTracker);
    }
}
