package com.garycgregg.android.myfriendgauss3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class ProblemDatabaseHelper extends SQLiteOpenHelper {

    // SQL for creating the Answer table
    private static final String CREATE_ANSWER_TABLE = String.format("create table %s(\n" +
                    "\t%s integer not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s real not null,\n" +
                    "        primary key(problem_id, row),\n" +
                    "\tforeign key(problem_id) references problem(problem_id)\n" +
                    "\ton delete cascade\n" +
                    "\ton update cascade)",
            ProblemDbSchema.AnswerTable.name,
            ProblemDbSchema.AnswerTable.Columns.PROBLEM_ID,
            ProblemDbSchema.AnswerTable.Columns.ROW,
            ProblemDbSchema.AnswerTable.Columns.ENTRY
    );

    // SQL for creating the Matrix table
    private static final String CREATE_MATRIX_TABLE = String.format("create table %s(\n" +
                    "\t%s integer not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s real not null,\n" +
                    "        primary key(problem_id, row, column),\n" +
                    "\tforeign key(problem_id) references problem(problem_id)\n" +
                    "\ton delete cascade\n" +
                    "\ton update cascade)",
            ProblemDbSchema.MatrixTable.name,
            ProblemDbSchema.MatrixTable.Columns.PROBLEM_ID,
            ProblemDbSchema.MatrixTable.Columns.ROW,
            ProblemDbSchema.MatrixTable.Columns.COLUMN,
            ProblemDbSchema.MatrixTable.Columns.ENTRY
    );

    // SQL for creating the Problem table
    private static final String CREATE_PROBLEM_TABLE = String.format("create table %s(\n" +
                    "\t%s integer primary key autoincrement,\n" +
                    "\t%s text not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s datetime not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s datetime,\n" +
                    "\t%s integer not null)",
            ProblemDbSchema.ProblemTable.name,
            ProblemDbSchema.ProblemTable.Columns.PROBLEM_ID,
            ProblemDbSchema.ProblemTable.Columns.NAME,
            ProblemDbSchema.ProblemTable.Columns.DIMENSIONS,
            ProblemDbSchema.ProblemTable.Columns.CREATED,
            ProblemDbSchema.ProblemTable.Columns.PRECISION,
            ProblemDbSchema.ProblemTable.Columns.SCIENTIFIC,
            ProblemDbSchema.ProblemTable.Columns.RANK,
            ProblemDbSchema.ProblemTable.Columns.SOLVED,
            ProblemDbSchema.ProblemTable.Columns.WRITE_LOCK);

    // SQL for creating the Vector table
    private static final String CREATE_VECTOR_TABLE = String.format("create table %s(\n" +
                    "\t%s integer not null,\n" +
                    "\t%s integer not null,\n" +
                    "\t%s real not null,\n" +
                    "        primary key(problem_id, row),\n" +
                    "\tforeign key(problem_id) references problem(problem_id)\n" +
                    "\ton delete cascade\n" +
                    "\ton update cascade)",
            ProblemDbSchema.VectorTable.name,
            ProblemDbSchema.VectorTable.Columns.PROBLEM_ID,
            ProblemDbSchema.VectorTable.Columns.ROW,
            ProblemDbSchema.VectorTable.Columns.ENTRY
    );

    // An array of create table commands to be submitted in sequence
    private static final String[] CREATE_COMMANDS = {
            CREATE_PROBLEM_TABLE,
            CREATE_MATRIX_TABLE,
            CREATE_ANSWER_TABLE,
            CREATE_VECTOR_TABLE
    };

    // The name of the database
    private static final String DATABASE_NAME = "gauss_problem.db";

    // The format for a drop table command
    private static final String DROP_TABLE_FORMAT = "drop table if exists %s";

    // SQL command to drop the Problem table
    private static final String DROP_PROBLEM_TABLE = String.format(DROP_TABLE_FORMAT,
            ProblemDbSchema.ProblemTable.name);

    // SQL command to drop the Matrix table
    private static final String DROP_MATRIX_TABLE = String.format(DROP_TABLE_FORMAT,
            ProblemDbSchema.MatrixTable.name);

    // SQL command to drop the Answer table
    private static final String DROP_ANSWER_TABLE = String.format(DROP_TABLE_FORMAT,
            ProblemDbSchema.AnswerTable.name);

    // SQL command to drop the Vector table
    private static final String DROP_VECTOR_TABLE = String.format(DROP_TABLE_FORMAT,
            ProblemDbSchema.VectorTable.name);

    // An array of drop table commands to be submitted in sequence
    private static final String[] DROP_COMMANDS = {
            DROP_VECTOR_TABLE,
            DROP_ANSWER_TABLE,
            DROP_MATRIX_TABLE,
            DROP_PROBLEM_TABLE
    };

    // The version of this database
    private static final int VERSION = 1;

    /**
     * Constructs a database helper object.
     *
     * @param context The context associated with the database object
     */
    public ProblemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Creates the required database tables.
     *
     * @param sqLiteDatabase The SQLite database in which to create the tables
     */
    private void createTables(SQLiteDatabase sqLiteDatabase) {
        executeCommands(sqLiteDatabase, CREATE_COMMANDS);
    }

    /**
     * Executes a sequence of database commands.
     *
     * @param sqLiteDatabase The SQLite database in which to execute the commands
     * @param commands       The sequence of commands
     */
    private void executeCommands(SQLiteDatabase sqLiteDatabase, String[] commands) {

        // Execute each command.
        for (String command : commands) {
            sqLiteDatabase.execSQL(command);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase sqLiteDatabase) {

        // We require foreign key constraints for this database.
        super.onConfigure(sqLiteDatabase);
        sqLiteDatabase.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // Upgrade the tables only if the new database version is greater than the old.
        if (oldVersion < newVersion) {

            /*
             * The new database version is greater than the old. Execute the drop commands and
             * recreate the tables.
             */
            executeCommands(sqLiteDatabase, DROP_COMMANDS);
            createTables(sqLiteDatabase);
        }
    }
}
