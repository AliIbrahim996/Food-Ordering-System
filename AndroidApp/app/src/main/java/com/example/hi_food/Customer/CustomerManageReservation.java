package com.example.hi_food.Customer;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hi_food.Adapters.Customer.RecyclerViewManageMealReservationAdapter;
import com.example.hi_food.Adapters.Customer.RecyclerViewManageTableReservationAdapter;
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

public class CustomerManageReservation extends Fragment {

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


        mView = inflater.inflate(R.layout.customer_manage_reservation, container, false);

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


        manageMealReservationRView = mView.findViewById(R.id.manageMReservationRView);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mView.getContext());

        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        //Todo set Data for Table Reservations
        new getMaleReservationTask().execute();

        manageMealReservationRView.setLayoutManager(linearLayoutManager1);
        manageMealReservationRView.setHasFixedSize(true);
        return mView;
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
                        "/HI-Food/API/Controller/ReservationController/getMealsReservations.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                JSONObject uData = new JSONObject();
                uData.put("customer_id", email);
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


    private void collectMealData(JSONArray data) throws JSONException {
        mealBookings = new ArrayList<>();
        System.out.println(data);
        JSONObject table_info;
        String t_info;
        CustomerMealBooking mealBooking;
        for (int i = 0; i < data.length(); i++) {
            mealBooking = new CustomerMealBooking();
            if (data.get(i).equals("no_data_found!")) {
                System.out.println(data.get(i));
            } else {
                JSONObject element = data.getJSONObject(i);
                String id = element.getString("id");
                String restaurant_id = element.getString("restaurant_id");
                String restaurant_name = element.getString("restaurant_name");
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
                String restaurent_tables_id = element.getString("restaurent_tables_id");
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
                mealBooking.setRestaurant_name(restaurant_name);
                mealBooking.setTable_id(restaurent_tables_id);
                mealBookings.add(mealBooking);
            }
            adapter2 = new RecyclerViewManageMealReservationAdapter(mContext, mealBookings);
            adapter2.setIp(ip);
            adapter2.setSharedPreferences(sharedPreferences);
            manageMealReservationRView.setAdapter(adapter2);
        }
    }
}
