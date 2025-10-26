package com.example.contracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {
    private static final int SEND_SMS_REQUEST = 100;
    private static final int READ_PHONE_NUMBERS_REQUEST = 200;
    private ConTrackerRepo repo;
    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        repo = ConTrackerRepo.getInstance(this);
    }

    public void Login(View v) {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        Intent newUserIntent = new Intent(LoginActivity.this, NewUserActivity.class);
        if (!usernameStr.isEmpty() && !passwordStr.isEmpty()) {
            ConTrackerRepo repo = ConTrackerRepo.getInstance(getApplicationContext());
            mUser = repo.getUser(usernameStr);
            if (mUser != null) {
                if (mUser.getPass().compareTo(passwordStr) == 0) {
                    // login success
                    if (mUser.getSMSPermission()) {
                        // user may decide to change preferences
                        if (!sendSMS()) {
                            mUser.setSMSPermission(Boolean.FALSE);
                        }
                    }
                    Intent viewConventionListIntent = new Intent(LoginActivity.this, ConventionList.class);
                    LoginActivity.this.startActivity(viewConventionListIntent);
                } else {
                    Toast.makeText(this, "Invalid password, try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                newUserIntent.putExtra("username", usernameStr);
                newUserIntent.putExtra("password", passwordStr);
                LoginActivity.this.startActivity(newUserIntent);
            }
        } else {
            LoginActivity.this.startActivity(newUserIntent);
        }
    }
    private Boolean sendSMS() {
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
                    int daysUntilConvention = (int) Math.floor(((float) difference / (1000 * 60 * 60 * 24)));
                    // send text message
                    String msg = "You have " + daysUntilConvention +
                            " days until your next convention: " +
                            nextConvention.getTitle();
                    smsMan.sendTextMessage(userPhoneNum, null, msg, null, null);
                }
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
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