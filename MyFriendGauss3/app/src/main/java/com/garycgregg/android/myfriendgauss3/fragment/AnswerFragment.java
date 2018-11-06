package com.garycgregg.android.myfriendgauss3.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        public Answer[] produceContent(@NonNull ProblemLab problemLab, long problemId) {

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
    protected boolean change(@NonNull Answer record, @NonNull ProblemLab problemLab) {

        // This fragment does not change answer entries.
        return false;
    }

    @Override
    protected boolean delete(@NonNull Answer record, @NonNull ProblemLab problemLab) {

        // This fragment does not delete answer entries.
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
    }

    @Override
    public void onDestroy() {

        // Set the answers to null, and call the superclass method.
        answers = null;
        super.onDestroy();
    }

    @Override
    protected void setContent(EditText editText) {

        // Is there content for this control?
        final Answer answer = getRecordTracker().get(editText.getId());
        if (null != answer) {

            // There is content for this control. Set it.
            editText.setText(Double.toString(answer.getEntry()));
        }
    }

    @Override
    protected void setRecordTracker() {

        /*
         * Create a new record tracker for this fragment. Copy the answers into the tracker. Set
         * the tracker.
         */
        final RecordTracker<Answer> recordTracker = new RecordTracker<>(getControlCount());
        copy(recordTracker, answers, indexProducer);
        setRecordTracker(recordTracker);
    }
}
