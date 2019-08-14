/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView petList;
    private PetCursorAdapter cursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        petList=(ListView)findViewById(R.id.pet_list);
        View emptyView = findViewById(R.id.empty_view);
        petList.setEmptyView(emptyView);

        cursorAdapter=new PetCursorAdapter(this,null);
        petList.setAdapter(cursorAdapter);

        petList.setOnItemClickListener(onItem);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(0,null,this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet() {

        ContentValues values=new ContentValues();
        values.put(PetsContract.PetsEntry.COLUMN_NAME,"ToTo");
        values.put(PetsContract.PetsEntry.COLUMN_BREED,"Terrier");
        values.put(PetsContract.PetsEntry.COLUMN_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        values.put(PetsContract.PetsEntry.COLUMN_WEIGHT,7);

        try{
            Uri insertedItemUri=getContentResolver().insert(PetsContract.PetsEntry.CONTENT_URI,values);
            if(insertedItemUri!=null)
                Toast.makeText(this,insertedItemUri.toString(),Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show();
        }
        catch (IllegalArgumentException e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, PetsContract.PetsEntry.CONTENT_URI,
                new String[]{PetsContract.PetsEntry.COLUMN_ID,
                        PetsContract.PetsEntry.COLUMN_NAME,
                        PetsContract.PetsEntry.COLUMN_BREED},
                null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    private AdapterView.OnItemClickListener onItem=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            intent.setData(ContentUris.withAppendedId(PetsContract.PetsEntry.CONTENT_URI,id));
            startActivity(intent);
        }
    };

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllPet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllPet() {
       getContentResolver().delete(PetsContract.PetsEntry.CONTENT_URI,null,null);
    }

}
