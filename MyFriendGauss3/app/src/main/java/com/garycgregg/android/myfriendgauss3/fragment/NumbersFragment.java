package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
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

    // The number of columns
    private int columns;

    // An index of contents by control ID
    private SparseArray<T> contentIndex;

    // The current locale
    private Locale locale;

    // The number or rows
    private int rows;

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
     * Adds a text watcher to an edit control.
     *
     * @param editText The edit control
     * @param row      The row of the edit control
     * @param column   The column of the edit control
     */
    protected abstract void addWatcher(EditText editText, int row, int column);

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        output("createContent(LayoutInflater, ViewGroup)");

        // Get the context and the arguments.
        final Context context = getActivity();
        final Bundle arguments = getArguments();

        // Get the resources. Retrieve the label argument.
        final Resources resources = getResources();
        final String label = arguments.getString(LABEL_ARGUMENT,
                resources.getString(R.string.table_title_hint));

        // Retrieve the background color and enabled flag.
        final int backgroundColor = arguments.getInt(BACKGROUND_COLOR_ARGUMENT,
                resources.getColor(R.color.tableEntryDefault));
        final boolean enabled = isEnabled();

        // Get the number of rows.
        final int defaultValue = 1;
        final int rows = arguments.getInt(ROWS_ARGUMENT, defaultValue);
        setRows(rows);

        // Get the number of columns.
        final int columns = arguments.getInt(COLUMNS_ARGUMENT, defaultValue);
        setColumns(columns);

        // Get the hint format argument.
        final boolean singleColumnHint = arguments.getBoolean(HINT_FORMAT_ARGUMENT,
                false);

        // Declare other local variables.
        int column;
        EditText editText;
        TableRow tableRow;

        // Inflate the content. Get the table container, and set its label.
        inflater.inflate(R.layout.content_table, container, true);
        final TextView tableLabel = container.findViewById(R.id.table_label);
        tableLabel.setText(label);

        // Get the table layout, and cycle for each table row.
        final TableLayout tableLayout = container.findViewById(R.id.table_layout);
        setTableLayout(tableLayout);
        for (int row = 0; row < rows; ++row) {

            // Create a new table row. Cycle for each column.
            tableRow = new TableRow(context);
            for (column = 0; column < columns; ++column) {

                // Create a content entry for the first/next row and column. Set its background.
                editText = (EditText) inflater.inflate(R.layout.content_entry, tableRow,
                        false);
                editText.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);

                // Set the hint.
                editText.setHint(singleColumnHint ?
                        String.format(locale, "(%d)", row) :
                        String.format(locale, "(%d,%d)", row, column));

                // Set the control ID.
                final int controlId = calculateId(row, column);
                editText.setId(controlId);

                // Make the control clickable or focusable if it is enabled.
                editText.setClickable(enabled);
                editText.setFocusable(enabled);

                // Add content and a watcher to the content entry. Add the entry to the table row.
                setContent(editText, controlId);
                addWatcher(editText, row, column);
                tableRow.addView(editText);
            }

            // Add the table row to the table layout.
            tableLayout.addView(tableRow);
        }
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
     * Gets the content index.
     *
     * @return The content index
     */
    protected SparseArray<T> getContentIndex() {
        return contentIndex;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // Call the superclass method, and get the current locale.
        super.onCreate(savedInstanceState);
        locale = getResources().getConfiguration().locale;
    }

    @Override
    public void onDestroy() {

        // Clear the locale before calling superclass method.
        locale = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        // Set the table layout to null, and call the superclass method.
        setTableLayout(null);
        super.onDestroyView();
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
     * @param editText  The edit control
     * @param controlId The ID of the edit control
     */
    protected abstract void setContent(EditText editText, int controlId);

    /**
     * Sets the content index.
     *
     * @param contentIndex The content index
     */
    protected void setContentIndex(SparseArray<T> contentIndex) {
        this.contentIndex = contentIndex;
    }

    /**
     * Sets the number of rows.
     *
     * @param rows The number of rows
     */
    private void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Sets the table layout.
     *
     * @param tableLayout The table layout
     */
    private void setTableLayout(TableLayout tableLayout) {
        this.tableLayout = tableLayout;
    }

    protected abstract static class IndexProducer<U> {

        /**
         * Populates a sparse array with contents.
         *
         * @param contentIndex A sparse array
         * @param content      The contents
         * @return The sparse array that the caller passed in
         */
        public SparseArray<U> populateArray(SparseArray<U> contentIndex, U[] content) {

            // Cycle for each content item.
            for (U item : content) {

                // Place the item in the sparse array with an appropriate index.
                contentIndex.put(produceId(item), item);
            }

            // Return the sparse array.
            return contentIndex;
        }

        /**
         * Produces an index for a given content item.
         *
         * @param contentItem The given content item
         * @return An index for the given content item
         */
        public abstract int produceId(U contentItem);
    }
}
