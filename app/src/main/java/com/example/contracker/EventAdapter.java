package com.example.contracker;
import static android.util.Log.INFO;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventAdapter extends ArrayAdapter<Event> {
    List<Event> events;
    int custom_layout_id;
    public EventAdapter(@NonNull Context ctx, int resource, @NonNull List<Event> events) {
        super(ctx, resource, events);
        this.events = events;
        custom_layout_id = resource;
    }
    @Override
    public int getCount() { return events.size(); }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            // get reference to main layout & init
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(custom_layout_id, null);
        }

        // get widget layouts
        TextView title = view.findViewById(R.id.event_title);
        TextView date = view.findViewById(R.id.event_date);
        TextView duration = view.findViewById(R.id.event_duration);
        TextView location = view.findViewById(R.id.event_location);
        TextView desc = view.findViewById(R.id.event_description);

        // get data
        Event event = events.get(position);
        String titleStr = event.getTitle();
        String locationStr = event.getLocation();
        // date stored in UTC, convert to local
        Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        UTC.setTimeInMillis(event.getDate());
        Calendar local = Calendar.getInstance(Locale.getDefault());
        local.set(
                UTC.get(Calendar.YEAR),
                UTC.get(Calendar.MONTH),
                UTC.get(Calendar.DATE),
                UTC.get(Calendar.HOUR_OF_DAY),
                UTC.get(Calendar.MINUTE)
        );
        SimpleDateFormat format = new SimpleDateFormat("MM/dd", Locale.getDefault());
        String dateStr = format.format(local.getTime());
        String startTime = event.getStart();
        String endTime = event.getEnd();
        String durationStr = startTime + " - " + endTime;
        String descStr = event.getDescription();

        // set widget data
        title.setText(titleStr);
        location.setText(locationStr);
        date.setText(dateStr);
        duration.setText(durationStr);
        desc.setText(descStr);
        return view;
    }
}
