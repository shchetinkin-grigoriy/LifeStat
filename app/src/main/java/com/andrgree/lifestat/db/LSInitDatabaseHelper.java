package com.andrgree.lifestat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.andrgree.lifestat.db.table.CheckTimestamp;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.db.table.UserStat;

/**
 * Created by grag on 12/19/15.
 *
 * Класс для инициализации, обновлния БД
 */
public class LSInitDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "life_stat.db";
    private static final int DATABASE_VERSION = 1;

    String TABLE_STAT_PARAM_CREATE = "create table "
            + StatParam.TABLE + "("
            + StatParam.COLUMN_ID + " integer primary key autoincrement, "
            + StatParam.COLUMN_NAME + " text not null, "
            + StatParam.COLUMN_CODE + " text, "
            + StatParam.COLUMN_DESC + " text, "
            + StatParam.COLUMN_DESC_CODE + " text, "
            + StatParam.COLUMN_STANDART + " integer DEFAULT 1, "
            + StatParam.COLUMN_ENABLE + " integer DEFAULT 1, "
            + StatParam.COLUMN_ACTIVE + " integer DEFAULT 1, "
            + StatParam.COLUMN_CREATE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP );";

    String TABLE_STAT_PARAM_INIT_LOAD =
              "insert into " + StatParam.TABLE + " values "
                      + "(1, 'MOOD', 'stat_param_mood', null, 'stat_param_mood_desc', 1, 1, 1, CURRENT_TIMESTAMP), "
                      + "(2, 'LOVE', 'stat_param_love', null, 'stat_param_love_desc', 1, 1, 1, CURRENT_TIMESTAMP), "
                      + "(3, 'BRAIN', 'stat_param_brain', null, 'stat_param_brain_desc', 1, 1, 1, CURRENT_TIMESTAMP), "
                      + "(4, 'HEALTH', 'stat_param_health', null, 'stat_param_health_desc', 1, 1, 1, CURRENT_TIMESTAMP);";

    String TABLE_USER_STAT_CREATE = "create table "
            + UserStat.TABLE + "("
            + UserStat.COLUMN_ID + " integer primary key autoincrement, "
            + UserStat.COLUMN_PARAM_ID + " integer not null, "
            + UserStat.COLUMN_MARK + " integer not null, "
            + UserStat.COLUMN_DESC + " text, "
            + UserStat.COLUMN_CREATE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + " FOREIGN KEY(" + UserStat.COLUMN_PARAM_ID  + ") REFERENCES " + StatParam.TABLE + "(" + StatParam.COLUMN_ID + "));";

    String TABLE_CHECK_TIMESTAMP_CREATE = "create table "
            + CheckTimestamp.TABLE + "("
            + CheckTimestamp.COLUMN_ID + " integer primary key autoincrement, "
            + CheckTimestamp.COLUMN_TS + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    public LSInitDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_STAT_PARAM_CREATE);
        database.execSQL(TABLE_USER_STAT_CREATE);
        database.execSQL(TABLE_CHECK_TIMESTAMP_CREATE);
        database.execSQL(TABLE_STAT_PARAM_INIT_LOAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(LSInitDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        /*database.execSQL("DROP TABLE IF EXISTS " + TABLE_STAT_PARAM_CREATE);
        onCreate(database);*/
    }


}
