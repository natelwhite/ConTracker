package com.example.contracker;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ConventionAdapter extends ArrayAdapter<Convention> {
    List<Convention> conventions;
    int custom_layout_id;
    public ConventionAdapter(@NonNull Context ctx, int resource, @NonNull List<Convention> conventions) {
        super(ctx, resource, conventions);
        this.conventions = conventions;
        custom_layout_id = resource;
    }
    @Override
    public int getCount() { return conventions.size(); }
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
        TextView title = view.findViewById(R.id.convention_title);
        TextView date = view.findViewById(R.id.convention_date);
        TextView location = view.findViewById(R.id.convention_location);

        // get data
        ConTrackerRepo repo = ConTrackerRepo.getInstance(getContext());
        Convention con = conventions.get(position);

        // set widget data
        title.setText(con.getTitle());
        Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        UTC.setTimeInMillis(con.getStart());
        Calendar local = Calendar.getInstance();
        local.setTimeZone(TimeZone.getDefault());
        local.set(
                UTC.get(Calendar.YEAR),
                UTC.get(Calendar.MONTH),
                UTC.get(Calendar.DATE),
                UTC.get(Calendar.HOUR_OF_DAY),
                UTC.get(Calendar.MINUTE)
        );
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        date.setText(dateFormat.format(local.getTime()));
        String locationStr = con.getAddress() + '\n' + con.getCity() + ", " + repo.getState(con.getStateId()).getAbbreviation();
        location.setText(locationStr);
        return view;
    }
}
