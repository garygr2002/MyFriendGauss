package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.garycgregg.android.myfriendgauss3.content.Answer;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import java.util.List;

public class AnswerFragment extends NumbersFragment<Answer> {

    // The tag for our logging
    private static final String TAG = AnswerFragment.class.getSimpleName();

    // Our content producer
    private final ContentProducer<Answer[]> contentProducer = new ContentProducer<Answer[]>() {

        @Override
        public Answer[] onNotFound(ProblemLab problemLab, long problemId) {
            return Answer.CREATOR.newArray(0);
        }

        @Override
        public Answer[] produceContent(ProblemLab problemLab, long problemId) {

            // Get answers for the given problem ID. Create an array to hold them.
            final List<Answer> answerList = problemLab.getAnswers(problemId);
            final Answer[] answers = Answer.CREATOR.newArray(answerList.size());

            // Copy the list entries to the array, and return the array.
            answerList.toArray(answers);
            return answers;
        }
    };
    // Our index producer
    private final IndexProducer<Answer> indexProducer = new IndexProducer<Answer>() {

        @Override
        public int produceId(Answer contentItem) {
            return calculateId(contentItem.getRow(), 0);
        }
    };
    // The answers
    private Answer[] answers;

    /**
     * Customizes an instance of an AnswerFragment with the required argument(s).
     *
     * @param problemId       The problem ID to be associated with the instance
     * @param label           The label argument
     * @param backgroundColor The background color argument
     * @param rows            The number of rows argument
     * @return A properly configured AnswerFragment
     */
    public static AnswerFragment createInstance(long problemId, String label, int backgroundColor,
                                                int rows) {

        /*
         * Create an instance of an AnswerFragment, and customize it with parameters required
         * of a NumbersFragment. Return the fragment.
         */
        final AnswerFragment fragment = new AnswerFragment();
        NumbersFragment.customizeInstance(fragment, problemId, label, backgroundColor,
                false, rows, 1, true);
        return fragment;
    }

    @Override
    protected void addWatcher(final EditText editText, int row, int column) {

        // Get the content index. Calculate the control ID from the row and the column.
        // TODO: Fix this.
        // final SparseArray<Answer> contentIndex = getContentIndex();
        final SparseArray<Answer> contentIndex = null;
        final int controlId = calculateId(row, column);

        // Is there no existing content with the calculated control ID?
        Answer answer = contentIndex.get(controlId);
        if (null == answer) {

            // There is no existing content. Create it, then set the problem ID.
            answer = new Answer();
            answer.setProblemId(getProblemId());

            // Set the row number, and put the content into the content index.
            answer.setRow(row);
            contentIndex.put(controlId, answer);
        }

        // Give the control a number text changed listener.
        editText.addTextChangedListener(new NumberTextWatcher<Answer>(answer, WHITESPACE_PATTERN) {

            @Override
            protected void setChange(String change) {

                /*
                 * Get the content object, set its entry, and add the content object to the change
                 * list.
                 */
                final Answer answer = getContent();
                answer.setEntry(Double.parseDouble(change));
                // TODO: Fix this.
                // addChange(answer);
            }
        });
    }

    @Override
    protected boolean change(Answer record, ProblemLab problemLab) {

        // TODO: Fill this in.
        return false;
    }

    @Override
    protected boolean delete(Answer record, ProblemLab problemLab) {

        // TODO: Fill this in.
        return false;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and get the answers.
        super.onCreate(savedInstanceState);
        answers = contentProducer.getContent(getProblemId());
        // TODO: Fix this.
        // setContentIndex(indexProducer.populateArray(new SparseArray<Answer>(), answers));
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

        // Set the answers to null, and call the superclass method.
        answers = null;
        super.onDestroy();
    }

    @Override
    protected void setContent(EditText editText, int controlId) {

        // Get the content index. Is there content for this control?
        // TODO: Fix this.
        // final SparseArray<Answer> contentIndex = getContentIndex();
        // final Answer answer = contentIndex.get(controlId);
        final Answer answer = null;
        if (null != answer) {

            // There is content for this control. Set it.
            editText.setText(Double.toString(answer.getEntry()));
        }
    }
}
