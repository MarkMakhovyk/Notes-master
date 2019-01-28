package com.dev.mark.notes.ui.global.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.dev.mark.notes.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String DIALOG_DATE = "DialogDate";
    public static final String EXTRA_DATE = "date";
    private static final String ARG_DATE = "date";
    private static final int REQUEST_DATE = 1;

    private Date date;
    private Button dateBt;
    private Button timeBt;

    public static DateDialogFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DateDialogFragment fragment = new DateDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        date = (Date) getArguments().getSerializable(ARG_DATE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        timeBt = v.findViewById(R.id.time_picker_button);
        dateBt = v.findViewById(R.id.date_picker_button);
        timeBt.setOnClickListener(this);
        dateBt.setOnClickListener(this);

        updateDate();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_fragment_title).setView(v)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeBt.setText(sdf.format(date));

        sdf = new SimpleDateFormat("dd MMM yyyy");
        dateBt.setText(sdf.format(date));
    }


    @Override
    public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        switch (v.getId()) {
            case R.id.date_picker_button:
                DatePickerDialogFragment dateDialog = DatePickerDialogFragment.newInstance(date);
                dateDialog.setTargetFragment(DateDialogFragment.this, REQUEST_DATE);
                dateDialog.show(manager, DIALOG_DATE);
                break;
            case R.id.time_picker_button:

                TimePickerDialogFragment timeDialog = TimePickerDialogFragment.newInstance(date);
                timeDialog.setTargetFragment(DateDialogFragment.this, REQUEST_DATE);
                timeDialog.show(manager, DIALOG_DATE);
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            date = (Date) data
                    .getSerializableExtra(EXTRA_DATE);

            updateDate();
        }
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
