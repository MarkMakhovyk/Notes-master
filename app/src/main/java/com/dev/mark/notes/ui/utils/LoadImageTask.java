package com.dev.mark.notes.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    private String path;

    public LoadImageTask(String path) {
        this.path = path;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return BitmapFactory.decodeFile(path);
    }
}
