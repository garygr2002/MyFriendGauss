package com.garycgregg.android.myfriendgauss2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

public class NumbersFragment extends CardFragment {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String PREFIX_STRING = NumbersFragment.class.getName();
    private static final String BACKGROUND_COLOR_ARGUMENT = String.format(FORMAT_STRING,
            PREFIX_STRING, "background_color");
    private static final String COLUMNS_ARGUMENT = String.format(FORMAT_STRING,
            PREFIX_STRING, "columns");
    private static final String ENABLED_ARGUMENT = String.format(FORMAT_STRING,
            PREFIX_STRING, "enabled");
    private static final String HINT_FORMAT_ARGUMENT = String.format(FORMAT_STRING,
            PREFIX_STRING, "hint_format");
    private static final String LABEL_ARGUMENT = String.format(FORMAT_STRING,
            PREFIX_STRING, "label");
    private static final String ROWS_ARGUMENT = String.format(FORMAT_STRING,
            PREFIX_STRING, "rows");
    private Locale locale;

    public static Fragment createInstance(String label, int backgroundColor, boolean enabled,
                                          int rows, int columns, boolean singleColumnHint) {

        final Bundle arguments = new Bundle();
        arguments.putString(LABEL_ARGUMENT, label);

        arguments.putInt(BACKGROUND_COLOR_ARGUMENT, backgroundColor);
        arguments.putInt(ROWS_ARGUMENT, rows);
        arguments.putInt(COLUMNS_ARGUMENT, columns);

        arguments.putBoolean(ENABLED_ARGUMENT, enabled);
        arguments.putBoolean(HINT_FORMAT_ARGUMENT, singleColumnHint);

        final Fragment fragment = new NumbersFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        final Context activity = getActivity();
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

            tableRow = new TableRow(activity);
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
