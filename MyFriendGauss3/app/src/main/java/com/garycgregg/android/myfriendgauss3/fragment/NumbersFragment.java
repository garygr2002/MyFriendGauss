package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import java.util.Locale;

public abstract class NumbersFragment<T> extends ContentFragment<T> {

    // The base control ID
    private static final int BASE_ID = 0;

    // The prefix for instance arguments
    private static final String PREFIX_STRING = NumbersFragment.class.getName();

    // The background color argument key
    private static final String BACKGROUND_COLOR_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "background_color");

    // The number of columns argument key
    private static final String COLUMNS_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "columns");

    // The key for the format for the hint in each control
    private static final String HINT_FORMAT_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "hint_format");

    // The label argument key
    private static final String LABEL_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "label");

    // The number of rows argument key
    private static final String ROWS_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "rows");

    // The tag for our logging
    private static final String TAG = NumbersFragment.class.getSimpleName();

    // The background color of this pane
    private int backgroundColor;

    // The number of columns
    private int columns;

    // The label for this pane
    private String label;

    // The current locale
    private Locale locale;

    // The number or rows
    private int rows;

    // True if the hint string will be a single column, false otherwise
    private boolean singleColumnHint;

    // Our table layout
    private TableLayout tableLayout;

    /**
     * Calculates a control ID from a row and column.
     *
     * @param row    A row number
     * @param column A column number
     * @return A control ID
     */
    protected static int calculateId(int row, int column) {
        return row * ProblemLab.MAX_DIMENSIONS + column + BASE_ID;
    }

    /**
     * Calculates a row and column from a control ID.
     *
     * @param controlId A control ID
     * @return A pair containing a row number and a column number
     */
    protected static Pair<Integer, Integer> calculateRowAndColumn(int controlId) {

        // Remove the base ID, and calculate the row number.
        final int minusBase = controlId - BASE_ID;
        final int row = minusBase / ProblemLab.MAX_DIMENSIONS;

        // Calculate the column number. Create and return the row/column pair.
        final int column = minusBase % ProblemLab.MAX_DIMENSIONS;
        return new Pair<>(row, column);
    }

    /**
     * Copies existing records from an array to a record tracker. The records are assumed exist
     * for the purposes of tracking.
     *
     * @param recordTracker A record tracker
     * @param records       The records to copy
     * @param indexProducer A producer of indices
     * @param <T>           The type of the record tracker, the array, and the index producer
     */
    protected static <T> void copy(RecordTracker<T> recordTracker, T[] records,
                                   IndexProducer<T> indexProducer) {

        // Cycle for each record, and put it in the record tracker.
        for (T record : records) {
            recordTracker.put(indexProducer.produceId(record), record, true);
        }
    }

    /**
     * Customizes an instance of a NumbersFragment with the required argument(s).
     *
     * @param fragment         An existing NumbersFragment
     * @param problemId        The problem ID to be associated with the instance
     * @param label            The label argument
     * @param backgroundColor  The background color argument
     * @param rows             The number of rows argument
     * @param columns          The number of columns argument
     * @param singleColumnHint True if the fragment will have a single column, false otherwise
     */
    public static void customizeInstance(NumbersFragment<?> fragment, long problemId,
                                         String label, int backgroundColor,
                                         boolean enabled, int rows, int columns,
                                         boolean singleColumnHint) {

        // Customize the fragment for content arguments. Get the fragment arguments.
        ContentFragment.customizeInstance(fragment, problemId, enabled);
        final Bundle arguments = getArguments(fragment);

        // Add the label and background color arguments.
        arguments.putString(LABEL_ARGUMENT, label);
        arguments.putInt(BACKGROUND_COLOR_ARGUMENT, backgroundColor);

        // Add the rows, columns and hint format arguments.
        arguments.putInt(ROWS_ARGUMENT, rows);
        arguments.putInt(COLUMNS_ARGUMENT, columns);
        arguments.putBoolean(HINT_FORMAT_ARGUMENT, singleColumnHint);
    }

    /**
     * Performs a view action on each view in a view group.
     *
     * @param viewGroup The view group
     * @param action    The action to perform
     */
    private static void performAction(ViewGroup viewGroup, ViewAction action) {

        // Cycle for each child view in the group.
        final int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; ++i) {

            // Perform the action on the first/next view.
            action.performAction(viewGroup.getChildAt(i));
        }
    }

    /**
     * Sets the value in an edit text.
     *
     * @param editText  The edit text in which to set a value
     * @param value     The value to set
     * @param forceFill Fill the control even if it has other content
     */
    private static void setValue(EditText editText, double value, boolean forceFill) {

        /*
         * Fill the control if the force-fill flag is set, or if the control only contains
         * whitespace content.
         */
        if (forceFill || NumberTextWatcher.isWhitespace(editText.getText().toString())) {
            editText.setText(Double.toString(value));
        }
    }

    /**
     * Adds an entry to the record tracker if it is missing.
     *
     * @param row    The row of the entry to check
     * @param column The column of the entry to check
     */
    protected void addIfMissing(int row, int column) {

        // The default action is to add no missing tracking entries.
    }

    /**
     * Adds a text watcher to an edit control.
     *
     * @param editText The edit control
     * @param row      The row of the edit control
     * @param column   The column of the edit control
     */
    protected void addWatcher(EditText editText, int row, int column) {

        // The default action is to add no text watcher.
    }

    private void createContent(LayoutInflater inflater, TableRow tableRow, int row) {

        // Get the background color and the context.
        final int backgroundColor = getBackgroundColor();
        final Context context = getActivity();

        // Get the enabled and single column hint flags.
        final boolean enabled = isEnabled();
        final boolean singleColumnHint = isSingleColumnHint();

        // Declare a variable to receive an edit text. Get the record tracker.
        EditText editText;
        final RecordTracker<?> recordTracker = getRecordTracker();

        final int columns = getColumns();
        for (int column = 0; column < columns; ++column) {

            // Create a new edit text, and set its background color.
            editText = (EditText) inflater.inflate(R.layout.content_entry, tableRow,
                    false);
            editText.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);

            // Set its hint.
            editText.setHint(singleColumnHint ?
                    String.format(locale, "(%d)", row) :
                    String.format(locale, "(%d,%d)", row, column));

            /*
             * Set the ID of the edit text. Make the control clickable or focusable if it is
             * enabled.
             */
            editText.setId(calculateId(row, column));
            editText.setClickable(enabled);
            editText.setFocusable(enabled);

            // Set content for the edit text. Add a record tracker entry if it is missing.
            setContent(editText);
            addIfMissing(row, column);

            // Add a watcher for the edit text, and add the entry to the table row.
            addWatcher(editText, row, column);
            tableRow.addView(editText);
        }
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        output("createContent(LayoutInflater, ViewGroup)");

        // Inflate our content. Set the label for the table.
        inflater.inflate(R.layout.content_table, container, true);
        final TextView tableLabel = container.findViewById(R.id.table_label);
        tableLabel.setText(getLabel());

        // Set the table layout and a record tracker.
        final TableLayout tableLayout = container.findViewById(R.id.table_layout);
        setTableLayout(tableLayout);
        setRecordTracker();

        // Get the context, and declare a variable to receive a table row.
        final Context context = getActivity();
        TableRow tableRow;

        // Get the number of rows, and cycle for each.
        final int rows = getRows();
        for (int row = 0; row < rows; ++row) {

            // Create a new table row and its content. Add the row to the table layout.
            tableRow = new TableRow(context);
            createContent(inflater, tableRow, row);
            tableLayout.addView(tableRow);
        }
    }

    /**
     * Gets the background color for this pane.
     *
     * @return The background color for this pane
     */
    protected int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Gets the number of columns.
     *
     * @return The number of columns
     */
    protected int getColumns() {
        return columns;
    }

    /**
     * Returns the number of controls in the fragment.
     *
     * @return The number of controls in the fragment
     */
    protected int getControlCount() {
        return getColumns() * getRows();
    }

    /**
     * Gets the label for this pane.
     *
     * @return The label for this pane
     */
    protected String getLabel() {
        return label;
    }

    /**
     * Gets the current locale.
     *
     * @return The current locale
     */
    protected Locale getLocale() {
        return locale;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    /**
     * Gets the number of rows.
     *
     * @return The number of rows
     */
    protected int getRows() {
        return rows;
    }

    /**
     * Gets the table layout.
     *
     * @return The table layout
     */
    protected TableLayout getTableLayout() {
        return tableLayout;
    }

    /**
     * Determines whether the hint string for each control will be single column.
     *
     * @return True if the hint string will be a single column, false otherwise
     */
    protected boolean isSingleColumnHint() {
        return singleColumnHint;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and our resources.
        super.onCreate(savedInstanceState);
        final Resources resources = getResources();

        // Set our local, and get our arguments.
        setLocale(resources.getConfiguration().locale);
        final Bundle arguments = getArguments();

        // Retrieve the label argument.
        setLabel(arguments.getString(LABEL_ARGUMENT,
                resources.getString(R.string.table_title_hint)));

        // Retrieve the background color.
        setBackgroundColor(arguments.getInt(BACKGROUND_COLOR_ARGUMENT,
                resources.getColor(R.color.tableEntryDefault)));

        // Declare the default rows/columns value, and get the number of rows.
        final int defaultValue = 1;
        setRows(arguments.getInt(ROWS_ARGUMENT, defaultValue));

        // Get the number of columns, and the single hint format flag.
        setColumns(arguments.getInt(COLUMNS_ARGUMENT, defaultValue));
        setSingleColumnHint(arguments.getBoolean(HINT_FORMAT_ARGUMENT, false));
    }

    @Override
    public void onDestroy() {

        // Clear the single column hint.
        final int defaultClear = 0;
        setSingleColumnHint(false);

        // Clear the number of columns and rows.
        setColumns(defaultClear);
        setRows(defaultClear);

        // Clear the background color and label.
        setBackgroundColor(defaultClear);
        setLabel(null);

        // Clear the locale, and call the superclass method.
        setLocale(null);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        // Release the changes, and set the table layout to null. Call the superclass method.
        releaseChanges();
        setTableLayout(null);
        super.onDestroyView();
    }

    /**
     * Sets the background color for this pane.
     *
     * @param backgroundColor The background color for this pane
     */
    private void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Sets the number of columns.
     *
     * @param columns The number of columns
     */
    private void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * Sets content of an edit control.
     *
     * @param editText The edit control
     */
    protected abstract void setContent(EditText editText);

    /**
     * Sets the label for this pane.
     *
     * @param label The label for this pane
     */
    private void setLabel(String label) {
        this.label = label;
    }

    /**
     * Sets the current locale.
     *
     * @param locale The current locale
     */
    private void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Sets the record tracker.
     */
    protected abstract void setRecordTracker();

    /**
     * Sets the number of rows.
     *
     * @param rows The number of rows
     */
    private void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Sets the single column hint.
     *
     * @param singleColumnHint True if the hint string will be a single column, false otherwise
     */
    private void setSingleColumnHint(boolean singleColumnHint) {
        this.singleColumnHint = singleColumnHint;
    }

    /**
     * Sets the table layout.
     *
     * @param tableLayout The table layout
     */
    private void setTableLayout(TableLayout tableLayout) {
        this.tableLayout = tableLayout;
    }

    /**
     * Sets values in the fragment.
     *
     * @param value     The value to set
     * @param forceFill Fill the control even if it has other content
     */
    public void setValue(final double value, final boolean forceFill) {

        // Perform this action for each table row.
        performAction(getTableLayout(), new ViewAction() {

            @Override
            public void performAction(View view) {

                // Perform this action for each edit control.
                NumbersFragment.performAction((TableRow) view, new ViewAction() {

                    @Override
                    public void performAction(View view) {
                        setValue((EditText) view, value, forceFill);
                    }
                });
            }
        });
    }

    protected interface IndexProducer<U> {

        /**
         * Produces an index for a given content item.
         *
         * @param contentItem The given content item
         * @return An index for the given content item
         */
        int produceId(U contentItem);
    }

    private interface ViewAction {

        /**
         * Performs an action on a view.
         *
         * @param view The view on which to perform an action
         */
        void performAction(View view);
    }
}
