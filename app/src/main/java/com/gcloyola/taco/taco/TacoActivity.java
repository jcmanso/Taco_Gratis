package com.gcloyola.taco.taco;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.DatePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;


public class TacoActivity extends AppCompatActivity {

    private Menu menu;
    private final String URL = "https://tacomensajero.com?view=mobile";
    private AdView mBottomBanner;
    private float x1,x2;
    static final int MIN_DISTANCE=150;
   // private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taco);


        //Anuncios
      //  mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBottomBanner = (AdView) findViewById(R.id.av_bottom_banner);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mBottomBanner.loadAd(adRequest);

        //Obtenemos la localizacion
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    45);
        }
        String locat;

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
        try{
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            locat = new String("&lat="+latitude+"&lon="+longitude);
            Log.w("TACO_CAL","Localización: "+locat);
        }catch (SecurityException e){
            locat = null;

            Log.e("TACO_CAL","No hay localización"+e.getMessage());

        }catch (Exception e){
            locat = null;
            Log.e("TACO_CAL","Error en la localizacion "+e.getMessage());
        }


        //Obtenemos fecha
        Bundle b = getIntent().getExtras();
        String date = null;
        if(b != null) {
            int year = b.getInt("year");
            int month = b.getInt("month");
            int day = b.getInt("day");
            month++;
            if(year!=0)
                date = new String("&date="+year+"/"+month+"/"+day);
        }

        //Componemos la url
        String final_url = new String(URL);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL);

        if(date!=null)
            stringBuilder.append(date);
        if(locat!=null)
            stringBuilder.append(locat);

        final_url = stringBuilder.toString();

        Log.v("TACO_CAL",final_url);
        //Web view
        WebView wb = (WebView) findViewById(R.id.taco_webview);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wb.loadUrl(final_url);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
/*    static {
        System.loadLibrary("native-lib");
    }*/

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater;
        if (Build.VERSION.SDK_INT > 15)
            inflater = getMenuInflater();
        else
            inflater = new MenuInflater(this);

        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_date:
                showDatePickerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
        return true;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Context mContext = getActivity().getApplicationContext();
            Intent intent = new Intent(mContext, TacoActivity.class);
            Bundle b = new Bundle();
            b.putInt("year", year);
            b.putInt("month", month);
            b.putInt("day", day);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBottomBanner != null) {
            mBottomBanner.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBottomBanner != null) {
            mBottomBanner.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBottomBanner != null) {
            mBottomBanner.destroy();
        }
    }

    MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {

            Log.d("TACO", "lon: "+location.getLongitude()+" ----- lat: "+location.getLatitude());
        }
    };


   /* @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                        Log.v("TACO_CAL","A la derecha");
                    }

                    // Right to left swipe action
                    else
                    {
                        Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                        Log.v("TACO_CAL","A la izquierda");
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    } */


}
