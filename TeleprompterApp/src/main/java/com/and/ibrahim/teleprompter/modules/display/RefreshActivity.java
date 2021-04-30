package com.and.ibrahim.teleprompter.modules.display;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import com.and.ibrahim.teleprompter.data.Contract;

public class RefreshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, RefreshActivity.class);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Contract.EXTRA_TEXT, extras.getString(Contract.EXTRA_TEXT));
            startActivity(intent);
            Intent cameraIntent = new Intent(this, DisplayActivity.class);
            startActivity(cameraIntent);

        }

    }

}
