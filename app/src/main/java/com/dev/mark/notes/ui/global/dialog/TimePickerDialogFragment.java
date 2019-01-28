package com.dev.mark.notes.ui.global.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.dev.mark.notes.R;

import java.util.Calendar;
import java.util.Date;

public class TimePickerDialogFragment extends DialogFragment {
    private static final String ARG_DATE = "dialog_time_picker";

    private TimePicker mTimePicker;

    public static TimePickerDialogFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time_picker, null);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentMinute(new Integer(minute));
        mTimePicker.setCurrentHour(new Integer(hour));
        mTimePicker.setIs24HourView(true);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title).setView(v)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour =  mTimePicker.getCurrentHour();
                                int minute = mTimePicker.getCurrentMinute();

                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                sendResult(Activity.RESULT_OK, calendar.getTime());
                            }
                        })
                .create();
    }


    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(DateDialogFragment.EXTRA_DATE, date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
