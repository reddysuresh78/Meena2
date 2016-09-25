package astrologytraining.com.meena2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG= "DisplayActivity";

    InterstitialAd mInterstitialAd;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        mFilePath = intent.getStringExtra(MainActivity.FILE_NAME);

        if(hasNetworkConnection(ConnectivityManager.TYPE_WIFI) || hasSpeedConnection("4G")){
            Log.d(TAG, "Showing Interstitial Ad due to WIFI/4G connectivity");
            showInterstitialAd();
        }else{
            Log.d(TAG, "Not Showing Generated file due to NO WIFI/4G connectivity");
            openFile();
        }
    }

    private void openFile() {

        File file = new File(mFilePath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }

    private void showInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);

         // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("9FE9315EC5961D7BCA2C8E3569A4C3F8")
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }

            @Override
            public void onAdClosed() {
                openFile();
//                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                openFile();
//                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
   }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private boolean hasNetworkConnection(int type) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni!= null) {
            if (ni.getType() == type  && (ni.isConnected() || ni.isConnectedOrConnecting())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSpeedConnection(String type) {

        if(!hasNetworkConnection(ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }

        TelephonyManager mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        String finalType = "";

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                finalType = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                finalType =  "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                finalType =  "4G";
                break;
            default:
                finalType = "Unknown";
                break;
        }
       if(finalType.equalsIgnoreCase(type)){
            return true;
        }
        return false;
    }

}
