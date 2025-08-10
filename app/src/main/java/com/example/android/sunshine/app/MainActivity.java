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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;
import com.example.android.sunshine.app.sync.DogSyncAdapter;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements DogFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String DETAILFRAGMENT2_TAG = "DF2TAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
//        } else if (findViewById(R.id.dog_detail_container) != null) {
//            // The detail container view will be present only in the large-screen layouts
//            // (res/layout-sw600dp). If this view is present, then the activity should be
//            // in two-pane mode.
//            mTwoPane = true;
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.dog_detail_container, new DetailFragment(), DETAILFRAGMENT2_TAG)
//                        .commit();
//            }
//        }
        else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        DogFragment dogFragment =  ((DogFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        dogFragment.setUseTodayLayout(!mTwoPane);

        DogSyncAdapter.initializeSyncAdapter(this);
        createAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLogger.d("sunshine", "inside onResume() ");
        String location = Utility.getPreferredLocation( this );
//         update the location in our second pane using the fragment manager
            if (location != null && !location.equals(mLocation)) {
            DogFragment ff = (DogFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        MyLogger.d("sunshine", "inside onItemSelected ");
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
    public void createAlarm() {
        //System request code
        int DATA_FETCHER_RC = 123;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //Create the time of day you would like it to go off. Use a calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        //Create an intent that points to the receiver. The system will notify the app about the current time, and send a broadcast to the app
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DATA_FETCHER_RC,intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        }
    }
}
