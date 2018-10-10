package com.example.android.dailydiary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.dailydiary.data.DiaryContract.DiaryEntry;

public class DiaryCursorAdapter extends CursorAdapter {

    public DiaryCursorAdapter (Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dateTextView = view.findViewById(R.id.list_date);
        TextView submittedTextView = view.findViewById(R.id.list_submitted);
        RatingBar mobilityRatingBar = view.findViewById(R.id.list_mobility);

        String date = DiaryEntry.convertUnixTimestampToDate(cursor.getLong(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_DATE)));
        boolean submitted = cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_SUBMITTED)) == 1;
        int mobility = cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_MOBILITY));

        dateTextView.setText(date);
        submittedTextView.setText(context.getString(R.string.list_submitted, submitted));
        mobilityRatingBar.setRating(mobility);
    }
}
