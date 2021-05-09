package com.example.hi_food.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hi_food.Adapters.Admin.RecyclerViewBrowseRestaurant;
import com.example.hi_food.Model.Restaurant;
import com.example.hi_food.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.HashMap;
import java.util.List;

public class AdminManageRestaurant extends Fragment {
    static RecyclerView adminRestaurantRView;
    static List<Restaurant> restaurants;
    static Context mContext;
    FloatingActionButton addRestaurant;
    static SharedPreferences sharedPreferences;
    static String email, full_name, ip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_admin_manage_restaurant, container, false);
        mContext = mView.getContext();
        Bundle b = getArguments();
        email = b.getString("email");
        full_name = b.getString("full_name");
        ip = b.getString("ip");
        adminRestaurantRView = mView.findViewById(R.id.adminRestaurantRView);
        adminRestaurantRView.setLayoutManager(new LinearLayoutManager(mContext));
        addRestaurant = mView.findViewById(R.id.addResFAB);
        addRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AddRestaurantRView.class);
            intent.putExtra("email", email);
            intent.putExtra("full_name", full_name);
            startActivity(intent);
        });
        sharedPreferences = mContext.getSharedPreferences("Preferences", mContext.MODE_PRIVATE);

        new getRestaurantsTask().execute();
        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
        mView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return true;
            }
            return false;
        });
        return mView;
    }

    static class getRestaurantsTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/AdminController/getRestaurants.php");
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
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Restaurants_Info");
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

    private static void collectData(JSONArray data) throws JSONException {
        restaurants = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            String id = element.getString("rest_id");
            String name = element.getString("restaurant_name");
            String location = element.getString("restaurant_address");
            String phoneNum = element.getString("restaurant_phone_no");
            String imageURL = element.getString("restaurant_imgUrl");
            String openAt = element.getString("open_at");
            String closeAt = element.getString("close_at");
            String status = element.getString("is_accepted");
            Restaurant r = new Restaurant(name, location, phoneNum, openAt, closeAt, imageURL
                    , status, id);
            String mgrName = "";
            if (TextUtils.isEmpty(element.getString("manager_name"))) {
                mgrName = "";
            } else {
                mgrName = element.getString("manager_name");
            }
            r.setStatus(status);
            r.setManager_name(mgrName);
            restaurants.add(r);
        }
        RecyclerViewBrowseRestaurant adapter = new RecyclerViewBrowseRestaurant(
                mContext, restaurants);
        adapter.setIp(ip);
        adapter.setSharedPreferences(sharedPreferences);
        adapter.setFull_name(full_name);
        adapter.setEmail(email);
        adminRestaurantRView.setAdapter(adapter);
    }
}