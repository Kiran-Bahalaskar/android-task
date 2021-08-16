package com.kiranbahalaskar.androidtaskfromlattice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.kiranbahalaskar.androidtaskfromlattice.Network.Utils;
import com.kiranbahalaskar.androidtaskfromlattice.Network.VolleySingleton;
import com.kiranbahalaskar.androidtaskfromlattice.Session.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etCityName;
    private MaterialButton btnResult;
    private TextView tvTemperatureInCentigrade, tvTemperatureInFahrenheit, tvLatitude, tvLongitude;
    private String strCityName, strTempInC, strTempInF, strLat, strLong;

    private LoginSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new LoginSession(this);

        toolbar = findViewById(R.id.myToolbar);
        toolbar.setTitle("Weather today");
        setSupportActionBar(toolbar);

        etCityName = findViewById(R.id.etCityName);
        btnResult = findViewById(R.id.btnResult);
        tvTemperatureInCentigrade = findViewById(R.id.tvTemperatureInCentigrade);
        tvTemperatureInFahrenheit = findViewById(R.id.tvTemperatureInFahrenheit);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);

        HashMap<String, String> user = session.getUserDetails();
        strCityName = user.get(LoginSession.KEY_CITY_NAME);

        etCityName.setText(strCityName);

        btnResult.setOnClickListener(view -> {
            setWeatherDetails();
        });
    }

    private void setWeatherDetails() {

        strCityName = etCityName.getText().toString().trim();

        if (strCityName.isEmpty()){

            Toast.makeText(getApplicationContext(), "Please Enter City Name", Toast.LENGTH_SHORT).show();

        }else {

            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Loading...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            final String weatherUrl = Utils.weatherUrl + strCityName;
            StringRequest request = new StringRequest(Request.Method.GET, weatherUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String location = jsonObject.getString("location");
                        String current = jsonObject.getString("current");
                        JSONObject objLocation = new JSONObject(location);
                        JSONObject objCurrent = new JSONObject(current);

                        strLat = objLocation.getString("lat");
                        strLong = objLocation.getString("lon");
                        strTempInC = objCurrent.getString("temp_c");
                        strTempInF = objCurrent.getString("temp_f");

                        tvLatitude.setText(strLat);
                        tvLongitude.setText(strLong);
                        tvTemperatureInCentigrade.setText(strTempInC);
                        tvTemperatureInFahrenheit.setText(strTempInF);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pd.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error == null || error.networkResponse == null) {
                        return;
                    }
                    String body;
                    final String statusCode = String.valueOf(error.networkResponse.statusCode);
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                        Toast.makeText(getApplicationContext(), "" + body, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        // exception
                        Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    pd.dismiss();

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return super.getParams();
                }
            };

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                session.logoutUser();
                Toast.makeText(getApplicationContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}