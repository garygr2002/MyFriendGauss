package com.garycgregg.android.myfriendgauss3.database;

public interface ProblemDbSchema {

    interface AnswerTable {

        String name = "answer";

        interface Columns {

            /*

create table answer(
	problem_id integer not null,
	row integer not null,
	entry real not null,
        primary key(problem_id, row),
	foreign key(problem_id) references problem(problem_id)
	on delete cascade
	on update cascade);

             */

            String ENTRY = "entry";
            String PROBLEM_ID = "problem_id";
            String ROW = "row";
        }
    }

    interface MatrixTable {

        String name = "matrix";

        interface Columns {

            /*

create table matrix(
	problem_id integer not null,
	row integer not null,
	column integer not null,
	entry real not null,
        primary key(problem_id, row, column),
	foreign key(problem_id) references problem(problem_id)
	on delete cascade
	on update cascade);

             */

            String COLUMN = "column";
            String ENTRY = "entry";
            String PROBLEM_ID = "problem_id";
            String ROW = "row";
        }
    }

    interface ProblemTable {

        String name = "problem";

        interface Columns {

            /*

create table problem(
	problem_id integer primary key autoincrement,
	name text not null,
	dimensions integer not null,
	created datetime not null,
	solved datetime,
	write_lock integer not null);

             */

            String CREATED = "created";
            String DIMENSIONS = "dimensions";
            String NAME = "name";
            String PROBLEM_ID = "problem_id";
            String SOLVED = "solved";
            String WRITE_LOCK = "write_lock";
        }
    }

    interface VectorTable {

        String name = "vector";

        interface Columns {

            /*

create table vector(
	problem_id integer not null,
	row integer not null,
	entry real not null,
        primary key(problem_id, row),
	foreign key(problem_id) references problem(problem_id)
	on delete cascade
	on update cascade);

             */

            String ENTRY = "entry";
            String PROBLEM_ID = "problem_id";
            String ROW = "row";
        }
    }
}
