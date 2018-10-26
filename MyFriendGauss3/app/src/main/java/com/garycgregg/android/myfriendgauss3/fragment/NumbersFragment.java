package com.garycgregg.android.myfriendgauss3.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.garycgregg.android.myfriendgauss3.R;

import java.util.Locale;

public abstract class NumbersFragment<T> extends ContentFragment<T> {

    // The prefix for instance arguments
    private static final String PREFIX_STRING = NumbersFragment.class.getName();

    // The background color argument
    private static final String BACKGROUND_COLOR_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "background_color");

    // The number of columns argument
    private static final String COLUMNS_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "columns");

    // The fragment enabled argument
    private static final String ENABLED_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "enabled");

    // The format for the hint in each control
    private static final String HINT_FORMAT_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "hint_format");

    // The label argument
    private static final String LABEL_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "label");

    // The number of rows argument
    private static final String ROWS_ARGUMENT = String.format(ARGUMENT_FORMAT_STRING,
            PREFIX_STRING, "rows");

    // The current locale
    private Locale locale;

    /**
     * Customizes an instance of a NumbersFragment with the required argument(s).
     *
     * @param fragment         An existing NumbersFragment
     * @param label            The label argument
     * @param backgroundColor  The background color argument
     * @param enabled          The fragment enabled argument
     * @param rows             The number of rows argument
     * @param columns          The number of columns argument
     * @param singleColumnHint True if the fragment will have a single column, false otherwise
     * @return A new card fragment
     */
    public static ContentFragment customizeInstance(NumbersFragment fragment, String label,
                                                    int backgroundColor, boolean enabled, int rows,
                                                    int columns, boolean singleColumnHint) {

        // Get the existing arguments, if any.
        Bundle arguments = fragment.getArguments();
        if (null == arguments) {

            // Create a new, empty arguments object if there is none already.
            arguments = new Bundle();
        }

        arguments.putInt(BACKGROUND_COLOR_ARGUMENT, backgroundColor);
        arguments.putInt(ROWS_ARGUMENT, rows);
        arguments.putInt(COLUMNS_ARGUMENT, columns);

        arguments.putBoolean(ENABLED_ARGUMENT, enabled);
        arguments.putBoolean(HINT_FORMAT_ARGUMENT, singleColumnHint);

        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void createControls(LayoutInflater inflater, ViewGroup container) {

        final Context context = getActivity();
        final Bundle arguments = getArguments();
        final int defaultValue = 1;
        final Resources resources = getResources();

        final String label = arguments.getString(LABEL_ARGUMENT,
                resources.getString(R.string.tale_title_hint));

        final int backgroundColor = arguments.getInt(BACKGROUND_COLOR_ARGUMENT,
                resources.getColor(R.color.tableEntryDefault));

        final int columns = arguments.getInt(COLUMNS_ARGUMENT, defaultValue);
        final int rows = arguments.getInt(ROWS_ARGUMENT, defaultValue);

        final boolean enabled = arguments.getBoolean(ENABLED_ARGUMENT, true);
        final boolean singleColumnHint = arguments.getBoolean(HINT_FORMAT_ARGUMENT, false);

        int column;
        EditText editText;
        TableRow tableRow;

        inflater.inflate(R.layout.content_table, container, true);
        final TextView tableLabel = container.findViewById(R.id.table_label);
        tableLabel.setText(label);

        final TableLayout tableLayout = container.findViewById(R.id.table_layout);
        for (int row = 0; row < rows; ++row) {

            tableRow = new TableRow(context);
            for (column = 0; column < columns; ++column) {

                editText = (EditText) inflater.inflate(R.layout.content_entry, tableRow,
                        false);
                editText.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);

                editText.setHint(singleColumnHint ?
                        String.format(locale, "(%d)", row) :
                        String.format(locale, "(%d,%d)", row, column));

                editText.setEnabled(enabled);
                tableRow.addView(editText);
            }

            tableLayout.addView(tableRow);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        locale = getResources().getConfiguration().locale;
    }
}
