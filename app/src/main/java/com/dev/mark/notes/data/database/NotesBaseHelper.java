package com.dev.mark.notes.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.dev.mark.notes.data.database.NotesDbSchema.NotesTable.NAME;

public class NotesBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weatherBase.db";

    public NotesBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NAME + "(" +
                " _id integer primary key autoincrement, " +
                NotesDbSchema.NotesTable.Cols.UUID + ", " +
                NotesDbSchema.NotesTable.Cols.TITLE + ", " +
                NotesDbSchema.NotesTable.Cols.TEXT_NOTE + ", " +
                NotesDbSchema.NotesTable.Cols.DATE_MAKE + ", " +
                NotesDbSchema.NotesTable.Cols.DATE_REMINDER + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
