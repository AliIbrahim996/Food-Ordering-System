package com.example.hi_food.ResataurantManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hi_food.Adapters.RestaurantManager.RecyclerViewManageTableAdapter;
import com.example.hi_food.Model.Table;
import com.example.hi_food.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageTable extends Fragment {

    RecyclerView recyclerView;
    FloatingActionButton addTable;
    View mView;
    Context mContext;
    ArrayList<String> restaurantNames;
    SharedPreferences sharedPreferences;
    String email, full_name, ip;
    List<Table> tables;
    RecyclerViewManageTableAdapter recyclerViewManageTableAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.manage_table, container, false);
        sharedPreferences = mView.getContext().getSharedPreferences("Preferences", mView.getContext().MODE_PRIVATE);
        mContext = mView.getContext();
        recyclerView = mView.findViewById(R.id.manageTableRview);
        recyclerView.setHasFixedSize(true);
        Bundle b = getArguments();
        email = b.getString("email");
        full_name = b.getString("full_name");
        ip = b.getString("ip");
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        addTable = mView.findViewById(R.id.addTableFAB);
        addTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddTable.class);
                intent.putExtra("email", email);
                intent.putExtra("full_name", full_name);
                intent.putExtra("ip", ip);
                startActivity(intent);
            }
        });
        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
        new getTableTask().execute();
        return mView;
    }

    class getTableTask extends AsyncTask<Void, Void, String> {

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
                uData.put("rest_id", email);
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

                    Toast.makeText(mContext,
                            "Response Code : " + responseCode, Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, responseMessage, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(mContext, message + " Response Code: " + responseCode, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(mContext, "There was an error" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        recyclerViewManageTableAdapter = new RecyclerViewManageTableAdapter(mContext, tables);
        recyclerViewManageTableAdapter.setIp(ip);
        recyclerViewManageTableAdapter.setEmail(email);
        recyclerViewManageTableAdapter.setFull_name(full_name);
        recyclerViewManageTableAdapter.setSharedPreferences(sharedPreferences);
        recyclerView.setAdapter(recyclerViewManageTableAdapter);
    }
}
