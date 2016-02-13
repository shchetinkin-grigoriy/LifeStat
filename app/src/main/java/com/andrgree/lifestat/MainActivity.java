package com.andrgree.lifestat;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import com.andrgree.lifestat.adapter.UserParamCursorAdapter;
import com.andrgree.lifestat.db.DataBaseContentProvider;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.db.table.UserStat;
import com.andrgree.lifestat.fragment.InputDialogFragment;

public class MainActivity extends AppCompatActivity implements InputDialogFragment.InputDialogFragmentListener, NavigationView.OnNavigationItemSelectedListener  {

    private boolean mDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_drawer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
}

