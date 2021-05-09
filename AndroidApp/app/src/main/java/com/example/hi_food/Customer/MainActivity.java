package com.example.hi_food.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi_food.API.DownloadImage;
import com.example.hi_food.Adapters.Customer.RecyclerViewRestaurantAdapter;
import com.example.hi_food.LogInActivity;
import com.example.hi_food.Model.Restaurant;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    NavigationView navigationView;
    String email, full_name;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mToggle;
    ImageView navHeaderImage;
    TextView name;
    String ip;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        getSupportActionBar().setTitle("MainPage");
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        name = navHeader.findViewById(R.id.head_userName);
        navHeaderImage = navHeader.findViewById(R.id.head_userProfile);
        email = intent.getStringExtra("email");
        TextView textView = navHeader.findViewById(R.id.head_user_email);
        textView.setText(email);
        final Bundle b = new Bundle();
        b.putString("email", email);
        b.putString("ip", ip);
        name.setText(intent.getStringExtra("full_name"));
        b.putString("full_name", intent.getStringExtra("full_name"));
        if (!TextUtils.isEmpty(intent.getStringExtra("imageData"))) {
            byte[] imageBytes = Base64.decode(intent.getStringExtra("imageData"), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            navHeaderImage.setImageBitmap(decodedImage);
        } else {
            new getCustomerImageTask().execute();
        }
        drawer = findViewById(R.id.draw_layout);
        mToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();

        navigationView.setNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.nav_main:
                            Customer_MainPage c = new Customer_MainPage();
                            c.setArguments(b);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer
                                    , c).commit();
                            getSupportActionBar().setTitle("Avaliable-Restaurant");
                            break;
                        case R.id.nav_cart:
                            CustomerManageReservation manageReservation = new CustomerManageReservation();
                            manageReservation.setArguments(b);
                            navigationView.setCheckedItem(R.id.nav_cart);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer
                                    ,manageReservation).commit();
                            getSupportActionBar().setTitle("Your Reservations");
                            //Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.nav_setting:
                            navigationView.setCheckedItem(R.id.nav_setting);
                            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
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
            Customer_MainPage c = new Customer_MainPage();
            c.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer
                    , c).commit();
            navigationView.setCheckedItem(R.id.nav_main);
            getSupportActionBar().setTitle("Avaliable-Restaurant");
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    class getCustomerImageTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/CustomerController/getImage.php");
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
                    responseCode = urlConnection.getResponseCode();
                    responseMessage = urlConnection.getResponseMessage();
                    if (responseCode != 200) {
                        System.out.println("Response Code: " + responseCode + "\nResponse Message :" + responseMessage);
                        return null;
                    }
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
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
                Toast.makeText(getApplicationContext(), "THERE WAS AN ERROR Response null! " + responseMessage, Toast.LENGTH_LONG).show();
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
                String imageUrl = jsonObject.getString("imageUrl");
                new DownloadImage(navHeaderImage).execute(imageUrl);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
