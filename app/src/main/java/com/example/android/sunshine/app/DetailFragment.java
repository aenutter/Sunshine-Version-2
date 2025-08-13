/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.sunshine.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.app.data.WeatherDbHelper;
//import com.example.android.sunshine.app.data.MyContentObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;
    private String mForecast;
    private Uri mUri;
    private Handler uiHandler;
    WeatherDbHelper mOpenHelper;
    int dogId;
    private View view;
//    HashMap<String, List<String>> dependentData;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.DogEntry.COLUMN_ID,
//            WeatherContract.DogEntry.DOG_ID,
            WeatherContract.DogEntry.COLUMN_DOG_NAME,
            WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR,
            WeatherContract.DogEntry.COLUMN_DOG_GENDER,
            WeatherContract.DogEntry.COLUMN_DOG_WALK_AM,
            WeatherContract.DogEntry.COLUMN_DOG_WALK_PM,
            WeatherContract.DogEntry.COLUMN_DOG_OFFICE,
            WeatherContract.DogEntry.COLUMN_DOG_VISITOR,
            WeatherContract.DogEntry.COLUMN_DOG_VOLUNTEER_ROOM,
            WeatherContract.DogEntry.COLUMN_DOG_ADVENTURE_TAILS,
            WeatherContract.DogEntry.COLUMN_DOG_PLAYGROUP
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_ID = 0;
//    public static final int COL_DOG_ID = 1;
    public static final int COL_DOG_NAME = 1;
    public static final int COL_DOG_WALKING_COLOR = 2;
    public static final int COL_DOG_GENDER = 3;
    public static final int COL_DOG_WALK_AM= 4;
    public static final int COL_DOG_WALK_PM = 5;
    public static final int COL_DOG_OFFICE = 6;
    public static final int COL_DOG_VISITOR = 7;
    public static final int COL_DOG_VOLUNTEER_ROOM = 8;
    public static final int COL_DOG_ADVENTURE_TAILS = 9;
    public static final int COL_DOG_PLAYGROUP = 10;

//    private ImageView mIconView;
    private TextView mNameView;
    private TextView mWalkingColorView;
    private TextView mGenderView;
    private TextView mTextViewBlue;
    private TextView mTextViewPink;
    private TextView mTextViewYellow;
    private TextView mTextViewOrange;
    private TextView mTextViewRed;
    public static CheckBox playgroupView;
    public static CheckBox mWalkAMView;
    public static CheckBox mWalkPMView;
    public static CheckBox mOfficeView;
    public static CheckBox mVisitorView;
    public static CheckBox mVolunteerView;
    public static CheckBox mAdventureTailsView;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButtonBlue;
    private RadioButton mRadioButtonPink;
    private RadioButton mRadioButtonYellow;
    private RadioButton mRadioButtonOrange;
    private RadioButton mRadioButtonRed;
    private RadioGroup radioGroupWalkingColors;
    Spinner spinnerFirst;
    Spinner spinnerSecond;
    View mFirstLineView;
    View mSecondLineView;
    View mThirdLineView;



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        uiHandler = new Handler(Looper.getMainLooper());
        View rootView = inflater.inflate(R.layout.fragment_detail_wide, container, false);
        MyLogger.d("sunshine", "rootView id: " + rootView.getId());
//        MyLogger.d("sunshine", "container root view id is: " + container.getRootView().getId());
        rootView.requestLayout();
        mOpenHelper = new WeatherDbHelper(getContext());

//        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
//        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        createAlarm();
        return rootView;
    }

//    public void setCheckboxStateDelayed(final CheckBox checkBox, final boolean isChecked, long delayMillis) {
//        uiHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkBox.setChecked(isChecked);
//            }
//        }, delayMillis);
//    }

//    public void setCheckboxStateDelayed(final CheckBox checkBox, final boolean checked, long delayMillis) {
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkBox.setChecked(checked);
//            }
//        }, delayMillis);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Access views within the fragment's layout

        mNameView = (TextView) view.findViewById(R.id.detail_dog_name_text_view);
//        // Now, you can safely set the text11
//        if (mNameView != null) {
//            mNameView.setText("Hello from Fragment!");
//        } else {
//            // Handle the case where the TextView is not found (e.g., log an error)
//            MyLogger.d("sunshine", "mNameView doesn't exist:");
//        }


        MyLogger.d("sunshine", "mNameView initialized");
        spinnerFirst = (Spinner) view.findViewById(R.id.spinner_first);
        spinnerSecond = (Spinner) view.findViewById(R.id.spinner_second);
        mGenderView = (TextView) view.findViewById(R.id.detail_dog_gender_textview);
        mFirstLineView = (View) view.findViewById(R.id.first_line_view);
        mSecondLineView = (View) view.findViewById(R.id.second_line_view);
        mThirdLineView = (View) view.findViewById(R.id.third_line_view);
//        mTextViewBlue = (TextView) view.findViewById(R.id.text_blue);
//        mTextViewPink = (TextView) view.findViewById(R.id.text_pink);
//        mTextViewYellow = (TextView) view.findViewById(R.id.text_yellow);
//        mTextViewOrange = (TextView) view.findViewById(R.id.text_orange);
//        mTextViewRed = (TextView) view.findViewById(R.id.text_red);
//        mWalkingColorView = (ImageView) view.findViewById(R.id.detail_dog);
        playgroupView = (CheckBox) view.findViewById(R.id.playgroup_check_box);
        mWalkAMView = (CheckBox) view.findViewById(R.id.walk_AM_checked_text_view);
        mWalkPMView = (CheckBox) view.findViewById(R.id.walk_PM_checked_text_view);
        mOfficeView = (CheckBox) view.findViewById(R.id.office_checked_text_view);
        mVisitorView = (CheckBox) view.findViewById(R.id.visitor_checked_text_view);
        mVolunteerView= (CheckBox) view.findViewById(R.id.volunteer_room_checked_text_view);
        mAdventureTailsView = (CheckBox) view.findViewById(R.id.adventure_tails_checked_text_view);
        mRadioButtonBlue = (RadioButton) view.findViewById(R.id.radio_blue);
        mRadioButtonPink = (RadioButton) view.findViewById(R.id.radio_pink);
        mRadioButtonYellow = (RadioButton) view.findViewById(R.id.radio_yellow);
        mRadioButtonOrange = (RadioButton) view.findViewById(R.id.radio_orange);
        mRadioButtonRed = (RadioButton) view.findViewById(R.id.radio_red);
        radioGroupWalkingColors = (RadioGroup) view.findViewById(R.id.walking_color_radio_group);
        view.requestLayout();

//        private MyContentObserver contentObserver;
//
//        // Create a Handler for the observer to run on the main thread
//        Handler handler = new Handler(Looper.getMainLooper());
//        contentObserver = new MyContentObserver(handler);
//
//        // Register the observer with the ContentResolver
//        // Replace 'YOUR_CONTENT_URI' with the actual URI you want to observe
//        requireContext().getContentResolver().registerContentObserver(
//                YOUR_CONTENT_URI, // The URI to observe
//                true, // Notify descendants of the URI
//                contentObserver
//        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_delete);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
//        if (mForecast != null) {
//            mShareActionProvider.setShareIntent(createShareForecastIntent());
//        }

//        MenuItem menuNewItem = menu.findItem(R.id.action_new);

//        MenuItem menuMoveItem = menu.findItem(R.id.action_move);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            dogId = data.getInt(COL_ID);
            int i = 0;
            for (i=0; i< data.getColumnCount(); i++)
                MyLogger.d("sunshine", "onloadfinished cursor column names: " + data.getColumnName(i) + " column value: " + data.getString(i));

//            final HashMap<String, List<String>> dependentData = new HashMap<>();
//            dependentData.put("Mandy", Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
//            dependentData.put("Kennel", Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
//
//            // Populate first spinner
//            ArrayAdapter<String> adapterFirst = new ArrayAdapter<>(getContext(),
//                    android.R.layout.simple_spinner_item, new ArrayList<>(dependentData.keySet()));
//            adapterFirst.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerFirst.setAdapter(adapterFirst);
//
//            // Set listener for first spinner
//            spinnerFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    String selectedCategory = parent.getItemAtPosition(position).toString();
//                    List<String> secondSpinnerOptions = dependentData.get(selectedCategory);
//
//                    // Populate second spinner based on first spinner's selection
//                    ArrayAdapter<String> adapterSecond = new ArrayAdapter<>(getActivity(),
//                            android.R.layout.simple_spinner_item, secondSpinnerOptions);
//                    adapterSecond.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerSecond.setAdapter(adapterSecond);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                    // Optionally handle when nothing is selected
//                }
//            });
            mFirstLineView.setVisibility(View.VISIBLE);
            mSecondLineView.setVisibility(View.VISIBLE);
            mThirdLineView.setVisibility(View.VISIBLE);
            String walkingColor  = data.getString(DogFragment.COL_DOG_WALKING_COLOR);

            if (walkingColor.equals("blue"))
                mRadioButtonBlue.setChecked(true);
            mRadioButtonBlue.setVisibility(View.VISIBLE);

            if (walkingColor.equals("pink"))
                mRadioButtonPink.setChecked(true);
            mRadioButtonPink.setVisibility(View.VISIBLE);

            if (walkingColor.equals("yellow"))
                mRadioButtonYellow.setChecked(true);
            mRadioButtonYellow.setVisibility(View.VISIBLE);

            if (walkingColor.equals("orange"))
                mRadioButtonOrange.setChecked(true);
            mRadioButtonOrange.setVisibility(View.VISIBLE);

            if (walkingColor.equals("red"))
                mRadioButtonRed.setChecked(true);
            mRadioButtonRed.setVisibility(View.VISIBLE);

            // Read description from cursor and update view
            String nameView = data.getString(COL_DOG_NAME);
            MyLogger.d("sunshine", "DetailFragment nameView: " + nameView);

            mNameView.setText(nameView);


            String genderView = data.getString(COL_DOG_GENDER);
            mGenderView.setText(genderView);

//            String breedView = data.getString(COL_DOG_WALKING_COLOR);
//            mBreedView.setText(breedView);

            //            MyLogger.d("sunshine", "DetailFragment walk AM value: " + data.getInt(COL_DOG_WALK_AM));
            playgroupView.setVisibility(View.VISIBLE);
            Boolean playgroup = (data.getInt(COL_DOG_PLAYGROUP) == 1);
            MyLogger.d("sunshine", "DetailFragment playgroup boolean value: " + playgroup);
            if (playgroup) {
                playgroupView.setChecked(true);
//                setCheckboxStateDelayed(mWalkAMView, walkAM, 100);
//                MyLogger.d("sunshine", "walk AM set after set checked to true: " + mWalkAMView.isChecked());
            } else {
//                mWalkAMView.setChecked(false);
//                MyLogger.d("sunshine", "walk AM set after set checked to false: " + mWalkAMView.isChecked());
            }

//            MyLogger.d("sunshine", "DetailFragment walk AM value: " + data.getInt(COL_DOG_WALK_AM));
            mWalkAMView.setVisibility(View.VISIBLE);
            Boolean walkAM = (data.getInt(COL_DOG_WALK_AM) == 1);
            MyLogger.d("sunshine", "DetailFragment walk AM boolean value: " + walkAM);
            if (walkAM) {
                mWalkAMView.setChecked(true);
//                setCheckboxStateDelayed(mWalkAMView, walkAM, 100);
//                MyLogger.d("sunshine", "walk AM set after set checked to true: " + mWalkAMView.isChecked());
            } else {
//                mWalkAMView.setChecked(false);
//                MyLogger.d("sunshine", "walk AM set after set checked to false: " + mWalkAMView.isChecked());
            }

//            MyLogger.d("sunshine", "DetailFragment walk PM value: " + data.getInt(COL_DOG_WALK_PM));
            mWalkPMView.setVisibility(View.VISIBLE);
            Boolean walkPM = (data.getInt(COL_DOG_WALK_PM) == 1);
            MyLogger.d("sunshine", "DetailFragment walk PM boolean value: " + walkPM);
            if (walkPM) {
                mWalkPMView.setChecked(true);
//                setCheckboxStateDelayed(mWalkPMView, walkPM, 100);
            }
            else {
//                mWalkPMView.setChecked(false);
            }

            mOfficeView.setVisibility(View.VISIBLE);
            final Boolean office = (data.getInt(COL_DOG_OFFICE) == 1);
            MyLogger.d("sunshine", "DetailFragment Office boolean value: " + office);
            if (office)
            {
                mOfficeView.setChecked(true);
//                mOfficeView.setOnCheckedChangeListener(null);
//                setCheckboxStateDelayed(mOfficeView, office, 100);
            } else {
//                mOfficeView.setChecked(false);
            }
            MyLogger.d("sunshine", "DetailFragment Office is checked: " + mOfficeView.isChecked());

            mVisitorView.setVisibility(View.VISIBLE);
            Boolean visitor = (data.getInt(COL_DOG_VISITOR) == 1);
            MyLogger.d("sunshine", "DetailFragment Visitor boolean value: " + visitor);
            if (visitor)
            {
                mVisitorView.setChecked(true);
//                setCheckboxStateDelayed(mVisitorView, visitor, 100);
            } else {
//                mOfficeView.setChecked(false);
            }

            mVolunteerView.setVisibility(View.VISIBLE);
            Boolean volunteerRoom = (data.getInt(COL_DOG_VOLUNTEER_ROOM) == 1);
            MyLogger.d("sunshine", "DetailFragment Volunteer Room boolean value: " + volunteerRoom);
            if (volunteerRoom)
            {
                mVolunteerView.setChecked(true);
//                setCheckboxStateDelayed(mVolunteerView, volunteerRoom, 100);
            } else {
//                mVolunteerView.setChecked(false);
            }

            mAdventureTailsView.setVisibility(View.VISIBLE);
            Boolean adventureTails = (data.getInt(COL_DOG_ADVENTURE_TAILS) == 1);
            MyLogger.d("sunshine", "DetailFragment Adventure Tails boolean value: " + adventureTails);
            if (adventureTails)
            {
                mAdventureTailsView.setChecked(true);
//                setCheckboxStateDelayed(mAdventureTailsView, adventureTails, 100);
            } else {
//                mAdventureTailsView.setChecked(false);
            }

            radioGroupWalkingColors.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // checkedId is the ID of the newly selected RadioButton
//                    Toast.makeText(getActivity(), "Inside onCheckedChanged", Toast.LENGTH_SHORT).show();
                    if (checkedId == R.id.radio_blue) {
                        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        String walkingStatus = "blue"; // 1 for checked, 0 for unchecked
                        values.put(WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR, walkingStatus);


                        // Assuming you have a unique ID for the item associated with the checkbox
                        String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                        String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                        if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                            getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                        }
                        db.close();
                        MyLogger.d("sunshine", "setOnCheckedChangeListener dog id: " + dogId);
                        MyLogger.d("sunshine", "setOnCheckedChangeListener completed status: " + walkingStatus);
                        MyLogger.d("sunshine", "setOnCheckedChangeListener whereClause: " + whereClause);
                        MyLogger.d("sunshine", "setOnCheckedChangeListener whereArgs: " + whereArgs);
                        // Handle selection of Option 1
                        Toast.makeText(getActivity(), "Blue button is pressed", Toast.LENGTH_SHORT).show();
                    } else if (checkedId == R.id.radio_pink) {
                        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        String walkingStatus = "pink"; // 1 for checked, 0 for unchecked
                        values.put(WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR, walkingStatus);


                        // Assuming you have a unique ID for the item associated with the checkbox
                        String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                        String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                        if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                            getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                        }
                        db.close();
                        // Handle selection of Option 2
                        Toast.makeText(getActivity(), "Pink button is pressed", Toast.LENGTH_SHORT).show();
                    } else if (checkedId == R.id.radio_yellow) {
                        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        String walkingStatus = "yellow"; // 1 for checked, 0 for unchecked
                        values.put(WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR, walkingStatus);


                        // Assuming you have a unique ID for the item associated with the checkbox
                        String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                        String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                        if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                            getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                        }
                        db.close();
                        // Handle selection of Option 2
                        Toast.makeText(getActivity(), "Yellow button is pressed", Toast.LENGTH_SHORT).show();
                    }
                    else if (checkedId == R.id.radio_orange) {
                        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        String walkingStatus = "orange"; // 1 for checked, 0 for unchecked
                        values.put(WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR, walkingStatus);


                        // Assuming you have a unique ID for the item associated with the checkbox
                        String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                        String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                        if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                            getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                        }
                        db.close();
                        // Handle selection of Option 2
                        Toast.makeText(getActivity(), "Orange button is pressed", Toast.LENGTH_SHORT).show();
                    } else if (checkedId == R.id.radio_red) {
                        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        String walkingStatus = "red"; // 1 for checked, 0 for unchecked
                        values.put(WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR, walkingStatus);


                        // Assuming you have a unique ID for the item associated with the checkbox
                        String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                        String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                        if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                            getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                        }
                        db.close();
                        // Handle selection of Option 2
                        Toast.makeText(getActivity(), "Red button is pressed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // setup on Check Change Listeners for all seven check boxes
            playgroupView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    int completedStatus = isChecked ? 1 : 0; // 1 for checked, 0 for unchecked
                    values.put(WeatherContract.DogEntry.COLUMN_DOG_PLAYGROUP, completedStatus);


                    // Assuming you have a unique ID for the item associated with the checkbox
                    String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                    String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                    if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                    }
                    db.close();
                    MyLogger.d("sunshine", "setOnCheckedChangeListener dog id: " + dogId);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener completed status: " + completedStatus);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereClause: " + whereClause);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereArgs: " + whereArgs);
                }
            });
            mWalkAMView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    int completedStatus = isChecked ? 1 : 0; // 1 for checked, 0 for unchecked
                    values.put(WeatherContract.DogEntry.COLUMN_DOG_WALK_AM, completedStatus);


                    // Assuming you have a unique ID for the item associated with the checkbox
                    String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                    String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                    if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                    }
                    db.close();
                    MyLogger.d("sunshine", "setOnCheckedChangeListener dog id: " + dogId);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener completed status: " + completedStatus);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereClause: " + whereClause);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereArgs: " + whereArgs);
                }
            });
            mWalkPMView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    int completedStatus = isChecked ? 1 : 0; // 1 for checked, 0 for unchecked
                    values.put(WeatherContract.DogEntry.COLUMN_DOG_WALK_PM, completedStatus);


                    // Assuming you have a unique ID for the item associated with the checkbox
                    String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                    String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                    if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                    }
                    db.close();
                    MyLogger.d("sunshine", "setOnCheckedChangeListener dog id: " + dogId);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener completed status: " + completedStatus);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereClause: " + whereClause);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereArgs: " + whereArgs[0]);
                }
            });

            mOfficeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    int completedStatus = isChecked ? 1 : 0; // 1 for checked, 0 for unchecked

//                    if (mOfficeView.isPressed()) {
//                        // CheckBox is checked
//                        Toast.makeText(getActivity(), "Checkbox is pressed!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // CheckBox is unchecked
//                        Toast.makeText(getActivity(), "Checkbox is not pressed!", Toast.LENGTH_SHORT).show();
//                    }
                    values.put(WeatherContract.DogEntry.COLUMN_DOG_OFFICE, completedStatus);


                    // Assuming you have a unique ID for the item associated with the checkbox
                    String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                    String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                    if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
//                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);

                    }
                    db.close();
                    MyLogger.d("sunshine", "mOfficeView setOnCheckedChangeListener dog id: " + dogId);
                    MyLogger.d("sunshine", "mOfficeView setOnCheckedChangeListener completed status: " + completedStatus);
                    MyLogger.d("sunshine", "mOfficeView setOnCheckedChangeListener whereClause: " + whereClause);
                    MyLogger.d("sunshine", "mOfficeView setOnCheckedChangeListener whereArgs: " + whereArgs[0]);
                }
            });
            mVisitorView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    int completedStatus = isChecked ? 1 : 0; // 1 for checked, 0 for unchecked
                    values.put(WeatherContract.DogEntry.COLUMN_DOG_VISITOR, completedStatus);


                    // Assuming you have a unique ID for the item associated with the checkbox
                    String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                    String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                    if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                    }
                    db.close();
                    MyLogger.d("sunshine", "setOnCheckedChangeListener dog id: " + dogId);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener completed status: " + completedStatus);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereClause: " + whereClause);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereArgs: " + whereArgs[0]);
                }
            });
            mVolunteerView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    int completedStatus = isChecked ? 1 : 0; // 1 for checked, 0 for unchecked
                    values.put(WeatherContract.DogEntry.COLUMN_DOG_VOLUNTEER_ROOM, completedStatus);


                    // Assuming you have a unique ID for the item associated with the checkbox
                    String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                    String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                    if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                    }
                    db.close();
                    MyLogger.d("sunshine", "setOnCheckedChangeListener dog id: " + dogId);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener completed status: " + completedStatus);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereClause: " + whereClause);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereArgs: " + whereArgs[0]);
                }
            });
            mAdventureTailsView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    int completedStatus = isChecked ? 1 : 0; // 1 for checked, 0 for unchecked
                    values.put(WeatherContract.DogEntry.COLUMN_DOG_ADVENTURE_TAILS, completedStatus);


                    // Assuming you have a unique ID for the item associated with the checkbox
                    String whereClause = WeatherContract.DogEntry.COLUMN_ID+ " = ?";
                    String[] whereArgs = {String.valueOf(dogId)}; // Replace itemId with the actual ID
//                    getContext().getContentResolver().update(WeatherContract.DogEntry.CONTENT_URI, values, whereClause, whereArgs);
                    if (db.update(WeatherContract.DogEntry.TABLE_NAME, values, whereClause, whereArgs) == 1) {
                        getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
                    }
                    db.close();
                    MyLogger.d("sunshine", "setOnCheckedChangeListener dog id: " + dogId);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener completed status: " + completedStatus);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereClause: " + whereClause);
                    MyLogger.d("sunshine", "setOnCheckedChangeListener whereArgs: " + whereArgs[0]);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    public void createAlarm() {
        //System request code
        int DATA_FETCHER_RC = 123;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);

        //Create the time of day you would like it to go off. Use a calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 38);

        //Create an intent that points to the receiver. The system will notify the app about the current time, and send a broadcast to the app
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), DATA_FETCHER_RC,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //initialize the alarm by using inexactrepeating. This allows the system to scheduler your alarm at the most efficient time around your
        //set time, it is usually a few seconds off your requested time.
        // you can also use setExact however this is not recommended. Use this only if it must be done then.

        //Also set the interval using the AlarmManager constants
        mAlarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);

    }
    //This is the broadcast receiver you create where you place your logic once the alarm is run. Once the system realizes your alarm should be run, it will communicate to your app via the BroadcastReceiver. You must implement onReceive.
    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Your code once the alarm is set off goes here
            //You can use an intent filter to filter the specified intent
            WeatherDbHelper mOpenHelper;
            mOpenHelper = new WeatherDbHelper(context);
            final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
//            String selection = WeatherContract.DogEntry.TABLE_NAME+
//                    "." + WeatherContract.DogEntry.COLUMN_ID + " = ? ";
//            String[] selectionArgs = new String[]{String.valueOf(itemID)};

            ContentValues values = new ContentValues();
            values.put(WeatherContract.DogEntry.COLUMN_DOG_PLAYGROUP, 0);
            values.put(WeatherContract.DogEntry.COLUMN_DOG_WALK_AM, 0);
            values.put(WeatherContract.DogEntry.COLUMN_DOG_WALK_PM, 0);
            values.put(WeatherContract.DogEntry.COLUMN_DOG_OFFICE, 0);
            values.put(WeatherContract.DogEntry.COLUMN_DOG_VISITOR, 0);
            values.put(WeatherContract.DogEntry.COLUMN_DOG_VOLUNTEER_ROOM, 0);
            values.put(WeatherContract.DogEntry.COLUMN_DOG_ADVENTURE_TAILS, 0);
            // Add more columns and their new values as needed

            Integer rowsUpdated = database.update(WeatherContract.DogEntry.TABLE_NAME, values, null, null);
//            Integer rowsDeleted = database.delete(WeatherContract.DogEntry.TABLE_NAME, selection, selectionArgs);
            Toast.makeText(context, "Action Delete rows updated: " + rowsUpdated, Toast.LENGTH_LONG).show();
            MyLogger.d("sunshine", "inside AlarmReceiver rows updated: " + rowsUpdated);
            database.close();
            context.getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
            DetailFragment.playgroupView.setChecked(false);
            DetailFragment.mWalkAMView.setChecked(false);
            DetailFragment.mWalkPMView.setChecked(false);
            DetailFragment.mOfficeView.setChecked(false);
            DetailFragment.mVisitorView.setChecked(false);
            DetailFragment.mVolunteerView.setChecked(false);
            DetailFragment.mAdventureTailsView.setChecked(false);
        }
    }

}