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

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.AppConstants;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;
import com.example.android.sunshine.app.sync.DogSyncAdapter;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class DogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = DogFragment.class.getSimpleName();
    private DogAdapter mDogAdapter;
//    WeatherDbHelper mOpenHelper;
    Cursor cursor;
//    private ShareActionProvider mShareActionProvider;
//    private String mForecast;

    static final String DETAIL_URI = "URI";

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int DOG_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] DOG_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.DogEntry.TABLE_NAME + "." + WeatherContract.DogEntry._ID,
            WeatherContract.DogEntry.COLUMN_DOG_NAME,
            WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR,
            WeatherContract.DogEntry.COLUMN_DOG_GENDER,
            WeatherContract.DogEntry.COLUMN_DOG_WALK_AM,
            WeatherContract.DogEntry.COLUMN_DOG_WALK_PM,
            WeatherContract.DogEntry.COLUMN_DOG_OFFICE,
            WeatherContract.DogEntry.COLUMN_DOG_VISITOR,
            WeatherContract.DogEntry.COLUMN_DOG_VOLUNTEER_ROOM,
            WeatherContract.DogEntry.COLUMN_DOG_ADVENTURE_TAILS,
            WeatherContract.DogEntry.COLUMN_DOG_LOCATION,
            WeatherContract.DogEntry.COLUMN_DOG_KENNEL_NUMBER,
            WeatherContract.DogEntry.COLUMN_DOG_PLAYGROUP
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_DOG_ID = 0;
//    static final int COL_DOG_DOG_ID = 1;
    static final int COL_DOG_NAME = 1;
    static final int COL_DOG_WALKING_COLOR = 2;
    static final int COL_DOG_GENDER = 3;
    static final int COL_DOG_WALK_AM = 4;
    static final int COL_DOG_WALK_PM = 5;
    static final int COL_DOG_OFFICE = 6;
    static final int COL_DOG_VISITOR = 7;
    static final int COL_DOG_VOLUNTEER_ROOM = 8;
    static final int COL_DOS_ADVENTURE_TAILS = 9;
    static final int COL_DOG_LOCATION = 10;
    static final int COL_DOS_KENNEL_NUMBER = 11;
    static final int COL_DOG_PLAYGROUP = 12;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public DogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_delete);

        // Get the provider and hold onto it to set/change the share intent.
//        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//
//        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
//        if (mForecast != null) {
//            mShareActionProvider.setShareIntent(createShareForecastIntent());
//        }

        MenuItem menuNewItem = menu.findItem(R.id.action_new);

        MenuItem menuMoveItem = menu.findItem(R.id.action_move);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getContext());

            // set prompts.xml to alertdialog builder
//            alertDialogBuilder.setView(promptsView);
//
//            final EditText userInput = (EditText) promptsView
//                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setMessage("Delete this entry? ")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    // edit text
//                                    result.setText(userInput.getText());
                                    if (id == DialogInterface.BUTTON_POSITIVE) {
//                                        Toast.makeText(getActivity(), "Positive button clicked!", Toast.LENGTH_SHORT).show();
                                        WeatherDbHelper mOpenHelper;
                                        mOpenHelper = new WeatherDbHelper(getContext());
                                        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
                                        String selection = WeatherContract.DogEntry.TABLE_NAME+
                                                "." + WeatherContract.DogEntry.COLUMN_ID + " = ? ";
                                        String[] selectionArgs = new String[]{String.valueOf(mPosition + AppConstants.GLOBAL_OFFSET)};
                                        Integer rowsDeleted = database.delete(WeatherContract.DogEntry.TABLE_NAME, selection, selectionArgs);
                                        Toast.makeText(getContext(), "Action Delete rows deleted: " + rowsDeleted + " position: " + mPosition, Toast.LENGTH_LONG).show();
                                        database.close();
                                        Utility.sortData(getContext());
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    if (id == DialogInterface.BUTTON_NEGATIVE) {
                                        Toast.makeText(getActivity(), "Negative button clicked!", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
//            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
////            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//            if (positiveButton != null) {
//                WeatherDbHelper mOpenHelper;
//                mOpenHelper = new WeatherDbHelper(getContext());
//                final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
//                String selection = WeatherContract.DogEntry.TABLE_NAME+
//                        "." + WeatherContract.DogEntry.COLUMN_ID + " = ? ";
//                String[] selectionArgs = new String[]{String.valueOf(mPosition + AppConstants.GLOBAL_OFFSET)};
//                Integer rowsDeleted = database.delete(WeatherContract.DogEntry.TABLE_NAME, selection, selectionArgs);
//                Toast.makeText(getContext(), "Action Delete rows deleted: " + rowsDeleted + " position: " + mPosition, Toast.LENGTH_LONG).show();
//                database.close();
//                Utility.sortData(getContext());
////            getContext().getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
//                return true;
//            }
//            System.exit(0);
//            alertDialog.getButton()
//            updateWeather();

        }
        if (id == R.id.action_new) {
//            openPreferredLocationInMap();
            Toast.makeText(getContext(), "Action New", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_move) {
//            openPreferredLocationInMap();
            Toast.makeText(getContext(), "Action Move", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mDogAdapter = new DogAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mDogAdapter);
        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                MyLogger.d("sunshine", "onItemClick position: " + position);
                if (cursor != null) {
//                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    MyLogger.d("sunshine", "onItemClick uri: " + WeatherContract.DogEntry.buildDogUri(position + AppConstants.GLOBAL_OFFSET ));
                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.DogEntry.buildDogUri(position + AppConstants.GLOBAL_OFFSET )
                            );
                }
                mPosition = position;
            }
        });

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                QuestionItem questionItem = (QuestionItem)parent.getItemAtPosition(position);
//
//                if(questionItem.id == 1){
//                    //do something
//                }
//                if(position == 2){
//                    // do something
//                }
//            }
//        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mDogAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DOG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged( ) {
        updateDog();
        getLoaderManager().restartLoader(DOG_LOADER, null, this);
    }

    private void updateDog() {
        DogSyncAdapter.syncImmediately(getActivity());
    }

/*    private void openPreferredLocationInMap() {
        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        if ( null != mForecastAdapter ) {
            Cursor c = mForecastAdapter.getCursor();
            if ( null != c ) {
                c.moveToPosition(0);
                String posLat = c.getString(COL_COORD_LAT);
                String posLong = c.getString(COL_COORD_LONG);
                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
            }

        }
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.DogEntry._ID + " ASC";
        String msortOrder = WeatherContract.DogEntry.COLUMN_DOG_LOCATION + " desc, " + WeatherContract.DogEntry.COLUMN_DOG_KENNEL_NUMBER + " asc";

//        String locationSetting = Utility.getPreferredLocation(getActivity());
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());
        Uri weatherForLocationUri = WeatherContract.DogEntry.buildDogUri(i);
        MyLogger.d("sunshine", "uri: " + weatherForLocationUri);

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                DOG_COLUMNS,
                null,
                null,
                msortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDogAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDogAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mDogAdapter != null) {
            mDogAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }
}