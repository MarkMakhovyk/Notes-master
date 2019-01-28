package com.dev.mark.notes.data.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.dev.mark.notes.domain.model.Note;

import java.util.Date;
import java.util.UUID;

public class NotesCursorWrapper extends CursorWrapper {

    public NotesCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote() {
        String uuid = getString(getColumnIndex(NotesDbSchema.NotesTable.Cols.UUID));
        String title = getString(getColumnIndex(NotesDbSchema.NotesTable.Cols.TITLE));
        String textNote = getString(getColumnIndex(NotesDbSchema.NotesTable.Cols.TEXT_NOTE));
        long dateMake = getLong(getColumnIndex(NotesDbSchema.NotesTable.Cols.DATE_MAKE));
        long dateReminder = getLong(getColumnIndex(NotesDbSchema.NotesTable.Cols.DATE_REMINDER));

        Note note = new Note();
        note.setId(UUID.fromString(uuid));
        note.setTitle(title);
        note.setTextNote(textNote);
        note.setDateMake(new Date(dateMake));
        if (dateReminder == 0) {
            note.setDateReminder(null);
        } else
            note.setDateReminder(new Date(dateReminder));
        return note;
    }
}
