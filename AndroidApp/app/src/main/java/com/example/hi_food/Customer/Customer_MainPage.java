package com.example.hi_food.Customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hi_food.Adapters.Customer.RecyclerViewRestaurantAdapter;
import com.example.hi_food.Model.Restaurant;
import com.example.hi_food.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;;
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

public class Customer_MainPage extends Fragment {

    RecyclerView restaurantRView;
    List<Restaurant> restaurants;
    Context mContext;
    SharedPreferences sharedPreferences;
    String email, ip, full_name;
    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.customer__dash_board, container, false);
        mContext = mView.getContext();
        Bundle b = getArguments();
        assert b != null;
        email = b.getString("email");
        ip = b.getString("ip");
        full_name = b.getString("full_name");
        restaurantRView = mView.findViewById(R.id.restaurantRView);
        restaurantRView.setHasFixedSize(true);
        restaurantRView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        sharedPreferences = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //Todo get all restaurants
        new getRestaurantsTask().execute();
        return mView;
    }

    public class getRestaurantsTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantController/getRestaurants.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
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

    private void collectData(JSONArray data) throws JSONException {
        restaurants = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject element = data.getJSONObject(i);
            String id = element.getString("rest_id");
            System.out.println("Rest_id " +id);
            String name = element.getString("restaurant_name");
            String location = element.getString("restaurant_address");
            String phoneNum = element.getString("restaurant_phone_no");
            String imageURL = element.getString("restaurant_imgUrl");
            String openAt = element.getString("open_at");
            String closeAt = element.getString("close_at");
            String status = element.getString("is_accepted");
            String avg_rate = element.getString("rate");
            String manager_email = element.getString("manger_email");
            Restaurant r = new Restaurant(
                    name, location,
                    phoneNum, openAt, closeAt, imageURL
                    , status, id);
            r.setRate(Double.parseDouble(avg_rate));
            r.setManger_email(manager_email);
            restaurants.add(r);
        }
        RecyclerViewRestaurantAdapter adapter = new RecyclerViewRestaurantAdapter(mContext, restaurants);
        adapter.setEmail(email);
        adapter.setIp(ip);
        restaurantRView.setAdapter(adapter);
    }
}