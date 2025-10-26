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

public class EditConventionActivity extends AppCompatActivity {
    ConTrackerRepo repo;
    Convention mConvention = new Convention();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_convention);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        repo = ConTrackerRepo.getInstance(this);

        // get list of state names
        List<State> states = repo.getStates();
        // sort alphabetically
        states.sort(Comparator.comparing(State::getName));
        ArrayList<String> stateNames = new ArrayList<String>();
        states.forEach(state -> { stateNames.add(state.getName()); });
        // put state names in spinner
        Spinner stateSpinner = findViewById(R.id.edit_convention_state_spinner);
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

        // get current convention data
        Intent intent = getIntent();
        if (intent.hasExtra("convention_id")) {
            long conventionId = intent.getLongExtra("convention_id", -1);
            mConvention = repo.getConvention(conventionId);
        } else {
            Toast.makeText(this, "Could not edit convention, data error.", Toast.LENGTH_LONG).show();
            Intent goBackIntent = new Intent(EditConventionActivity.this, ConventionList.class);
            EditConventionActivity.this.startActivity(goBackIntent);
            return;
        }
        EditText titleET = findViewById(R.id.edit_convention_title);
        EditText dateRangeET = findViewById(R.id.edit_convention_date_range);
        EditText addressET = findViewById(R.id.edit_convention_address);
        EditText cityET = findViewById(R.id.edit_convention_city);
        titleET.setText(mConvention.getTitle());
        addressET.setText(mConvention.getAddress());
        cityET.setText(mConvention.getCity());
        // dates stored in UTC, convert to local for display
        // start date
        Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        UTC.setTimeInMillis(mConvention.getStart());
        Calendar local = Calendar.getInstance(TimeZone.getDefault());
        local.set(
                UTC.get(Calendar.YEAR),
                UTC.get(Calendar.MONTH),
                UTC.get(Calendar.DATE),
                UTC.get(Calendar.HOUR_OF_DAY),
                UTC.get(Calendar.MINUTE)
        );
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String startStr = format.format(local.getTime());
        // end date
        UTC.setTimeInMillis(mConvention.getEnd());
        local.set(
                UTC.get(Calendar.YEAR),
                UTC.get(Calendar.MONTH),
                UTC.get(Calendar.DATE),
                UTC.get(Calendar.HOUR_OF_DAY),
                UTC.get(Calendar.MINUTE)
        );
        String endStr = format.format(local.getTime());
        String dateRange = startStr + "\n- " + endStr;
        dateRangeET.setText(dateRange);
    }
    public void SaveConvention(View v) {
        // get string data
        EditText titleET = findViewById(R.id.edit_convention_title);
        String title = titleET.getText().toString();
        EditText addressET = findViewById(R.id.edit_convention_address);
        String address = addressET.getText().toString();
        EditText cityET = findViewById(R.id.edit_convention_city);
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
                Toast.makeText(this, "Fatal error updating convention", Toast.LENGTH_LONG).show();
                return;
            }
            repo.updateConvention(mConvention);
            Intent newConventionIntent = new Intent (EditConventionActivity.this, ConventionList.class);
            EditConventionActivity.this.startActivity(newConventionIntent);
        }
    }
}