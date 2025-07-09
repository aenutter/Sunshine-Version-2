package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link DogAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class DogAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
//        public final ImageView iconView;
        public final TextView nameView;
        public final TextView genderView;
        public final TextView walkAMView;
        public final TextView walkPMView;
        public final TextView officeView;
        public final TextView visitorView;
        public final TextView volunteerView;
        public final TextView adventureTailsView;
//        public final TextView playGroupView;

        public ViewHolder(View view) {
//            playGroupView = (TextView) view.findViewById(R.id.list_item_playgroup);
//            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            nameView = (TextView) view.findViewById(R.id.list_item_dog_name);
            genderView = (TextView) view.findViewById(R.id.list_item_dog_gender);
            walkAMView = (TextView) view.findViewById(R.id.list_item_dog_walk_am);
            walkPMView = (TextView) view.findViewById(R.id.list_item_dog_walk_pm);
            officeView = (TextView) view.findViewById(R.id.list_item_dog_office);
            visitorView = (TextView) view.findViewById(R.id.list_item_dog_visitor);
            volunteerView = (TextView) view.findViewById(R.id.list_item_dog_volunteer_room);
            adventureTailsView = (TextView) view.findViewById(R.id.list_item_dog_adventure_tails);
        }
    }

    public DogAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item_dog;
//        switch (viewType) {
//            case VIEW_TYPE_TODAY: {
//                layoutId = R.layout.list_item_forecast_today;
//                break;
//            }
//            case VIEW_TYPE_FUTURE_DAY: {
//                layoutId = R.layout.list_item_forecast;
//                break;
//            }
//        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());

        // Read weather forecast from cursor
        String name = cursor.getString(DogFragment.COL_DOG_NAME);
        // Find TextView and set weather forecast on it
        viewHolder.nameView.setText(name);

        String gender = cursor.getString(DogFragment.COL_DOG_GENDER);
        // For accessibility, add a content description to the icon field
        viewHolder.genderView.setText(gender);

        Boolean walkAM = (cursor.getInt(DogFragment.COL_DOG_WALK_AM) == 1);
        if (walkAM) viewHolder.walkAMView.setText("Walk AM"); else viewHolder.walkAMView.setText("");
//        (walkAM) ? viewHolder.walkAMView.setText("Walk AM") : viewHolder.walkAMView.setText("Walk AM");

        Boolean walkPM = (cursor.getInt(DogFragment.COL_DOG_WALK_PM) == 1);
        if (walkPM) viewHolder.walkAMView.setText("Walk PM"); else viewHolder.walkPMView.setText("");

        Boolean office = (cursor.getInt(DogFragment.COL_DOG_OFFICE) == 1);
        if (office) viewHolder.officeView.setText("Office"); else viewHolder.officeView.setText("");

        Boolean visitor = (cursor.getInt(DogFragment.COL_DOG_VISITOR) == 1);
        if (visitor) viewHolder.visitorView.setText("Visitor Room"); else viewHolder.visitorView.setText("");

        Boolean volunteer = (cursor.getInt(DogFragment.COL_DOG_VOLUNTEER_ROOM) == 1);
        if (volunteer) viewHolder.volunteerView.setText("Volunteer Room"); else viewHolder.volunteerView.setText("");

        Boolean adventureTails = (cursor.getInt(DogFragment.COL_DOS_ADVENTURE_TAILS) == 1);
        if (adventureTails) viewHolder.adventureTailsView.setText("Adventure Tails"); else viewHolder.adventureTailsView.setText("");
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}