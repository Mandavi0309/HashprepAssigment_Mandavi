package com.android.assignmnet.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHandler extends SQLiteOpenHelper {
    public static final String TAG = DbHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DataManager";

    //Table
    public static final String REPO_DATA_TABLE = "repo_table";


    /**
     * Login table column names
     */
    public static final String KEY_REPO_ID = "id";
    public static final String KEY_DATA = "repo_data";


    /**
     * schema for Login data
     */
    public static String CREATE_REPO_DATA_TABLE = "CREATE TABLE IF NOT EXISTS " + REPO_DATA_TABLE + "("
            + KEY_REPO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DATA + " TEXT )";


    private static DbHandler mInstance = null;
    private Context mContext;

    public static DbHandler getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DbHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }


    private DbHandler(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = ctx;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_REPO_DATA_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + REPO_DATA_TABLE);

        onCreate(db);
    }
}
