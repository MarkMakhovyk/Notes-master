package com.dev.mark.notes.domain.model;

import java.util.Date;
import java.util.UUID;

public class Note {
    private static String fileExtension = ".jpg";
    private static String fileFormat = "IMG_";
    private UUID id ;
    private String title = "";
    private String textNote = "";
    private Date dateMake;
    private Date dateReminder = null;

    public Note() {
        this(UUID.randomUUID());
    }

    public Note(UUID id) {
        this.id = id;
        dateMake = new Date();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
    }

    public Date getDateMake() {
        return dateMake;
    }

    public void setDateMake(Date dateMake) {
        this.dateMake = dateMake;
    }

    public Date getDateReminder() {
        return dateReminder;
    }

    public void setDateReminder(Date dateReminder) {
        this.dateReminder = dateReminder;
    }

    public String getPhotoFilename() {
        return fileFormat + getId().toString() + fileExtension;
    }
}
