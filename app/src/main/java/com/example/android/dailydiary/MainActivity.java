package com.example.android.dailydiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.dailydiary.data.DiaryContract.DiaryEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private int mQuantity;
    private boolean mSubmitted;
    private boolean mDiaryHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mDiaryHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDiaryHasChanged = true;
            return false;
        }
    };

    private EditText mNameTextView;
    private TextView mDateTextView;
    private RatingBar mMobilityRatingBar;
    private CheckBox mStiffnessCheckbox;
    private CheckBox mPainCheckbox;
    private TextView mFallsTextView;

    private TextView mDiarySummaryTextView;

    private Calendar mCalendar;

    private static final int EDITOR_LOADER_MANAGER = 1;
    private Uri mUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String nameSetting =
                sharedPrefs.getString(getString(R.string.settings_name_key),"");

        mNameTextView = findViewById(R.id.name_text);
        mDateTextView = findViewById(R.id.date_text_view);
        mMobilityRatingBar = findViewById(R.id.rating_bar);
        mStiffnessCheckbox = findViewById(R.id.stiffness);
        mPainCheckbox = findViewById(R.id.pain);
        mFallsTextView = findViewById(R.id.quantity_text_view);
        mDiarySummaryTextView = findViewById(R.id.diary_summary_text_view);

        mSubmitted = false;

        mCalendar = Calendar.getInstance();

        Button changeDateButton = findViewById(R.id.change_date_button);
        changeDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        addChangeListeners();

        mUri = getIntent().getData();
        if (mUri != null) {
            setTitle(R.string.activity_title_edit_diary);
            getLoaderManager().initLoader(EDITOR_LOADER_MANAGER, null, this);
        } else {
            setTitle(R.string.activity_title_new_diary);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an entry that hasn't been created yet.)
            invalidateOptionsMenu();

            if (!nameSetting.isEmpty()) {
                mNameTextView.setText(nameSetting);
            }

            mQuantity = Integer.parseInt(mFallsTextView.getText().toString());

            // set current date into textview
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            // set selected date into textview
            mDateTextView.setText(simpleDateFormat.format(mCalendar.getTime()));

            mDiarySummaryTextView.setText(R.string.summary_initial_value);
        }
    }

    private void addChangeListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mSubmitted = false;
                mDiarySummaryTextView.setText(R.string.item_changed_message);

            }
        };

        mNameTextView.addTextChangedListener(textWatcher);
        mDateTextView.addTextChangedListener(textWatcher);
        mFallsTextView.addTextChangedListener(textWatcher);
        mMobilityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mSubmitted = false;
                mDiarySummaryTextView.setText(R.string.item_changed_message);
            }
        });
        mStiffnessCheckbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSubmitted = false;
                mDiarySummaryTextView.setText(R.string.item_changed_message);
            }
        });
        mPainCheckbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSubmitted = false;
                mDiarySummaryTextView.setText(R.string.item_changed_message);
            }
        });

        mNameTextView.setOnTouchListener(mTouchListener);
        mDateTextView.setOnTouchListener(mTouchListener);
        mFallsTextView.setOnTouchListener(mTouchListener);
        mMobilityRatingBar.setOnTouchListener(mTouchListener);
        mStiffnessCheckbox.setOnTouchListener(mTouchListener);
        mPainCheckbox.setOnTouchListener(mTouchListener);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        OnDateSetListener datePickerListener = new OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                mCalendar.set(selectedYear, selectedMonth, selectedDay);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                mDateTextView.setText(simpleDateFormat.format(mCalendar.getTime()));
            }
        };

        // set date picker as current/selected date
        return new DatePickerDialog(this, datePickerListener,
                mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitDiary(View view) {
        String name = mNameTextView.getText().toString();
        String date = mDateTextView.getText().toString();
        int mobilityRating = Math.round(mMobilityRatingBar.getRating());//ratingStars(R.id.rating_bar);
        boolean stiffness = mStiffnessCheckbox.isChecked();
        boolean pain = mPainCheckbox.isChecked();
        int fallRisk = calculateFallRisk(stiffness, pain);

        switch (view.getId()) {
            case R.id.submit_button:
                mSubmitted = true;
                String diarySummary = createOrderSummary(name, date, mobilityRating, stiffness, pain,
                        mQuantity, fallRisk);
                displayMessage(diarySummary, name);
            case R.id.action_save:
                saveEntry(name, date, mobilityRating, stiffness, pain, mQuantity, mSubmitted);
                finish();
        }
    }

    private void saveEntry(String name,
                           String date,
                           int mobilityRating,
                           boolean stiffness,
                           boolean pain,
                           int quantity,
                           boolean submitted) {

        ContentValues values = new ContentValues();
        values.put(DiaryEntry.COLUMN_DIARY_NAME, name.trim());
        values.put(DiaryEntry.COLUMN_DIARY_DATE, DiaryEntry.convertDateToUnixTimestamp(date));
        values.put(DiaryEntry.COLUMN_DIARY_MOBILITY, mobilityRating);
        values.put(DiaryEntry.COLUMN_DIARY_STIFFNESS, stiffness);
        values.put(DiaryEntry.COLUMN_DIARY_PAIN, pain);
        values.put(DiaryEntry.COLUMN_DIARY_FALLS, quantity);
        values.put(DiaryEntry.COLUMN_DIARY_SUBMITTED, submitted);

        if (mUri != null) {
            int result = getContentResolver().update(mUri, values, null, null);
            if (result > 0)
                Toast.makeText(this, R.string.update_successful, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show();
        } else {
            Uri result = getContentResolver().insert(DiaryEntry.CONTENT_URI, values);
            if (result == null)
                Toast.makeText(this, R.string.insert_failed, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.insert_successful, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates the diary summary
     *
     * @param name           of patient
     * @param date           of diary entry
     * @param mobilityRating of patient
     * @param stiffness      checked state
     * @param pain           checked state
     * @param quantity       number of falls
     * @param fallRisk       fall risk
     * @return message string
     */
    private String createOrderSummary(String name,
                                      String date,
                                      int mobilityRating,
                                      boolean stiffness,
                                      boolean pain,
                                      int quantity,
                                      int fallRisk) {
        String message;
        message = getResources().getString(R.string.diary_summary_name, name);
        message += "\n" + getResources().getString(R.string.diary_summary_date, date);
        message += "\n" + getResources().getString(R.string.diary_summary_mobility, mobilityRating);
        message += "\n" + getResources().getString(R.string.diary_summary_stiffness, stiffness);
        message += "\n" + getResources().getString(R.string.diary_summary_pain, pain);
        message += "\n" + getResources().getString(R.string.diary_summary_quantity, quantity);
        message += "\n" + getResources().getString(R.string.diary_summary_fall_risk, fallRisk) + "%";
//        }
        return message;
    }

    /**
     * Calculates fall risk (falls * (10 + 3 for pain and 5 for stiffness))
     *
     * @param stiffness stiffness checked
     * @param pain      pain checked
     * @return totalRisk
     */
    private int calculateFallRisk(boolean stiffness, boolean pain) {
        int riskPerFall = 10;
        if (stiffness)
            riskPerFall += 5;
        if (pain)
            riskPerFall += 3;
        return riskPerFall * mQuantity;
    }

    /**
     * This method is called when the + button is clicked.
     */
    public void increment(View view) {
        if (mQuantity > 99) {
            Toast.makeText(this, getResources().getString(R.string.more_than_100_falls),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mQuantity = mQuantity + 1;
        mFallsTextView.setText(String.valueOf(mQuantity));
        String messageString = "This is the message";
    }

    /**
     * This method is called when the - button is clicked.
     */
    public void decrement(View view) {
        if (mQuantity < 1) {
            Toast.makeText(this, getResources().getString(R.string.less_than_0_falls),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mQuantity = mQuantity - 1;
        mFallsTextView.setText(String.valueOf(mQuantity));
        //displayQuantity(mQuantity);
    }


    /**
     * This method displays the given text on the screen.
     */
    private void displayMessage(String message, String name) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:m.o.khan@warwick.ac.uk")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.diary_summary_email_subject, name));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            mDiarySummaryTextView.setText(getString(R.string.diary_sent));
        } else {
            mDiarySummaryTextView.setText(getString(R.string.could_not_submit_message, message));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new entry, hide the "Delete" menu item.
        if (mUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                submitDiary(findViewById(R.id.action_save));
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mDiaryHasChanged) {
                    NavUtils.navigateUpFromSameTask(MainActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(MainActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the diary hasn't changed, continue with handling back button press
        if (!mDiaryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                DiaryEntry._ID,
                DiaryEntry.COLUMN_DIARY_NAME,
                DiaryEntry.COLUMN_DIARY_DATE,
                DiaryEntry.COLUMN_DIARY_MOBILITY,
                DiaryEntry.COLUMN_DIARY_STIFFNESS,
                DiaryEntry.COLUMN_DIARY_PAIN,
                DiaryEntry.COLUMN_DIARY_FALLS,
                DiaryEntry.COLUMN_DIARY_SUBMITTED
        };
        if (ContentUris.parseId(mUri) != -1)
            return new CursorLoader(this, mUri, projection, null, null, null);
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_NAME));
            long date = cursor.getLong(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_DATE));
            int mobility = cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_MOBILITY));
            boolean stiffness = cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_STIFFNESS)) == 1;
            boolean pain = cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_PAIN)) == 1;
            int falls = cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_FALLS));
            boolean submitted = cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_DIARY_SUBMITTED)) == 1;

            mNameTextView.setText(name);
            mCalendar.setTimeInMillis(date);
            mDateTextView.setText(DiaryEntry.convertUnixTimestampToDate(date));
            mMobilityRatingBar.setRating(mobility);
            mStiffnessCheckbox.setChecked(stiffness);
            mPainCheckbox.setChecked(pain);
            mQuantity = falls;
            mFallsTextView.setText(String.valueOf(falls));
            mSubmitted = submitted;

            if (submitted) {
                mDiarySummaryTextView.setText(R.string.diary_sent);
            } else {
                mDiarySummaryTextView.setText(R.string.diary_saved);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameTextView.getText().clear();
        mCalendar = Calendar.getInstance();
        mDateTextView.setText(DiaryEntry.convertUnixTimestampToDate(mCalendar.getTimeInMillis()));
        mMobilityRatingBar.setRating(3);
        mStiffnessCheckbox.setChecked(false);
        mPainCheckbox.setChecked(false);
        mQuantity = 0;
        mFallsTextView.setText(String.valueOf(0));
        mSubmitted = false;
    }

    /**
     * Shows an alert dialog if there are unsaved changes and the back button has been pressed
     * @param discardButtonClickListener the button to listen on
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    /**
     * Shows an alert dialog if the user clicks delete, avoids accidental deletion
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the entry.
                deleteEntry();
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

    /**
     * Perform the deletion of the entry in the database.
     */
    private void deleteEntry() {
        if (mUri != null) {
            int result = getContentResolver().delete(mUri, null, null);
            if (result > 0) {
                Toast.makeText(this, R.string.diary_deleting_successful, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.diary_deleting_falied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
