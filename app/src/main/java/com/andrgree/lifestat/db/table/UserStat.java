package com.andrgree.lifestat.db.table;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;

/**
 * Created by grag on 12/19/15.
 *
 * Класс для хранения статистики
 */
public class UserStat {

    public static final String TABLE = "user_stat";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PARAM_ID = "param_id";
    public static final String COLUMN_MARK = "mark";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_CREATE_DATE = "create_date";

    private long id;
    private int paramId;
    private int mark;
    private Calendar createDate;
    private String desc;

    private String paramCode;
    private String paramName;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static UserStat fromCursor(Cursor cursor) {
        //us.id, sp.id, sp.name, sp.code, us.mark, us.create_date
        UserStat statParam = new UserStat();
        statParam.setId(cursor.getInt(0));
        statParam.setParamId(cursor.getInt(1));
        statParam.setParamName(cursor.getString(2));
        statParam.setParamCode(cursor.getString(3));
        statParam.setMark(cursor.getInt(4));
        statParam.setDesc(cursor.getString(5));
        //statParam.setCreateDate(cursor.getString()Int(2));

        return statParam;
    }

    public int getParamId() {
        return paramId;
    }

    public void setParamId(int paramId) {
        this.paramId = paramId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }


    public String getTitle(Context context) {
        if(paramCode != null) {
            String packageName = context.getPackageName();
            Integer resId = context.getResources().getIdentifier(paramCode, "string", packageName);
            if(resId != null && resId > 0) {
                return context.getResources().getString(resId);
            }
        }

        return paramName;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}