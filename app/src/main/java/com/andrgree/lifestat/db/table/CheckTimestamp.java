package com.andrgree.lifestat.db.table;

import android.database.Cursor;

import java.util.Calendar;

/**
 * Created by grag on 12/19/15.
 *
 * Класс для хранения временных отметок ввода данных для пользователя
 */
public class CheckTimestamp {

    public static final String TABLE = "check_timestamp";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TS = "create_date";

    private long id;
    private Calendar ts;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static CheckTimestamp fromCursor(Cursor cursor) {
        CheckTimestamp ts = new CheckTimestamp();
        ts.setId(cursor.getInt(0));
        //statParam.setCreateDate(cursor.getString()Int(2));

        return ts;
    }
}