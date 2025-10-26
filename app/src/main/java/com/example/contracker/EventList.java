package com.example.contracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EventList extends AppCompatActivity {
    Convention mConvention;
    ConTrackerRepo repo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = ConTrackerRepo.getInstance(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent.hasExtra("convention_id")) {
            long conventionId = intent.getLongExtra("convention_id", -1);
            mConvention = repo.getConvention(conventionId);
            setTitle(mConvention.getTitle());
            List<Event> events = repo.getEvents(mConvention.getId());
            // create gridview & set adapter
            GridView eventGridView = findViewById(R.id.event_list);
            EventAdapter eventAdapter = new EventAdapter(this, R.layout.activity_event_view, events);
            eventGridView.setAdapter(eventAdapter);
            eventGridView.setOnItemClickListener((parent, view, position, id) -> {
                long eventId = events.get(position).getId();
                Intent editEventIntent = new Intent(EventList.this, EditEventActivity.class);
                editEventIntent.putExtra("event_id", eventId);
                EventList.this.startActivity(editEventIntent);
            });
        } else {
            Toast.makeText(this, "Application Err: Bad data", Toast.LENGTH_SHORT).show();
            backToConventionsList();
        }
    }
    public void DeleteConvention(View v) {
        if (mConvention != null) {
            List<Event> events = repo.getEvents(mConvention.getId());
            // delete all events associated with convention
            for (Event event : events) {
                repo.deleteEvent(event);
            }
            repo.deleteConvention(mConvention);
        } else {
            Toast.makeText(this, "ERR: bad convention id", Toast.LENGTH_LONG).show();
        }
        backToConventionsList();
    }
    public void EditConventionActivity(View v) {
        if (mConvention != null) {
            Intent editConventionIntent = new Intent(EventList.this, EditConventionActivity.class);
            editConventionIntent.putExtra("convention_id", mConvention.getId());
            EventList.this.startActivity(editConventionIntent);
        } else {
            Toast.makeText(this, "ERR: bad convention id", Toast.LENGTH_LONG).show();
            backToConventionsList();
        }
    }
    public void NewEventActivity(View v) {
        if (mConvention != null) {
            Intent newEventIntent = new Intent(EventList.this, NewEventActivity.class);
            newEventIntent.putExtra("convention_id", mConvention.getId());
            EventList.this.startActivity(newEventIntent);
        } else {
            Toast.makeText(this, "ERR: bad convention id", Toast.LENGTH_LONG).show();
            backToConventionsList();
        }
    }
    private void backToConventionsList() {
        Intent goBackIntent = new Intent(EventList.this, ConventionList.class);
        EventList.this.startActivity(goBackIntent);
    }
}