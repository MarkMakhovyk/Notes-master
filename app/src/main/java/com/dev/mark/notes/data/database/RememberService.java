package com.dev.mark.notes.data.database;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationManagerCompat;

import com.dev.mark.notes.R;
import com.dev.mark.notes.domain.model.Note;
import com.dev.mark.notes.ui.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RememberService extends BroadcastReceiver {
    private static final String EXTRA_UUID = "uuid";

    public static Intent newIntent(Context context) {
        return new Intent(context, RememberService.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        UUID uuid = (UUID) intent.getSerializableExtra(EXTRA_UUID);
        Note note = NoteDAO.get(context).getNote(uuid);
        if (note == null && note.getDateReminder() == null) {
            RememberService.setServiceWakeUp(context);
        }

        Resources resources = context.getResources();

        Intent startIntent = MainActivity.newInstance(context, uuid);

        PendingIntent pIntent = PendingIntent.getActivity(
                context, 0, startIntent, 0);
        Notification notification = new Notification.Builder(context)
                .setTicker(resources.getString(R.string.ticker_text_notification))
                .setSmallIcon(R.drawable.notepad)
                .setContentTitle(note.getTitle())
                .setContentText(note.getTextNote())
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify( (int) note.getDateMake().getTime(), notification);
    }

    public static void setService(Context context, Note note) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy  HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(note.getDateReminder());

        Intent intent = RememberService.newIntent(context);
        intent.putExtra(EXTRA_UUID, note.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                (int) note.getDateMake().getTime(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void setServiceWakeUp(Context context) {
        cancelService(context);

        List<Note> notes = NoteDAO.get(context).getNotesReminder();

        for (Note note : notes) {
            if (note.getDateReminder().after(new Date()))
                setService(context, note);
        }

    }

    private static void cancelService(Context context) {
        Intent intent = RememberService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
