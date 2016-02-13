package com.andrgree.lifestat.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.andrgree.lifestat.db.table.CheckTimestamp;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.db.table.UserStat;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by grag on 12/19/15.
 *
 * Класс для работы с БД(параметры пользователя)
 */
public class DataBaseContentProvider extends ContentProvider {

    // database
    private LSInitDatabaseHelper database;

    // used for the UriMacher
    private static final int ALL_STAT_RARAMS = 10;
    private static final int STAT_RARAMS =20;
    private static final int STAT_RARAM_ID = 30;
    private static final int USER_STATS =40;
    private static final int USER_STAT_ID = 50;
    private static final int CHECK_TIMESTAMPS =60;
    private static final int CHECK_TIMESTAMP_ID = 70;
    private static final String AUTHORITY = "com.andrgree.lifestat.db";

    private static final String STAT_PARAM_PATH = "stat-param";
    private static final String USER_STAT_PATH = "user-stat";
    private static final String CHECK_TIMESTAMP_PATH = "check-timestamp";
    public static final Uri STAT_PARAM_URI = Uri.parse("content://" + AUTHORITY + "/" + STAT_PARAM_PATH);
    public static final Uri USER_STAT_URI = Uri.parse("content://" + AUTHORITY + "/" + USER_STAT_PATH);
    public static final Uri CHECK_TIMESTAMP_URI = Uri.parse("content://" + AUTHORITY + "/" + CHECK_TIMESTAMP_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, STAT_PARAM_PATH + "/all", ALL_STAT_RARAMS);
        sURIMatcher.addURI(AUTHORITY, STAT_PARAM_PATH, STAT_RARAMS);
        sURIMatcher.addURI(AUTHORITY, STAT_PARAM_PATH + "/#", STAT_RARAM_ID);

        sURIMatcher.addURI(AUTHORITY, USER_STAT_PATH, USER_STATS);
        sURIMatcher.addURI(AUTHORITY, USER_STAT_PATH + "/#", USER_STAT_ID);

        sURIMatcher.addURI(AUTHORITY, CHECK_TIMESTAMP_PATH, CHECK_TIMESTAMPS);//TODO
        sURIMatcher.addURI(AUTHORITY, CHECK_TIMESTAMP_PATH + "/#", CHECK_TIMESTAMP_ID);
    }

    @Override
    public boolean onCreate() {
        database = new LSInitDatabaseHelper(getContext());
        return false;
    }


    private Cursor statParamCursor(int uriType, String columnId, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //checkColumns(projection);

        switch (uriType) {
            case ALL_STAT_RARAMS:
                queryBuilder.setTables(StatParam.TABLE);
                break;
            case STAT_RARAMS:
                queryBuilder.setTables(StatParam.TABLE);
                queryBuilder.appendWhere(StatParam.COLUMN_ACTIVE + "= 1");
                break;
            case STAT_RARAM_ID:
                queryBuilder.setTables(StatParam.TABLE);
                queryBuilder.appendWhere(StatParam.COLUMN_ID + "=" + columnId);
                break;
        }

        SQLiteDatabase db = database.getWritableDatabase();

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor userStatCursor(int uriType, String[] selectionArgs) {
        StringBuilder sql = new StringBuilder();
        //"select us.id, sp.id, sp.code, us.mark, us.create_date from stat_param sp left join user_stat us on sp.id=us.param_id where strftime('%s', us.create_date) > 0 or strftime('%s', us.create_date) is null"
        sql.append("select us.id id, us.param_id param_id, us.name name, us.code code, us.mark mark, us.desc desc, us.create_date create_date ");
        sql.append("  from (");
        sql.append("    select us.id id, sp.id param_id, sp.name name, sp.code code, us.mark mark, us.desc desc, us.create_date create_date ");
        sql.append("      from stat_param sp ");
        sql.append("      join user_stat us on sp.id=us.param_id ");
        sql.append("     where (CAST(strftime('%s', us.create_date) AS INTEGER) >= ?) and sp.active = 1 ");
        sql.append("    union ");
        sql.append("    select null, sp.id, sp.name, sp.code, null, null, null ");
        sql.append("      from stat_param sp ");
        sql.append("     where sp.active = 1 and sp.id not in ( ");
        sql.append("       select sp.id ");
        sql.append("         from stat_param sp ");
        sql.append("         left join user_stat us on sp.id = us.param_id ");
        sql.append("        where(CAST(strftime('%s', us.create_date)AS INTEGER) >= ?)and sp.active = 1 ");
        sql.append("       ) ");
        sql.append("  ) us ");
        sql.append(" order by us.param_id ");

        /*sql.append("select us.id, sp.id, sp.name, sp.code, us.mark, us.desc, us.create_date ");
        sql.append("  from stat_param sp ");
        sql.append("  left join user_stat us on sp.id=us.param_id ");
        sql.append(" where (strftime('%s', us.create_date) > ? or strftime('%s', us.create_date) is null)");
        sql.append("   and sp.active = 1");*/

        //checkColumns(projection);

        switch (uriType) {
            case USER_STATS:

                break;
            case USER_STAT_ID:
                break;
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql.toString(), selectionArgs);//queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        Cursor cursor;
        if (uriType == ALL_STAT_RARAMS || uriType == STAT_RARAMS || uriType == STAT_RARAM_ID) {
            cursor = statParamCursor(uriType, uri.getLastPathSegment(), projection, selection, selectionArgs, sortOrder);
        } else if (uriType == USER_STATS || uriType == USER_STAT_ID) {
            cursor = userStatCursor(uriType, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case STAT_RARAMS:
                id = sqlDB.insert(StatParam.TABLE, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                getContext().getContentResolver().notifyChange(USER_STAT_URI, null);
                break;
            case USER_STATS:
                id = sqlDB.insert(UserStat.TABLE, null, values);
                getContext().getContentResolver().notifyChange(STAT_PARAM_URI, null);
                getContext().getContentResolver().notifyChange(USER_STAT_URI, null);
                break;
            case CHECK_TIMESTAMPS:
                id = sqlDB.insert(CheckTimestamp.TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return Uri.parse(STAT_PARAM_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case STAT_RARAMS:
                rowsDeleted = sqlDB.delete(StatParam.TABLE, selection,
                        selectionArgs);
                getContext().getContentResolver().notifyChange(USER_STAT_URI, null);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case STAT_RARAM_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(StatParam.TABLE, StatParam.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(StatParam.TABLE, StatParam.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                getContext().getContentResolver().notifyChange(USER_STAT_URI, null);
                getContext().getContentResolver().notifyChange(uri, null);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        String id = uri.getLastPathSegment();
        switch (uriType) {
            case STAT_RARAM_ID:
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(StatParam.TABLE, values, StatParam.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(StatParam.TABLE, values, StatParam.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                getContext().getContentResolver().notifyChange(USER_STAT_URI, null);

                break;
            case USER_STAT_ID:
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(UserStat.TABLE, values, UserStat.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(UserStat.TABLE, values, UserStat.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                getContext().getContentResolver().notifyChange(STAT_PARAM_URI, null);
                getContext().getContentResolver().notifyChange(USER_STAT_URI, null);

                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { StatParam.COLUMN_ID, StatParam.COLUMN_NAME, StatParam.COLUMN_DESC, StatParam.COLUMN_CODE, StatParam.COLUMN_DESC_CODE, StatParam.COLUMN_STANDART, StatParam.COLUMN_ENABLE, StatParam.COLUMN_ACTIVE, StatParam.COLUMN_CREATE_DATE };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
