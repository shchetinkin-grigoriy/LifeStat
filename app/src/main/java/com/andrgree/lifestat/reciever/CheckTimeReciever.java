package com.andrgree.lifestat.reciever;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import com.andrgree.lifestat.MainActivity;
import com.andrgree.lifestat.R;
import com.andrgree.lifestat.db.DataBaseContentProvider;
import com.andrgree.lifestat.db.table.CheckTimestamp;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.db.table.UserStat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by grag on 2/9/16.
 */
public class CheckTimeReciever extends BroadcastReceiver {

    final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private String getStringResourceByName(Context context, String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
        } else {
            //Запись в базу
            ContentValues values = new ContentValues();
            values.put(CheckTimestamp.COLUMN_TS, dateFormat.format(new Date()));
            context.getContentResolver().insert(DataBaseContentProvider.CHECK_TIMESTAMP_URI, values);

            //Отправка уведомления
            //SharedPreferences pref = context.getSharedPreferences("preference", Context.MODE_PRIVATE);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if(pref.getBoolean("pref_notify_user", false)) {
                Intent intentNotify = new Intent(context, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intentNotify, 0);

                Notification notify = new Notification.Builder(context)
                        .setContentTitle(getStringResourceByName(context,"notify_input_title"))
                        .setContentText(getStringResourceByName(context, "notify_input_desc"))
                         .setSmallIcon(R.drawable.side_nav_bar)
                        .setContentIntent(pIntent)
                        //.addAction(R.drawable.icon, "Call", pIntent)
                        //.addAction(R.drawable.icon, "More", pIntent)
                        //.addAction(R.drawable.icon, "And more", pIntent)
                        .build();

                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notify);
            }
        }
    }
}
