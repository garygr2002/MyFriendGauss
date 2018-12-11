package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.content.Vector;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import org.w3c.dom.Text;

import java.util.List;

public class VectorFragment extends NumbersFragment<Vector> {

    // The tag for our logging
    private static final String TAG = VectorFragment.class.getSimpleName();

    // Our content producer
    private final ContentProducer<Vector[]> contentProducer = new ContentProducer<Vector[]>() {

        @Override
        public Vector[] onNotFound(ProblemLab problemLab, long problemId) {
            return Vector.CREATOR.newArray(0);
        }

        @Override
        public Vector[] produceContent(@NonNull ProblemLab problemLab, long problemId) {

            // Get vectors for the given problem ID. Create an array to hold them.
            final List<Vector> vectorList = problemLab.getVectors(problemId);
            final Vector[] vectors = Vector.CREATOR.newArray(vectorList.size());

            // Copy the list entries to the array, and return the array.
            vectorList.toArray(vectors);
            return vectors;
        }
    };

    // Our index producer
    private final IndexProducer<Vector> indexProducer = new IndexProducer<Vector>() {

        @Override
        public int produceId(Vector contentItem) {
            return calculateId(contentItem.getRow(), 0);
        }
    };

    // The vectors
    private Vector[] vectors;

    /**
     * Customizes an instance of a VectorFragment with the required argument(s).
     *
     * @param problemId       The problem ID to be associated with the instance
     * @param label           The label argument
     * @param backgroundColor The background color argument
     * @param enabled         The fragment enabled argument
     * @param rows            The number of rows argument
     * @param precision       The precision of entry expressions
     * @param scientific      True if the output will be in scientific notation, false otherwise
     * @return A properly configured MatrixFragment
     */
    public static VectorFragment createInstance(long problemId, String label, int backgroundColor,
                                                boolean enabled, int rows, int precision,
                                                boolean scientific) {

        /*
         * Create an instance of an VectorFragment, and customize it with parameters required
         * of a NumbersFragment. Return the fragment.
         */
        final VectorFragment fragment = new VectorFragment();
        NumbersFragment.customizeInstance(fragment, problemId, label, backgroundColor,
                enabled, rows, 1, true, precision, scientific);
        return fragment;
    }

    @Override
    protected void addIfMissing(int row, int column) {

        // Get the record tracker. Calculate the control ID from the row and the column.
        final RecordTracker<Vector> recordTracker = getRecordTracker();
        final int controlId = calculateId(row, column);

        // Is there no existing record with the calculated control ID?
        Vector vector = recordTracker.get(controlId);
        if (null == vector) {

            /*
             * There is no existing record with the calculated control ID. Create one, and set the
             * problem ID.
             */
            vector = new Vector();
            vector.setProblemId(getProblemId());

            // Set the row. Set an invalid entry, and add the content to the record tracker.
            vector.setRow(row);
            vector.setEntry(Vector.INVALID_ENTRY);
            recordTracker.put(controlId, vector, false);
        }
    }

    @Override
    protected void addWatcher(final EditText editText, int row, int column) {

        /*
         * Call the superclass method. Get the record tracker. Calculate the control ID from the
         * row and the column.
         */
        super.addWatcher(editText, row, column);
        final RecordTracker<Vector> recordTracker = getRecordTracker();
        final int controlId = calculateId(row, column);

        /*
         * Find the existing vector for the control ID. It better be there if
         * addIfMissing(row, column) has been called before this method!
         */
        final Vector vector = recordTracker.get(controlId);
        if (null != vector) {

            /*
             * Found the existing vector entry for the control ID. Create a new number text
             * watcher.
             */
            final TextWatcher watcher = new NumberTextWatcher<Vector>(vector) {

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
            };

            // Set the watcher as a text changed listener, and as a tag.
            editText.addTextChangedListener(watcher);
            editText.setTag(watcher);
        }
    }

    @Override
    protected boolean change(@NonNull Vector record, @NonNull ProblemLab problemLab) {

        // Add or replace the record, and return true for handling the request.
        problemLab.addOrReplace(record);
        return true;
    }

    @Override
    protected boolean delete(@NonNull Vector record, @NonNull ProblemLab problemLab) {

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

        // Call the superclass method, and get the vectors.
        super.onCreate(savedInstanceState);
        vectors = contentProducer.getContent(getProblemId());
    }

    @Override
    public void onDestroy() {

        // Set the vectors to null, and call the superclass method.
        vectors = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {

        // TODO: Delete this method.
        super.onPause();
        outputDatabaseDebugString("Vector", getProblemLab().getVectors(getProblemId()));
    }

    @Override
    protected void setContent(EditText editText) {

        // Is there content for this control?
        final Vector vector = getRecordTracker().get(editText.getId());
        if (null != vector) {

            // There is content for this control. Set it.
            editText.setText(format(vector.getEntry()));
        }
    }

    @Override
    protected void setRecordTracker() {

        /*
         * Create a new record tracker for this fragment. Copy the vectors into the tracker. Set
         * the tracker.
         */

        // Create a new record tracker for this fragment.
        final RecordTracker<Vector> recordTracker = new RecordTracker<>(getControlCount(),
                this);


        // Copy the vector into the tracker.
        copy(recordTracker, vectors, indexProducer);
        setRecordTracker(recordTracker);
    }
}
