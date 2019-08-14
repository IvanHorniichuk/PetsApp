package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class PetsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="shelter.db";
    public static final int DATABASE_VERSION=1;

    private static final String CREATE_TABLE_QUERY="CREATE TABLE "+PetsContract.PetsEntry.TABLE_NAME+
            " ("+ PetsContract.PetsEntry.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            PetsContract.PetsEntry.COLUMN_NAME +" TEXT NOT NULL, "+
            PetsContract.PetsEntry.COLUMN_BREED +" TEXT, "+
            PetsContract.PetsEntry.COLUMN_GENDER +" INTEGER NOT NULL, "+
            PetsContract.PetsEntry.COLUMN_WEIGHT +" INTEGER NOT NULL DEFAULT 0);";
    private static final String DROP_TABLE_QUERY="DROP TABLE IF EXISTS "+PetsContract.PetsEntry.TABLE_NAME;



    public PetsDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_QUERY);
        onCreate(db);

    }
}
