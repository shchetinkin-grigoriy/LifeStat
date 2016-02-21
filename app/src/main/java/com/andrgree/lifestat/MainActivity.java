package com.andrgree.lifestat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.andrgree.lifestat.adapter.UserParamCursorAdapter;
import com.andrgree.lifestat.db.DataBaseContentProvider;
import com.andrgree.lifestat.db.table.CheckTimestamp;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.db.table.UserStat;
import com.andrgree.lifestat.fragment.InputDialogFragment;
import com.andrgree.lifestat.fragment.MainFragment;
import com.andrgree.lifestat.fragment.MainSettingFragment;
import com.andrgree.lifestat.listener.TimePreferenceChangeListener;
import com.andrgree.lifestat.preference.TimePreference;
import com.andrgree.lifestat.reciever.CheckTimeReciever;
import com.andrgree.lifestat.rest.WeatherInfo;
import com.andrgree.lifestat.rest.WeatherInterface;
import com.andrgree.lifestat.rest.WeatherShowInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.Calendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements InputDialogFragment.InputDialogFragmentListener, MainFragment.MainDataListener,
        NavigationView.OnNavigationItemSelectedListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback, Callback<WeatherInfo>
    /*, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/ {
    private boolean mDualPane;
    //private GoogleApiClient mGoogleApiClient;
    public Location location;
    public MainFragment mainInfo;
    public MainSettingFragment mainSettingFragment;
    public WeatherShowInfo weatherShowInfo;
    private LocationManager locationManager;
    private WeatherInterface weatherInterface;
    private final int REQUEST_LOCATION = 10;
    private final String WEATHER_KEY = "0942f780be66ad5017d56a12c5b1005a";

    private void initLocation() {
        Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String city = pref.getString("pref_user_city", "London");

            Call<WeatherInfo> call = weatherInterface.getWeather(city, WEATHER_KEY);
            call.enqueue(this);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3 * 3600000, 0, this);
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3 * 3600000, 0, this);
                }

                location = locationManager.getLastKnownLocation(isNetworkEnabled ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER);
                if(location != null) {
                    onLocationChanged(location);
                }
            }
        }
    }

    /**
     * инициализаци данных по текущей погоде
     */
    public void initWeatherData() {
        if(weatherShowInfo == null) {
            String[] projection = {CheckTimestamp.COLUMN_ID, CheckTimestamp.COLUMN_TEMPERATURE, CheckTimestamp.COLUMN_PRESSURE, CheckTimestamp.COLUMN_MOON_DAY, CheckTimestamp.COLUMN_HUMIDITY, CheckTimestamp.COLUMN_WIND, CheckTimestamp.COLUMN_TS };
            Cursor query = getContentResolver().query(DataBaseContentProvider.CHECK_TIMESTAMP_URI, projection, null, null, null);
            if(query.getCount() > 0) {
                query.moveToFirst();
                CheckTimestamp checkTimestamp = CheckTimestamp.fromCursor(query);

                weatherShowInfo = new WeatherShowInfo(checkTimestamp);
            }
        }
    }

    /**
     * Инициализиция запуска уведомлений
     */
    private void initAlarmNotification() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); //getSharedPreferences("preference", MODE_PRIVATE);
        boolean alarmEnable = sharedPreferences.getBoolean("pref_send_to_server", false);
        int count = Integer.parseInt(sharedPreferences.getString("pref_count_notification", "0"));
        //PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        //Перезапуск всех alarm'ов
        Calendar calendar = null;
        Intent intent = new Intent(this, CheckTimeReciever.class);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(alarmEnable) {
            PendingIntent alarmIntent = null;
            for (int i = 1; i <= count; i++) {
                Long prefTime = sharedPreferences.getLong("pref_time_" + i, 0);
                if(prefTime != 0) {
                    Calendar prefTimeCal = Calendar.getInstance();
                    prefTimeCal.setTimeInMillis(prefTime);

                    alarmIntent = PendingIntent.getBroadcast(this, i, intent, 0);
                    alarm.cancel(alarmIntent);
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, prefTimeCal.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, prefTimeCal.get(Calendar.MINUTE));

                    alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
                    //alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 30000, alarmIntent);
                }

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        initWeatherData();

        setContentView(R.layout.activity_drawer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainInfo = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainInfo);//(MainFragment)findViewById(R.id.mainInfo);
        mainSettingFragment = (MainSettingFragment) getFragmentManager().findFragmentById(R.id.mainSettings);//(MainFragment)findViewById(R.id.mainInfo);
        View dayDetail = findViewById(R.id.dayDetail);
        mDualPane = dayDetail != null && dayDetail.getVisibility() == View.VISIBLE;
        Log.d(MainActivity.class.getName(), mDualPane ? "true" : "false");

        /*if(mDualPane) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.dayDetail, new DayDetailFragment());
            //ft.add(android.R.id.content, new DayDetailFragment());
            ft.commit();
        }*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //LOCATION
        locationManager = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        weatherInterface = retrofit.create(WeatherInterface.class);
        /*if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }*/

        //DEFAULT TIME PREFERENCE
        initAlarmNotification();
    }


/*    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            // permission has been granted, continue as usual


            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Toast.makeText(this, "Location: " + mLastLocation.toString(), Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("213", "76");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("213",         connectionResult.toString());

    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        //mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Меню
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Выбор меню
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_main_settings:
                intent = new Intent(this, MainSettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_user_settings:
                intent = new Intent(this, UserSettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.day_detail:
                intent = new Intent(this, DayDetailActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_input:
                showInputDialog(0, ((RecyclerView) findViewById(R.id.userParamList)).getAdapter().getItemCount(), false);
                break;
            case R.id.action_statistic:
                //return true;
                break;
            case R.id.action_common_statistic:
                //return true;
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        //Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
        View view = findViewById(R.id.mainInfo);
        Snackbar.make(view, inputText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public UserStat onLoadUserStat(int position) {
        RecyclerView userParamList = (RecyclerView) findViewById(R.id.userParamList);
        UserParamCursorAdapter.ViewHolder viewHolder = (UserParamCursorAdapter.ViewHolder) userParamList.findViewHolderForAdapterPosition(position);

        return  viewHolder.userStat;
    }

    @Override
    public int onLoadUncheckedPosition(int currentPosition) {
        RecyclerView userParamList = (RecyclerView) findViewById(R.id.userParamList);
        for (int i = 0; i < userParamList.getChildCount(); i++) {
            UserParamCursorAdapter.ViewHolder viewHolder = (UserParamCursorAdapter.ViewHolder) userParamList.findViewHolderForAdapterPosition(i);
            if(viewHolder.userStat.getId() == 0L && i != currentPosition) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void onMarkClick(UserStat userStat) {
        RecyclerView useraramList = (RecyclerView) findViewById(R.id.userParamList);

        ContentValues values = new ContentValues();
        values.put(UserStat.COLUMN_PARAM_ID, userStat.getParamId());
        values.put(UserStat.COLUMN_MARK, userStat.getMark());
        values.put(UserStat.COLUMN_DESC, userStat.getDesc());

        if(userStat.getId() != 0) {
            getContentResolver().update(ContentUris.withAppendedId(DataBaseContentProvider.USER_STAT_URI, userStat.getId()), values, null, null);
        } else {
            getContentResolver().insert(DataBaseContentProvider.USER_STAT_URI, values);
        }
        Snackbar.make(useraramList, "Position", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    /**
     * Отображение диалогового окна для ввода значений параметров
     */
    @Override
    public void showInputDialog(int position, int count, boolean isNew) {
        FragmentManager fm = getSupportFragmentManager();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        InputDialogFragment inputDialogFragment = new InputDialogFragment();
        if(position >= 0) {
            Bundle args = new Bundle();
            args.putBoolean("new", isNew);
            args.putInt("position", position);
            args.putInt("count", count);
            args.putInt("height", displaymetrics.heightPixels);
            args.putInt("width", displaymetrics.widthPixels);
            inputDialogFragment.setArguments(args);
        }
        inputDialogFragment.show(fm, "inputDialogFragment");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        onOptionsItemSelected(item);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location", location.toString());
        this.location = location;

        Call<WeatherInfo> call = weatherInterface.getWeather(location.getLatitude(), location.getLongitude(), WEATHER_KEY);
        call.enqueue(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Location", provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("Location", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("Location", provider);
    }

    @Override
    public void onResponse(Response<WeatherInfo> response, Retrofit retrofit) {
        System.out.print(response.message());
        this.weatherShowInfo = new WeatherShowInfo(response.body());
        updateWeatherShowData();
    }


    @Override
    public void updateWeatherShowData() {
        if(mainInfo == null) {
            mainInfo = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainInfo);//(MainFragment)findViewById(R.id.mainInfo);
        }
        mainInfo.updateWeatherShowData(weatherShowInfo);
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }
}

