package com.example.contracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class NewUserActivity extends AppCompatActivity {
    private static final int SEND_SMS_REQUEST = 100;
    private static final int READ_PHONE_NUMBERS_REQUEST = 200;

    private ConTrackerRepo repo;
    private final User mUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        repo = ConTrackerRepo.getInstance(this);

        EditText user = findViewById(R.id.new_user_username);
        EditText pass = findViewById(R.id.new_user_password);
        Intent intent = getIntent();
        // check if they tried a username & password
        if (intent.hasExtra("username") && intent.hasExtra("password")) {
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            user.setText(username);
            pass.setText(password);
        } else {
            user.setHint("Username");
            pass.setHint("Password");
        }
    }

    public void SignUp(View v) {
        // get widget data
        EditText userET = findViewById(R.id.new_user_username);
        EditText passET = findViewById(R.id.new_user_password);
        CheckBox SMSCheck = findViewById(R.id.new_user_sms);
        String user = userET.getText().toString();
        String pass = passET.getText().toString();
        // validate data
        // since all data is client-side,
        // injection attacks aren't a concern
        if (user.isEmpty() && pass.isEmpty()) {
            Toast.makeText(this, "Enter a username and password to continue", Toast.LENGTH_SHORT).show();
        } else if (user.isEmpty()) {
            Toast.makeText(this, "Enter a username to continue", Toast.LENGTH_SHORT).show();
        } else if (pass.isEmpty()) {
            Toast.makeText(this, "Enter a password to continue", Toast.LENGTH_SHORT).show();
        } else {
            if (repo.getUser(user) == null) {
                mUser.setUser(user);
                mUser.setPass(pass);
                if (SMSCheck.isChecked()) {
                    sendSMS();
                }
                if (!mUser.isValid()) {
                    Toast.makeText(this, "Fatal error creating user", Toast.LENGTH_LONG).show();
                    return;
                }
                repo.addUser(mUser);
                Intent viewConventionListIntent = new Intent(NewUserActivity.this, ConventionList.class);
                NewUserActivity.this.startActivity(viewConventionListIntent);
            } else {
                Toast.makeText(this, "Username taken, try again.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void sendSMS() {
        // check permissions for SMS and reading phone numbers
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_NUMBERS}, READ_PHONE_NUMBERS_REQUEST);
        } else {
            // Permission is granted, detect user's phone number
            SmsManager smsMan = getSystemService(SmsManager.class);
            SubscriptionManager subMan = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> subInfoList = subMan.getActiveSubscriptionInfoList();
            if (subInfoList != null && !subInfoList.isEmpty()) {
                SubscriptionInfo subInfo = subInfoList.getFirst();
                int subId = subInfo.getSubscriptionId();
                String userPhoneNum = subMan.getPhoneNumber(subId);
                // get data to send text
                // query is ordered by date
                // the first one is guaranteed to be the next one
                if (!repo.getConventions().isEmpty()) {
                    Convention nextConvention = repo.getConventions().getFirst();
                    Calendar convention = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    convention.setTimeInMillis(nextConvention.getStart());
                    Calendar current = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    long difference = convention.getTimeInMillis() - current.getTimeInMillis();
                    int daysUntilConvention = (int) Math.floor(((float) difference / (1000*60*60*24)));
                    // send text message
                    String msg = "You have " + daysUntilConvention +
                            " days until your next convention: " +
                            nextConvention.getTitle();
                    smsMan.sendTextMessage(userPhoneNum, null, msg, null, null);
                }
                mUser.setSMSPermission(Boolean.TRUE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SEND_SMS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            }
        } else if (requestCode == READ_PHONE_NUMBERS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            }
        }
    }
}