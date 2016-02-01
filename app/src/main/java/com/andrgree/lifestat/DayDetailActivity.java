package com.andrgree.lifestat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.andrgree.lifestat.adapter.UserParamAdapter;
import com.andrgree.lifestat.fragment.DayDetailFragment;
import com.andrgree.lifestat.listener.UserParamListListener;

public class DayDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_day_detail);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //finish();
            //return;
        }

        if (savedInstanceState == null) {
            DayDetailFragment details = new DayDetailFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }


    }

}

