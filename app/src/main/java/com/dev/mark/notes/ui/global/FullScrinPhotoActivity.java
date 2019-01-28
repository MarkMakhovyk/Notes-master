package com.dev.mark.notes.ui.global;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.dev.mark.notes.R;
import com.dev.mark.notes.ui.utils.LoadImageTask;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class FullScrinPhotoActivity extends AppCompatActivity {

    private static final String EXTRA_FILE = "file";

    private File file;
    private ActionBar actionBar;
    ImageView photoImageView;

    public static Intent newIntent(Context packageContext, File file) {
        Intent intent = new Intent(packageContext, FullScrinPhotoActivity.class);
        intent.putExtra(EXTRA_FILE, file);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details_photo);
        file = (File) getIntent().getSerializableExtra(EXTRA_FILE);

        photoImageView = (ImageView) findViewById(R.id.photo_details);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.photo_details);

        setImage();
    }


    private void setImage() {
        LoadImageTask loadImageTask = new LoadImageTask(file.getPath());
        loadImageTask.execute();
        try {
            Bitmap bitmap = loadImageTask.get();
            photoImageView.setImageBitmap(bitmap);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
