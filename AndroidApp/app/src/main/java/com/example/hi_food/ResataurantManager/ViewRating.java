package com.example.hi_food.ResataurantManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hi_food.Adapters.RestaurantManager.RecyclerViewManageRating;
import com.example.hi_food.Model.Rate;
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

public class ViewRating extends Fragment {
    SharedPreferences sharedPreferences;
    String email, full_name, ip;
    View mView;
    Context mContext;
    RecyclerView recyclerView;
    List<Rate> rateList;
    RecyclerViewManageRating adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.view_rating, container, false);
        mContext = mView.getContext();
        sharedPreferences = mView.getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        recyclerView = mView.findViewById(R.id.rViewRating);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        Bundle b = getArguments();
        email = b.getString("email");
        full_name = b.getString("full_name");
        ip = b.getString("ip");
        //Todo get rating
        new getRatingTask().execute();
        return mView;
    }

    class getRatingTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/browseCustomersRating.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("rest_email", email);
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
                        System.out.println("No data");
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
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Rating");
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
        rateList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            if (data.get(i).equals("no_data_found!")) {
                System.out.println(data.get(i));
            } else {
                JSONObject element = data.getJSONObject(i);
                System.out.println("Element " + element);
                String id = element.getString("id");
                String customer_id = element.getString("customer_id");
                String customer_name = element.getString("customer_name");
                System.out.println(customer_name);
                String customer_rate = element.getString("customer_rate");
                String feedBack = element.getString("feedBack");
                String restaurant_id = element.getString("restaurant_id");
                Rate r = new Rate(Integer.parseInt(customer_id), Integer.parseInt(restaurant_id), feedBack,
                        Integer.parseInt(customer_rate));
                r.setC_name(customer_name);
                r.setId(Integer.parseInt(id));
                rateList.add(r);

            }
        }
        adapter = new RecyclerViewManageRating(mContext, rateList);
        recyclerView.setAdapter(adapter);
    }
}
