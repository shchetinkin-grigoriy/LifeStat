package com.andrgree.lifestat;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.andrgree.lifestat.adapter.UserSettingPagerAdapter;

public class UserSettingActivity extends AppCompatActivity {
    UserSettingPagerAdapter userSettingPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        userSettingPagerAdapter = new UserSettingPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.userSettingPager);
        viewPager.setAdapter(userSettingPagerAdapter);

        //getFragmentManager().beginTransaction().replace(R.id.mainSettings, new MainSettingFragment()).commit();
    }

}

