package com.andrgree.lifestat.db.table;

import android.content.Context;
import android.database.Cursor;

import com.andrgree.lifestat.R;

import java.util.Calendar;

/**
 * Created by grag on 12/19/15.
 *
 * Класс для хранения типов параметров
 */
public class StatParam {

    public static final String TABLE = "stat_param";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_DESC_CODE = "desc_code";
    public static final String COLUMN_STANDART = "standart"; //стандартный параметр или введенный пользователем
    public static final String COLUMN_ENABLE = "enable"; //значение галочки
    public static final String COLUMN_ACTIVE = "active"; //видимость (удален или нет с точки зрения пользователя
    public static final String COLUMN_CREATE_DATE = "create_date";

    private int id;
    private String name;
    private String code;
    private String desc;
    private String descCode;
    private boolean standart = true;
    private boolean enable = true;
    private boolean active = true;
    private Calendar createDate = Calendar.getInstance();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getTitle(Context context) {
        if(code != null) {
            String packageName = context.getPackageName();
            Integer resId = context.getResources().getIdentifier(code, "string", packageName);
            if(resId != null && resId > 0) {
                return context.getResources().getString(resId);
            }
        }

        return name;
    }

    public String getDescription(Context context) {
        if(descCode != null) {
            String packageName = context.getPackageName();
            Integer resId = context.getResources().getIdentifier(descCode, "string", packageName);
            if(resId != null && resId > 0) {
                return context.getResources().getString(resId);
            }
        }

        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public static StatParam fromCursor(Cursor cursor) {
        StatParam statParam = new StatParam();
        statParam.setId(cursor.getInt(0));
        statParam.setName(cursor.getString(1));
        statParam.setCode(cursor.getString(2));
        statParam.setDesc(cursor.getString(3));
        statParam.setDescCode(cursor.getString(4));
        statParam.setStandart(cursor.getInt(5) == 1);
        statParam.setEnable(cursor.getInt(6) == 1);
        statParam.setActive(cursor.getInt(7) == 1);

        return statParam;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getStandart() {
        return standart;
    }

    public void setStandart(boolean standart) {
        this.standart = standart;
    }

    public String getDescCode() {
        return descCode;
    }

    public void setDescCode(String descCode) {
        this.descCode = descCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }
}
