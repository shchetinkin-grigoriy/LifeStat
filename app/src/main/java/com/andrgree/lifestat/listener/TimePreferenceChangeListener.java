package com.andrgree.lifestat.listener;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.andrgree.lifestat.fragment.MainSettingFragment;
import com.andrgree.lifestat.preference.TimePreference;
import com.andrgree.lifestat.reciever.CheckTimeReciever;
import com.andrgree.lifestat.scheduler.CheckTimeJobService;

import java.util.Calendar;
import java.util.List;


/**
 * Created by grag on 11/28/15.
 */
public class TimePreferenceChangeListener implements Preference.OnPreferenceChangeListener {

    private PreferenceFragment parent;
    public TimePreferenceChangeListener(PreferenceFragment fragment) {
        parent = fragment;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        AlarmManager alarm = (AlarmManager) parent.getActivity().getSystemService(Context.ALARM_SERVICE);
        List<TimePreference> timePreferenceList = ((MainSettingFragment) parent).getTimePreferenceList();
        TimePreference timePreference = null;

        Intent intent = new Intent(parent.getActivity(), CheckTimeReciever.class);
        PendingIntent alarmIntent = null;

        //Перезапуск всех alarm'ов
        Calendar calendar = null;
        for (int i = 0; i < timePreferenceList.size(); i++) {
            timePreference = timePreferenceList.get(i);
            timePreference = timePreference.getKey().equals(preference.getKey()) ? (TimePreference)preference : timePreference;
            String[] time = timePreference.getSummary().toString().split(":");
            if (timePreference.isEnabled() && time != null && time.length == 2) {
                alarmIntent = PendingIntent.getBroadcast(parent.getActivity(), i, intent, 0);
                alarm.cancel(alarmIntent);
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(time[1].split(" ")[0]));

                //alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
                /*Calendar cal = Calendar.getInstance();
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, alarmIntent);*/
            }

            /*
            ComponentName mServiceComponent = new ComponentName(parent.getActivity(), CheckTimeJobService.class);;
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if(currentapiVersion < 5) {

            } else {
                JobInfo.Builder builder = new JobInfo.Builder(i,mServiceComponent);
                builder.setMinimumLatency(Long.valueOf(delay) * 1000);
                builder.setOverrideDeadline(Long.valueOf(deadline) * 1000);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

                builder.setRequiresDeviceIdle(true);
                builder.setRequiresCharging(true);
                JobScheduler jobScheduler = (JobScheduler) parent.getActivity().getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);

                jobScheduler.schedule(builder.build());
            }*/
        }

        return true;
    }

}
