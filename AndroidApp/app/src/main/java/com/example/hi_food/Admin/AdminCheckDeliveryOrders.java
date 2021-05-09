package com.example.hi_food.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.Adapters.Admin.RecyclerViewAdminDeliveryAdapter;
import com.example.hi_food.Model.CustomerMealBooking;
import com.example.hi_food.Model.DeliveryServices;
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

public class AdminCheckDeliveryOrders extends Fragment {
    RecyclerView rViewDelivery;
    RecyclerViewAdminDeliveryAdapter adapter;
    List<DeliveryServices> mealBookingList;
    View mView;
    Context mContext;
    SharedPreferences sharedPreferences;
    String ip, email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.check_delivery_services, container, false);
        Bundle b = getArguments();
        ip = b.getString("ip");
        email = b.getString("email");
        mContext = mView.getContext();
        rViewDelivery = mView.findViewById(R.id.rViewDelivery);
        rViewDelivery.setHasFixedSize(true);
        rViewDelivery.setLayoutManager(new LinearLayoutManager(mContext));
        sharedPreferences = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //get all delivery
        new getDeliveryTask().execute();
        return mView;
    }

    public class getDeliveryTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/AdminController/checkDeliverOrders.php");
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
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("reservation_info");
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
        mealBookingList = new ArrayList<>();
        System.out.println(data);
        for (int i = 0; i < data.length(); i++) {
            if (data.get(i).equals("no_data_found!")) {
                System.out.println(data.get(i));
            } else {
                JSONObject element = data.getJSONObject(i);
                System.out.println("Element " + element);
                String reservation_id = element.getString("id");
                String customer_name = element.getString("customer_name");
                String meal_name = element.getString("meal_name");
                String restaurant_name = element.getString("restaurant_name");
                String quantity = element.getString("quantity");
                String date_booking = element.getString("data_time_booking");
                String order_status = element.getString("order_status");
                String date_delivered = TextUtils.isEmpty(element.getString("data_time_delivary")) ? "" :
                        element.getString("data_time_delivary");
                DeliveryServices dServices = new DeliveryServices();
                dServices.setCustomer_name(customer_name);
                dServices.setDate_booking(date_booking);
                dServices.setDate_delivered(date_delivered);
                dServices.setOrder_status(order_status);
                dServices.setMeal_name(meal_name);
                dServices.setQuantity(quantity);
                dServices.setRestaurant_name(restaurant_name);
                dServices.setReservation_id(reservation_id);
                mealBookingList.add(dServices);
            }
        }
        adapter = new RecyclerViewAdminDeliveryAdapter(mContext, mealBookingList);
        rViewDelivery.setAdapter(adapter);
    }
}
