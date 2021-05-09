package com.example.hi_food.Customer;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hi_food.Adapters.Customer.RecyclerViewCategoryAdapter;
import com.example.hi_food.Model.Category;
import com.example.hi_food.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
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

public class BrowseCategories extends AppCompatActivity {

    private RecyclerView categoriesRView;
    List<Category> categories;
    String ip, rest_id, rest_email, c_email;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_categories);
        getSupportActionBar().setTitle("Categories");
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        rest_id = intent.getStringExtra("rest_id");
        rest_email = intent.getStringExtra("rest_email");
        c_email = intent.getStringExtra("c_email");
        ip = intent.getStringExtra("ip");
        categoriesRView = findViewById(R.id.categoriesRView);
        categoriesRView.setHasFixedSize(true);
        categoriesRView.setLayoutManager(new GridLayoutManager(this, 2));
        new getCategoryTask().execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    public class getCategoryTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/CategoryController/getAllCategories.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("email", rest_email);
                System.out.println(uData);
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
                    if (urlConnection.getResponseCode() != 200) {
                        System.out.println("Response code: " + urlConnection.getResponseCode());
                        System.out.println("Response Message: " + responseMessage);
                        return responseMessage;
                    }
                    System.out.println(urlConnection.getInputStream());
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
            try {

                if (responseCode != 200) {

                    Toast.makeText(getApplicationContext(),
                            "Response Code : " + responseCode, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject jsonObject = new JSONObject(response);
                int flag = -3;
                String message = "";
                flag = jsonObject.getInt("flag");
                if (flag == 1) {
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Categories_Info");
                    JSONArray data = Restaurants_Info.getJSONArray("data");
                    collectData(data);
                    System.out.println(data);
                } else {
                    Toast.makeText(getApplicationContext(), message + " Response Code: " + responseCode, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void collectData(JSONArray data) throws JSONException {
        categories = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            String id = element.getString("cat_id");
            String cat_name = element.getString("cat_name");
            String cat_imgUrl = element.getString("cat_imgUrl");
            String menu_id = element.getString("menu_id");
            Category c = new Category(cat_name, id, cat_imgUrl, Integer.parseInt(menu_id));
            categories.add(c);
        }
        RecyclerViewCategoryAdapter recyclerViewCategoryAdapter = new RecyclerViewCategoryAdapter(
                getApplicationContext(),
                categories,
                c_email,
                rest_email,
                rest_id,
                ip
        );
        categoriesRView.setAdapter(recyclerViewCategoryAdapter);
    }
}
