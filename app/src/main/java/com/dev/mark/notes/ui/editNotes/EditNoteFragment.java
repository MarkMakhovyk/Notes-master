package com.dev.mark.notes.ui.editNotes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.mark.notes.R;
import com.dev.mark.notes.data.database.NoteDAO;
import com.dev.mark.notes.data.database.RememberService;
import com.dev.mark.notes.domain.model.Note;
import com.dev.mark.notes.ui.global.FullScrinPhotoActivity;
import com.dev.mark.notes.ui.global.dialog.DateDialogFragment;
import com.dev.mark.notes.ui.utils.CopyImageFromGallery;
import com.dev.mark.notes.ui.utils.LoadImageTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditNoteFragment extends Fragment {

    private static final String DIALOG_DATE = "date";
    private static final String ARG_UUID = "uuid";
    private static final int REQUEST_DATE = 1;
    private static final int REQUEST_PHOTO= 2;
    private static final int REQUEST_GALLERY = 3;

    private File photoFile;
    private Note note;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy  HH:mm");
    private String filePackage = "com.dev.mark.notes.fileprovider";

    @BindView(R.id.title_note)
    EditText titleNote;

    @BindView(R.id.text_note)
    EditText textNote;

    @BindView(R.id.date_notes)
    TextView dateNotes;

    @BindView(R.id.reminder_notes_data)
    TextView reminderDateNotes;

    @BindView(R.id.add_reminder)
    CardView addReminder;

    @BindView(R.id.make_photo)
    CardView makePhoto;

    @BindView(R.id.load_photo)
    CardView loadPhoto;

    @BindView(R.id.image_photo)
    ImageView photoImageView;

    @BindView(R.id.delete_photo)
    ImageButton deletePhoto;

    @BindView(R.id.card_view_show_photo)
    CardView showPhotoView;

    public static EditNoteFragment newInstance(Note note) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_UUID, note.getId());
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UUID uuid = (UUID) getArguments().getSerializable(ARG_UUID);
            note = NoteDAO.get(getContext()).getNote(uuid);
            getActivity().setTitle(R.string.edit_note);
        } else {
            getActivity().setTitle(R.string.add_note);
            note = new Note();
        }

        photoFile = NoteDAO.get(getActivity()).getPhotoFile(getActivity(),note);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (!saveNoteToDB())
                    Snackbar.make(getActivity().findViewById(R.id.layout_choice),
                            R.string.empty_fields, Snackbar.LENGTH_LONG).show();
                else
                    getActivity().onBackPressed();
                return true;

            case R.id.send:
                IntentBuilder intentBuilder = createIntentReport();
                startActivity(intentBuilder.createChooserIntent());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private IntentBuilder createIntentReport() {
        final IntentBuilder intentBuilder = IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(getString(R.string.title_report) + note.getTextNote() +
                        "\n" + note.getTextNote() +
                        "\n" + getString(R.string.note_made) + dateFormat.format(note.getDateMake()))
                .setChooserTitle(note.getTitle());

        if (photoFile.exists())
            intentBuilder.addStream(FileProvider.getUriForFile(getActivity(),
                    filePackage, photoFile));

        return intentBuilder;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_notes, container, false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    void initView() {
        dateNotes.setText(dateFormat.format(note.getDateMake()));

        if (note.getDateReminder() != null)
            updateDateReminder(note.getDateReminder());

        titleNote.setText(note.getTitle());
        textNote.setText(note.getTextNote());

        addListeners();
        updatePhoto();
    }

    void addListeners() {
        PackageManager packageManager = getActivity().getPackageManager();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = photoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        makePhoto.setEnabled(canTakePhoto);

        makePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        filePackage,
                        photoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        loadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
            }
        });
        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DateDialogFragment timeDialog = DateDialogFragment.newInstance(new Date());
                timeDialog.setTargetFragment(EditNoteFragment.this, REQUEST_DATE);
                timeDialog.show(manager, DIALOG_DATE);
            }
        });
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoFile.delete();
                showPhotoView.setVisibility(View.GONE);
            }
        });
        titleNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.setTextNote(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updatePhoto() {
        showPhotoView.setVisibility(photoFile.exists() ? View.VISIBLE : View.GONE);
        if (!photoFile.exists())
            return;

        LoadImageTask loadImageTask = new LoadImageTask(photoFile.getPath());
        loadImageTask.execute();
        try {
            Bitmap bitmap = loadImageTask.get();
            photoImageView.setImageBitmap(bitmap);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FullScrinPhotoActivity.newIntent(getContext(), photoFile);
                startActivity(intent);
            }
        });
    }

    private void updateDateReminder(Date date) {
        reminderDateNotes.setText(dateFormat.format(date));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DateDialogFragment.EXTRA_DATE);
            note.setDateReminder(date);
            updateDateReminder(date);
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    filePackage,
                    photoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhoto();
        } else if (requestCode == REQUEST_GALLERY) {
            try {
                new CopyImageFromGallery(data, getContext(), photoFile).execute().get();
                updatePhoto();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        boolean isSave = saveNoteToDB();
        if (!isSave && photoFile.exists())
            photoFile.delete();
    }

    private boolean saveNoteToDB() {
        if (titleNote.getText().toString().equals("") || textNote.getText().toString().equals(""))
            return false;
        if (NoteDAO.get(getContext()).getNote(note.getId()) != null)
            NoteDAO.get(getContext()).updateNote(note);
        else
            NoteDAO.get(getContext()).addNote(note);

        RememberService.setServiceWakeUp(getContext());
        return true;
    }
}
