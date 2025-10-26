package com.example.contracker;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class EditEventActivity extends AppCompatActivity {
    ConTrackerRepo repo;
    Event mEvent = new Event();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        repo = ConTrackerRepo.getInstance(this);

        // get event data
        Intent intent = getIntent();
        if (intent.hasExtra("event_id")) {
            long eventId = intent.getLongExtra("event_id", -1);
            mEvent = repo.getEvent(eventId);
        } else {
            Toast.makeText(this, "Could not edit event, data error.", Toast.LENGTH_LONG).show();
            Intent goBackIntent = new Intent(EditEventActivity.this, EventList.class);
            EditEventActivity.this.startActivity(goBackIntent);
            return;
        }
        EditText titleET = findViewById(R.id.edit_event_title);
        EditText startET = findViewById(R.id.edit_event_start);
        EditText endET = findViewById(R.id.edit_event_end);
        EditText locationET = findViewById(R.id.edit_event_location);
        EditText descriptionET = findViewById(R.id.edit_event_description);
        titleET.setText(mEvent.getTitle());
        startET.setText(mEvent.getStart());
        endET.setText(mEvent.getEnd());
        locationET.setText(mEvent.getLocation());
        descriptionET.setText(mEvent.getDescription());
        // date dialog event listener
        EditText dateET = findViewById(R.id.edit_event_date);
        dateET.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> datePickerBuilder = MaterialDatePicker.Builder.datePicker();
            datePickerBuilder.setSelection(Calendar.getInstance().getTimeInMillis());
            datePickerBuilder.setTitleText("Event Date");
            MaterialDatePicker<Long> datePicker = datePickerBuilder.build();
            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    // save result
                    mEvent.setDate(selection);
                    // stored in UTC time, convert to local for display
                    Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    UTC.setTimeInMillis(selection);
                    Calendar local = Calendar.getInstance(TimeZone.getDefault());
                    local.set(
                            UTC.get(Calendar.YEAR),
                            UTC.get(Calendar.MONTH),
                            UTC.get(Calendar.DATE),
                            UTC.get(Calendar.HOUR_OF_DAY),
                            UTC.get(Calendar.MINUTE)
                    );
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    dateET.setText(format.format(local.getTime()));
                }
            });
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        // start time dialog event listener
        startET.setOnClickListener(v -> {
            // get current time to set dialog with
            Calendar current = Calendar.getInstance();
            int hour = current.get(Calendar.HOUR_OF_DAY);
            int minute = current.get(Calendar.MINUTE);
            // create dialog with event listener
            TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // save result
                    mEvent.setStart(timeFormatter(hourOfDay, minute));
                    // update result
                    startET.setText(mEvent.getStart());
                }
            }, hour, minute, false);
            timePickerDialog.setTitle("Event Start Time");
            timePickerDialog.show();
        });
    }
    public void SaveEvent(View v) {
        // get string data
        EditText titleET = findViewById(R.id.edit_event_title);
        EditText locationET = findViewById(R.id.edit_event_location);
        EditText descriptionET = findViewById(R.id.edit_event_description);
        String title = titleET.getText().toString();
        String location = locationET.getText().toString();
        String description = descriptionET.getText().toString();
        // validate data
        // since all data is client side,
        // injection attacks aren't a concern
        if (title.isEmpty()) {
            Toast.makeText(this, "Please provide a title", Toast.LENGTH_SHORT).show();
        } else if (location.isEmpty()) {
            Toast.makeText(this, "Please provide a location", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Please provide a description", Toast.LENGTH_SHORT).show();
        } else {
            mEvent.setTitle(title);
            mEvent.setLocation(location);
            mEvent.setDescription(description);
            if (!mEvent.isValid()) {
                Toast.makeText(this, "Fatal error updating event", Toast.LENGTH_LONG).show();
                return;
            }
            repo.updateEvent(mEvent);
            Intent eventListIntent = new Intent (EditEventActivity.this, EventList.class);
            eventListIntent.putExtra("convention_id", mEvent.getConventionId());
            EditEventActivity.this.startActivity(eventListIntent);
        }
    }
    public void DeleteEvent(View v) {
        Intent eventListIntent = new Intent(EditEventActivity.this, EventList.class);
        eventListIntent.putExtra("convention_id", mEvent.getConventionId());
        repo.deleteEvent(mEvent);
        EditEventActivity.this.startActivity(eventListIntent);
    }
    private String timeFormatter(int hourOfDay, int minute) {
        String hourResult;
        String AMPMResult;
        String minuteResult;
        switch(hourOfDay) {
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                hourResult = "0" + (hourOfDay - 12);
                AMPMResult = "PM";
                break;
            default:
                hourResult = "0" + hourOfDay;
                AMPMResult = "AM";
        }
        switch(minute) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                minuteResult = "0" + minute;
                break;
            default:
                minuteResult = String.valueOf(minute);
        }
        return hourResult + ":" + minuteResult + " " + AMPMResult;
    }
}