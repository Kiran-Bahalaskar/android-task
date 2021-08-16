package com.kiranbahalaskar.androidtaskfromlattice.Authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.kiranbahalaskar.androidtaskfromlattice.MainActivity;
import com.kiranbahalaskar.androidtaskfromlattice.Network.Utils;
import com.kiranbahalaskar.androidtaskfromlattice.Network.VolleySingleton;
import com.kiranbahalaskar.androidtaskfromlattice.R;
import com.kiranbahalaskar.androidtaskfromlattice.Session.LoginSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Map;

public class ActivityRegistration extends AppCompatActivity {

    private Calendar calendar = Calendar.getInstance();

    private Toolbar toolbar;
    private EditText etMobileNumber, etFullName, etDateOfBirth, etAddressLine1, etAddressLine2, etPinCode;
    private Spinner spGender;
    private TextView tvDistrict, tvState;
    private MaterialButton btnCheck, btnRegistration;

    private String strMobileNumber, strFullName, strDOB, strAddressLine1, strAddressLine2, strPin, strDistrict, strState;

    private LoginSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        session = new LoginSession(this);

        isLogin();

        toolbar = findViewById(R.id.myToolbar);
        toolbar.setTitle("Registration");
        setSupportActionBar(toolbar);

        etMobileNumber = findViewById(R.id.etMobileNumber);
        etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etAddressLine1 = findViewById(R.id.etAddressLine1);
        etAddressLine2 = findViewById(R.id.etAddressLine2);
        etPinCode = findViewById(R.id.etPinCode);
        spGender = findViewById(R.id.spGender);
        tvDistrict = findViewById(R.id.tvDistrict);
        tvState = findViewById(R.id.tvState);
        btnCheck = findViewById(R.id.btnCheck);
        btnRegistration = findViewById(R.id.btnRegistration);


        btnCheck.setOnClickListener(view -> {
            checkPin();
        });

        btnRegistration.setOnClickListener(view -> {
            registration();
        });

        setUpCalendar();
    }



    private void setUpCalendar() {
        etDateOfBirth.setOnClickListener(view -> {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    String sMonth = "";
                    if (month < 10) {
                        sMonth = "0" + String.valueOf(month);
                    } else {
                        sMonth = String.valueOf(month);
                    }

                    strDOB = year + "-" + sMonth + "-" + dayOfMonth;
                    etDateOfBirth.setText(strDOB);

                }
            }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void checkPin() {

        strPin = etPinCode.getText().toString().trim();

        if (strPin.isEmpty()) {

            Toast.makeText(this, "Please Enter Pin Code", Toast.LENGTH_SHORT).show();

        } else {

            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Loading...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            final String checkUrl = Utils.checkPinUrl + strPin;
            StringRequest request = new StringRequest(Request.Method.GET, checkUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String status = jsonObject.getString("Status");

                        if (status.equals("Success")) {

                            JSONArray array = new JSONArray(jsonObject.getString("PostOffice"));
                            JSONObject object = array.getJSONObject(0);

                            strDistrict = object.getString("District");
                            strState = object.getString("State");

                            tvDistrict.setText(strDistrict);
                            tvState.setText(strState);

                        } else {
                            Toast.makeText(getApplicationContext(), "Pin Code is Not Correct", Toast.LENGTH_SHORT).show();
                        }


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
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return super.getParams();
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        }
    }

    private void registration() {

        strMobileNumber = etMobileNumber.getText().toString().trim();
        strFullName = etFullName.getText().toString().trim();
        strDOB = etDateOfBirth.getText().toString().trim();
        strAddressLine1 = etAddressLine1.getText().toString().trim();
        strAddressLine2 = etAddressLine2.getText().toString().trim();
        strPin = etPinCode.getText().toString().trim();
        strDistrict = tvDistrict.getText().toString();
        strState = tvState.getText().toString();

        if (strMobileNumber.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();

        } else if (strFullName.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Enter Full Name", Toast.LENGTH_SHORT).show();

        } else if (strDOB.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Enter Date of Birth", Toast.LENGTH_SHORT).show();

        } else if (strAddressLine1.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Enter Address Line 1", Toast.LENGTH_SHORT).show();

        } else if (strAddressLine2.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Enter Address Line 2", Toast.LENGTH_SHORT).show();

        } else if (strPin.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Enter Pin Code", Toast.LENGTH_SHORT).show();

        } else if (strDistrict.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Please Set District", Toast.LENGTH_SHORT).show();

        } else {

            session.createLoginSession(strDistrict);

            startActivity(new Intent(getApplicationContext(), MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

            Toast.makeText(getApplicationContext(), "Registration Successfully", Toast.LENGTH_SHORT).show();

        }
    }

    private void isLogin() {

        if (session.isLoggedIn()) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

        }
    }
}

