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

public class NewEventActivity extends AppCompatActivity {
    ConTrackerRepo repo;
    Event mEvent = new Event();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = ConTrackerRepo.getInstance(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get convention id associated with this event
        Intent intent = getIntent();
        // check if they tried a username & password
        if (intent.hasExtra("convention_id")) {
            mEvent.setConventionId(intent.getLongExtra("convention_id", -1));
        } else {
            Intent goBackIntent = new Intent(NewEventActivity.this, EventList.class);
            NewEventActivity.this.startActivity(goBackIntent);
        }
        // date dialog event listener
        EditText dateET = findViewById(R.id.new_event_date);
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        // start time dialog event listener
        EditText startTimeET = findViewById(R.id.new_event_start);
        startTimeET.setOnClickListener(v -> {
            // get current time to set dialog with
            Calendar current = Calendar.getInstance();
            int hour = current.get(Calendar.HOUR_OF_DAY);
            int minute = current.get(Calendar.MINUTE);
            // create dialog with event listener
            TimePickerDialog timePickerDialog = new TimePickerDialog(NewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // save result
                    mEvent.setStart(timeFormatter(hourOfDay, minute));
                    // update display
                    startTimeET.setText(mEvent.getStart());
                }
            }, hour, minute, false);
            timePickerDialog.setTitle("Event Start Time");
            timePickerDialog.show();
        });
        // end time dialog event listener
        EditText endTimeET = findViewById(R.id.new_event_end);
        endTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current time
                Calendar current = Calendar.getInstance();
                int hour = current.get(Calendar.HOUR_OF_DAY);
                int minute = current.get(Calendar.MINUTE);
                // create dialog with event listener
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // save result
                        mEvent.setEnd(timeFormatter(hourOfDay, minute));
                        // update display
                        endTimeET.setText(mEvent.getEnd());
                    }
                }, hour, minute, false);
                timePickerDialog.setTitle("Event End Time");
                timePickerDialog.show();
            }
        });
    }
    public void NewEvent(View v) {
        // get string data
        EditText titleET = findViewById(R.id.new_event_title);
        String title = titleET.getText().toString();
        EditText locationET = findViewById(R.id.new_event_location);
        String location = locationET.getText().toString();
        EditText descriptionET = findViewById(R.id.new_event_description);
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
                Toast.makeText(this, "Fatal error creating event", Toast.LENGTH_LONG).show();
                return;
            }
            repo.addEvent(mEvent);
            Intent eventListIntent = new Intent (NewEventActivity.this, EventList.class);
            eventListIntent.putExtra("convention_id", mEvent.getConventionId());
            NewEventActivity.this.startActivity(eventListIntent);
        }
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