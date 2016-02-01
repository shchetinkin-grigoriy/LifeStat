package com.andrgree.lifestat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.andrgree.lifestat.fragment.MainSettingFragment;

/**
 * Основные настройки, сохранямые в приложении
 */
public class MainSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

        getFragmentManager().beginTransaction().replace(R.id.mainSettings, new MainSettingFragment()).commit();

    }

}

