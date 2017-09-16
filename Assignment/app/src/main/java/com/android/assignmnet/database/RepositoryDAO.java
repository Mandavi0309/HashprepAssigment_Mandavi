package com.android.assignmnet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.assignmnet.model.RepoDbModel;


public class RepositoryDAO {
    private final String TAG = RepositoryDAO.class.getSimpleName();
    private DbHandler dbHandler;
    private SQLiteDatabase database;
    private Context mContext;


    public static String[] REPO_DATA_TABLE_COLUMNS = {
            DbHandler.KEY_REPO_ID,
            DbHandler.KEY_DATA
    };


    public RepositoryDAO(Context mContext) {
        this.mContext = mContext;
        open();
    }

    public void open() throws SQLException {
        if (dbHandler == null) {
            dbHandler = DbHandler.getInstance(mContext);
        }
    }

    public void close() {
        dbHandler.close();
    }


    /**
     * Method to save repo data in db
     *
     * @param data
     * @return
     */
    public long saveRepoData(String data) {
        database = dbHandler.getWritableDatabase();
        long rowId = 0;
        try {

            Log.d(TAG, "Repo data " + data);
            ContentValues values = new ContentValues();
            values.put(DbHandler.KEY_DATA, data);

            // Inserting Row
            rowId = database.insert(DbHandler.REPO_DATA_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return rowId;
    }

    /**
     * Method to delete Repo Data
     *
     * @return
     */
    public void deleteRepoData() {
        Log.d(TAG, "deleting Repo data");
        database = dbHandler.getWritableDatabase();
        database.delete(DbHandler.REPO_DATA_TABLE, null, null);

    }

    /**
     * Method to get  Repo Data
     *
     * @return RepoDbModel
     */
    public RepoDbModel getRepoData() {
        Cursor cursor = null;
        RepoDbModel objRepoDbModel = null;
        database = dbHandler.getReadableDatabase();
        try {
            cursor = database.query(DbHandler.REPO_DATA_TABLE,
                    REPO_DATA_TABLE_COLUMNS, null, null, null, null,
                    null);


            if (cursor.moveToFirst()) {

                do {
                    int id = cursor.getInt(cursor.getColumnIndex(DbHandler.KEY_REPO_ID));

                    String data = cursor.getString(cursor.getColumnIndex(DbHandler.KEY_DATA));

                    objRepoDbModel = new RepoDbModel(id, data);

                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return objRepoDbModel;
    }

    /**
     * Method to repo data  based on id
     *
     * @param objLoginDbModel
     */
    public void updateRepoData(RepoDbModel objLoginDbModel) {
        database = dbHandler.getWritableDatabase();
        if (objLoginDbModel != null) {
            ContentValues cv = new ContentValues();
            cv.put(DbHandler.KEY_DATA, objLoginDbModel.getData());

            database.update(DbHandler.REPO_DATA_TABLE, cv, DbHandler.KEY_REPO_ID + " = ?", new String[]{objLoginDbModel.getId() + ""});


        }
    }
}
