
package com.demo.simon.db;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataAdapter
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DataAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DataAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        } catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    // public List<String> getA

    public Cursor getAllCities()
    {
        try
        {
            String sql = "SELECT sName, sCode FROM tCity ORDER BY sName";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null)
            {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public String getCitiesByKeywords(String keywords) {
        List<String> results = null;
        try
        {
            String sql = "SELECT sName, sCode FROM City WHERE sName LIKE '" + keywords + "' ORDER BY sName";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null)
            {
                mCur.moveToNext();
            }
            return mCur.getString(mCur.getColumnIndex("sName"));
        } catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
}
