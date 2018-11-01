package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.content.Vector;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

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
        public Vector[] produceContent(ProblemLab problemLab, long problemId) {

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
     * @param problemId        The problem ID to be associated with the instance
     * @param label            The label argument
     * @param backgroundColor  The background color argument
     * @param enabled          The fragment enabled argument
     * @param rows             The number of rows argument
     * @return A properly configured MatrixFragment
     */
    public static VectorFragment createInstance(long problemId, String label, int backgroundColor,
                                                boolean enabled, int rows) {

        /*
         * Create an instance of an VectorFragment, and customize it with parameters required
         * of a NumbersFragment. Return the fragment.
         */
        final VectorFragment fragment = new VectorFragment();
        NumbersFragment.customizeInstance(fragment, problemId, label, backgroundColor,
                enabled, rows, 1, true);
        return fragment;
    }

    @Override
    protected void addWatcher(final EditText editText, int row, int column) {

        // Get the content index. Calculate the control ID from the row and the column.
        // TODO: Fix this.
        // final SparseArray<Vector> contentIndex = getContentIndex();
        final SparseArray<Vector> contentIndex = null;
        final int controlId = calculateId(row, column);

        // Is there no existing content with the calculated control ID?
        Vector vector = contentIndex.get(controlId);
        if (null == vector) {

            // There is no existing content. Create it, then set the problem ID.
            vector = new Vector();
            vector.setProblemId(getProblemId());

            // Set the row number, and put the content into the content index.
            vector.setRow(row);
            contentIndex.put(controlId, vector);
        }

        // Give the control a number text changed listener.
        editText.addTextChangedListener(new NumberTextWatcher<Vector>(vector, WHITESPACE_PATTERN) {

            @Override
            protected void setChange(String change) {

                /*
                 * Get the content object, set its entry, and add the content object to the change
                 * list.
                 */
                final Vector vector = getContent();
                vector.setEntry(Double.parseDouble(change));
                // TODO: Fix this.
                // addChange(vector);
            }
        });
    }

    @Override
    protected boolean change(Vector record, ProblemLab problemLab) {

        // TODO: Fill this in.
        return false;
    }

    @Override
    protected boolean delete(Vector record, ProblemLab problemLab) {

        // TODO: Fill this in.
        return false;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and get the vectors. Create and set a content index.
        super.onCreate(savedInstanceState);
        vectors = contentProducer.getContent(getProblemId());

        // TODO: Fix this.
        // setContentIndex(indexProducer.populateArray(new SparseArray<Vector>(), vectors));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Call the superclass method to get a view. Clear the changes, and return the view.
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        // clearChanges();
        return view;
    }

    @Override
    public void onDestroy() {

        // Set the vectors to null, and call the superclass method.
        vectors = null;
        super.onDestroy();
    }

    @Override
    protected void setContent(EditText editText, int controlId) {

        // Get the content index. Is there content for this control?
        // TODO: Fix this.
        // final SparseArray<Vector> contentIndex = getContentIndex();
        final SparseArray<Vector> contentIndex = null;
        final Vector vector = contentIndex.get(controlId);
        if (null != vector) {

            // There is content for this control. Set it.
            editText.setText(Double.toString(vector.getEntry()));
        }
    }
}
