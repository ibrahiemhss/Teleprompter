package com.and.ibrahim.teleprompter.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.callback.OnActionAd;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Objects;

import static com.and.ibrahim.teleprompter.data.Contract.INTERSTITAI_ADS;
import static com.and.ibrahim.teleprompter.data.Contract.INTERSTITAI_AD_ACTION;
import static com.and.ibrahim.teleprompter.data.Contract.REAWRDED_ADS;
import static com.and.ibrahim.teleprompter.data.Contract.REAWRDED_AD_ACTION;

public class AdsUtils {

    private static final String TAG = "AddsUtils";
    private final Context mContext;
    private final AdRequest mAdRequest;
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    private boolean mFullScreenAdFirstShown = false;
    final private Handler handler = new Handler();
    private int mTime;
    private final OnActionAd onActionAd;

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "mShowAddAfter =" + mTime);
            handler.postDelayed(this, mTime);
        }
    };

    public AdsUtils(Context mContext, OnActionAd onCloseAd) {
        this.mContext = mContext;
        this.onActionAd = onCloseAd;
        mAdRequest = new AdRequest.Builder().build();
        MobileAds.initialize(mContext, initializationStatus -> {
        });
    }

    public void initializeBannerAd(AdView adView) {

        adView.loadAd(mAdRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i(TAG, "AdView onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.i(TAG, "AdView LoadAdError== " + adError.toString());

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i(TAG, "AdView onAdOpened");

            }

            @Override
            public void onAdClicked() {
                Log.i(TAG, "AdView onAdClicked");

                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                Log.i(TAG, "AdView onAdClosed");

                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    public void initializeInterstitialAd() {

        InterstitialAd.load(mContext, mContext.getResources().getString(R.string.interstitial_pub), mAdRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d(TAG, "The ad was dismissed.");
                        onActionAd.onClose(INTERSTITAI_AD_ACTION);
                        //loadingAddsHandler(60000);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d(TAG, "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d(TAG, "The ad was shown.");
                    }
                });
                if (!mFullScreenAdFirstShown) {
                    Log.d(TAG, "interstitialAd show onAdLoaded");
                    //loadingAddsHandler(10000);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

    }

    public void initializeRewardedAd() {

        RewardedAd.load(mContext, mContext.getResources().getString(R.string.rewarded_pub),
                mAdRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, "rewardedAd onAdFailedToLoad " + Objects.requireNonNull(loadAdError.getResponseInfo()).toString());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    Log.i(TAG, "rewardedAd Ad was shown");
                                    SharedPrefManager.getInstance(mContext).setFullScreenAdShown(true);
                                    mRewardedAd = null;
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when ad fails to show.
                                    Log.i(TAG, "onAdFailedToShowFullScreenContent = " + adError);
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Don't forget to set the ad reference to null so you
                                    // don't show the ad a second time.
                                    //loadingAddsHandler(2400000);
                                    Log.i(TAG, "onAdDismissedFullScreenContent");
                                    onActionAd.onClose(REAWRDED_AD_ACTION);
                                }
                            });

                        if (!mFullScreenAdFirstShown) {
                            Log.d(TAG, "rewardedAd show onAdLoaded");
                           // showAdd();
                        }
                        Log.d(TAG, "rewardedAd onAdLoaded");
                    }

                });

    }

    public void showAdd(int type) {

        if (type == INTERSTITAI_ADS) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) mContext);
                mFullScreenAdFirstShown = true;
            } else {
                Log.d(TAG, "The interstitial ad wasn't ready yet.");
            }
        } else if (type == REAWRDED_ADS) {
            if (mRewardedAd != null) {
                mRewardedAd.show((Activity) mContext, rewardItem -> {
                    // Handle the reward.
                    mFullScreenAdFirstShown = true;
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                });
                mFullScreenAdFirstShown = true;
            }
        }
    }

    private void loadingAddsHandler(int time) {
        mTime=time;
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, time);
    }

    public void dispose(String lifeStatus){
        SharedPrefManager.getInstance(mContext).setFullScreenAdShown(false);
        Log.d(TAG, "adds dispose = "+lifeStatus+" is shown ="+SharedPrefManager.getInstance(mContext).isFullScreenAdShown());
        handler.removeCallbacks(runnable);
    }
}
