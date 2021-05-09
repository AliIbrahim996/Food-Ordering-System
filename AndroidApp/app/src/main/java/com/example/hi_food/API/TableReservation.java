package com.example.hi_food.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.hi_food.Model.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TableReservation extends AsyncTask<Void, Void, String> {
    StringBuilder stringBuilder;
    String responseMessage;
    int responseCode;
    List<Order> orders;
    Context mContext;
    String ip, rest_email, c_email;
    String table_id;
    SharedPreferences sharedPreferences;
    int meal_id;
    int quantity;
    String message;
    JSONObject resrvInfo;

    public TableReservation(List<Order> orders, Context mContext, String ip, String rest_email, String c_email, String table_id,
                            SharedPreferences sharedPreferences) {
        this.orders = orders;
        this.mContext = mContext;
        this.ip = ip;
        this.rest_email = rest_email;
        this.c_email = c_email;
        this.table_id = table_id;
        resrvInfo = new JSONObject();
        System.out.println("Table_id"+ table_id);
        this.sharedPreferences = sharedPreferences;
    }

    public TableReservation(List<Order> orders, Context mContext, String ip, String rest_email, String c_email, String table_id,
                            SharedPreferences sharedPreferences,JSONObject object) {
        this.orders = orders;
        this.mContext = mContext;
        this.ip = ip;
        this.rest_email = rest_email;
        this.c_email = c_email;
        this.table_id = table_id;
        System.out.println("Table_id"+ table_id);
        this.sharedPreferences = sharedPreferences;
        resrvInfo = object;
        System.out.println("data reservation "+ resrvInfo);
    }

    public JSONObject getResrvInfo() {
        return resrvInfo;
    }

    public void setResrvInfo(JSONObject resrvInfo) {
        this.resrvInfo = resrvInfo;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                    "/HI-Food/API/Controller/ReservationController/createTableReservation.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            System.out.println(url);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            JSONObject uData = new JSONObject();
            uData.put("restaurant_id", rest_email);
            uData.put("restaurent_tables_id", table_id);
            uData.put("customer_id", c_email);
            uData.put("reservation_info", resrvInfo);
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
                if (urlConnection.getResponseCode() != 201) {
                    System.out.println("Not created");
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
                System.out.println("Response Code: " + responseCode + " Response Message :" + responseMessage);
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

            if (responseCode != 201) {
                Toast.makeText(mContext,
                        "Response Code : " + responseCode, Toast.LENGTH_LONG).show();
                Toast.makeText(mContext, responseMessage, Toast.LENGTH_LONG).show();
                return;
            }

            System.out.println("Response : " + response);
            System.out.println("Object dose not collected");
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Object dose not collected");
            int flag = -3;

            flag = jsonObject.getInt("flag");
            message = jsonObject.getString("message");
            if (flag == 1) {
                reserveMeal();
                System.out.println("Message " + message);
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private void reserveMeal() {
        for (Order o : orders) {
            meal_id = Integer.parseInt(o.getMeal_id());
            quantity = o.getQty();
            if (quantity > 0) {
                System.out.println("Creating order");
                new createMTOrderTask(sharedPreferences
                        , mContext, rest_email, meal_id,
                        c_email, ip, quantity, table_id).execute();
            }
        }
    }
}