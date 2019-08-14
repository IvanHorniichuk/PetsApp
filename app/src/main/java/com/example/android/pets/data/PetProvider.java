package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetsDBHelper mDbHelper;

    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
            sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS,PETS);
            sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS+"/#",PET_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper =new PetsDBHelper(getContext());


        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor=null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = database.query(PetsContract.PetsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PET_ID:

                selection = PetsContract.PetsEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(PetsContract.PetsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                    return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {

        String name=contentValues.getAsString(PetsContract.PetsEntry.COLUMN_NAME);
        if(name.isEmpty()||name==null)
            throw new IllegalArgumentException("Pet must have name");
        Integer gender=contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_GENDER);
        if(!PetsContract.PetsEntry.isGenderIsCorrect(gender)||gender==null)
            throw new IllegalArgumentException("Gender must be defined");
        Integer weight=contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_WEIGHT);
        if(weight<0||weight==null)
            throw new IllegalArgumentException("Weight must be greater than 0");
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id=db.insert(PetsContract.PetsEntry.TABLE_NAME,null,contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection= PetsContract.PetsEntry.COLUMN_ID+"=?";
                selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri,contentValues,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

    }
    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        if(contentValues.size()==0)
            return 0;

        if(contentValues.containsKey(PetsContract.PetsEntry.COLUMN_NAME))
        {
            String name=contentValues.getAsString(PetsContract.PetsEntry.COLUMN_NAME);
            if(name.isEmpty()||name==null)
                throw new IllegalArgumentException("Pet must have name");
        }

        if(contentValues.containsKey(PetsContract.PetsEntry.COLUMN_GENDER))
        {
            Integer gender=contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_GENDER);
            if(!PetsContract.PetsEntry.isGenderIsCorrect(gender))
                throw new IllegalArgumentException("Gender must be defined");
        }

        if(contentValues.containsKey(PetsContract.PetsEntry.COLUMN_WEIGHT))
        {
            Integer weight=contentValues.getAsInteger(PetsContract.PetsEntry.COLUMN_WEIGHT);
            if(weight<0||weight==null)
                throw new IllegalArgumentException("Weight must be greater than 0");
        }

        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri,null);
        return db.update(PetsContract.PetsEntry.TABLE_NAME,contentValues,selection,selectionArgs);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(PetsContract.PetsEntry.TABLE_NAME,selection,selectionArgs);
            case PET_ID:
                selection= PetsContract.PetsEntry.COLUMN_ID+"=?";
                selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(PetsContract.PetsEntry.TABLE_NAME,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}