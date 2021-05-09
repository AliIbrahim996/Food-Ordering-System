package com.example.hi_food.API;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

public class createMTOrderTask extends AsyncTask<Void, Void, String> {

    StringBuilder stringBuilder;
    String responseMessage;
    int responseCode;
    SharedPreferences sharedPreferences;
    Context mContext;
    String rest_email, c_email, ip, table_id;
    int quantity, meal_id;
    String message;
    private final ProgressDialog dialog;

    public createMTOrderTask(SharedPreferences sharedPreferences,
                             Context mContext, String rest_email, int meal_id,
                             String c_email, String ip, int quantity,
                             String table_id) {

        this.sharedPreferences = sharedPreferences;
        this.mContext = mContext;
        this.rest_email = rest_email;
        this.meal_id = meal_id;
        this.c_email = c_email;
        this.ip = ip;
        this.quantity = quantity;
        this.table_id = table_id;

        dialog = new ProgressDialog(mContext);
    }

    protected void onPreExecute() {
        this.dialog.setMessage("Processing...");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            if (quantity != 0) {
                URL url = new URL("http://" + sharedPreferences.getString("SERVER_IP", ip) +
                        "/HI-Food/API/Controller/ReservationController/createMealOnTableReservation.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
                        new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                JSONObject uData = new JSONObject();
                uData.put("restaurant_id", rest_email);
                uData.put("meal_id", meal_id);
                uData.put("customer_id", c_email);
                uData.put("data_time_booking", currentDate + " " + currentTime);
                uData.put("is_in_door", 0);
                uData.put("order_status", "waiting for approval");
                uData.put("quantity", quantity);
                uData.put("table_id", table_id);
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
            } else {
                dialog.dismiss();
                Toast.makeText(mContext, "Quantity is zero", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
        return null;
    }

    protected void onPostExecute(String response) {
        try {

            if (responseCode != 201 || response == null) {
                System.out.println("Response Code: " + responseCode + "\nResponse Message :" + responseMessage);
                dialog.dismiss();
                return;
            }
            JSONObject jsonObject = new JSONObject(response);
            int flag = -3;

            flag = jsonObject.getInt("flag");
            message = jsonObject.getString("message");
            if (flag == 1) {
                System.out.println("Message " + message);
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }
}
