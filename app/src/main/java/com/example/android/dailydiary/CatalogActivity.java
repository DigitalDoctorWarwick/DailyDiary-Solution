package com.example.android.dailydiary;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.dailydiary.data.DiaryContract.DiaryEntry;

public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DIARY_LOADER = 0;
    DiaryCursorAdapter mDiaryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open MainActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ListView diaryList = findViewById(R.id.diary_list);
        mDiaryCursorAdapter = new DiaryCursorAdapter(this, null);
        diaryList.setAdapter(mDiaryCursorAdapter);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        diaryList.setEmptyView(emptyView);

        diaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, MainActivity.class);
                Uri uri = ContentUris.withAppendedId(DiaryEntry.CONTENT_URI,id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(DIARY_LOADER,null,this);
    }

    private void insertEntry() {
        ContentValues values = new ContentValues();
        values.put(DiaryEntry.COLUMN_DIARY_NAME, "Omar Khan");
        values.put(DiaryEntry.COLUMN_DIARY_DATE, DiaryEntry.convertDateToUnixTimestamp("08/12/1989"));
        values.put(DiaryEntry.COLUMN_DIARY_MOBILITY, 3);
        values.put(DiaryEntry.COLUMN_DIARY_STIFFNESS, true);
        values.put(DiaryEntry.COLUMN_DIARY_PAIN, false);
        values.put(DiaryEntry.COLUMN_DIARY_FALLS, 2);
        values.put(DiaryEntry.COLUMN_DIARY_SUBMITTED, true);
        getContentResolver().insert(DiaryEntry.CONTENT_URI, values);
        Log.v("CatalogActivity", getString(R.string.dummy_data_added_toast));
    }

    /**
     * Helper method to delete all entries in the database.
     */
    private void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(DiaryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xmlalog.xml file.
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
                insertEntry();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String [] columns = {
                DiaryEntry._ID,
                DiaryEntry.COLUMN_DIARY_DATE,
                DiaryEntry.COLUMN_DIARY_MOBILITY,
                DiaryEntry.COLUMN_DIARY_FALLS,
                DiaryEntry.COLUMN_DIARY_SUBMITTED
        };
        String orderBy = DiaryEntry.COLUMN_DIARY_DATE + " DESC";

        return new CursorLoader(this, DiaryEntry.CONTENT_URI, columns, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mDiaryCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDiaryCursorAdapter.swapCursor(null);
    }

}
