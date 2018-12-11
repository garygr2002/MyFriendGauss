package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.garycgregg.android.myfriendgauss3.R;
import com.garycgregg.android.myfriendgauss3.content.BaseGaussEntry;
import com.garycgregg.android.myfriendgauss3.content.Matrix;
import com.garycgregg.android.myfriendgauss3.database.ProblemLab;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class NumbersFragment<T> extends ContentFragment<T>
        implements RecordTracker.CountListener {

    // The maximum entry precision
    public static final int MAXIMUM_PRECISION = 15;

    // The minimum entry precision
    public static final int MINIMUM_PRECISION = 0;

    // The default entry precision
    public static final int DEFAULT_PRECISION = MINIMUM_PRECISION;

    // TODO: Delete this; The special tag for database debugging
    protected static final String SPECIAL = "DatabaseDebug";

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

    // The precision argument key
    private static final String PRECISION_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "precision");

    // The number of rows argument key
    private static final String ROWS_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "rows");

    // The scientific notation argument key
    private static final String SCIENTIFIC_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "scientific");

    // The tag for our logging
    private static final String TAG = NumbersFragment.class.getSimpleName();

    // Notifies a listener of an 'on equal' event
    private final ListenerNotifier equalNotifier = new ListenerNotifier() {

        @Override
        public void notifyListener(@NonNull CountListener listener, int id) {
            listener.onEqual(id);
        }
    };

    // Notifies a listener of an 'on greater' event
    private final ListenerNotifier greaterNotifier = new ListenerNotifier() {

        @Override
        public void notifyListener(@NonNull CountListener listener, int id) {
            listener.onGreater(id);
        }
    };

    // Notifies a listener of an 'on less' event
    private final ListenerNotifier lessNotifier = new ListenerNotifier() {

        @Override
        public void notifyListener(@NonNull CountListener listener, int id) {
            listener.onLess(id);
        }
    };

    // The activity count listener
    private CountListener activityListener;

    // The background color of this pane
    private int backgroundColor;

    // The number of columns
    private int columns;

    // The entry formatter
    private NumberFormat formatter;

    // The label for this pane
    private String label;

    // The current locale
    private Locale locale;

    // The parent fragment count listener
    private CountListener parentListener;

    // The precision of entry expressions
    private int precision;

    // The number or rows
    private int rows;

    // True if the output will be in scientific notation, false otherwise
    private boolean scientific;

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
     * Creates a number format given a precision and a flag indicating whether scientific notation
     * is desired.
     *
     * @param precision  The precision of the output
     * @param scientific True if the output will be in scientific notation, false otherwise (Note:
     *                   this method ignores this parameter if the precision is DEFAULT_PRECISION,
     *                   or is otherwise not a positive value).
     * @return A number format with the desired characteristics
     */
    @Nullable
    private static NumberFormat createFormat(int precision, boolean scientific) {

        // Declare and initialize the result. Is the precision not the default?
        NumberFormat result = null;
        if (DEFAULT_PRECISION < precision) {

            /*
             * The precision is not the default. Create a character array with the desired
             * precision, and fill it with the DecimalFormat number character.
             */
            final char[] precisionChars = new char[precision - 1];
            Arrays.fill(precisionChars, '#');

            /*
             * Create a format string for the non-exponent portion of the format. Create and return
             * a new DecimalFormat in either non-scientific, or scientific notation.
             */
            final String format = String.format("0.0%s", new String(precisionChars));
            result = new DecimalFormat(scientific ? String.format("%sE0", format) : format);
        }

        // Return the result.
        return result;
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
     * @param precision        The precision of entry expressions
     * @param scientific       True if the output will be in scientific notation, false otherwise
     */
    public static void customizeInstance(NumbersFragment<?> fragment, long problemId,
                                         String label, int backgroundColor,
                                         boolean enabled, int rows, int columns,
                                         boolean singleColumnHint,
                                         int precision,
                                         boolean scientific) {

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

        // Add the precision and scientific notation arguments.
        arguments.putInt(PRECISION_ARGUMENT, precision);
        arguments.putBoolean(SCIENTIFIC_ARGUMENT, scientific);
    }

    /**
     * Notifies a count listener of an event.
     *
     * @param countListener The count listener to notify
     * @param notifier      The notifier containing the event to fire
     * @param id            The ID of the fragment firing the callback
     */
    private static void notifyListener(CountListener countListener,
                                       ListenerNotifier notifier, int id) {

        // Only notify the listener if it is not null.
        if (null != countListener) {
            notifier.notifyListener(countListener, id);
        }
    }

    /**
     * Outputs a database debug string. TODO: Delete this.
     *
     * @param label The label to use for the debug string
     * @param list  A list of BaseGaussEntries
     * @param <U>   The type of BaseGaussEntry in the list
     */
    protected static <U extends BaseGaussEntry> void outputDatabaseDebugString(String label,
                                                                               List<U> list) {

        // Cycle for each element in the list.
        for (BaseGaussEntry entry : list) {

            // Output the debug string.
            Log.d(SPECIAL, String.format("%s entry for problem ID %d, row %d, column %d: '%f'.",
                    label, entry.getProblemId(), entry.getRow(),
                    (entry instanceof Matrix) ? ((Matrix) entry).getColumn() : 0,
                    entry.getEntry()));
        }
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

        // Give the EditText a focus change listener.
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                // Does the view not have focus?
                if (!hasFocus) {

                    /*
                     * Cast the view to an EditText, and try to convert its content. Is the content
                     * not a decimal number?
                     */
                    final EditText field = (EditText) view;
                    final Double entry = NumberTextWatcher.convert(field.getText().toString());
                    if (null == entry) {

                        // The content is not a decimal number. Clear the text field.
                        field.setText("");
                    }

                    // The content as a decimal number.
                    else {
                        setUnwatchedText(field, entry);
                    }
                }
            }
        });
    }

    /**
     * Determines if the numbers fragment contains missing entries.
     *
     * @return True if the numbers fragment contains missing entries, false otherwise
     */
    public boolean areEntriesMissing() {
        return getRecordTracker().areEntriesMissing();
    }

    /**
     * Creates content for a single table row.
     *
     * @param inflater The inflater to use for each control
     * @param tableRow The table row to receive the controls
     * @param row      The number of the row
     */
    private void createContent(LayoutInflater inflater, TableRow tableRow, int row) {

        // Get the background color and the context.
        final int backgroundColor = getBackgroundColor();
        final Context context = getActivity();

        // Get the enabled and single column hint flags.
        final boolean enabled = isEnabled();
        final boolean singleColumnHint = isSingleColumnHint();

        // Declare a variable to receive an edit text. Cycle for each column.
        EditText editText;
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

        // Inflate our content. Create the formatter.
        inflater.inflate(R.layout.content_table, container, true);
        formatter = createFormat(getPrecision(), isScientific());

        // Set the label for the table.
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
     * Formats an entry.
     *
     * @param entry An entry to format
     * @return The formatted entry
     */
    protected String format(double entry) {
        return ((null == formatter) ? Double.toString(entry) : formatter.format(entry))
                .toLowerCase();
    }

    /**
     * Gets the activity count listener.
     *
     * @return The activity count listener
     */
    private CountListener getActivityListener() {
        return activityListener;
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
     * Gets the parent fragment count listener.
     *
     * @return The parent fragment count listener
     */
    private CountListener getParentListener() {
        return parentListener;
    }

    /**
     * Gets the precision of entry expressions.
     *
     * @return The precision of entry expressions
     */
    protected int getPrecision() {
        return precision;
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
     * Determines if the output will be in scientific notation.
     *
     * @return True if the output will be in scientific notation, false otherwise
     */
    protected boolean isScientific() {
        return scientific;
    }

    /**
     * Determines whether the hint string for each control will be single column.
     *
     * @return True if the hint string will be a single column, false otherwise
     */
    protected boolean isSingleColumnHint() {
        return singleColumnHint;
    }

    /**
     * Notifies the count listeners of an event.
     *
     * @param notifier The notifier containing the event to fire
     * @param id       The ID of the fragment firing the callback
     */
    private void notifyListeners(ListenerNotifier notifier, int id) {

        // Notify first the parent fragment listener, then the activity listener.
        notifyListener(getParentListener(), notifier, id);
        notifyListener(getActivityListener(), notifier, id);
    }

    @Override
    public void onAttach(Context context) {

        // Call the superclass method, and set the activity listener.
        super.onAttach(context);
        setActivityListener((context instanceof CountListener) ?
                ((CountListener) context) : null);

        // Set the parent fragment listener.
        final Fragment fragment = getParentFragment();
        setParentListener((fragment instanceof CountListener) ?
                ((CountListener) fragment) : null);
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

        // Get the precision, and the scientific notation flag.
        setPrecision(arguments.getInt(PRECISION_ARGUMENT, DEFAULT_PRECISION));
        setScientific(arguments.getBoolean(SCIENTIFIC_ARGUMENT, false));
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

        // Release the changes and set the table layout to null.
        releaseChanges();
        setTableLayout(null);

        // Set the formatter to null. Call the superclass method.
        formatter = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {

        // Clear the listeners, and call the superclass method.
        setParentListener(null);
        setActivityListener(null);
        super.onDetach();
    }

    @Override
    public void onEqual() {
        notifyListeners(equalNotifier, getId());
    }

    @Override
    public void onGreater() {
        notifyListeners(greaterNotifier, getId());
    }

    @Override
    public void onLess() {
        notifyListeners(lessNotifier, getId());
    }

    /**
     * Sets the activity count listener.
     *
     * @param activityListener The activity count listener
     */
    private void setActivityListener(CountListener activityListener) {
        this.activityListener = activityListener;
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
     * Sets the parent fragment count listener.
     *
     * @param parentListener The parent fragment count listener
     */
    private void setParentListener(CountListener parentListener) {
        this.parentListener = parentListener;
    }

    /**
     * Sets the precision of entry expressions.
     *
     * @param precision The precision of entry expressions
     */
    private void setPrecision(int precision) {
        this.precision = precision;
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
     * Sets if the output will be in scientific notation, false otherwise
     *
     * @param scientific True if the output will be in scientific notation, false otherwise
     */
    private void setScientific(boolean scientific) {
        this.scientific = scientific;
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
     * Removes a text watcher from an edit text before setting the text using a formatted double,
     * then adds back the listener.
     *
     * @param field The edit text in which to set the entry
     * @param entry The entry to set in the edit text
     * @return True if a watcher was removed and re-added, false otherwise
     */
    private boolean setUnwatchedText(@NonNull EditText field, double entry) {

        /*
         * Get any object attached as a tag to the field. Try to cast the object to a text
         * watcher.
         */
        final Object tag = field.getTag();
        final TextWatcher watcher = (tag instanceof TextWatcher) ? (TextWatcher) tag : null;

        // Is the object a text watcher?
        final boolean result = (null != watcher);
        if (result) {

            // The object is a text watcher. Remove it from the field.
            field.removeTextChangedListener(watcher);
        }

        // Set the text in the field using the formatted entry.
        field.setText(format(entry));
        if (result) {

            // Add the watcher back to the field.
            field.addTextChangedListener(watcher);
        }

        // Return the result.
        return result;
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

    public interface CountListener {

        /**
         * Indicates the record count has transitioned to equal capacity.
         *
         * @param id The ID of the fragment firing the callback
         */
        void onEqual(int id);

        /**
         * Indicates the record count has transitioned to greater than capacity.
         *
         * @param id The ID of the fragment firing the callback
         */
        void onGreater(int id);

        /**
         * Indicates the record count has transitioned to less than capacity.
         *
         * @param id The ID of the fragment firing the callback
         */
        void onLess(int id);
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

    private interface ListenerNotifier {

        /**
         * Notifies a count listener that an event has occurred.
         *
         * @param listener The listener to notify
         * @param id       The ID of the fragment firing the callback
         */
        void notifyListener(@NonNull CountListener listener, int id);
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
