package com.example.hi_food.ResataurantManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hi_food.Adapters.RestaurantManager.RecyclerViewManageMealReservationAdapter;
import com.example.hi_food.Adapters.RestaurantManager.RecyclerViewManageTableReservationAdapter;
import com.example.hi_food.Model.CustomerMealBooking;
import com.example.hi_food.Model.CustomerTableBooking;
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

public class ManageReservation extends Fragment {

    RecyclerView manageTableReservationRView, manageMealReservationRView;
    RecyclerViewManageTableReservationAdapter adapter1;
    RecyclerViewManageMealReservationAdapter adapter2;
    View mView;
    SharedPreferences sharedPreferences;
    String email, full_name, ip;
    Context mContext;
    List<CustomerTableBooking> tableBookings;
    List<CustomerMealBooking> mealBookings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.manage_reservation, container, false);

        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
        mView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return true;
            }
            return false;
        });
        sharedPreferences = mView.getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mContext = mView.getContext();
        Bundle b = getArguments();
        email = b.getString("email");
        full_name = b.getString("full_name");
        ip = b.getString("ip");

        manageTableReservationRView = mView.findViewById(R.id.manageTReservationRView);
        manageMealReservationRView = mView.findViewById(R.id.manageMReservationRView);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mView.getContext());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mView.getContext());
        linearLayoutManager1.setOrientation(RecyclerView.HORIZONTAL);
        linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        //Todo set Data for Table Reservations
        new getTableReservationTask().execute();
        new getMaleReservationTask().execute();
        manageTableReservationRView.setLayoutManager(linearLayoutManager1);
        manageTableReservationRView.setHasFixedSize(true);
        manageMealReservationRView.setLayoutManager(linearLayoutManager2);
        manageMealReservationRView.setHasFixedSize(true);
        return mView;
    }

    class getTableReservationTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/getTablesReservations.php");
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
                    JSONObject Restaurants_Info = jsonObject.getJSONObject("Reservations");
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

    class getMaleReservationTask extends AsyncTask<Void, Void, String> {

        StringBuilder stringBuilder;
        String responseMessage;
        int responseCode;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/RestaurantManagerController/getMealsReservations.php");
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
                    JSONObject reservation_info = jsonObject.getJSONObject("reservation_info");
                    JSONArray data = reservation_info.getJSONArray("data");
                    collectMealData(data);
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
        tableBookings = new ArrayList<>();
        System.out.println(data);
        CustomerTableBooking tableBooking;
        for (int i = 0; i < data.length(); i++) {
            tableBooking = new CustomerTableBooking();
            if (data.get(i).equals("no_data_found!")) {
                System.out.println(data.get(i));
            } else {
                JSONObject element = data.getJSONObject(i);
                String id = element.getString("id");
                String restaurant_tables_id = element.getString("restaurant_tables_id");
                String restaurant_id = element.getString("restaurant_id");
                String table_number = element.getString("table_number");
                String t_status = element.getString("t_status");
                String customer_name = element.getString("customer_name");
                String customer_id = element.getString("customer_id");
                JSONObject reservation_info = element.getJSONObject("reservation_info");
                tableBooking.setCustomer_id(customer_id);
                tableBooking.setCustomer_name(customer_name);
                tableBooking.setReservation_info(reservation_info);
                tableBooking.setT_status(t_status);
                tableBooking.setTable_number(table_number);
                tableBooking.setRestaurant_tables_id(restaurant_tables_id);
                tableBooking.setRestaurant_id(restaurant_id);
                tableBooking.setId(id);
                tableBookings.add(tableBooking);
            }
            adapter1 = new RecyclerViewManageTableReservationAdapter(
                    mView.getContext(), tableBookings);
            adapter1.setIp(ip);
            adapter1.setSharedPreferences(sharedPreferences);
            manageTableReservationRView.setAdapter(adapter1);
        }
    }

    private void collectMealData(JSONArray data) throws JSONException {
        mealBookings = new ArrayList<>();
        System.out.println(data);
        JSONObject table_info;
        String t_info;
        CustomerMealBooking mealBooking;
        for (int i = 0; i < data.length(); i++) {
            mealBooking  = new CustomerMealBooking();
            if (data.get(i).equals("no_data_found!")) {
                System.out.println(data.get(i));
            } else {
                JSONObject element = data.getJSONObject(i);
                String id = element.getString("id");
                String restaurant_id = element.getString("restaurant_id");
                String meal_id = element.getString("meal_id");
                String meal_name = element.getString("meal_name");
                String quantity = element.getString("quantity");
                String unit_price = element.getString("unit_price");
                String order_status = element.getString("order_status");
                String data_time_booking = element.getString("data_time_booking");
                String data_time_delivary = element.getString("data_time_delivary");
                String customer_name = element.getString("customer_name");
                String customer_id = element.getString("customer_id");
                String is_in_door = element.getString("is_in_door");
                if (Integer.parseInt(is_in_door) == 1) {
                    t_info = element.getString("table_info");
                    mealBooking.setTable_info(new JSONObject().put("table_info", t_info));
                } else {
                    table_info = element.getJSONObject("table_info");
                    mealBooking.setTable_info(table_info);
                }
                mealBooking.setId(id);
                mealBooking.setCustomer_name(customer_name);
                mealBooking.setCustomer_id(customer_id);
                mealBooking.setData_time_booking(data_time_booking);
                mealBooking.setData_time_delivary(data_time_delivary);
                mealBooking.setQuantity(quantity);
                mealBooking.setUnit_price(unit_price);
                mealBooking.setOrder_status(order_status);
                mealBooking.setMeal_id(meal_id);
                mealBooking.setMeal_name(meal_name);
                mealBooking.setRestaurant_id(restaurant_id);
                mealBooking.setIs_in_door(is_in_door);
                mealBookings.add(mealBooking);
            }
            adapter2 = new RecyclerViewManageMealReservationAdapter(mContext, mealBookings);
            adapter2.setIp(ip);
            adapter2.setSharedPreferences(sharedPreferences);
            manageMealReservationRView.setAdapter(adapter2);
        }
    }
}
