package com.example.hi_food.ResataurantManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.example.hi_food.LogInActivity;
import com.example.hi_food.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainPage extends AppCompatActivity {
    NavigationView navigationView;
    String email, full_name, ip;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mToggle;
    ImageView navHeaderImage;
    TextView name;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        getSupportActionBar().setTitle("MainPage");
        Intent intent = getIntent();
        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        name = navHeader.findViewById(R.id.head_userName);
        navHeaderImage = navHeader.findViewById(R.id.head_userProfile);
        email = intent.getStringExtra("email");
        ip = intent.getStringExtra("ip");
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
            new getImageTask().execute();
        }
        drawer = findViewById(R.id.manager_drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.nav_manageTables:
                                ManageTable manageTable = new ManageTable();
                                manageTable.setArguments(b);
                                getSupportFragmentManager().beginTransaction().replace(
                                        R.id.managerFragmentContainer
                                        , manageTable).commit();
                                getSupportActionBar().setTitle("Manage-Tables");
                                break;
                            case R.id.nav_manageReservation:
                                ManageReservation manageReservation = new ManageReservation();
                                manageReservation.setArguments(b);
                                getSupportFragmentManager().beginTransaction().replace(
                                        R.id.managerFragmentContainer
                                        , manageReservation).commit();
                                getSupportActionBar().setTitle("Manage-Reservation");
                                break;
                            case R.id.nav_manageMeals:
                                ManageMeals meals = new ManageMeals();
                                meals.setArguments(b);
                                getSupportFragmentManager().beginTransaction().replace(
                                        R.id.managerFragmentContainer
                                        , meals).commit();
                                getSupportActionBar().setTitle("Manage-Meals");
                                break;
                            case R.id.nav_manageRating:
                                ViewRating viewRating = new ViewRating();
                                viewRating.setArguments(b);
                                getSupportFragmentManager().beginTransaction().replace(
                                        R.id.managerFragmentContainer
                                        , viewRating).commit();
                                getSupportActionBar().setTitle("Manage-Rating");
                                break;
                            case R.id.nav_manageDelivery:
                                CheckDeliveryServices checkDeliveryServices = new CheckDeliveryServices();
                                checkDeliveryServices.setArguments(b);
                                getSupportFragmentManager().beginTransaction().replace(
                                        R.id.managerFragmentContainer
                                        , checkDeliveryServices).commit();
                                getSupportActionBar().setTitle("Manage-DeliverServices");
                                break;
                            case R.id.nav_Managersetting:
                                getSupportActionBar().setTitle("Settings");
                                Toast.makeText(getApplicationContext(),
                                        "Settings", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.nav_logOut:
                                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                                finish();
                                break;

                        }
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            ManageTable manageTable = new ManageTable();
            manageTable.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.managerFragmentContainer
                    , manageTable).commit();
            navigationView.setCheckedItem(R.id.nav_main);
            getSupportActionBar().setTitle("Manage-Tables");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class getImageTask extends AsyncTask<Void, Void, String> {
        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/getImage.php");
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
