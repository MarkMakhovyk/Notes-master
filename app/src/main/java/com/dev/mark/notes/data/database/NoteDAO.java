package com.dev.mark.notes.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dev.mark.notes.domain.model.Note;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.dev.mark.notes.data.database.NotesDbSchema.NotesTable;

public class NoteDAO {

    private static NoteDAO noteDAO;
    private Context context;
    private SQLiteDatabase mDatabase;

    public static NoteDAO get(Context context) {
        if (noteDAO == null) {
            noteDAO = new NoteDAO(context);
        }
        return noteDAO;
    }

    private NoteDAO(Context context) {
        context = context.getApplicationContext();
        mDatabase = new NotesBaseHelper(context)
                .getWritableDatabase();
    }

    public File getPhotoFile(Context context, Note note) {
        File filesDir = context.getFilesDir();
        return new File(filesDir, note.getPhotoFilename());
    }

    public void addNote(Note note) {
        ContentValues values = getContentValues(note);
        mDatabase.insert(NotesTable.NAME, null, values);
    }

    public void deleteNote(Note note, File file) {
        String uuidString = note.getId().toString();
        mDatabase.delete(NotesTable.NAME,
                NotesTable.Cols.UUID + " = ?",
                new String[] { uuidString} );
        if (file.exists())
            file.delete();
    }


    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        NotesCursorWrapper cursor = queryNotes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    public List<Note> getNotesReminder() {

        List<Note> notes = new ArrayList<>();
        NotesCursorWrapper cursor = queryNotes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursor.getNote();
                if (note.getDateReminder() != null)
                    notes.add(note);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }
    public Note getNote(UUID id) {
        NotesCursorWrapper cursor = queryNotes(
                NotesTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    public void updateNote(Note note) {
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);
        int upp = mDatabase.update(NotesTable.NAME, values,
                NotesTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }
    private static ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(NotesTable.Cols.UUID, note.getId().toString());
        values.put(NotesTable.Cols.TITLE, note.getTitle());
        values.put(NotesTable.Cols.TEXT_NOTE, note.getTextNote());
        values.put(NotesTable.Cols.DATE_MAKE, note.getDateMake().getTime());
        values.put(NotesTable.Cols.DATE_REMINDER,note.getDateReminder() == null ? 0 : note.getDateReminder().getTime());
        return values;
    }
    private NotesCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                NotesTable.NAME,
                null, // columns - с null выбираются все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new NotesCursorWrapper(cursor);
    }
}
