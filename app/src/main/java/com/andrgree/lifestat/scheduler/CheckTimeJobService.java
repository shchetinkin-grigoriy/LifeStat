package com.andrgree.lifestat.scheduler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by grag on 2/9/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@Deprecated
public class CheckTimeJobService extends JobService {
    private static final String TAG = "SyncService";


    @Override
    public boolean onStartJob(JobParameters params) {
        // We don't do any real 'work' in this sample app. All we'll
        // do is track which jobs have landed on our service, and
        // update the UI accordingly.
        Log.i(TAG, "on start job: " + params.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "on stop job: " + params.getJobId());
        return true;
    }

    Activity mActivity;
    private final LinkedList<JobParameters> jobParamsMap = new LinkedList<JobParameters>();

    public void setUiCallback(Activity activity) {
        mActivity = activity;
    }

}