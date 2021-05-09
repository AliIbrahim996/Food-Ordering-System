package com.example.hi_food.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.example.hi_food.Adapters.Customer.RecyclerViewTableAdapter;
import com.example.hi_food.Model.Order;
import com.example.hi_food.Model.Table;
import com.example.hi_food.R;

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

public class BrowseTables extends AppCompatActivity {
    private RecyclerView tableRView;
    List<Order> orders;
    List<Table> tables;
    String c_email, ip, rest_email, rest_id;
    SharedPreferences sharedPreferences;
    RecyclerViewTableAdapter recyclerViewTableAdapter;

    public BrowseTables() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_tables);
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orders = (List<Order>) bundle.getSerializable("orders");
            System.out.println("Orders size" + orders.size());
        }

        rest_id = intent.getStringExtra("rest_id");
        rest_email = intent.getStringExtra("rest_email");
        c_email = intent.getStringExtra("c_email");
        ip = intent.getStringExtra("ip");
        tableRView = findViewById(R.id.tableRView);
        getSupportActionBar().setTitle("Tables");
        tableRView.setHasFixedSize(true);
        tableRView.setLayoutManager(new LinearLayoutManager(this));
        new getTablesTask().execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public class getTablesTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/TableController/getTables.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("rest_id", rest_email);
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
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Tables_Info");
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
        tables = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            String restaurant_id = element.getString("restaurant_id");
            String id = element.getString("id");
            String table_number = element.getString("table_number");
            String table_location = element.getString("table_location");
            String table_number_seats = element.getString("table_number_seats");
            String table_status = element.getString("table_status");
            String imageUrl = element.getString("imageUrl");
            Table t = new Table(Integer.parseInt(table_number_seats), table_location, Integer.parseInt(table_number));
            t.setId(id);
            t.setImageURL(imageUrl);
            t.setRest_id(restaurant_id);
            t.setTableStatus(table_status);
            tables.add(t);
        }
        recyclerViewTableAdapter = new RecyclerViewTableAdapter(this, tables);
        recyclerViewTableAdapter.setOrders(orders);
        recyclerViewTableAdapter.setC_email(c_email);
        recyclerViewTableAdapter.setIp(ip);
        recyclerViewTableAdapter.setRest_email(rest_email);
        recyclerViewTableAdapter.setRest_id(rest_id);
        recyclerViewTableAdapter.setRest_id(rest_id);
        recyclerViewTableAdapter.setSharedPreferences(sharedPreferences);
        tableRView.setAdapter(recyclerViewTableAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);
        return true;
    }

}
