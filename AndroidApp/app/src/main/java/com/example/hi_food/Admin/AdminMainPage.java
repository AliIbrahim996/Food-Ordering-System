package com.example.hi_food.Admin;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.LogInActivity;
import com.example.hi_food.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminMainPage extends AppCompatActivity {
    NavigationView navigationView;
    String email, full_name, ip;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mToggle;
    ImageView navHeaderImage;
    TextView name;
    SharedPreferences sharedPreferences;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_page);
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        drawer = findViewById(R.id.draw_layout);
        mToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();
        Intent intent = getIntent();
        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        name = navHeader.findViewById(R.id.head_userName);
        navHeaderImage = navHeader.findViewById(R.id.head_userProfile);
        email = intent.getStringExtra("email");
        ip = intent.getStringExtra("ip");
        b = new Bundle();
        b.putString("email", email);
        b.putString("ip", ip);
        TextView textView = navHeader.findViewById(R.id.head_user_email);
        textView.setText(email);
        getInfoTask infoTask = new getInfoTask();
        infoTask.execute();
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_R_mag:
                    AdminManageRestaurant admin = new AdminManageRestaurant();
                    admin.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frag
                            , admin).commit();
                    getSupportActionBar().setTitle("Available-Restaurant");
                    break;
                case R.id.nav_c_mag:
                    AdminManageCustomers manageCustomers = new AdminManageCustomers();
                    manageCustomers.setArguments(b);
                    getSupportActionBar().setTitle("Customers");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frag
                            , manageCustomers).commit();
                    break;
                case R.id.nav_reservation:
                    AdminBrowseReservations browseReservations = new AdminBrowseReservations();
                    browseReservations.setArguments(b);
                    getSupportActionBar().setTitle("Reservations");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frag
                            , browseReservations).commit();
                    break;
                case R.id.nav_rates:
                    AdminBrowseRates rates = new AdminBrowseRates();
                    rates.setArguments(b);
                    getSupportActionBar().setTitle("Restaurant-Rates");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frag
                            , rates).commit();
                    break;
                case R.id.nav_orders:
                    AdminCheckDeliveryOrders deliveryOrders = new AdminCheckDeliveryOrders();
                    deliveryOrders.setArguments(b);
                    getSupportActionBar().setTitle("Delivery-services");
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frag
                            , deliveryOrders).commit();
                    break;
                case R.id.nav_logOut:
                    startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                    finish();
                    break;

            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getSupportActionBar().setTitle("Available-Restaurant");
            AdminManageRestaurant admin = new AdminManageRestaurant();
            admin.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frag
                    , admin).commit();
            navigationView.setCheckedItem(R.id.nav_main);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    class getInfoTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/AdminController/getInfo.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("email", email);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(uData.toString());
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                try {
                    System.out.println(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    responseCode = urlConnection.getResponseCode();
                    responseMessage = urlConnection.getResponseMessage();
                    System.out.println("Response Code: " + responseCode + "\nResponse Message :" + responseMessage);
                    return stringBuilder.toString();

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                Toast.makeText(getApplicationContext(), "THERE WAS AN ERROR Response null", Toast.LENGTH_LONG).show();
                return;
            }
            if (responseCode != 200) {
                Toast.makeText(getApplicationContext(), "THERE WAS AN ERROR response code is: " + responseCode, Toast.LENGTH_LONG).show();
                return;
            }
            String message = "";
            int flag = -2;

            try {
                JSONObject jsonObject = new JSONObject(response);
                flag = jsonObject.getInt("flag");


                if (flag == 1) {
                    JSONObject admin = jsonObject.getJSONObject("Admin");
                    System.out.println("Admin data" + admin);
                    full_name = admin.getString("full_name");
                    name.setText(full_name);
                    b.putString("full_name", full_name);
                    String imageUrl = admin.getString("admin_image");
                    new DownloadImage(navHeaderImage).execute(imageUrl);
                } else {
                    message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
