package com.and.ibrahim.teleprompter.modules.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.modules.listContents.ListContentsActivity;
import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import static com.and.ibrahim.teleprompter.data.Contract.OPEN_ALL_ADS;
import static com.and.ibrahim.teleprompter.data.Contract.OPEN_FULL_SCREEN_ADS;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private Intent mIntent;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    boolean isTablet;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isTablet = getResources().getBoolean(R.bool.isTablet);
       /* MobileAds.initialize(this, initializationStatus -> {
        });
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Config params updated: " +updated);
                            Log.d(TAG, "Config params fullScreen ads: " + mFirebaseRemoteConfig.getBoolean(OPEN_FULL_SCREEN_ADS));
                            Log.d(TAG, "Config params all ads: " + mFirebaseRemoteConfig.getBoolean(OPEN_ALL_ADS));

                            SharedPrefManager.getInstance(SplashActivity.this).setFullScreenAdsOn( mFirebaseRemoteConfig.getBoolean(OPEN_FULL_SCREEN_ADS));


                        } else {
                            Log.d(TAG, "Fetch Config params failed: ");
                        }
                        goToMain();
                    }
                });*/
//        // Initialize a dummy banner using the default test banner id provided by google to get the device id from logcat using 'Ads' tag
      // ConsentSDK.initDummyBanner(this);

        // Initialize ConsentSDK
        // Loading indicator
        goToMain();
        loadingHandler();
    }

    private void startInit(){
        if(!SharedPrefManager.getInstance(SplashActivity.this).isInitedSdk()){
            ConsentSDK consentSDK = new ConsentSDK.Builder(SplashActivity.this)
                    .addTestDeviceId("your device id from logcat") // Add your test device id "Remove addTestDeviceId on production!"
                    .addCustomLogTag("CUSTOM_TAG") // Add custom tag default: ID_LOG
                    .addPrivacyPolicy("https://teleprompter-app/teleprompter") // Add your privacy policy url
                    .addPublisherId("pub-2142834117997426") // Add your admob publisher id
                    .build();
            Log.i(TAG, "isInitedSdk = "+SharedPrefManager.getInstance(SplashActivity.this).isInitedSdk());

            // To check the consent and to move to MainActivity after everything is fine :).
            consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {
                @Override
                public void onResult(boolean isRequestLocationInEeaOrUnknown) {

                    if(isRequestLocationInEeaOrUnknown){
                        Log.i(TAG, "isRequestLocationInEeaOrUnknown 1= "+ true);

                        goToMain();
                        SharedPrefManager.getInstance(SplashActivity.this).setInitedSdk(true);
                    }else {
                        Log.i(TAG, "isRequestLocationInEeaOrUnknown 2= "+ false);

                        finish();
                        System.exit(0);

                    }

                }
            });
        }else{
            Log.i(TAG, "isInitedSdk = "+SharedPrefManager.getInstance(SplashActivity.this).isInitedSdk());
            goToMain();
        }
    }
    // Go to MainActivity
    private void goToMain() {
        // Wait few seconds just to show my stunning loading indication, you like it right :P.
        new Handler().postDelayed(() -> {
            if (isTablet) {
                mIntent = new Intent(SplashActivity.this, DisplayActivity.class);
            } else {
                mIntent = new Intent(SplashActivity.this, ListContentsActivity.class);

            }
             startActivity(mIntent);
            //finish();
        }, 1000);
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
                handler.postDelayed(this, 200);
            }
        };
        handler.postDelayed(runnable, 500);
    }
}

