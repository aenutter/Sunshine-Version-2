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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.example.android.sunshine.app.data.AppConstants;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Utility {

    public static void sortData(Context context) {

        WeatherDbHelper mOpenHelper;
        mOpenHelper = new WeatherDbHelper(context);
        Integer COLUMN_ID = 0;
        Integer COLUMN_DOG_NAME = 1;
        Integer COLUMN_DOG_WALKING_COLOR = 2;
        Integer COLUMN_DOG_LOCATION = 3;
        Integer COLUMN_DOG_KENNEL_NUMBER = 4;
        Integer COLUMN_DOG_PLAYGROUP = 5;
        Integer COLUMN_DOG_GENDER = 6;
        Integer COLUMN_DOG_WALK_AM = 7;
        Integer COLUMN_DOG_WALK_PM = 8;
        Integer COLUMN_DOG_OFFICE = 9;
        Integer COLUMN_DOG_VISITOR = 10;
        Integer COLUMN_DOG_VOLUNTEER_ROOM = 11;
        Integer COLUMN_DOG_ADVENTURE_TAILS = 12;



        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
    // Assuming 'database' is your SQLiteDatabase instance

        // 1. Delete existing data (use with caution!)
//        database.delete(WeatherContract.DogEntry.TABLE_NAME, null, null);

        // 2. Retrieve sorted data
    //    WeatherContract.DogEntry.COLUMN_DOG_LOCATION + " desc, " + WeatherContract.DogEntry.COLUMN_DOG_KENNEL_NUMBER + " asc"
        String selectQuery = "SELECT * FROM " + WeatherContract.DogEntry.TABLE_NAME + " ORDER BY " + WeatherContract.DogEntry.COLUMN_DOG_LOCATION
                + " desc, " + WeatherContract.DogEntry.COLUMN_DOG_KENNEL_NUMBER + " asc";
        MyLogger.d("sunshine", "utility sortdata sql query: " + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        Cursor newCursor = cursor;
        MyLogger.d("sunshine", "utility sortdata newcursor rows before delete: " + newCursor.getCount());
                // 1. Delete existing data (use with caution!)
        Integer rowsDeleted = database.delete(WeatherContract.DogEntry.TABLE_NAME, null, null);
//        selectQuery = "UPDATE sqlite_sequence SET seq = 0 WHERE name = " + WeatherContract.DogEntry.TABLE_NAME;
//        newCursor = database.rawQuery(selectQuery, null);

//        MyLogger.d("sunshine", "utility sortdata newcursor rows after delete: " + newCursor.getCount());
        Vector<ContentValues> cVVector = new Vector<ContentValues>(newCursor.getCount());


        context.getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
        MyLogger.d("sunshine", "utility sortdata rows deleted: " + rowsDeleted);
        MyLogger.d("sunshine", "utility sortdata cursor column count before while loop: " + cursor.getColumnCount());
        MyLogger.d("sunshine", "utility sortdata rows after delete: " + cursor.getCount());

//        for(int i = 0; i < cursor.getColumnCount(); i++) {
//            MyLogger.d("sunshine", "utility sortdata cursor column " + i + " name: " + cursor.getColumnName(i));
//        }
        // 3. Insert data back in sorted order
        int i = 0;
        if (cursor.moveToFirst()) {
//            cursor.moveToFirst();
            ContentValues values = new ContentValues();
//            for(int i = 0; i < cursor.getColumnCount(); i++) {
//                MyLogger.d("sunshine", "utility sortdata cursor column " + i + " name: " + cursor.getColumnName(i));
////                MyLogger.d("sunshine", "utility sortdata cursor column value: " + i + " id: " + cursor.getInt(cursor.getColumnIndex("_id")));
//
//            }
            do {
//                MyLogger.d("sunshine", "utility sortdata cursor  in while loop column count: " + cursor.getColumnCount());
//                MyLogger.d("sunshine", "utility sortdata cursor in while loop column 0 name: " + cursor.getColumnName(0));
                // Extract data from cursor and create ContentValues

                // Populate values based on your table columns

                // e.g., values.put("column_name", cursor.getString(cursor.getColumnIndex("column_name")));

//                values.put(WeatherContract.DogEntry.COLUMN_ID, cursor.getInt(COLUMN_ID));
//                MyLogger.d("sunshine", "utility sortdata inside while loop id: " + cursor.getInt(COLUMN_ID));

//                values.put(WeatherContract.DogEntry.COLUMN_ID, cursor.getInt(0));

                values.put(WeatherContract.DogEntry.COLUMN_DOG_NAME, cursor.getString(COLUMN_DOG_NAME));
                MyLogger.d("sunshine", "utility sortdata inside while loop name: " + cursor.getString(COLUMN_DOG_NAME));

                values.put(WeatherContract.DogEntry.COLUMN_DOG_WALKING_COLOR, cursor.getString(COLUMN_DOG_WALKING_COLOR));
                MyLogger.d("sunshine", "utility sortdata inside while loop walking color: " + cursor.getString(COLUMN_DOG_WALKING_COLOR));

                values.put(WeatherContract.DogEntry.COLUMN_DOG_LOCATION, cursor.getString(COLUMN_DOG_LOCATION));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_KENNEL_NUMBER, cursor.getInt(COLUMN_DOG_KENNEL_NUMBER));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_PLAYGROUP, cursor.getInt(COLUMN_DOG_PLAYGROUP));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_GENDER, cursor.getString(COLUMN_DOG_GENDER));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_WALK_AM, cursor.getInt(COLUMN_DOG_WALK_AM));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_WALK_PM, cursor.getInt(COLUMN_DOG_WALK_PM));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_OFFICE, cursor.getInt(COLUMN_DOG_OFFICE));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_VISITOR, cursor.getInt(COLUMN_DOG_VISITOR));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_VOLUNTEER_ROOM, cursor.getInt(COLUMN_DOG_VOLUNTEER_ROOM));
                values.put(WeatherContract.DogEntry.COLUMN_DOG_ADVENTURE_TAILS, cursor.getInt(COLUMN_DOG_ADVENTURE_TAILS));
                cVVector.add(values);
                Long inserted = database.insert(WeatherContract.DogEntry.TABLE_NAME, null, values);
                MyLogger.d("sunshine", "utility sortdata inside while loop inserted: " + inserted);
//                String selection = WeatherContract.DogEntry.TABLE_NAME+
//                        "." + WeatherContract.DogEntry.COLUMN_ID + " = ? ";
//                String[] selectionArgs = new String[]{String.valueOf(cursor.getInt(i+6))};
//                int updated = database.update(WeatherContract.DogEntry.TABLE_NAME, values, null, null);
//                MyLogger.d("sunshine", "utility sortdata inside while loop inserted: " + updated);
                if (i == 0)
                    AppConstants.GLOBAL_OFFSET = inserted.intValue();
                i++;
            } while (cursor.moveToNext());
        }
//        if ( cVVector.size() > 0 ) {
//            ContentValues[] cvArray = new ContentValues[cVVector.size()];
//            cVVector.toArray(cvArray);
//            context.getContentResolver().bulkInsert(WeatherContract.DogEntry.CONTENT_URI, cvArray);
//
//            // delete old data so we don't build up an endless history
////                getContext().getContentResolver().delete(WeatherContract.DogEntry.CONTENT_URI,
////                        WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
////                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});
//
////            notifyWeather();
//        }

        MyLogger.d("sunshine", "Sortdata Sync Complete. " + cVVector.size() + " Inserted");
        cursor.close();
        newCursor.close();

        database.close();
        context.getContentResolver().notifyChange(WeatherContract.DogEntry.CONTENT_URI, null);
    }

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(Context context, double temperature) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        String suffix = "\u00B0";
        if (!isMetric(context)) {
            temperature = (temperature * 1.8) + 32;
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return a user-friendly representation of the date.
     */
//    public static String getFriendlyDayString(Context context, long dateInMillis) {
//        // The day string for forecast uses the following logic:
//        // For today: "Today, June 8"
//        // For tomorrow:  "Tomorrow"
//        // For the next 5 days: "Wednesday" (just the day name)
//        // For all days after that: "Mon Jun 8"
//
//        Time time = new Time();
//        time.setToNow();
//        long currentTime = System.currentTimeMillis();
//        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
//        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);
//
//        // If the date we're building the String for is today's date, the format
//        // is "Today, June 24"
//        if (julianDay == currentJulianDay) {
//            String today = context.getString(R.string.today);
//            int formatId = R.string.format_full_friendly_date;
//            return String.format(context.getString(
//                    formatId,
//                    today,
//                    getFormattedMonthDay(context, dateInMillis)));
//        } else if ( julianDay < currentJulianDay + 7 ) {
//            // If the input date is less than a week in the future, just return the day name.
//            return getDayName(context, dateInMillis);
//        } else {
//            // Otherwise, use the form "Mon Jun 3"
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(dateInMillis);
//        }
//    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, long dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat;
        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }
        return String.format(context.getString(windFormat), windSpeed, direction);
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }
}