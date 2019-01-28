package com.dev.mark.notes.data.database;

public class NotesDbSchema {
    public static final class NotesTable {
        public static final String NAME = "note";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String TEXT_NOTE = "text_note";
            public static final String DATE_MAKE = "date_make";
            public static final String DATE_REMINDER = "date_reminder";
        }
    }
}
