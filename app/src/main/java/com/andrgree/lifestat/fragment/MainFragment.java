package com.andrgree.lifestat.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrgree.lifestat.MainActivity;
import com.andrgree.lifestat.R;
import com.andrgree.lifestat.adapter.UserParamCursorAdapter;
import com.andrgree.lifestat.db.DataBaseContentProvider;
import com.andrgree.lifestat.db.table.CheckTimestamp;
import com.andrgree.lifestat.db.table.UserStat;
import com.andrgree.lifestat.listener.UserParamListListener;
import com.andrgree.lifestat.rest.WeatherInfo;
import com.andrgree.lifestat.rest.WeatherInterface;
import com.andrgree.lifestat.rest.WeatherShowInfo;
import com.andrgree.lifestat.view.ContextMenuRecyclerView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.Calendar;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by grag on 12/1/15.
 *
 * Информацию о дне (описание лунного дня, зодиакального дня и т.д.)
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private ContextMenuRecyclerView userParamsRView;
    private UserParamCursorAdapter userParamsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private MainDataListener mainDataListener;

    private TextView pressureValue;
    private TextView temperatureValue;
    private TextView windValue;
    private TextView moonDayValue;

    public interface MainDataListener {
        void showInputDialog(int position, int count, boolean isNew);
        void updateWeatherShowData();
    }

    /**
     * инициализаци userParamsRView
     */
    public void initUserParamsRView(View view) {
        layoutManager = new LinearLayoutManager(getContext());
        userParamsRView = (ContextMenuRecyclerView) view.findViewById(R.id.userParamList);

        userParamsRView.setHasFixedSize(true);
        userParamsRView.setLayoutManager(layoutManager);
        getLoaderManager().initLoader(0, null, this);

        userParamsAdapter = new UserParamCursorAdapter(getContext(), null);
        userParamsRView.setAdapter(userParamsAdapter);

        userParamsRView.addOnItemTouchListener(new UserParamListListener(getContext(), new UserParamListListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int count) {
                mainDataListener.showInputDialog(position, count, false);
                //Snackbar.make(userParamsRView, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }));

        registerForContextMenu(userParamsRView);
    }

    /**
     * инициализаци userParamsRView
     */
    public void initWeatherData(View view) {
        pressureValue = (TextView) view.findViewById(R.id.pressureValue);
        temperatureValue = (TextView) view.findViewById(R.id.temperatureValue);
        windValue = (TextView) view.findViewById(R.id.windValue);
        moonDayValue = (TextView) view.findViewById(R.id.moonDayValue);

        mainDataListener.updateWeatherShowData();
    }


    /*
     * Обновление поараметров погодных условий
     */
    public void updateWeatherShowData(WeatherShowInfo weatherShowInfo) {
        if(weatherShowInfo != null) {
            pressureValue.setText(weatherShowInfo.getPressure());
            temperatureValue.setText(weatherShowInfo.getTemperature());
            windValue.setText(weatherShowInfo.getWind());
            moonDayValue.setText(weatherShowInfo.getMoodDay());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainDataListener = (MainDataListener) context;
        //Activity activity = context instanceof Activity ? (Activity) context : null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initUserParamsRView(view);
        initWeatherData(view);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Calendar prefTimeCalendar = getNotificationCalendar();

        String[] selectionArgs = new String[]{(new Long(prefTimeCalendar.getTime().getTime() / 1000)).toString(), (new Long(prefTimeCalendar.getTime().getTime() / 1000)).toString()};

        String[] projection = {UserStat.COLUMN_ID, UserStat.COLUMN_PARAM_ID, UserStat.COLUMN_MARK, UserStat.COLUMN_DESC, UserStat.COLUMN_CREATE_DATE};
        CursorLoader cursorLoader = new CursorLoader(this.getContext(), DataBaseContentProvider.USER_STAT_URI, projection, "us.create_date", selectionArgs, null);
        return cursorLoader;
    }

    private Calendar getNotificationCalendar() {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar prefTimeCalendar = Calendar.getInstance();
        Calendar prefTimeCalendar1 = Calendar.getInstance();
        Calendar prefTimeCalendar2 = Calendar.getInstance();
        Calendar prefTimeCalendar3 = Calendar.getInstance();
        Calendar prefTimeCalendar4 = Calendar.getInstance();
        Calendar prefTimeCalendar5 = Calendar.getInstance();
        NavigableSet<Calendar> prefTimeCalendars = new TreeSet<>();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        Long settingTime = settings.getLong("pref_time_1", 0L);
        prefTimeCalendar1.setTimeInMillis(settingTime);
        prefTimeCalendar1.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
        prefTimeCalendar1.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
        prefTimeCalendar1.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
        prefTimeCalendars.add(prefTimeCalendar1);

        settingTime = settings.getLong("pref_time_2", 0L);
        prefTimeCalendar2.setTimeInMillis(settingTime);
        prefTimeCalendar2.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
        prefTimeCalendar2.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
        prefTimeCalendar2.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
        prefTimeCalendars.add(prefTimeCalendar2);

        settingTime = settings.getLong("pref_time_3", 0L);
        prefTimeCalendar3.setTimeInMillis(settingTime);
        prefTimeCalendar3.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
        prefTimeCalendar3.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
        prefTimeCalendar3.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
        prefTimeCalendars.add(prefTimeCalendar3);

        settingTime = settings.getLong("pref_time_4", 0L);
        prefTimeCalendar4.setTimeInMillis(settingTime);
        prefTimeCalendar4.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
        prefTimeCalendar4.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
        prefTimeCalendar4.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
        prefTimeCalendars.add(prefTimeCalendar4);

        settingTime = settings.getLong("pref_time_5", 0L);
        prefTimeCalendar5.setTimeInMillis(settingTime);
        prefTimeCalendar5.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
        prefTimeCalendar5.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
        prefTimeCalendar5.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
        prefTimeCalendars.add(prefTimeCalendar5);

        Iterator<Calendar> iterator = prefTimeCalendars.descendingIterator();
        while (iterator.hasNext()) {
            prefTimeCalendar = iterator.next();
            if (prefTimeCalendar.compareTo(currentCalendar) < 0) {
                return prefTimeCalendar;
            }
        }
        currentCalendar.set(Calendar.HOUR, 0);
        currentCalendar.set(Calendar.MINUTE, 0);
        return currentCalendar;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        userParamsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        userParamsAdapter.swapCursor(null);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context_user_stat, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerViewContextMenuInfo info = (ContextMenuRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_menu_new:
                //editNote(info.id);
                ((MainActivity) getActivity()).showInputDialog(info.position, userParamsRView.getChildCount(), true);
                return true;
            case R.id.action_menu_update:
                ((MainActivity) getActivity()).showInputDialog(info.position, userParamsRView.getChildCount(), false);
                //deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {


        }
    }
}