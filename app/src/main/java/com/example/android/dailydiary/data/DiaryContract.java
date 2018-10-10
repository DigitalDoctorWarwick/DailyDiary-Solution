package com.example.android.dailydiary.data;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DiaryContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.dailydiary";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DIARY = "diary";

    private DiaryContract() {}

    public static final class DiaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DIARY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of diary entries.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;

        public static final String TABLE_NAME = "diary";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DIARY_NAME = "name";
        public static final String COLUMN_DIARY_DATE = "date";
        public static final String COLUMN_DIARY_MOBILITY = "mobility";
        public static final String COLUMN_DIARY_STIFFNESS = "stiffness";
        public static final String COLUMN_DIARY_PAIN = "pain";
        public static final String COLUMN_DIARY_FALLS = "falls";
        public static final String COLUMN_DIARY_SUBMITTED = "submitted";

        public static long convertDateToUnixTimestamp(String strDate){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            long timeStamp = 0;
            try {
                Date date = formatter.parse(strDate);
                timeStamp = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return timeStamp;
        }

        public static String convertUnixTimestampToDate(long timestamp){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date(timestamp);
            return formatter.format(date);
        }
    }
}
