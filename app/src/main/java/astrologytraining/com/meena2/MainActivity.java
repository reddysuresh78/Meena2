package astrologytraining.com.meena2;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import astrologytraining.com.horoscopegenerator.HoraGenerator;

import static android.os.Environment.MEDIA_MOUNTED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String MEENA2_IMAGE = "MEENA2.JPG";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String TIME_FORMAT = "kk:mm:ss";
    public static final String FILE_NAME = "FILE_NAME";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Map<String, String> mPlacesMap = new HashMap<>();

    private SimpleDateFormat mDateFormatter  = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private SimpleDateFormat mTimeFormatter =  new SimpleDateFormat(TIME_FORMAT, Locale.US);

    private ProgressDialog mProgressDialog;
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;

    private EditText mTxtDateOfBirth;
    private EditText mTxtTimeOfBirth;
    private AutoCompleteTextView mTxtBirthPlace;
    private EditText mTxtNativeName;
    private Button mBtnGenerate;
    private Button mBtnClear;

    private AdView mAdView;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mProgressDialog.isShowing() && mProgressDialog != null) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("9FE9315EC5961D7BCA2C8E3569A4C3F8")
                .build();
        mAdView.loadAd(adRequest);

        mTxtDateOfBirth = (EditText) findViewById(R.id.txtDateOfBirth);
        mTxtDateOfBirth.setInputType(InputType.TYPE_NULL);

        mTxtNativeName = (EditText) findViewById(R.id.txtNativeName);
        mTxtNativeName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });


        mTxtTimeOfBirth = (EditText) findViewById(R.id.txtTimeOfBirth);
        mTxtTimeOfBirth.setInputType(InputType.TYPE_NULL);

        Calendar newCalendar = Calendar.getInstance();

        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mTxtDateOfBirth.setText(mDateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(Calendar.HOUR_OF_DAY, hour);
                newTime.set(Calendar.MINUTE, minute);

                mTxtTimeOfBirth.setText(mTimeFormatter.format(newTime.getTime()));
            }

        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        mTxtDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Show your calender here
                    setDate();
                    mDatePickerDialog.show();
                } else {
                    // Hide your calender here
                    mDatePickerDialog.hide();
                }
            }
        });

        mTxtDateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // On button click show datepicker dialog
                setDate();
                mDatePickerDialog.show();
            }
        });

        mTxtTimeOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Show your calender here
                    setTime();
                    mTimePickerDialog.show();
                } else {
                    // Hide your calender here
                    mTimePickerDialog.hide();
                }
            }
        });

        mTxtTimeOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // On button click show datepicker dialog
                setTime();
                mTimePickerDialog.show();
            }
        });

        mTxtBirthPlace = (AutoCompleteTextView) findViewById(R.id.txtPlaceOfBirth);

        loadPlacesData();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,
                mPlacesMap.keySet().toArray(new String[mPlacesMap.size()]));
        mTxtBirthPlace.setThreshold(1);//will start working from first character
        mTxtBirthPlace.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        mBtnGenerate = (Button) findViewById(R.id.btnGenerate);

        mBtnClear = (Button) findViewById(R.id.btnClear);

        mBtnClear.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        clear();

                    }
                });

        mBtnGenerate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isValidInput()) {
                    return;
                }

                if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    Log.d("MAIN", "Media mounted");
                    if (!isStoragePermissionGranted())
                        return;

                    if (!isStorageReadPermissionGranted())
                        return;
                }

                String filePath = getExternalCacheDir().toString() + "/" + mTxtNativeName.getText().toString() + ".pdf";

                String timeOfBirth = mTxtDateOfBirth.getText().toString() + " " + mTxtTimeOfBirth.getText().toString();

                String birthPlace = mTxtBirthPlace.getText().toString();

                String birthPlaceCord = mPlacesMap.get(birthPlace);

                String[] values = birthPlaceCord.split("~");
                double latitude = getDoubleValue(values[1]);
                double longitude = getDoubleValue(values[2]);

//                filePath = getApplicationContext().getFilesDir().getPath().toString() + "/test.pdf";

                Log.d("MAIN", "Filepath " + filePath);
                try {


                    HoraGenerator generator = new HoraGenerator(
                            mTxtNativeName.getText().toString(),
                            longitude,
                            latitude,
                            timeOfBirth,
//                            timeOfBirth,
                            TimeZone.getTimeZone("Asia/Calcutta"),
                            birthPlace,
                            true);
                    generator.setLocal(true);
                    generator.setFilePath(filePath);
                    generator.setImageData(getImageData());

                    PDFGeneratorTask pdfGeneratorTask = new PDFGeneratorTask();
                    pdfGeneratorTask.execute(generator);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error while generating chart", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
//                Toast.makeText(getApplicationContext(), "Ad is loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
//                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
//                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
//                Toast.makeText(getApplicationContext(), "Ad is opened!", Toast.LENGTH_SHORT).show();
            }
        });
        clear();
        Log.d(TAG,"OnCreate complete");
    }

    private void clear(){
        mTxtNativeName.setText("");
        mTxtBirthPlace.setText("");
        Calendar newDate = Calendar.getInstance();
        mTxtDateOfBirth.setText(mDateFormatter.format(newDate.getTime()));
        mTxtTimeOfBirth.setText(mTimeFormatter.format(newDate.getTime()));
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void setDate() {
        String curDate = mTxtDateOfBirth.getText().toString();
        if (curDate != null && !curDate.isEmpty()) {
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(mDateFormatter.parse(curDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDatePickerDialog.getDatePicker().updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }
    }

    private void setTime() {
        String curDate = mTxtTimeOfBirth.getText().toString();
        if (curDate != null && !curDate.isEmpty()) {
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(mTimeFormatter.parse(curDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mTimePickerDialog.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        }
    }

    private class PDFGeneratorTask extends AsyncTask<HoraGenerator, Void, String> {

        @Override
        protected String doInBackground(HoraGenerator... generators) {

            try {
                generators[0].generate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return generators[0].getFilePath();
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            openShowFileActivity(result);
//            openFile(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                    //android.R.style.Theme_DeviceDefault_Dialog_Alert);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Generating...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setInverseBackgroundForced(true);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private boolean isValidInput() {
        String name = mTxtNativeName.getText().toString().trim();

        if (name.isEmpty()) {
            mTxtNativeName.setError(getResources().getString(R.string.error_name_blank));
            mTxtNativeName.requestFocus();
            return false;
        }

        String place = mTxtBirthPlace.getText().toString().trim();

        if (place.isEmpty()) {
            mTxtBirthPlace.setError(getResources().getString(R.string.error_place_blank));
            mTxtBirthPlace.requestFocus();
            return false;
        }

        if (mPlacesMap.get(place) == null) {
            mTxtBirthPlace.setError(getResources().getString(R.string.error_place_invalid));
            mTxtBirthPlace.requestFocus();
            return false;
        }

        return true;
    }

    private double getDoubleValue(String value) {
        //Example 24#19
        String[] values = value.split("#");
        return Integer.parseInt(values[0]) + Integer.parseInt(values[1]) / 60.0D + 0 / 3600.0D;
    }

    private Map<String, byte[]> getImageData() {

        Log.d(TAG, "Start Image loading");
        Resources res = getApplicationContext().getResources();
        Drawable drawable = res.getDrawable(R.drawable.meena2);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();

        Map<String, byte[]> imageData = new HashMap<String, byte[]>();
        imageData.put(MEENA2_IMAGE, bitMapData);

        Log.d(TAG, "End Image loading");
        return imageData;
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            mBtnGenerate.performClick();
        }
    }

    public boolean isStorageReadPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void sendEmail(File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"reddysureshcmc@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
        intent.putExtra(Intent.EXTRA_TEXT, "body text");

        if (!file.exists() || !file.canRead()) {
            Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Uri uri = Uri.parse("file://" + file.getAbsolutePath());
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Send email..."));
    }

    private void openShowFileActivity(String filePath) {
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra(FILE_NAME, filePath);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void loadPlacesData() {

        Log.d(TAG, "Start String Array loading");
        Resources res = getResources();
        String[] places = res.getStringArray(R.array.city_names);
        Log.d(TAG, "Read String Array");
        for (String place : places) {
            String[] placeInfo = place.split("~");
            mPlacesMap.put(placeInfo[0], place);

        }
        Log.d(TAG, "End String Array loading");
    }

}
