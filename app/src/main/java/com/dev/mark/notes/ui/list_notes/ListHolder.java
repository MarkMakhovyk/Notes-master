package com.dev.mark.notes.ui.list_notes;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.mark.notes.R;
import com.dev.mark.notes.domain.model.Note;
import com.dev.mark.notes.ui.utils.LoadImageTask;

import java.io.File;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListHolder extends RecyclerView.ViewHolder {
    public Note note;

    @BindView(R.id.title_note)
    public TextView titleNote;

    @BindView(R.id.text_note)
    public TextView textNote;

    @BindView(R.id.data_reminder)
    public TextView dateReminderTv;

    @BindView(R.id.date_made)
    public TextView dateMadeTv;

    @BindView(R.id.photo_note)
    public ImageView photoView;

    @BindView(R.id.layout_reminder)
    public LinearLayout reminderLayout;

    @BindView(R.id.checkbox)
    public CheckBox checkBox;



    public ListHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        photoView.setVisibility(View.GONE);
    }

    void bind(Note note, File photoFile) {
        this.note = note;
        String dateFormat = "dd MMM HH:mm";
        String dateString = DateFormat.format(dateFormat, note.getDateMake()).toString();
        dateMadeTv.setText(dateString);
        titleNote.setText(note.getTitle());
        textNote.setText(note.getTextNote());

        if (note.getDateReminder() == null) {
            reminderLayout.setVisibility(View.GONE);
        } else {
            reminderLayout.setVisibility(View.VISIBLE);

            dateString = DateFormat.format(dateFormat,note.getDateReminder()).toString();
            dateReminderTv.setText(dateString);
        }
        showPhoto(photoFile);

    }

    private void showPhoto(File photoFile) {
        photoView.setVisibility(photoFile.exists() ? View.VISIBLE : View.GONE);
        if (!photoFile.exists())
            return;

        LoadImageTask loadImageTask = new LoadImageTask(photoFile.getPath());
        loadImageTask.execute();
        try {
            Bitmap bitmap = loadImageTask.get();
            photoView.setImageBitmap(bitmap);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
