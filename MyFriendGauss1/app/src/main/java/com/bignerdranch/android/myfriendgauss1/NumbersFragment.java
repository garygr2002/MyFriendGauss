package com.bignerdranch.android.myfriendgauss1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Locale;

public class NumbersFragment extends CardFragment {

    private static final String FORMAT_STRING = "%s.%s_argument";
    private static final String COLUMNS_ARGUMENT = String.format(FORMAT_STRING,
            NumbersFragment.class.getName(),
            "columns");
    private static final String ROWS_ARGUMENT = String.format(FORMAT_STRING,
            NumbersFragment.class.getName(),
            "rows");
    private Locale locale;

    public static Fragment createInstance(int rows, int columns) {

        final Bundle arguments = new Bundle();
        arguments.putSerializable(ROWS_ARGUMENT, rows);
        arguments.putSerializable(COLUMNS_ARGUMENT, columns);

        final Fragment fragment = new NumbersFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void createContent(LayoutInflater inflater, ViewGroup container) {

        final Bundle arguments = getArguments();
        final int defaultValue = 1;

        final int columns = arguments.getInt(COLUMNS_ARGUMENT, defaultValue);
        final int rows = arguments.getInt(ROWS_ARGUMENT, defaultValue);
        final Context activity = getActivity();

        int column;
        EditText editText;
        TableRow tableRow;

        inflater.inflate(R.layout.fragment_table, container, true);
        final TableLayout tableLayout = container.findViewById(R.id.table_layout);
        for (int row = 0; row < rows; ++row) {

            tableRow = new TableRow(activity);
            for (column = 0; column < columns; ++column) {

                editText = (EditText) inflater.inflate(R.layout.table_item, tableRow, false);

                editText.setHint((1 < columns) ?
                        String.format(locale, "(%d,%d)", row, column) :
                        String.format(locale, "(%d)", row));

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
