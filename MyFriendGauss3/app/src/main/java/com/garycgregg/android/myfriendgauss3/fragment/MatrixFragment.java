package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.content.Matrix;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MatrixFragment extends NumbersFragment<Matrix> {

    // The tag for our logging
    private static final String TAG = AnswerFragment.class.getSimpleName();

    // Our content producer
    private final ContentProducer<Matrix[]> contentProducer = new ContentProducer<Matrix[]>() {

        @Override
        public Matrix[] onNotFound(ProblemLab problemLab, long problemId) {
            return Matrix.CREATOR.newArray(0);
        }

        @Override
        public Matrix[] produceContent(ProblemLab problemLab, long problemId) {

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
     * @param problemId        The problem ID to be associated with the instance
     * @param label            The label argument
     * @param backgroundColor  The background color argument
     * @param enabled          The fragment enabled argument
     * @param rows             The number of rows argument
     * @param columns          The number of columns argument
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
    protected void addWatcher(final EditText editText, int row, int column) {

        // Get the content index. Calculate the control ID from the row and the column.
        final SparseArray<Matrix> contentIndex = getContentIndex();
        final int controlId = calculateId(row, column);

        // Is there no existing content with the calculated control ID?
        Matrix matrix = contentIndex.get(controlId);
        if (null == matrix) {

            // There is no existing content. Create it, then set the problem ID.
            matrix = new Matrix();
            matrix.setProblemId(getProblemId());

            // Set the row and column numbers, and add the content to the content index.
            matrix.setRow(row);
            matrix.setColumn(column);
            contentIndex.put(controlId, matrix);
        }

        // Give the control a number text changed listener.
        editText.addTextChangedListener(new NumberTextWatcher<Matrix>(matrix) {

            @Override
            protected void setChange(String change) {

                /*
                 * Get the content object, set its entry, and add the content object to the change
                 * list.
                 */
                final Matrix matrix = getContent();
                matrix.setEntry(Double.parseDouble(change));
                addChange(matrix);
            }
        });
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and get the matrixEntries. Create and set a content index.
        super.onCreate(savedInstanceState);
        matrixEntries = contentProducer.getContent(getProblemId());
        setContentIndex(indexProducer.populateArray(new SparseArray<Matrix>(), matrixEntries));

        // Set the change list and the change set.
        setChangeList(new ArrayList<Matrix>());
        setChangeSet(new HashSet<Matrix>());
    }

    @Override
    public void onDestroy() {

        // Release the changes, and set the content index to null.
        releaseChanges();
        setContentIndex(null);

        // Set the matrixEntries to null, and call the superclass method.
        matrixEntries = null;
        super.onDestroy();
    }

    @Override
    protected void setContent(EditText editText, int controlId) {

        // Get the content index. Is there content for this control?
        final SparseArray<Matrix> contentIndex = getContentIndex();
        final Matrix matrix = contentIndex.get(controlId);
        if (null != matrix) {

            // There is content for this control. Set it.
            editText.setText(Double.toString(matrix.getEntry()));
        }
    }
}
