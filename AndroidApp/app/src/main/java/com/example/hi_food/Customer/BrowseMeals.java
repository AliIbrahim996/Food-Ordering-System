package com.example.hi_food.Customer;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hi_food.API.CreateOrderTask;
import com.example.hi_food.Adapters.Customer.RecyclerViewCategoryAdapter;
import com.example.hi_food.Adapters.Customer.RecyclerViewMealAdapter;
import com.example.hi_food.Interfaces.DialogCallback;
import com.example.hi_food.Model.Category;
import com.example.hi_food.Model.Meal;
import com.example.hi_food.Model.Order;
import com.example.hi_food.R;
import com.example.hi_food.utils.GlobalUtils;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class BrowseMeals extends AppCompatActivity {
    private RecyclerView mealRView;
    List<Meal> meals;
    List<Order> orders;
    String cat_id, c_email, ip, rest_email, rest_id;
    SharedPreferences sharedPreferences;
    Button m_delivery, m_book;
    int meal_id;
    int quantity;
    String message;
    RecyclerViewMealAdapter recyclerViewMealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_meals);
        getSupportActionBar().setTitle("Avaliable Meals");
        Intent intent = getIntent();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        rest_id = intent.getStringExtra("rest_id");
        rest_email = intent.getStringExtra("rest_email");
        c_email = intent.getStringExtra("c_email");
        cat_id = intent.getStringExtra("cat_id");
        ip = intent.getStringExtra("ip");
        m_book = findViewById(R.id.m_book);
        mealRView = this.findViewById(R.id.mealRView);
        mealRView.setHasFixedSize(true);
        mealRView.setLayoutManager(new LinearLayoutManager(this));
        new getMealTask().execute();
        m_delivery = findViewById(R.id.m_delivery);
        m_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Order> orders = recyclerViewMealAdapter.getOrders();
                for (int i = 0; i < orders.size(); i++) {
                    meal_id = Integer.parseInt(orders.get(i).getMeal_id());
                    quantity = orders.get(i).getQty();
                    if (quantity > 0) {
                        System.out.println("Creating order with q = " + quantity);
                        CreateOrderTask c = new CreateOrderTask(BrowseMeals.this, sharedPreferences,
                                ip, rest_email, meal_id, c_email, quantity);
                        c.execute();
                    }
                }
                System.out.println("Finished!");

                new getMealTask().execute();
                GlobalUtils g = new GlobalUtils(rest_id,c_email,ip,BrowseMeals.this);
                g.showDialogRating(BrowseMeals.this, new DialogCallback() {
                    @Override
                    public void callBack(String ratings) {

                    }
                });
            }
        });
        m_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("orders", (Serializable) orders);
                BrowseTables browseTables = new BrowseTables();
                Intent intent1 = new Intent(getApplicationContext(), browseTables.getClass());
                intent1.putExtra("ip", ip);
                intent1.putExtra("rest_email", rest_email);
                intent1.putExtra("c_email", c_email);
                intent1.putExtra("rest_id", rest_id);
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class getMealTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/getAllMeals.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("category_id", cat_id);
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
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Meals_info");
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
        meals = new ArrayList<>();
        orders = new ArrayList();
        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            String meal_id = element.getString("meal_id");
            String meal_name = element.getString("meal_name");
            String calories = element.getString("calories");
            String price = element.getString("price");
            String Image = element.getString("Image");
            String category_id = element.getString("category_id");
            Meal m = new Meal(meal_name, Double.parseDouble(calories), Double.parseDouble(price));
            Order o = new Order(meal_id, 0, Double.parseDouble(price));
            orders.add(o);
            m.setId(meal_id);
            m.setImageURL(Image);
            m.setCat_id(category_id);
            meals.add(m);
        }

        recyclerViewMealAdapter = new RecyclerViewMealAdapter(
                this, meals);
        recyclerViewMealAdapter.setOrders(orders);
        mealRView.setAdapter(recyclerViewMealAdapter);
    }
}
