package com.dev.mark.notes.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyImageFromGallery extends AsyncTask<Void, Void, Void> {
    private Intent intent;
    private Context context;
    private File photoFile;

    public CopyImageFromGallery(Intent intent, Context context, File photoFile) {
        this.context = context;
        this.intent = intent;
        this.photoFile = photoFile;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Bitmap bitmap = null;
        Uri selectedImage = intent.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photoFile.exists()) photoFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
