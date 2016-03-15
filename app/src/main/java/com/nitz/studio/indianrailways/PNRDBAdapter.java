package com.nitz.studio.indianrailways;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nitinpoddar on 11/24/15.
 */
public class PNRDBAdapter {
    public PNRHelper helper;

    public PNRDBAdapter(Context context) {
        helper = new PNRHelper(context);
    }

    public long insertPNR(String pnr, String train_number, String train_name, String doj){

        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PNRHelper.UID, pnr);
            values.put(PNRHelper.TRAIN_NAME, train_name);
            values.put(PNRHelper.TRAIN_NUMBER, train_number);
            values.put(PNRHelper.DATE_OF_JOURNEY, doj);
            long rowid = db.insert(PNRHelper.TABLE_NAME, null, values);
            return rowid;
        } catch (SQLiteException e){
            Log.i("Error while inserting", ""+e);
            return 0;
        }
    }

    public Cursor selectAll(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlQuery = "Select * from "+PNRHelper.TABLE_NAME;
        return db.rawQuery(sqlQuery, null);
    }

    public int getPNRCount(String pnr, String column_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlQuery = "Select * from "+PNRHelper.TABLE_NAME +" WHERE " +column_name+" =?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{pnr});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int getTableCount(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlQuery = "Select * from "+PNRHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteRow(String pnr){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(PNRHelper.TABLE_NAME, PNRHelper.UID +" = ?", new String[]{pnr});
    }

    class PNRHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "db_pnr_history";
        private static final String TABLE_NAME = "tn_pnr_history";
        private static final int DATABASE_VERSION = 1;
        private static final String UID = "_id";
        private static final String TRAIN_NUMBER = "train_number";
        private static final String TRAIN_NAME = "train_name";
        private static final String DATE_OF_JOURNEY = "date_of_journey";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ( "+UID+" VARCHAR(255) PRIMARY KEY, "+
                TRAIN_NUMBER + " VARCHAR(255), " +TRAIN_NAME+ " VARCHAR(255), "+DATE_OF_JOURNEY+ " VARCHAR(255));";

        public PNRHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (SQLiteException e) {
                Log.i("Error in creating table", "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //do nothing
        }
    }
}
