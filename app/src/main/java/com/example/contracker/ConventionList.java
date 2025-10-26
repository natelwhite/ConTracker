package com.example.contracker;

import static android.util.Log.INFO;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.GridView;
import android.widget.Toast;

import java.util.List;

public class ConventionList extends AppCompatActivity {
    ConTrackerRepo repo;
    public static final int NEW_CONVENTION_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = ConTrackerRepo.getInstance(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_convention_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get gridview & set adapter
        GridView conventionsGridView = findViewById(R.id.convention_list);
        ConventionAdapter conAdapter = new ConventionAdapter(this, R.layout.activity_convention_view, repo.getConventions());
        conventionsGridView.setAdapter(conAdapter);
        conventionsGridView.setOnItemClickListener((parent, view, position, id) -> {
            Convention con = repo.getConventions().get(position);
            Intent viewEventListIntent = new Intent(ConventionList.this, EventList.class);
            viewEventListIntent.putExtra("convention_id", con.getId());
            ConventionList.this.startActivity(viewEventListIntent);
        });
    }
    public void NewConvention(View v) {
        Intent newConventionIntent = new Intent(ConventionList.this, NewConventionActivity.class);
        ConventionList.this.startActivity(newConventionIntent);
    }
}