package com.example.myweatherapplication;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, conditionTV, temperatureTV;
    private TextInputEditText cityEdit;
    private RecyclerView weatherRV;
    private ImageView backIV, searchIV, iconIV;

    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;


    private ArrayList<RecyclerViewModelClass> recyclerViewModelArray;
    private RecyclerViewAdapter recyclerViewAdapter;

    private String cityName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate method called");

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.homeRL);
        loadingPB = findViewById(R.id.loadingPB);
        cityNameTV = findViewById(R.id.cityNameTV);
        conditionTV = findViewById(R.id.conditionTV);
        temperatureTV = findViewById(R.id.temperatureTV);
        cityEdit = findViewById(R.id.cityEdit);
        weatherRV = findViewById(R.id.weatherRV);
        backIV = findViewById(R.id.backIV);
        searchIV = findViewById(R.id.searchIV);
        iconIV = findViewById(R.id.iconIV);

        recyclerViewModelArray = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(this, recyclerViewModelArray);
        weatherRV.setAdapter(recyclerViewAdapter);








        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                                                        ACCESS_COARSE_LOCATION}, PERMISSION_CODE);

        }


        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());
//        cityName = "London";

        getWeatherInfo(cityName);


        // search icon,
        //on click calls getWeatherInfo method
        // displays simple toast message
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdit.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                } else{
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }

//            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        });

    }


    // handles the permission of getting current loation
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted..", Toast.LENGTH_SHORT).show();
            } else  {
                Toast.makeText(this," Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }

        }




    }


    // this method get the city name form lat and long
    private String getCityName (double longitude, double latitude){
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address adress : addresses){
                if (adress != null){
                    String city = adress.getLocality();
                    if (city != null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "City Not Found", Toast.LENGTH_LONG).show();
                    }
                }
            }



        }catch (IOException e) {
            e.printStackTrace();
        }


        return cityName;
//        return cityName = "New York";

    }



    // get weather info method,
    // calls the weather API,
    // parses the JSON data
    private void getWeatherInfo (String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=e7b821e8f28749669c5202905213009&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                recyclerViewModelArray.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature + "°c");

                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    if (isDay==1){
                        //morning

//                        Picasso.get().load("src/main/res/drawable/dayback.jpg").into(backIV);
                    } else {
                        //night
//                        Picasso.get().load("src/main/res/drawable/nightback.JPG").into(backIV);
                    }

                    JSONObject forcastObj = response.getJSONObject("forecast");
                    JSONObject forcastday = forcastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray  = forcastday.getJSONArray("hour");

                    for (int i=0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String tempC = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        recyclerViewModelArray.add(new RecyclerViewModelClass(time, tempC, img, wind));

                    }
                    recyclerViewAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);


    }


}