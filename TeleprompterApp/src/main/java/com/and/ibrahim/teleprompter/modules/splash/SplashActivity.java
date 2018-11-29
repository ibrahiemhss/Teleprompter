package com.and.ibrahim.teleprompter.modules.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.modules.listContents.ListContentsActivity;
import com.ayoubfletcher.consentsdk.ConsentSDK;

import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private Intent mIntent;

    boolean isTablet;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isTablet = getResources().getBoolean(R.bool.isTablet);


//        // Initialize a dummy banner using the default test banner id provided by google to get the device id from logcat using 'Ads' tag
//        ConsentSDK.initDummyBanner(this);

        // Initialize ConsentSDK
        ConsentSDK consentSDK = new ConsentSDK.Builder(this)
                .addTestDeviceId("your device id from logcat") // Add your test device id "Remove addTestDeviceId on production!"
                .addCustomLogTag("CUSTOM_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy("https://sites.google.com/view/teleprompter-app/home/teleprompter") // Add your privacy policy url
                .addPublisherId("pub-2142834117997426") // Add your admob publisher id
                .build();

        // To check the consent and to move to MainActivity after everything is fine :).
        consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {
            @Override
            public void onResult(boolean isRequestLocationInEeaOrUnknown) {
                goToMain();
            }
        });

        // Loading indicator
        loadingHandler();
    }

    // Go to MainActivity
    private void goToMain() {
        // Wait few seconds just to show my stunning loading indication, you like it right :P.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTablet) {
                    mIntent = new Intent(SplashActivity.this, DisplayActivity.class);
                } else {
                    mIntent = new Intent(SplashActivity.this, ListContentsActivity.class);

                }
                 startActivity(mIntent);
                //finish();
            }
        }, 3000);
    }

    /**
     * Some stuff to tell that your app is loading and it's not lagging.
     */
    // Loading indicator handler
    private void loadingHandler() {
        final TextView loadingTxt = findViewById(R.id.loadingTxt);
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Loading Txt
                if(loadingTxt.getText().length() > 10) {
                    loadingTxt.setText("Loading ");
                } else {
                    loadingTxt.setText(loadingTxt.getText()+".");
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(runnable, 500);
    }
}

