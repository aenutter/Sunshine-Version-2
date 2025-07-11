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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;

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

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.DogEntry._ID,
            WeatherContract.DogEntry.COLUMN_DOG_NAME,
            WeatherContract.DogEntry.COLUMN_DOG_BREED,
            WeatherContract.DogEntry.COLUMN_DOG_GENDER,
            WeatherContract.DogEntry.COLUMN_DOG_WALK_AM,
            WeatherContract.DogEntry.COLUMN_DOG_WALK_PM,
            WeatherContract.DogEntry.COLUMN_DOG_OFFICE,
            WeatherContract.DogEntry.COLUMN_DOG_VISITOR,
            WeatherContract.DogEntry.COLUMN_DOG_VOLUNTEER_ROOM,
            WeatherContract.DogEntry.COLUMN_DOG_ADVENTURE_TAILS
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_DOG_ID = 0;
    public static final int COL_DOG_NAME = 1;
    public static final int COL_DOG_BREED = 2;
    public static final int COL_DOG_GENDER = 3;
    public static final int COL_DOG_WALK_AM= 4;
    public static final int COL_DOG_WALK_PM = 5;
    public static final int COL_DOG_OFFICE = 6;
    public static final int COL_DOG_VISITOR = 7;
    public static final int COL_DOG_VOLUNTEER_ROOM = 8;
    public static final int COL_DOG_ADVENTURE_TAILS = 9;

//    private ImageView mIconView;
    private TextView mNameView;
    private TextView mBreedView;
    private TextView mGenderView;
    private CheckedTextView mWalkAMView;
    private CheckedTextView mWalkPMView;
    private CheckedTextView mOfficeView;
    private CheckedTextView mVisitorView;
    private CheckedTextView mVolunteerView;
    private CheckedTextView mAdventureTailsView;

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

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        MyLogger.d("sunshine", "rootView id: " + rootView.getId());
//        MyLogger.d("sunshine", "container root view id is: " + container.getRootView().getId());
        rootView.requestLayout();


//        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
//        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

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
        mGenderView = (TextView) view.findViewById(R.id.detail_dog_gender_textview);
//        mBreedView = (TextView) view.findViewById(R.id.detail_dog_breed_text_view);
        mWalkAMView = (CheckedTextView) view.findViewById(R.id.walk_AM_checked_text_view);
        mWalkPMView = (CheckedTextView) view.findViewById(R.id.walk_PM_checked_text_view);
        mOfficeView = (CheckedTextView) view.findViewById(R.id.office_checked_text_view);
        mVisitorView = (CheckedTextView) view.findViewById(R.id.visitor_checked_text_view);
        mVolunteerView= (CheckedTextView) view.findViewById(R.id.volunteer_room_checked_text_view);
        mAdventureTailsView = (CheckedTextView) view.findViewById(R.id.adventure_tails_checked_text_view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
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
            int dogId = data.getInt(COL_DOG_ID);

            // Use weather art image
//            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            // Read date from cursor and update views for day of week and date
//            long date = data.getLong(COL_WEATHER_DATE);
//            String nameText = Utility.getDayName(getActivity(), date);
//            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
//            mFriendlyDateView.setText(friendlyDateText);
//            mDateView.setText(dateText);

            // Read description from cursor and update view
            String nameView = data.getString(COL_DOG_NAME);
            MyLogger.d("sunshine", "nameView: " + nameView);

            mNameView.setText(nameView);


            String genderView = data.getString(COL_DOG_GENDER);
            mGenderView.setText(genderView);

            String breedView = data.getString(COL_DOG_BREED);
//            mBreedView.setText(breedView);

            MyLogger.d("sunshine", "walk AM value: " + data.getInt(COL_DOG_WALK_AM));
            Boolean walkAM = (data.getInt(COL_DOG_WALK_AM) == 1);
            MyLogger.d("sunshine", "walk AM boolean value: " + walkAM);
            if (walkAM) mWalkAMView.setChecked(true); mWalkAMView.setChecked(false);

            MyLogger.d("sunshine", "walk PM value: " + data.getInt(COL_DOG_WALK_PM));
            Boolean walkPM = (data.getInt(COL_DOG_WALK_PM) == 1);
            MyLogger.d("sunshine", "walk PM boolean value: " + walkPM);
            if (walkPM) {
                mWalkPMView.setChecked(true);
            }
            else {
                mWalkPMView.setChecked(false);
            }

            Boolean office = (data.getInt(COL_DOG_OFFICE) == 1);
            if (office) mOfficeView.setChecked(true); mOfficeView.setChecked(false);

            Boolean visitor = (data.getInt(COL_DOG_VISITOR) == 1);
            if (visitor) mVisitorView.setChecked(true); mVisitorView.setChecked(false);

            Boolean volunteerRoom = (data.getInt(COL_DOG_VOLUNTEER_ROOM) == 1);
            if (volunteerRoom) mVolunteerView.setChecked(true); mVolunteerView.setChecked(false);

            Boolean adventureTails = (data.getInt(COL_DOG_ADVENTURE_TAILS) == 1);
            if (adventureTails) mAdventureTailsView.setChecked(true); mAdventureTailsView.setChecked(false);

            // For accessibility, add a content description to the icon field
//            mIconView.setContentDescription(description);

            // Read high temperature from cursor and update view
//            boolean isMetric = Utility.isMetric(getActivity());

//            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
//            String highString = Utility.formatTemperature(getActivity(), high);
//            mHighTempView.setText(highString);

            // Read low temperature from cursor and update view
//            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
//            String lowString = Utility.formatTemperature(getActivity(), low);
//            mLowTempView.setText(lowString);

            // Read humidity from cursor and update view
//            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
//            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            // Read wind speed and direction from cursor and update view
//            float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
//            float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
//            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

            // Read pressure from cursor and update view
//            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
//            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            // We still need this for the share intent
//            mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(createShareForecastIntent());
//            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}