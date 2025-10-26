package com.example.contracker;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NewConventionActivity extends AppCompatActivity {
    ConTrackerRepo repo;
    Convention mConvention = new Convention();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_convention);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        repo = ConTrackerRepo.getInstance(this);
        // get list of state names
        List<State> states = repo.getStates();
        // sort alphabetically
        states.sort(new Comparator<State>() {
            @Override
            public int compare(State lhs, State rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        ArrayList<String> stateNames = new ArrayList<String>();
        states.forEach(state -> { stateNames.add(state.getName()); });
        // put state names in spinner
        Spinner stateSpinner = findViewById(R.id.new_convention_state_spinner);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateNames);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        // spinner event listener
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "A state must be selected before continuing", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                State state = states.get(position);
                mConvention.setStateId(state.getId());
                Toast.makeText(getApplicationContext(), state.getName() + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        // SelectDateRangeBtn listener
        findViewById(R.id.new_convention_date_range).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create date picker
                MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                        MaterialDatePicker.Builder.dateRangePicker();
                MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

                // picker result event listener
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    // set result
                    mConvention.setStart(selection.first);
                    mConvention.setEnd(selection.second);
                    // stored in UTC, convert to local for display
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTimeZone(TimeZone.getTimeZone("UTC"));
                    selectedDate.setTimeInMillis(mConvention.getStart());
                    Calendar local = Calendar.getInstance();
                    local.setTimeZone(TimeZone.getDefault());
                    local.set(
                            selectedDate.get(Calendar.YEAR),
                            selectedDate.get(Calendar.MONTH),
                            selectedDate.get(Calendar.DATE),
                            selectedDate.get(Calendar.HOUR_OF_DAY),
                            selectedDate.get(Calendar.MINUTE)
                    );
                    String startStr = dateFormat.format(local.getTime());
                    selectedDate.setTimeInMillis(mConvention.getEnd());
                    local.set(
                            selectedDate.get(Calendar.YEAR),
                            selectedDate.get(Calendar.MONTH),
                            selectedDate.get(Calendar.DATE),
                            selectedDate.get(Calendar.HOUR_OF_DAY),
                            selectedDate.get(Calendar.MINUTE)
                    );
                    String endStr = dateFormat.format(local.getTime());
                    String datePickerResultStr = startStr + "\n- " + endStr;
                    EditText datePickerResult = findViewById(R.id.new_convention_date_range);
                    datePickerResult.setText(datePickerResultStr);
                });
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });
    }
    public void NewConvention(View v) {
        // get string data
        EditText titleET = findViewById(R.id.new_convention_title);
        String title = titleET.getText().toString();
        EditText addressET = findViewById(R.id.new_convention_address);
        String address = addressET.getText().toString();
        EditText cityET = findViewById(R.id.new_convention_city);
        String city = cityET.getText().toString();
        // validate data
        // since all data is client-side,
        // injection attacks aren't a concern
        if (title.isEmpty()) {
            Toast.makeText(this, "Please provide a title", Toast.LENGTH_SHORT).show();
        } else if (address.isEmpty()) {
            Toast.makeText(this, "Please provide an address", Toast.LENGTH_SHORT).show();
        } else if (city.isEmpty()) {
            Toast.makeText(this, "Please provide a city", Toast.LENGTH_SHORT).show();
        } else {
            mConvention.setTitle(title);
            mConvention.setAddress(address);
            mConvention.setCity(city);
            if (!mConvention.isValid()) {
                Toast.makeText(this, "Fatal error creating convention", Toast.LENGTH_LONG).show();
                return;
            }
            repo.addConvention(mConvention);
            Intent newConventionIntent = new Intent (NewConventionActivity.this, ConventionList.class);
            NewConventionActivity.this.startActivity(newConventionIntent);
        }
    }
}